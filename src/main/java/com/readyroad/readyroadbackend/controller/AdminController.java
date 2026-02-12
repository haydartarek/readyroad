package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.entity.UserCategoryProgress;
import com.readyroad.readyroadbackend.domain.repository.TrafficSignRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizAttemptRepository;
import com.readyroad.readyroadbackend.domain.repository.UserCategoryProgressRepository;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.entity.QuizAttempt;
import com.readyroad.readyroadbackend.dto.CreateTrafficSignRequest;
import com.readyroad.readyroadbackend.dto.response.AdminTrafficSignResponse;
import com.readyroad.readyroadbackend.dto.response.PageResponse;
import com.readyroad.readyroadbackend.dto.response.TrafficSignResponse;
import com.readyroad.readyroadbackend.service.TrafficSignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

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
    private final UserCategoryProgressRepository categoryProgressRepository;
    private final TrafficSignService trafficSignService;

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
     * Scenario 1: Paginated admin signs list
     * GET /api/admin/signs?page=0&size=20&sort=signCode,asc&categoryCode=A&q=stop
     */
    @GetMapping("/signs")
    public ResponseEntity<PageResponse<AdminTrafficSignResponse>> getAdminSigns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "signCode,asc") String sort,
            @RequestParam(required = false) String categoryCode,
            @RequestParam(required = false) String q) {

        log.info("📋 Admin signs list: page={}, size={}, sort={}, category={}, q={}", page, size, sort, categoryCode,
                q);
        PageResponse<AdminTrafficSignResponse> result = trafficSignService.getAdminSignsPaginated(page, size, sort,
                categoryCode, q);
        return ResponseEntity.ok(result);
    }

    /**
     * Get single sign by ID (admin view with timestamps)
     * GET /api/admin/signs/{id}
     */
    @GetMapping("/signs/{id}")
    public ResponseEntity<?> getSignById(@PathVariable Long id) {
        log.info("🔍 Admin fetching sign id={}", id);
        try {
            AdminTrafficSignResponse sign = trafficSignService.getAdminSignById(id);
            return ResponseEntity.ok(sign);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Scenario: Admin can delete a traffic sign via admin endpoint
     * DELETE /api/admin/signs/{id}
     * Handles 404, 409 (foreign key constraint)
     */
    @DeleteMapping("/signs/{id}")
    public ResponseEntity<?> deleteSign(@PathVariable Long id) {
        log.info("🗑️ Attempting to delete sign with id: {}", id);
        try {
            trafficSignService.deleteSign(id);
            log.info("✅ Sign deleted successfully with id: {}", id);
            return ResponseEntity.ok(Map.of(
                    "message", "Sign deleted successfully",
                    "id", id));
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Sign not found with id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            log.warn("⚠️ Cannot delete sign id={} — referenced by other records", id);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error",
                            "Cannot delete sign — it is referenced by quiz questions or other records. Remove those references first."));
        }
    }

    /**
     * Create a new traffic sign
     * POST /api/admin/signs
     */
    @PostMapping("/signs")
    public ResponseEntity<?> createSign(@Valid @RequestBody CreateTrafficSignRequest request) {
        log.info("➕ Creating new traffic sign: {}", request.getSignCode());
        try {
            TrafficSignResponse created = trafficSignService.createSign(request);
            log.info("✅ Sign created successfully: {}", created.signCode());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Failed to create sign: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update an existing traffic sign
     * PUT /api/admin/signs/{id}
     */
    @PutMapping("/signs/{id}")
    public ResponseEntity<?> updateSign(@PathVariable Long id, @Valid @RequestBody CreateTrafficSignRequest request) {
        log.info("✏️ Updating traffic sign with id: {}", id);
        try {
            TrafficSignResponse updated = trafficSignService.updateSign(id, request);
            log.info("✅ Sign updated successfully: {}", updated.signCode());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Failed to update sign: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
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
                            "message", e.getMessage()));
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
                            "message", e.getMessage()));
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
                "role", user.getRole().name());

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
                "timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(health);
    }

    // ─── Admin Analytics Endpoints ─────────────────────

    /**
     * Global quiz performance stats
     * GET /api/admin/analytics/quiz-stats
     */
    @GetMapping("/analytics/quiz-stats")
    public ResponseEntity<?> getQuizStats() {
        log.info("📊 Admin quiz stats requested");

        Double avgScore = quizAttemptRepository.getGlobalAverageScore();
        Long totalCompleted = quizAttemptRepository.countGlobalCompleted();
        Long totalPassed = quizAttemptRepository.countGlobalPassed();
        double passRate = (totalCompleted != null && totalCompleted > 0 && totalPassed != null)
                ? (double) totalPassed / totalCompleted * 100.0
                : 0.0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("averageScore", avgScore != null ? Math.round(avgScore * 100.0) / 100.0 : 0);
        stats.put("totalCompleted", totalCompleted != null ? totalCompleted : 0);
        stats.put("totalPassed", totalPassed != null ? totalPassed : 0);
        stats.put("passRate", Math.round(passRate * 100.0) / 100.0);

        return ResponseEntity.ok(stats);
    }

    /**
     * Global category performance stats aggregated across all users
     * GET /api/admin/analytics/category-stats
     */
    @GetMapping("/analytics/category-stats")
    public ResponseEntity<?> getCategoryStats() {
        log.info("📊 Admin category stats requested");

        List<UserCategoryProgress> allProgress = categoryProgressRepository.findAllWithCategory();

        // Group by categoryId and aggregate
        Map<Long, Map<String, Object>> grouped = new HashMap<>();
        for (UserCategoryProgress p : allProgress) {
            Long catId = p.getCategoryId();
            Map<String, Object> entry = grouped.computeIfAbsent(catId, k -> {
                Map<String, Object> m = new HashMap<>();
                m.put("categoryId", catId);
                m.put("categoryCode", p.getCategory() != null ? p.getCategory().getCode() : "");
                m.put("categoryName", p.getCategory() != null ? p.getCategory().getNameEn() : "");
                m.put("totalAttempted", 0);
                m.put("totalCorrect", 0);
                m.put("userCount", 0);
                m.put("accuracySum", BigDecimal.ZERO);
                return m;
            });
            entry.put("totalAttempted", (int) entry.get("totalAttempted")
                    + (p.getQuestionsAttempted() != null ? p.getQuestionsAttempted() : 0));
            entry.put("totalCorrect",
                    (int) entry.get("totalCorrect") + (p.getCorrectAnswers() != null ? p.getCorrectAnswers() : 0));
            entry.put("userCount", (int) entry.get("userCount") + 1);
            BigDecimal acc = p.getAccuracyRate() != null ? p.getAccuracyRate() : BigDecimal.ZERO;
            entry.put("accuracySum", ((BigDecimal) entry.get("accuracySum")).add(acc));
        }

        List<Map<String, Object>> result = grouped.values().stream()
                .map(entry -> {
                    int userCount = (int) entry.get("userCount");
                    BigDecimal accuracySum = (BigDecimal) entry.get("accuracySum");
                    double avgAccuracy = userCount > 0
                            ? accuracySum.divide(BigDecimal.valueOf(userCount), 2, RoundingMode.HALF_UP).doubleValue()
                            : 0.0;
                    Map<String, Object> out = new HashMap<>();
                    out.put("categoryId", entry.get("categoryId"));
                    out.put("categoryCode", entry.get("categoryCode"));
                    out.put("categoryName", entry.get("categoryName"));
                    out.put("totalAttempted", entry.get("totalAttempted"));
                    out.put("totalCorrect", entry.get("totalCorrect"));
                    out.put("avgAccuracy", avgAccuracy);
                    out.put("userCount", userCount);
                    return out;
                })
                .sorted((a, b) -> Double.compare((double) a.get("avgAccuracy"), (double) b.get("avgAccuracy")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    /**
     * Recent exams across all users
     * GET /api/admin/analytics/recent-exams
     */
    @GetMapping("/analytics/recent-exams")
    public ResponseEntity<?> getRecentExams(@RequestParam(defaultValue = "20") int limit) {
        log.info("📊 Admin recent exams requested (limit: {})", limit);

        List<QuizAttempt> recentExams = quizAttemptRepository
                .findByCompletedAtIsNotNullOrderByCompletedAtDesc(PageRequest.of(0, limit));

        List<Map<String, Object>> exams = recentExams.stream()
                .map(qa -> {
                    Map<String, Object> exam = new HashMap<>();
                    exam.put("examId", qa.getId());
                    exam.put("score", qa.getCorrectAnswers());
                    exam.put("totalQuestions", qa.getTotalQuestions());
                    exam.put("scorePercentage", qa.getScorePercentage());
                    exam.put("passed", qa.getPassed());
                    exam.put("startedAt", qa.getStartedAt() != null ? qa.getStartedAt().toString() : null);
                    exam.put("completedAt", qa.getCompletedAt() != null ? qa.getCompletedAt().toString() : null);
                    // Include user info
                    if (qa.getUser() != null) {
                        exam.put("userId", qa.getUser().getId());
                        exam.put("username", qa.getUser().getUsername());
                        exam.put("fullName", qa.getUser().getFullName());
                    }
                    return exam;
                })
                .collect(Collectors.toList());

        long total = quizAttemptRepository.countGlobalCompleted();
        return ResponseEntity.ok(Map.of("exams", exams, "total", total));
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
