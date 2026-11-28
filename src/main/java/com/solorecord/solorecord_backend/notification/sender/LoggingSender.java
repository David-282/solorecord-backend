package com.solorecord.solorecord_backend.notification.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used when no Brevo API key is configured (local development, CI). It prints the whole message,
 * including OTP codes, so a developer can complete flows without a mail account. Never used in
 * production, where BREVO_API_KEY is set.
 */
public class LoggingSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(LoggingSender.class);

    @Override
    public void send(String recipientEmail, String recipientName, String subject, String body) {
        log.info("[DEV EMAIL - no BREVO_API_KEY set] to={} subject={}\n{}", recipientEmail, subject, body);
    }
}
