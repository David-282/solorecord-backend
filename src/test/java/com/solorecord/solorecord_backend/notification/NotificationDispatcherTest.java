package com.solorecord.solorecord_backend.notification;

import com.solorecord.solorecord_backend.notification.sender.NotificationSendException;
import com.solorecord.solorecord_backend.notification.sender.NotificationSender;
import com.solorecord.solorecord_backend.notification.service.NotificationDispatcher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class NotificationDispatcherTest {

    private final NotificationSender sender = mock(NotificationSender.class);

    private NotificationDispatcher dispatcher(int attempts) {
        return new NotificationDispatcher(sender, attempts, 1);
    }

    @Test
    void sendsOnceWhenFirstAttemptWorks() {
        assertTrue(dispatcher(3).dispatch("a@x.com", "A", "Subject", "Body"));

        verify(sender, times(1)).send("a@x.com", "A", "Subject", "Body");
    }

    @Test
    void retriesAfterAFailureAndSucceeds() {
        doThrow(new NotificationSendException("boom"))
                .doNothing()
                .when(sender).send(anyString(), any(), anyString(), anyString());

        assertTrue(dispatcher(3).dispatch("a@x.com", "A", "Subject", "Body"));

        verify(sender, times(2)).send(eq("a@x.com"), any(), anyString(), anyString());
    }

    @Test
    void givesUpAfterMaxAttemptsWithoutThrowing() {
        doThrow(new NotificationSendException("boom"))
                .when(sender).send(anyString(), any(), anyString(), anyString());

        assertFalse(dispatcher(3).dispatch("a@x.com", "A", "Subject", "Body"));

        verify(sender, times(3)).send(eq("a@x.com"), any(), anyString(), anyString());
    }

    @Test
    void atLeastOneAttemptEvenIfConfiguredAsZero() {
        doNothing().when(sender).send(anyString(), any(), anyString(), anyString());

        assertTrue(dispatcher(0).dispatch("a@x.com", "A", "Subject", "Body"));

        verify(sender, times(1)).send(eq("a@x.com"), any(), anyString(), anyString());
    }
}
