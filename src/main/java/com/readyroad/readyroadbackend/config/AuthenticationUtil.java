package com.readyroad.readyroadbackend.config;

import com.readyroad.readyroadbackend.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Authentication Utility for extracting user information from Spring Security context
 *
 * Supports two modes:
 * 1. DEV MODE (spring.security.mode=dev): Falls back to test user ID 1
 * 2. PRODUCTION MODE: Extracts user ID from JWT authentication
 *
 * @author ReadyRoad Team
 * @since Phase 5 - Feature B Security Enhancement
 */
@Component
@Slf4j
public class AuthenticationUtil {

    @Value("${spring.security.mode:secure}")
    private String securityMode;

    /**
     * Extract user ID from Spring Security Authentication
     *
     * Dev Mode (spring.security.mode=dev):
     * - Returns fallback user ID 1 for testing
     * - Logs warning about dev mode usage
     *
     * Production Mode (spring.security.mode=secure):
     * - Extracts user ID from authenticated User principal
     * - Returns null if authentication is missing or invalid
     *
     * @param authentication Spring Security authentication object
     * @return User ID or null if not authenticated (production mode)
     */
    public Long extractUserId(Authentication authentication) {
        // Check if dev mode is enabled
        if ("dev".equalsIgnoreCase(securityMode)) {
            return handleDevMode(authentication);
        }

        // Production mode: strict authentication required
        return handleProductionMode(authentication);
    }

    /**
     * Dev mode handler - provides fallback user ID for testing
     *
     * @param authentication Spring Security authentication (may be null)
     * @return User ID 1 (test user)
     */
    private Long handleDevMode(Authentication authentication) {
        if (authentication == null || "anonymousUser".equals(authentication.getPrincipal())) {
            log.warn("[DEV MODE] No authentication found, using fallback user ID 1");
            return 1L;
        }

        // Try to extract real user if authenticated
        if (authentication.getPrincipal() instanceof User user) {
            log.debug("[DEV MODE] Authenticated user: {} (ID: {})", user.getUsername(), user.getId());
            return user.getId();
        }

        // Fallback for dev mode
        log.warn("[DEV MODE] Authentication principal type unknown: {}, using fallback user ID 1",
            authentication.getPrincipal().getClass().getSimpleName());
        return 1L;
    }

    /**
     * Production mode handler - strict authentication enforcement
     *
     * @param authentication Spring Security authentication
     * @return User ID from JWT or null if not authenticated
     */
    private Long handleProductionMode(Authentication authentication) {
        if (authentication == null || "anonymousUser".equals(authentication.getPrincipal())) {
            log.warn("[PRODUCTION MODE] No authentication provided - access denied");
            return null;
        }

        if (!authentication.isAuthenticated()) {
            log.warn("[PRODUCTION MODE] Authentication present but not validated - access denied");
            return null;
        }

        // Extract User from principal
        if (authentication.getPrincipal() instanceof User user) {
            log.debug("[PRODUCTION MODE] Authenticated user: {} (ID: {})", user.getUsername(), user.getId());
            return user.getId();
        }

        // Unknown principal type - security risk
        log.error("[PRODUCTION MODE] Invalid authentication principal type: {} - access denied",
            authentication.getPrincipal().getClass().getSimpleName());
        return null;
    }

    /**
     * Check if dev mode is active
     *
     * @return true if security mode is 'dev'
     */
    public boolean isDevMode() {
        return "dev".equalsIgnoreCase(securityMode);
    }

    /**
     * Check if authentication is required (production mode)
     *
     * @return true if security mode is NOT 'dev'
     */
    public boolean isAuthenticationRequired() {
        return !isDevMode();
    }
}
