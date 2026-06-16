package com.ciprian.hospital_appointments.api;

import com.ciprian.hospital_appointments.dto.AppointmentDto;
import com.ciprian.hospital_appointments.dto.ResponseDto;
import com.ciprian.hospital_appointments.service.AppointmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/appointment")
@RequiredArgsConstructor
@Tag(name = "API Planificare consultatii")
public class AppointmentController {

    private final AppointmentService appointmentService;


    @PostMapping("/book")
    public ResponseEntity<ResponseDto<AppointmentDto>> bookAppointment(@RequestBody @Valid AppointmentDto appointmentDto) {
        return ResponseEntity.ok(appointmentService.bookAppointment(appointmentDto));
    }

    @GetMapping
    public ResponseEntity<ResponseDto<List<AppointmentDto>>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getMyAppointments());
    }

    @PutMapping("/cancel/{appointmentId}")
    public ResponseEntity<ResponseDto<?>> cancelAppointment(@PathVariable UUID appointmentId) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(appointmentId));
    }

    @PutMapping("/complete/{appointmentId}")
    public ResponseEntity<ResponseDto<?>> completeAppointment(@PathVariable UUID appointmentId) {
        return ResponseEntity.ok(appointmentService.completeAppointment(appointmentId));
    }
}
