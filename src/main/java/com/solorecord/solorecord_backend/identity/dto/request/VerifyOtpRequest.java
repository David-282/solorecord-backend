package com.solorecord.solorecord_backend.identity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VerifyOtpRequest {

    @NotBlank
    private String email;
    @NotBlank @Pattern(regexp = "\\d{6}", message = "must be a 6-digit code")
    private String otp;

}
