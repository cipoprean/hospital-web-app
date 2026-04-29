package com.ciprian.hospital_appointments.domain;

import com.ciprian.hospital_appointments.domain.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String appointmentId;
    @Column(nullable = false)
    LocalDateTime startDate;
    LocalDateTime endDate;
    String meetingLink;
    String purposeOfConsultation;
    String initialSymptoms;
    @Enumerated(EnumType.STRING)
    AppointmentStatus appointmentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    Patient patient;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Consultation consultation;

}
