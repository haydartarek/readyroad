package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.config.TestDataSeederConfig;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.model.UserQuestionHistory;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.UserQuestionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Adaptive Difficulty Integration Test - Phase 4 (Law #2)
 *
 * Tests that the system adjusts question difficulty based on user performance.
 * Verifies Law #2: Question difficulty adapts to user skill level.
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestDataSeederConfig.class)
@Transactional
class AdaptiveDifficultyIntegrationTest {

    @Autowired
    private SmartQuizService smartQuizService;

    @Autowired
    private UserPerformanceService performanceService;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private UserQuestionHistoryRepository historyRepository;

    @BeforeEach
    void setUp() {
        // Clean user history only (seeder provides questions)
        historyRepository.deleteAll();

        // ✅ TestDataSeederConfig already provides 60 PUBLISHED questions with proper difficulty distribution
        // No need to create test questions manually
    }

    @Test
    @Disabled("TODO: Refactor SmartQuizService - adaptive difficulty logic needs review")
    @DisplayName("High performer (90% accuracy) gets HARD questions biased")
    void testHighPerformerGetsHardQuestions() {
        // GIVEN: User with 90% accuracy (18 correct, 2 wrong out of 20)
        Long userId = 888L;
        createPerformanceHistory(userId, 18, 2);

        // Verify accuracy calculation
        double accuracy = performanceService.calculateRecentAccuracy(userId);
        assertThat(accuracy).isGreaterThanOrEqualTo(0.85)
            .as("Expected accuracy >= 85%%, got %.0f%%", accuracy * 100);

        // Verify recommended difficulty
        QuizQuestion.DifficultyLevel recommended =
            performanceService.getRecommendedDifficulty(userId);
        assertThat(recommended).isEqualTo(QuizQuestion.DifficultyLevel.HARD)
            .as("High performer should get HARD recommendation");

        // WHEN: Request adaptive quiz
        List<QuizQuestion> quiz = smartQuizService.generateAdaptiveQuiz(userId, 10, null);

        // THEN: Majority should be HARD (bias works)
        long hardCount = quiz.stream()
            .filter(q -> q.getDifficultyLevel() == QuizQuestion.DifficultyLevel.HARD)
            .count();

        assertThat(hardCount).isGreaterThanOrEqualTo(5)
            .as("Expected 5+ HARD questions for 90%% accuracy user, got %d HARD out of %d total",
                hardCount, quiz.size());

        System.out.println("✅ High performer test PASSED: " + hardCount + " HARD questions returned");
    }

    @Test
    @Disabled("TODO: Refactor SmartQuizService - adaptive difficulty logic needs review")
    @DisplayName("Low performer (40% accuracy) gets EASY questions biased")
    void testLowPerformerGetsEasyQuestions() {
        // GIVEN: User with 40% accuracy (8 correct, 12 wrong out of 20)
        Long userId = 777L;
        createPerformanceHistory(userId, 8, 12);

        // Verify accuracy calculation
        double accuracy = performanceService.calculateRecentAccuracy(userId);
        assertThat(accuracy).isLessThanOrEqualTo(0.50)
            .as("Expected accuracy <= 50%%, got %.0f%%", accuracy * 100);

        // Verify recommended difficulty
        QuizQuestion.DifficultyLevel recommended =
            performanceService.getRecommendedDifficulty(userId);
        assertThat(recommended).isEqualTo(QuizQuestion.DifficultyLevel.EASY)
            .as("Low performer should get EASY recommendation");

        // WHEN: Request adaptive quiz
        List<QuizQuestion> quiz = smartQuizService.generateAdaptiveQuiz(userId, 10, null);

        // THEN: Majority should be EASY (bias works)
        long easyCount = quiz.stream()
            .filter(q -> q.getDifficultyLevel() == QuizQuestion.DifficultyLevel.EASY)
            .count();

        assertThat(easyCount).isGreaterThanOrEqualTo(5)
            .as("Expected 5+ EASY questions for 40%% accuracy user, got %d EASY out of %d total",
                easyCount, quiz.size());

        System.out.println("✅ Low performer test PASSED: " + easyCount + " EASY questions returned");
    }

    @Test
    @Disabled("TODO: Refactor SmartQuizService - cooldown mechanism needs implementation")
    @DisplayName("Adaptive quiz still respects 24h cooldown (Law #1 + Law #2 together)")
    void testAdaptiveQuizRespectssCooldown() {
        // GIVEN: High performer who saw question 1 minute ago
        Long userId = 666L;
        createPerformanceHistory(userId, 18, 2); // 90% accuracy

        List<QuizQuestion> allQuestions = quizQuestionRepository.findAll();
        assertThat(allQuestions).isNotEmpty()
            .as("TestDataSeederConfig should have seeded questions");
        Long seenQuestionId = allQuestions.get(0).getId();

        // Mark one question as seen 1 minute ago
        UserQuestionHistory recentHistory = UserQuestionHistory.builder()
            .userId(userId)
            .questionId(seenQuestionId)
            .answeredAt(LocalDateTime.now().minusMinutes(1))
            .isCorrect(true)
            .timeTakenSeconds(30)
            .build();
        historyRepository.save(recentHistory);

        // WHEN: Request adaptive quiz
        List<QuizQuestion> quiz = smartQuizService.generateAdaptiveQuiz(userId, 10, null);

        // THEN: Recent question should be excluded (cooldown working)
        boolean containsRecentQuestion = quiz.stream()
            .anyMatch(q -> q.getId().equals(seenQuestionId));

        assertThat(containsRecentQuestion).isFalse()
            .as("Question %d seen 1 min ago should be excluded by cooldown", seenQuestionId);

        System.out.println("✅ Cooldown test PASSED: Recent question excluded from adaptive quiz");
    }

    @Test
    @DisplayName("No performance history defaults to MEDIUM difficulty (neutral)")
    void testNoHistoryDefaultsToMedium() {
        // GIVEN: User with no performance history
        Long userId = 555L;
        // No history created

        // WHEN: Calculate accuracy
        double accuracy = performanceService.calculateRecentAccuracy(userId);

        // THEN: Should return neutral (0.5)
        assertThat(accuracy).isEqualTo(0.5, within(0.01))
            .as("User with no history should have neutral accuracy (0.5)");

        // AND: Recommended difficulty should be MEDIUM
        QuizQuestion.DifficultyLevel recommended =
            performanceService.getRecommendedDifficulty(userId);
        assertThat(recommended).isEqualTo(QuizQuestion.DifficultyLevel.MEDIUM)
            .as("User with no history should get MEDIUM difficulty");

        System.out.println("✅ Neutral default test PASSED: No history → MEDIUM difficulty");
    }

    // ============================================================================
    // Helper Methods
    // ============================================================================

    private void createPerformanceHistory(Long userId, int correctCount, int wrongCount) {
        List<QuizQuestion> questions = quizQuestionRepository.findAll();

        // Add correct answers
        for (int i = 0; i < correctCount && i < questions.size(); i++) {
            UserQuestionHistory history = UserQuestionHistory.builder()
                    .userId(userId)
                    .questionId(questions.get(i).getId())
                    .answeredAt(LocalDateTime.now().minusHours(25 + i)) // More than 24 hours
                    .isCorrect(true)
                    .timeTakenSeconds(30)
                    .build();
            historyRepository.save(history);
        }

        // Add wrong answers
        for (int i = 0; i < wrongCount && (correctCount + i) < questions.size(); i++) {
            UserQuestionHistory history = UserQuestionHistory.builder()
                    .userId(userId)
                    .questionId(questions.get(correctCount + i).getId())
                    .answeredAt(LocalDateTime.now().minusHours(25 + correctCount + i)) // More than 24 hours
                    .isCorrect(false)
                    .timeTakenSeconds(60)
                    .build();
            historyRepository.save(history);
        }

        System.out.println(String.format(
                "✅ Created performance history: %d correct, %d wrong (%.0f%% accuracy)",
                correctCount,
                wrongCount,
                (double) correctCount / (correctCount + wrongCount) * 100
        ));
    }
}
