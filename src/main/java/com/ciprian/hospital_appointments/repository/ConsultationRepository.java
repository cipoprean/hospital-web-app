package com.ciprian.hospital_appointments.repository;

import com.ciprian.hospital_appointments.domain.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsultationRepository extends JpaRepository<Consultation, String> {

    Optional<Consultation> findByAppointmentAppointmentId(String appointmentId);

    List<Consultation> findByAppointmentPatientPatientIdOrderByConsultationDateDesc(String patientId);
}
