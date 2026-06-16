package com.ciprian.hospital_appointments.service;

import com.ciprian.hospital_appointments.config.exceptions.BadRequestException;
import com.ciprian.hospital_appointments.domain.Appointment;
import com.ciprian.hospital_appointments.domain.User;
import com.ciprian.hospital_appointments.domain.enums.AppointmentStatus;
import com.ciprian.hospital_appointments.dto.AppointmentDto;
import com.ciprian.hospital_appointments.dto.NotificationDto;
import com.ciprian.hospital_appointments.dto.ResponseDto;
import com.ciprian.hospital_appointments.repository.AppointmentRepository;
import com.ciprian.hospital_appointments.repository.DoctorRepository;
import com.ciprian.hospital_appointments.repository.PatientRepository;
import com.ciprian.hospital_appointments.util.JitsiMeetingLinkGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy 'at' hh:mm a");

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    private final ModelMapper modelMapper;

    private final NotificationService notificationService;
    private final UserService userService;

    public ResponseDto<AppointmentDto> bookAppointment(AppointmentDto appointmentDto) {

        var user = userService.getLoggedUser();

        var patient = patientRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Pacientul nu exista!"));

        var doctor = doctorRepository.findById(appointmentDto.getDoctorId())
                .orElseThrow(() -> new BadRequestException("Doctorul nu exista!"));

        if (appointmentDto.getStartDate().isBefore(appointmentDto.getEndDate().plusHours(1))) {
            throw new BadRequestException("Programarea trebuie realizata cu cel putin o ora in avans");
        }

        //vom verifica pentru conflicte de programari cu o ora inainte de inceputul consultatiei
        LocalDateTime checkStart = appointmentDto.getStartDate().minusMinutes(60);

        var conflictHours = appointmentRepository.findConflictingAppointments(
                doctor.getDoctorId(),
                checkStart,
                appointmentDto.getEndDate()
        );

        if (!conflictHours.isEmpty()) {
            throw new BadRequestException("Doctorul nu este dinsponibil pentru consultatie conform cu data inceperii! Va rugam verificati programul!");
        }

        var meetingLink = JitsiMeetingLinkGenerator.generateJitsiMeetingLink();

        var appointment = Appointment
                .builder()
                .doctor(doctor)
                .patient(patient)
                .startDate(appointmentDto.getStartDate())
                .endDate(appointmentDto.getEndDate())
                .meetingLink(meetingLink)
                .initialSymptoms(appointmentDto.getInitialSymptoms())
                .purposeOfConsultation(appointmentDto.getPurposeOfConsultation())
                .appointmentStatus(AppointmentStatus.SCHEDUELED)
                .build();

        var appointmentSaved = appointmentRepository.save(appointment);

        sendAppointmentConfirmation(appointmentSaved);

        return ResponseDto.<AppointmentDto>builder()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .message("Programarea a fost efectuata cu succes")
                .data(modelMapper.map(appointmentSaved, AppointmentDto.class))
                .build();
    }

    public ResponseDto<List<AppointmentDto>> getMyAppointments() {

        var user = userService.getLoggedUser();
        var appointments = new ArrayList<>();

        user.getRoles().forEach(role -> {
            switch (role.getRoleName()) {
                case "PATIENT": {
                    patientRepository.findByUser(user)
                            .orElseThrow(() -> new BadRequestException("Pacientul nu exista!"));

                    var patientAppointments = appointmentRepository.findByPatient_User_UserIdOrderByAppointmentIdDesc(user.getUserId());
                    appointments.addAll(patientAppointments);
                    break;
                }
                case "DOCTOR": {
                    doctorRepository.findByUser(user)
                            .orElseThrow(() -> new BadRequestException("Doctorul nu exista!"));
                    var doctorAppointments = appointmentRepository.findByDoctor_User_UserIdOrderByAppointmentIdDesc(user.getUserId());
                    appointments.addAll(doctorAppointments);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Rolul " + role.getRoleName() + " nu exista sau nu are consultatii!");
            }
        });

        return ResponseDto.<List<AppointmentDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultatiile au fost aduse cu succes")
                .data(appointments.stream().map(a -> modelMapper.map(a, AppointmentDto.class)).toList())
                .build();
    }

    public ResponseDto<?> cancelAppointment(UUID appointmentId) {

        var user = userService.getLoggedUser();

        var appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BadRequestException("Consultatia nu exista!"));

        boolean isAppointendUser = appointment.getPatient().getUser().getUserId().equals(user.getUserId()) ||
                appointment.getDoctor().getUser().getUserId().equals(user.getUserId());

        if (!isAppointendUser) {
            throw new BadRequestException("Consultatia nu poate fi anulata decat de doctorul sau pacientul implciat");
        }

        appointment.setAppointmentStatus(AppointmentStatus.CANCELED);
        var savedAppointment = appointmentRepository.save(appointment);

        sendAppointmentCancelation(savedAppointment, user);

        return ResponseDto
                .builder()
                .statusCode(HttpStatus.OK.value())
                .message("Appointment cancelled successfully")
                .build();
    }

    public ResponseDto<?> completeAppointment(UUID appointmentId) {

        var user = userService.getLoggedUser();

        var appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BadRequestException("Consultatia nu exista!"));

        if (!appointment.getDoctor().getUser().getUserId().equals(user.getUserId())) {
            throw new BadRequestException("Doar doctorul caruia i-a fost asignata consultatie o poate anula");
        }

        appointment.setAppointmentStatus(AppointmentStatus.COMPLETED);
        appointment.setEndDate(LocalDateTime.now());
        appointmentRepository.save(appointment);

        return ResponseDto
                .builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultatia a fost terminata cu succes! Se pot crea documentele rezultate in urma acesteia!")
                .build();
    }


    private void sendAppointmentConfirmation(Appointment appointment) {
        var patient = appointment.getPatient().getUser();
        var doctor = appointment.getDoctor().getUser();

        String formattedTime = appointment.getStartDate().format(FORMATTER);

        sendNotificatioNForPatient(appointment, patient, formattedTime);
        sendNotificationForDoctor(appointment, doctor, formattedTime);
    }

    private void sendNotificatioNForPatient(Appointment appointment, User patient, String formattedTime) {
        var patientVars = new HashMap<String, Object>();
        patientVars.put("patientName", patient.getName());
        patientVars.put("doctorName", appointment.getDoctor().getFirstName());
        patientVars.put("appointmentTime", formattedTime);
        patientVars.put("isVirtual", true);
        patientVars.put("meetingLink", appointment.getMeetingLink());
        patientVars.put("purposeOfConsultation", appointment.getPurposeOfConsultation());

        var notification = NotificationDto
                .builder()
                .subject("MyDigitalHospital: Consultatia a fost confirmata")
                .templateName("patient-appointment")
                .templateVariables(patientVars)
                .build();

        notificationService.sendEmail(notification, patient);

        log.info("Confirmation email send for patient: {}", patient.getName());
    }

    private void sendNotificationForDoctor(Appointment appointment, User doctor, String formattedTime) {
        var doctorVars = new HashMap<String, Object>();
        doctorVars.put("doctorName", doctor.getName());
        doctorVars.put("patientFullName", appointment.getPatient().getUser().getName());
        doctorVars.put("appointmentTime", formattedTime);
        doctorVars.put("isVirtual", true);
        doctorVars.put("meetingLink", appointment.getMeetingLink());
        doctorVars.put("purposeOfConsultation", appointment.getPurposeOfConsultation());

        var notification = NotificationDto
                .builder()
                .subject("MyDigitalHospital: Consultatia a fost confirmata")
                .templateName("patient-appointment")
                .templateVariables(doctorVars)
                .build();

        notificationService.sendEmail(notification, doctor);

        log.info("Confirmation email send for doctor: {}", doctor.getName());
    }

    public void sendAppointmentCancelation(Appointment appointment, User user) {
        var patientUser = appointment.getPatient().getUser();
        var doctorUser = appointment.getDoctor().getUser();

        boolean isAppointendUser = patientUser.getUserId().equals(user.getUserId()) ||
                doctorUser.getUserId().equals(user.getUserId());

        if (!isAppointendUser) {
            log.error("Cancelation initiated by user not asscoiated with appointment. User ID: {}", user.getUserId());
            return;
        }

        String formattedTime = appointment.getStartDate().format(FORMATTER);
        String cancellingPartyName = user.getName();

        Map<String, Object> baseVars = new HashMap<>();
        baseVars.put("cancellingPartyName", cancellingPartyName);
        baseVars.put("appointmentTime", formattedTime);
        baseVars.put("doctorName", appointment.getDoctor().getLastName());
        baseVars.put("patientFullName", patientUser.getName());

        // --- 1. Dispatch Email to Doctor ---
        Map<String, Object> doctorVars = new HashMap<>(baseVars);
        doctorVars.put("recipientName", doctorUser.getName());

        var doctorNotification = NotificationDto.builder()
                .recipient(doctorUser.getEmail())
                .subject("My Digital Hospital: Consultatie anulata")
                .templateName("appointment-cancellation")
                .templateVariables(doctorVars)
                .build();

        notificationService.sendEmail(doctorNotification, doctorUser);
        log.info("Dispatched cancellation email to Doctor: {}", doctorUser.getEmail());


        // --- 2. Dispatch Email to Patient ---
        Map<String, Object> patientVars = new HashMap<>(baseVars);
        patientVars.put("recipientName", patientUser.getName());

        var patientNotification = NotificationDto.builder()
                .recipient(patientUser.getEmail())
                .subject("My Digital Hospital: Consultatie anulata (ID: " + appointment.getAppointmentId() + ")")
                .templateName("appointment-cancellation")
                .templateVariables(patientVars)
                .build();

        notificationService.sendEmail(patientNotification, patientUser);
        log.info("Dispatched cancellation email to Patient: {}", patientUser.getEmail());
    }
}
