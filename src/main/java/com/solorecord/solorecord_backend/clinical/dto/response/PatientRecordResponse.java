package com.solorecord.solorecord_backend.clinical.dto.response;

import java.util.List;
import java.util.UUID;

public record PatientRecordResponse(
        UUID patientId,
        List<DiagnosisResponse> diagnoses,
        List<PrescriptionResponse> prescriptions,
        List<AllergyResponse> allergies,
        List<VisitNoteResponse> visitNotes
) {
}
