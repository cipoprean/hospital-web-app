package com.ciprian.hospital_appointments.api;

import com.ciprian.hospital_appointments.dto.ConsultationDto;
import com.ciprian.hospital_appointments.dto.ResponseDto;
import com.ciprian.hospital_appointments.service.ConsultationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/consultation")
@RequiredArgsConstructor
@Tag(name = "API Consultatii")
public class ConsultationController {

    private final ConsultationService consultationService;


    @PostMapping
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<ResponseDto<ConsultationDto>> createConsultation(@RequestBody @Valid ConsultationDto consultationDto) {
        return ResponseEntity.ok(consultationService.createConsultation(consultationDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<ConsultationDto>> getConsultationByAppointmentId(@PathVariable UUID id) {
        return ResponseEntity.ok(consultationService.getConsultationByAppointmentId(id));
    }

    @GetMapping("/{patientId}/istoric")
    public ResponseEntity<ResponseDto<List<ConsultationDto>>> getConsultationByPatientId(@PathVariable UUID patientId) {
        return ResponseEntity.ok(consultationService.getConsultationsForPatient(patientId));
    }
}
