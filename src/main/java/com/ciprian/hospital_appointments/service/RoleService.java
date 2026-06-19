package com.ciprian.hospital_appointments.service;

import com.ciprian.hospital_appointments.config.exceptions.BadRequestException;
import com.ciprian.hospital_appointments.domain.Role;
import com.ciprian.hospital_appointments.dto.ModificaRolDto;
import com.ciprian.hospital_appointments.dto.ResponseDto;
import com.ciprian.hospital_appointments.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public ResponseDto<Role> createRole(ModificaRolDto roleDto) {

        Role savedRole = roleRepository.save(Role
                .builder()
                .roleName(roleDto.getName())
                .build());

        return ResponseDto.<Role>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Rolul a fost adaugat cu succes!")
                .data(savedRole)
                .build();
    }

    public ResponseDto<Role> updateRole(String roleId, ModificaRolDto roleDto) {

        var role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BadRequestException("Rolul nu exista"));

        role.setRoleName(roleDto.getName());
        var updatedRole = roleRepository.save(role);

        return ResponseDto.<Role>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Rolul a fost modificat cu succes!")
                .data(updatedRole)
                .build();
    }

    public ResponseDto<Role> deleteRole(String roleId) {
        var role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BadRequestException("Rolul nu exista"));

        roleRepository.delete(role);

        return ResponseDto.<Role>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Rolul a fost sters cu succes")
                .build();
    }

    public ResponseDto<List<Role>> getAllRoles() {
        var roles = roleRepository.findAll();

        return ResponseDto.<List<Role>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Rolurile au fost aduse cu succes!")
                .data(roles)
                .build();
    }
}
