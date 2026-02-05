package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationQuestion;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationQuestionRepository;
import com.readyroad.readyroadbackend.dto.exam.ExamStartResponse;
import com.readyroad.readyroadbackend.mapper.ExamMapper;
import com.readyroad.readyroadbackend.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    /**
     * Start exam simulation
     * POST /api/exam-simulations/start
     */
    @PostMapping("/start")
    @Operation(summary = "Start exam simulation")
    public ResponseEntity<ExamStartResponse> startExam(
        @Parameter(description = "User ID", required = true)
        @RequestParam Long userId
    ) {
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
     */
    @GetMapping("/active")
    @Operation(summary = "Get active exam")
    public ResponseEntity<Map<String, Object>> getActiveExam(
        @Parameter(description = "User ID", required = true)
        @RequestParam Long userId
    ) {
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
     */
    @GetMapping("/history")
    @Operation(summary = "Get exam history")
    public ResponseEntity<Map<String, Object>> getExamHistory(
        @Parameter(description = "User ID", required = true)
        @RequestParam Long userId
    ) {
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
}
