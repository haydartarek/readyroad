package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.repository.TrafficSignRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizAttemptRepository;
import com.readyroad.readyroadbackend.domain.enums.Role;
import lombok.RequiredArgsConstructor;
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
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalSigns", signRepository.count());
        stats.put("totalUsers", userRepository.count());
        stats.put("totalQuizAttempts", quizAttemptRepository.count());

        // Additional useful stats
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
        if (!signRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        signRepository.deleteById(id);

        return ResponseEntity.ok(Map.of(
                "message", "Sign deleted successfully",
                "id", id));
    }

    /**
     * Get all users (admin only)
     * GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var users = userRepository.findAll();

        return ResponseEntity.ok(Map.of(
                "users", users,
                "total", users.size()));
    }

    /**
     * Get user by ID
     * GET /api/admin/users/{id}
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update user role
     * PUT /api/admin/users/{id}/role
     */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String roleStr = request.get("role");

        Role newRole;
        try {
            newRole = Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid role. Must be USER, MODERATOR, or ADMIN"));
        }

        var userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var user = userOpt.get();
        user.setRole(newRole);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "Role updated successfully",
                "user", user));
    }

    /**
     * Get system health metrics
     * GET /api/admin/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();

        health.put("status", "UP");
        health.put("database", "Connected");
        health.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(health);
    }
}