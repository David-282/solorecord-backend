package com.solorecord.solorecord_backend.notification.service;

import com.solorecord.solorecord_backend.notification.sender.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Sends through the configured channel with retries. Waits between attempts, doubling each time.
 * Every failed attempt and the final give-up are logged; recipients are masked in logs.
 * Retries happen on the async listener thread, so they never block or roll back the caller.
 * Pending retries are lost if the app stops (in-process only).
 */
@Component
public class NotificationDispatcher {

    private static final Logger log = LoggerFactory.getLogger(NotificationDispatcher.class);

    private final NotificationSender sender;
    private final int maxAttempts;
    private final long initialDelayMs;

    public NotificationDispatcher(
            NotificationSender sender,
            @Value("${app.notification.retry.max-attempts:3}") int maxAttempts,
            @Value("${app.notification.retry.initial-delay-ms:5000}") long initialDelayMs) {
        this.sender = sender;
        this.maxAttempts = Math.max(1, maxAttempts);
        this.initialDelayMs = Math.max(0, initialDelayMs);
    }

    /** @return true if delivered, false if every attempt failed. Never throws. */
    public boolean dispatch(String recipientEmail, String recipientName, String subject, String body) {
        long delay = initialDelayMs;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                sender.send(recipientEmail, recipientName, subject, body);
                if (attempt > 1) {
                    log.info("Email '{}' to {} delivered on attempt {}/{}", subject, mask(recipientEmail), attempt, maxAttempts);
                }
                return true;
            } catch (RuntimeException ex) {
                if (attempt == maxAttempts) {
                    log.error("Email '{}' to {} FAILED after {} attempts, giving up: {}",
                            subject, mask(recipientEmail), maxAttempts, ex.getMessage());
                    return false;
                }
                log.warn("Email '{}' to {} failed on attempt {}/{} ({}), retrying in {} ms",
                        subject, mask(recipientEmail), attempt, maxAttempts, ex.getMessage(), delay);
                if (!pause(delay)) {
                    log.error("Email '{}' to {} abandoned: retry interrupted", subject, mask(recipientEmail));
                    return false;
                }
                delay *= 2;
            }
        }
        return false;
    }

    private boolean pause(long millis) {
        try {
            Thread.sleep(millis);
            return true;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    static String mask(String email) {
        if (email == null) {
            return "unknown";
        }
        int at = email.indexOf('@');
        if (at <= 1) {
            return "***" + (at >= 0 ? email.substring(at) : "");
        }
        return email.charAt(0) + "***" + email.substring(at);
    }
}
