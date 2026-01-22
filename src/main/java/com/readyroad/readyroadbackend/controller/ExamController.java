package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationQuestion;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationQuestionRepository;
import com.readyroad.readyroadbackend.dto.exam.ExamResultsDTO;
import com.readyroad.readyroadbackend.dto.exam.ExamStartResponse;
import com.readyroad.readyroadbackend.dto.exam.SubmitExamAnswerRequest;
import com.readyroad.readyroadbackend.dto.exam.SubmitExamAnswerResponse;
import com.readyroad.readyroadbackend.mapper.ExamMapper;
import com.readyroad.readyroadbackend.service.ExamService;
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
@Tag(name = "Exam Simulation", description = "Belgian driving license exam simulation (50 questions, 30 minutes)")
@CrossOrigin(origins = "*")
public class ExamController {

    private final ExamService examService;
    private final ExamSimulationQuestionRepository examQuestionRepository;
    private final ExamMapper examMapper;

    /**
     * Story A1: Start exam simulation
     *
     * POST /api/exams/simulations/start
     */
    @PostMapping("/start")
    @Operation(
        summary = "Start exam simulation",
        description = "Start a new 50-question exam simulation with 30-minute time limit. " +
                     "Respects 24h cooldown (Law #1) and adaptive difficulty (Law #2)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Exam started successfully"),
        @ApiResponse(responseCode = "400", description = "User already has active exam"),
        @ApiResponse(responseCode = "409", description = "Insufficient questions available")
    })
    public ResponseEntity<ExamStartResponse> startExam(
        @Parameter(description = "User ID", required = true)
        @RequestParam Long userId
    ) {
        try {
            log.info("📝 Starting exam simulation for user: {}", userId);

            // Service returns entity (business logic)
            ExamSimulation exam = examService.startExamSimulation(userId);

            // Get questions for DTO mapping
            List<ExamSimulationQuestion> questions = examQuestionRepository
                .findByExamIdOrderByQuestionOrder(exam.getId());

            // Map entity to DTO (presentation logic)
            ExamStartResponse response = examMapper.toStartResponse(exam, questions);

            log.info("✅ Exam started: examId={}, userId={}, questions={}",
                exam.getId(), userId, questions.size());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalStateException e) {
            log.warn("⚠️ Cannot start exam for user {}: {}", userId, e.getMessage());

            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    /**
     * Check if user can start exam
     *
     * GET /api/exams/simulations/can-start?userId={userId}
     */
    @GetMapping("/can-start")
    @Operation(
        summary = "Check if user can start exam",
        description = "Checks if user has an active exam in progress"
    )
    public ResponseEntity<Map<String, Object>> canStartExam(
        @Parameter(description = "User ID", required = true)
        @RequestParam Long userId
    ) {
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
    @Operation(
        summary = "Get exam by ID",
        description = "Retrieve exam simulation details"
    )
    public ResponseEntity<Map<String, Object>> getExam(
        @Parameter(description = "Exam ID", required = true)
        @PathVariable Long examId
    ) {
        ExamSimulation exam = examService.getExamById(examId);

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
    @Operation(
        summary = "Submit exam answer",
        description = "Submit answer for a specific question in an exam. " +
                     "Does NOT reveal if answer is correct (security). " +
                     "Allows updating answer if question already answered."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Answer submitted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid answer data"),
        @ApiResponse(responseCode = "404", description = "Exam or question not found"),
        @ApiResponse(responseCode = "409", description = "Exam not active")
    })
    public ResponseEntity<SubmitExamAnswerResponse> submitAnswer(
        @Parameter(description = "Exam ID", required = true)
        @PathVariable Long examId,

        @Parameter(description = "Question ID", required = true)
        @PathVariable Long questionId,

        @Parameter(description = "Answer submission data", required = true)
        @Valid @RequestBody SubmitExamAnswerRequest request
    ) {
        log.info("📝 Submitting answer for exam {} question {}", examId, questionId);

        SubmitExamAnswerResponse response = examService.submitAnswer(
            examId,
            questionId,
            request
        );

        log.info("✅ Answer submitted: answerId={}, progress={}/{}",
            response.getAnswerId(),
            response.getTotalAnswered(),
            response.getTotalQuestions());

        return ResponseEntity.ok(response);
    }

    /**
     * Story A3: Get exam results
     *
     * GET /api/exams/simulations/{examId}/results
     */
    @GetMapping("/{examId}/results")
    @Operation(
        summary = "Get exam results",
        description = "Get comprehensive exam results including category breakdown and incorrect questions. " +
                      "Only accessible after exam is completed. User can only view their own exams."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Results retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Exam belongs to another user"),
        @ApiResponse(responseCode = "404", description = "Exam not found"),
        @ApiResponse(responseCode = "400", description = "Exam not completed yet")
    })
    public ResponseEntity<ExamResultsDTO> getExamResults(
        @Parameter(description = "Exam ID", required = true)
        @PathVariable Long examId,
        @Parameter(description = "User ID", required = true)
        @RequestParam Long userId
    ) {
        log.info("📊 Fetching exam results: examId={}, userId={}", examId, userId);

        ExamResultsDTO results = examService.getExamResults(examId, userId);

        log.info("✅ Results retrieved: examId={}, score={}/{}, passed={}",
            examId, results.getCorrectAnswers(), results.getTotalQuestions(), results.getPassed());

        return ResponseEntity.ok(results);
    }
}
