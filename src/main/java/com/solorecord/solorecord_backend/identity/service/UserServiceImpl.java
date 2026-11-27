package com.solorecord.solorecord_backend.identity.service;


import com.solorecord.solorecord_backend.events.model.StaffOnboardedEvent;
import com.solorecord.solorecord_backend.events.publisher.EventPublisher;
import com.solorecord.solorecord_backend.facility.dto.request.StaffOnboardingRequest;
import com.solorecord.solorecord_backend.facility.service.FacilityService;
import com.solorecord.solorecord_backend.identity.data.model.AccountStatus;
import com.solorecord.solorecord_backend.identity.data.model.Role;
import com.solorecord.solorecord_backend.identity.data.model.User;
import com.solorecord.solorecord_backend.identity.data.repository.UserRepository;
import com.solorecord.solorecord_backend.identity.dto.request.ActivateAccountRequest;
import com.solorecord.solorecord_backend.identity.dto.request.CreateUserRequest;
import com.solorecord.solorecord_backend.identity.dto.request.VerifyOtpRequest;
import com.solorecord.solorecord_backend.identity.dto.response.RegisterResponse;
import com.solorecord.solorecord_backend.identity.dto.response.UserContactResponse;
import com.solorecord.solorecord_backend.identity.dto.response.UserResponse;
import com.solorecord.solorecord_backend.identity.exception.*;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    /** Roles created by someone else and activated through the OTP flow. */
    private static final Set<Role> INVITED_ROLES =
            EnumSet.of(Role.FACILITY_ADMIN, Role.DOCTOR, Role.LAB_TECHNICIAN);

    /** Roles a facility admin may deactivate or reactivate. */
    private static final Set<Role> STAFF_ROLES = EnumSet.of(Role.DOCTOR, Role.LAB_TECHNICIAN);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final EventPublisher eventPublisher;
    private final FacilityService facilityService;

    private static String normalise(String email) {
        return email.strip().toLowerCase();
    }

    @Override
    @Transactional
    public RegisterResponse createUser(@NonNull CreateUserRequest createUserRequest) {
        String validEmail = normalise(createUserRequest.getEmail());

        if (userRepository.existsByEmail(validEmail)) {
            throw new UserAlreadyExistException("User already exist");
        }

        User user = new User(
                createUserRequest.getFullName(),
                validEmail,
                passwordEncoder.encode(createUserRequest.getRawPassword()),
                createUserRequest.getPhoneNumber(),
                Role.PATIENT
        );
        user.setStatus(AccountStatus.PENDING_VERIFICATION);
        userRepository.save(user);

        otpService.generateAndSendOtp(user.getEmail());

        return new RegisterResponse(
                "Registration successful for " + user.getEmail(),
                user.getEmail()
        );
    }

    @Override
    @Transactional
    public void verifyOtp(VerifyOtpRequest request) {
        String validEmail = normalise(request.getEmail());

        if (!otpService.verifyOtp(validEmail, request.getOtp())) {
            throw new InvalidOtpException("Otp is not valid");
        }

        // Self-registration is for patients only. Invited roles activate through /activation.
        User user = userRepository.findByEmail(validEmail)
                .filter(u -> u.getRole() == Role.PATIENT)
                .filter(u -> u.getStatus() == AccountStatus.PENDING_VERIFICATION)
                .orElseThrow(() -> new InvalidOtpException("Otp is not valid"));

        user.activate();
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void createSuperAdmin(String fullName, String email, String rawPassword, String phoneNumber) {
        String validEmail = normalise(email);

        if (userRepository.existsByRole(Role.SUPER_ADMIN)) {
            return;
        }
        if (userRepository.existsByEmail(validEmail)) {
            throw new UserAlreadyExistException("An account with the super admin email already exists");
        }

        User admin = new User(
                fullName,
                validEmail,
                passwordEncoder.encode(rawPassword),
                Role.SUPER_ADMIN,
                phoneNumber);
        admin.activate();
        userRepository.save(admin);
    }

    private User findManageableStaff(UUID userId, UUID adminId, UUID facilityId) {
        if (userId.equals(adminId)) {
            throw new UserNotAuthorizedException("You cannot change your own account status");
        }
        // One query on id AND facility: another facility's user is "not found",
        // the same answer as a user that doesn't exist, so nothing leaks.
        return userRepository.findByIdAndFacilityId(userId, facilityId)
                .filter(u -> STAFF_ROLES.contains(u.getRole()))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Transactional
    @Override
    public void deactivateStaff(UUID userId, UUID adminId, UUID facilityId) {
        User user = findManageableStaff(userId, adminId, facilityId);
        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidAccountStateException("Only active accounts can be deactivated");
        }
        user.deactivate();
    }

    @Transactional
    @Override
    public void reactivateStaff(UUID userId, UUID adminId, UUID facilityId) {
        User user = findManageableStaff(userId, adminId, facilityId);
        if (user.getStatus() != AccountStatus.INACTIVE) {
            throw new InvalidAccountStateException("Only deactivated accounts can be reactivated");
        }
        user.activate();
    }

    @Override
    @Transactional(readOnly = true)
    public UserContactResponse getUserContact(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User does not exist"));

        return new UserContactResponse(user.getFullName(), user.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUserByFacilityId(UUID facilityId) {
        return userRepository.findByFacilityId(facilityId).stream()
                .map(user -> new UserResponse(
                        user.getFullName(),
                        user.getEmail(),
                        user.getStatus(),
                        user.getRole().name(),
                        user.getId()
                ))
                .toList();
    }

    @Override
    public boolean existById(UUID id) {
        return userRepository.existsById(id);
    }

    @Override
    @Transactional
    public void createFacilityAdmin(String fullName, String email, String phone, UUID facilityId) {
        String validEmail = normalise(email);
        if (userRepository.existsByEmail(validEmail)) {
            throw new UserAlreadyExistException("An account with this email already exists");
        }

        String placeholderHash = passwordEncoder.encode(UUID.randomUUID().toString());
        userRepository.save(User.invited(fullName, validEmail, phone, placeholderHash,
                facilityId, Role.FACILITY_ADMIN));
    }

    @Override
    public void requestActivationCode(String email) {
        String validEmail = normalise(email);

        // Count first, so real and fake emails are limited the same way.
        otpService.checkRequestLimit(validEmail);

        userRepository.findByEmail(validEmail)
                .filter(u -> INVITED_ROLES.contains(u.getRole()))
                .filter(u -> u.getStatus() == AccountStatus.PENDING_VERIFICATION)
                .ifPresent(u -> otpService.generateAndSendOtp(validEmail));
    }

    @Override
    @Transactional
    public void activateAccount(ActivateAccountRequest request) {
        String validEmail = normalise(request.email());

        if (!otpService.verifyOtp(validEmail, request.otp())) {
            throw new InvalidOtpException("Invalid or expired code");
        }

        User user = userRepository.findByEmail(validEmail)
                .filter(u -> INVITED_ROLES.contains(u.getRole()))
                .filter(u -> u.getStatus() == AccountStatus.PENDING_VERIFICATION)
                .orElseThrow(() -> new InvalidOtpException("Invalid or expired code"));

        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.activate();
    }

    @Transactional
    @Override
    public void createStaff(StaffOnboardingRequest request, UUID facilityAdminId, UUID facilityId) {
        String validEmail = normalise(request.email());

        if (userRepository.existsByEmail(validEmail)) {
            throw new UserAlreadyExistException("User already exist");
        }

        if (!userRepository.existsByIdAndRole(facilityAdminId, Role.FACILITY_ADMIN)) {
            throw new UserNotAuthorizedException("User not authorized");
        }

        String placeholderHash = passwordEncoder.encode(UUID.randomUUID().toString());
        User user = User.invited(
                request.fullName(),
                validEmail,
                request.phoneNumber(),
                placeholderHash,
                facilityId,
                Role.valueOf(request.role().name())
        );

        String facilityName = facilityService.getFacilityName(facilityId);

        userRepository.save(user);

        eventPublisher.publish(StaffOnboardedEvent.of(user.getEmail(), user.getFullName(), facilityName));
    }
}
