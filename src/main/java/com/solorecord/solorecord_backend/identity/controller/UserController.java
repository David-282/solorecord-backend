

package com.solorecord.solorecord_backend.identity.controller;

import com.solorecord.solorecord_backend.identity.data.model.User;
import com.solorecord.solorecord_backend.identity.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;


    @PatchMapping("/{userId}/activate")
    public ResponseEntity<String> activateUser(@PathVariable UUID userId) {
        userService.activateUser(userId);
        return ResponseEntity.ok("User activated successfully.");
    }

    @PatchMapping("/{userId}/deactivate")
    public ResponseEntity<String> deactivateUser(@PathVariable UUID userId) {
        userService.deactivateUser(userId);
        return ResponseEntity.ok("User deactivated successfully.");
    }

    @GetMapping("/facility/{facilityId}")
    public ResponseEntity<List<User>> getUsersByFacilityId(@PathVariable UUID facilityId) {
        List<User> users = userService.getUserByFacilityId(facilityId);
        return ResponseEntity.ok(users);
    }
}
