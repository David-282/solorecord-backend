package com.solorecord.solorecord_backend.identity.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
public class AuthResponse {

    private String token;
    private String role;
    private String fullName;
    private UUID userId;
}
