package com.ciprian.hospital_appointments.repository;

import com.ciprian.hospital_appointments.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, String> {

    List<Appointment> findByDoctor_User_UserIdOrderByAppointmentIdDesc(Long userId);

    List<Appointment> findByPatient_User_UserIdOrderByAppointmentIdDesc(Long userId);

    @Query("""
             SELECT a FROM Appointment a
             WHERE a.doctor.doctorId = :doctorId
             AND a.appointmentStatus = 'SCHEDUELED'
             AND a.startDate < :newEndTime
             AND a.endDate < :newStartTime
            """)
    List<Appointment> findConflictingAppointments(String doctorId, LocalDateTime newStartTime, LocalDateTime newEndTime);
}
