package com.ciprian.hospital_appointments.repository;

import com.ciprian.hospital_appointments.domain.Patient;
import com.ciprian.hospital_appointments.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, String> {

    Optional<Patient> findByUser(User user);
}
