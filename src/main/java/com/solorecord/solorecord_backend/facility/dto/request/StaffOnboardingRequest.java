package com.solorecord.solorecord_backend.facility.dto.request;

import com.solorecord.solorecord_backend.facility.data.model.StaffRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StaffOnboardingRequest(
        @Email @NotBlank String email,
        @NotBlank String fullName,
        @NotNull StaffRole role

) {
}
