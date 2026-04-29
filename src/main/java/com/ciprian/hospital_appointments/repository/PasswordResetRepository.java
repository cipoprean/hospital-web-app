package com.ciprian.hospital_appointments.repository;

import com.ciprian.hospital_appointments.domain.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, String> {

    Optional<PasswordReset> findByCode(String code);

    void deleteByUserUserId(String userId);
}
