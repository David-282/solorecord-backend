package com.solorecord.solorecord_backend.identity.service;


import com.solorecord.solorecord_backend.identity.data.model.User;
import com.solorecord.solorecord_backend.identity.data.repository.UserRepository;
import com.solorecord.solorecord_backend.identity.dto.request.CreateUserRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public User createUser(CreateUserRequest createUserRequest) {
        return null;
    }

    @Override
    public void deactivateUser(UUID userId) {

    }

    @Override
    public void activateUser(UUID userId) {

    }

    @Override
    public User getUserById(UUID userId) {
        return null;
    }

    @Override
    public List<User> getUserByFacilityId(UUID facilityId) {
        return List.of();
    }

}


