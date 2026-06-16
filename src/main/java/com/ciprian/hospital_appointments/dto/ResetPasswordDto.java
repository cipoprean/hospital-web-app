package com.ciprian.hospital_appointments.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResetPasswordDto {
    @NotBlank(message = "Codul este obligatoriu")
    @Email(message = "Email invalid")
    private String email;
    @NotBlank(message = "Email-ul este obligatoriu")
    @Size(max = 5, message = "Codul trebuie sa contina maxim 5 caractere")
    private String code;
    @NotBlank
    @Size(min = 8, message = "Parola trebuie să aibă minim 8 caractere")
    private String newPassword;

}
