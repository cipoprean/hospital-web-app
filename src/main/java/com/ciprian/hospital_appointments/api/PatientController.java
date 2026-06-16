package com.ciprian.hospital_appointments.api;

import com.ciprian.hospital_appointments.domain.enums.BloodGroup;
import com.ciprian.hospital_appointments.domain.enums.GenoType;
import com.ciprian.hospital_appointments.dto.PatientDto;
import com.ciprian.hospital_appointments.dto.ResponseDto;
import com.ciprian.hospital_appointments.service.PatientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/patient")
@RequiredArgsConstructor
@Tag(name = "API Pacienti")
public class PatientController {

    private final PatientService patientService;


    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<ResponseDto<PatientDto>> profile() {
        return ResponseEntity.ok(patientService.getPatientProfile());
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<ResponseDto<?>> updatePatient(@RequestBody @Valid PatientDto patientDto) {
        return ResponseEntity.ok(patientService.updatePatientProfile(patientDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<PatientDto>> getPatientById(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping("/blood-groups")
    public ResponseEntity<ResponseDto<List<BloodGroup>>> getBloodGroups() {
        return ResponseEntity.ok(patientService.getBloodGroups());
    }

    @GetMapping("/geno-types")
    public ResponseEntity<ResponseDto<List<GenoType>>> getGenoTypes() {
        return ResponseEntity.ok(patientService.getGenoTypes());
    }

}
