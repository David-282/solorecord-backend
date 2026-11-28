package com.solorecord.solorecord_backend.events.model;

import java.time.Instant;
import java.util.UUID;

/** Raised when a result is marked COMPLETED. Outbreak detection and notification consume it. */
public record TestResultCompletedEvent(
        UUID eventId,
        Instant occurredAt,
        UUID resultId,
        UUID patientId,
        UUID facilityId,
        String testType,
        UUID reviewingDoctorId
) {

    public static TestResultCompletedEvent of(UUID resultId, UUID patientId, UUID facilityId,
                                              String testType, UUID reviewingDoctorId) {
        return new TestResultCompletedEvent(UUID.randomUUID(), Instant.now(),
                resultId, patientId, facilityId, testType, reviewingDoctorId);
    }
}
