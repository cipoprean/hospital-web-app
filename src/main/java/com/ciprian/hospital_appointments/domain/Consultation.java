package com.ciprian.hospital_appointments.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "consultation")
public class Consultation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID consultationId;
    private LocalDateTime consultationDate;

    @Lob
    private String subjectiveNotes;
    @Lob
    private String objectiveFindings;
    @Lob
    private String assesments;
    @Lob
    private String plan;

    @OneToOne
    @JoinColumn(name = "appointment_id", unique = true, nullable = false)
    private Appointment appointment;
}
