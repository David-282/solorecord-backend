package com.solorecord.solorecord_backend.events.model;

import java.time.Instant;
import java.util.UUID;

/** Carries the one-time code to the notification module. Never log this event. */
public record OtpIssuedEvent(
        UUID eventId,
        Instant occurredAt,
        String email,
        String otp
) {

    public static OtpIssuedEvent of(String email, String otp) {
        return new OtpIssuedEvent(UUID.randomUUID(), Instant.now(), email, otp);
    }

    @Override
    public String toString() {
        return "OtpIssuedEvent[eventId=" + eventId + "]";
    }
}
