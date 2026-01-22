package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.model.UserQuestionHistory;
import com.readyroad.readyroadbackend.domain.repository.UserQuestionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User Performance Service - Phase 4 (Adaptive Difficulty / Law #2)
 *
 * Analyzes user performance history to determine appropriate difficulty level.
 * Supports Law #2: Adjust question difficulty based on user performance.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserPerformanceService {

    private final UserQuestionHistoryRepository historyRepository;

    // Performance thresholds
    private static final double HIGH_PERFORMER_THRESHOLD = 0.80; // 80%
    private static final double LOW_PERFORMER_THRESHOLD = 0.50;  // 50%
    private static final int DEFAULT_HISTORY_LIMIT = 20;         // Last 20 questions

    /**
     * Calculate user accuracy from recent answered questions.
     *
     * @param userId User ID
     * @param questionLimit Number of recent questions to analyze (default: 20)
     * @return Accuracy percentage (0.0 - 1.0), or 0.5 if no history
     */
    public double calculateRecentAccuracy(Long userId, int questionLimit) {
        log.debug("Calculating accuracy for user {}, limit {}", userId, questionLimit);

        // Get recent answered questions (where is_correct is not NULL)
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30); // Last 30 days
        List<UserQuestionHistory> recentHistory =
            historyRepository.findRecentAnsweredQuestions(
                userId,
                cutoff,
                PageRequest.of(0, questionLimit)
            );

        if (recentHistory.isEmpty()) {
            log.debug("No performance history for user {}, returning neutral (0.5)", userId);
            return 0.5; // Neutral - no data to base decision on
        }

        // Count correct answers
        long correctCount = recentHistory.stream()
            .filter(h -> Boolean.TRUE.equals(h.getIsCorrect()))
            .count();

        double accuracy = (double) correctCount / recentHistory.size();

        log.info("User {} accuracy: {}/{} = {:.2f}%",
            userId, correctCount, recentHistory.size(), accuracy * 100);

        return accuracy;
    }

    /**
     * Calculate accuracy using default history limit (20 questions).
     */
    public double calculateRecentAccuracy(Long userId) {
        return calculateRecentAccuracy(userId, DEFAULT_HISTORY_LIMIT);
    }

    /**
     * Determine recommended difficulty level based on user performance.
     *
     * Algorithm:
     * - Accuracy >= 80% → HARD (challenge the user)
     * - Accuracy < 50% → EASY (build confidence)
     * - Otherwise (50-79%) → MEDIUM (balanced, includes neutral 50%)
     *
     * @param userId User ID
     * @return Recommended difficulty level
     */
    public QuizQuestion.DifficultyLevel getRecommendedDifficulty(Long userId) {
        double accuracy = calculateRecentAccuracy(userId);

        if (accuracy >= HIGH_PERFORMER_THRESHOLD) {
            log.info("User {} is high performer ({}%), recommending HARD",
                userId, String.format("%.0f", accuracy * 100));
            return QuizQuestion.DifficultyLevel.HARD;
        } else if (accuracy < LOW_PERFORMER_THRESHOLD) { // Changed: < instead of <=
            log.info("User {} needs support ({}%), recommending EASY",
                userId, String.format("%.0f", accuracy * 100));
            return QuizQuestion.DifficultyLevel.EASY;
        } else {
            log.info("User {} is progressing ({}%), recommending MEDIUM",
                userId, String.format("%.0f", accuracy * 100));
            return QuizQuestion.DifficultyLevel.MEDIUM;
        }
    }

    /**
     * Get difficulty weights based on user performance.
     * Used to bias random selection toward appropriate difficulty.
     *
     * Returns probability distribution for each difficulty level.
     * Example: { EASY: 0.1, MEDIUM: 0.3, HARD: 0.6 } for high performers
     *
     * @param accuracy User accuracy (0.0 - 1.0)
     * @return Map of DifficultyLevel → weight
     */
    public Map<QuizQuestion.DifficultyLevel, Double> getDifficultyWeights(double accuracy) {
        Map<QuizQuestion.DifficultyLevel, Double> weights = new HashMap<>();

        if (accuracy >= HIGH_PERFORMER_THRESHOLD) {
            // High performer: bias toward HARD
            weights.put(QuizQuestion.DifficultyLevel.EASY, 0.1);
            weights.put(QuizQuestion.DifficultyLevel.MEDIUM, 0.3);
            weights.put(QuizQuestion.DifficultyLevel.HARD, 0.6);
        } else if (accuracy <= LOW_PERFORMER_THRESHOLD) {
            // Low performer: bias toward EASY
            weights.put(QuizQuestion.DifficultyLevel.EASY, 0.6);
            weights.put(QuizQuestion.DifficultyLevel.MEDIUM, 0.3);
            weights.put(QuizQuestion.DifficultyLevel.HARD, 0.1);
        } else {
            // Average performer: bias toward MEDIUM
            weights.put(QuizQuestion.DifficultyLevel.EASY, 0.2);
            weights.put(QuizQuestion.DifficultyLevel.MEDIUM, 0.6);
            weights.put(QuizQuestion.DifficultyLevel.HARD, 0.2);
        }

        log.debug("Difficulty weights for accuracy {}: {}",
            String.format("%.0f%%", accuracy * 100), weights);

        return weights;
    }

    /**
     * Count questions answered in recent history.
     * Useful for UI display.
     */
    public long countAnsweredQuestions(Long userId, int days) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
        return historyRepository.countAnsweredQuestions(userId, cutoff);
    }

    /**
     * Get overall user statistics.
     * Returns summary of user performance.
     */
    public UserPerformanceStats getPerformanceStats(Long userId) {
        double accuracy = calculateRecentAccuracy(userId);
        QuizQuestion.DifficultyLevel recommended = getRecommendedDifficulty(userId);
        long questionsAnswered = countAnsweredQuestions(userId, 30);

        return UserPerformanceStats.builder()
            .userId(userId)
            .recentAccuracy(accuracy)
            .recommendedDifficulty(recommended)
            .questionsAnsweredLast30Days(questionsAnswered)
            .build();
    }

    /**
     * Performance statistics DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class UserPerformanceStats {
        private Long userId;
        private double recentAccuracy;
        private QuizQuestion.DifficultyLevel recommendedDifficulty;
        private long questionsAnsweredLast30Days;
    }
}
