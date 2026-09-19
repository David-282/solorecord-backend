package com.solorecord.solorecord_backend.identity.service;

import com.solorecord.solorecord_backend.identity.exception.IncorrectPasswordException;
import com.solorecord.solorecord_backend.identity.data.model.User;
import com.solorecord.solorecord_backend.identity.data.repository.UserRepository;
import com.solorecord.solorecord_backend.identity.dto.request.LoginRequest;
import com.solorecord.solorecord_backend.identity.dto.response.AuthResponse;
import com.solorecord.solorecord_backend.identity.security.CustomUserDetails;
import com.solorecord.solorecord_backend.identity.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;



    @Override
    public AuthResponse authenticateUser(LoginRequest request) {
        UserDetails user = loadUserByUsername(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            throw  new IncorrectPasswordException("User details not valid");

        }

        CustomUserDetails loggedUser = (CustomUserDetails) user;

        String token = jwtUtil.generateToken(loggedUser.getUser());

        return new AuthResponse(
                token,
                loggedUser.getUser().getRole().name(),
                loggedUser.getUser().getId(),
                user.getUsername()

        );
    }


    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User details not valid"));
        return new CustomUserDetails(user);


    }


}
