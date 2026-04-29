package com.ciprian.hospital_appointments.repository;

import com.ciprian.hospital_appointments.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByName(String name);
}
