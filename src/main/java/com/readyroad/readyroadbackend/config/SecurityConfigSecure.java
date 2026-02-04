package com.readyroad.readyroadbackend.config;

import com.readyroad.readyroadbackend.config.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * Security Configuration with RBAC
 * 
 * Implements Feature: ReadyRoad RBAC hardening and Admin tooling
 * 
 * Scenarios Covered:
 * - Block non-admin users from admin endpoints (403)
 * - Allow admin users to access admin endpoints (200)
 * - Restrict traffic sign write operations to ADMIN
 * - Restrict data import endpoints to ADMIN
 * - Allow moderator and admin to access moderation endpoints
 * - Preserve public endpoints without role checks
 * 
 * @author ReadyRoad Team
 * @since 2026-02-04
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Profile("secure")
public class SecurityConfigSecure {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ============================================
                // RBAC Authorization Rules
                // ============================================
                .authorizeHttpRequests(auth -> auth
                        // Public auth endpoints - MUST come FIRST
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh", "/api/auth/health").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()

                        // Special case: /api/auth/me requires JWT (but no specific role)
                        .requestMatchers("/api/auth/me").authenticated()

                        // ===== ADMIN-ONLY ENDPOINTS =====
                        // Scenario: Block non-admin users from admin endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/data-import/**").hasRole("ADMIN")

                        // ===== ADMIN-ONLY WRITE OPERATIONS ON TRAFFIC SIGNS =====
                        // Scenario: Restrict traffic sign write operations to ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/traffic-signs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/traffic-signs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/traffic-signs/**").hasRole("ADMIN")

                        // ===== MODERATION ENDPOINTS (MODERATOR + ADMIN) =====
                        // Scenario: Allow moderator and admin to access moderation endpoints
                        .requestMatchers("/api/moderation/**").hasAnyRole("MODERATOR", "ADMIN")

                        // ===== PUBLIC READ ENDPOINTS =====
                        // Scenario: Preserve public endpoints without role checks
                        .requestMatchers(HttpMethod.GET, "/api/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/traffic-signs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/info").permitAll()

                        // Swagger/OpenAPI documentation
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()

                        // Static resources
                        .requestMatchers("/images/**", "/static/**", "/public/**").permitAll()

                        // ===== DEFAULT: All other /api/** require JWT (but no role check) =====
                        // Scenario: Keep the rest of /api protected by JWT only
                        .requestMatchers("/api/**").authenticated()

                        // Catch-all: require authentication
                        .anyRequest().authenticated())

                // ============================================
                // Exception Handling (401 & 403)
                // ============================================
                .exceptionHandling(exception -> exception
                        // 401 Unauthorized - No JWT or invalid JWT
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}");
                        })
                        // 403 Forbidden - JWT valid but insufficient role
                        // Scenario: Response should indicate "Access Denied"
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\":\"Access Denied\",\"message\":\"Insufficient permissions\"}");
                        }))

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
