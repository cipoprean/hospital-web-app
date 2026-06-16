package com.ciprian.hospital_appointments.repository;

import com.ciprian.hospital_appointments.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, String> {
}
