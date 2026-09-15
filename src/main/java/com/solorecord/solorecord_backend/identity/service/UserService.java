package com.solorecord.solorecord_backend.identity.service;

import com.solorecord.solorecord_backend.identity.data.model.User;
import com.solorecord.solorecord_backend.identity.dto.request.CreateUserRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.UUID;

public interface UserService{

    User createUser(CreateUserRequest createUserRequest);

    void deactivateUser(UUID userId);

    void activateUser(UUID userId);

    User getUserById(UUID userId);

    List<User> getUserByFacilityId(UUID facilityId);

}
