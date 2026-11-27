package com.solorecord.solorecord_backend.events.model;

import java.time.Instant;
import java.util.UUID;

/** Raised when a facility admin routes a lab-initiated result to a doctor for review. */
public record TestResultAssignedEvent(
        UUID eventId,
        Instant occurredAt,
        UUID resultId,
        UUID doctorId,
        UUID facilityId
) {

    public static TestResultAssignedEvent of(UUID resultId, UUID doctorId, UUID facilityId) {
        return new TestResultAssignedEvent(UUID.randomUUID(), Instant.now(), resultId, doctorId, facilityId);
    }
}
