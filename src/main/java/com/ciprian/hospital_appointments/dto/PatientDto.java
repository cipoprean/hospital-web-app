package com.ciprian.hospital_appointments.dto;

import com.ciprian.hospital_appointments.domain.enums.BloodGroup;
import com.ciprian.hospital_appointments.domain.enums.GenoType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @Size(max = 100, message = "Prenumele trebuie sa aiba maxim 100 de caractere")
    String firstName;

    @Size(max = 100, message = "Numele trebuie sa aiba maxim 100 de caractere")
    String lastName;

    LocalDate birthDate;

    @Pattern(regexp = "^\\+?[0-9\\s\\-()/]{7,20}$", message = "Numarul de telefon nu este valid")
    String phoneNumber;

    @Size(max = 500, message = "Alergiile cunoscute trebuie sa aiba maxim 500 de caractere")
    String knownAllergies;

    BloodGroup bloodGroup;
    GenoType genoType;
    UserDto user;
}
