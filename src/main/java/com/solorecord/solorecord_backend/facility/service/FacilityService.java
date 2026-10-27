package com.solorecord.solorecord_backend.facility.service;

import com.solorecord.solorecord_backend.facility.dto.request.FacilityOnboardingRequest;
import com.solorecord.solorecord_backend.facility.dto.request.FacilityRejectRequest;
import com.solorecord.solorecord_backend.facility.dto.request.StaffOnboardingRequest;
import com.solorecord.solorecord_backend.facility.dto.response.AdminContactResponse;
import com.solorecord.solorecord_backend.facility.dto.response.FacilityOnboardingResponse;
import com.solorecord.solorecord_backend.facility.dto.response.PendingFacilityResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface FacilityService {

    FacilityOnboardingResponse onboardFacility (FacilityOnboardingRequest request);

    void  approveFacility (UUID facilityId, UUID reviewerId);


    @Transactional
    void disapproveFacility(UUID facilityId, UUID reviewerId, FacilityRejectRequest request);

    List<PendingFacilityResponse> getPendingFacilities();

    AdminContactResponse getAdminContact(UUID facilityId);

    String getFacilityName (UUID id);
}
