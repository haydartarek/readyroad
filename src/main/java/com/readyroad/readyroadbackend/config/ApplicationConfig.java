package com.readyroad.readyroadbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Application Configuration
 *
 * Central configuration for application-wide beans:
 * - Security components (UserDetailsService, PasswordEncoder)
 * - JSON serialization (ObjectMapper with Java 8 Date/Time and Hibernate
 * support)
 *
 * Best Practices Applied:
 * - Constructor injection via @RequiredArgsConstructor
 * - @Primary on ObjectMapper to override Spring Boot defaults
 * - Explicit configuration for transparent behavior
 * - Comprehensive JavaDoc for maintainability
 *
 * @author ReadyRoad Team
 * @since 2026-01-18
 * @updated 2026-02-05 - Added Hibernate5Module for lazy proxy serialization
 */
@Slf4j
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    /**
     * UserDetailsService bean for Spring Security authentication.
     * 
     * Loads user details from database by username or email.
     * Throws UsernameNotFoundException if user not found.
     *
     * @return UserDetailsService implementation using UserRepository
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return identifier -> {
            log.debug("Loading user details for identifier: {}", identifier);
            return userRepository.findByUsernameOrEmailIgnoreCase(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));
        };
    }

    /**
     * BCrypt password encoder for secure password hashing.
     * 
     * Uses BCrypt hashing algorithm with default strength (10 rounds).
     * Industry standard for password storage.
     *
     * @return BCrypt password encoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication manager for processing authentication requests.
     * 
     * Central entry point for Spring Security authentication.
     * Delegates to Spring Security's DaoAuthenticationProvider, auto-configured
     * from the UserDetailsService and PasswordEncoder beans above.
     *
     * @param config Spring's authentication configuration
     * @return AuthenticationManager instance
     * @throws Exception if authentication manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Custom ObjectMapper with Java 8 Date/Time support.
     *
     * Configured modules:
     * - JavaTimeModule: Handles Java 8 Date/Time types (LocalDateTime, LocalDate,
     * Instant, etc.)
     *
     * Serialization features:
     * - ISO-8601 date format (e.g., "2026-02-05T10:54:00") instead of Unix
     * timestamps
     * - Empty beans allowed to prevent serialization errors on simple entities
     *
     * Note: Lazy loading is handled by @EntityGraph and @JsonIgnore annotations,
     * not by Jackson modules (Hibernate 7 compatibility).
     *
     * @return Configured ObjectMapper instance
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        log.info("🔧 Configuring custom ObjectMapper with Java 8 Date/Time support");

        ObjectMapper mapper = new ObjectMapper();

        // Java 8 Date/Time support (LocalDateTime, LocalDate, etc.)
        mapper.registerModule(new JavaTimeModule());
        log.debug("✅ Registered JavaTimeModule for Java 8 Date/Time serialization");

        // Date formatting: ISO-8601 strings instead of numeric timestamps
        // Example: "2026-02-05T10:54:00" vs 1738753640000
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Allow serialization of empty beans (entities with no getters)
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        log.info("✅ ObjectMapper configured successfully");
        return mapper;
    }

    /**
     * In-memory cache manager for mostly-static reference data (e.g. categories).
     * Uses Spring's built-in ConcurrentMapCacheManager — no external dependency
     * needed.
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("categories");
    }
}
