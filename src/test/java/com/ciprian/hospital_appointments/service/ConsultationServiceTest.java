package com.ciprian.hospital_appointments.service;

import com.ciprian.hospital_appointments.config.exceptions.BadRequestException;
import com.ciprian.hospital_appointments.domain.Appointment;
import com.ciprian.hospital_appointments.domain.Consultation;
import com.ciprian.hospital_appointments.domain.Doctor;
import com.ciprian.hospital_appointments.domain.Patient;
import com.ciprian.hospital_appointments.domain.User;
import com.ciprian.hospital_appointments.domain.enums.AppointmentStatus;
import com.ciprian.hospital_appointments.dto.ConsultationDto;
import com.ciprian.hospital_appointments.repository.AppointmentRepository;
import com.ciprian.hospital_appointments.repository.ConsultationRepository;
import com.ciprian.hospital_appointments.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultationServiceTest {

    @Mock
    private ConsultationRepository consultationRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ConsultationService consultationService;

    private User user;
    private User doctorUser;
    private Patient patient;
    private Doctor doctor;
    private Appointment appointment;
    private Consultation consultation;
    private ConsultationDto consultationDto;
    private UUID patientId;
    private UUID userId;
    private UUID appointmentId;

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();
        userId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();
        var doctorUserId = UUID.randomUUID();

        user = User.builder()
                .userId(userId)
                .name("Ion Popescu")
                .email("ion@example.com")
                .build();

        doctorUser = User.builder()
                .userId(doctorUserId)
                .name("Dr. Andrei")
                .email("dr.andrei@example.com")
                .build();

        doctor = Doctor.builder()
                .doctorId(UUID.randomUUID())
                .user(doctorUser)
                .build();

        patient = Patient.builder()
                .patientId(patientId)
                .firstName("Ion")
                .lastName("Popescu")
                .user(user)
                .build();

        appointment = Appointment.builder()
                .appointmentId(appointmentId)
                .appointmentStatus(AppointmentStatus.SCHEDUELED)
                .doctor(doctor)
                .patient(patient)
                .startDate(LocalDateTime.now())
                .build();

        consultation = Consultation.builder()
                .consultationId(UUID.randomUUID())
                .consultationDate(LocalDateTime.now())
                .subjectiveNotes("Subiectiv")
                .objectiveFindings("Obiectiv")
                .assesments("Evaluari")
                .plan("Plan")
                .appointment(appointment)
                .build();

        consultationDto = ConsultationDto.builder()
                .consultationId(consultation.getConsultationId())
                .appointmentId(appointmentId)
                .consultationDate(consultation.getConsultationDate())
                .subjectiveNotes("Subiectiv")
                .objectiveFindings("Obiectiv")
                .assesments("Evaluari")
                .plan("Plan")
                .build();
    }

    @Test
    void createConsultation_shouldCreateSuccessfully() {
        when(userService.getLoggedUser()).thenReturn(doctorUser);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(consultationRepository.findByAppointmentAppointmentId(appointmentId)).thenReturn(Optional.empty());
        when(consultationRepository.save(any(Consultation.class))).thenReturn(consultation);
        when(modelMapper.map(any(Consultation.class), eq(ConsultationDto.class))).thenReturn(consultationDto);

        var response = consultationService.createConsultation(consultationDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo("Consultatia a fost creata cu succes");
        assertThat(response.getData()).isEqualTo(consultationDto);
        verify(appointmentRepository).save(appointment);
        assertThat(appointment.getAppointmentStatus()).isEqualTo(AppointmentStatus.COMPLETED);
    }

    @Test
    void createConsultation_shouldThrowBadRequest_whenAppointmentNotFound() {
        when(userService.getLoggedUser()).thenReturn(doctorUser);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultationService.createConsultation(consultationDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Programarea la consultatie nu exista");
    }

    @Test
    void createConsultation_shouldThrowBadRequest_whenDoctorNotAuthorized() {
        when(userService.getLoggedUser()).thenReturn(user);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> consultationService.createConsultation(consultationDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Nu sunteti autorizat sa creati notele de constatare pentru acest consult");
    }

    @Test
    void createConsultation_shouldThrowBadRequest_whenConsultationAlreadyExists() {
        when(userService.getLoggedUser()).thenReturn(doctorUser);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(consultationRepository.findByAppointmentAppointmentId(appointmentId)).thenReturn(Optional.of(consultation));

        assertThatThrownBy(() -> consultationService.createConsultation(consultationDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Notele de constatare ale acestei consultatii exista deja");
    }

    @Test
    void getConsultationByAppointmentId_shouldReturnConsultation() {
        when(consultationRepository.findByAppointmentAppointmentId(appointmentId)).thenReturn(Optional.of(consultation));
        when(modelMapper.map(consultation, ConsultationDto.class)).thenReturn(consultationDto);

        var response = consultationService.getConsultationByAppointmentId(appointmentId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo("Consultatia a fost adusa cu succes");
        assertThat(response.getData()).isEqualTo(consultationDto);
    }

    @Test
    void getConsultationByAppointmentId_shouldThrowBadRequest_whenNotFound() {
        when(consultationRepository.findByAppointmentAppointmentId(appointmentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultationService.getConsultationByAppointmentId(appointmentId))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Consultatie nu exista");
    }

    @Test
    void getConsultationsForPatient_shouldReturnConsultations_whenPatientIdIsProvided() {
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(consultationRepository.findByAppointmentPatientPatientIdOrderByConsultationDateDesc(patientId))
                .thenReturn(List.of(consultation));
        when(modelMapper.map(consultation, ConsultationDto.class)).thenReturn(consultationDto);

        var response = consultationService.getConsultationsForPatient(patientId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo("Consultatiile au fost aduse cu succes");
        assertThat(response.getData()).hasSize(1);
        assertThat(response.getData().get(0)).isEqualTo(consultationDto);
    }

    @Test
    void getConsultationsForPatient_shouldReturnConsultationsFromLoggedUser_whenPatientIdIsNull() {
        when(userService.getLoggedUser()).thenReturn(user);
        when(patientRepository.findByUser(user)).thenReturn(Optional.of(patient));
        when(consultationRepository.findByAppointmentPatientPatientIdOrderByConsultationDateDesc(patientId))
                .thenReturn(List.of(consultation));
        when(modelMapper.map(consultation, ConsultationDto.class)).thenReturn(consultationDto);

        var response = consultationService.getConsultationsForPatient(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo("Consultatiile au fost aduse cu succes");
        assertThat(response.getData()).hasSize(1);
    }

    @Test
    void getConsultationsForPatient_shouldReturnEmptyList_whenNoConsultationsExist() {
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(consultationRepository.findByAppointmentPatientPatientIdOrderByConsultationDateDesc(patientId))
                .thenReturn(List.of());

        var response = consultationService.getConsultationsForPatient(patientId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo("Nu exista consultatii pentru acest pacient");
        assertThat(response.getData()).isEmpty();
    }

    @Test
    void getConsultationsForPatient_shouldThrowBadRequest_whenPatientNotFoundById() {
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultationService.getConsultationsForPatient(patientId))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Profilul de pacient nu exista pentru acest utilizator");
    }

    @Test
    void getConsultationsForPatient_shouldThrowBadRequest_whenLoggedUserHasNoPatientProfile() {
        when(userService.getLoggedUser()).thenReturn(user);
        when(patientRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultationService.getConsultationsForPatient(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Profilul de pacient nu exista pentru acest utilizator");
    }
}
