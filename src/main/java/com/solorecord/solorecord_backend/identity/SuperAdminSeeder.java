package com.solorecord.solorecord_backend.identity;

import com.solorecord.solorecord_backend.identity.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class SuperAdminSeeder implements CommandLineRunner {

    private final UserService userService;

    @Value("${app.super-admin.email}")
    private String email;

    @Value("${app.super-admin.full-name}")
    private String fullName;

    @Value("${app.super-admin.password}")
    private String password;


    @Override
    public void run(String... args) throws Exception {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            log.warn("Super admin not seeded: SUPER_ADMIN_EMAIL or SUPER_ADMIN_PASSWORD is not set");
            return;
        }
        if (password.length() < 8) {
            log.warn("Super admin not seeded: password must be at least 8 characters");
            return;
        }

        userService.createSuperAdmin(fullName, email, password);
        log.info("Super admin check complete");


    }
}
