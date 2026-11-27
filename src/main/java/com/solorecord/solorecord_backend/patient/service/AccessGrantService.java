package com.solorecord.solorecord_backend.patient.service;

import com.solorecord.solorecord_backend.patient.dto.request.AccessRequest;
import com.solorecord.solorecord_backend.patient.dto.response.AccessGrantResponse;

import java.util.List;
import java.util.UUID;

public interface AccessGrantService {


    AccessGrantResponse accessRequest(UUID doctorId, UUID doctorFacilityId, AccessRequest request);
    void accessApprove(UUID callerPatientId, UUID grantId);
    void accessDeny(UUID callerPatientId, UUID grantId);
    void accessRevoke(UUID callerPatientId, UUID grantId);
    List<AccessGrantResponse> listPending(UUID callerPatientId);
    List<AccessGrantResponse> listForDoctor(UUID doctorId);

    /** True only if this doctor holds a GRANTED grant for this patient at this facility. */
    boolean hasActiveAccess(UUID doctorId, UUID facilityId, UUID patientId);

}
