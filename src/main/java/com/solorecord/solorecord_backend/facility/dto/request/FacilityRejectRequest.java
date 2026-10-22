package com.solorecord.solorecord_backend.facility.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FacilityRejectRequest(
         @NotBlank @Size(max= 500) String rejectionMessage
) {
}

