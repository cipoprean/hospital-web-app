package com.ciprian.hospital_appointments.service;

import com.ciprian.hospital_appointments.config.exceptions.BadRequestException;
import com.ciprian.hospital_appointments.domain.Appointment;
import com.ciprian.hospital_appointments.domain.Consultation;
import com.ciprian.hospital_appointments.domain.User;
import com.ciprian.hospital_appointments.domain.enums.AppointmentStatus;
import com.ciprian.hospital_appointments.dto.ConsultationDto;
import com.ciprian.hospital_appointments.dto.ResponseDto;
import com.ciprian.hospital_appointments.repository.AppointmentRepository;
import com.ciprian.hospital_appointments.repository.ConsultationRepository;
import com.ciprian.hospital_appointments.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    private final UserService userService;

    private final ModelMapper modelMapper;


    public ResponseDto<ConsultationDto> createConsultation(ConsultationDto consultationDto) {

        var user = userService.getLoggedUser();

        var appointment = appointmentRepository.findById(consultationDto.getAppointmentId())
                .orElseThrow(() -> new BadRequestException("Programarea la consultatie nu exista"));

        checkAppointedDoctorIsValid(appointment, user);

        appointment.setAppointmentStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        var consultation = Consultation
                .builder()
                .consultationDate(LocalDateTime.now())
                .subjectiveNotes(consultationDto.getSubjectiveNotes())
                .objectiveFindings(consultationDto.getObjectiveFindings())
                .assesments(consultationDto.getAssesments())
                .plan(consultationDto.getPlan())
                .appointment(appointment)
                .build();

        consultationRepository.save(consultation);

        return ResponseDto.<ConsultationDto>
                        builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultatia a fost creata cu succes")
                .data(modelMapper.map(consultation, ConsultationDto.class))
                .build();
    }

    private void checkAppointedDoctorIsValid(Appointment appointment, User user) {
        if (!appointment.getDoctor().getUser().getUserId().equals(user.getUserId())) {
            throw new BadRequestException("Nu sunteti autorizat sa creati notele de constatare pentru acest consult");
        }

        if (consultationRepository.findByAppointmentAppointmentId(appointment.getAppointmentId()).isPresent()) {
            throw new BadRequestException("Notele de constatare ale acestei consultatii exista deja");
        }
    }

    public ResponseDto<ConsultationDto> getConsultationByAppointmentId(UUID appointmentId) {

        var consultation = consultationRepository.findByAppointmentAppointmentId(appointmentId)
                .orElseThrow(() -> new BadRequestException("Consultatie nu exista"));

        return ResponseDto.<ConsultationDto>
                        builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultatia a fost adusa cu succes")
                .data(modelMapper.map(consultation, ConsultationDto.class))
                .build();
    }

    public ResponseDto<List<ConsultationDto>> getConsultationsForPatient(UUID patientId) {

        var patient = patientId != null
                ? patientRepository.findById(patientId)
                : patientRepository.findByUser(userService.getLoggedUser());

        var resolvedPatient = patient
                .orElseThrow(() -> new BadRequestException("Profilul de pacient nu exista pentru acest utilizator"));

        var consultations = consultationRepository
                .findByAppointmentPatientPatientIdOrderByConsultationDateDesc(resolvedPatient.getPatientId());

        var message = consultations.isEmpty()
                ? "Nu exista consultatii pentru acest pacient"
                : "Consultatiile au fost aduse cu succes";

        return ResponseDto.<List<ConsultationDto>>
                        builder()
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .data(consultations.stream().map(c -> modelMapper.map(c, ConsultationDto.class)).toList())
                .build();
    }
}
