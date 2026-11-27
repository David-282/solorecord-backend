package com.solorecord.solorecord_backend.clinical.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record DiagnosisResponse(
        UUID id, UUID patientId, UUID doctorId, UUID facilityId,
        String condition, String notes, UUID testResultId, LocalDateTime diagnosedAt
) {
}
