package com.solorecord.solorecord_backend.notification;

import com.solorecord.solorecord_backend.events.model.AccessRequestedEvent;
import com.solorecord.solorecord_backend.events.model.OtpIssuedEvent;
import com.solorecord.solorecord_backend.events.model.TestResultCompletedEvent;
import com.solorecord.solorecord_backend.facility.service.FacilityService;
import com.solorecord.solorecord_backend.identity.dto.response.UserContactResponse;
import com.solorecord.solorecord_backend.identity.exception.UserNotFoundException;
import com.solorecord.solorecord_backend.identity.service.UserService;
import com.solorecord.solorecord_backend.notification.service.NotificationDispatcher;
import com.solorecord.solorecord_backend.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationServiceTest {

    private NotificationDispatcher dispatcher;
    private UserService userService;
    private FacilityService facilityService;
    private NotificationService service;

    @BeforeEach
    void setUp() {
        dispatcher = mock(NotificationDispatcher.class);
        userService = mock(UserService.class);
        facilityService = mock(FacilityService.class);
        service = new NotificationService(dispatcher, userService, facilityService);
    }

    @Test
    void otpEmailContainsTheCode() {
        service.onOtpIssued(OtpIssuedEvent.of("a@x.com", "123456"));

        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
        verify(dispatcher).dispatch(eq("a@x.com"), any(), any(), body.capture());
        assertTrue(body.getValue().contains("123456"));
    }

    @Test
    void accessRequestedEmailsThePatientNamingDoctorAndFacility() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        UUID facilityId = UUID.randomUUID();
        when(userService.getUserContact(patientId)).thenReturn(new UserContactResponse("Pat Ient", "pat@x.com"));
        when(userService.getUserContact(doctorId)).thenReturn(new UserContactResponse("Dr Who", "dr@x.com"));
        when(facilityService.getFacilityName(facilityId)).thenReturn("General Hospital");

        service.onAccessRequested(AccessRequestedEvent.of(UUID.randomUUID(), patientId, doctorId, facilityId));

        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
        verify(dispatcher).dispatch(eq("pat@x.com"), eq("Pat Ient"), any(), body.capture());
        assertTrue(body.getValue().contains("Dr Who"));
        assertTrue(body.getValue().contains("General Hospital"));
    }

    @Test
    void lookupFailureIsSwallowedAndNothingIsSent() {
        UUID patientId = UUID.randomUUID();
        when(userService.getUserContact(patientId)).thenThrow(new UserNotFoundException("gone"));

        service.onRecordUpdated(patientId, UUID.randomUUID(), UUID.randomUUID());

        verify(dispatcher, never()).dispatch(any(), any(), any(), any());
    }

    @Test
    void emailsNeverContainClinicalDetails() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        UUID facilityId = UUID.randomUUID();
        when(userService.getUserContact(patientId)).thenReturn(new UserContactResponse("Pat Ient", "pat@x.com"));
        when(userService.getUserContact(doctorId)).thenReturn(new UserContactResponse("Dr Who", "dr@x.com"));
        when(facilityService.getFacilityName(facilityId)).thenReturn("General Hospital");

        service.onRecordUpdated(patientId, doctorId, facilityId);

        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
        verify(dispatcher).dispatch(any(), any(), any(), body.capture());
        assertFalse(body.getValue().toLowerCase().contains("diagnos"));
        assertFalse(body.getValue().toLowerCase().contains("allerg"));
    }

    @Test
    void completedResultWithoutReviewerSendsNothing() {
        service.onTestResultCompleted(TestResultCompletedEvent.of(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "MALARIA", null));

        verify(dispatcher, never()).dispatch(any(), any(), any(), any());
    }

    @Test
    void completedResultEmailsTheReviewingDoctor() {
        UUID doctorId = UUID.randomUUID();
        when(userService.getUserContact(doctorId)).thenReturn(new UserContactResponse("Dr Who", "dr@x.com"));

        service.onTestResultCompleted(TestResultCompletedEvent.of(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "MALARIA", doctorId));

        verify(dispatcher).dispatch(eq("dr@x.com"), eq("Dr Who"), any(), any());
    }
}
