package com.ciprian.hospital_appointments.api;

import com.ciprian.hospital_appointments.dto.ConsultationDto;
import com.ciprian.hospital_appointments.dto.ResponseDto;
import com.ciprian.hospital_appointments.service.ConsultationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ConsultationControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ConsultationService consultationService;

    @BeforeEach
    void setUp() {
        objectMapper.findAndRegisterModules();
    }

    @InjectMocks
    private ConsultationController consultationController;

    @Test
    void createConsultation_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(consultationController).build();

        var requestDto = ConsultationDto.builder()
                .appointmentId(UUID.randomUUID())
                .consultationDate(LocalDateTime.now())
                .subjectiveNotes("Subiectiv")
                .objectiveFindings("Obiectiv")
                .assesments("Evaluari")
                .plan("Plan")
                .build();

        var responseDto = ConsultationDto.builder()
                .consultationId(UUID.randomUUID())
                .appointmentId(requestDto.getAppointmentId())
                .consultationDate(requestDto.getConsultationDate())
                .subjectiveNotes("Subiectiv")
                .objectiveFindings("Obiectiv")
                .assesments("Evaluari")
                .plan("Plan")
                .build();

        var response = ResponseDto.<ConsultationDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultatia a fost creata cu succes")
                .data(responseDto)
                .build();

        when(consultationService.createConsultation(any())).thenReturn(response);

        mockMvc.perform(post("/api/consultation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.subjectiveNotes").value("Subiectiv"))
                .andExpect(jsonPath("$.message").value("Consultatia a fost creata cu succes"));
    }

    @Test
    void createConsultation_shouldReturnBadRequest_whenAppointmentIdIsNull() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(consultationController).build();

        var requestDto = ConsultationDto.builder()
                .consultationDate(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/api/consultation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getConsultationByAppointmentId_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(consultationController).build();

        var appointmentId = UUID.randomUUID();
        var consultationDto = ConsultationDto.builder()
                .consultationId(UUID.randomUUID())
                .appointmentId(appointmentId)
                .subjectiveNotes("Subiectiv")
                .build();

        var response = ResponseDto.<ConsultationDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultatia a fost adusa cu succes")
                .data(consultationDto)
                .build();

        when(consultationService.getConsultationByAppointmentId(appointmentId)).thenReturn(response);

        mockMvc.perform(get("/api/consultation/{id}", appointmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.subjectiveNotes").value("Subiectiv"));
    }

    @Test
    void getConsultationsByPatientId_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(consultationController).build();

        var patientId = UUID.randomUUID();
        var consultationDto = ConsultationDto.builder()
                .consultationId(UUID.randomUUID())
                .subjectiveNotes("Subiectiv")
                .build();

        var response = ResponseDto.<List<ConsultationDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultatiile au fost aduse cu succes")
                .data(List.of(consultationDto))
                .build();

        when(consultationService.getConsultationsForPatient(patientId)).thenReturn(response);

        mockMvc.perform(get("/api/consultation/{patientId}/istoric", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].subjectiveNotes").value("Subiectiv"));
    }
}
