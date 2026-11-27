package com.solorecord.solorecord_backend.patient.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class AccessRequest {

    @NotNull
    private UUID patientId;

}
