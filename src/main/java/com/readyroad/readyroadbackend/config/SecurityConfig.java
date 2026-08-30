package com.readyroad.readyroadbackend.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
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
 * @version 2.2 (Renamed from SecurityConfigDev; production security config)
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final MaintenanceModeFilter maintenanceModeFilter;

        @Value("${CORS_ALLOWED_ORIGINS:${app.cors.allowed-origins:}}")
        private String allowedOriginsProperty;

        @Value("${app.cors.max-age:3600}")
        private Long corsMaxAge;

        @Bean
        public RoleHierarchy roleHierarchy() {
                log.info("Configuring Role Hierarchy:");
                log.info("   ROLE_ADMIN > ROLE_MODERATOR > ROLE_USER");

                RoleHierarchyImpl hierarchy = RoleHierarchyImpl.fromHierarchy(
                                "ROLE_ADMIN > ROLE_MODERATOR\n" +
                                                "ROLE_MODERATOR > ROLE_USER");

                log.info("Role Hierarchy configured successfully");
                log.info("   - ADMIN users can access USER and MODERATOR protected endpoints");
                log.info("   - MODERATOR users can access USER protected endpoints");

                return hierarchy;
        }

        @Bean
        @SuppressWarnings("deprecation")
        public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
                log.info("Configuring Method Security with Role Hierarchy");

                DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
                handler.setRoleHierarchy(roleHierarchy);

                log.info("Method Security Expression Handler configured");
                log.info("   - @PreAuthorize annotations now respect role hierarchy");

                return handler;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                log.info("Security Configuration: Production-Ready Mode");
                log.info("   Public reads: Traffic Signs (no auth)");
                log.info("   Protected: Exams, Practice, Profile (JWT required)");
                log.info("   Admin: All admin operations (JWT + role check)");
                log.info("   Hierarchy: ADMIN > MODERATOR > USER");

                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .authorizeHttpRequests(auth -> auth
                                                // Health & Monitoring
                                                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                                                .requestMatchers("/api/health").permitAll()

                                                // API Documentation (admin-only)
                                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").hasRole("ADMIN")

                                                // Public static images served by the backend
                                                .requestMatchers(HttpMethod.GET, "/images/signs/**").permitAll()
                                                .requestMatchers(HttpMethod.HEAD, "/images/signs/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/images/quiz/**").permitAll()
                                                .requestMatchers(HttpMethod.HEAD, "/images/quiz/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/images/articles/**").permitAll()
                                                .requestMatchers(HttpMethod.HEAD, "/images/articles/**").permitAll()

                                                // Authentication
                                                .requestMatchers(
                                                                "/api/auth/login",
                                                                "/api/auth/register",
                                                                "/api/auth/google/exchange",
                                                                "/api/auth/health")
                                                .permitAll()
                                                .requestMatchers("/api/auth/forgot-password",
                                                                "/api/auth/reset-password")
                                                .permitAll()

                                                // Public Read-Only Endpoints
                                                .requestMatchers(HttpMethod.GET, "/api/traffic-signs/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/lessons/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/questions/public").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/quiz/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/traffic-rules/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/search").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/home/stats").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/youtube/videos").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/articles/**").permitAll()

                                                // User Operations
                                                .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
                                                .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()
                                                .requestMatchers("/api/users/me/**").authenticated()
                                                .requestMatchers("/api/users/**").authenticated()

                                                // Exam & Practice
                                                .requestMatchers("/api/exams/**").authenticated()
                                                .requestMatchers("/api/practice/**").authenticated()

                                                // Sign Quiz
                                                .requestMatchers("/api/sign-quiz/**").authenticated()

                                                // Analytics & Progress
                                                .requestMatchers("/api/analytics/**").authenticated()
                                                .requestMatchers("/api/progress/**").authenticated()

                                                // Admin Operations
                                                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                                                // Write Operations
                                                .requestMatchers(HttpMethod.POST, "/api/**").authenticated()
                                                .requestMatchers(HttpMethod.PUT, "/api/**").authenticated()
                                                .requestMatchers(HttpMethod.DELETE, "/api/**").authenticated()
                                                .requestMatchers(HttpMethod.PATCH, "/api/**").authenticated()

                                                // Default deny for unregistered routes.
                                                .anyRequest().denyAll())
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
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterAfter(maintenanceModeFilter, JwtAuthenticationFilter.class);

                log.info("Security configured successfully");
                log.info("   - Public endpoints: /api/traffic-signs/** (GET)");
                log.info("   - Protected endpoints: /api/exams/**, /api/users/**, etc.");
                log.info("   - Admin endpoints: /api/admin/** (ADMIN role required)");
                log.info("   - Role Hierarchy: ADMIN can access USER and MODERATOR endpoints");

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                log.info("Configuring CORS for cross-origin requests");

                CorsConfiguration configuration = new CorsConfiguration();

                if (allowedOriginsProperty != null && !allowedOriginsProperty.isBlank()) {
                        List<String> origins = Arrays.stream(allowedOriginsProperty.split(","))
                                        .map(String::trim)
                                        .filter(origin -> !origin.isEmpty())
                                        .toList();
                        configuration.setAllowedOrigins(origins);
                        log.info("   Using configured CORS origins: {}", origins);
                } else {
                        configuration.setAllowedOrigins(Arrays.asList(
                                        "http://localhost:3000",
                                        "http://localhost:3001",
                                        "http://localhost:3002",
                                        "http://localhost:8890"));
                        log.info("   Using default development CORS origins");
                }

                configuration.setAllowedMethods(Arrays.asList(
                                HttpMethod.GET.name(),
                                HttpMethod.POST.name(),
                                HttpMethod.PUT.name(),
                                HttpMethod.DELETE.name(),
                                HttpMethod.OPTIONS.name(),
                                HttpMethod.PATCH.name(),
                                HttpMethod.HEAD.name()));

                configuration.setAllowedHeaders(List.of("*"));
                configuration.setExposedHeaders(Arrays.asList(
                                "Access-Control-Allow-Origin",
                                "Access-Control-Allow-Credentials",
                                "Authorization",
                                "Content-Type",
                                "X-Total-Count"));

                configuration.setAllowCredentials(true);
                configuration.setMaxAge(corsMaxAge);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);

                log.info("CORS configured successfully");
                return source;
        }
}
