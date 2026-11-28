package com.solorecord.solorecord_backend.notification.sender;

import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

/** Sends transactional email through Brevo's REST API. */
public class BrevoEmailSender implements NotificationSender {

    private static final String ENDPOINT = "https://api.brevo.com/v3/smtp/email";

    private final RestClient restClient;
    private final String apiKey;
    private final String senderEmail;
    private final String senderName;

    public BrevoEmailSender(RestClient restClient, String apiKey, String senderEmail, String senderName) {
        this.restClient = restClient;
        this.apiKey = apiKey;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
    }

    @Override
    public void send(String recipientEmail, String recipientName, String subject, String body) {
        Map<String, Object> payload = Map.of(
                "sender", Map.of("email", senderEmail, "name", senderName),
                "to", List.of(Map.of("email", recipientEmail, "name", recipientName == null ? recipientEmail : recipientName)),
                "subject", subject,
                "textContent", body);

        try {
            restClient.post()
                    .uri(ENDPOINT)
                    .header("api-key", apiKey)
                    .header("accept", "application/json")
                    .header("content-type", "application/json")
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            // Message only: the exception text can echo request details.
            throw new NotificationSendException("Brevo request failed: " + ex.getClass().getSimpleName(), ex);
        }
    }
}
