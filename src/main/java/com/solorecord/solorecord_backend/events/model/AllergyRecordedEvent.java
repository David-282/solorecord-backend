package com.solorecord.solorecord_backend.events.model;

import java.time.Instant;
import java.util.UUID;

public record AllergyRecordedEvent(
        UUID eventId,
        Instant occurredAt,
        UUID recordId,
        UUID patientId,
        UUID doctorId,
        UUID facilityId
) {

    public static AllergyRecordedEvent of(UUID recordId, UUID patientId, UUID doctorId, UUID facilityId) {
        return new AllergyRecordedEvent(UUID.randomUUID(), Instant.now(), recordId, patientId, doctorId, facilityId);
    }
}
