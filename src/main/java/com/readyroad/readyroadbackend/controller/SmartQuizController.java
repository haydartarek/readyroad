package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.config.AuthenticationUtil;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.service.SmartQuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Smart Quiz Controller - Phase 3: 24h Cooldown MVP
 * Enforces Law #1: Questions don't repeat within 24 hours for same user.
 * Requires authentication to track user history.
 */
@RestController
@RequestMapping("/api/smart-quiz")
@RequiredArgsConstructor
@Slf4j
public class SmartQuizController {

    private final SmartQuizService smartQuizService;
    private final AuthenticationUtil authenticationUtil;

    /**
     * Generate random smart quiz with 24h cooldown.
     */
    @GetMapping("/random")
    public ResponseEntity<List<QuizQuestion>> generateRandomSmartQuiz(
        @RequestParam(defaultValue = "10") int count,
        Authentication authentication
    ) {
        Long userId = authenticationUtil.extractUserId(authentication);
        log.info("Smart quiz request: userId={}, count={}", userId, count);

        List<QuizQuestion> questions = smartQuizService.generateSmartQuiz(userId, count);

        if (questions.isEmpty()) {
            log.warn("No fresh questions available for user {}", userId);
        }

        return ResponseEntity.ok(questions);
    }

    /**
     * Generate smart quiz by category with 24h cooldown.
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<QuizQuestion>> generateCategorySmartQuiz(
        @PathVariable Long categoryId,
        @RequestParam(defaultValue = "10") int count,
        Authentication authentication
    ) {
        Long userId = authenticationUtil.extractUserId(authentication);
        log.info("Smart quiz request: userId={}, categoryId={}, count={}", userId, categoryId, count);

        List<QuizQuestion> questions = smartQuizService.generateSmartQuiz(userId, count, categoryId);

        return ResponseEntity.ok(questions);
    }

    /**
     * Get statistics about available fresh questions.
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getFreshQuestionStats(Authentication authentication) {
        Long userId = authenticationUtil.extractUserId(authentication);
        long freshCount = smartQuizService.countFreshQuestions(userId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("userId", userId);
        stats.put("freshQuestionsAvailable", freshCount);
        stats.put("cooldownHours", 24);
        stats.put("message", freshCount > 0
            ? "You have " + freshCount + " fresh questions available"
            : "All questions seen in last 24h. Try again later!");

        return ResponseEntity.ok(stats);
    }

    /**
     * Get fresh question count for a specific category.
     */
    @GetMapping("/stats/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getCategoryFreshQuestionStats(
        @PathVariable Long categoryId,
        Authentication authentication
    ) {
        Long userId = authenticationUtil.extractUserId(authentication);
        long freshCount = smartQuizService.countFreshQuestionsInCategory(userId, categoryId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("userId", userId);
        stats.put("categoryId", categoryId);
        stats.put("freshQuestionsAvailable", freshCount);
        stats.put("cooldownHours", 24);

        return ResponseEntity.ok(stats);
    }

}

