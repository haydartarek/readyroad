package com.readyroad.readyroadbackend.config;

import com.readyroad.readyroadbackend.service.AdminSystemSettingsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaintenanceModeFilter extends OncePerRequestFilter {

    private static final Set<String> MAINTENANCE_ALLOWED_PREFIXES = Set.of(
            "/actuator/health",
            "/actuator/info",
            "/swagger-ui",
            "/v3/api-docs",
            "/api/auth/login",
            "/api/auth/health",
            "/api/auth/me",
            "/api/admin");

    private final AdminSystemSettingsService adminSystemSettingsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return MAINTENANCE_ALLOWED_PREFIXES.stream().anyMatch(uri::startsWith);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (!adminSystemSettingsService.isMaintenanceModeEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication != null
                && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                        .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

        if (isAdmin) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("⛔ Blocking request during maintenance mode: {}", request.getRequestURI());
        response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"error\":\"Service Unavailable\",\"message\":\"The platform is currently in maintenance mode.\"}");
    }
}
