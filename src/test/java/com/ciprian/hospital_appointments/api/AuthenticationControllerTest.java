package com.ciprian.hospital_appointments.api;

import com.ciprian.hospital_appointments.dto.*;
import com.ciprian.hospital_appointments.service.AuthenticationService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void register_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();

        var dto = new UserRegistrationDto();
        dto.setEmail("test@example.com");
        dto.setPassword("password123");
        dto.setName("Test User");

        var response = ResponseDto.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("success")
                .data("test@example.com")
                .build();

        when(authenticationService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").value("test@example.com"));
    }

    @Test
    void register_shouldReturnBadRequest_whenEmailIsBlank() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();

        var dto = new UserRegistrationDto();
        dto.setPassword("password123");
        dto.setName("Test User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();

        var dto = new UserLoginDto();
        dto.setEmail("test@example.com");
        dto.setPassword("password123");

        var loginData = UserLoginResponseDto.builder()
                .token("jwt-token")
                .roles(java.util.List.of("USER"))
                .build();

        var response = ResponseDto.<UserLoginResponseDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("success")
                .data(loginData)
                .build();

        when(authenticationService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("jwt-token"))
                .andExpect(jsonPath("$.data.roles[0]").value("USER"));
    }

    @Test
    void login_shouldReturnBadRequest_whenEmailIsBlank() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();

        var dto = new UserLoginDto();
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void forgotPassword_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();

        var dto = new ForgotPasswordDto();
        dto.setEmail("test@example.com");

        var response = ResponseDto.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Email trimis")
                .build();

        doReturn(response).when(authenticationService).forgetPassword(any());

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email trimis"));
    }

    @Test
    void forgotPassword_shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();

        var dto = new ForgotPasswordDto();
        dto.setEmail("invalid");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resetPassword_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();

        var dto = new ResetPasswordDto();
        dto.setEmail("test@example.com");
        dto.setCode("12345");
        dto.setNewPassword("newPassword123");

        var response = ResponseDto.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Parola actualizata")
                .build();

        doReturn(response).when(authenticationService).updatePasswordByResetCode(any());

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Parola actualizata"));
    }

    @Test
    void resetPassword_shouldReturnBadRequest_whenCodeIsMissing() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();

        var dto = new ResetPasswordDto();
        dto.setEmail("test@example.com");
        dto.setNewPassword("newPassword123");

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
