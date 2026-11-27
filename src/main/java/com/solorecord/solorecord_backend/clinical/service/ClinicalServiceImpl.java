package com.solorecord.solorecord_backend.clinical.service;

import com.solorecord.solorecord_backend.clinical.data.model.Allergy;
import com.solorecord.solorecord_backend.clinical.data.model.Diagnosis;
import com.solorecord.solorecord_backend.clinical.data.model.Prescription;
import com.solorecord.solorecord_backend.clinical.data.model.VisitNote;
import com.solorecord.solorecord_backend.clinical.data.repository.AllergyRepository;
import com.solorecord.solorecord_backend.clinical.data.repository.DiagnosisRepository;
import com.solorecord.solorecord_backend.clinical.data.repository.PrescriptionRepository;
import com.solorecord.solorecord_backend.clinical.data.repository.VisitNoteRepository;
import com.solorecord.solorecord_backend.clinical.dto.request.AllergyRequest;
import com.solorecord.solorecord_backend.clinical.dto.request.DiagnosisRequest;
import com.solorecord.solorecord_backend.clinical.dto.request.PrescriptionRequest;
import com.solorecord.solorecord_backend.clinical.dto.request.VisitNoteRequest;
import com.solorecord.solorecord_backend.clinical.dto.response.AllergyResponse;
import com.solorecord.solorecord_backend.clinical.dto.response.DiagnosisResponse;
import com.solorecord.solorecord_backend.clinical.dto.response.PatientRecordResponse;
import com.solorecord.solorecord_backend.clinical.dto.response.PrescriptionResponse;
import com.solorecord.solorecord_backend.clinical.dto.response.VisitNoteResponse;
import com.solorecord.solorecord_backend.clinical.exception.AccessNotGrantedException;
import com.solorecord.solorecord_backend.clinical.exception.InvalidDiagnosisLinkException;
import com.solorecord.solorecord_backend.events.model.AllergyRecordedEvent;
import com.solorecord.solorecord_backend.events.model.DiagnosisRecordedEvent;
import com.solorecord.solorecord_backend.events.model.PrescriptionRecordedEvent;
import com.solorecord.solorecord_backend.events.model.VisitNoteRecordedEvent;
import com.solorecord.solorecord_backend.events.publisher.EventPublisher;
import com.solorecord.solorecord_backend.lab.service.LabService;
import com.solorecord.solorecord_backend.patient.service.AccessGrantService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ClinicalServiceImpl implements ClinicalService {

    private final DiagnosisRepository diagnosisRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final AllergyRepository allergyRepository;
    private final VisitNoteRepository visitNoteRepository;
    private final AccessGrantService accessGrantService;
    private final LabService labService;
    private final EventPublisher eventPublisher;

    @Override
    @Transactional
    public DiagnosisResponse addDiagnosis(UUID doctorId, UUID facilityId, UUID patientId, DiagnosisRequest request) {
        requireAccess(doctorId, facilityId, patientId);

        if (request.testResultId() != null
                && !labService.isCompletedResultForPatient(request.testResultId(), patientId)) {
            throw new InvalidDiagnosisLinkException("Test result is not a completed result for this patient");
        }

        Diagnosis saved = diagnosisRepository.save(new Diagnosis(
                patientId, doctorId, facilityId,
                request.condition().strip(), request.notes(), request.testResultId()));

        eventPublisher.publish(DiagnosisRecordedEvent.of(saved.getId(), patientId, doctorId, facilityId));
        return toResponse(saved);
    }

    @Override
    @Transactional
    public PrescriptionResponse addPrescription(UUID doctorId, UUID facilityId, UUID patientId, PrescriptionRequest request) {
        requireAccess(doctorId, facilityId, patientId);

        if (request.diagnosisId() != null
                && !diagnosisRepository.existsByIdAndPatientId(request.diagnosisId(), patientId)) {
            throw new InvalidDiagnosisLinkException("Diagnosis does not belong to this patient");
        }

        Prescription saved = prescriptionRepository.save(new Prescription(
                patientId, doctorId, facilityId,
                request.medication().strip(), request.dosage().strip(), request.duration().strip(),
                request.diagnosisId()));

        eventPublisher.publish(PrescriptionRecordedEvent.of(saved.getId(), patientId, doctorId, facilityId));
        return toResponse(saved);
    }

    @Override
    @Transactional
    public AllergyResponse addAllergy(UUID doctorId, UUID facilityId, UUID patientId, AllergyRequest request) {
        requireAccess(doctorId, facilityId, patientId);

        Allergy saved = allergyRepository.save(new Allergy(
                patientId, doctorId, facilityId, request.allergen().strip(), request.severity().strip()));

        eventPublisher.publish(AllergyRecordedEvent.of(saved.getId(), patientId, doctorId, facilityId));
        return toResponse(saved);
    }

    @Override
    @Transactional
    public VisitNoteResponse addVisitNote(UUID doctorId, UUID facilityId, UUID patientId, VisitNoteRequest request) {
        requireAccess(doctorId, facilityId, patientId);

        VisitNote saved = visitNoteRepository.save(new VisitNote(
                patientId, doctorId, facilityId,
                request.bloodPressure(), request.temperature(), request.heartRate(),
                request.respiratoryRate(), request.oxygenSaturation(), request.weight(),
                request.otherVitals(), request.notes()));

        eventPublisher.publish(VisitNoteRecordedEvent.of(saved.getId(), patientId, doctorId, facilityId));
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientRecordResponse getRecordForDoctor(UUID doctorId, UUID facilityId, UUID patientId) {
        requireAccess(doctorId, facilityId, patientId);
        return buildRecord(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientRecordResponse getOwnRecord(UUID patientId) {
        return buildRecord(patientId);
    }

    /**
     * One answer for "no grant", "revoked" and "patient does not exist", so a doctor cannot
     * probe which patient IDs are real. (A grant can only exist for a real patient.)
     */
    private void requireAccess(UUID doctorId, UUID facilityId, UUID patientId) {
        if (!accessGrantService.hasActiveAccess(doctorId, facilityId, patientId)) {
            throw new AccessNotGrantedException("You do not have active access to this patient's record");
        }
    }

    private PatientRecordResponse buildRecord(UUID patientId) {
        return new PatientRecordResponse(
                patientId,
                diagnosisRepository.findByPatientIdOrderByDiagnosedAtDesc(patientId).stream().map(this::toResponse).toList(),
                prescriptionRepository.findByPatientIdOrderByPrescribedAtDesc(patientId).stream().map(this::toResponse).toList(),
                allergyRepository.findByPatientIdOrderByRecordedAtDesc(patientId).stream().map(this::toResponse).toList(),
                visitNoteRepository.findByPatientIdOrderByVisitedAtDesc(patientId).stream().map(this::toResponse).toList());
    }

    private DiagnosisResponse toResponse(Diagnosis d) {
        return new DiagnosisResponse(d.getId(), d.getPatientId(), d.getDoctorId(), d.getFacilityId(),
                d.getCondition(), d.getNotes(), d.getTestResultId(), d.getDiagnosedAt());
    }

    private PrescriptionResponse toResponse(Prescription p) {
        return new PrescriptionResponse(p.getId(), p.getPatientId(), p.getDoctorId(), p.getFacilityId(),
                p.getMedication(), p.getDosage(), p.getDuration(), p.getDiagnosisId(), p.getPrescribedAt());
    }

    private AllergyResponse toResponse(Allergy a) {
        return new AllergyResponse(a.getId(), a.getPatientId(), a.getDoctorId(), a.getFacilityId(),
                a.getAllergen(), a.getSeverity(), a.getRecordedAt());
    }

    private VisitNoteResponse toResponse(VisitNote v) {
        return new VisitNoteResponse(v.getId(), v.getPatientId(), v.getDoctorId(), v.getFacilityId(),
                v.getBloodPressure(), v.getTemperature(), v.getHeartRate(), v.getRespiratoryRate(),
                v.getOxygenSaturation(), v.getWeight(), v.getOtherVitals(), v.getNotes(), v.getVisitedAt());
    }
}
