package com.solorecord.solorecord_backend.notification.listener;

import com.solorecord.solorecord_backend.events.model.*;
import com.solorecord.solorecord_backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Runs after the publishing transaction commits (so a rolled-back action sends nothing) and on a
 * separate thread (so a slow or failing mail provider never blocks or breaks the action).
 * fallbackExecution lets events published outside a transaction, such as an activation OTP
 * request, still be delivered.
 */
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(OtpIssuedEvent event) {
        notificationService.onOtpIssued(event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(StaffOnboardedEvent event) {
        notificationService.onStaffOnboarded(event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(FacilityApprovedEvent event) {
        notificationService.onFacilityApproved(event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(FacilityRejectedEvent event) {
        notificationService.onFacilityRejected(event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(AccessRequestedEvent event) {
        notificationService.onAccessRequested(event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(AccessApprovedEvent event) {
        notificationService.onAccessApproved(event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(AccessDeniedEvent event) {
        notificationService.onAccessDenied(event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(AccessRevokedEvent event) {
        notificationService.onAccessRevoked(event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(DiagnosisRecordedEvent event) {
        notificationService.onRecordUpdated(event.patientId(), event.doctorId(), event.facilityId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(PrescriptionRecordedEvent event) {
        notificationService.onRecordUpdated(event.patientId(), event.doctorId(), event.facilityId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(AllergyRecordedEvent event) {
        notificationService.onRecordUpdated(event.patientId(), event.doctorId(), event.facilityId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(VisitNoteRecordedEvent event) {
        notificationService.onRecordUpdated(event.patientId(), event.doctorId(), event.facilityId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(TestResultCompletedEvent event) {
        notificationService.onTestResultCompleted(event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(TestResultAssignedEvent event) {
        notificationService.onTestResultAssigned(event);
    }
}
