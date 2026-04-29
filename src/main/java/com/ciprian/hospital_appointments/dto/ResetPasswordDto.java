package com.ciprian.hospital_appointments.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResetPasswordDto {

    private String email;

    private String code;
    private String newPassword;

}
