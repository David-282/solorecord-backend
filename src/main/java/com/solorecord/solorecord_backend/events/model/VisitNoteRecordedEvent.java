package com.solorecord.solorecord_backend.events.model;

import java.time.Instant;
import java.util.UUID;

public record VisitNoteRecordedEvent(
        UUID eventId,
        Instant occurredAt,
        UUID recordId,
        UUID patientId,
        UUID doctorId,
        UUID facilityId
) {

    public static VisitNoteRecordedEvent of(UUID recordId, UUID patientId, UUID doctorId, UUID facilityId) {
        return new VisitNoteRecordedEvent(UUID.randomUUID(), Instant.now(), recordId, patientId, doctorId, facilityId);
    }
}
