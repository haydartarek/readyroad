package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.RoadSignSummaryDto;
import com.readyroad.readyroadbackend.dto.sign.*;
import com.readyroad.readyroadbackend.service.SignQuizService;
import com.readyroad.readyroadbackend.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User-facing Sign Quiz REST API.
 *
 * <pre>
 * GET  /api/sign-quiz/signs                                         → list all active signs
 * POST /api/sign-quiz/practice/{signCode}                           → start practice session (stateful)
 * POST /api/sign-quiz/practice/{sessionId}/questions/{qId}/answer   → submit one answer
 * GET  /api/sign-quiz/practice/{sessionId}/results                  → full session results
 * GET  /api/sign-quiz/exam/{signCode}/{examNumber}                  → get exam questions (stateless)
 * POST /api/sign-quiz/exam/{signCode}/{examNumber}/submit           → submit all exam answers
 * </pre>
 *
 * <p>All endpoints require a valid JWT (enforced in SecurityConfig).</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/sign-quiz")
@RequiredArgsConstructor
@Tag(name = "Sign Quiz", description = "Road sign quiz engine — practice (stateful) and exam (stateless) modes")
public class SignQuizController {

    private final SignQuizService    signQuizService;
    private final AuthenticationUtil authUtil;

    // ── GET /api/sign-quiz/signs ─────────────────────────────────────────────

    @GetMapping("/signs")
    @Operation(
        summary     = "List active road signs",
        description = "Returns a lightweight summary of every active road sign, ordered by sign code."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sign list returned"),
        @ApiResponse(responseCode = "401", description = "JWT not provided or invalid")
    })
    public ResponseEntity<List<RoadSignSummaryDto>> listSigns(Authentication auth) {
        log.debug("GET /api/sign-quiz/signs — user {}", authUtil.extractUserId(auth));
        return ResponseEntity.ok(signQuizService.getActiveSigns());
    }

    // ── POST /api/sign-quiz/practice/{signCode} ──────────────────────────────

    @PostMapping("/practice/{signCode}")
    @Operation(
        summary     = "Start practice session",
        description = "Creates (or resumes) a stateful practice session for the given sign. "
                    + "Returns the session ID and all shuffled questions without correct-answer flags."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Session started or resumed"),
        @ApiResponse(responseCode = "404", description = "Sign not found or not active"),
        @ApiResponse(responseCode = "422", description = "Sign has no active questions"),
        @ApiResponse(responseCode = "401", description = "JWT not provided or invalid")
    })
    public ResponseEntity<SignPracticeSessionDto> startPractice(
            @Parameter(description = "Road sign code, e.g. A1, B19, C3", example = "A1")
            @PathVariable String signCode,
            Authentication auth) {

        Long userId = authUtil.extractUserId(auth);
        log.info("POST /api/sign-quiz/practice/{} — user {}", signCode, userId);
        return ResponseEntity.ok(signQuizService.startPracticeSession(userId, signCode));
    }

    // ── POST /api/sign-quiz/practice/{sessionId}/questions/{questionId}/answer

    @PostMapping("/practice/{sessionId}/questions/{questionId}/answer")
    @Operation(
        summary     = "Submit one practice answer",
        description = "Records one answer for a question within the given session. "
                    + "Returns immediate feedback including the correct answer and explanation."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Answer recorded, feedback returned"),
        @ApiResponse(responseCode = "404", description = "Session or question not found"),
        @ApiResponse(responseCode = "400", description = "Choice does not belong to the question"),
        @ApiResponse(responseCode = "409", description = "Session is already COMPLETED or question already answered"),
        @ApiResponse(responseCode = "401", description = "JWT not provided or invalid")
    })
    public ResponseEntity<SignPracticeAnswerResponse> submitAnswer(
            @Parameter(description = "Practice session ID returned from start-session")
            @PathVariable Long sessionId,

            @Parameter(description = "Question ID from the session's question list")
            @PathVariable Long questionId,

            @Valid @RequestBody SignPracticeAnswerRequest request,
            Authentication auth) {

        Long userId = authUtil.extractUserId(auth);
        log.debug("POST /practice/{}/questions/{}/answer — user {}", sessionId, questionId, userId);

        SignPracticeAnswerResponse response = signQuizService.submitPracticeAnswer(
                sessionId, questionId,
                request.choiceId(), request.timeTakenSeconds(),
                userId);
        return ResponseEntity.ok(response);
    }

    // ── GET /api/sign-quiz/practice/{sessionId}/results ─────────────────────

    @GetMapping("/practice/{sessionId}/results")
    @Operation(
        summary     = "Get practice session results",
        description = "Returns the full result summary for a practice session. "
                    + "Available for both IN_PROGRESS (partial) and COMPLETED sessions."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Results returned"),
        @ApiResponse(responseCode = "404", description = "Session not found or does not belong to user"),
        @ApiResponse(responseCode = "401", description = "JWT not provided or invalid")
    })
    public ResponseEntity<SignPracticeResultDto> getPracticeResults(
            @Parameter(description = "Practice session ID")
            @PathVariable Long sessionId,
            Authentication auth) {

        Long userId = authUtil.extractUserId(auth);
        log.debug("GET /practice/{}/results — user {}", sessionId, userId);
        return ResponseEntity.ok(signQuizService.getPracticeResults(sessionId, userId));
    }

    // ── GET /api/sign-quiz/exam/{signCode}/{examNumber} ──────────────────────

    @GetMapping("/exam/{signCode}/{examNumber}")
    @Operation(
        summary     = "Get exam questions (stateless)",
        description = "Returns the ordered question list for a sign exam. "
                    + "No session is created; the client collects all answers and submits in one request."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Exam questions returned"),
        @ApiResponse(responseCode = "404", description = "Sign or exam not found"),
        @ApiResponse(responseCode = "401", description = "JWT not provided or invalid")
    })
    public ResponseEntity<SignExamQuestionsDto> getExamQuestions(
            @Parameter(description = "Road sign code, e.g. A1", example = "A1")
            @PathVariable String signCode,

            @Parameter(description = "Exam number: 1 or 2", example = "1")
            @PathVariable int examNumber,
            Authentication auth) {

        Long userId = authUtil.extractUserId(auth);
        log.debug("GET /exam/{}/{} — user {}", signCode, examNumber, userId);
        return ResponseEntity.ok(signQuizService.getExamQuestions(signCode, examNumber, userId));
    }

    // ── POST /api/sign-quiz/exam/{signCode}/{examNumber}/submit ──────────────

    @PostMapping("/exam/{signCode}/{examNumber}/submit")
    @Operation(
        summary     = "Submit exam answers (stateless)",
        description = "Evaluates all submitted answers at once. "
                    + "Passing threshold: ≥ 80% of linked questions. "
                    + "Updates user_weak_areas in V11 after evaluation."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Exam result returned"),
        @ApiResponse(responseCode = "400", description = "Validation error in request body"),
        @ApiResponse(responseCode = "404", description = "Sign or exam not found"),
        @ApiResponse(responseCode = "401", description = "JWT not provided or invalid")
    })
    public ResponseEntity<SignExamResultDto> submitExam(
            @Parameter(description = "Road sign code, e.g. A1", example = "A1")
            @PathVariable String signCode,

            @Parameter(description = "Exam number: 1 or 2", example = "1")
            @PathVariable int examNumber,

            @Valid @RequestBody SignExamSubmitRequest request,
            Authentication auth) {

        Long userId = authUtil.extractUserId(auth);
        log.info("POST /exam/{}/{}/submit — user {} ({} answers)",
                signCode, examNumber, userId, request.answers().size());

        SignExamResultDto result = signQuizService.submitExam(
                signCode, examNumber, request.answers(), userId);
        return ResponseEntity.ok(result);
    }

    // ── GET /api/sign-quiz/signs/{signCode}/status ───────────────────────────

    @GetMapping("/signs/{signCode}/status")
    @Operation(
        summary     = "Get user progress for a single sign",
        description = "Returns the progress snapshot (practice, exam 1, exam 2) for the "
                    + "authenticated user and the specified sign code. Used by the sign "
                    + "detail page to show locked/unlocked state and best scores."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Progress returned"),
        @ApiResponse(responseCode = "404", description = "Sign not found"),
        @ApiResponse(responseCode = "401", description = "JWT not provided or invalid")
    })
    public ResponseEntity<SignUserProgressDto> getSignStatus(
            @Parameter(description = "Road sign code, e.g. A1", example = "A1")
            @PathVariable String signCode,
            Authentication auth) {

        Long userId = authUtil.extractUserId(auth);
        log.debug("GET /signs/{}/status — user {}", signCode, userId);
        return ResponseEntity.ok(signQuizService.getUserSignProgress(signCode, userId));
    }

    // ── GET /api/sign-quiz/user-progress ────────────────────────────────────

    @GetMapping("/user-progress")
    @Operation(
        summary     = "Get user progress for all signs",
        description = "Returns a progress snapshot for every active road sign in one call. "
                    + "Used by the signs list page to show completion badges without N+1 round-trips."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Progress list returned"),
        @ApiResponse(responseCode = "401", description = "JWT not provided or invalid")
    })
    public ResponseEntity<List<SignUserProgressDto>> getAllProgress(Authentication auth) {
        Long userId = authUtil.extractUserId(auth);
        log.debug("GET /user-progress — user {}", userId);
        return ResponseEntity.ok(signQuizService.getAllUserProgress(userId));
    }

    // ── GET /api/sign-quiz/random-practice ──────────────────────────────────

    @GetMapping("/random-practice")
    @Operation(
        summary     = "Get random sign practice questions (stateless)",
        description = "Returns 50 randomly selected active sign questions (20 EASY + 18 MEDIUM + 12 HARD), "
                    + "shuffled. No session is created. Choices are returned WITHOUT the isCorrect flag."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "50 shuffled sign questions returned"),
        @ApiResponse(responseCode = "401", description = "JWT not provided or invalid")
    })
    public ResponseEntity<List<SignQuizQuestionDto>> getRandomPractice(Authentication auth) {
        log.debug("GET /random-practice — user {}", authUtil.extractUserId(auth));
        return ResponseEntity.ok(signQuizService.getRandomSignPracticeQuestions());
    }

    // ── POST /api/sign-quiz/random-practice/check ────────────────────────────

    @PostMapping("/random-practice/check")
    @Operation(
        summary     = "Check random sign practice answers (stateless)",
        description = "Evaluates all submitted answers for a random-practice session. "
                    + "No DB write — purely stateless. Passing score: 41/50."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Practice result returned"),
        @ApiResponse(responseCode = "400", description = "Empty or invalid request body"),
        @ApiResponse(responseCode = "401", description = "JWT not provided or invalid")
    })
    public ResponseEntity<SignRandomPracticeResultDto> checkRandomPractice(
            @RequestBody List<SignRandomPracticeAnswerRequest> answers,
            Authentication auth) {
        log.debug("POST /random-practice/check — user {}, {} answers",
                authUtil.extractUserId(auth), answers.size());
        return ResponseEntity.ok(signQuizService.checkRandomSignPracticeAnswers(answers));
    }
}
