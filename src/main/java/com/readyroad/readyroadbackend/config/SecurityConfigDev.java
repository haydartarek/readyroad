package com.readyroad.readyroadbackend.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Development Security Configuration
 *
 * Active when profile != "secure" (i.e., when secure is NOT active)
 * AND spring.security.mode != "secure"
 *
 * All endpoints are PUBLIC (no authentication required)
 *
 * Mutually exclusive with SecurityConfigSecure and SecurityConfigTest to prevent conflicts.
 *
 * Use for:
 * - Local development
 * - Testing with Postman
 * - Frontend development without authentication
 * - Integration tests in dev mode
 *
 * Run with: mvn spring-boot:run (default)
 * Or: mvn spring-boot:run -Dspring-boot.run.profiles=dev
 *
 * @author ReadyRoad Team
 * @since 2026-01-18
 */
@Configuration
@EnableWebSecurity
@Profile("!secure")  // ✅ FIXED: Only active when "secure" is NOT present
@ConditionalOnProperty(name = "spring.security.mode", havingValue = "dev", matchIfMissing = true)
public class SecurityConfigDev {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Protected endpoints - require authentication
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
