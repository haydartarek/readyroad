package com.readyroad.readyroadbackend.config;

import jakarta.servlet.http.HttpServletResponse;
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

import java.util.Arrays;
import java.util.List;

/**
 * Secure/Production Security Configuration
 *
 * Active when profile = "secure"
 * JWT authentication REQUIRED for protected endpoints
 *
 * Public endpoints (NO JWT required):
 * - /api/auth/** (register, login, health)
 * - /api/health
 * - /api/traffic-signs/**
 * - /api/search
 * - /actuator/health, /actuator/info
 * - /swagger-ui/**, /v3/api-docs/**
 *
 * Protected endpoints (JWT REQUIRED):
 * - /api/** (all other endpoints)
 *
 * @author ReadyRoad Team
 * @since 2026-01-18
 * @lastModified 2026-01-30 - Root cause fix: explicit method-level matchers + CORS in security chain
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("secure")
@RequiredArgsConstructor
public class SecurityConfigSecure {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (stateless JWT)
                .csrf(AbstractHttpConfigurer::disable)

                // Enable CORS using the bean below
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Authorization rules (ORDER MATTERS: specific matchers BEFORE wildcards)
                .authorizeHttpRequests(auth -> auth
                        // /api/auth/me requires JWT (must come BEFORE /api/auth/**)
                        .requestMatchers("/api/auth/me").authenticated()

                        // Auth endpoints - ALL methods (GET, POST) - login, register, health
                        .requestMatchers("/api/auth/**").permitAll()

                        // Public endpoints
                        .requestMatchers(HttpMethod.GET, "/api/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/traffic-signs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/info").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()

                        // Protected endpoints (require JWT)
                        .requestMatchers("/api/**").authenticated()

                        // Any other request
                        .anyRequest().authenticated()
                )

                // Exception handling
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}");
                        })
                )

                // Stateless session
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authentication provider
                .authenticationProvider(authenticationProvider)

                // JWT filter before UsernamePasswordAuthentication filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS configuration source integrated into the security filter chain.
     * This replaces the separate CorsConfig bean to avoid filter ordering issues.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8081"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "Origin",
                "X-Requested-With"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
