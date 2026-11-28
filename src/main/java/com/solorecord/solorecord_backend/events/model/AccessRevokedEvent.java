package com.solorecord.solorecord_backend.events.model;

import java.time.Instant;
import java.util.UUID;

public record AccessRevokedEvent(
        UUID eventId,
        Instant occurredAt,
        UUID grantId,
        UUID patientId,
        UUID doctorId,
        UUID facilityId
) {

    public static AccessRevokedEvent of(UUID grantId, UUID patientId, UUID doctorId, UUID facilityId) {
        return new AccessRevokedEvent(UUID.randomUUID(), Instant.now(), grantId, patientId, doctorId, facilityId);
    }
}
