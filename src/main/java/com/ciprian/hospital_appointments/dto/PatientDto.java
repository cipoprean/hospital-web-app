package com.ciprian.hospital_appointments.dto;

import com.ciprian.hospital_appointments.domain.enums.BloodGrouo;
import com.ciprian.hospital_appointments.domain.enums.GenoType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientDto {

    String patientId;
    String firstName;
    String lastName;
    LocalDate birthDate;
    String phoneNumber;
    String knownAllergies;
    BloodGrouo bloodGrouo;
    GenoType genoType;
    UserDto user;
}
