package com.ciprian.hospital_appointments.dto;

import com.ciprian.hospital_appointments.domain.enums.AppointmentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentDto {

    String appointmentId;
    LocalDateTime startDate;
    LocalDateTime endDate;
    String meetingLink;
    String purposeOfConsultation;
    String initialSymptoms;
    AppointmentStatus appointmentStatus;
    DoctorDto doctor;
    PatientDto patient;
}
