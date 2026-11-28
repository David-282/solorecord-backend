package com.solorecord.solorecord_backend.events.model;

import java.time.Instant;
import java.util.UUID;

public record AccessDeniedEvent(
        UUID eventId,
        Instant occurredAt,
        UUID grantId,
        UUID patientId,
        UUID doctorId,
        UUID facilityId
) {

    public static AccessDeniedEvent of(UUID grantId, UUID patientId, UUID doctorId, UUID facilityId) {
        return new AccessDeniedEvent(UUID.randomUUID(), Instant.now(), grantId, patientId, doctorId, facilityId);
    }
}
