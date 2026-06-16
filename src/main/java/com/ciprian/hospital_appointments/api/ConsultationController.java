package com.ciprian.hospital_appointments.api;

import com.ciprian.hospital_appointments.service.ConsultationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/consultation")
@RequiredArgsConstructor
@Tag(name = "API Consultatii")
public class ConsultationController {

    private final ConsultationService consultationService;
}
