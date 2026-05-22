package com.readyroad.readyroadbackend.security;

import com.readyroad.readyroadbackend.util.AuthenticationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Security Verification Test for Feature B - PRODUCTION MODE
 *
 * This test proves that AuthenticationUtil rejects missing authentication
 * when spring.security.mode is set to "secure" (production mode)
 * and no valid authentication is provided.
 *
 * This is distinct from regular integration tests which run in dev mode.
 *
 * @author ReadyRoad Team
 * @since Phase 5 - Feature B Security Verification
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.security.mode=secure" // Override to production mode
})
@DisplayName("Feature B Security Verification - Production Mode")
public class FeatureBProductionSecurityTest {

    @Autowired
    private AuthenticationUtil authenticationUtil;

    // =========================================================================
    // Scenario 6: Secure mode must be provable by at least one test
    // =========================================================================

    @Test
    @DisplayName("PROOF: AuthenticationUtil rejects missing authentication in secure mode")
    void testAuthenticationUtil_SecureMode_RejectsNoAuth() {
        // Given: spring.security.mode=secure (set via @TestPropertySource)
        // And: No authentication provided

        // When & Then: Missing authentication is rejected immediately
        assertThatThrownBy(() -> authenticationUtil.extractUserId(null))
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
                .hasMessage("Authentication required. Please login.");

        // Verify secure mode is active
        assertThat(authenticationUtil.isDevMode())
                .as("Should not be in dev mode")
                .isFalse();
    }

    @Test
    @DisplayName("PROOF: AuthenticationUtil rejects anonymous user in secure mode")
    void testAuthenticationUtil_SecureMode_RejectsAnonymousUser() {
        // Given: spring.security.mode=secure
        // And: Anonymous authentication
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getPrincipal()).thenReturn("anonymousUser");
        when(mockAuth.isAuthenticated()).thenReturn(false);

        // When & Then: Anonymous authentication is rejected immediately
        assertThatThrownBy(() -> authenticationUtil.extractUserId(mockAuth))
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
                .hasMessage("Authentication required. Anonymous access not permitted.");
    }

    @Test
    @DisplayName("PROOF: isAuthenticationRequired returns true in secure mode")
    void testAuthenticationUtil_SecureMode_RequiresAuthentication() {
        // Given: spring.security.mode=secure

        // When: Check if authentication is required
        boolean required = authenticationUtil.isAuthenticationRequired();

        // Then: Should return true
        assertThat(required)
                .as("Production mode should require authentication")
                .isTrue();
    }

    @Test
    @DisplayName("VERIFICATION: Security mode is configured as secure")
    void testSecurityMode_IsSecure() {
        // Given: @TestPropertySource sets spring.security.mode=secure

        // When: Check dev mode status
        boolean isDevMode = authenticationUtil.isDevMode();

        // Then: Should be false (not in dev mode)
        assertThat(isDevMode)
                .as("Security mode should be 'secure', not 'dev'")
                .isFalse();
    }
}
