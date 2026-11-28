package com.solorecord.solorecord_backend.events.model;

import java.time.Instant;
import java.util.UUID;

/** Carries the applicant's contact details because a rejected facility has no user account. */
public record FacilityRejectedEvent(
        UUID eventId,
        Instant occurredAt,
        UUID facilityId,
        String facilityName,
        String adminFullName,
        String adminEmail,
        String rejectionReason
) {

    public static FacilityRejectedEvent of(UUID facilityId, String facilityName, String adminFullName,
                                           String adminEmail, String rejectionReason) {
        return new FacilityRejectedEvent(UUID.randomUUID(), Instant.now(), facilityId, facilityName,
                adminFullName, adminEmail, rejectionReason);
    }
}
