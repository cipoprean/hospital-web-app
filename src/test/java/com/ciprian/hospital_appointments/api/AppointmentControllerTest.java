package com.ciprian.hospital_appointments.api;

import com.ciprian.hospital_appointments.dto.AppointmentDto;
import com.ciprian.hospital_appointments.dto.ResponseDto;
import com.ciprian.hospital_appointments.service.AppointmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentController appointmentController;

    @Test
    void bookAppointment_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(appointmentController).build();

        var startDate = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);

        var appointmentDto = AppointmentDto.builder()
                .doctorId(UUID.randomUUID())
                .startDate(startDate)
                .endDate(startDate.plusHours(1))
                .purposeOfConsultation("Consultatie cardiologie")
                .initialSymptoms("Durere in piept")
                .build();

        var response = ResponseDto.<AppointmentDto>builder()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .message("Programarea a fost efectuata cu succes")
                .data(appointmentDto)
                .build();

        when(appointmentService.bookAppointment(any(AppointmentDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/appointment/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Programarea a fost efectuata cu succes"))
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NO_CONTENT.value()));
    }

    @Test
    void bookAppointment_shouldReturnBadRequest_whenDoctorIdIsNull() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(appointmentController).build();

        var startDate = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);

        var appointmentDto = AppointmentDto.builder()
                .startDate(startDate)
                .endDate(startDate.plusHours(1))
                .build();

        mockMvc.perform(post("/api/appointment/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookAppointment_shouldReturnBadRequest_whenStartDateIsNull() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(appointmentController).build();

        var appointmentDto = AppointmentDto.builder()
                .doctorId(UUID.randomUUID())
                .build();

        mockMvc.perform(post("/api/appointment/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllAppointments_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(appointmentController).build();

        var startDate = LocalDateTime.now().plusDays(1);
        var appointmentDto = AppointmentDto.builder()
                .appointmentId(UUID.randomUUID())
                .startDate(startDate)
                .purposeOfConsultation("Consultatie")
                .build();

        var response = ResponseDto.<List<AppointmentDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultatiile au fost aduse cu succes")
                .data(List.of(appointmentDto))
                .build();

        when(appointmentService.getMyAppointments()).thenReturn(response);

        mockMvc.perform(get("/api/appointment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].purposeOfConsultation").value("Consultatie"));
    }

    @Test
    void cancelAppointment_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(appointmentController).build();

        var appointmentId = UUID.randomUUID();
        var response = ResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Appointment cancelled successfully")
                .build();

        doReturn(response).when(appointmentService).cancelAppointment(appointmentId);

        mockMvc.perform(put("/api/appointment/cancel/{appointmentId}", appointmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Appointment cancelled successfully"));
    }

    @Test
    void completeAppointment_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(appointmentController).build();

        var appointmentId = UUID.randomUUID();
        var response = ResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultatia a fost terminata cu succes! Se pot crea documentele rezultate in urma acesteia!")
                .build();

        doReturn(response).when(appointmentService).completeAppointment(appointmentId);

        mockMvc.perform(put("/api/appointment/complete/{appointmentId}", appointmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Consultatia a fost terminata cu succes! Se pot crea documentele rezultate in urma acesteia!"));
    }
}
