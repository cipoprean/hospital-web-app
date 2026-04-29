package com.ciprian.hospital_appointments.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePasswordDto {

    @NotBlank(message = "Vechea parola este obligatorie")
    private String oldPassword;
    @NotBlank(message = "Noua parola este obligatorie")
    private String newPassword;
}
