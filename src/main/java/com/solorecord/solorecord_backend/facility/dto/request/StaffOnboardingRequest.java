package com.solorecord.solorecord_backend.facility.dto.request;

import com.solorecord.solorecord_backend.facility.data.model.StaffRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Details a facility admin submits to add a staff member. The role is limited to {@link StaffRole},
 * so a privileged role such as SUPER_ADMIN is rejected while the body is parsed.
 */
public record StaffOnboardingRequest(
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(max = 150) String fullName,
        @NotBlank @Size(max = 20) String phoneNumber,
        @NotNull StaffRole role
) {
}
