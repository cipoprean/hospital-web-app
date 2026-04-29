package com.ciprian.hospital_appointments.dto;

import com.ciprian.hospital_appointments.domain.enums.Specialization;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoctorDto {

    String doctorId;
    String firstName;
    String lastName;
    Specialization specialization;
    String licenseNumber;
    UserDto user;
}
