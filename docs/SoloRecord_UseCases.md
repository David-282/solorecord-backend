# SoloRecord — Use Cases

## Actor: Patient

### Use Case: Grant Facility Access

- **Actor:** Patient
- **Trigger:** A doctor at a facility requests access to the patient's record
- **Precondition:** Patient is registered on SoloRecord; doctor has searched and found the patient

**Main Flow:**
1. Doctor requests access to patient's record
2. System sends a notification to the patient, showing the requesting facility and doctor
3. Patient reviews the request
4. Patient approves the request (via OTP/in-app confirmation)
5. System grants the doctor access to the patient's record at that facility
6. System logs the access grant in the audit trail

**Alternate Flow — Patient Denies:**
1. Patient denies the request
2. System notifies the doctor that access was declined
3. No access granted; system logs the denial in the audit trail

### Use Case: Revoke Facility Access

- **Actor:** Patient
- **Trigger:** Patient decides to remove a facility's access to their record
- **Precondition:** Facility currently has active, granted access to the patient's record

**Main Flow:**
1. Patient opens their list of facilities with active access
2. Patient selects a facility and chooses to revoke access
3. System confirms the action with the patient
4. Patient confirms
5. System removes the facility/doctor's access to the record
6. System logs the revocation in the audit trail
7. System notifies the affected doctor/facility that access has been revoked

**Alternate Flow — Patient Cancels:**
1. Patient cancels the confirmation step
2. No change is made; access remains active

## Actor: Doctor

### Use Case: Request Patient Access

- **Actor:** Doctor
- **Trigger:** Doctor needs to view a patient's medical history but doesn't currently have access
- **Precondition:** Doctor is logged in; patient exists in the system

**Main Flow:**
1. Doctor searches for the patient (by ID, national ID, or phone number)
2. System returns matching patient record(s)
3. Doctor selects the correct patient and requests access
4. System sends the request to the patient (triggers "Grant Facility Access")
5. Doctor waits for patient approval

**Alternate Flow — Access Approved:**
1. Patient approves → doctor gains access, notified they can now view the record

**Alternate Flow — Access Denied:**
1. Patient denies → doctor is notified access was declined, cannot view record

**Alternate Flow — No Match Found:**
1. Search returns no matching patient → doctor is informed no record exists, can proceed to register a new patient if appropriate

### Use Case: Enter Diagnosis (also applies to Prescription, Allergy, and Vitals/Visit Notes entries)

- **Actor:** Doctor
- **Trigger:** Doctor needs to record a diagnosis, either from clinical judgment or based on a completed test result
- **Precondition:** Doctor has active, granted access to the patient's record

**Main Flow:**
1. Doctor opens the patient's record
2. Doctor selects "Add Diagnosis"
3. Doctor enters diagnosis details (condition, notes, date)
4. (Optional) Doctor links the diagnosis to a specific completed test result, if one exists
5. Doctor saves the entry
6. System adds the diagnosis to the patient's record
7. System logs the entry in the audit trail
8. System notifies the patient that their record was updated

**Alternate Flow — Access Expired/Revoked Mid-Session:**
1. Doctor's access was revoked before saving → system blocks the save, doctor is notified access is no longer valid

### Use Case: View Patient Record

- **Actor:** Doctor
- **Trigger:** Doctor wants to review a patient's history
- **Precondition:** Doctor has active, granted access

**Main Flow:**
1. Doctor opens the patient's record
2. System displays diagnoses, test results, prescriptions, allergies, and visit notes

## Actor: Lab Technician

### Use Case: Enter Test Result

- **Actor:** Lab Technician
- **Trigger:** Either (a) fulfilling a doctor's test order, or (b) lab tech independently initiates a test (e.g. walk-in/routine screening)
- **Precondition:** Lab tech is logged in and associated with a facility

**Main Flow (Order-based):**
1. Doctor creates a test order (test type, patient, facility, notes)
2. Lab technician sees the pending order in their queue
3. Lab technician performs the test and enters the result
4. Lab technician marks the result as "completed"
5. System links the result to the original order
6. System triggers an outbreak detection check
7. Doctor (who ordered it) is notified the result is ready

**Alternate Flow — Lab-Tech-Initiated (no order):**
1. Lab technician selects/searches for the patient directly (no prior order)
2. Lab technician enters the test details and result
3. Lab technician marks the result as "completed"
4. System triggers an outbreak detection check
5. System notifies the Facility Admin (since there's no ordering doctor)
6. Facility Admin assigns/routes the result to a doctor for review
7. Assigned doctor is notified and reviews the result

## Actor: Facility Admin

### Use Case: View Outbreak Alert & Escalate

- **Actor:** Facility Admin (primary), Super Admin (review step)
- **Trigger:** System's outbreak detection engine flags a threshold/cluster match
- **Precondition:** Outbreak detection engine has run and found a match

**Main Flow:**
1. System detects a cluster of completed test results matching outbreak criteria
2. System generates an in-app outbreak alert (condition, case count, time window, geography — no patient names)
3. System notifies the Facility Admin(s) of affected facilities
4. Facility Admin opens and reviews the alert
5. Facility Admin manually escalates to the relevant health body outside the system
6. Facility Admin marks the alert as "escalated"
7. System logs the escalation in the audit trail

**Alternate Flow — Dismissed as False Positive:**
1. Facility Admin determines it's not a genuine outbreak
2. Facility Admin marks the alert as "dismissed" with a reason/note
3. Dismissal is routed to Super Admin for review
4. Super Admin either confirms the dismissal or overrides it (reopens the alert)
5. System logs the review decision in the audit trail

### Use Case: Manage Facility Staff

- **Actor:** Facility Admin
- **Trigger:** Admin needs to add, remove, or update a staff member's account (Doctor or Lab Technician)

**Main Flow:**
1. Facility Admin opens facility staff management panel
2. Admin selects "Add Staff"
3. Admin enters staff details (name, role, contact info)
4. System creates the account and sends login credentials to the staff member
5. System logs the action in the audit trail

**Alternate Flow — Deactivate Staff:**
1. Admin selects an existing staff member
2. Admin chooses "Deactivate"
3. System revokes that staff member's login access immediately
4. System logs the deactivation

## Actor: Super Admin

### Use Case: Onboard Facility

- **Actor:** Super Admin
- **Trigger:** A new facility applies to join SoloRecord

**Main Flow:**
1. Facility submits an onboarding request (facility name, address, credentials/license info)
2. Super Admin reviews the submitted credentials/license
3. Super Admin verifies legitimacy (manual check, outside system scope)
4. Super Admin approves the facility
5. System creates the facility's account and a default Facility Admin login
6. System logs the onboarding in the audit trail

**Alternate Flow — Rejected:**
1. Super Admin cannot verify credentials or finds them invalid
2. Super Admin rejects the request, with a reason
3. Facility is notified of rejection
