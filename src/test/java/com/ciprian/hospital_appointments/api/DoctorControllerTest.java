package com.ciprian.hospital_appointments.api;

import com.ciprian.hospital_appointments.domain.enums.Specialization;
import com.ciprian.hospital_appointments.dto.DoctorDto;
import com.ciprian.hospital_appointments.dto.ResponseDto;
import com.ciprian.hospital_appointments.service.DoctorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DoctorControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private DoctorController doctorController;

    @Test
    void profile_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(doctorController).build();

        var doctorDto = DoctorDto.builder()
                .doctorId(UUID.randomUUID().toString())
                .firstName("Andrei")
                .lastName("Pop")
                .specialization(Specialization.CARDIOLOGIE)
                .build();

        var response = ResponseDto.<DoctorDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Profilul doctorului a fost gasit cu succes")
                .data(doctorDto)
                .build();

        when(doctorService.getDoctorProfile()).thenReturn(response);

        mockMvc.perform(get("/api/doctor/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Andrei"))
                .andExpect(jsonPath("$.data.lastName").value("Pop"));
    }

    @Test
    void updateDoctor_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(doctorController).build();

        var updateDto = DoctorDto.builder()
                .firstName("Andrei")
                .lastName("Pop")
                .licenseNumber("LIC-12345")
                .build();

        var response = ResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Profilul doctorului a fost actualizat cu succes")
                .build();

        doReturn(response).when(doctorService).updateDoctorProfile(any());

        mockMvc.perform(put("/api/doctor/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profilul doctorului a fost actualizat cu succes"));
    }

    @Test
    void updateDoctor_shouldReturnBadRequest_whenLicenseNumberIsInvalid() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(doctorController).build();

        var updateDto = DoctorDto.builder()
                .licenseNumber("ab")
                .build();

        mockMvc.perform(put("/api/doctor/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateDoctor_shouldReturnBadRequest_whenFirstNameExceedsMaxLength() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(doctorController).build();

        var updateDto = DoctorDto.builder()
                .firstName("a".repeat(101))
                .build();

        mockMvc.perform(put("/api/doctor/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateDoctor_shouldReturnBadRequest_whenLastNameExceedsMaxLength() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(doctorController).build();

        var updateDto = DoctorDto.builder()
                .lastName("a".repeat(101))
                .build();

        mockMvc.perform(put("/api/doctor/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDoctorById_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(doctorController).build();

        var doctorId = UUID.randomUUID();
        var doctorDto = DoctorDto.builder()
                .doctorId(doctorId.toString())
                .firstName("Andrei")
                .build();

        var response = ResponseDto.<DoctorDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Doctorul a fost gasit cu succes")
                .data(doctorDto)
                .build();

        when(doctorService.getDoctorById(doctorId)).thenReturn(response);

        mockMvc.perform(get("/api/doctor/{id}", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Andrei"));
    }

    @Test
    void getDoctorsBySpecialization_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(doctorController).build();

        var doctorDto = DoctorDto.builder()
                .firstName("Andrei")
                .specialization(Specialization.CARDIOLOGIE)
                .build();

        var response = ResponseDto.<List<DoctorDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Doctorii au fost adusi cu succes")
                .data(List.of(doctorDto))
                .build();

        when(doctorService.getDoctorsBySpecialization(Specialization.CARDIOLOGIE)).thenReturn(response);

        mockMvc.perform(get("/api/doctor/specialization/CARDIOLOGIE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].firstName").value("Andrei"));
    }

    @Test
    void getAllDoctors_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(doctorController).build();

        var response = ResponseDto.<List<DoctorDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Doctorii au fost adusi cu succes")
                .data(List.of())
                .build();

        when(doctorService.getAllDoctor()).thenReturn(response);

        mockMvc.perform(get("/api/doctor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getAllSpecializations_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(doctorController).build();

        var response = ResponseDto.<List<Specialization>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Specializarile au fost aduse cu succes")
                .data(List.of(Specialization.values()))
                .build();

        when(doctorService.getAllSpecialization()).thenReturn(response);

        mockMvc.perform(get("/api/doctor/specialization"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").isString());
    }
}
