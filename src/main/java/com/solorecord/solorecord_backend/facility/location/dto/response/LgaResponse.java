package com.solorecord.solorecord_backend.facility.location.dto.response;

import java.util.UUID;

public record LgaResponse(
        UUID lgaId,
        String name,
        String code
) {

}
