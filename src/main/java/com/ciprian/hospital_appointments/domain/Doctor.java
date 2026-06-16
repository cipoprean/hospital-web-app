package com.ciprian.hospital_appointments.domain;

import com.ciprian.hospital_appointments.domain.enums.Specialization;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "doctor")
public class Doctor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID doctorId;

    String firstName;
    String lastName;

    @Enumerated(EnumType.STRING)
    Specialization specialization;

    String licenseNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    User user;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Appointment> appointments;
}
