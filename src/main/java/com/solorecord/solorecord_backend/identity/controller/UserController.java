

package com.solorecord.solorecord_backend.identity.controller;

import com.solorecord.solorecord_backend.identity.data.model.User;
import com.solorecord.solorecord_backend.identity.dto.response.UserResponse;
import com.solorecord.solorecord_backend.identity.security.CustomUserDetails;
import com.solorecord.solorecord_backend.identity.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('FACILITY_ADMIN')")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/{userId}/deactivate")
    @PreAuthorize("hasRole('FACILITY_ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable UUID userId,
                                           @AuthenticationPrincipal CustomUserDetails principal) {
        User admin = principal.getUser();
        userService.deactivateStaff(userId, admin.getId(), admin.getFacilityId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/activate")
    @PreAuthorize("hasRole('FACILITY_ADMIN')")
    public ResponseEntity<Void> activate(@PathVariable UUID userId,
                                         @AuthenticationPrincipal CustomUserDetails principal) {
        User admin = principal.getUser();
        userService.reactivateStaff(userId, admin.getId(), admin.getFacilityId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/facility")
    @PreAuthorize("hasRole('FACILITY_ADMIN')")
    public ResponseEntity<List<UserResponse>> getUsersByFacilityId(@AuthenticationPrincipal CustomUserDetails principal ) {
        List<UserResponse> users = userService.getUserByFacilityId(principal.getUser().getFacilityId());
        return ResponseEntity.ok(users);
    }
}
