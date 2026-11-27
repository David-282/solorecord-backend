package com.solorecord.solorecord_backend.lab.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResultValueRequest(
        @NotBlank @Size(max = 2000) String resultValue
) {
}
