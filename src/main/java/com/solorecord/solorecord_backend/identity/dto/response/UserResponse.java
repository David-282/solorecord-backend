package com.solorecord.solorecord_backend.identity.dto.response;

import com.solorecord.solorecord_backend.identity.data.model.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private String fullName;
    private String email;
    private AccountStatus status;
    private String role;
    private UUID id;

}
