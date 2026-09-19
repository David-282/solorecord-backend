package com.solorecord.solorecord_backend.identity.service;

import com.solorecord.solorecord_backend.identity.dto.request.CreateUserRequest;
import com.solorecord.solorecord_backend.identity.dto.request.VerifyOtpRequest;
import com.solorecord.solorecord_backend.identity.dto.response.RegisterResponse;
import com.solorecord.solorecord_backend.identity.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService{

    RegisterResponse createUser(CreateUserRequest createUserRequest);

    void deactivateUser(UUID userId);

    void activateUser(UUID userId);

    void verifyOtp (VerifyOtpRequest request);

    List<UserResponse> getUserByFacilityId(UUID facilityId);

}
