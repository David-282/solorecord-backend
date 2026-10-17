package com.solorecord.solorecord_backend.patient.service;

import com.solorecord.solorecord_backend.patient.dto.request.PatientLookUpRequest;
import com.solorecord.solorecord_backend.patient.dto.response.PatientLookUpResponse;

import java.util.UUID;

public interface PatientLookUpService {

    PatientLookUpResponse lookup(UUID doctorId, UUID doctorFacilityId, PatientLookUpRequest request);
}
