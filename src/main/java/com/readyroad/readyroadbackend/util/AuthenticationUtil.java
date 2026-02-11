package com.readyroad.readyroadbackend.util;

import com.readyroad.readyroadbackend.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationUtil {

    private static final Long DEV_MODE_FALLBACK_USER_ID = 1L;
    private static final String ANONYMOUS_USER = "anonymousUser";

    @Value("${spring.security.mode:secure}")
    private String securityMode;

    public Long extractUserId(Authentication authentication) {
        if (isDevMode()) {
            return handleDevMode(authentication);
        }
        return handleProductionMode(authentication);
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return extractUserId(authentication);
    }

    public User extractUser(Authentication authentication) {
        if (isDevMode()) {
            log.warn("[DEV MODE] extractUser() called");
            if (authentication != null && authentication.getPrincipal() instanceof User user) {
                return user;
            }
            throw new AuthenticationCredentialsNotFoundException("[DEV MODE] Cannot extract User entity");
        }

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("[PRODUCTION MODE] User must be authenticated");
        }

        if (authentication.getPrincipal() instanceof User user) {
            log.debug("[PRODUCTION MODE] Extracted User: {} (ID: {})", user.getUsername(), user.getId());
            return user;
        }

        throw new AuthenticationCredentialsNotFoundException("[PRODUCTION MODE] Invalid principal type");
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return extractUser(authentication);
    }

    public boolean isAuthenticated(Authentication authentication) {
        if (isDevMode()) {
            return true;
        }
        return authentication != null &&
                authentication.isAuthenticated() &&
                !ANONYMOUS_USER.equals(authentication.getPrincipal());
    }

    public boolean isDevMode() {
        return "dev".equalsIgnoreCase(securityMode);
    }

    public boolean isProductionMode() {
        return !isDevMode();
    }

    public boolean isAuthenticationRequired() {
        return isProductionMode();
    }

    private Long handleDevMode(Authentication authentication) {
        if (authentication == null || ANONYMOUS_USER.equals(authentication.getPrincipal())) {
            log.warn("[DEV MODE] No authentication found, using fallback user ID: {}", DEV_MODE_FALLBACK_USER_ID);
            return DEV_MODE_FALLBACK_USER_ID;
        }

        if (authentication.getPrincipal() instanceof User user) {
            log.debug("[DEV MODE] Authenticated user: {} (ID: {})", user.getUsername(), user.getId());
            return user.getId();
        }

        log.warn("[DEV MODE] Unknown principal type, using fallback user ID: {}", DEV_MODE_FALLBACK_USER_ID);
        return DEV_MODE_FALLBACK_USER_ID;
    }

    private Long handleProductionMode(Authentication authentication) {
        if (authentication == null) {
            log.error("[PRODUCTION MODE] Authentication is null");
            throw new AuthenticationCredentialsNotFoundException("Authentication required. Please login.");
        }

        if (ANONYMOUS_USER.equals(authentication.getPrincipal())) {
            log.error("[PRODUCTION MODE] Anonymous user detected");
            throw new AuthenticationCredentialsNotFoundException(
                    "Authentication required. Anonymous access not permitted.");
        }

        if (!authentication.isAuthenticated()) {
            log.error("[PRODUCTION MODE] Authentication not validated");
            throw new AuthenticationCredentialsNotFoundException("Authentication failed. Please login again.");
        }

        if (authentication.getPrincipal() instanceof User user) {
            log.debug("[PRODUCTION MODE] Authenticated user: {} (ID: {})", user.getUsername(), user.getId());
            return user.getId();
        }

        log.error("[PRODUCTION MODE] Invalid principal type: {}",
                authentication.getPrincipal().getClass().getSimpleName());
        throw new AuthenticationCredentialsNotFoundException("Invalid authentication principal");
    }
}
