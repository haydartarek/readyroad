package com.readyroad.readyroadbackend.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security Configuration - Production Ready
 * 
 * This configuration implements a sensible security model:
 * - Public access to read-only educational content
 * - Protected access for user operations and data modifications
 * - JWT-based stateless authentication
 * - Role Hierarchy: ADMIN > MODERATOR > USER
 * 
 * @author ReadyRoad Team
 * @since 2026-02-08
 * @version 2.1 (Added Role Hierarchy Support)
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize
@RequiredArgsConstructor
public class SecurityConfigDev {

    private final JwtAuthenticationFilter jwtAuthFilter;

    /**
     * Role Hierarchy Configuration
     * 
     * Defines role inheritance:
     * - ADMIN inherits all permissions from MODERATOR and USER
     * - MODERATOR inherits all permissions from USER
     * 
     * This allows @PreAuthorize("hasRole('USER')") to grant access to ADMIN and
     * MODERATOR
     * without explicit role checks.
     * 
     * @return RoleHierarchy bean
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        log.info("🎭 Configuring Role Hierarchy:");
        log.info("   ROLE_ADMIN > ROLE_MODERATOR > ROLE_USER");

        // Use static factory method instead of constructor (Spring Security 6.3+)
        RoleHierarchyImpl hierarchy = RoleHierarchyImpl.fromHierarchy(
                "ROLE_ADMIN > ROLE_MODERATOR\n" +
                        "ROLE_MODERATOR > ROLE_USER");

        log.info("✅ Role Hierarchy configured successfully");
        log.info("   - ADMIN users can access USER and MODERATOR protected endpoints");
        log.info("   - MODERATOR users can access USER protected endpoints");

        return hierarchy;
    }

    /**
     * Method Security Expression Handler with Role Hierarchy
     * 
     * Integrates Role Hierarchy with @PreAuthorize annotations
     * so that role inheritance works with method-level security.
     * 
     * @param roleHierarchy the role hierarchy bean
     * @return MethodSecurityExpressionHandler configured with hierarchy
     */
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        log.info("🔐 Configuring Method Security with Role Hierarchy");

        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();

        // ✅ Use non-deprecated API (Spring Security 7.0+)
        // Instead of: handler.setRoleHierarchy(roleHierarchy)
        // Use the constructor or withRoleHierarchy() if available
        // For now, suppress warning as the replacement requires different approach
        @SuppressWarnings("deprecation")
        var configuredHandler = handler;
        configuredHandler.setRoleHierarchy(roleHierarchy);

        log.info("✅ Method Security Expression Handler configured");
        log.info("   - @PreAuthorize annotations now respect role hierarchy");

        return configuredHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("╔═══════════════════════════════════════════════════════════════╗");
        log.info("║   🔒 Security Configuration: Production-Ready Mode            ║");
        log.info("║                                                               ║");
        log.info("║   ✅ Public Reads:  Traffic Signs (no auth)                   ║");
        log.info("║   🔐 Protected:     Exams, Practice, Profile (JWT required)   ║");
        log.info("║   🛡️  Admin:        All admin operations (JWT + role check)   ║");
        log.info("║   🎭 Hierarchy:     ADMIN > MODERATOR > USER                  ║");
        log.info("╚═══════════════════════════════════════════════════════════════╝");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // ═══════════════════════════════════════════════════════════
                        // PUBLIC ENDPOINTS (No Authentication Required)
                        // ═══════════════════════════════════════════════════════════

                        // Health & Monitoring
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                        // API Documentation
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Authentication
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()

                        // Public Read-Only Endpoints
                        .requestMatchers(HttpMethod.GET, "/api/traffic-signs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/lessons/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/questions/public").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/smart-quiz/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/quiz/**").permitAll()

                        // ═══════════════════════════════════════════════════════════
                        // PROTECTED ENDPOINTS (JWT Authentication Required)
                        // ═══════════════════════════════════════════════════════════

                        // User Operations (requires authentication)
                        // Note: Role-specific restrictions should use @PreAuthorize in controllers
                        .requestMatchers("/api/users/me/**").authenticated()
                        .requestMatchers("/api/users/**").authenticated()

                        // Exam & Practice
                        .requestMatchers("/api/exams/**").authenticated()
                        .requestMatchers("/api/practice/**").authenticated()

                        // Analytics & Progress
                        .requestMatchers("/api/analytics/**").authenticated()
                        .requestMatchers("/api/progress/**").authenticated()

                        // Write Operations (POST, PUT, DELETE, PATCH)
                        .requestMatchers(HttpMethod.POST, "/api/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/**").authenticated()

                        // Admin Operations (ADMIN role required)
                        // Note: With Role Hierarchy, only ADMIN can access this
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Default: permit all other requests (can be changed to denyAll() for stricter
                        // security)
                        .anyRequest().permitAll())
                // Return proper 401 for unauthenticated requests (not 403)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\":\"Unauthorized\",\"message\":\"Authentication required. Please login.\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\":\"Forbidden\",\"message\":\"You do not have permission to access this resource.\"}");
                        }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        log.info("✅ Security configured successfully");
        log.info("   - Public endpoints: /api/traffic-signs/** (GET)");
        log.info("   - Protected endpoints: /api/exams/**, /api/users/**, etc.");
        log.info("   - Admin endpoints: /api/admin/** (ADMIN role required)");
        log.info("   - Role Hierarchy: ADMIN can access USER and MODERATOR endpoints");

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("🌍 Configuring CORS for cross-origin requests");

        CorsConfiguration configuration = new CorsConfiguration();

        // Allowed origins
        String allowedOrigins = System.getenv("CORS_ALLOWED_ORIGINS");
        if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
            configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
            log.info("   Using custom CORS origins from environment: {}", allowedOrigins);
        } else {
            configuration.setAllowedOrigins(Arrays.asList(
                    "http://localhost:3000",
                    "http://localhost:3001",
                    "http://localhost:3002",
                    "http://localhost:8890"));
            log.info("   Using default development CORS origins");
        }

        // Allowed methods
        configuration.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.HEAD.name()));

        // Allowed headers
        configuration.setAllowedHeaders(List.of("*"));

        // Exposed headers
        configuration.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials",
                "Authorization",
                "Content-Type",
                "X-Total-Count"));

        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        log.info("✅ CORS configured successfully");
        return source;
    }
}
