package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.AuthIdentityRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.dto.AuthResponse;
import com.readyroad.readyroadbackend.dto.LoginRequest;
import com.readyroad.readyroadbackend.dto.RegisterRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
    private final AuthIdentityRepository authIdentityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final NotificationService notificationService;
    private final BackendMessageService messages;

    /**
     * Register a new user
     *
     * @param request Registration request with user details
     * @return AuthResponse with JWT token
     * @throws IllegalArgumentException if username or email already exists
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedUsername = request.getUsername().trim();
        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        String normalizedFullName = request.getFullName().trim().replaceAll("\\s+", " ");

        log.info("📝 Registration request for username: {}", normalizedUsername);

        // Check if username already exists
        if (userRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            log.warn("❌ Username already exists: {}", normalizedUsername);
            throw new IllegalArgumentException(messages.get("auth.username_exists"));
        }

        // Check if email already exists
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            log.warn("❌ Email already exists: {}", normalizedEmail);
            throw new IllegalArgumentException(messages.get("auth.email_exists"));
        }

        // Create new user
        User user = new User();
        user.setUsername(normalizedUsername);
        user.setEmail(normalizedEmail);
        user.setFullName(normalizedFullName);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER); // Default role
        user.setIsActive(true);
        user.setIsLocked(false);

        // Save user to database
        user = userRepository.save(user);
        log.info("✅ User registered successfully: {}", user.getUsername());

        // Notify all admins about the new registration
        try {
            notificationService.notifyAdminsNewUser(user.getUsername(), user.getEmail());
        } catch (Exception ex) {
            log.warn("Admin new-user notification failed: {}", ex.getMessage());
        }

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
        String identifier = request.getUsername().trim();
        log.info("Login attempt for identifier={}", identifier);

        try {
            userRepository.findByUsernameOrEmailIgnoreCase(identifier)
                    .orElseThrow(() -> new BadCredentialsException(messages.get("auth.login.invalid_credentials")));

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            identifier,
                            request.getPassword()));
        } catch (Exception e) {
            log.warn("Authentication failed for identifier={}: {}", identifier, e.getMessage());
            throw e;
        }

        User user = userRepository.findByUsernameOrEmailIgnoreCase(identifier)
                .orElseThrow(() -> new IllegalArgumentException(messages.get("auth.user_not_found")));

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        String jwtToken = jwtService.generateToken(claims, user.getUsername());

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
        List<String> linkedProviders = authIdentityRepository.findByUserId(user.getId()).stream()
                .map(identity -> identity.getProvider().name())
                .sorted()
                .toList();

        return AuthResponse.of(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                linkedProviders,
                false);
    }
}
