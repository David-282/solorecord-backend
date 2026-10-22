package com.solorecord.solorecord_backend.facility.controller;

import com.solorecord.solorecord_backend.facility.dto.request.FacilityOnboardingRequest;
import com.solorecord.solorecord_backend.facility.dto.response.FacilityOnboardingResponse;
import com.solorecord.solorecord_backend.facility.service.FacilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/facilities")
@RestController
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;


    @PostMapping("/apply")
    public ResponseEntity<FacilityOnboardingResponse> apply(
            @Valid @RequestBody FacilityOnboardingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(facilityService.onboardFacility(request));
    }
}
