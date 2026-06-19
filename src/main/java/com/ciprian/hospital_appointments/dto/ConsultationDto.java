package com.ciprian.hospital_appointments.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class ConsultationDto {

    UUID consultationId;
    @NotNull(message = "Campul doctorId este obligatoriu")
    UUID appointmentId;
    @NotNull(message = "Campul doctorId este obligatoriu")
    LocalDateTime consultationDate;
    String subjectiveNotes;
    String objectiveFindings;
    String assesments;
    String plan;
}
