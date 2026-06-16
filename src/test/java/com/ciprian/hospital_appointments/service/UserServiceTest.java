package com.ciprian.hospital_appointments.service;

import com.ciprian.hospital_appointments.config.exceptions.BadRequestException;
import com.ciprian.hospital_appointments.config.exceptions.NotFoundException;
import com.ciprian.hospital_appointments.domain.Role;
import com.ciprian.hospital_appointments.domain.User;
import com.ciprian.hospital_appointments.dto.ResponseDto;
import com.ciprian.hospital_appointments.dto.UpdatePasswordDto;
import com.ciprian.hospital_appointments.dto.UserDto;
import com.ciprian.hospital_appointments.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private NotificationService notificationService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        var role = Role.builder().roleId(UUID.randomUUID()).roleName("PATIENT").build();

        user = User.builder()
                .userId(userId)
                .name("Ion Popescu")
                .email("ion@example.com")
                .password("encodedPassword")
                .profilePictureUrl(null)
                .roles(List.of(role))
                .build();

        userDto = UserDto.builder()
                .userId(userId.toString())
                .name("Ion Popescu")
                .email("ion@example.com")
                .profilePictureUrl(null)
                .roles(List.of(role))
                .build();

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getLoggedUser_shouldReturnUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("ion@example.com");
        when(userRepository.findByEmail("ion@example.com")).thenReturn(Optional.of(user));

        var result = userService.getLoggedUser();

        assertThat(result).isEqualTo(user);
    }

    @Test
    void getLoggedUser_shouldThrowBadRequest_whenNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThatThrownBy(() -> userService.getLoggedUser())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Utilizatorul nu este autentificat");
    }

    @Test
    void getLoggedUser_shouldThrowNotFound_whenUserNotFound() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("unknown@example.com");
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getLoggedUser())
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Utilizatorul nu a fost gasit");
    }

    @Test
    void getUserById_shouldReturnUserDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        var response = userService.getUserById(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo("Datele utilizatorului au fost aduse cu succes");
        assertThat(response.getData()).isEqualTo(userDto);
    }

    @Test
    void getUserById_shouldThrowBadRequest_whenNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Utilizatorul nu exista");
    }

    @Test
    void getLoggedUserProfile_shouldReturnUserDto() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("ion@example.com");
        when(userRepository.findByEmail("ion@example.com")).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        var response = userService.getLoggedUserProfile();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo("Datele utilizatorului au fost aduse cu succes");
        assertThat(response.getData()).isEqualTo(userDto);
    }

    @Test
    void getAllUsers_shouldReturnUserList() {
        var user2 = User.builder().userId(UUID.randomUUID()).name("Maria").email("maria@example.com").build();
        var userDto2 = UserDto.builder().userId(user2.getUserId().toString()).name("Maria").email("maria@example.com").build();

        when(userRepository.findAll()).thenReturn(List.of(user, user2));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);
        when(modelMapper.map(user2, UserDto.class)).thenReturn(userDto2);

        var response = userService.getAllUsers();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo("Utilizatorii au fost adusi cu succes");
        assertThat(response.getData()).hasSize(2);
    }

    @Test
    void getAllUsers_shouldReturnEmptyList_whenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        var response = userService.getAllUsers();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getData()).isEmpty();
    }

    @Test
    void updatePassword_shouldUpdateSuccessfully() {
        var dto = new UpdatePasswordDto();
        dto.setOldPassword("oldPass");
        dto.setNewPassword("newPass");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("ion@example.com");
        when(userRepository.findByEmail("ion@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("newEncodedPass");

        var response = userService.updatePassword(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMessage()).isEqualTo("Parola a fost actualizata cu succes");
        verify(userRepository).save(user);
        verify(notificationService).sendEmail(any(), eq(user));
    }

    @Test
    void updatePassword_shouldThrowBadRequest_whenPasswordsAreNull() {
        var dto = new UpdatePasswordDto();
        dto.setOldPassword(null);
        dto.setNewPassword(null);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("ion@example.com");
        when(userRepository.findByEmail("ion@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.updatePassword(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Noua sau vechea parola nu exista");
    }

    @Test
    void updatePassword_shouldThrowBadRequest_whenOldPasswordDoesNotMatch() {
        var dto = new UpdatePasswordDto();
        dto.setOldPassword("wrongPassword");
        dto.setNewPassword("newPass");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("ion@example.com");
        when(userRepository.findByEmail("ion@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> userService.updatePassword(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Parolele nu coincid.");
    }
}
