package com.solorecord.solorecord_backend.clinical.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AllergyResponse(
        UUID id, UUID patientId, UUID doctorId, UUID facilityId,
        String allergen, String severity, LocalDateTime recordedAt
) {
}
