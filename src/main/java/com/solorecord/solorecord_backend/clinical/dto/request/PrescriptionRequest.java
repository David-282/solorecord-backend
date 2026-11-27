package com.solorecord.solorecord_backend.clinical.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record PrescriptionRequest(
        @NotBlank @Size(max = 255) String medication,
        @NotBlank @Size(max = 255) String dosage,
        @NotBlank @Size(max = 255) String duration,
        UUID diagnosisId
) {
}
