package com.ciprian.hospital_appointments.repository;

import com.ciprian.hospital_appointments.domain.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultationRepository extends JpaRepository<Consultation, String> {

    Optional<Consultation> findByAppointmentAppointmentId(UUID appointmentId);

    List<Consultation> findByAppointmentPatientPatientIdOrderByConsultationDateDesc(UUID patientId);
}
