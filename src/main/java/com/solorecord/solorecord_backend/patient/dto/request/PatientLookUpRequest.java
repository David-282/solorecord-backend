package com.solorecord.solorecord_backend.patient.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record PatientLookUpRequest(
        @NotBlank String nationalId,
        @NotNull @Past LocalDate dateOfBirth
) {
}
