package com.solorecord.solorecord_backend.patient.service;

import com.solorecord.solorecord_backend.patient.dto.request.CompleteProfileRequest;

import java.util.UUID;

public interface PatientService {

    void completeProfile(UUID userId, CompleteProfileRequest request);
    boolean hasCompletedProfile(UUID userId);
}
