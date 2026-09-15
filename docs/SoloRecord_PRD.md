# SoloRecord — Product Requirements Document

*"One Patient. One Medical Record."*

## 1. Problem Statement

Patient medical records are not centralized. When a patient relocates or switches facilities, their medical history doesn't follow them — so new providers repeat tests that have already been done, wasting time, money, and resources. This mainly hurts patients, who bear higher out-of-pocket costs and delayed treatment, but it also burdens doctors, who must work with incomplete history and redo diagnostic work that already exists elsewhere.

## 2. Goals / Objectives

1. Enable easy, centralized transfer of patient records across facilities — reducing repeated tests and cost to the patient.
2. Give doctors fast access to a patient's accurate medical history, so treatment decisions aren't delayed or based on incomplete information.
3. Detect and help prevent disease outbreaks early by tracking patterns in test results across facilities.

## 3. Users / Personas

| Role | Description |
|---|---|
| **Patient** | Owns their record; must approve access per facility (per-facility consent) before a new facility can view their history. |
| **Doctor / Clinician** | Views patient history (once access granted), enters diagnoses, prescriptions, allergies, and vitals/visit notes. |
| **Lab Technician** | Enters test results, separate from the doctor's clinical entries. |
| **Facility / Hospital Admin** | Manages facility-level staff accounts and roles; sees in-app outbreak alerts and manually escalates to the necessary health body. |
| **Super Admin** | Platform-level role; onboards new facilities onto SoloRecord after verifying their credentials/license; manages facility accounts. |
| **System (automated)** | Detects outbreak thresholds/clusters from completed test results and raises in-app alerts; maintains the audit trail. |

**Patient consent model:** Per-facility consent. A patient must explicitly approve each new facility's request to access their record. Approval is verified via OTP or in-app confirmation, which doubles as identity verification — confirming the person granting access is genuinely the patient in question.

## 4. Core Features (MVP)

### Patient
1. Registration / profile creation
2. View own medical record (diagnoses, test results, prescriptions, allergies, visit notes)
3. Grant / revoke facility access requests (per-facility consent, OTP/in-app verified)
4. Receive notifications (access requests, record updates)
5. View own audit trail (which facility/doctor accessed their record, and when)

### Doctor
6. Search / look up patient (by ID, national ID, or phone number)
7. Request access to a patient's record at a new facility
8. View patient record (once access granted)
9. Enter / update diagnoses
10. Enter / update prescriptions (medication, dosage, duration)
11. Record allergies
12. Record vitals / visit notes

### Lab Technician
13. Enter test results (linked to patient + facility)
14. Mark test results as "completed" (this triggers outbreak detection)

### Facility Admin
15. Manage facility staff accounts / roles
16. View in-app outbreak alerts

### Super Admin
17. Onboard new facilities (with credential/license verification)
18. Manage facility accounts (activate / deactivate)

### System (automated)
19. Outbreak detection engine (threshold/cluster count within a time window and geography, based on completed test results)
20. Audit trail / logging engine

### Cross-cutting
21. Authentication / login (all roles)
22. Record export / print (e.g. for referrals)

## 5. Out of Scope

1. Emergency response / dispatch functionality
2. Lagos-specific geography — system is general-purpose
3. Public health official dashboard/role — alerts stop at Facility Admin
4. Appointment booking / scheduling
5. Multi-facility record-existence lookup for doctors
6. Automatic external notification (SMS/email) to health bodies — escalation is manual
7. Billing / payment processing

## 6. Success Metrics

All metrics are functional/demo-based, appropriate for a capstone evaluation rather than live production usage data.

1. Record access is granted and viewable promptly once consent is approved.
2. System surfaces existing test results/history to prevent duplicate testing (demonstrable functionally).
3. Outbreak detection engine produces the expected alert / no-alert outcome across defined test scenarios (rule-based for MVP; noted below as a candidate for future ML-based detection).
4. Role-based access is strictly enforced — no user can perform actions outside their role's permissions.
5. Audit trail is complete — every record access/action is logged and retrievable.
6. Record view across approved facilities is correctly consolidated — no duplication or missing data.
7. Notifications are reliably delivered for access requests and record updates.

## 7. Future Enhancements (Noted, Not In Scope for MVP)

- ML-based outbreak detection, replacing or augmenting the rule-based threshold engine to reduce false positives/negatives.
