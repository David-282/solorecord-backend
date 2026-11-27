package com.solorecord.solorecord_backend.clinical.service;

import com.solorecord.solorecord_backend.clinical.dto.request.AllergyRequest;
import com.solorecord.solorecord_backend.clinical.dto.request.DiagnosisRequest;
import com.solorecord.solorecord_backend.clinical.dto.request.PrescriptionRequest;
import com.solorecord.solorecord_backend.clinical.dto.request.VisitNoteRequest;
import com.solorecord.solorecord_backend.clinical.dto.response.AllergyResponse;
import com.solorecord.solorecord_backend.clinical.dto.response.DiagnosisResponse;
import com.solorecord.solorecord_backend.clinical.dto.response.PatientRecordResponse;
import com.solorecord.solorecord_backend.clinical.dto.response.PrescriptionResponse;
import com.solorecord.solorecord_backend.clinical.dto.response.VisitNoteResponse;

import java.util.UUID;

/**
 * Doctor and facility always come from the authenticated principal, never from a request body.
 * Every doctor-side call requires an active (GRANTED) access grant for that patient at that facility.
 */
public interface ClinicalService {

    DiagnosisResponse addDiagnosis(UUID doctorId, UUID facilityId, UUID patientId, DiagnosisRequest request);

    PrescriptionResponse addPrescription(UUID doctorId, UUID facilityId, UUID patientId, PrescriptionRequest request);

    AllergyResponse addAllergy(UUID doctorId, UUID facilityId, UUID patientId, AllergyRequest request);

    VisitNoteResponse addVisitNote(UUID doctorId, UUID facilityId, UUID patientId, VisitNoteRequest request);

    PatientRecordResponse getRecordForDoctor(UUID doctorId, UUID facilityId, UUID patientId);

    PatientRecordResponse getOwnRecord(UUID patientId);
}
