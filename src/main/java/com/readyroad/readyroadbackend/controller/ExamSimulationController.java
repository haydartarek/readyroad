package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationQuestion;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationQuestionRepository;
import com.readyroad.readyroadbackend.dto.exam.ExamStartResponse;
import com.readyroad.readyroadbackend.mapper.ExamMapper;
import com.readyroad.readyroadbackend.service.ExamService;
import com.readyroad.readyroadbackend.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exam Simulation Controller - Alias for /api/exam-simulations
 * This controller provides the same endpoints as ExamController but with a different base path
 * to maintain compatibility with frontend/tests expecting /api/exam-simulations
 */
@RestController
@RequestMapping("/api/exam-simulations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Exam Simulation (Alias)", description = "Alternative endpoints for exam simulation")
@CrossOrigin(origins = "*")
public class ExamSimulationController {

    private final ExamService examService;
    private final ExamSimulationQuestionRepository examQuestionRepository;
    private final ExamMapper examMapper;
    private final AuthenticationUtil authenticationUtil;

    /**
     * Start exam simulation
     * POST /api/exam-simulations/start
     *
     * Supports both authenticated (JWT token) and manual userId parameter
     */
    @PostMapping("/start")
    @Operation(summary = "Start exam simulation")
    public ResponseEntity<ExamStartResponse> startExam(
        Authentication authentication,
        @Parameter(description = "User ID (optional if authenticated)")
        @RequestParam(required = false) Long userId
    ) {
        // Extract userId from JWT token if not provided
        if (userId == null) {
            userId = authenticationUtil.extractUserId(authentication);
            if (userId == null) {
                log.warn("❌ No userId provided and no authentication found");
                return ResponseEntity.badRequest().build();
            }
        }

        log.info("📝 Starting exam simulation for user: {} (via /api/exam-simulations)", userId);

        ExamSimulation exam = examService.startExamSimulation(userId);
        List<ExamSimulationQuestion> questions = examQuestionRepository
            .findByExamIdOrderByQuestionOrder(exam.getId());
        ExamStartResponse response = examMapper.toStartResponse(exam, questions);

        log.info("✅ Exam started: examId={}, userId={}, questions={}",
            exam.getId(), userId, questions.size());

        return ResponseEntity.ok(response);
    }

    /**
     * Get active exam for user
     * GET /api/exam-simulations/active
     *
     * Supports both authenticated (JWT token) and manual userId parameter
     */
    @GetMapping("/active")
    @Operation(summary = "Get active exam")
    public ResponseEntity<Map<String, Object>> getActiveExam(
        Authentication authentication,
        @Parameter(description = "User ID (optional if authenticated)")
        @RequestParam(required = false) Long userId
    ) {
        // Extract userId from JWT token if not provided
        if (userId == null) {
            userId = authenticationUtil.extractUserId(authentication);
            if (userId == null) {
                log.warn("❌ No userId provided and no authentication found");
                return ResponseEntity.badRequest().build();
            }
        }

        log.info("🔍 GET /api/exam-simulations/active - userId: {}", userId);

        ExamSimulation activeExam = examService.getActiveExam(userId);

        if (activeExam == null) {
            log.info("ℹ️ No active exam found for user: {}", userId);
            Map<String, Object> response = new HashMap<>();
            response.put("hasActiveExam", false);
            response.put("activeExam", null);
            return ResponseEntity.ok(response);
        }

        List<ExamSimulationQuestion> questions = examQuestionRepository
            .findByExamIdOrderByQuestionOrder(activeExam.getId());
        ExamStartResponse examResponse = examMapper.toStartResponse(activeExam, questions);

        Map<String, Object> response = new HashMap<>();
        response.put("hasActiveExam", true);
        response.put("activeExam", examResponse);

        log.info("✅ Active exam found: examId={}, userId={}", activeExam.getId(), userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get exam history for user
     * GET /api/exam-simulations/history
     *
     * Supports both authenticated (JWT token) and manual userId parameter
     */
    @GetMapping("/history")
    @Operation(summary = "Get exam history")
    public ResponseEntity<Map<String, Object>> getExamHistory(
        Authentication authentication,
        @Parameter(description = "User ID (optional if authenticated)")
        @RequestParam(required = false) Long userId
    ) {
        // Extract userId from JWT token if not provided
        if (userId == null) {
            userId = authenticationUtil.extractUserId(authentication);
            if (userId == null) {
                log.warn("❌ No userId provided and no authentication found");
                return ResponseEntity.badRequest().build();
            }
        }

        log.info("📜 GET /api/exam-simulations/history - userId: {}", userId);

        List<ExamSimulation> completedExams = examService.getCompletedExams(userId);

        List<Map<String, Object>> examHistory = completedExams.stream()
            .map(exam -> {
                Map<String, Object> examData = new HashMap<>();
                examData.put("examId", exam.getId());
                examData.put("startedAt", exam.getStartedAt());
                examData.put("completedAt", exam.getCompletedAt());
                examData.put("status", exam.getStatus());
                examData.put("scorePercentage", exam.getScorePercentage());
                examData.put("totalQuestions", exam.getTotalQuestions());
                examData.put("correctAnswers", exam.getCorrectAnswers());
                examData.put("passed", exam.isPassed());
                return examData;
            })
            .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("totalExams", examHistory.size());
        response.put("exams", examHistory);

        log.info("✅ Exam history retrieved: userId={}, totalExams={}", userId, examHistory.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel active exam for user
     * DELETE /api/exam-simulations/active
     *
     * Supports both authenticated (JWT token) and manual userId parameter
     */
    @DeleteMapping("/active")
    @Operation(summary = "Cancel active exam")
    public ResponseEntity<Map<String, Object>> cancelActiveExam(
        Authentication authentication,
        @Parameter(description = "User ID (optional if authenticated)")
        @RequestParam(required = false) Long userId
    ) {
        // Extract userId from JWT token if not provided
        if (userId == null) {
            userId = authenticationUtil.extractUserId(authentication);
            if (userId == null) {
                log.warn("❌ No userId provided and no authentication found");
                return ResponseEntity.badRequest().build();
            }
        }

        log.info("🗑️ DELETE /api/exam-simulations/active - userId: {}", userId);

        ExamSimulation activeExam = examService.getActiveExam(userId);

        if (activeExam == null) {
            log.info("ℹ️ No active exam found for user: {}", userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "No active exam to cancel");
            return ResponseEntity.ok(response);
        }

        // Cancel the exam by setting status to CANCELLED or COMPLETED
        examService.cancelExam(activeExam.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Active exam cancelled successfully");
        response.put("cancelledExamId", activeExam.getId());

        log.info("✅ Active exam cancelled: examId={}, userId={}", activeExam.getId(), userId);
        return ResponseEntity.ok(response);
    }
}
