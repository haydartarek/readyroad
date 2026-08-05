package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.AuthProvider;
import com.readyroad.readyroadbackend.domain.repository.AuthIdentityRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.dto.AuthResponse;
import com.readyroad.readyroadbackend.dto.ForgotPasswordRequest;
import com.readyroad.readyroadbackend.dto.GoogleAuthExchangeRequest;
import com.readyroad.readyroadbackend.dto.LoginRequest;
import com.readyroad.readyroadbackend.dto.RegisterRequest;
import com.readyroad.readyroadbackend.dto.ResetPasswordRequest;
import com.readyroad.readyroadbackend.service.AuthService;
import com.readyroad.readyroadbackend.service.AdminSystemSettingsService;
import com.readyroad.readyroadbackend.service.BackendMessageService;
import com.readyroad.readyroadbackend.service.PasswordResetService;
import com.readyroad.readyroadbackend.service.SocialAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Controller
 *
 * Endpoints:
 * - POST /api/auth/register - Register new user
 * - POST /api/auth/login - Login user
 * - GET /api/auth/me - Get current user info (requires JWT)
 *
 * @author ReadyRoad Team
 * @since 2026-01-18
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final AuthIdentityRepository authIdentityRepository;
    private final PasswordResetService passwordResetService;
    private final AdminSystemSettingsService adminSystemSettingsService;
    private final SocialAuthService socialAuthService;
    private final BackendMessageService messages;

    /**
     * Register a new user
     *
     * @param request Registration request
     * @return AuthResponse with JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (!adminSystemSettingsService.areRegistrationsAllowed()) {
            return errorResponse(HttpStatus.FORBIDDEN, messages.get("auth.register.disabled"));
        }
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Login user
     *
     * @param request Login request
     * @return AuthResponse with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            log.warn("Login failed for identifier={}: {}", request.getUsername(), e.getMessage());
            return errorResponse(HttpStatus.UNAUTHORIZED, messages.get("auth.login.invalid_credentials"));
        }
    }

    @PostMapping("/google/exchange")
    public ResponseEntity<AuthResponse> exchangeGoogleCode(@Valid @RequestBody GoogleAuthExchangeRequest request) {
        AuthResponse response = socialAuthService.authenticateWithGoogle(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get current authenticated user info
     *
     * Requires valid JWT token in Authorization header
     *
     * @return Current user information
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return errorResponse(HttpStatus.UNAUTHORIZED, messages.get("auth.not_authenticated"));
        }

        // Principal can be User (from JWT filter) or String (username)
        // Handle both cases safely to prevent ClassCastException
        Object principal = authentication.getPrincipal();
        User user;

        if (principal instanceof User) {
            user = (User) principal;
        } else if (principal instanceof String username) {
            // Fallback: load user from database by username or email identifier
            user = userRepository.findByUsernameOrEmailIgnoreCase(username)
                    .orElse(null);
            if (user == null) {
                return errorResponse(HttpStatus.NOT_FOUND, messages.get("auth.user_not_found"));
            }
        } else {
            return errorResponse(HttpStatus.UNAUTHORIZED, messages.get("auth.not_authenticated"));
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("fullName", user.getFullName());
        userInfo.put("role", user.getRole());
        userInfo.put("preferredLanguage", user.getPreferredLanguage());
        userInfo.put("isActive", user.getIsActive());
        userInfo.put("emailVerified", user.getEmailVerified());
        userInfo.put("createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        userInfo.put(
                "linkedProviders",
                authIdentityRepository.findByUserId(user.getId()).stream()
                        .map(identity -> identity.getProvider().name())
                        .sorted()
                        .toList());
        userInfo.put("googleLinked",
                authIdentityRepository.existsByUserIdAndProvider(user.getId(), AuthProvider.GOOGLE));

        return ResponseEntity.ok(userInfo);
    }

    /**
     * Health check for auth endpoints
     *
     * @return Status message
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", messages.get("auth.health.service"));
        return ResponseEntity.ok(response);
    }

    /**
     * Forgot password — step 1
     *
     * Always returns 200 OK regardless of whether the email exists
     * to prevent user-enumeration attacks.
     *
     * POST /api/auth/forgot-password
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", messages.get("auth.forgot_password.sent")));
    }

    /**
     * Reset password — step 2
     *
     * POST /api/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", messages.get("auth.reset_password.updated")));
        } catch (IllegalArgumentException ex) {
            return errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    private ResponseEntity<Map<String, String>> errorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(errorBody(message));
    }

    private Map<String, String> errorBody(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        error.put("message", message);
        return error;
    }
}
