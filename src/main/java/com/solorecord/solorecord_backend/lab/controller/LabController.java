package com.solorecord.solorecord_backend.lab.controller;

import com.solorecord.solorecord_backend.identity.data.model.User;
import com.solorecord.solorecord_backend.identity.security.CustomUserDetails;
import com.solorecord.solorecord_backend.lab.dto.request.LabInitiatedResultRequest;
import com.solorecord.solorecord_backend.lab.dto.request.ResultValueRequest;
import com.solorecord.solorecord_backend.lab.dto.request.RouteResultRequest;
import com.solorecord.solorecord_backend.lab.dto.request.TestOrderRequest;
import com.solorecord.solorecord_backend.lab.dto.response.TestOrderResponse;
import com.solorecord.solorecord_backend.lab.dto.response.TestResultResponse;
import com.solorecord.solorecord_backend.lab.service.LabService;
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
@RequestMapping("/api/v1/lab")
@RequiredArgsConstructor
public class LabController {

    private final LabService labService;

    @PostMapping("/orders")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<TestOrderResponse> createOrder(@Valid @RequestBody TestOrderRequest request,
                                                         @AuthenticationPrincipal CustomUserDetails principal) {
        User doctor = principal.getUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(labService.createOrder(doctor.getId(), doctor.getFacilityId(), request));
    }

    @GetMapping("/orders/pending")
    @PreAuthorize("hasRole('LAB_TECHNICIAN')")
    public ResponseEntity<List<TestOrderResponse>> pendingOrders(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(labService.listPendingOrders(principal.getUser().getFacilityId()));
    }

    @PostMapping("/orders/{orderId}/results")
    @PreAuthorize("hasRole('LAB_TECHNICIAN')")
    public ResponseEntity<TestResultResponse> enterOrderResult(@PathVariable UUID orderId,
                                                               @Valid @RequestBody ResultValueRequest request,
                                                               @AuthenticationPrincipal CustomUserDetails principal) {
        User tech = principal.getUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(labService.enterOrderResult(tech.getId(), tech.getFacilityId(), orderId, request));
    }

    @PostMapping("/results")
    @PreAuthorize("hasRole('LAB_TECHNICIAN')")
    public ResponseEntity<TestResultResponse> enterLabInitiatedResult(@Valid @RequestBody LabInitiatedResultRequest request,
                                                                      @AuthenticationPrincipal CustomUserDetails principal) {
        User tech = principal.getUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(labService.enterLabInitiatedResult(tech.getId(), tech.getFacilityId(), request));
    }

    @PatchMapping("/results/{resultId}")
    @PreAuthorize("hasRole('LAB_TECHNICIAN')")
    public ResponseEntity<TestResultResponse> updateResult(@PathVariable UUID resultId,
                                                           @Valid @RequestBody ResultValueRequest request,
                                                           @AuthenticationPrincipal CustomUserDetails principal) {
        User tech = principal.getUser();
        return ResponseEntity.ok(labService.updateResultValue(tech.getId(), tech.getFacilityId(), resultId, request));
    }

    @PatchMapping("/results/{resultId}/complete")
    @PreAuthorize("hasRole('LAB_TECHNICIAN')")
    public ResponseEntity<TestResultResponse> completeResult(@PathVariable UUID resultId,
                                                             @AuthenticationPrincipal CustomUserDetails principal) {
        User tech = principal.getUser();
        return ResponseEntity.ok(labService.completeResult(tech.getId(), tech.getFacilityId(), resultId));
    }

    @PatchMapping("/results/{resultId}/reviewer")
    @PreAuthorize("hasRole('FACILITY_ADMIN')")
    public ResponseEntity<TestResultResponse> routeResult(@PathVariable UUID resultId,
                                                          @Valid @RequestBody RouteResultRequest request,
                                                          @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(labService.routeResult(
                principal.getUser().getFacilityId(), resultId, request.doctorId()));
    }

    @GetMapping("/patients/{patientId}/results")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<TestResultResponse>> resultsForPatient(@PathVariable UUID patientId,
                                                                      @AuthenticationPrincipal CustomUserDetails principal) {
        User doctor = principal.getUser();
        return ResponseEntity.ok(labService.getResultsForDoctor(doctor.getId(), doctor.getFacilityId(), patientId));
    }

    @GetMapping("/me/results")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<TestResultResponse>> ownResults(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(labService.getOwnResults(principal.getUser().getId()));
    }
}
