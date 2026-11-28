package com.solorecord.solorecord_backend.notification.service;

import com.solorecord.solorecord_backend.events.model.*;
import com.solorecord.solorecord_backend.facility.dto.response.AdminContactResponse;
import com.solorecord.solorecord_backend.facility.service.FacilityService;
import com.solorecord.solorecord_backend.identity.dto.response.UserContactResponse;
import com.solorecord.solorecord_backend.identity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Builds the email for each event and hands it to the dispatcher. Emails never contain clinical
 * details (no conditions, results or medications): they say that something happened and point
 * the reader back to the app. A lookup failure is logged and swallowed so it cannot affect the
 * action that raised the event.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationDispatcher dispatcher;
    private final UserService userService;
    private final FacilityService facilityService;

    public void onOtpIssued(OtpIssuedEvent event) {
        guarded("OTP", () -> dispatcher.dispatch(event.email(), null,
                "Your SoloRecord verification code",
                "Your verification code is " + event.otp() + ".\n\n"
                        + "It expires in 10 minutes. If you did not request it, ignore this email."));
    }

    public void onStaffOnboarded(StaffOnboardedEvent event) {
        guarded("staff onboarded", () -> dispatcher.dispatch(event.email(), event.fullName(),
                "Your SoloRecord account is ready to activate",
                "Hello " + event.fullName() + ",\n\n"
                        + event.facilityName() + " has added you to SoloRecord.\n"
                        + "Open SoloRecord, choose account activation, enter this email address to receive a "
                        + "code, and set your password."));
    }

    public void onFacilityApproved(FacilityApprovedEvent event) {
        guarded("facility approved", () -> {
            AdminContactResponse admin = facilityService.getAdminContact(event.facilityId());
            String facility = facilityService.getFacilityName(event.facilityId());
            dispatcher.dispatch(admin.adminEmail(), admin.adminFullName(),
                    "Your facility has been approved on SoloRecord",
                    "Hello " + admin.adminFullName() + ",\n\n"
                            + facility + " has been approved. Open SoloRecord, choose account activation, "
                            + "enter this email address to receive a code, and set your password.");
        });
    }

    public void onFacilityRejected(FacilityRejectedEvent event) {
        guarded("facility rejected", () -> dispatcher.dispatch(event.adminEmail(), event.adminFullName(),
                "Update on your SoloRecord facility application",
                "Hello " + event.adminFullName() + ",\n\n"
                        + "Your application for " + event.facilityName() + " was not approved.\n\n"
                        + "Reason: " + event.rejectionReason() + "\n\n"
                        + "You are welcome to apply again."));
    }

    public void onAccessRequested(AccessRequestedEvent event) {
        guarded("access requested", () -> {
            UserContactResponse patient = userService.getUserContact(event.patientId());
            UserContactResponse doctor = userService.getUserContact(event.doctorId());
            String facility = facilityService.getFacilityName(event.facilityId());
            dispatcher.dispatch(patient.email(), patient.fullName(),
                    "A doctor has requested access to your record",
                    "Hello " + patient.fullName() + ",\n\n"
                            + doctor.fullName() + " at " + facility + " has requested access to your medical "
                            + "record. Open SoloRecord to approve or deny the request.");
        });
    }

    public void onAccessApproved(AccessApprovedEvent event) {
        notifyDoctor("access approved", event.doctorId(), event.patientId(),
                "Access to a patient record was approved",
                "approved your request for access to their record. You can now view it in SoloRecord.");
    }

    public void onAccessDenied(AccessDeniedEvent event) {
        notifyDoctor("access denied", event.doctorId(), event.patientId(),
                "Access to a patient record was declined",
                "declined your request for access to their record.");
    }

    public void onAccessRevoked(AccessRevokedEvent event) {
        notifyDoctor("access revoked", event.doctorId(), event.patientId(),
                "Access to a patient record was revoked",
                "revoked your access to their record. You can no longer view or add to it.");
    }

    public void onRecordUpdated(UUID patientId, UUID doctorId, UUID facilityId) {
        guarded("record updated", () -> {
            UserContactResponse patient = userService.getUserContact(patientId);
            UserContactResponse doctor = userService.getUserContact(doctorId);
            String facility = facilityService.getFacilityName(facilityId);
            dispatcher.dispatch(patient.email(), patient.fullName(),
                    "Your medical record was updated",
                    "Hello " + patient.fullName() + ",\n\n"
                            + doctor.fullName() + " at " + facility + " added to your medical record. "
                            + "Open SoloRecord to view it.");
        });
    }

    public void onTestResultCompleted(TestResultCompletedEvent event) {
        // Lab-initiated results have no reviewing doctor yet; they are announced when routed.
        if (event.reviewingDoctorId() == null) {
            return;
        }
        notifyResultReady("result ready", event.reviewingDoctorId());
    }

    public void onTestResultAssigned(TestResultAssignedEvent event) {
        notifyResultReady("result assigned", event.doctorId());
    }

    private void notifyResultReady(String label, UUID doctorId) {
        guarded(label, () -> {
            UserContactResponse doctor = userService.getUserContact(doctorId);
            dispatcher.dispatch(doctor.email(), doctor.fullName(),
                    "A test result is ready for review",
                    "Hello " + doctor.fullName() + ",\n\n"
                            + "A lab result is ready for your review. Open SoloRecord to view it.");
        });
    }

    private void notifyDoctor(String label, UUID doctorId, UUID patientId, String subject, String action) {
        guarded(label, () -> {
            UserContactResponse doctor = userService.getUserContact(doctorId);
            UserContactResponse patient = userService.getUserContact(patientId);
            dispatcher.dispatch(doctor.email(), doctor.fullName(), subject,
                    "Hello " + doctor.fullName() + ",\n\n" + patient.fullName() + " " + action);
        });
    }

    private void guarded(String label, Runnable work) {
        try {
            work.run();
        } catch (RuntimeException ex) {
            log.error("Could not prepare '{}' notification: {}", label, ex.getMessage());
        }
    }
}
