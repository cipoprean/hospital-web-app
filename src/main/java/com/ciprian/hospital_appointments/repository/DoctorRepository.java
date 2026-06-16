package com.ciprian.hospital_appointments.repository;

import com.ciprian.hospital_appointments.domain.Doctor;
import com.ciprian.hospital_appointments.domain.User;
import com.ciprian.hospital_appointments.domain.enums.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    Optional<Doctor> findByUser(User user);

    List<Doctor> findBySpecialization(Specialization specialization);
}
