package com.solorecord.solorecord_backend.identity.service;

import com.solorecord.solorecord_backend.identity.data.repository.UserRepository;
import com.solorecord.solorecord_backend.identity.dto.request.LoginRequest;
import com.solorecord.solorecord_backend.identity.dto.response.AuthResponse;
import lombok.AllArgsConstructor;
import org.hibernate.boot.internal.Abstract;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;



    @Override
    public AuthResponse authenticateUser(LoginRequest request) {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
