package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.dto.exam.ExamResultsDTO;
import com.readyroad.readyroadbackend.dto.exam.ExamStartResponse;
import com.readyroad.readyroadbackend.dto.exam.SubmitExamAnswerRequest;
import com.readyroad.readyroadbackend.dto.exam.SubmitExamAnswerResponse;
import com.readyroad.readyroadbackend.service.BackendMessageService;
import com.readyroad.readyroadbackend.service.ExamService;
import com.readyroad.readyroadbackend.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exam Simulation Controller - Phase 5
 * Handles 50-question exam simulation for Belgian driving license
 */
@RestController
@RequestMapping("/api/exams/simulations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Exam Simulation", description = "Theory exam simulation (50 questions, 15 seconds per question)")
public class ExamController {

        private final ExamService examService;
        private final AuthenticationUtil authenticationUtil;
        private final BackendMessageService messages;

        /**
         * Story A1: Start exam simulation
         *
         * POST /api/exams/simulations/start
         */
        @PostMapping("/start")
        @Operation(summary = "Start exam simulation", description = "Start a new 50-question exam simulation with 15 seconds per question. "
                        +
                        "Respects 24h cooldown (Law #1) and adaptive difficulty (Law #2).")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Exam started successfully"),
                        @ApiResponse(responseCode = "400", description = "User already has active exam"),
                        @ApiResponse(responseCode = "409", description = "Insufficient questions available")
        })
        public ResponseEntity<?> startExam() {
                // ✅ SECURITY FIX: Get userId from JWT instead of request param
                Long userId = authenticationUtil.getCurrentUserId();
                log.info("📝 Starting exam simulation for authenticated user: {}", userId);

                ExamStartResponse response = examService.startExamResponse(userId);

                log.info("✅ Exam started: examId={}, userId={}, questions={}",
                                response.getExamId(), userId, response.getQuestions().size());

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        /**
         * Check if user can start exam
         *
         * GET /api/exams/simulations/can-start?userId={userId}
         */
        @GetMapping("/can-start")
        @Operation(summary = "Check if user can start exam", description = "Checks if user has an active exam in progress")
        public ResponseEntity<Map<String, Object>> canStartExam() {
                // ✅ SECURITY FIX: Get userId from JWT
                Long userId = authenticationUtil.getCurrentUserId();

                boolean canStart = examService.canStartExam(userId);
                ExamSimulation activeExam = examService.getActiveExam(userId);

                Map<String, Object> response = new HashMap<>();
                response.put("canStart", canStart);

                if (activeExam != null) {
                        response.put("activeExamId", activeExam.getId());
                        response.put("startedAt", activeExam.getStartedAt());
                        response.put("expiresAt", activeExam.getExpiresAt());
                }

                return ResponseEntity.ok(response);
        }

        /**
         * Get exam by ID
         *
         * GET /api/exams/simulations/{examId}
         */
        @GetMapping("/{examId}")
        @Operation(summary = "Get exam by ID", description = "Retrieve exam simulation details")
        public ResponseEntity<Map<String, Object>> getExam(
                        @Parameter(description = "Exam ID", required = true) @PathVariable Long examId) {
                Long userId = authenticationUtil.getCurrentUserId();
                ExamSimulation exam = examService.getExamById(examId);
                if (!exam.getUserId().equals(userId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }

                Map<String, Object> response = new HashMap<>();
                response.put("examId", exam.getId());
                response.put("userId", exam.getUserId());
                response.put("status", exam.getStatus().name());
                response.put("startedAt", exam.getStartedAt());
                response.put("expiresAt", exam.getExpiresAt());
                response.put("totalQuestions", exam.getTotalQuestions());

                if (exam.isCompleted()) {
                        response.put("completedAt", exam.getCompletedAt());
                        response.put("correctAnswers", exam.getCorrectAnswers());
                        response.put("scorePercentage", exam.getScorePercentage());
                        response.put("passed", exam.isPassed());
                }

                return ResponseEntity.ok(response);
        }

        /**
         * Story A2: Submit exam answer
         *
         * POST /api/exams/simulations/{examId}/questions/{questionId}/answer
         */
        @PostMapping("/{examId}/questions/{questionId}/answer")
        @Operation(summary = "Submit exam answer", description = "Submit answer for a specific question in an exam. " +
                        "Does NOT reveal if answer is correct (security). " +
                        "Allows updating answer if question already answered.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Answer submitted successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid answer data"),
                        @ApiResponse(responseCode = "404", description = "Exam or question not found"),
                        @ApiResponse(responseCode = "409", description = "Exam not active")
        })
        public ResponseEntity<SubmitExamAnswerResponse> submitAnswer(
                        @Parameter(description = "Exam ID", required = true) @PathVariable Long examId,

                        @Parameter(description = "Question ID", required = true) @PathVariable Long questionId,

                        @Parameter(description = "Answer submission data", required = true) @Valid @RequestBody SubmitExamAnswerRequest request) {
                Long userId = authenticationUtil.getCurrentUserId();
                log.info("📝 Submitting answer for exam {} question {} — user {}", examId, questionId, userId);

                SubmitExamAnswerResponse response = examService.submitAnswer(
                                examId,
                                questionId,
                                request,
                                userId);

                log.info("✅ Answer submitted: answerId={}, progress={}/{}",
                                response.getAnswerId(),
                                response.getTotalAnswered(),
                                response.getTotalQuestions());

                return ResponseEntity.ok(response);
        }

        @PostMapping("/{examId}/questions/{questionId}/timeout")
        @Operation(summary = "Record a timed-out exam question", description = "Finalizes a displayed theory question as unanswered after its 15-second timer. "
                        + "The operation is idempotent and never fabricates a selected option.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Timeout recorded or the question was already finalized"),
                        @ApiResponse(responseCode = "403", description = "Exam belongs to another user"),
                        @ApiResponse(responseCode = "404", description = "Exam or question not found"),
                        @ApiResponse(responseCode = "409", description = "Exam is not active")
        })
        public ResponseEntity<Void> recordQuestionTimeout(
                        @PathVariable Long examId,
                        @PathVariable Long questionId) {
                Long userId = authenticationUtil.getCurrentUserId();
                examService.recordQuestionTimeout(examId, questionId, userId);
                return ResponseEntity.noContent().build();
        }

        /**
         * Submit (complete) exam simulation
         *
         * POST /api/exams/simulations/{examId}/submit
         *
         * Marks the exam as COMPLETED and calculates the final score.
         * Must be called before GET /results so the exam is in a terminal state.
         * Idempotent — safe to call if already completed.
         */
        @PostMapping("/{examId}/submit")
        @Operation(summary = "Submit and complete exam", description = "Marks the exam as COMPLETED and persists the final score. "
                        +
                        "Must be called when user clicks Submit. Idempotent if already completed.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Exam completed successfully"),
                        @ApiResponse(responseCode = "400", description = "Exam not in progress"),
                        @ApiResponse(responseCode = "403", description = "Exam belongs to another user"),
                        @ApiResponse(responseCode = "404", description = "Exam not found")
        })
        public ResponseEntity<Map<String, Object>> submitExam(
                        @Parameter(description = "Exam ID", required = true) @PathVariable Long examId) {
                Long userId = authenticationUtil.getCurrentUserId();
                log.info("📋 Submitting exam: examId={}, userId={}", examId, userId);

                examService.completeExam(examId, userId);

                Map<String, Object> response = new HashMap<>();
                response.put("examId", examId);
                response.put("status", "COMPLETED");
                response.put("message", messages.get("exam.submit.completed"));

                log.info("✅ Exam submitted: examId={}", examId);
                return ResponseEntity.ok(response);
        }

        @PostMapping("/{examId}/questions/{questionId}/presented")
        @Operation(summary = "Record a presented exam question", description = "Records that the authenticated exam owner actually viewed one question. "
                        + "The operation is idempotent for each question in an exam.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Presentation recorded or already recorded"),
                        @ApiResponse(responseCode = "403", description = "Exam belongs to another user"),
                        @ApiResponse(responseCode = "404", description = "Exam or question not found"),
                        @ApiResponse(responseCode = "409", description = "Exam is not active")
        })
        public ResponseEntity<Void> recordQuestionPresented(
                        @PathVariable Long examId,
                        @PathVariable Long questionId) {
                Long userId = authenticationUtil.getCurrentUserId();
                examService.recordQuestionPresented(examId, questionId, userId);
                return ResponseEntity.noContent().build();
        }

        @PostMapping("/{examId}/abandon")
        @Operation(summary = "Abandon an incomplete exam", description = "Terminates an incomplete attempt without creating a score or learner result.")
        public ResponseEntity<Map<String, Object>> abandonExam(
                        @Parameter(description = "Exam ID", required = true) @PathVariable Long examId) {
                Long userId = authenticationUtil.getCurrentUserId();
                examService.cancelExam(examId, userId);
                return ResponseEntity.ok(Map.of(
                                "examId", examId,
                                "status", "ABANDONED"));
        }

        /**
         * Story A3: Get exam results
         *
         * GET /api/exams/simulations/{examId}/results
         */
        @GetMapping("/{examId}/results")
        @Operation(summary = "Get exam results", description = "Get comprehensive exam results including category breakdown and incorrect questions. "
                        +
                        "Only accessible after exam is completed. User can only view their own exams.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Results retrieved successfully"),
                        @ApiResponse(responseCode = "403", description = "Exam belongs to another user"),
                        @ApiResponse(responseCode = "404", description = "Exam not found"),
                        @ApiResponse(responseCode = "400", description = "Exam not completed yet")
        })
        public ResponseEntity<ExamResultsDTO> getExamResults(
                        @Parameter(description = "Exam ID", required = true) @PathVariable Long examId) {
                // ✅ SECURITY FIX: Get userId from JWT
                Long userId = authenticationUtil.getCurrentUserId();
                log.info("📊 Fetching exam results: examId={}, userId={}", examId, userId);

                ExamResultsDTO results = examService.getExamResults(examId, userId);

                log.info("✅ Results retrieved: examId={}, score={}/{}, passed={}",
                                examId, results.getCorrectAnswers(), results.getTotalQuestions(), results.getPassed());

                return ResponseEntity.ok(results);
        }

        /**
         * Get active exam for user
         *
         * GET /api/exams/simulations/active?userId={userId}
         */
        @GetMapping("/active")
        @Operation(summary = "Get active exam", description = "Get the currently active exam for the user, if any")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Active exam retrieved or null if none"),
                        @ApiResponse(responseCode = "404", description = "No active exam found")
        })
        public ResponseEntity<Map<String, Object>> getActiveExam() {
                // ✅ SECURITY FIX: Get userId from JWT
                Long userId = authenticationUtil.getCurrentUserId();
                log.info("🔍 GET /api/exams/simulations/active - userId: {}", userId);

                ExamStartResponse activeExam = examService.getActiveExamResponse(userId);

                if (activeExam == null) {
                        log.info("ℹ️ No active exam found for user: {}", userId);
                        Map<String, Object> response = new HashMap<>();
                        response.put("hasActiveExam", false);
                        response.put("activeExam", null);
                        return ResponseEntity.ok(response);
                }

                Map<String, Object> response = new HashMap<>();
                response.put("hasActiveExam", true);
                response.put("activeExam", activeExam);

                log.info("✅ Active exam found: examId={}, userId={}", activeExam.getExamId(), userId);
                return ResponseEntity.ok(response);
        }

        /**
         * Get exam history for user
         *
         * GET /api/exams/simulations/history?userId={userId}
         */
        @GetMapping("/history")
        @Operation(summary = "Get exam history", description = "Get all completed exams for the user with their results")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Exam history retrieved successfully")
        })
        public ResponseEntity<Map<String, Object>> getExamHistory() {
                // ✅ SECURITY FIX: Get userId from JWT
                Long userId = authenticationUtil.getCurrentUserId();
                log.info("📜 GET /api/exams/simulations/history - userId: {}", userId);

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
}
