package com.solorecord.solorecord_backend.identity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {

    private String message;
    private String email;
}
