package com.readyroad.readyroadbackend.config;

/**
 * DEPRECATED - CORS is now handled INSIDE the SecurityFilterChain
 *
 * This standalone CorsFilter bean was causing a critical conflict with Spring Security:
 * - The CorsFilter bean was processed OUTSIDE the security filter chain
 * - This caused POST requests to /api/auth/** to be blocked with 401
 *   even though permitAll() was configured in SecurityConfigSecure
 * - Root cause: dual CORS processing (standalone filter + security chain CORS)
 *
 * CORS is now configured via corsConfigurationSource() in:
 * - SecurityConfig.java (active security config)
 *
 * DO NOT RE-ENABLE THIS CLASS. If CORS settings need to change,
 * update the corsConfigurationSource() method in SecurityConfig.
 *
 * @author ReadyRoad Team
 * @since 2026-01-18
 * @deprecated 2026-01-30 - Replaced by integrated CORS in SecurityFilterChain
 */
// @Configuration  ← DISABLED: Do NOT re-enable. See Javadoc above.
@Deprecated
public class CorsConfig {

    // DISABLED: CorsFilter bean removed to prevent conflict with SecurityFilterChain CORS
    // See SecurityConfigSecure.corsConfigurationSource() for the active CORS configuration
}

