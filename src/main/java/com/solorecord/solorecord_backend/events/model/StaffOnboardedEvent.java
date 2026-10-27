package com.solorecord.solorecord_backend.events.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record StaffOnboardedEvent(
         String email,
        @NotNull UUID eventId,
                Instant occurredAt,
         String fullName,
         String facilityName

        ) {

    public static StaffOnboardedEvent of(String email, String fullName, String facilityName){
        return new StaffOnboardedEvent(
                email,
                UUID.randomUUID(),
                Instant.now(),
                fullName,
                facilityName

        );
    }
}
