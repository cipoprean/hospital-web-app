package com.ciprian.hospital_appointments.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserLoginResponseDto {

    private String token;
    private List<String> roles;
}
