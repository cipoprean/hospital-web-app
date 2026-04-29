package com.ciprian.hospital_appointments.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDto {

    @NotBlank(message = "Email-ul este obligatoriu")
    @Email
    private String email;

    @NotBlank(message = "Parola este obligatorie")
    private String password;
}
