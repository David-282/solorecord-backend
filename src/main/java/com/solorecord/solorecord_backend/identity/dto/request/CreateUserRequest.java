package com.solorecord.solorecord_backend.identity.dto.request;

import com.solorecord.solorecord_backend.identity.data.model.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateUserRequest {

    private String fullName;
    private String email;
    private String rawPassword;
    private String phoneNumber;
    private UUID facilityId;
    private Role role;
}
