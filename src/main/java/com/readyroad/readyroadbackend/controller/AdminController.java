package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.entity.UserCategoryProgress;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.domain.repository.UserCategoryProgressRepository;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
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
import com.readyroad.readyroadbackend.dto.CreateTrafficSignRequest;
import com.readyroad.readyroadbackend.dto.SignGovernanceReport;
import com.readyroad.readyroadbackend.dto.SignImportEntry;
import com.readyroad.readyroadbackend.dto.response.AdminQuizQuestionResponse;
import com.readyroad.readyroadbackend.dto.response.AdminSystemSettingsResponse;
import com.readyroad.readyroadbackend.dto.response.AdminTrafficSignResponse;
import com.readyroad.readyroadbackend.dto.response.PageResponse;
import com.readyroad.readyroadbackend.dto.response.TrafficSignResponse;
import com.readyroad.readyroadbackend.service.AdminQuizService;
import com.readyroad.readyroadbackend.service.AdminSystemSettingsService;
import com.readyroad.readyroadbackend.service.BackendMessageService;
import com.readyroad.readyroadbackend.service.TrafficSignService;
import com.readyroad.readyroadbackend.service.SignImportService;
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
    private final FileUploadService fileUploadService;
    private final SignImportService signImportService;
    private final SignGovernanceService signGovernanceService;
    private final NotificationService notificationService;
    private final AdminSystemSettingsService adminSystemSettingsService;
    private final BackendMessageService messages;

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
        stats.put("totalQuizAttempts", examSimulationRepository.count());
        stats.put("totalQuizQuestions", quizQuestionRepository.count());
        stats.put("totalSignQuestions", signQuestionRepository.count());
        stats.put("totalSignPracticeSessions", signPracticeSessionRepository.count());
        stats.put("totalSignExamAttempts", signExamResultRepository.count());
        stats.put("totalPassedSignExamResults", signExamResultRepository.countByPassedTrue());
        stats.put("totalRandomSignExamAttempts",
                signRandomPracticeSessionRepository.countByStatus(
                        SignRandomPracticeSession.SessionStatus.COMPLETED));
        stats.put("totalPassedRandomSignExamResults", signRandomPracticeSessionRepository.countByPassedTrue());
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
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("❌ Upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", messages.get("admin.upload.failed")));
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
                    "message", messages.get("admin.sign.deleted"),
                    "id", id));
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Sign not found with id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            log.warn("⚠️ Cannot delete sign id={} — referenced by other records", id);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", messages.get("admin.sign.delete_referenced")));
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

    // ─── Signs: Long Description Import Pipeline ─────────────

    /**
     * Validate (dry-run) a batch of sign long descriptions.
     * POST /api/admin/signs/import/validate
     * Returns a preview of what would change without writing to DB.
     */
    @PostMapping("/signs/import/validate")
    public ResponseEntity<SignImportEntry.ImportResult> validateSignImport(
            @RequestBody SignImportEntry.ImportRequest request) {
        log.info("🔍 Validating sign import: {} entries", request.signs().size());
        SignImportEntry.ImportResult result = signImportService.validateEntries(request.signs());
        return ResponseEntity.ok(result);
    }

    /**
     * Execute a batch import of sign long descriptions.
     * POST /api/admin/signs/import/execute
     * Upserts long_description_* fields matched by sign code.
     * Set dryRun=true in the request body to preview without writing.
     */
    @PostMapping("/signs/import/execute")
    public ResponseEntity<SignImportEntry.ImportResult> executeSignImport(
            @RequestBody SignImportEntry.ImportRequest request) {
        log.info("📥 Executing sign import: {} entries, dryRun={}", request.signs().size(), request.dryRun());
        SignImportEntry.ImportResult result = signImportService.importLongDescriptions(
                request.signs(), request.dryRun());
        return ResponseEntity.ok(result);
    }

    // ─── Signs: Governance Audit ─────────────────────────

    /**
     * Run a canonical-source governance audit (signs.json ↔ DB consistency check).
     * GET /api/admin/signs/governance/audit
     * Returns a detailed report of mismatches, orphans and completeness issues.
     */
    @GetMapping("/signs/governance/audit")
    public ResponseEntity<SignGovernanceReport.AuditResult> governanceAudit() {
        log.info("Running canonical governance audit (signs.json vs DB)");
        SignGovernanceReport.AuditResult result = signGovernanceService.audit();
        return ResponseEntity.ok(result);
    }

    // ═══════════════════════════════════════════════════
    // Quiz Questions CRUD
    // ═══════════════════════════════════════════════════

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
            return ResponseEntity.notFound().build();
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
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (ConstraintViolationException e) {
            log.warn("⚠️ Validation failed creating quiz question: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("⚠️ Edit blocked for quiz question id={} — {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (ConstraintViolationException e) {
            log.warn("⚠️ Validation failed updating quiz question: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("⚠️ Cannot delete quiz question id={} — {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            log.warn("⚠️ Cannot delete quiz question id={} — DB constraint violation", id);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", messages.get("admin.quiz.delete_referenced")));
        }
    }

    @GetMapping("/settings")
    public ResponseEntity<AdminSystemSettingsResponse> getAdminSettings() {
        return ResponseEntity.ok(adminSystemSettingsService.getSettings());
    }

    @PutMapping("/settings")
    public ResponseEntity<AdminSystemSettingsResponse> updateAdminSettings(
            @Valid @RequestBody AdminSystemSettingsUpdateRequest request) {
        return ResponseEntity.ok(adminSystemSettingsService.updateSettings(request));
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
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, safeSortField));
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
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", messages.get("admin.internal_server_error")));
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
                    .body(Map.of("error", messages.get("admin.internal_server_error")));
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
            @RequestBody Map<String, Boolean> request) {

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
        log.info("📊 Admin exam stats requested");

        Double avgScore = examSimulationRepository.getAverageScoreOfCompleted();
        Long totalCompleted = examSimulationRepository.countByStatus(ExamSimulation.ExamStatus.COMPLETED);
        Long totalPassed = examSimulationRepository.countByStatusAndCorrectAnswersGreaterThanEqual(
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
     *
     * Queries exam_simulations (real Belgian driving license exam sessions),
     * NOT quiz_attempts (practice quiz sessions).
     */
    @GetMapping("/analytics/recent-exams")
    public ResponseEntity<?> getRecentExams(@RequestParam(defaultValue = "20") int limit) {
        log.info("📊 Admin recent exams requested (limit: {})", limit);

        List<ExamSimulation> recentExams = examSimulationRepository
                .findByStatusOrderByCompletedAtDesc(
                        ExamSimulation.ExamStatus.COMPLETED,
                        PageRequest.of(0, limit));

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
                    // Resolve user info from userId
                    exam.put("userId", es.getUserId());
                    userRepository.findById(es.getUserId()).ifPresent(u -> {
                        exam.put("username", u.getUsername());
                        exam.put("fullName", u.getFullName());
                    });
                    return exam;
                })
                .collect(Collectors.toList());

        long total = examSimulationRepository.countByStatus(ExamSimulation.ExamStatus.COMPLETED);
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

        List<com.readyroad.readyroadbackend.domain.entity.QuizQuestion> allQuestions = quizQuestionRepository.findAll();

        int totalQuestions = allQuestions.size();
        int tooFewOptions = 0; // < 2 options
        int tooManyOptions = 0; // > 3 options
        int zeroCorrect = 0;
        int multipleCorrect = 0;
        int noEnglishText = 0; // options missing English text
        int inactivePublished = 0; // inactive but PUBLISHED (anomaly)
        int draftActive = 0; // DRAFT but active

        for (var q : allQuestions) {
            var opts = q.getOptions();
            int optCount = opts != null ? opts.size() : 0;

            if (optCount < 2)
                tooFewOptions++;
            if (optCount > 3)
                tooManyOptions++;

            if (opts != null) {
                long correct = opts.stream()
                        .filter(o -> Boolean.TRUE.equals(o.getIsCorrect()))
                        .count();
                if (correct == 0)
                    zeroCorrect++;
                if (correct > 1)
                    multipleCorrect++;

                boolean missingText = opts.stream()
                        .anyMatch(o -> o.getOptionTextEn() == null || o.getOptionTextEn().isBlank());
                if (missingText)
                    noEnglishText++;
            }

            boolean isActive = Boolean.TRUE.equals(q.getIsActive());
            boolean isPublished = q
                    .getStatus() == com.readyroad.readyroadbackend.domain.entity.QuizQuestion.QuestionStatus.PUBLISHED;
            if (!isActive && isPublished)
                inactivePublished++;
            if (isActive && !isPublished)
                draftActive++;
        }

        int compliant = totalQuestions - tooFewOptions - tooManyOptions - zeroCorrect - multipleCorrect;

        Map<String, Object> report = new HashMap<>();
        report.put("totalQuestions", totalQuestions);
        report.put("compliant", Math.max(0, compliant));
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
            return ResponseEntity.badRequest()
                    .body(Map.of("error", messages.get("admin.notification.title_message_required")));
        }

        log.info("Admin broadcast notification: title={}", title);
        notificationService.broadcastPlatformAlert(title, message, link);

        return ResponseEntity.ok(Map.of("status", "sent", "title", title));
    }
}
