package com.solorecord.solorecord_backend.clinical.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record PrescriptionResponse(
        UUID id, UUID patientId, UUID doctorId, UUID facilityId,
        String medication, String dosage, String duration, UUID diagnosisId, LocalDateTime prescribedAt
) {
}
