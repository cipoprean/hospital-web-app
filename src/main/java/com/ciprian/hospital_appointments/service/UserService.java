package com.ciprian.hospital_appointments.service;

import com.ciprian.hospital_appointments.config.exceptions.BadRequestException;
import com.ciprian.hospital_appointments.config.exceptions.NotFoundException;
import com.ciprian.hospital_appointments.domain.User;
import com.ciprian.hospital_appointments.dto.NotificationDto;
import com.ciprian.hospital_appointments.dto.ResponseDto;
import com.ciprian.hospital_appointments.dto.UpdatePasswordDto;
import com.ciprian.hospital_appointments.dto.UserDto;
import com.ciprian.hospital_appointments.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final String uploadPath = "upload/profile-pictures";

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public User getLoggedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(authentication)) {
            throw new BadRequestException("Utilizatorul nu este autentificat");
        }

        var email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Utilizatorul nu a fost gasit"));
    }

    public ResponseDto<UserDto> getUserById(UUID userId) {

        var userDto = userRepository.findById(userId)
                .map(u -> modelMapper.map(u, UserDto.class))
                .orElseThrow(() -> new BadRequestException("Utilizatorul nu exista"));

        return ResponseDto.<UserDto>
                        builder()
                .statusCode(HttpStatus.OK.value())
                .message("Datele utilizatorului au fost aduse cu succes")
                .data(userDto)
                .build();
    }

    public ResponseDto<UserDto> getLoggedUserProfile() {

        var user = getLoggedUser();

        var userDto = modelMapper.map(user, UserDto.class);

        return ResponseDto.<UserDto>
                        builder()
                .statusCode(HttpStatus.OK.value())
                .message("Datele utilizatorului au fost aduse cu succes")
                .data(userDto)
                .build();
    }

    public ResponseDto<List<UserDto>> getAllUsers() {

        var users = userRepository.findAll()
                .stream()
                .map(usr -> modelMapper.map(usr, UserDto.class))
                .toList();

        return ResponseDto.<List<UserDto>>
                        builder()
                .statusCode(HttpStatus.OK.value())
                .message("Utilizatorii au fost adusi cu succes")
                .data(users)
                .build();
    }

    public ResponseDto<?> updatePassword(@Valid @RequestBody UpdatePasswordDto dto) {

        var user = getLoggedUser();

        if (Objects.isNull(dto.getNewPassword()) || Objects.isNull(dto.getOldPassword())) {
            throw new BadRequestException("Noua sau vechea parola nu exista");
        }

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Parolele nu coincid.");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        var notification = NotificationDto
                .builder()
                .recipient(user.getEmail())
                .subject("Parola dumneavoastra a fost actualizata cu succes")
                .templateName("password-change")
                .templateVariables(Map.of(
                        "name", user.getName()
                ))
                .build();

        notificationService.sendEmail(notification, user);

        return ResponseDto
                .builder()
                .statusCode(HttpStatus.OK.value())
                .message("Parola a fost actualizata cu succes")
                .build();
    }

    public ResponseDto<?> updateProfilePicture(MultipartFile profilePicture) {

        var user = getLoggedUser();

        try {
            Path path = Paths.get(uploadPath);

            if (Files.notExists(path)) {
                Files.createDirectories(path);
            }

            if (Objects.nonNull(user.getProfilePictureUrl()) && !user.getProfilePictureUrl().isEmpty()) {
                Path oldPath = Paths.get(user.getProfilePictureUrl());
                if (Files.exists(oldPath)) {
                    Files.delete(oldPath);
                }
            }

            String originalFilename = profilePicture.getOriginalFilename();
            String fileExtension = "";

            if (Objects.nonNull(originalFilename) && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String fileName = UUID.randomUUID() + "." + fileExtension;
            Path filePath = path.resolve(fileName);

            Files.copy(profilePicture.getInputStream(), filePath);

            user.setProfilePictureUrl(uploadPath + fileName);
            userRepository.save(user);

            return ResponseDto
                    .builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Poza de profil a fost incarcata cu succes")
                    .build();

        } catch (IOException ioe) {
            throw new RuntimeException(ioe.getMessage());
        }
    }

    //todo - de implementat
    public ResponseDto<?> updateProfilePictureMinio(MultipartFile profilePicture) {
        return null;
    }
}
