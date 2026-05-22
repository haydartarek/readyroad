package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.util.AuthenticationUtil;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.dto.QuizQuestionDTO;
import com.readyroad.readyroadbackend.dto.TheoryExamAnswerRequest;
import com.readyroad.readyroadbackend.dto.TheoryExamResultDTO;
import com.readyroad.readyroadbackend.dto.practice.SubmitPracticeAnswerRequest;
import com.readyroad.readyroadbackend.dto.practice.SubmitPracticeAnswerResponse;
import com.readyroad.readyroadbackend.mapper.QuizQuestionMapper;
import com.readyroad.readyroadbackend.service.CategoryService;
import com.readyroad.readyroadbackend.service.PracticeService;
import com.readyroad.readyroadbackend.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * QuizController - Basic Quiz Generation
 *
 * **Phase 2 Restoration:** Implemented January 18, 2026
 *
 * Provides REST endpoints for the current theory-question bank:
 * - ✅ Random quiz generation
 * - ✅ Category-filtered quiz generation
 * - ❌ NO 24-hour cooldown
 * - ❌ NO user history tracking
 * - ❌ NO adaptive difficulty
 */
@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Quiz", description = "Basic quiz generation endpoints")
public class QuizController {

    private final QuizService quizService;
    private final CategoryService categoryService;
    private final PracticeService practiceService;
    private final AuthenticationUtil authenticationUtil;
    private final QuizQuestionMapper quizQuestionMapper;

    /**
     * Generate a random quiz
     *
     * GET /api/quiz/random?count=10
     *
     * @param count Number of questions (default: 10, max: 50)
     * @return List of random quiz questions with options
     */
    @GetMapping("/random")
    @Operation(summary = "Generate random quiz", description = "Returns random questions from all active categories")
    public ResponseEntity<List<QuizQuestionDTO>> generateRandomQuiz(
            @Parameter(description = "Number of questions (max 50)", example = "10") @RequestParam(defaultValue = "10") int count) {

        List<QuizQuestion> questions = quizService.generateRandomQuiz(count);
        return ResponseEntity.ok(quizQuestionMapper.toDTOList(questions));
    }

    /**
     * Generate a quiz from a specific category
     *
     * GET /api/quiz/category/{categoryId}?count=10
     *
     * @param categoryId Category ID to filter by
     * @param count      Number of questions (default: 10, max: 50)
     * @return List of random quiz questions from the category with options
     */
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Generate quiz by category", description = "Returns random questions from a specific category")
    public ResponseEntity<List<QuizQuestionDTO>> generateQuizByCategory(
            @Parameter(description = "Category ID", example = "1") @PathVariable Long categoryId,
            @Parameter(description = "Number of questions (max 50)", example = "10") @RequestParam(defaultValue = "10") int count) {

        List<QuizQuestion> questions = quizService.generateQuizByCategory(categoryId, count);
        return ResponseEntity.ok(quizQuestionMapper.toDTOList(questions));
    }

    /**
     * Get statistics about available questions
     *
     * GET /api/quiz/stats
     *
     * @return Statistics about quiz questions
     */
    @GetMapping("/stats")
    @Operation(summary = "Get quiz statistics", description = "Returns total count of available questions")
    public ResponseEntity<QuizStats> getQuizStats() {
        Long totalQuestions = quizService.getTotalActiveQuestions();
        Long totalCategories = (long) categoryService.getAllActiveCategories().size();
        return ResponseEntity.ok(new QuizStats(totalQuestions, totalCategories));
    }

    /**
     * Get statistics about questions in a specific category
     *
     * GET /api/quiz/stats/category/{categoryId}
     *
     * @param categoryId Category ID
     * @return Statistics about questions in the category
     */
    @GetMapping("/stats/category/{categoryId}")
    @Operation(summary = "Get category quiz statistics", description = "Returns question count for a specific category")
    public ResponseEntity<QuizStats> getCategoryStats(
            @Parameter(description = "Category ID", example = "1") @PathVariable Long categoryId) {

        Long categoryQuestions = quizService.getActiveQuestionsByCategory(categoryId);
        return ResponseEntity.ok(new QuizStats(categoryQuestions, null));
    }

    /**
     * Simple DTO for quiz statistics
     */
    public record QuizStats(Long totalQuestions, Long totalCategories) {
    }

    // ============================================================================
    // STORY B1: SUBMIT PRACTICE ANSWER
    // ============================================================================

    /**
     * Submit practice quiz answer and get immediate feedback
     * Story B1: Submit Practice Answer
     *
     * POST /api/quiz/questions/{questionId}/answer
     *
     * Features:
     * - Immediate correctness feedback
     * - Shows correct answer and explanation (all languages)
     * - Records in user_question_history (24h cooldown enforcement)
     * - Updates category progress tracking
     * - Returns updated accuracy and mastery level
     *
     * @param questionId     Question ID
     * @param request        Answer submission request (selectedOptionId, timeTaken)
     * @param authentication Spring Security authentication
     * @return Comprehensive feedback with updated progress
     */
    @PostMapping("/questions/{questionId}/answer")
    @Operation(summary = "Submit practice answer (Story B1)", description = """
            Submit answer for practice question and receive immediate feedback with updated progress.

            **Feature B1: Answer Submission & Progress Tracking**

            This endpoint:
            - Evaluates answer correctness
            - Provides immediate feedback with correct answer and explanation (all languages)
            - Records submission in user_question_history (enforces 24h cooldown)
            - Updates category progress tracking
            - Recalculates mastery level (BEGINNER/INTERMEDIATE/ADVANCED)
            - Returns updated accuracy statistics

            **Security:** Requires authentication. User can only submit answers for their own account.

            **Business Rules:**
            - Mastery Levels: BEGINNER (<50%), INTERMEDIATE (50-79%), ADVANCED (≥80%)
            - 24-hour cooldown enforced via last_shown_at timestamp
            - Category progress updated after each submission
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Answer submitted successfully with feedback", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubmitPracticeAnswerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request - invalid questionId or selectedOptionId", content = @Content),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "404", description = "Question not found", content = @Content)
    })
    public ResponseEntity<SubmitPracticeAnswerResponse> submitPracticeAnswer(
            @Parameter(description = "Question ID", example = "42", required = true) @PathVariable Long questionId,

            @Valid @RequestBody SubmitPracticeAnswerRequest request,

            Authentication authentication) {

        // Extract user ID from authentication
        Long userId = authenticationUtil.extractUserId(authentication);

        // In production mode, return 401 if not authenticated
        if (userId == null) {
            log.warn("Unauthenticated access attempt to /api/quiz/questions/{}/answer - returning 401", questionId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Submit answer and get feedback
        SubmitPracticeAnswerResponse response = practiceService.submitPracticeAnswer(
                userId,
                questionId,
                request);

        return ResponseEntity.ok(response);
    }

    // ============================================================================
    // BELGIAN THEORY EXAM — /api/quiz/theory-exam
    // Distribution: 20 EASY + 20 MEDIUM + 10 HARD = 50 questions
    // Per-question timer enforced client-side (15 seconds)
    // Pass threshold: 41/50 (Belgian rijbewijs B standard)
    // Stateless — no DB session, no history written
    // ============================================================================

    /**
     * Fetch 50 questions for a Belgian theory exam practice session.
     * Distribution: 20 easy / 20 medium / 10 hard, fully shuffled.
     *
     * GET /api/quiz/theory-exam
     */
    @GetMapping("/theory-exam")
    @Operation(summary = "Get Belgian theory exam question set (20E+20M+10H)")
    public ResponseEntity<List<QuizQuestionDTO>> getTheoryExamQuestions(
            Authentication authentication) {
        Long userId = authenticationUtil.extractUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<QuizQuestion> questions = quizService.getTheoryExamQuestions();
        return ResponseEntity.ok(quizQuestionMapper.toDTOList(questions));
    }

    /**
     * Stateless answer check for a completed theory exam practice session.
     * Calculates score, pass/fail, and per-question correctness.
     * Does NOT write to DB or update user history.
     *
     * POST /api/quiz/theory-exam/check
     */
    @PostMapping("/theory-exam/check")
    @Operation(summary = "Check Belgian theory exam answers (stateless, no history recorded)")
    public ResponseEntity<TheoryExamResultDTO> checkTheoryExamAnswers(
            @RequestBody List<TheoryExamAnswerRequest> answers,
            Authentication authentication) {
        Long userId = authenticationUtil.extractUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        TheoryExamResultDTO result = quizService.checkTheoryExamAnswers(answers);
        return ResponseEntity.ok(result);
    }

    // ============================================================================
    // End of QuizController
    // ============================================================================
}
