package com.solorecord.solorecord_backend.lab.dto.response;

import com.solorecord.solorecord_backend.lab.data.model.ResultStatus;
import com.solorecord.solorecord_backend.lab.data.model.TestType;

import java.time.LocalDateTime;
import java.util.UUID;

public record TestResultResponse(
        UUID id, UUID patientId, UUID labTechId, UUID facilityId, UUID testOrderId,
        TestType testType, String otherTestName, String resultValue, ResultStatus status,
        LocalDateTime enteredAt, LocalDateTime completedAt, UUID reviewingDoctorId
) {
}
