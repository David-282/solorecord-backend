package com.solorecord.solorecord_backend.patient.dto.response;

import com.solorecord.solorecord_backend.patient.utility.NameMasker;

import java.util.UUID;

public record PatientLookUpResponse(
        UUID patientId,
        String name
) {

    public static PatientLookUpResponse of(UUID patientId, String name){

        return new PatientLookUpResponse(
                patientId,
                NameMasker.mask(name));

    }

}
