package com.solorecord.solorecord_backend.identity.controller;

import com.solorecord.solorecord_backend.identity.dto.request.CreateUserRequest;
import com.solorecord.solorecord_backend.identity.dto.request.LoginRequest;
import com.solorecord.solorecord_backend.identity.dto.request.VerifyOtpRequest;
import com.solorecord.solorecord_backend.identity.dto.response.AuthResponse;
import com.solorecord.solorecord_backend.identity.dto.response.RegisterResponse;
import com.solorecord.solorecord_backend.identity.service.AuthService;
import com.solorecord.solorecord_backend.identity.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody CreateUserRequest request) {
        RegisterResponse response = userService.createUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest request) {
        userService.verifyOtp(request);
        return ResponseEntity.ok("Account verified successfully. You may now log in.");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.authenticateUser(request);
        return ResponseEntity.ok(response);
    }
}