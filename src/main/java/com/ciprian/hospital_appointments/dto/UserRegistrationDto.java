package com.ciprian.hospital_appointments.dto;

import com.ciprian.hospital_appointments.domain.enums.Specialization;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegistrationDto {

    @NotBlank(message = "Numele este obligatoriu")
    String name;
    List<String> roles;
    private Specialization specialization; //just for doctors
    private String licenseNumber; //just for doctors
    @NotBlank(message = "Email-ul este obligatoriu")
    @Email
    private String email;
    @NotBlank(message = "Parola este obligatorie")
    private String password;

}
