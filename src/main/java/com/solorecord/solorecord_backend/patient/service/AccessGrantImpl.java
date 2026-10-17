package com.solorecord.solorecord_backend.patient.service;

import com.solorecord.solorecord_backend.events.model.AccessRequestedEvent;
import com.solorecord.solorecord_backend.events.publisher.EventPublisher;
import com.solorecord.solorecord_backend.patient.Mapper;
import com.solorecord.solorecord_backend.patient.data.model.AccessGrant;
import com.solorecord.solorecord_backend.patient.data.model.AccessStatus;
import com.solorecord.solorecord_backend.patient.data.repository.AccessGrantRepository;
import com.solorecord.solorecord_backend.patient.data.repository.PatientRepository;
import com.solorecord.solorecord_backend.patient.dto.request.AccessRequest;
import com.solorecord.solorecord_backend.patient.dto.response.AccessGrantResponse;
import com.solorecord.solorecord_backend.patient.exception.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


import static com.solorecord.solorecord_backend.patient.Mapper.map;

@Service
@AllArgsConstructor
@Transactional
public class AccessGrantImpl implements AccessGrantService{

    private final PatientRepository patientRepository;
    private final AccessGrantRepository accessGrantRepository;
    private final EventPublisher eventPublisher;


    @Override
    public AccessGrantResponse accessRequest(UUID doctorId, UUID doctorFacilityId, AccessRequest request) {
        if (doctorFacilityId == null) {
            throw new InvalidAccessRequestException("Doctor is not attached to a facility");
        }

        if (!patientRepository.existsById(request.getPatientId())) {
            throw new UserNotFoundException("User not found");
        }

        boolean alreadyOpen = accessGrantRepository
                .existsByDoctorIdAndPatientIdAndFacilityIdAndStatusIn(
                        doctorId,
                        request.getPatientId(),
                        doctorFacilityId,
                        List.of(AccessStatus.PENDING, AccessStatus.GRANTED));

        if (alreadyOpen) {
            throw new DuplicateAccessRequestException(
                    "An active or pending access request already exists");
        }

        AccessGrant grant =  accessGrantRepository.save(
                new AccessGrant(request.getPatientId(), doctorId, doctorFacilityId));

        eventPublisher.publish(AccessRequestedEvent.of(grant.getId(),grant.getPatientId(),grant.getDoctorId(),grant.getFacilityId()));


        return map(  grant
                );
    }

    @Override
    public void accessApprove(UUID callerPatientId, UUID grantId) {
        AccessGrant access = loadOwnedGrant(callerPatientId, grantId);
        requireStatus(access, AccessStatus.PENDING, "Only pending requests can be approved");
        access.grant();
        accessGrantRepository.save(access);
    }

    @Override
    public void accessDeny(UUID callerPatientId, UUID grantId) {
        AccessGrant access = loadOwnedGrant(callerPatientId, grantId);
        requireStatus(access, AccessStatus.PENDING, "Only pending requests can be denied");
        access.deny();
        accessGrantRepository.save(access);
    }

    @Override
    public void accessRevoke(UUID callerPatientId, UUID grantId) {
        AccessGrant access = loadOwnedGrant(callerPatientId, grantId);
        requireStatus(access, AccessStatus.GRANTED, "Only granted access can be revoked");
        access.revoke();
        accessGrantRepository.save(access);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccessGrantResponse> listPending(UUID callerPatientId) {
        return accessGrantRepository
                .findByPatientIdAndStatusOrderByCreatedAtDesc(callerPatientId, AccessStatus.PENDING)
                .stream()
                .map(Mapper::map)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccessGrantResponse> listForDoctor(UUID doctorId) {
        return accessGrantRepository
                .findByDoctorIdOrderByCreatedAtDesc(doctorId)
                .stream()
                .map(Mapper ::map)
                .toList();
    }

    private AccessGrant loadOwnedGrant(UUID callerPatientId, UUID grantId) {
        return accessGrantRepository.findById(grantId)
                .filter(g -> g.getPatientId().equals(callerPatientId))
                .orElseThrow(() -> new AccessGrantNotFoundException("Access request not found"));
    }

    private void requireStatus(AccessGrant access, AccessStatus expected, String message) {
        if (access.getStatus() != expected) {
            throw new InvalidAccessStateException(message);
        }
    }
}
