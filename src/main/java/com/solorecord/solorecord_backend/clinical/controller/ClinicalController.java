package com.solorecord.solorecord_backend.clinical.controller;

import com.solorecord.solorecord_backend.clinical.dto.request.AllergyRequest;
import com.solorecord.solorecord_backend.clinical.dto.request.DiagnosisRequest;
import com.solorecord.solorecord_backend.clinical.dto.request.PrescriptionRequest;
import com.solorecord.solorecord_backend.clinical.dto.request.VisitNoteRequest;
import com.solorecord.solorecord_backend.clinical.dto.response.AllergyResponse;
import com.solorecord.solorecord_backend.clinical.dto.response.DiagnosisResponse;
import com.solorecord.solorecord_backend.clinical.dto.response.PatientRecordResponse;
import com.solorecord.solorecord_backend.clinical.dto.response.PrescriptionResponse;
import com.solorecord.solorecord_backend.clinical.dto.response.VisitNoteResponse;
import com.solorecord.solorecord_backend.clinical.service.ClinicalService;
import com.solorecord.solorecord_backend.identity.data.model.User;
import com.solorecord.solorecord_backend.identity.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clinical")
@RequiredArgsConstructor
public class ClinicalController {

    private final ClinicalService clinicalService;

    @PostMapping("/patients/{patientId}/diagnoses")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DiagnosisResponse> addDiagnosis(@PathVariable UUID patientId,
                                                          @Valid @RequestBody DiagnosisRequest request,
                                                          @AuthenticationPrincipal CustomUserDetails principal) {
        User doctor = principal.getUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clinicalService.addDiagnosis(doctor.getId(), doctor.getFacilityId(), patientId, request));
    }

    @PostMapping("/patients/{patientId}/prescriptions")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<PrescriptionResponse> addPrescription(@PathVariable UUID patientId,
                                                                @Valid @RequestBody PrescriptionRequest request,
                                                                @AuthenticationPrincipal CustomUserDetails principal) {
        User doctor = principal.getUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clinicalService.addPrescription(doctor.getId(), doctor.getFacilityId(), patientId, request));
    }

    @PostMapping("/patients/{patientId}/allergies")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AllergyResponse> addAllergy(@PathVariable UUID patientId,
                                                      @Valid @RequestBody AllergyRequest request,
                                                      @AuthenticationPrincipal CustomUserDetails principal) {
        User doctor = principal.getUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clinicalService.addAllergy(doctor.getId(), doctor.getFacilityId(), patientId, request));
    }

    @PostMapping("/patients/{patientId}/visit-notes")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<VisitNoteResponse> addVisitNote(@PathVariable UUID patientId,
                                                          @Valid @RequestBody VisitNoteRequest request,
                                                          @AuthenticationPrincipal CustomUserDetails principal) {
        User doctor = principal.getUser();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clinicalService.addVisitNote(doctor.getId(), doctor.getFacilityId(), patientId, request));
    }

    @GetMapping("/patients/{patientId}/record")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<PatientRecordResponse> getRecord(@PathVariable UUID patientId,
                                                           @AuthenticationPrincipal CustomUserDetails principal) {
        User doctor = principal.getUser();
        return ResponseEntity.ok(clinicalService.getRecordForDoctor(doctor.getId(), doctor.getFacilityId(), patientId));
    }

    @GetMapping("/me/record")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientRecordResponse> getOwnRecord(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(clinicalService.getOwnRecord(principal.getUser().getId()));
    }
}
