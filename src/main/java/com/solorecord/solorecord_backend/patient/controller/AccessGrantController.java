package com.solorecord.solorecord_backend.patient.controller;

import com.solorecord.solorecord_backend.identity.security.CustomUserDetails;
import com.solorecord.solorecord_backend.patient.dto.request.AccessRequest;
import com.solorecord.solorecord_backend.patient.dto.response.AccessGrantResponse;
import com.solorecord.solorecord_backend.patient.service.AccessGrantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/access-grants")
@RequiredArgsConstructor
@Tag(name = "Access Grants")
public class AccessGrantController {

    private final AccessGrantService accessGrantService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AccessGrantResponse> requestAccess(
            @Valid @RequestBody AccessRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {

        AccessGrantResponse response = accessGrantService.accessRequest(
                principal.getUser().getId(),
                principal.getUser().getFacilityId(),
                request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{grantId}/approve")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Void> approve(
            @PathVariable UUID grantId,
            @AuthenticationPrincipal CustomUserDetails principal) {

        accessGrantService.accessApprove(principal.getUser().getId(), grantId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{grantId}/deny")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Void> deny(
            @PathVariable UUID grantId,
            @AuthenticationPrincipal CustomUserDetails principal) {

        accessGrantService.accessDeny(principal.getUser().getId(), grantId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{grantId}/revoke")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Void> revoke(
            @PathVariable UUID grantId,
            @AuthenticationPrincipal CustomUserDetails principal) {

        accessGrantService.accessRevoke(principal.getUser().getId(), grantId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<AccessGrantResponse>> pending(
            @AuthenticationPrincipal CustomUserDetails principal) {

        return ResponseEntity.ok(accessGrantService.listPending(principal.getUser().getId()));
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<AccessGrantResponse>> mine(
            @AuthenticationPrincipal CustomUserDetails principal) {

        return ResponseEntity.ok(accessGrantService.listForDoctor(principal.getUser().getId()));
    }
}