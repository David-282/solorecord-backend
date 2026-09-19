# SoloRecord — Class Diagram

Architecture: modular monolith, organized into 8 modules — identity, patient, facility, clinical, lab, outbreak, audit, notification.

## Module: identity

### Class: User
| Field | Type |
|---|---|
| id | UUID |
| fullName | String |
| email | String |
| phone | String |
| passwordHash | String |
| role | Role (enum) |
| facilityId | UUID (nullable — set for Doctor/LabTech/FacilityAdmin, null for Patient/SuperAdmin) |
| status | AccountStatus (enum) |
| createdAt | DateTime |

**Methods:** `authenticate(password)`, `deactivate()`, `activate()`

**Enum — Role:** PATIENT, DOCTOR, LAB_TECHNICIAN, FACILITY_ADMIN, SUPER_ADMIN
**Enum — AccountStatus:** ACTIVE, DEACTIVATED

Other modules reference `User` by `userId`/`doctorId`/`labTechId` — no inheritance, no direct object composition.

## Module: patient

### Class: Patient
| Field | Type |
|---|---|
| id | UUID |
| userId | UUID (→ User) |
| dateOfBirth | Date |
| gender | String |
| address | String |
| nationalId | String |

### Class: AccessGrant
| Field | Type |
|---|---|
| id | UUID |
| patientId | UUID (→ Patient) |
| doctorId | UUID (→ User) |
| facilityId | UUID (→ Facility) |
| status | AccessStatus (enum) |
| requestedAt | DateTime |
| respondedAt | DateTime (nullable) |
| revokedAt | DateTime (nullable) |

**Methods:** `grant()`, `deny()`, `revoke()`, `isActive()`

**Enum — AccessStatus:** PENDING, GRANTED, DENIED, REVOKED

**Relationships:** `User (1) → Patient (0..1)` via userId · `Patient (1) → AccessGrant (many)` · `AccessGrant → User (doctor)` and `AccessGrant → Facility` by ID.

## Module: facility

### Class: Facility
| Field | Type |
|---|---|
| id | UUID |
| name | String |
| address | String |
| licenseNumber | String |
| adminFullName | String |
| adminEmail | String |
| adminPhone | String |
| status | FacilityStatus (enum) |
| submittedAt | DateTime |
| reviewedAt | DateTime (nullable) |
| reviewedBy | UUID (nullable, → User) |
| rejectionReason | String (nullable) |

**Methods:** `approve(superAdminId)` — also triggers `identity` to create the initial Facility Admin account · `reject(superAdminId, reason)`

**Enum — FacilityStatus:** PENDING, APPROVED, REJECTED

**Supporting service:** `FacilityStaffService` — `addStaff()`, `deactivateStaff()`, `listStaff()` (delegates account creation/deactivation to `identity`)

**Relationships:** `Facility (1) → User (many)` via facilityId FK (staff not owned/nested inside Facility — lazy-loaded reference, not eager collection).

## Module: clinical

### Class: Diagnosis
| Field | Type |
|---|---|
| id | UUID |
| patientId | UUID |
| doctorId | UUID |
| facilityId | UUID |
| condition | String |
| notes | String |
| diagnosedAt | DateTime |
| testResultId | UUID (nullable — optional link to lab module) |

### Class: Prescription
| Field | Type |
|---|---|
| id | UUID |
| patientId | UUID |
| doctorId | UUID |
| facilityId | UUID |
| medication | String |
| dosage | String |
| duration | String |
| prescribedAt | DateTime |
| diagnosisId | UUID (nullable) |

### Class: Allergy
| Field | Type |
|---|---|
| id | UUID |
| patientId | UUID |
| doctorId | UUID |
| allergen | String |
| severity | String |
| recordedAt | DateTime |

### Class: VisitNote
| Field | Type |
|---|---|
| id | UUID |
| patientId | UUID |
| doctorId | UUID |
| facilityId | UUID |
| bloodPressure | String (nullable) |
| temperature | Double (nullable) |
| heartRate | Integer (nullable) |
| respiratoryRate | Integer (nullable) |
| oxygenSaturation | Double (nullable) |
| weight | Double (nullable) |
| otherVitals | String (nullable) |
| notes | String |
| visitedAt | DateTime |

**Relationships:** `Diagnosis (0..1) → TestResult` via testResultId (lab module) · `Prescription (many) → Diagnosis (0..1)` via diagnosisId.

## Module: lab

### Class: TestOrder
| Field | Type |
|---|---|
| id | UUID |
| patientId | UUID |
| doctorId | UUID |
| facilityId | UUID |
| testType | String |
| notes | String (nullable) |
| status | OrderStatus (enum) |
| orderedAt | DateTime |

**Enum — OrderStatus:** PENDING, FULFILLED

### Class: TestResult
| Field | Type |
|---|---|
| id | UUID |
| patientId | UUID |
| labTechId | UUID |
| facilityId | UUID |
| testOrderId | UUID (nullable — null for lab-initiated tests) |
| testType | String (always populated) |
| resultValue | String |
| status | ResultStatus (enum) |
| enteredAt | DateTime |
| completedAt | DateTime (nullable) |
| reviewingDoctorId | UUID (nullable) |

**Methods:** `markCompleted()` — triggers outbreak check + notifies reviewing doctor or Facility Admin

**Enum — ResultStatus:** PENDING, COMPLETED

**Relationships:** `TestOrder (1) → TestResult (0..1)`.

## Module: outbreak

### Class: OutbreakThresholdRule
| Field | Type |
|---|---|
| id | UUID |
| condition | String |
| minCaseCount | Integer |
| timeWindowDays | Integer |
| geographyScope | String |

One rule per condition (not per geography) — MVP scope.

### Class: OutbreakAlert
| Field | Type |
|---|---|
| id | UUID |
| condition | String |
| caseCount | Integer |
| geography | String |
| windowStart | DateTime |
| windowEnd | DateTime |
| status | AlertStatus (enum) |
| detectedAt | DateTime |
| escalatedBy | UUID (nullable) |
| escalatedAt | DateTime (nullable) |
| dismissalReason | String (nullable) |
| reviewedBySuperAdmin | UUID (nullable) |
| reviewDecision | String (nullable) |

No direct references to individual patients/test results are stored — aggregate data only, to withhold patient identity.

**Enum — AlertStatus:** OPEN, ESCALATED, DISMISSED, REOPENED

### Class: OutbreakDetectionService (non-persisted engine)
**Methods:** `evaluateNewResult(testResult)`, `checkThreshold(condition, geography, window)`, `createAlert()`

## Module: audit

### Class: AuditLog
| Field | Type |
|---|---|
| id | UUID |
| actorUserId | UUID |
| action | String |
| entityType | String |
| entityId | UUID |
| patientId | UUID (nullable) |
| timestamp | DateTime |
| details | String (nullable) |

**Supporting service:** `AuditLogService.log(...)` — called by every module after any state-changing action. Single generic table, not one log class per module.

## Module: notification

### Class: Notification
| Field | Type |
|---|---|
| id | UUID |
| recipientUserId | UUID |
| type | NotificationType (enum) |
| message | String |
| relatedEntityId | UUID (nullable) |
| isRead | Boolean |
| createdAt | DateTime |

**Enum — NotificationType:** ACCESS_REQUEST, ACCESS_GRANTED, ACCESS_DENIED, RECORD_UPDATED, RESULT_READY, OUTBREAK_ALERT, ACCOUNT_CREATED

**Supporting service:** `NotificationService` — `send(...)`, `markAsRead(...)`. In-app only for MVP.

## Cross-Cutting Notes

`AuditLog` and `Notification` are written to by every module (patient, clinical, lab, facility, outbreak) whenever a state-changing action occurs — not just from the outbreak module. These connections are omitted from the per-module diagrams above to avoid clutter, but every use case that ends in "system logs..." or "system notifies..." routes through these two services.
