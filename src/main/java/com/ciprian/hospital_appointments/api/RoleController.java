package com.ciprian.hospital_appointments.api;

import com.ciprian.hospital_appointments.domain.Role;
import com.ciprian.hospital_appointments.dto.ModificaRolDto;
import com.ciprian.hospital_appointments.dto.ResponseDto;
import com.ciprian.hospital_appointments.service.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/role")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
@Tag(name = "API Roluri")
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/all")
    public ResponseEntity<ResponseDto<List<Role>>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PostMapping
    public ResponseEntity<ResponseDto<Role>> addRole(@RequestBody ModificaRolDto role) {
        return ResponseEntity.ok(roleService.createRole(role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<Role>> updateRole(@PathVariable String id, @RequestBody ModificaRolDto role) {
        return ResponseEntity.ok(roleService.updateRole(id, role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Role>> deleteRole(@PathVariable String id) {
        return ResponseEntity.ok(roleService.deleteRole(id));
    }
}
