package com.ciprian.hospital_appointments.repository;

import com.ciprian.hospital_appointments.domain.Doctor;
import com.ciprian.hospital_appointments.domain.User;
import com.ciprian.hospital_appointments.domain.enums.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, String> {

    Optional<Doctor> findByUser(User user);

    List<Doctor> findBySpecialization(Specialization specialization);
}
