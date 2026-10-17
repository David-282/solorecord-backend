package com.solorecord.solorecord_backend.patient.dto.request;

import java.time.LocalDate;

public record PatientLookUpRequest(
        String nationalId,
        LocalDate dateOfBirth
) {
}
