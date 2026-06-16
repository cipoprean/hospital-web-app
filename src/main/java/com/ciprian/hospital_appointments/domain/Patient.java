package com.ciprian.hospital_appointments.domain;

import com.ciprian.hospital_appointments.domain.enums.BloodGroup;
import com.ciprian.hospital_appointments.domain.enums.GenoType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "patient")
public class Patient extends BaseEntity {

    @Enumerated(EnumType.STRING)
    BloodGroup bloodGroup;
    String firstName;
    String lastName;
    LocalDate birthDate;
    String phoneNumber;
    @Lob
    String knownAllergies;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID patientId;
    @Enumerated(EnumType.STRING)
    GenoType genoType;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    User user;
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Appointment> appointments;


}
