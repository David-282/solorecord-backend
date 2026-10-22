package com.solorecord.solorecord_backend.facility.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record PendingFacilityResponse(
        UUID id, String facilityName, String licenseNumber,
        UUID lgaId, String adminEmail, LocalDateTime submittedAt
) {
}
