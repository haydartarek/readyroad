package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.util.AuthenticationUtil;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.dto.QuizQuestionDTO;
import com.readyroad.readyroadbackend.mapper.QuizQuestionMapper;
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
 * 
 * Public Access: Works without authentication (guest mode: no cooldown
 * tracking)
 * Authenticated Access: Tracks user history and enforces 24h cooldown
 */
@RestController
@RequestMapping("/api/smart-quiz")
@RequiredArgsConstructor
@Slf4j
public class SmartQuizController {

    private final SmartQuizService smartQuizService;
    private final AuthenticationUtil authenticationUtil;
    private final QuizQuestionMapper quizQuestionMapper;

    /**
     * Generate random smart quiz with 24h cooldown.
     * 
     * @param count          Number of questions (default: 10)
     * @param authentication Optional - if present, cooldown is enforced; if null,
     *                       returns random questions
     */
    @GetMapping("/random")
    public ResponseEntity<List<QuizQuestionDTO>> generateRandomSmartQuiz(
            @RequestParam(defaultValue = "10") int count,
            Authentication authentication) {
        // Public endpoint: authentication is optional
        Long userId = isAuthenticatedUser(authentication)
                ? authenticationUtil.extractUserId(authentication)
                : null;

        if (userId != null) {
            log.info("Smart quiz request (authenticated): userId={}, count={}", userId, count);
        } else {
            log.info("Smart quiz request (guest): count={}", count);
        }

        List<QuizQuestion> questions = smartQuizService.generateSmartQuiz(userId, count);

        if (questions.isEmpty()) {
            log.warn("No questions available for user {}", userId);
        }

        return ResponseEntity.ok(quizQuestionMapper.toDTOList(questions));
    }

    /**
     * Generate smart quiz by category with 24h cooldown.
     * 
     * @param categoryId     Category ID
     * @param count          Number of questions (default: 10)
     * @param authentication Optional - if present, cooldown is enforced; if null,
     *                       returns random questions
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<QuizQuestionDTO>> generateCategorySmartQuiz(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "10") int count,
            Authentication authentication) {
        // Public endpoint: authentication is optional
        Long userId = isAuthenticatedUser(authentication)
                ? authenticationUtil.extractUserId(authentication)
                : null;

        if (userId != null) {
            log.info("Smart quiz request (authenticated): userId={}, categoryId={}, count={}", userId, categoryId,
                    count);
        } else {
            log.info("Smart quiz request (guest): categoryId={}, count={}", categoryId, count);
        }

        List<QuizQuestion> questions = smartQuizService.generateSmartQuiz(userId, count, categoryId);

        return ResponseEntity.ok(quizQuestionMapper.toDTOList(questions));
    }

    /**
     * Get statistics about available fresh questions.
     * 
     * @param authentication Optional - if present, returns personalized stats with
     *                       cooldown info
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getFreshQuestionStats(Authentication authentication) {
        // Public endpoint: authentication is optional
        Long userId = isAuthenticatedUser(authentication)
                ? authenticationUtil.extractUserId(authentication)
                : null;

        Map<String, Object> stats = new HashMap<>();

        if (userId != null) {
            // Authenticated: show personalized stats with cooldown info
            long freshCount = smartQuizService.countFreshQuestions(userId);
            stats.put("userId", userId);
            stats.put("freshQuestionsAvailable", freshCount);
            stats.put("cooldownHours", 24);
            stats.put("message", freshCount > 0
                    ? "You have " + freshCount + " fresh questions available"
                    : "All questions seen recently. They will become available after 24h cooldown.");
        } else {
            // Guest: show general stats
            long totalCount = smartQuizService.countTotalQuestions();
            stats.put("totalQuestionsAvailable", totalCount);
            stats.put("message", "Login to track your progress and enable 24h question cooldown.");
            stats.put("guestMode", true);
        }

        return ResponseEntity.ok(stats);
    }

    /**
     * Get fresh question count for a specific category.
     * Public endpoint: works for both guests and authenticated users.
     */
    @GetMapping("/stats/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getCategoryFreshQuestionStats(
            @PathVariable Long categoryId,
            Authentication authentication) {
        // Public endpoint: authentication is optional
        Long userId = isAuthenticatedUser(authentication)
                ? authenticationUtil.extractUserId(authentication)
                : null;

        Map<String, Object> stats = new HashMap<>();
        stats.put("categoryId", categoryId);

        if (userId != null) {
            // Authenticated: show personalized fresh count with cooldown info
            long freshCount = smartQuizService.countFreshQuestionsInCategory(userId, categoryId);
            stats.put("userId", userId);
            stats.put("freshQuestionsAvailable", freshCount);
            stats.put("cooldownHours", 24);
        } else {
            // Guest: show total published count for category
            long totalCount = smartQuizService.countTotalQuestionsInCategory(categoryId);
            stats.put("totalQuestionsAvailable", totalCount);
            stats.put("guestMode", true);
        }

        return ResponseEntity.ok(stats);
    }

    /**
     * Check if authentication represents a real authenticated user (not anonymous).
     */
    private boolean isAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        // Check if not anonymous user
        Object principal = authentication.getPrincipal();
        return principal != null && !"anonymousUser".equals(principal);
    }
}
