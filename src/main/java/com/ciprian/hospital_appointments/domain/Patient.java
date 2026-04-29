package com.ciprian.hospital_appointments.domain;

import com.ciprian.hospital_appointments.domain.enums.BloodGrouo;
import com.ciprian.hospital_appointments.domain.enums.GenoType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "patient")
public class Patient {

    String firstName;
    String lastName;
    LocalDate birthDate;
    String phoneNumber;
    @Lob
    String knownAllergies;
    @Enumerated(EnumType.STRING)
    BloodGrouo bloodGrouo;
    @Enumerated(EnumType.STRING)
    GenoType genoType;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    User user;
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Appointment> appointments;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String patientId;


}
