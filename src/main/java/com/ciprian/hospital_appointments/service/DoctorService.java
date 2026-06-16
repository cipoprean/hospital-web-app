package com.ciprian.hospital_appointments.service;

import com.ciprian.hospital_appointments.config.exceptions.BadRequestException;
import com.ciprian.hospital_appointments.domain.enums.Specialization;
import com.ciprian.hospital_appointments.dto.DoctorDto;
import com.ciprian.hospital_appointments.dto.ResponseDto;
import com.ciprian.hospital_appointments.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public ResponseDto<DoctorDto> getDoctorProfile() {
        var user = userService.getLoggedUser();

        var doctor = doctorRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Profilul doctorului nu a fost gasit"));


        return ResponseDto.<DoctorDto>
                        builder()
                .statusCode(HttpStatus.OK.value())
                .message("Profilul doctorului a fost adus cu succes")
                .data(modelMapper.map(doctor, DoctorDto.class))
                .build();
    }

    public ResponseDto<?> updateDoctorProfile(DoctorDto doctorDto) {

        var user = userService.getLoggedUser();

        var doctor = doctorRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Profilul doctorului nu a fost gasit"));

        if (StringUtils.hasText(doctorDto.getFirstName())) {
            doctor.setFirstName(doctorDto.getFirstName());
        }

        if (StringUtils.hasText(doctorDto.getLastName())) {
            doctor.setLastName(doctorDto.getLastName());
        }

        Optional.ofNullable(doctorDto.getSpecialization()).ifPresent(doctor::setSpecialization);

        doctorRepository.save(doctor);

        log.info("Doctor profile updated");

        return ResponseDto
                .builder()
                .statusCode(HttpStatus.OK.value())
                .message("Profiul doctorului a fost modificat cu succes")
                .build();
    }

    public ResponseDto<List<DoctorDto>> getAllDoctor() {

        var doctors = doctorRepository.findAll();
        return ResponseDto.<List<DoctorDto>>
                        builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lista doctorilor a fost adusa cu succes.")
                .data(doctors.stream().map(d -> modelMapper.map(d, DoctorDto.class)).toList())
                .build();
    }

    public ResponseDto<DoctorDto> getDoctorById(UUID doctorId) {

        var doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new BadRequestException("Doctorului nu a fost gasit"));

        return ResponseDto.<DoctorDto>
                        builder()
                .statusCode(HttpStatus.OK.value())
                .message("Doctorul a fost adus cu succes")
                .data(modelMapper.map(doctor, DoctorDto.class))
                .build();
    }

    public ResponseDto<List<DoctorDto>> getDoctorsBySpecialization(Specialization specialization) {

        var doctors = doctorRepository.findBySpecialization(specialization);

        return ResponseDto.<List<DoctorDto>>
                        builder()
                .message("Lista doctorilor a fost adus cu succes")
                .data(doctors.stream().map(d -> modelMapper.map(d, DoctorDto.class)).toList())
                .build();
    }

    public ResponseDto<List<Specialization>> getAllSpecialization() {
        return ResponseDto.<List<Specialization>>
                        builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lista specializarilor a fost adus cu succes")
                .data(Arrays.asList(Specialization.values()))
                .build();
    }
}
