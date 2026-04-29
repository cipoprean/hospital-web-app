package com.ciprian.hospital_appointments.dto;

import com.ciprian.hospital_appointments.domain.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private String userId;
    private String name;
    private String email;
    @JsonInclude
    private String password;
    private String profilePictureUrl;
    private List<Role> roles;

}
