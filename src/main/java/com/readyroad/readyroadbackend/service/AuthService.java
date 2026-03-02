package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.dto.AuthResponse;
import com.readyroad.readyroadbackend.dto.LoginRequest;
import com.readyroad.readyroadbackend.dto.RegisterRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication Service
 *
 * Handles user registration and authentication logic
 *
 * @author ReadyRoad Team
 * @since 2026-01-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user
     *
     * @param request Registration request with user details
     * @return AuthResponse with JWT token
     * @throws IllegalArgumentException if username or email already exists
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("📝 Registration request for username: {}", request.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("❌ Username already exists: {}", request.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("❌ Email already exists: {}", request.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER); // Default role
        user.setIsActive(true);
        user.setIsLocked(false);

        // Save user to database
        user = userRepository.save(user);
        log.info("✅ User registered successfully: {}", user.getUsername());

        // Generate JWT token with role claim
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        String jwtToken = jwtService.generateToken(claims, user.getUsername());

        // Build and return response
        return buildAuthResponse(user, jwtToken);
    }

    /**
     * Authenticate user and generate JWT token
     *
     * @param request Login request with username and password
     * @return AuthResponse with JWT token
     * @throws org.springframework.security.core.AuthenticationException if
     *                                                                   credentials
     *                                                                   are invalid
     */
    public AuthResponse login(LoginRequest request) {
        log.info("🔐 Login attempt for username: {}", request.getUsername());
        log.debug("📋 Login request details:");
        log.debug("   - Username: {}", request.getUsername());
        log.debug("   - Password length: {}", request.getPassword() != null ? request.getPassword().length() : 0);

        try {
            // Load user first to check details
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> {
                        log.error("❌ User not found: {}", request.getUsername());
                        return new IllegalArgumentException("User not found");
                    });

            log.info("✅ User found in database: {}", user.getUsername());
            log.debug("📊 User details:");
            log.debug("   - ID: {}", user.getId());
            log.debug("   - Email: {}", user.getEmail());
            log.debug("   - Role: {}", user.getRole());
            log.debug("   - Active: {}", user.getIsActive());
            log.debug("   - Locked: {}", user.getIsLocked());
            log.debug("   - Password Hash (first 30 chars): {}",
                    user.getPasswordHash() != null
                            ? user.getPasswordHash().substring(0, Math.min(30, user.getPasswordHash().length()))
                            : "NULL");
            log.debug("   - Password Hash length: {}",
                    user.getPasswordHash() != null ? user.getPasswordHash().length() : 0);

            // Test password match manually for debugging
            log.info("🔍 Testing password match...");
            boolean matches = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
            log.info("🎯 Password match result: {}", matches);

            if (!matches) {
                log.error("❌ Password does not match for user: {}", request.getUsername());
                // ⚠️ NEVER log passwords or hashes - security violation
            }

            // Authenticate user using Spring Security
            log.info("🔐 Calling AuthenticationManager.authenticate()...");
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));
            log.info("✅ Authentication successful!");

        } catch (Exception e) {
            log.error("❌ Authentication failed for user: {}", request.getUsername());
            log.error("❌ Exception type: {}", e.getClass().getName());
            log.error("❌ Exception message: {}", e.getMessage());
            throw e;
        }

        // Load user from database again (already loaded above, but keeping original
        // flow)
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Generate JWT token with role claim
        log.info("🎫 Generating JWT token for user: {}", user.getUsername());
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        String jwtToken = jwtService.generateToken(claims, user.getUsername());
        log.info("✅ JWT token generated successfully");

        // Build and return response
        return buildAuthResponse(user, jwtToken);
    }

    /**
     * Build authentication response from user and token
     *
     * @param user  User entity
     * @param token JWT token
     * @return AuthResponse
     */
    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.of(token, user.getId(), user.getUsername(), user.getEmail(), user.getFullName(),
                user.getRole());
    }
}
