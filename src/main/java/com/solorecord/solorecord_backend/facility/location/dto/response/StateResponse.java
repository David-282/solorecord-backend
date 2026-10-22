package com.solorecord.solorecord_backend.facility.location.dto.response;

import java.util.UUID;

public record StateResponse (
        UUID stateId,
        String code,
        String name
) {
}
