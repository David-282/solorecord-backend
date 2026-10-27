package com.solorecord.solorecord_backend.identity.service;

import com.solorecord.solorecord_backend.facility.dto.request.StaffOnboardingRequest;
import com.solorecord.solorecord_backend.identity.dto.request.ActivateAccountRequest;
import com.solorecord.solorecord_backend.identity.dto.request.CreateUserRequest;
import com.solorecord.solorecord_backend.identity.dto.request.VerifyOtpRequest;
import com.solorecord.solorecord_backend.identity.dto.response.RegisterResponse;
import com.solorecord.solorecord_backend.identity.dto.response.UserContactResponse;
import com.solorecord.solorecord_backend.identity.dto.response.UserResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface UserService{

    RegisterResponse createUser(CreateUserRequest createUserRequest);

    void deactivateUser(UUID userId);

    void activateUser(UUID userId);

    void verifyOtp (VerifyOtpRequest request);

    void createSuperAdmin(String fullName, String email, String rawPassword);

    UserContactResponse getUserContact(UUID id);

    List<UserResponse> getUserByFacilityId(UUID facilityId);

    boolean existById (UUID id);

    void createFacilityAdmin(String s, String s1, String s2, UUID uuid);


    void requestActivationCode(String email);

    void activateAccount(ActivateAccountRequest request);

    @Transactional
    void createStaff(StaffOnboardingRequest request, UUID facilityAdminId, UUID facilityId);
}
