package com.solorecord.solorecord_backend.patient.dto.request;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Setter
@Getter
public class AccessRequest {

    private UUID patientId;

}
