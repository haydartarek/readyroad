package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.dto.AuthResponse;
import com.readyroad.readyroadbackend.dto.LoginRequest;
import com.readyroad.readyroadbackend.dto.RegisterRequest;
import com.readyroad.readyroadbackend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    /**
     * Register a new user
     *
     * @param request Registration request
     * @return AuthResponse with JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
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
        log.info("========================================");
        log.info("🔐 LOGIN REQUEST RECEIVED");
        log.info("========================================");
        log.info("Username: {}", request.getUsername());
        log.info("========================================");

        try {
            log.info("🔄 Calling AuthService.login()...");
            AuthResponse response = authService.login(request);
            log.info("✅ Login successful!");
            log.info("User ID: {}", response.getUserId());
            log.info("========================================");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ LOGIN FAILED");
            log.error("Exception Type: {}", e.getClass().getName());
            log.error("Error Message: {}", e.getMessage());
            log.info("========================================");
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
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
            Map<String, String> error = new HashMap<>();
            error.put("error", "Not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        // Principal can be User (from JWT filter) or String (username)
        // Handle both cases safely to prevent ClassCastException
        Object principal = authentication.getPrincipal();
        User user;

        if (principal instanceof User) {
            user = (User) principal;
        } else if (principal instanceof String username) {
            // Fallback: load user from database by username
            user = userRepository.findByUsername(username)
                    .orElse(null);
            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("fullName", user.getFullName());
        userInfo.put("role", user.getRole());
        userInfo.put("isActive", user.getIsActive());

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
        response.put("service", "Authentication Service");
        return ResponseEntity.ok(response);
    }
}
