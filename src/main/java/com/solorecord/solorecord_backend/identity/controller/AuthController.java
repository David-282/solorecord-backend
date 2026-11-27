package com.solorecord.solorecord_backend.identity.controller;

import com.solorecord.solorecord_backend.identity.dto.request.*;
import com.solorecord.solorecord_backend.identity.dto.response.AuthResponse;
import com.solorecord.solorecord_backend.identity.dto.response.RegisterResponse;
import com.solorecord.solorecord_backend.identity.service.AuthService;
import com.solorecord.solorecord_backend.identity.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Public authentication endpoints. Everything under /api/v1/auth is open in SecurityConfig,
 * so nothing that needs a logged-in user belongs here.
 */
@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid CreateUserRequest request) {
        RegisterResponse response = userService.createUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(@RequestBody @Valid VerifyOtpRequest request) {
        userService.verifyOtp(request);
        return ResponseEntity.ok(Map.of("message", "Account verified successfully. You may now log in."));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        AuthResponse response = authService.authenticateUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/activation/request-code")
    public ResponseEntity<Map<String, String>> requestCode(
            @Valid @RequestBody RequestActivationCodeRequest request) {
        userService.requestActivationCode(request.email());
        return ResponseEntity.ok(Map.of(
                "message", "If this account is awaiting activation, a code has been sent to the email."));
    }

    @PostMapping("/activation/activate")
    public ResponseEntity<Void> activate(@Valid @RequestBody ActivateAccountRequest request) {
        userService.activateAccount(request);
        return ResponseEntity.noContent().build();
    }
}
