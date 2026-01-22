package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.model.UserQuestionHistory;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.UserQuestionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SmartQuizService Integration Test - Phase 3 MVP
 *
 * Purpose: Prove 24h cooldown works (Law #1)
 * Scope: ONE critical test - question doesn't repeat within window
 *
 * Strategy:
 * - Uses test profile (H2 database)
 * - Transactional (rollback after test)
 * - Tests core cooldown logic only
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SmartQuizCooldownIntegrationTest {

    @Autowired
    private SmartQuizService smartQuizService;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private UserQuestionHistoryRepository historyRepository;

    private static final Long TEST_USER_ID = 999L;
    private Long testQuestionId;

    @BeforeEach
    void setUp() {
        // Clean test data
        historyRepository.deleteAll();
        quizQuestionRepository.deleteAll();

        // Create test questions
        createTestQuestion(1L, "Test Question 1");
        createTestQuestion(2L, "Test Question 2");
        createTestQuestion(3L, "Test Question 3");
        createTestQuestion(4L, "Test Question 4");
        createTestQuestion(5L, "Test Question 5");

        // Get first available question for testing
        List<com.readyroad.readyroadbackend.domain.entity.QuizQuestion> questions =
            quizQuestionRepository.findAll();

        if (questions.isEmpty()) {
            throw new IllegalStateException(
                "Test setup failed - no questions created"
            );
        }

        testQuestionId = questions.get(0).getId();
    }

    private void createTestQuestion(Long id, String title) {
        com.readyroad.readyroadbackend.domain.entity.QuizQuestion question =
            new com.readyroad.readyroadbackend.domain.entity.QuizQuestion();

        question.setQuestionAr(title + " AR");
        question.setQuestionEn(title + " EN");
        question.setQuestionNl(title + " NL");
        question.setQuestionFr(title + " FR");
        question.setQuestionType(com.readyroad.readyroadbackend.domain.entity.QuizQuestion.QuestionType.MULTIPLE_CHOICE);
        question.setDifficultyLevel(com.readyroad.readyroadbackend.domain.entity.QuizQuestion.DifficultyLevel.EASY);
        question.setIsActive(true);

        // Belgian Compliance: 2-3 options required
        QuizAnswerOption option1 = new QuizAnswerOption();
        option1.setOptionTextEn("Correct Answer");
        option1.setOptionTextAr("الإجابة الصحيحة");
        option1.setOptionTextNl("Correct Antwoord");
        option1.setOptionTextFr("Réponse correcte");
        option1.setIsCorrect(true);
        option1.setQuestion(question);

        QuizAnswerOption option2 = new QuizAnswerOption();
        option2.setOptionTextEn("Wrong Answer 1");
        option2.setOptionTextAr("إجابة خاطئة 1");
        option2.setOptionTextNl("Fout Antwoord 1");
        option2.setOptionTextFr("Mauvaise réponse 1");
        option2.setIsCorrect(false);
        option2.setQuestion(question);

        QuizAnswerOption option3 = new QuizAnswerOption();
        option3.setOptionTextEn("Wrong Answer 2");
        option3.setOptionTextAr("إجابة خاطئة 2");
        option3.setOptionTextNl("Fout Antwoord 2");
        option3.setOptionTextFr("Mauvaise réponse 2");
        option3.setIsCorrect(false);
        option3.setQuestion(question);

        question.getOptions().add(option1);
        question.getOptions().add(option2);
        question.getOptions().add(option3);

        quizQuestionRepository.save(question);
    }

    /**
     * 🎯 THE ONE TEST THAT MATTERS
     *
     * Proves: Questions seen recently do NOT appear in smart quiz
     *
     * If this passes: Phase 3 MVP is VERIFIED
     * If this fails: Phase 3 is NOT working
     */
    @Test
    @DisplayName("Phase 3 MVP: Question seen 1 minute ago does NOT repeat")
    void testQuestionDoesNotRepeatWithin24Hours() {
        // GIVEN: User saw a question 1 minute ago
        UserQuestionHistory recentHistory = UserQuestionHistory.builder()
            .userId(TEST_USER_ID)
            .questionId(testQuestionId)
            .answeredAt(LocalDateTime.now().minusMinutes(1))
            .build();

        historyRepository.save(recentHistory);
        historyRepository.flush();

        // WHEN: User requests smart quiz
        List<QuizQuestion> smartQuiz = smartQuizService.generateSmartQuiz(
            TEST_USER_ID,
            10
        );

        // THEN: The recent question should NOT be in the quiz
        List<Long> quizQuestionIds = smartQuiz.stream()
            .map(QuizQuestion::getId)
            .toList();

        assertFalse(
            quizQuestionIds.contains(testQuestionId),
            String.format(
                "Question %d was seen 1 minute ago but appeared in smart quiz. " +
                "24h cooldown is NOT working! Quiz IDs: %s",
                testQuestionId,
                quizQuestionIds
            )
        );

        // Additional verification: Quiz should contain OTHER questions
        assertFalse(
            smartQuiz.isEmpty(),
            "Smart quiz is empty - should return questions that were NOT seen recently"
        );

        System.out.println("✅ Phase 3 MVP VERIFIED: Cooldown working correctly");
        System.out.println("   - Excluded question: " + testQuestionId);
        System.out.println("   - Returned questions: " + quizQuestionIds.size());
    }

    /**
     * Bonus verification: Quiz records history for future cooldown
     */
    @Test
    @DisplayName("Smart quiz records question history for future cooldown")
    void testSmartQuizRecordsHistory() {
        // GIVEN: Clean history
        long countBefore = historyRepository.count();

        // WHEN: User takes smart quiz
        List<QuizQuestion> quiz = smartQuizService.generateSmartQuiz(TEST_USER_ID, 5);

        // THEN: History should be recorded
        long countAfter = historyRepository.count();

        assertEquals(
            countBefore + quiz.size(),
            countAfter,
            "Smart quiz should record history for all returned questions"
        );

        // Verify user ID is correct
        List<UserQuestionHistory> userHistory =
            historyRepository.findByUserIdAndAnsweredAtAfter(
                TEST_USER_ID,
                LocalDateTime.now().minusMinutes(1)
            );

        assertEquals(
            quiz.size(),
            userHistory.size(),
            "History should match quiz size for user " + TEST_USER_ID
        );

        System.out.println("✅ History recording verified");
    }
}
