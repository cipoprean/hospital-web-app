package com.ciprian.hospital_appointments.service;

import com.ciprian.hospital_appointments.config.exceptions.BadRequestException;
import com.ciprian.hospital_appointments.domain.Appointment;
import com.ciprian.hospital_appointments.domain.Doctor;
import com.ciprian.hospital_appointments.domain.Patient;
import com.ciprian.hospital_appointments.domain.Role;
import com.ciprian.hospital_appointments.domain.User;
import com.ciprian.hospital_appointments.domain.enums.AppointmentStatus;
import com.ciprian.hospital_appointments.dto.AppointmentDto;
import com.ciprian.hospital_appointments.dto.ResponseDto;
import com.ciprian.hospital_appointments.repository.AppointmentRepository;
import com.ciprian.hospital_appointments.repository.DoctorRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AppointmentService appointmentService;

    private User patientUser;
    private User doctorUser;
    private Patient patient;
    private Doctor doctor;
    private Appointment appointment;
    private AppointmentDto appointmentDto;
    private UUID appointmentId;
    private UUID doctorId;
    private UUID patientId;

    @BeforeEach
    void setUp() {
        appointmentId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
        patientId = UUID.randomUUID();

        var patientRole = Role.builder().roleId(UUID.randomUUID()).roleName("PATIENT").build();
        var doctorRole = Role.builder().roleId(UUID.randomUUID()).roleName("DOCTOR").build();
        var adminRole = Role.builder().roleId(UUID.randomUUID()).roleName("ADMIN").build();

        patientUser = User.builder()
                .userId(UUID.randomUUID())
                .name("Ion Popescu")
                .email("ion@example.com")
                .roles(List.of(patientRole))
                .build();

        doctorUser = User.builder()
                .userId(UUID.randomUUID())
                .name("Andrei Pop")
                .email("andrei@example.com")
                .roles(List.of(doctorRole))
                .build();

        patient = Patient.builder()
                .patientId(patientId)
                .firstName("Ion")
                .lastName("Popescu")
                .user(patientUser)
                .build();

        doctor = Doctor.builder()
                .doctorId(doctorId)
                .firstName("Andrei")
                .lastName("Pop")
                .user(doctorUser)
                .build();

        var endDate = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        var startDate = endDate.plusHours(2);

        appointment = Appointment.builder()
                .appointmentId(appointmentId)
                .startDate(startDate)
                .endDate(endDate)
                .meetingLink("https://jit.si/mdh-test123")
                .purposeOfConsultation("Consultatie cardiologie")
                .initialSymptoms("Durere in piept")
                .appointmentStatus(AppointmentStatus.SCHEDUELED)
                .doctor(doctor)
                .patient(patient)
                .build();

        appointmentDto = AppointmentDto.builder()
                .doctorId(doctorId)
                .startDate(startDate)
                .endDate(endDate)
                .purposeOfConsultation("Consultatie cardiologie")
                .initialSymptoms("Durere in piept")
                .build();
    }

    @Test
    void bookAppointment_shouldBookSuccessfully() {
        when(userService.getLoggedUser()).thenReturn(patientUser);
        when(patientRepository.findByUser(patientUser)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findConflictingAppointments(any(), any(), any())).thenReturn(List.of());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(modelMapper.map(appointment, AppointmentDto.class)).thenReturn(appointmentDto);

        var response = appointmentService.bookAppointment(appointmentDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(response.getMessage()).isEqualTo("Programarea a fost efectuata cu succes");
        assertThat(response.getData()).isEqualTo(appointmentDto);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void bookAppointment_shouldThrowBadRequest_whenPatientNotFound() {
        when(userService.getLoggedUser()).thenReturn(patientUser);
        when(patientRepository.findByUser(patientUser)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.bookAppointment(appointmentDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Pacientul nu exista!");
    }

    @Test
    void bookAppointment_shouldThrowBadRequest_whenDoctorNotFound() {
        when(userService.getLoggedUser()).thenReturn(patientUser);
        when(patientRepository.findByUser(patientUser)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.bookAppointment(appointmentDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Doctorul nu exista!");
    }

    @Test
    void bookAppointment_shouldThrowBadRequest_whenConflictExists() {
        when(userService.getLoggedUser()).thenReturn(patientUser);
        when(patientRepository.findByUser(patientUser)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findConflictingAppointments(any(), any(), any())).thenReturn(List.of(appointment));

        assertThatThrownBy(() -> appointmentService.bookAppointment(appointmentDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Doctorul nu este dinsponibil pentru consultatie conform cu data inceperii! Va rugam verificati programul!");
    }

    @Test
    void getMyAppointments_shouldReturnPatientAppointments() {
        var role = Role.builder().roleId(UUID.randomUUID()).roleName("PATIENT").build();
        patientUser.setRoles(List.of(role));

        when(userService.getLoggedUser()).thenReturn(patientUser);
        when(patientRepository.findByUser(patientUser)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByPatient_User_UserIdOrderByAppointmentIdDesc(patientUser.getUserId()))
                .thenReturn(List.of(appointment));
        when(modelMapper.map(appointment, AppointmentDto.class)).thenReturn(appointmentDto);

        var response = appointmentService.getMyAppointments();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo("Consultatiile au fost aduse cu succes");
        assertThat(response.getData()).hasSize(1);
        assertThat(response.getData().get(0)).isEqualTo(appointmentDto);
    }

    @Test
    void getMyAppointments_shouldReturnDoctorAppointments() {
        var role = Role.builder().roleId(UUID.randomUUID()).roleName("DOCTOR").build();
        doctorUser.setRoles(List.of(role));

        when(userService.getLoggedUser()).thenReturn(doctorUser);
        when(doctorRepository.findByUser(doctorUser)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctor_User_UserIdOrderByAppointmentIdDesc(doctorUser.getUserId()))
                .thenReturn(List.of(appointment));
        when(modelMapper.map(appointment, AppointmentDto.class)).thenReturn(appointmentDto);

        var response = appointmentService.getMyAppointments();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo("Consultatiile au fost aduse cu succes");
        assertThat(response.getData()).hasSize(1);
        assertThat(response.getData().get(0)).isEqualTo(appointmentDto);
    }

    @Test
    void getMyAppointments_shouldThrowBadRequest_whenPatientNotFound() {
        var role = Role.builder().roleId(UUID.randomUUID()).roleName("PATIENT").build();
        patientUser.setRoles(List.of(role));

        when(userService.getLoggedUser()).thenReturn(patientUser);
        when(patientRepository.findByUser(patientUser)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.getMyAppointments())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Pacientul nu exista!");
    }

    @Test
    void getMyAppointments_shouldThrowBadRequest_whenDoctorNotFound() {
        var role = Role.builder().roleId(UUID.randomUUID()).roleName("DOCTOR").build();
        doctorUser.setRoles(List.of(role));

        when(userService.getLoggedUser()).thenReturn(doctorUser);
        when(doctorRepository.findByUser(doctorUser)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.getMyAppointments())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Doctorul nu exista!");
    }

    @Test
    void cancelAppointment_shouldCancelSuccessfully() {
        when(userService.getLoggedUser()).thenReturn(patientUser);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        var response = appointmentService.cancelAppointment(appointmentId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo("Appointment cancelled successfully");
        assertThat(appointment.getAppointmentStatus()).isEqualTo(AppointmentStatus.CANCELED);
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void cancelAppointment_shouldThrowBadRequest_whenAppointmentNotFound() {
        when(userService.getLoggedUser()).thenReturn(patientUser);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.cancelAppointment(appointmentId))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Consultatia nu exista!");
    }

    @Test
    void cancelAppointment_shouldThrowBadRequest_whenUserNotAssociated() {
        var otherUser = User.builder()
                .userId(UUID.randomUUID())
                .name("Alt User")
                .email("alt@example.com")
                .build();

        when(userService.getLoggedUser()).thenReturn(otherUser);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.cancelAppointment(appointmentId))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Consultatia nu poate fi anulata decat de doctorul sau pacientul implciat");
    }

    @Test
    void completeAppointment_shouldCompleteSuccessfully() {
        when(userService.getLoggedUser()).thenReturn(doctorUser);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        var response = appointmentService.completeAppointment(appointmentId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo("Consultatia a fost terminata cu succes! Se pot crea documentele rezultate in urma acesteia!");
        assertThat(appointment.getAppointmentStatus()).isEqualTo(AppointmentStatus.COMPLETED);
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void completeAppointment_shouldThrowBadRequest_whenAppointmentNotFound() {
        when(userService.getLoggedUser()).thenReturn(doctorUser);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.completeAppointment(appointmentId))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Consultatia nu exista!");
    }

    @Test
    void completeAppointment_shouldThrowBadRequest_whenNotAssignedDoctor() {
        var otherDoctorUser = User.builder()
                .userId(UUID.randomUUID())
                .name("Alt Doctor")
                .email("altdoctor@example.com")
                .build();

        when(userService.getLoggedUser()).thenReturn(otherDoctorUser);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.completeAppointment(appointmentId))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Doar doctorul caruia i-a fost asignata consultatie o poate anula");
    }
}
