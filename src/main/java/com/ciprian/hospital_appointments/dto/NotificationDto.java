package com.ciprian.hospital_appointments.dto;

import com.ciprian.hospital_appointments.domain.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDto {

    Long notificationId;
    String subject;
    String recipient;
    NotificationType type;
    UserDto user;
    String message;
    LocalDateTime creationDate;

    //for email notification
    String templateName;
    Map<String, Object> templateVariables;
}
