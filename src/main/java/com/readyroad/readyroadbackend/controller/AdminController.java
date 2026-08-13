package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.domain.repository.UserCategoryProgressRepository;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.SignExamResultRepository;
import com.readyroad.readyroadbackend.domain.repository.SignPracticeSessionRepository;
import com.readyroad.readyroadbackend.domain.repository.SignQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.SignRandomPracticeSessionRepository;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.entity.SignRandomPracticeSession;
import com.readyroad.readyroadbackend.dto.AdminSystemSettingsUpdateRequest;
import com.readyroad.readyroadbackend.dto.AdminQuizQuestionRequest;
import com.readyroad.readyroadbackend.dto.AdminCreateUserRequest;
import com.readyroad.readyroadbackend.dto.SignGovernanceReport;
import com.readyroad.readyroadbackend.dto.response.AdminQuizQuestionResponse;
import com.readyroad.readyroadbackend.dto.response.AdminQuizCategoryResponse;
import com.readyroad.readyroadbackend.dto.response.AdminSystemSettingsResponse;
import com.readyroad.readyroadbackend.dto.response.AdminTrafficSignResponse;
import com.readyroad.readyroadbackend.dto.response.PageResponse;
import com.readyroad.readyroadbackend.service.AdminQuizService;
import com.readyroad.readyroadbackend.service.AdminUserService;
import com.readyroad.readyroadbackend.service.AdminSystemSettingsService;
import com.readyroad.readyroadbackend.service.BackendMessageService;
import com.readyroad.readyroadbackend.service.TrafficSignService;
import com.readyroad.readyroadbackend.service.SignGovernanceService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.readyroad.readyroadbackend.service.FileUploadService;
import com.readyroad.readyroadbackend.service.NotificationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import javax.sql.DataSource;

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

    private final RoadSignRepository signRepository;
    private final UserRepository userRepository;
    private final ExamSimulationRepository examSimulationRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final SignQuestionRepository signQuestionRepository;
    private final SignPracticeSessionRepository signPracticeSessionRepository;
    private final SignExamResultRepository signExamResultRepository;
    private final SignRandomPracticeSessionRepository signRandomPracticeSessionRepository;
    private final UserCategoryProgressRepository categoryProgressRepository;
    private final TrafficSignService trafficSignService;
    private final AdminQuizService adminQuizService;
    private final AdminUserService adminUserService;
    private final FileUploadService fileUploadService;
    private final SignGovernanceService signGovernanceService;
    private final NotificationService notificationService;
    private final AdminSystemSettingsService adminSystemSettingsService;
    private final BackendMessageService messages;
    private final DataSource dataSource;

    /**
     * Scenario: Admin dashboard returns aggregated stats
     * GET /api/admin/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        log.info("📊 Admin dashboard accessed");

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSigns", signRepository.countByIsActiveTrue());
        stats.put("totalUsers", userRepository.count());
        stats.put("totalQuizAttempts", examSimulationRepository.countStudentExamsByStatus(ExamSimulation.ExamStatus.COMPLETED));
        Map<String, Long> difficultyCounts = new java.util.LinkedHashMap<>();
        difficultyCounts.put("EASY",
                quizQuestionRepository.countByDifficultyLevel(QuizQuestion.DifficultyLevel.EASY));
        difficultyCounts.put("MEDIUM",
                quizQuestionRepository.countByDifficultyLevel(QuizQuestion.DifficultyLevel.MEDIUM));
        difficultyCounts.put("HARD",
                quizQuestionRepository.countByDifficultyLevel(QuizQuestion.DifficultyLevel.HARD));
        difficultyCounts.put("UNCLASSIFIED", quizQuestionRepository.countByDifficultyLevelIsNull());
        stats.put("quizQuestionDifficultyCounts", difficultyCounts);
        stats.put("totalQuizQuestions", quizQuestionRepository.count());
        stats.put("totalSignQuestions", signQuestionRepository.count());
        stats.put("totalSignPracticeSessions", signPracticeSessionRepository.countStudentSessions());
        stats.put("totalSignExamAttempts", signExamResultRepository.countStudentResults());
        stats.put("totalPassedSignExamResults", signExamResultRepository.countPassedStudentResults());
        stats.put("totalRandomSignExamAttempts",
                signRandomPracticeSessionRepository.countStudentSessionsByStatus(
                        SignRandomPracticeSession.SessionStatus.COMPLETED));
        stats.put("totalPassedRandomSignExamResults", signRandomPracticeSessionRepository.countPassedStudentSessions());
        stats.put("activeUsers", userRepository.countByIsActiveTrue());
        stats.put("adminUsers", userRepository.countByRole(Role.ADMIN));
        stats.put("moderatorUsers", userRepository.countByRole(Role.MODERATOR));

        return ResponseEntity.ok(stats);
    }

    // ─── Image Upload ────────────────────────────────────────

    /**
     * Upload an image file for quiz questions.
     * POST /api/admin/upload/image
     * Accepts multipart/form-data with a "file" part.
     * Returns the URL path of the uploaded image.
     */
    @PostMapping("/upload/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("📤 Image upload request: name={}, size={}, type={}",
                file.getOriginalFilename(), file.getSize(), file.getContentType());
        try {
            String imageUrl = fileUploadService.uploadImage(file);
            return ResponseEntity.ok(Map.of(
                    "url", imageUrl,
                    "filename", file.getOriginalFilename(),
                    "size", file.getSize()));
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Upload validation failed: {}", e.getMessage());
            return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("❌ Upload failed", e);
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, messages.get("admin.upload.failed"));
        }
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
            return errorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // ─── Signs: Governance Audit ─────────────────────────

    /**
     * Run a canonical-source governance audit (signs_import ↔ DB consistency check).
     * GET /api/admin/signs/governance/audit
     * Returns a detailed report of mismatches, orphans and completeness issues.
     */
    @GetMapping("/signs/governance/audit")
    public ResponseEntity<SignGovernanceReport.AuditResult> governanceAudit() {
        log.info("Running canonical governance audit (signs_import vs DB)");
        SignGovernanceReport.AuditResult result = signGovernanceService.audit();
        return ResponseEntity.ok(result);
    }

    // ═══════════════════════════════════════════════════
    // Quiz Questions CRUD
    // ═══════════════════════════════════════════════════

    @GetMapping("/quiz/categories")
    public ResponseEntity<List<AdminQuizCategoryResponse>> getTheoreticalQuizCategories() {
        return ResponseEntity.ok(adminQuizService.getTheoreticalCategories());
    }

    /**
     * Paginated admin quiz questions list
     * GET
     * /api/admin/quiz/questions?page=0&size=20&sort=createdAt,desc&categoryCode=A&difficulty=EASY&q=traffic
     */
    @GetMapping("/quiz/questions")
    public ResponseEntity<PageResponse<AdminQuizQuestionResponse>> getAdminQuizQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) String categoryCode,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String hasImage,
            @RequestParam(required = false) String q) {

        log.info(
                "📋 Admin quiz questions list: page={}, size={}, sort={}, category={}, difficulty={}, hasImage={}, q={}",
                page, size, sort, categoryCode, difficulty, hasImage, q);
        PageResponse<AdminQuizQuestionResponse> result = adminQuizService.getQuestionsPaginated(page, size, sort,
                categoryCode, difficulty, hasImage, q);
        return ResponseEntity.ok(result);
    }

    /**
     * Get single quiz question by ID (admin view)
     * GET /api/admin/quiz/questions/{id}
     */
    @GetMapping("/quiz/questions/{id}")
    public ResponseEntity<?> getQuizQuestionById(@PathVariable Long id) {
        log.info("🔍 Admin fetching quiz question id={}", id);
        try {
            AdminQuizQuestionResponse question = adminQuizService.getQuestionById(id);
            return ResponseEntity.ok(question);
        } catch (IllegalArgumentException e) {
            return errorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Create a new quiz question
     * POST /api/admin/quiz/questions
     */
    @PostMapping("/quiz/questions")
    public ResponseEntity<?> createQuizQuestion(@Valid @RequestBody AdminQuizQuestionRequest request) {
        log.info("➕ Creating new quiz question");
        try {
            AdminQuizQuestionResponse created = adminQuizService.createQuestion(request);
            log.info("✅ Quiz question created id={}", created.id());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Failed to create quiz question: {}", e.getMessage());
            return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (ConstraintViolationException e) {
            log.warn("⚠️ Validation failed creating quiz question: {}", e.getMessage());
            return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Update an existing quiz question
     * PUT /api/admin/quiz/questions/{id}
     */
    @PutMapping("/quiz/questions/{id}")
    public ResponseEntity<?> updateQuizQuestion(@PathVariable Long id,
            @Valid @RequestBody AdminQuizQuestionRequest request) {
        log.info("✏️ Updating quiz question id={}", id);
        try {
            AdminQuizQuestionResponse updated = adminQuizService.updateQuestion(id, request);
            log.info("✅ Quiz question updated id={}", updated.id());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Failed to update quiz question: {}", e.getMessage());
            return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            log.warn("⚠️ Edit blocked for quiz question id={} — {}", id, e.getMessage());
            return errorResponse(HttpStatus.CONFLICT, e.getMessage());
        } catch (ConstraintViolationException e) {
            log.warn("⚠️ Validation failed updating quiz question: {}", e.getMessage());
            return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Delete a quiz question
     * DELETE /api/admin/quiz/questions/{id}
     */
    @DeleteMapping("/quiz/questions/{id}")
    public ResponseEntity<?> deleteQuizQuestion(@PathVariable Long id) {
        log.info("🗑️ Attempting to delete quiz question id={}", id);
        try {
            adminQuizService.deleteQuestion(id);
            log.info("✅ Quiz question deleted id={}", id);
            return ResponseEntity.ok(Map.of(
                    "message", messages.get("admin.quiz.deleted"),
                    "id", id));
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Quiz question not found id={}", id);
            return errorResponse(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            log.warn("⚠️ Cannot delete quiz question id={} — {}", id, e.getMessage());
            return errorResponse(HttpStatus.CONFLICT, e.getMessage());
        } catch (DataIntegrityViolationException e) {
            log.warn("⚠️ Cannot delete quiz question id={} — DB constraint violation", id);
            return errorResponse(HttpStatus.CONFLICT, messages.get("admin.quiz.delete_referenced"));
        }
    }

    @GetMapping("/settings")
    public ResponseEntity<AdminSystemSettingsResponse> getAdminSettings() {
        return ResponseEntity.ok(adminSystemSettingsService.getSettings());
    }

    @PutMapping("/settings")
    public ResponseEntity<AdminSystemSettingsResponse> updateAdminSettings(
            @Valid @RequestBody AdminSystemSettingsUpdateRequest request) {
        try {
            return ResponseEntity.ok(adminSystemSettingsService.updateSettings(request));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @GetMapping("/quiz/correct-answer-distribution")
    public ResponseEntity<?> getCorrectAnswerDistribution() {
        return ResponseEntity.ok(adminQuizService.getCorrectAnswerDistribution());
    }

    /**
     * Get all users (admin only)
     * GET /api/admin/users?page=0&size=20&q=haydar&sortField=createdAt&sortDir=desc
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            log.info("👥 Fetching admin users (page: {}, size: {}, q: {}, sortField: {}, sortDir: {})",
                    page, size, q, sortField, sortDir);

            Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
            String safeSortField = switch (sortField) {
                case "id", "username", "email", "fullName", "role", "createdAt" -> sortField;
                default -> "createdAt";
            };
            int safePage = Math.max(0, page);
            int safeSize = Math.max(1, Math.min(size, 100));
            Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(direction, safeSortField));
            Page<User> usersPage = userRepository.findAdminUsers(q == null ? "" : q.trim(), pageable);

            List<Map<String, Object>> userDTOs = usersPage.getContent().stream()
                    .map(this::convertToUserDTO)
                    .toList();

            log.info("✅ Retrieved {} users successfully", userDTOs.size());

            return ResponseEntity.ok(Map.of(
                    "users", userDTOs,
                    "total", usersPage.getTotalElements(),
                    "page", usersPage.getNumber(),
                    "size", usersPage.getSize(),
                    "totalPages", usersPage.getTotalPages(),
                    "query", q == null ? "" : q.trim(),
                    "sortField", safeSortField,
                    "sortDir", direction.name().toLowerCase()));

        } catch (Exception e) {
            log.error("❌ Error fetching users", e);
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, messages.get("admin.internal_server_error"));
        }
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(
            @Valid @RequestBody AdminCreateUserRequest request,
            Principal principal) {
        try {
            User user = adminUserService.createUser(
                    request,
                    principal != null ? principal.getName() : "system");
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToUserDTO(user));
        } catch (IllegalStateException ex) {
            return errorResponse(HttpStatus.CONFLICT, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping("/users/summary")
    public ResponseEntity<Map<String, Object>> getUserSummary() {
        LocalDateTime newSince = LocalDateTime.now().minusDays(7);
        return ResponseEntity.ok(Map.of(
                "total", userRepository.count(),
                "active", userRepository.countByIsActiveTrueAndIsLockedFalse(),
                "locked", userRepository.countByIsLockedTrue(),
                "inactive", userRepository.countByIsActiveFalse(),
                "newThisWeek", userRepository.countByCreatedAtGreaterThanEqual(newSince),
                "newSince", newSince.toString()));
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
                        return errorResponse(HttpStatus.NOT_FOUND, messages.get("auth.user_not_found"));
                    });

        } catch (Exception e) {
            log.error("❌ Error fetching user with id: {}", id, e);
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, messages.get("admin.internal_server_error"));
        }
    }

    /**
     * Update user role
     * PUT /api/admin/users/{id}/role
     */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            Principal principal) {

        log.info("🔄 Updating role for user with id: {}", id);

        String roleStr = request.get("role");
        if (roleStr == null || roleStr.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messages.get("admin.user.role_required"));
        }

        Role newRole;
        try {
            newRole = Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Invalid role provided: {}", roleStr);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messages.get("admin.user.invalid_role"));
        }

        var userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            log.warn("⚠️ User not found with id: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, messages.get("auth.user_not_found"));
        }

        var user = userOpt.get();
        Role oldRole = user.getRole();

        if (oldRole == Role.ADMIN && newRole != Role.ADMIN) {
            if (isCurrentAdmin(principal, user)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        messages.get("admin.user.self_role_change_forbidden"));
            }
            if (userRepository.countByRole(Role.ADMIN) <= 1) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        messages.get("admin.user.last_admin_required"));
            }
        }

        user.setRole(newRole);
        userRepository.save(user);

        log.info("✅ Role updated from {} to {} for user: {}", oldRole, newRole, user.getUsername());

        Map<String, Object> userDto = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole().name());

        return ResponseEntity.ok(Map.of(
                "message", messages.get("admin.user.role_updated"),
                "user", userDto));
    }

    /**
     * Lock/Unlock user account
     * PUT /api/admin/users/{id}/lock
     */
    @PutMapping("/users/{id}/lock")
    public ResponseEntity<?> toggleUserLock(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request,
            Principal principal) {

        log.info("🔒 Toggling lock for user with id: {}", id);

        Boolean isLocked = request.get("isLocked");
        if (isLocked == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messages.get("admin.user.lock_required"));
        }

        var userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            log.warn("⚠️ User not found with id: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, messages.get("auth.user_not_found"));
        }

        var user = userOpt.get();

        if (Boolean.TRUE.equals(isLocked)) {
            if (isCurrentAdmin(principal, user)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        messages.get("admin.user.self_lock_forbidden"));
            }
            if (user.getRole() == Role.ADMIN
                    && userRepository.countByRoleAndIsActiveTrueAndIsLockedFalse(Role.ADMIN) <= 1) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        messages.get("admin.user.last_admin_required"));
            }
        }

        user.setIsLocked(isLocked);
        userRepository.save(user);

        String action = isLocked ? "locked" : "unlocked";
        log.info("✅ User {} successfully {}", user.getUsername(), action);

        return ResponseEntity.ok(Map.of(
                "message", messages.get(isLocked ? "admin.user.locked" : "admin.user.unlocked"),
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
        long timestamp = System.currentTimeMillis();

        try (Connection connection = dataSource.getConnection()) {
            boolean databaseUp = connection.isValid(2);
            Map<String, Object> health = Map.of(
                    "status", databaseUp ? "UP" : "DOWN",
                    "database", databaseUp ? "Connected" : "Unavailable",
                    "timestamp", timestamp);
            return ResponseEntity.status(databaseUp ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE).body(health);
        } catch (Exception exception) {
            log.error("Admin database health check failed", exception);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                    "status", "DOWN",
                    "database", "Unavailable",
                    "timestamp", timestamp));
        }
    }

    // ─── Admin Analytics Endpoints ─────────────────────

    /**
     * Global quiz performance stats
     * GET /api/admin/analytics/quiz-stats
     */
    @GetMapping("/analytics/quiz-stats")
    public ResponseEntity<?> getQuizStats() {
        log.info("📊 Admin exam stats requested");

        Double avgScore = examSimulationRepository.getStudentAverageScoreOfCompleted();
        Long totalCompleted = examSimulationRepository.countStudentExamsByStatus(ExamSimulation.ExamStatus.COMPLETED);
        Long totalPassed = examSimulationRepository.countStudentExamsByStatusAndCorrectAnswersGreaterThanEqual(
                ExamSimulation.ExamStatus.COMPLETED, 41);
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

        List<Object[]> rows = categoryProgressRepository.findCategoryStatsAggregated();

        List<Map<String, Object>> result = rows.stream().map(row -> {
            Map<String, Object> out = new HashMap<>();
            out.put("categoryId", ((Number) row[0]).longValue());
            out.put("categoryCode", row[1] != null ? row[1].toString() : "");
            out.put("categoryName", row[2] != null ? row[2].toString() : "");
            out.put("totalAttempted", ((Number) row[3]).longValue());
            out.put("totalCorrect", ((Number) row[4]).longValue());
            out.put("userCount", ((Number) row[5]).longValue());
            out.put("avgAccuracy", row[6] != null
                    ? BigDecimal.valueOf(((Number) row[6]).doubleValue()).setScale(2, RoundingMode.HALF_UP).doubleValue()
                    : 0.0);
            return out;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    /**
     * Recent exams across all users
     * GET /api/admin/analytics/recent-exams
     *
     * Queries exam_simulations (real Belgian driving license exam sessions),
     * NOT quiz_attempts (practice quiz sessions).
     */
    @GetMapping("/analytics/recent-exams")
    public ResponseEntity<?> getRecentExams(@RequestParam(defaultValue = "20") int limit) {
        log.info("📊 Admin recent exams requested (limit: {})", limit);

        List<ExamSimulation> recentExams = examSimulationRepository
                .findStudentExamsByStatusOrderByCompletedAtDesc(
                        ExamSimulation.ExamStatus.COMPLETED,
                        PageRequest.of(0, limit));

        // Batch-fetch all users in one query to avoid N+1
        List<Long> userIds = recentExams.stream().map(ExamSimulation::getUserId).distinct()
                .collect(Collectors.toList());
        Map<Long, User> userById = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        List<Map<String, Object>> exams = recentExams.stream()
                .map(es -> {
                    Map<String, Object> exam = new HashMap<>();
                    exam.put("examId", es.getId());
                    exam.put("score", es.getCorrectAnswers());
                    exam.put("totalQuestions", es.getTotalQuestions());
                    exam.put("scorePercentage", es.getScorePercentage());
                    exam.put("passed", es.isPassed());
                    exam.put("startedAt", es.getStartedAt() != null ? es.getStartedAt().toString() : null);
                    exam.put("completedAt", es.getCompletedAt() != null ? es.getCompletedAt().toString() : null);
                    exam.put("timeTakenSeconds", es.getTimeTakenSeconds());
                    exam.put("userId", es.getUserId());
                    User u = userById.get(es.getUserId());
                    if (u != null) {
                        exam.put("username", u.getUsername());
                        exam.put("fullName", u.getFullName());
                    }
                    return exam;
                })
                .collect(Collectors.toList());

        long total = examSimulationRepository.countStudentExamsByStatus(ExamSimulation.ExamStatus.COMPLETED);
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
        dto.put("preferredLanguage", user.getPreferredLanguage());
        dto.put("emailVerified", user.getEmailVerified() != null ? user.getEmailVerified() : false);

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

    private boolean isCurrentAdmin(Principal principal, User user) {
        return principal != null
                && principal.getName() != null
                && (principal.getName().equalsIgnoreCase(user.getUsername())
                        || principal.getName().equalsIgnoreCase(user.getEmail()));
    }

    // ═══════════════════════════════════════════════════
    // Quiz Quality Diagnostics
    // ═══════════════════════════════════════════════════

    /**
     * Admin-only diagnostics endpoint: reports quiz question integrity violations.
     * Returns counts of questions failing the 2-3 options Belgian standard,
     * questions with wrong correct-answer counts, inactive/draft anomalies, etc.
     *
     * GET /api/admin/diagnostics/quiz-integrity
     */
    @GetMapping("/diagnostics/quiz-integrity")
    public ResponseEntity<?> getQuizIntegrityReport() {
        log.info("🔬 Admin quiz integrity diagnostics requested");

        long totalQuestions = quizQuestionRepository.count();
        long tooFewOptions = quizQuestionRepository.countQuestionsWithFewerThanTwoOptions();
        long tooManyOptions = quizQuestionRepository.countQuestionsWithMoreThanThreeOptions();
        long zeroCorrect = quizQuestionRepository.countQuestionsWithZeroCorrectOptions();
        long multipleCorrect = quizQuestionRepository.countQuestionsWithMultipleCorrectOptions();
        long noEnglishText = quizQuestionRepository.countQuestionsWithOptionsMissingEnglishText();
        long inactivePublished = quizQuestionRepository.countInactivePublishedQuestions();
        long draftActive = quizQuestionRepository.countActiveDraftQuestions();

        long compliant = totalQuestions - tooFewOptions - tooManyOptions - zeroCorrect - multipleCorrect;

        Map<String, Object> report = new HashMap<>();
        report.put("totalQuestions", totalQuestions);
        report.put("compliant", Math.max(0L, compliant));
        report.put("violations", Map.of(
                "tooFewOptions", tooFewOptions,
                "tooManyOptions", tooManyOptions,
                "zeroCorrectOptions", zeroCorrect,
                "multipleCorrectOptions", multipleCorrect,
                "optionsMissingEnglishText", noEnglishText));
        report.put("anomalies", Map.of(
                "inactiveButPublished", inactivePublished,
                "activeButDraft", draftActive));

        return ResponseEntity.ok(report);
    }

    /**
     * Broadcast a platform-wide alert to all active users.
     * POST /api/admin/notifications/send
     *
     * Body: { "title": "...", "message": "...", "link": "..." }
     */
    @PostMapping("/notifications/send")
    public ResponseEntity<?> sendPlatformNotification(@RequestBody Map<String, String> body) {
        String title = body.get("title");
        String message = body.get("message");
        String link = body.getOrDefault("link", "/");

        if (title == null || title.isBlank() || message == null || message.isBlank()) {
            return errorResponse(HttpStatus.BAD_REQUEST, messages.get("admin.notification.title_message_required"));
        }

        log.info("Admin broadcast notification: title={}", title);
        notificationService.broadcastPlatformAlert(title, message, link);

        return ResponseEntity.ok(Map.of("status", "sent", "title", title));
    }

    private ResponseEntity<Map<String, Object>> errorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(errorBody(message));
    }

    private Map<String, Object> errorBody(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", message);
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now());
        return body;
    }
}
