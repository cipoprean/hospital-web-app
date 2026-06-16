package com.ciprian.hospital_appointments.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordDto {
    @NotBlank(message = "Email-ul este obligatoriu")
    @Email(message = "Email invalid")
    private String email;
}
