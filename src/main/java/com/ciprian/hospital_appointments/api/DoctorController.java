package com.ciprian.hospital_appointments.api;

import com.ciprian.hospital_appointments.domain.enums.Specialization;
import com.ciprian.hospital_appointments.dto.DoctorDto;
import com.ciprian.hospital_appointments.dto.ResponseDto;
import com.ciprian.hospital_appointments.service.DoctorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/doctor")
@RequiredArgsConstructor
@Tag(name = "API Doctori")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<ResponseDto<DoctorDto>> profile() {
        return ResponseEntity.ok(doctorService.getDoctorProfile());
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<ResponseDto<?>> updateDoctor(@RequestBody @Valid DoctorDto doctorDto) {
        return ResponseEntity.ok(doctorService.updateDoctorProfile(doctorDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<DoctorDto>> getDoctorById(@PathVariable UUID id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @GetMapping("specialization/{specialization}")
    public ResponseEntity<ResponseDto<List<DoctorDto>>> getDoctorById(@PathVariable Specialization specialization) {
        return ResponseEntity.ok(doctorService.getDoctorsBySpecialization(specialization));
    }

    @GetMapping
    public ResponseEntity<ResponseDto<List<DoctorDto>>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctor());
    }

    @GetMapping("/specialization")
    public ResponseEntity<ResponseDto<List<Specialization>>> getAllSpecializations() {
        return ResponseEntity.ok(doctorService.getAllSpecialization());
    }
}
