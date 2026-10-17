package com.solorecord.solorecord_backend.events.model;

import java.time.Instant;
import java.util.UUID;

public record PatientLookedUpEvent(
        UUID eventId,
        Instant occurredAt,
        UUID doctorId,
        UUID facilityId,
        boolean matched
) {
    public static PatientLookedUpEvent of(UUID doctorId, UUID facilityId, boolean matched) {
        return new PatientLookedUpEvent(UUID.randomUUID(), Instant.now(), doctorId, facilityId, matched);
    }
}
