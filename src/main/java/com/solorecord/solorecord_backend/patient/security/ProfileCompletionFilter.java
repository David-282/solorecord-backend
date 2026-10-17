package com.solorecord.solorecord_backend.patient.security;

import com.solorecord.solorecord_backend.identity.data.model.Role;
import com.solorecord.solorecord_backend.identity.security.CustomUserDetails;
import com.solorecord.solorecord_backend.patient.service.PatientService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class ProfileCompletionFilter extends OncePerRequestFilter {

    private final PatientService patientService;

    private static final List<String> ALLOWED_PATHS = List.of(
            "/api/v1/auth",
            "/api/v1/patients/complete-profile",
            "/api/v1/patients/profile-status"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        boolean isAllowed = ALLOWED_PATHS.stream().anyMatch(path::startsWith);

        if (isAllowed) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            var user = userDetails.getUser();

            if (user.getRole() == Role.PATIENT && !patientService.hasCompletedProfile(user.getId())) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Please complete your profile before continuing.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}