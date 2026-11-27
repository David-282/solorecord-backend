package com.solorecord.solorecord_backend.clinical.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record DiagnosisRequest(
        @NotBlank @Size(max = 255) String condition,
        @Size(max = 2000) String notes,
        UUID testResultId
) {
}
