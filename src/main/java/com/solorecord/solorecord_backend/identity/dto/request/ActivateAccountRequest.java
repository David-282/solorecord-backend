package com.solorecord.solorecord_backend.identity.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActivateAccountRequest(
        @NotBlank @Size(min = 8) String password,
        @NotBlank @Email String email,
        @NotBlank String otp

) {
}
