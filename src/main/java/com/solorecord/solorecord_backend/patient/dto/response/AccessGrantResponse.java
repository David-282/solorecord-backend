package com.solorecord.solorecord_backend.patient.dto.response;

import com.solorecord.solorecord_backend.patient.data.model.AccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccessGrantResponse {

    private  UUID grantId;
    private UUID patientId;
    private UUID doctorId;
    private UUID facilityId;
    private AccessStatus status;
    private LocalDateTime createdAt;
}
