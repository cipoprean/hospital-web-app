package com.ciprian.hospital_appointments.api;

import com.ciprian.hospital_appointments.dto.ResponseDto;
import com.ciprian.hospital_appointments.dto.UpdatePasswordDto;
import com.ciprian.hospital_appointments.dto.UserDto;
import com.ciprian.hospital_appointments.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
class UserControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getCurrentLoggedUser_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        var userDto = UserDto.builder()
                .userId(UUID.randomUUID().toString())
                .name("Ion Popescu")
                .email("ion@example.com")
                .build();

        var response = ResponseDto.<UserDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Datele utilizatorului au fost aduse cu succes")
                .data(userDto)
                .build();

        when(userService.getLoggedUserProfile()).thenReturn(response);

        mockMvc.perform(get("/api/user/current-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("ion@example.com"))
                .andExpect(jsonPath("$.data.name").value("Ion Popescu"));
    }

    @Test
    void getUserById_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        var userId = UUID.randomUUID();
        var userDto = UserDto.builder().userId(userId.toString()).name("Ion Popescu").build();

        var response = ResponseDto.<UserDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Datele utilizatorului au fost aduse cu succes")
                .data(userDto)
                .build();

        when(userService.getUserById(userId)).thenReturn(response);

        mockMvc.perform(get("/api/user/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Ion Popescu"));
    }

    @Test
    void getAllUsers_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        var userDto = UserDto.builder().userId(UUID.randomUUID().toString()).name("Ion Popescu").build();

        var response = ResponseDto.<List<UserDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Utilizatorii au fost adusi cu succes")
                .data(List.of(userDto))
                .build();

        when(userService.getAllUsers()).thenReturn(response);

        mockMvc.perform(get("/api/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Ion Popescu"));
    }

    @Test
    void updateUserPassword_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        var dto = new UpdatePasswordDto();
        dto.setOldPassword("oldPass");
        dto.setNewPassword("newPass");

        var response = ResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Parola a fost actualizata cu succes")
                .build();

        doReturn(response).when(userService).updatePassword(any());

        mockMvc.perform(put("/api/user/update-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Parola a fost actualizata cu succes"));
    }

    @Test
    void updateUserPassword_shouldReturnBadRequest_whenOldPasswordIsBlank() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        var dto = new UpdatePasswordDto();
        dto.setNewPassword("newPass");

        mockMvc.perform(put("/api/user/update-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserPassword_shouldReturnBadRequest_whenNewPasswordIsBlank() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        var dto = new UpdatePasswordDto();
        dto.setOldPassword("oldPass");

        mockMvc.perform(put("/api/user/update-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProfilePicture_shouldReturnOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        var response = ResponseDto.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Poza de profil a fost incarcata cu succes")
                .build();

        doReturn(response).when(userService).updateProfilePicture(any());

        var file = new MockMultipartFile("file", "profile.jpg", "image/jpeg", "image-content".getBytes());

        mockMvc.perform(multipart("/api/user/profile-picture")
                        .file(file)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Poza de profil a fost incarcata cu succes"));
    }
}
