package com.solorecord.solorecord_backend.lab.dto.response;

import com.solorecord.solorecord_backend.lab.data.model.OrderStatus;
import com.solorecord.solorecord_backend.lab.data.model.TestType;

import java.time.LocalDateTime;
import java.util.UUID;

public record TestOrderResponse(
        UUID id, UUID patientId, UUID doctorId, UUID facilityId,
        TestType testType, String otherTestName, String notes,
        OrderStatus status, LocalDateTime orderedAt
) {
}
