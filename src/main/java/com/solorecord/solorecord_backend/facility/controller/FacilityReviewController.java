package com.solorecord.solorecord_backend.facility.controller;

import com.solorecord.solorecord_backend.facility.dto.request.FacilityRejectRequest;
import com.solorecord.solorecord_backend.facility.dto.response.PendingFacilityResponse;
import com.solorecord.solorecord_backend.facility.service.FacilityService;
import com.solorecord.solorecord_backend.identity.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/facilities")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
public class FacilityReviewController {
    private final FacilityService facilityService;

    @GetMapping("/pending")
    public ResponseEntity<List<PendingFacilityResponse>> pending() {
        return ResponseEntity.ok(facilityService.getPendingFacilities());
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable UUID id,
                                        @AuthenticationPrincipal CustomUserDetails principal) {
        facilityService.approveFacility(id,principal.getUser().getId() ); // ADAPT: however your principal exposes the user id
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable UUID id,
                                       @Valid @RequestBody FacilityRejectRequest request,
                                       @AuthenticationPrincipal CustomUserDetails principal) {
        facilityService.disapproveFacility(id, principal.getUser().getId(),request);
        return ResponseEntity.noContent().build();
    }
}