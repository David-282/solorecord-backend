package com.solorecord.solorecord_backend.identity.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequestActivationCodeRequest(
        @NotBlank @Email String email
) {
}
