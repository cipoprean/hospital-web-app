package com.ciprian.hospital_appointments.service;

import com.ciprian.hospital_appointments.config.exceptions.BadRequestException;
import com.ciprian.hospital_appointments.domain.Patient;
import com.ciprian.hospital_appointments.domain.User;
import com.ciprian.hospital_appointments.domain.enums.BloodGroup;
import com.ciprian.hospital_appointments.domain.enums.GenoType;
import com.ciprian.hospital_appointments.dto.PatientDto;
import com.ciprian.hospital_appointments.dto.ResponseDto;
import com.ciprian.hospital_appointments.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PatientService patientService;

    private User user;
    private Patient patient;
    private PatientDto patientDto;
    private UUID patientId;

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();
        user = User.builder()
                .userId(UUID.randomUUID())
                .name("Ion Popescu")
                .email("ion@example.com")
                .build();

        patient = Patient.builder()
                .patientId(patientId)
                .firstName("Ion")
                .lastName("Popescu")
                .birthDate(LocalDate.of(1990, 1, 15))
                .phoneNumber("0712345678")
                .knownAllergies("Polen")
                .bloodGroup(BloodGroup.A_POSITIVE)
                .genoType(GenoType.AA)
                .user(user)
                .build();

        patientDto = PatientDto.builder()
                .patientId(patientId.toString())
                .firstName("Ion")
                .lastName("Popescu")
                .birthDate(LocalDate.of(1990, 1, 15))
                .phoneNumber("0712345678")
                .knownAllergies("Polen")
                .bloodGroup(BloodGroup.A_POSITIVE)
                .genoType(GenoType.AA)
                .build();
    }

    @Test
    void getPatientProfile_shouldReturnPatientDto() {
        when(userService.getLoggedUser()).thenReturn(user);
        when(patientRepository.findByUser(user)).thenReturn(Optional.of(patient));
        when(modelMapper.map(patient, PatientDto.class)).thenReturn(patientDto);

        var response = patientService.getPatientProfile();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo("Profilul pacientui a fost gasit cu succes");
        assertThat(response.getData()).isEqualTo(patientDto);
    }

    @Test
    void getPatientProfile_shouldThrowBadRequest_whenNotFound() {
        when(userService.getLoggedUser()).thenReturn(user);
        when(patientRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.getPatientProfile())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Utilizatorul nu exista su nu a fost gasit");
    }

    @Test
    void updatePatientProfile_shouldUpdateAllFields() {
        var updateDto = PatientDto.builder()
                .firstName("Ionut")
                .lastName("Popescu Jr")
                .phoneNumber("0798765432")
                .knownAllergies("Lapte")
                .birthDate(LocalDate.of(1995, 5, 20))
                .bloodGroup(BloodGroup.B_POSITIVE)
                .genoType(GenoType.AS)
                .build();

        when(userService.getLoggedUser()).thenReturn(user);
        when(patientRepository.findByUser(user)).thenReturn(Optional.of(patient));

        var response = patientService.updatePatientProfile(updateDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo("Profilul pacientului a fost actualizat cu succes");
        verify(patientRepository).save(patient);
        assertThat(patient.getFirstName()).isEqualTo("Ionut");
        assertThat(patient.getLastName()).isEqualTo("Popescu Jr");
        assertThat(patient.getPhoneNumber()).isEqualTo("0798765432");
        assertThat(patient.getKnownAllergies()).isEqualTo("Lapte");
        assertThat(patient.getBirthDate()).isEqualTo(LocalDate.of(1995, 5, 20));
        assertThat(patient.getBloodGroup()).isEqualTo(BloodGroup.B_POSITIVE);
        assertThat(patient.getGenoType()).isEqualTo(GenoType.AS);
    }

    @Test
    void updatePatientProfile_shouldOnlyUpdateProvidedFields() {
        var updateDto = PatientDto.builder()
                .firstName("Ionut")
                .build();

        when(userService.getLoggedUser()).thenReturn(user);
        when(patientRepository.findByUser(user)).thenReturn(Optional.of(patient));

        var response = patientService.updatePatientProfile(updateDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        verify(patientRepository).save(patient);
        assertThat(patient.getFirstName()).isEqualTo("Ionut");
        assertThat(patient.getLastName()).isEqualTo("Popescu");
        assertThat(patient.getPhoneNumber()).isEqualTo("0712345678");
    }

    @Test
    void updatePatientProfile_shouldThrowBadRequest_whenPatientNotFound() {
        when(userService.getLoggedUser()).thenReturn(user);
        when(patientRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.updatePatientProfile(new PatientDto()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Utilizatorul nu exista su nu a fost gasit");
    }

    @Test
    void getPatientById_shouldReturnPatientDto() {
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(modelMapper.map(patient, PatientDto.class)).thenReturn(patientDto);

        var response = patientService.getPatientById(patientId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo("Pacientul a fost gasit cu succes");
        assertThat(response.getData()).isEqualTo(patientDto);
    }

    @Test
    void getPatientById_shouldThrowBadRequest_whenNotFound() {
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.getPatientById(patientId))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Utilizatorul nu exista su nu a fost gasit");
    }

    @Test
    void getBloodGroups_shouldReturnAllValues() {
        var response = patientService.getBloodGroups();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo("Lista grupelor de sange a fost adusa cu succes");
        assertThat(response.getData()).containsExactly(BloodGroup.values());
    }

    @Test
    void getGenoTypes_shouldReturnAllValues() {
        var response = patientService.getGenoTypes();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo("Lista categoriilor de grupe de sange a fost adusa cu succes");
        assertThat(response.getData()).containsExactly(GenoType.values());
    }
}
