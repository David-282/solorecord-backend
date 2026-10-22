package com.solorecord.solorecord_backend.facility.dto.response;

public record AdminContactResponse(
        String adminFullName,
        String adminEmail,
        String adminPhone
) {
}
