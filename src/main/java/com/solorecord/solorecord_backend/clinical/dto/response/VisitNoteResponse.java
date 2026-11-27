package com.solorecord.solorecord_backend.clinical.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record VisitNoteResponse(
        UUID id, UUID patientId, UUID doctorId, UUID facilityId,
        String bloodPressure, Double temperature, Integer heartRate, Integer respiratoryRate,
        Double oxygenSaturation, Double weight, String otherVitals, String notes, LocalDateTime visitedAt
) {
}
