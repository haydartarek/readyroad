package com.readyroad.readyroadbackend.config;

import com.readyroad.readyroadbackend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 *
 * Intercepts HTTP requests and validates JWT tokens.
 * Extracts user information from valid tokens and sets authentication context.
 *
 * Flow:
 * 1. Extract JWT token from Authorization header
 * 2. Validate token and extract username
 * 3. Load user details from database
 * 4. Set authentication in SecurityContext
 *
 * @author ReadyRoad Team
 * @since 2026-01-18
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Skip JWT authentication filter for public endpoints
     * CRITICAL: This ensures /api/auth/** endpoints are accessible without JWT
     * tokens
     *
     * @param request HTTP request
     * @return true if filter should be skipped, false otherwise
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();

        // /api/auth/me REQUIRES JWT - do NOT skip
        if (path.equals("/api/auth/me")) {
            log.debug("🔐 JWT filter ACTIVE for /api/auth/me (requires authentication)");
            return false;
        }

        // Skip filter for other authentication endpoints (login, register, health)
        if (path.startsWith("/api/auth/")) {
            log.debug("⏭️ Skipping JWT filter for auth endpoint: {}", path);
            return true;
        }

        // Skip filter for public endpoints
        if (path.equals("/api/health") ||
                path.startsWith("/api/traffic-signs/") ||
                path.equals("/api/search") ||
                path.startsWith("/actuator/") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars/")) {
            log.debug("⏭️ Skipping JWT filter for public endpoint: {}", path);
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String requestURI = request.getRequestURI();
        final String method = request.getMethod();

        log.debug("🔍 JWT Filter - {} {}", method, requestURI);

        // Extract Authorization header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Check if Authorization header is present and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("⚪ No JWT token found - allowing anonymous access to: {} {}", method, requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token (remove "Bearer " prefix)
        jwt = authHeader.substring(7);
        log.debug("🔑 JWT token found (length: {})", jwt.length());

        try {
            // Extract username from JWT token
            username = jwtService.extractUsername(jwt);
            log.debug("👤 Username extracted from JWT: {}", username);

            // If username is extracted and no authentication exists in context
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Load user details from database
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                log.debug("✅ User details loaded from database");

                // Validate token
                if (jwtService.validateToken(jwt, userDetails)) {

                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    // Set authentication details
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication in SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("✅ Authentication set in SecurityContext for user: {}", username);
                } else {
                    log.warn("⚠️ Invalid JWT token for user: {}", username);
                }
            }
        } catch (Exception e) {
            // Log error and continue without authentication
            log.error("❌ Cannot set user authentication: {}", e.getMessage());
            log.error("Exception details:", e);
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}
