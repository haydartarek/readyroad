package com.readyroad.readyroadbackend.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Test Security Configuration - Respects spring.security.mode
 *
 * Active when:
 * - Profile = "test" (H2 database)
 * - spring.security.mode = "secure" (JWT enforcement needed)
 *
 * This configuration allows integration tests to verify JWT security
 * behavior while using H2 in-memory database.
 *
 * Public endpoints:
 * - /api/auth/** (register, login)
 * - /actuator/health
 *
 * Protected endpoints:
 * - /api/** (require JWT)
 *
 * @author ReadyRoad Team
 * @since 2026-01-21
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("test")
@ConditionalOnProperty(name = "spring.security.mode", havingValue = "secure")
@RequiredArgsConstructor
public class SecurityConfigTest {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;


    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // /api/auth/me requires JWT (must come BEFORE /api/auth/**)
                        .requestMatchers("/api/auth/me").authenticated()

                        // Public endpoints
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/health",
                                "/actuator/health",
                                "/actuator/info",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Protected endpoints (require JWT)
                        .requestMatchers("/api/**").authenticated()

                        // Any other request
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Return 401 for unauthenticated requests
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                "{\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}"
                            );
                        })
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
