package com.solorecord.solorecord_backend.identity.service;


import com.solorecord.solorecord_backend.identity.data.model.AccountStatus;
import com.solorecord.solorecord_backend.identity.data.model.User;
import com.solorecord.solorecord_backend.identity.data.repository.UserRepository;
import com.solorecord.solorecord_backend.identity.dto.request.CreateUserRequest;
import com.solorecord.solorecord_backend.identity.dto.request.VerifyOtpRequest;
import com.solorecord.solorecord_backend.identity.dto.response.RegisterResponse;
import com.solorecord.solorecord_backend.identity.dto.response.UserResponse;
import com.solorecord.solorecord_backend.identity.exception.*;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;


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
                createUserRequest.getFacilityId(),
                createUserRequest.getRole()
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

}


