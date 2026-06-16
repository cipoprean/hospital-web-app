package com.ciprian.hospital_appointments.api;

import com.ciprian.hospital_appointments.dto.*;
import com.ciprian.hospital_appointments.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "API Autentificare")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDto<String>> register(@Valid @RequestBody UserRegistrationDto dto) {

        return ResponseEntity.ok(authenticationService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<UserLoginResponseDto>> login(@Valid @RequestBody UserLoginDto dto) {
        return ResponseEntity.ok(authenticationService.login(dto));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseDto<?>> forgotPasswort(@Valid @RequestBody ForgotPasswordDto dto) {
        return ResponseEntity.ok(authenticationService.forgetPassword(dto.getEmail()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseDto<?>> resetPasswort(@Valid @RequestBody ResetPasswordDto dto) {
        return ResponseEntity.ok(authenticationService.updatePasswordByResetCode(dto));
    }
}
