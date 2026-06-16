package com.ciprian.hospital_appointments.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModificaRolDto {

    @NotBlank(message = "Campul nume este obligatoriu")
    String name;
}
