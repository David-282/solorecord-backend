package com.solorecord.solorecord_backend.facility.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class FacilityOnboardingRequest {

    @NotBlank
    @Size(max = 150)
    private String facilityName;

    @NotBlank
    @Size(max = 150)
    private String addressLine;

    @NotNull
    private UUID lgaId;

    @NotBlank
    @Size(max = 150)
    private String licenseNumber;

    @NotBlank
    @Size(max = 150)
    private String adminFullName;

    @NotBlank
    @Email
    @Size(max = 150)
    private String adminEmail;

    @NotBlank
    private String adminPhone;
}
