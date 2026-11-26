package com.solorecord.solorecord_backend.identity.dto.request;

import com.solorecord.solorecord_backend.identity.data.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateUserRequest {

    @NotBlank
    private String fullName;
    @NotBlank @Email
    private String email;
    @Size(min = 8)
    private String rawPassword;
    private String phoneNumber;
//    private UUID facilityId;
//    private Role role;
}
