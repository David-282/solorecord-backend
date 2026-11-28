package com.solorecord.solorecord_backend.notification.config;

import com.solorecord.solorecord_backend.notification.sender.BrevoEmailSender;
import com.solorecord.solorecord_backend.notification.sender.LoggingSender;
import com.solorecord.solorecord_backend.notification.sender.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestClient;

@Configuration
@EnableAsync
public class NotificationConfig {

    private static final Logger log = LoggerFactory.getLogger(NotificationConfig.class);

    @Bean
    public NotificationSender notificationSender(
            @Value("${app.notification.brevo.api-key:}") String apiKey,
            @Value("${app.notification.sender-email:}") String senderEmail,
            @Value("${app.notification.sender-name:SoloRecord}") String senderName) {

        if (apiKey.isBlank() || senderEmail.isBlank()) {
            log.warn("BREVO_API_KEY or NOTIFICATION_SENDER_EMAIL not set: emails will only be logged");
            return new LoggingSender();
        }
        return new BrevoEmailSender(RestClient.create(), apiKey, senderEmail, senderName);
    }
}
