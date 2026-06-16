package com.ciprian.hospital_appointments.api;

import com.ciprian.hospital_appointments.dto.ResponseDto;
import com.ciprian.hospital_appointments.dto.UpdatePasswordDto;
import com.ciprian.hospital_appointments.dto.UserDto;
import com.ciprian.hospital_appointments.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
@Tag(name = "API Utilizatori")
public class UserController {

    private final UserService userService;

    @GetMapping("/current-user")
    public ResponseEntity<ResponseDto<UserDto>> getCurrentLoggedUser() {
        return ResponseEntity.ok(userService.getLoggedUserProfile());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<UserDto>> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDto<List<UserDto>>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/update-password")
    public ResponseEntity<ResponseDto<?>> updateUserPassword(@RequestBody @Valid UpdatePasswordDto dto) {
        return ResponseEntity.ok(userService.updatePassword(dto));
    }

    @PutMapping("/profile-picture")
    public ResponseEntity<ResponseDto<?>> updateUserPassword(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(userService.updateProfilePicture(file));
    }
}
