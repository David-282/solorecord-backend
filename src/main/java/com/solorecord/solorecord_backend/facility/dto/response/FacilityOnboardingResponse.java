package com.solorecord.solorecord_backend.facility.dto.response;

import com.solorecord.solorecord_backend.facility.data.model.FacilityStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record FacilityOnboardingResponse(
        UUID id,
        String facilityName,
        LocalDateTime submittedAt,
        String message,
        FacilityStatus status
) {
}
