package com.solorecord.solorecord_backend.clinical;

import com.solorecord.solorecord_backend.clinical.data.model.Diagnosis;
import com.solorecord.solorecord_backend.clinical.data.model.Prescription;
import com.solorecord.solorecord_backend.clinical.data.repository.AllergyRepository;
import com.solorecord.solorecord_backend.clinical.data.repository.DiagnosisRepository;
import com.solorecord.solorecord_backend.clinical.data.repository.PrescriptionRepository;
import com.solorecord.solorecord_backend.clinical.data.repository.VisitNoteRepository;
import com.solorecord.solorecord_backend.clinical.dto.request.DiagnosisRequest;
import com.solorecord.solorecord_backend.clinical.dto.request.PrescriptionRequest;
import com.solorecord.solorecord_backend.clinical.dto.response.DiagnosisResponse;
import com.solorecord.solorecord_backend.clinical.exception.AccessNotGrantedException;
import com.solorecord.solorecord_backend.clinical.exception.InvalidDiagnosisLinkException;
import com.solorecord.solorecord_backend.clinical.service.ClinicalServiceImpl;
import com.solorecord.solorecord_backend.events.publisher.EventPublisher;
import com.solorecord.solorecord_backend.lab.service.LabService;
import com.solorecord.solorecord_backend.patient.service.AccessGrantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClinicalServiceImplTest {

    private final UUID doctorId = UUID.randomUUID();
    private final UUID facilityId = UUID.randomUUID();
    private final UUID patientId = UUID.randomUUID();

    private DiagnosisRepository diagnosisRepository;
    private PrescriptionRepository prescriptionRepository;
    private AccessGrantService accessGrantService;
    private LabService labService;
    private EventPublisher eventPublisher;
    private ClinicalServiceImpl service;

    @BeforeEach
    void setUp() {
        diagnosisRepository = mock(DiagnosisRepository.class);
        prescriptionRepository = mock(PrescriptionRepository.class);
        accessGrantService = mock(AccessGrantService.class);
        labService = mock(LabService.class);
        eventPublisher = mock(EventPublisher.class);
        service = new ClinicalServiceImpl(diagnosisRepository, prescriptionRepository,
                mock(AllergyRepository.class), mock(VisitNoteRepository.class),
                accessGrantService, labService, eventPublisher);
    }

    @Test
    void addDiagnosisWithoutActiveGrantIsRejectedAndNothingSaved() {
        when(accessGrantService.hasActiveAccess(doctorId, facilityId, patientId)).thenReturn(false);

        assertThrows(AccessNotGrantedException.class, () ->
                service.addDiagnosis(doctorId, facilityId, patientId, new DiagnosisRequest("Malaria", null, null)));

        verify(diagnosisRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void addDiagnosisWithGrantSavesWithServerSetDoctorAndFacility() {
        when(accessGrantService.hasActiveAccess(doctorId, facilityId, patientId)).thenReturn(true);
        when(diagnosisRepository.save(any(Diagnosis.class))).thenAnswer(i -> i.getArgument(0));

        DiagnosisResponse response = service.addDiagnosis(doctorId, facilityId, patientId,
                new DiagnosisRequest("  Malaria ", "fever", null));

        assertEquals("Malaria", response.condition());
        assertEquals(doctorId, response.doctorId());
        assertEquals(facilityId, response.facilityId());
        assertEquals(patientId, response.patientId());
        verify(eventPublisher).publish(any());
    }

    @Test
    void prescriptionLinkedToAnotherPatientsDiagnosisIsRejected() {
        UUID diagnosisId = UUID.randomUUID();
        when(accessGrantService.hasActiveAccess(doctorId, facilityId, patientId)).thenReturn(true);
        when(diagnosisRepository.existsByIdAndPatientId(diagnosisId, patientId)).thenReturn(false);

        assertThrows(InvalidDiagnosisLinkException.class, () ->
                service.addPrescription(doctorId, facilityId, patientId,
                        new PrescriptionRequest("Paracetamol", "500mg", "3 days", diagnosisId)));

        verify(prescriptionRepository, never()).save(any(Prescription.class));
    }

    @Test
    void doctorRecordReadRequiresActiveGrant() {
        when(accessGrantService.hasActiveAccess(doctorId, facilityId, patientId)).thenReturn(false);

        assertThrows(AccessNotGrantedException.class, () ->
                service.getRecordForDoctor(doctorId, facilityId, patientId));

        verify(diagnosisRepository, never()).findByPatientIdOrderByDiagnosedAtDesc(any());
    }

    @Test
    void diagnosisLinkedToUnknownOrIncompleteResultIsRejected() {
        UUID resultId = UUID.randomUUID();
        when(accessGrantService.hasActiveAccess(doctorId, facilityId, patientId)).thenReturn(true);
        when(labService.isCompletedResultForPatient(resultId, patientId)).thenReturn(false);

        assertThrows(InvalidDiagnosisLinkException.class, () ->
                service.addDiagnosis(doctorId, facilityId, patientId,
                        new DiagnosisRequest("Malaria", null, resultId)));

        verify(diagnosisRepository, never()).save(any());
    }
}
