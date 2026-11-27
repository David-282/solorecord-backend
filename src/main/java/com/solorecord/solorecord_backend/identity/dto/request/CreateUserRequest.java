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

    @NotBlank @Size(max = 150)
    private String fullName;
    @NotBlank @Email @Size(max = 254)
    private String email;
    @NotBlank @Size(min = 8, max = 72)
    private String rawPassword;
    @NotBlank @Size(max = 20)
    private String phoneNumber;
//    private UUID facilityId;
//    private Role role;
}
