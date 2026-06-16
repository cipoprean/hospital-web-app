package com.ciprian.hospital_appointments.api;

import com.ciprian.hospital_appointments.domain.enums.BloodGroup;
import com.ciprian.hospital_appointments.domain.enums.GenoType;
import com.ciprian.hospital_appointments.dto.PatientDto;
import com.ciprian.hospital_appointments.dto.ResponseDto;
import com.ciprian.hospital_appointments.service.PatientService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    @Test
    void profile_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();

        var patientDto = PatientDto.builder()
                .patientId(UUID.randomUUID().toString())
                .firstName("Ion")
                .lastName("Popescu")
                .build();

        var response = ResponseDto.<PatientDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Profilul pacientui a fost gasit cu succes")
                .data(patientDto)
                .build();

        when(patientService.getPatientProfile()).thenReturn(response);

        mockMvc.perform(get("/api/patient/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Ion"))
                .andExpect(jsonPath("$.data.lastName").value("Popescu"));
    }

    @Test
    void updatePatient_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();

        var updateDto = PatientDto.builder()
                .firstName("Ionut")
                .lastName("Popescu")
                .phoneNumber("0798765432")
                .build();

        var response = ResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Profilul pacientului a fost actualizat cu succes")
                .build();

        doReturn(response).when(patientService).updatePatientProfile(any());

        mockMvc.perform(put("/api/patient/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profilul pacientului a fost actualizat cu succes"));
    }

    @Test
    void updatePatient_shouldReturnBadRequest_whenPhoneNumberIsInvalid() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();

        var updateDto = PatientDto.builder()
                .firstName("Ionut")
                .phoneNumber("abc")
                .build();

        mockMvc.perform(put("/api/patient/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePatient_shouldReturnBadRequest_whenFirstNameExceedsMaxLength() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();

        var updateDto = PatientDto.builder()
                .firstName("a".repeat(101))
                .build();

        mockMvc.perform(put("/api/patient/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePatient_shouldReturnBadRequest_whenLastNameExceedsMaxLength() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();

        var updateDto = PatientDto.builder()
                .lastName("a".repeat(101))
                .build();

        mockMvc.perform(put("/api/patient/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePatient_shouldReturnBadRequest_whenKnownAllergiesExceedsMaxLength() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();

        var updateDto = PatientDto.builder()
                .knownAllergies("a".repeat(501))
                .build();

        mockMvc.perform(put("/api/patient/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPatientById_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();

        var patientId = UUID.randomUUID();
        var patientDto = PatientDto.builder()
                .patientId(patientId.toString())
                .firstName("Ion")
                .build();

        var response = ResponseDto.<PatientDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Pacientul a fost gasit cu succes")
                .data(patientDto)
                .build();

        when(patientService.getPatientById(patientId)).thenReturn(response);

        mockMvc.perform(get("/api/patient/{id}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Ion"));
    }

    @Test
    void getBloodGroups_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();

        var response = ResponseDto.<List<BloodGroup>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lista grupelor de sange a fost adusa cu succes")
                .data(List.of(BloodGroup.values()))
                .build();

        when(patientService.getBloodGroups()).thenReturn(response);

        mockMvc.perform(get("/api/patient/blood-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").value("A_POSITIVE"))
                .andExpect(jsonPath("$.data[7]").value("O_NEGATIVE"));
    }

    @Test
    void getGenoTypes_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();

        var response = ResponseDto.<List<GenoType>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Lista categoriilor de grupe de sange a fost adusa cu succes")
                .data(List.of(GenoType.values()))
                .build();

        when(patientService.getGenoTypes()).thenReturn(response);

        mockMvc.perform(get("/api/patient/geno-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").value("AA"))
                .andExpect(jsonPath("$.data[3]").value("AC"));
    }
}
