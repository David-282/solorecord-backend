package com.solorecord.solorecord_backend.lab.dto.request;

import com.solorecord.solorecord_backend.lab.data.model.TestType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record TestOrderRequest(
        @NotNull UUID patientId,
        @NotNull TestType testType,
        @Size(max = 100) String otherTestName,
        @Size(max = 1000) String notes
) {
}
