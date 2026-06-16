package com.ciprian.hospital_appointments.dto;

import com.ciprian.hospital_appointments.domain.enums.AppointmentStatus;
import com.ciprian.hospital_appointments.validator.ValidDateRange;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@ValidDateRange(startDate = "startDate", endDate = "endDate")
public class AppointmentDto {

    UUID appointmentId;
    @NotNull(message = "Campul doctorId este obligatoriu")
    UUID doctorId;
    @NotNull(message = "Campul Data inceput este obligatoriu")
    @Future(message = "Campul data inceput nu poate fi in trecut")
    LocalDateTime startDate;
    LocalDateTime endDate;

    String meetingLink;
    String purposeOfConsultation;
    String initialSymptoms;
    
    AppointmentStatus appointmentStatus;
    DoctorDto doctor;
    PatientDto patient;
}
