package com.ciprian.hospital_appointments.dto;

import com.ciprian.hospital_appointments.domain.enums.Specialization;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @Size(max = 100, message = "Prenumele trebuie sa aiba maxim 100 de caractere")
    String firstName;

    @Size(max = 100, message = "Numele trebuie sa aiba maxim 100 de caractere")
    String lastName;

    Specialization specialization;

    @Pattern(regexp = "^[A-Za-z0-9\\-]{5,30}$", message = "Numarul de licenta nu este valid")
    String licenseNumber;

    UserDto user;
}
