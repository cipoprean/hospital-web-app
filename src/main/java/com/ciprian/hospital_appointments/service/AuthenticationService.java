package com.ciprian.hospital_appointments.service;

import com.ciprian.hospital_appointments.config.exceptions.BadRequestException;
import com.ciprian.hospital_appointments.domain.*;
import com.ciprian.hospital_appointments.domain.enums.RoleEnum;
import com.ciprian.hospital_appointments.domain.enums.Specialization;
import com.ciprian.hospital_appointments.dto.*;
import com.ciprian.hospital_appointments.repository.*;
import com.ciprian.hospital_appointments.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final NotificationService notificationService;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final CodeGenerator codeGenerator;


    @Value("${login.link}")
    private String logicLink;
    @Value("${reset.link}")
    private String resetLink;

    public ResponseDto<String> register(UserRegistrationDto userRegistrationDto) {

        userRepository.findByEmail(userRegistrationDto.getEmail())
                .ifPresent(user -> {
                    throw new BadRequestException("Utilizatorul exista deja");
                });

        var roles = Objects.nonNull(userRegistrationDto.getRoles()) || !userRegistrationDto.getRoles().isEmpty()
                ? userRegistrationDto.getRoles().stream().map(String::toUpperCase).toList() :
                List.of(RoleEnum.PATIENT.name());

        boolean isDoctor = roles.contains(RoleEnum.DOCTOR.name());

        boolean hasNoLicense = Objects.isNull(userRegistrationDto.getLicenseNumber()) || userRegistrationDto.getLicenseNumber().isBlank();
        if (isDoctor && hasNoLicense) {
            throw new BadRequestException("Numarul de licenta este obligatoriu pentru a te putea inregistra ca doctor");
        }

        boolean isValidSpecialization = Arrays.stream(Specialization.values()).anyMatch(role -> role.name().equals(userRegistrationDto.getSpecialization()));
        boolean hasNoSpecialization = (Objects.isNull(userRegistrationDto.getSpecialization()) ||
                isValidSpecialization);

        if (isDoctor && hasNoSpecialization) {
            throw new BadRequestException("Specializarea este obligatorie pentru a te putea inregistra ca doctor");
        }

        var validateRoles = validateAndLoadRoles(userRegistrationDto.getRoles());

        var userBuilder = User
                .builder()
                .email(userRegistrationDto.getEmail())
                .roles(validateRoles)
                .name(userRegistrationDto.getName())
                .password(passwordEncoder.encode(userRegistrationDto.getPassword()))
                .build();

        var user = userRepository.save(userBuilder);

        log.info("Utilizatorul {} a fost inregistrat cu succes. Numar roluri in aplicatie: {}", user, validateRoles.size());

        //creare profil utilizator
        createUserProfileByRoleName(userRegistrationDto, user, validateRoles);
        sendRegistrationEmail(userRegistrationDto, user);

        return ResponseDto.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Utilizatorul a fost inregistrat cu succes.")
                .data(user.getEmail())
                .build();
    }

    public ResponseDto<UserLoginResponseDto> login(UserLoginDto dto) {

        var user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BadRequestException("Email-ul sau parola sunt gresite. Va rugam reincercati."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadRequestException("Email-ul sau parola sunt gresite. Va rugam reincercati.");
        }

        String token = jwtService.generateToken(user.getEmail());

        UserLoginResponseDto responseDto = UserLoginResponseDto
                .builder()
                .roles(user.getRoles().stream().map(Role::getRoleName).toList())
                .token(token)
                .build();

        return ResponseDto.<UserLoginResponseDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Utilizatorul a fost autentificat cu succes")
                .data(responseDto)
                .build();
    }

    @Transactional
    public ResponseDto<?> forgetPassword(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Utilizatorul nu exista"));

        //stergem orice code este asociat utilizatrului
        passwordResetRepository.deleteByUser_UserId(user.getUserId());

        String code = codeGenerator.generateCode();

        var resetCode = PasswordReset
                .builder()
                .user(user)
                .code(code)
                .used(false)
                .expiryDate(LocalDateTime.now().plusHours(5))
                .build();

        passwordResetRepository.save(resetCode);

        sendResetPasswordEmail(user, resetCode.getCode());

        return ResponseDto
                .builder()
                .statusCode(HttpStatus.OK.value())
                .message("Codul de resetare al parolei a fost trimis catre adresa dumneavoastra de email")
                .build();
    }

    public ResponseDto<?> updatePasswordByResetCode(ResetPasswordDto resetPasswordDto) {
        String code = resetPasswordDto.getCode();
        String newPassword = resetPasswordDto.getNewPassword();

        log.info("The code is {}", code);
        log.info("The new password is {}", newPassword);

        var resetCode = passwordResetRepository.findByCode(code)
                .orElseThrow(() -> new BadRequestException("Codule pentru resetarea parolei este invalid."));

        checkIfCodeIsExpired(resetCode);

        var user = resetCode.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetRepository.delete(resetCode);

        sendPasswordUpdateEmail(user);
        return ResponseDto
                .builder()
                .statusCode(HttpStatus.OK.value())
                .message("Parola a fost actualizată cu succes.")
                .build();

    }

    private void checkIfCodeIsExpired(PasswordReset resetCode) {
        if (resetCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetRepository.delete(resetCode);
        }
    }

    private List<Role> validateAndLoadRoles(List<String> roles) {
        return roles.stream()
                .map(roleRepository::findByRoleName)
                .flatMap(Optional::stream)
                .toList();
    }

    private void createUserProfileByRoleName(UserRegistrationDto dto, User user, List<Role> roles) {

        roles.forEach(role -> {
            RoleEnum roleEnum = RoleEnum.valueOf(role.getRoleName());
            switch (roleEnum) {
                case DOCTOR:
                    createDoctorProfile(dto, user);
                    break;
                case PATIENT:
                    createPatientProfile(user);
                    break;
                case ADMIN:
                    log.info("Rolul de administrator a fost asignat utilizatorului: {}", user.getEmail());
                    break;
                default:
                    log.info("Rolul {} nu corespunde nici unui profil existent in cadrul aplicatiei", role.getRoleName());
                    break;
            }
        });
    }

    private void createPatientProfile(User user) {
        patientRepository.save(
                Patient
                        .builder()
                        .user(user)
                        .build()
        );

        log.info("Profil creat cu rol PACIENT pentru utilizatorul {}", user.getEmail());
    }

    private void createDoctorProfile(UserRegistrationDto dto, User user) {
        doctorRepository.save(
                Doctor
                        .builder()
                        .specialization(dto.getSpecialization())
                        .licenseNumber(dto.getLicenseNumber())
                        .user(user)
                        .build()
        );

        log.info("Profil creat cu rol DOCTOR pentru utilizatorul {}", user.getEmail());
    }

    private void sendRegistrationEmail(UserRegistrationDto dto, User user) {

        var welcomeEmail = NotificationDto
                .builder()
                .recipient(user.getEmail())
                .subject("Bun venit la MyDigitalHospital§")
                .templateName("welcome")
                .templateVariables(
                        Map.of(
                                "name", dto.getName(),
                                "loginlink", logicLink
                        )
                )
                .build();

        notificationService.sendEmail(welcomeEmail, user);
    }

    private void sendResetPasswordEmail(User user, String code) {

        var resetPasswordEmail = NotificationDto
                .builder()
                .recipient(user.getEmail())
                .subject("Cerere resetare parola")
                .templateName("password-reset")
                .templateVariables(Map.of(
                        "name", user.getName(),
                        "resetLink", resetLink + code
                ))
                .build();

        notificationService.sendEmail(resetPasswordEmail, user);
    }

    private void sendPasswordUpdateEmail(User user) {

        var passwordResetEmail = NotificationDto
                .builder()
                .recipient(user.getEmail())
                .subject("Parola actualizată")
                .templateName("password-update-confirmation")
                .templateVariables(Map.of(
                        "name", user.getName()
                ))
                .build();

        notificationService.sendEmail(passwordResetEmail, user);
    }
}
