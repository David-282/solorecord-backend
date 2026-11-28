package com.solorecord.solorecord_backend.lab.service;

import com.solorecord.solorecord_backend.lab.dto.request.LabInitiatedResultRequest;
import com.solorecord.solorecord_backend.lab.dto.request.ResultValueRequest;
import com.solorecord.solorecord_backend.lab.dto.request.TestOrderRequest;
import com.solorecord.solorecord_backend.lab.dto.response.TestOrderResponse;
import com.solorecord.solorecord_backend.lab.dto.response.TestResultResponse;

import java.util.List;
import java.util.UUID;

/**
 * Doctor, lab tech, admin and facility always come from the authenticated principal.
 * Orders need the doctor's active access grant. Lab-initiated walk-in results need no grant
 * but are limited to the lab tech's own facility.
 */
public interface LabService {

    TestOrderResponse createOrder(UUID doctorId, UUID facilityId, TestOrderRequest request);

    List<TestOrderResponse> listPendingOrders(UUID facilityId);

    TestResultResponse enterOrderResult(UUID labTechId, UUID facilityId, UUID orderId, ResultValueRequest request);

    TestResultResponse enterLabInitiatedResult(UUID labTechId, UUID facilityId, LabInitiatedResultRequest request);

    TestResultResponse updateResultValue(UUID labTechId, UUID facilityId, UUID resultId, ResultValueRequest request);

    TestResultResponse completeResult(UUID labTechId, UUID facilityId, UUID resultId);

    TestResultResponse routeResult(UUID adminFacilityId, UUID resultId, UUID doctorId);

    List<TestResultResponse> getResultsForDoctor(UUID doctorId, UUID facilityId, UUID patientId);

    List<TestResultResponse> getOwnResults(UUID patientId);

    /** Used by clinical to validate a diagnosis link. */
    boolean isCompletedResultForPatient(UUID resultId, UUID patientId);
}
