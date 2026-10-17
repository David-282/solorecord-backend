package com.solorecord.solorecord_backend.patient.controller;

import com.solorecord.solorecord_backend.identity.data.model.User;
import com.solorecord.solorecord_backend.identity.security.CustomUserDetails;
import com.solorecord.solorecord_backend.patient.dto.request.CompleteProfileRequest;
import com.solorecord.solorecord_backend.patient.dto.request.PatientLookUpRequest;
import com.solorecord.solorecord_backend.patient.dto.response.PatientLookUpResponse;
import com.solorecord.solorecord_backend.patient.dto.response.ProfileStatusResponse;
import com.solorecord.solorecord_backend.patient.service.PatientLookUpService;
import com.solorecord.solorecord_backend.patient.service.PatientService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
@AllArgsConstructor
public class PatientController {

    private final PatientService patientService;

    private final PatientLookUpService patientLookUpService;

    @PostMapping("/complete-profile")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<String> completeProfile(@RequestBody @Valid CompleteProfileRequest request) {
        User user = getCurrentUser();
        patientService.completeProfile(user.getId(), request);
        return ResponseEntity.ok("Profile completed successfully.");
    }

    @GetMapping("/profile-status")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ProfileStatusResponse> getProfileStatus() {
        User user = getCurrentUser();
        boolean complete = patientService.hasCompletedProfile(user.getId());
        return ResponseEntity.ok(new ProfileStatusResponse(complete));
    }

    @PostMapping("/lookup")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<PatientLookUpResponse> patientLookUp (@RequestBody @Valid PatientLookUpRequest request){
        User user = getCurrentUser();
        return ResponseEntity.ok( patientLookUpService.lookup(user.getId(),user.getFacilityId(), request));

    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new IllegalStateException("No authenticated user found in security context");
        }

        return userDetails.getUser();
    }
}