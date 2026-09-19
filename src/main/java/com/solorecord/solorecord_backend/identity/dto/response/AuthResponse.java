package com.solorecord.solorecord_backend.identity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String role;
    private UUID userId;
    private  String email;
}
