package com.solorecord.solorecord_backend.notification.sender;

/**
 * One delivery channel. Email is implemented now; an SMS sender can be added later as another
 * implementation without touching the listeners or the message-building service.
 */
public interface NotificationSender {

    /** Sends one message. Throws {@link NotificationSendException} if delivery fails. */
    void send(String recipientEmail, String recipientName, String subject, String body);
}
