package com.solorecord.solorecord_backend.identity.service;

import com.solorecord.solorecord_backend.identity.dto.request.LoginRequest;
import com.solorecord.solorecord_backend.identity.dto.response.AuthResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {

    AuthResponse authenticateUser(LoginRequest request);
}
