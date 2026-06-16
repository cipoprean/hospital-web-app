package com.ciprian.hospital_appointments.domain;

import com.ciprian.hospital_appointments.domain.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notification")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID notificationId;

    String subject;
    String recipient;
    String message;

    @Enumerated(EnumType.STRING)
    NotificationType type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;


}
