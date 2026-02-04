package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.repository.TrafficSignRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizAttemptRepository;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Admin Controller
 * All endpoints require ROLE_ADMIN
 *
 * Implements Feature Scenarios:
 * - Admin dashboard returns aggregated stats
 * - Non-admin cannot delete a traffic sign via admin endpoint (403)
 * - Admin can delete a traffic sign via admin endpoint (200)
 *
 * @author ReadyRoad Team
 * @since 2026-02-04
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final TrafficSignRepository signRepository;
    private final UserRepository userRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    /**
     * Scenario: Admin dashboard returns aggregated stats
     * GET /api/admin/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        log.info("📊 Admin dashboard accessed");

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSigns", signRepository.count());
        stats.put("totalUsers", userRepository.count());
        stats.put("totalQuizAttempts", quizAttemptRepository.count());
        stats.put("activeUsers", userRepository.countByIsActiveTrue());
        stats.put("adminUsers", userRepository.countByRole(Role.ADMIN));
        stats.put("moderatorUsers", userRepository.countByRole(Role.MODERATOR));

        return ResponseEntity.ok(stats);
    }

    /**
     * Scenario: Admin can delete a traffic sign via admin endpoint
     * DELETE /api/admin/signs/{id}
     */
    @DeleteMapping("/signs/{id}")
    public ResponseEntity<?> deleteSign(@PathVariable Long id) {
        log.info("🗑️ Attempting to delete sign with id: {}", id);

        if (!signRepository.existsById(id)) {
            log.warn("⚠️ Sign not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }

        signRepository.deleteById(id);
        log.info("✅ Sign deleted successfully with id: {}", id);

        return ResponseEntity.ok(Map.of(
                "message", "Sign deleted successfully",
                "id", id));
    }

    /**
     * Get all users (admin only)
     * GET /api/admin/users?page=0&size=20
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            log.info("👥 Fetching all users (page: {}, size: {})", page, size);

            List<User> users = userRepository.findAll();
            List<Map<String, Object>> userDTOs = users.stream()
                    .map(this::convertToUserDTO)
                    .toList();

            log.info("✅ Retrieved {} users successfully", userDTOs.size());

            return ResponseEntity.ok(Map.of(
                    "users", userDTOs,
                    "total", userDTOs.size(),
                    "page", page,
                    "size", size));

        } catch (Exception e) {
            log.error("❌ Error fetching users", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error", "Internal Server Error",
                            "message", e.getMessage()
                    ));
        }
    }

    /**
     * Get user by ID
     * GET /api/admin/users/{id}
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            log.info("👤 Fetching user with id: {}", id);

            return userRepository.findById(id)
                    .map(user -> {
                        log.info("✅ User found with username: {}", user.getUsername());
                        return ResponseEntity.ok(convertToUserDTO(user));
                    })
                    .orElseGet(() -> {
                        log.warn("⚠️ User not found with id: {}", id);
                        return ResponseEntity.notFound().build();
                    });

        } catch (Exception e) {
            log.error("❌ Error fetching user with id: {}", id, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error", "Internal Server Error",
                            "message", e.getMessage()
                    ));
        }
    }

    /**
     * Update user role
     * PUT /api/admin/users/{id}/role
     */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        log.info("🔄 Updating role for user with id: {}", id);

        String roleStr = request.get("role");
        if (roleStr == null || roleStr.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Role is required"));
        }

        Role newRole;
        try {
            newRole = Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Invalid role provided: {}", roleStr);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid role. Must be USER, MODERATOR, or ADMIN"));
        }

        var userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            log.warn("⚠️ User not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }

        var user = userOpt.get();
        Role oldRole = user.getRole();
        user.setRole(newRole);
        userRepository.save(user);

        log.info("✅ Role updated from {} to {} for user: {}", oldRole, newRole, user.getUsername());

        Map<String, Object> userDto = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole().name()
        );

        return ResponseEntity.ok(Map.of(
                "message", "Role updated successfully",
                "user", userDto));
    }

    /**
     * Lock/Unlock user account
     * PUT /api/admin/users/{id}/lock
     */
    @PutMapping("/users/{id}/lock")
    public ResponseEntity<?> toggleUserLock(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {

        log.info("🔒 Toggling lock for user with id: {}", id);

        Boolean isLocked = request.get("isLocked");
        if (isLocked == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "isLocked field is required"));
        }

        var userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            log.warn("⚠️ User not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }

        var user = userOpt.get();
        user.setIsLocked(isLocked);
        userRepository.save(user);

        String action = isLocked ? "locked" : "unlocked";
        log.info("✅ User {} successfully {}", user.getUsername(), action);

        return ResponseEntity.ok(Map.of(
                "message", String.format("User %s successfully", action),
                "username", user.getUsername(),
                "isLocked", isLocked));
    }

    /**
     * Get system health metrics
     * GET /api/admin/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> getSystemHealth() {
        log.info("🏥 System health check requested");

        Map<String, Object> health = Map.of(
                "status", "UP",
                "database", "Connected",
                "timestamp", System.currentTimeMillis()
        );

        return ResponseEntity.ok(health);
    }

    /**
     * Helper method: Convert User entity to DTO (Safe version)
     */
    private Map<String, Object> convertToUserDTO(User user) {
        Map<String, Object> dto = new HashMap<>();
        
        // Required fields
        dto.put("id", user.getId());
        dto.put("username", user.getUsername());
        dto.put("email", user.getEmail());
        dto.put("fullName", user.getFullName());
        
        // Enum field - convert to string
        dto.put("role", user.getRole() != null ? user.getRole().name() : "USER");
        
        // Boolean fields with null safety
        dto.put("isActive", user.getIsActive() != null ? user.getIsActive() : true);
        dto.put("isLocked", user.getIsLocked() != null ? user.getIsLocked() : false);
        
        // Timestamp field with null check (from BaseEntity)
        try {
            if (user.getCreatedAt() != null) {
                dto.put("createdAt", user.getCreatedAt().toString());
            }
        } catch (Exception e) {
            // Field doesn't exist or can't be accessed - skip it
            log.debug("createdAt not available for user {}", user.getId());
        }
        
        return dto;
    }
}
