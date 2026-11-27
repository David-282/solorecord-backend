package com.solorecord.solorecord_backend.clinical.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VisitNoteRequest(
        @Pattern(regexp = "\\d{2,3}/\\d{2,3}", message = "must look like 120/80") String bloodPressure,
        @DecimalMin("25.0") @DecimalMax("45.0") Double temperature,
        @Min(20) @Max(300) Integer heartRate,
        @Min(4) @Max(80) Integer respiratoryRate,
        @DecimalMin("0.0") @DecimalMax("100.0") Double oxygenSaturation,
        @DecimalMin("0.1") @DecimalMax("500.0") Double weight,
        @Size(max = 500) String otherVitals,
        @NotBlank @Size(max = 4000) String notes
) {
}
