package com.ciprian.hospital_appointments.api;

import com.ciprian.hospital_appointments.domain.Role;
import com.ciprian.hospital_appointments.dto.ModificaRolDto;
import com.ciprian.hospital_appointments.dto.ResponseDto;
import com.ciprian.hospital_appointments.service.RoleService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roleController).build();
    }

    @Test
    void getAllRoles_shouldReturnOk() throws Exception {
        var roles = List.of(new Role(), new Role());
        var response = ResponseDto.<List<Role>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("success")
                .data(roles)
                .build();

        when(roleService.getAllRoles()).thenReturn(response);

        mockMvc.perform(get("/api/role/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));
    }

    @Test
    void getAllRoles_shouldReturnOk_whenNotAuthenticated() throws Exception {
        var roles = List.of(new Role(), new Role());
        var response = ResponseDto.<List<Role>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("success")
                .data(roles)
                .build();

        when(roleService.getAllRoles()).thenReturn(response);

        mockMvc.perform(get("/api/role/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));
    }

    @Test
    void addRole_shouldReturnOk() throws Exception {
        var dto = new ModificaRolDto();
        dto.setName("ROLE_TEST");

        var role = Role.builder().roleName("ROLE_TEST").build();

        var response = ResponseDto.<Role>builder()
                .statusCode(HttpStatus.OK.value())
                .message("created")
                .data(role)
                .build();

        when(roleService.createRole(any())).thenReturn(response);

        mockMvc.perform(post("/api/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.roleName").value("ROLE_TEST"));
    }

    @Test
    void updateRole_shouldReturnOk() throws Exception {
        var dto = new ModificaRolDto();
        dto.setName("ROLE_UPDATED");

        var role = Role.builder().roleName("ROLE_UPDATED").build();

        var response = ResponseDto.<Role>builder()
                .statusCode(HttpStatus.OK.value())
                .message("updated")
                .data(role)
                .build();

        when(roleService.updateRole(eq("1"), any())).thenReturn(response);

        mockMvc.perform(put("/api/role/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.roleName").value("ROLE_UPDATED"));
    }

    @Test
    void deleteRole_shouldReturnOk() throws Exception {
        var response = ResponseDto.<Role>builder()
                .statusCode(HttpStatus.OK.value())
                .message("deleted")
                .build();

        when(roleService.deleteRole("1")).thenReturn(response);

        mockMvc.perform(delete("/api/role/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("deleted"));
    }
}
