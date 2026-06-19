package com.ciprian.hospital_appointments.service;

import com.ciprian.hospital_appointments.config.exceptions.BadRequestException;
import com.ciprian.hospital_appointments.domain.enums.BloodGroup;
import com.ciprian.hospital_appointments.domain.enums.GenoType;
import com.ciprian.hospital_appointments.dto.PatientDto;
import com.ciprian.hospital_appointments.dto.ResponseDto;
import com.ciprian.hospital_appointments.repository.PatientRepository;
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
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public ResponseDto<PatientDto> getPatientProfile() {
        var user = userService.getLoggedUser();

        var patient = patientRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Utilizatorul nu exista su nu a fost gasit"));

        return ResponseDto.<PatientDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Profilul pacientui a fost gasit cu succes")
                .data(modelMapper.map(patient, PatientDto.class))
                .build();
    }

    public ResponseDto<?> updatePatientProfile(PatientDto patientDto) {

        var user = userService.getLoggedUser();

        var patient = patientRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Utilizatorul nu exista su nu a fost gasit"));

        if (StringUtils.hasText(patientDto.getFirstName())) {
            patient.setFirstName(patientDto.getFirstName());
        }

        if (StringUtils.hasText(patientDto.getLastName())) {
            patient.setLastName(patientDto.getLastName());
        }

        if (StringUtils.hasText(patientDto.getPhoneNumber())) {
            patient.setPhoneNumber(patientDto.getPhoneNumber());
        }

        if (StringUtils.hasText(patientDto.getKnownAllergies())) {
            patient.setKnownAllergies(patientDto.getKnownAllergies());
        }

        Optional.ofNullable(patientDto.getBirthDate()).ifPresent(patient::setBirthday);
        Optional.ofNullable(patientDto.getBloodGroup()).ifPresent(patient::setBloodGroup);
        Optional.ofNullable(patientDto.getGenoType()).ifPresent(patient::setGenotype);

        patientRepository.save(patient);

        log.info("Patient profile updated");

        return ResponseDto.
                builder()
                .statusCode(HttpStatus.OK.value())
                .message("Profilul pacientului a fost actualizat cu succes")
                .build();
    }

    public ResponseDto<PatientDto> getPatientById(UUID patientId) {
        var patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new BadRequestException("Utilizatorul nu exista su nu a fost gasit"));

        return ResponseDto.<PatientDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Pacientul a fost gasit cu succes")
                .data(modelMapper.map(patient, PatientDto.class))
                .build();
    }

    public ResponseDto<List<BloodGroup>> getBloodGroups() {
        return ResponseDto.<List<BloodGroup>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lista grupelor de sange a fost adusa cu succes")
                .data(Arrays.asList(BloodGroup.values()))
                .build();
    }

    public ResponseDto<List<GenoType>> getGenoTypes() {
        return ResponseDto.<List<GenoType>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lista categoriilor de grupe de sange a fost adusa cu succes")
                .data(Arrays.asList(GenoType.values()))
                .build();
    }
}
