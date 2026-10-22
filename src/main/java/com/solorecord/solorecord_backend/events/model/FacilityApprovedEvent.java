package com.solorecord.solorecord_backend.events.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record FacilityApprovedEvent(
        UUID eventId,
        UUID facilityId,
        UUID reviewedBy,
        LocalDateTime occurredAt
) {

    public static FacilityApprovedEvent of( UUID facilityId, UUID reviewedBy){

        return new FacilityApprovedEvent(
                UUID.randomUUID(),
                facilityId,
                reviewedBy,
                LocalDateTime.now()
        );
    }
}
