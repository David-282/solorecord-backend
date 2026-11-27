package com.solorecord.solorecord_backend.clinical.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AllergyRequest(
        @NotBlank @Size(max = 255) String allergen,
        @NotBlank @Size(max = 50) String severity
) {
}
