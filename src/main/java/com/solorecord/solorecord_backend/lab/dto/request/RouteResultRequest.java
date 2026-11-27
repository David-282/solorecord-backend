package com.solorecord.solorecord_backend.lab.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RouteResultRequest(
        @NotNull UUID doctorId
) {
}
