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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.solorecord.solorecord_backend.patient.Mapper.map;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final EventPublisher eventPublisher;
    private final FacilityService facilityService;


    @Override
    public RegisterResponse createUser(@NonNull CreateUserRequest createUserRequest) {
        Optional<User> existingUser = userRepository.findByEmail(createUserRequest.getEmail());

        if(existingUser .isPresent()){
            throw new UserAlreadyExistException("User already exist");
        }

        String passwordHash = passwordEncoder.encode(createUserRequest.getRawPassword());
        User user = new User(
                createUserRequest.getFullName(),
                createUserRequest.getEmail(),
                passwordHash,
                createUserRequest.getPhoneNumber(),
                Role.PATIENT
        );
        user.setStatus(AccountStatus.PENDING_VERIFICATION);
        userRepository.save(user);

        otpService.generateAndSendOtp(user.getEmail());

        return new RegisterResponse(
                "Registration successful for "+ user.getEmail(),
                user.getEmail()
        );
    }

    @Override
    public void verifyOtp (VerifyOtpRequest request){

        if (otpService.verifyOtp(request.getEmail(), request.getOtp())){
            User user = userRepository.findByEmail(request.getEmail()).get();
            user.activate();
            userRepository.save(user);
            return;

        }

        throw new InvalidOtpException("Otp is not valid");

    }

    @Override
    @Transactional
    public void createSuperAdmin(String fullName, String email, String rawPassword) {
        String validEmail = email.strip().toLowerCase();

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
                "12345667");
        admin.activate();
        userRepository.save(admin);
    }

    @Override
    public void deactivateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User details not valid"));

        if(user.getStatus() == AccountStatus.INACTIVE){
            throw new UserAlreadyDeactivatedException("User is already deactivated");
        }

        if(user.getStatus() == AccountStatus.PENDING_VERIFICATION){
            throw new UserNotVerifiedException("User is not verified yet");
        }
        user.deactivate();
        userRepository.save(user);
    }

    @Override
    public void activateUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User details not valid"));

        if(user.getStatus() == AccountStatus.ACTIVE){
            throw new UserAlreadyActivatedException("User is already active");
        }

        if(user.getStatus() == AccountStatus.PENDING_VERIFICATION){
            throw new UserNotVerifiedException("User is not verified yet");
        }

        user.activate();
        userRepository.save(user);

    }

    @Override
    public UserContactResponse getUserContact(UUID id){
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new UserNotFoundException ("User does not exist");
        }

        return new UserContactResponse(
          user.get().getFullName(),
          user.get().getEmail()
        );

    }


    @Override
    public List<UserResponse> getUserByFacilityId(UUID facilityId) {

        List<User> users =  userRepository.findByFacilityId(facilityId);

        return users.stream()
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

        String validEmail = email.strip().toLowerCase();
        if (userRepository.existsByEmail(validEmail) ){
            throw new UserAlreadyExistException("An account with this email already exists");
        }

        String passwordHash = passwordEncoder.encode(UUID.randomUUID().toString());
        userRepository.save( map(validEmail, phone, facilityId, fullName,passwordHash));

    }


    @Override
    public void requestActivationCode(String email) {
        String validEmail = email.strip().toLowerCase();

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
        String validEmail = request.email().strip().toLowerCase();

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

        String validEmail = request.email().strip().toLowerCase();

        if(userRepository.existsByEmail(validEmail)){
            throw new UserAlreadyExistException("User already exist");
        }

        if(!userRepository.existsByIdAndRole(facilityAdminId, Role.FACILITY_ADMIN)){
            throw new UserNotAuthorizedException("User not authorized");
        }

        User user = new User(
                request.fullName(),
                request.email(),
                passwordEncoder.encode(UUID.randomUUID().toString()),
                facilityId,
                Role.valueOf(request.role().name())
        );
        user.setStatus(AccountStatus.PENDING_VERIFICATION);

        String facilityName = facilityService.getFacilityName(facilityId);

        eventPublisher.publish(StaffOnboardedEvent.of(user.getEmail(), user.getFullName(), facilityName));

    }

    private static final Set<Role> INVITED_ROLES =
            EnumSet.of(Role.FACILITY_ADMIN, Role.DOCTOR, Role.LAB_TECHNICIAN);


}


