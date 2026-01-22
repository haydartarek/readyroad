package com.readyroad.readyroadbackend.integration;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion.TypicalErrorType;
import com.readyroad.readyroadbackend.domain.model.UserQuestionHistory;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.UserQuestionHistoryRepository;
import com.readyroad.readyroadbackend.dto.ErrorPatternResponse;
import com.readyroad.readyroadbackend.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Story C1: View Error Patterns - BDD Integration Tests
 * Tests error pattern analytics functionality
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Story C1: View Error Patterns - Integration Tests")
public class StoryC1ErrorPatternsIntegrationTest {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private UserQuestionHistoryRepository historyRepository;

    @Autowired
    private QuizQuestionRepository questionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Long testUserId;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUserId = 1L;

        // Create test category
        testCategory = new Category();
        testCategory.setCode("SIGNS");
        testCategory.setNameEn("Traffic Signs");
        testCategory.setNameAr("إشارات المرور");
        testCategory.setNameNl("Verkeersborden");
        testCategory.setNameFr("Panneaux de signalisation");
        testCategory.setIsActive(true);
        testCategory.setDisplayOrder(1);
        testCategory = categoryRepository.save(testCategory);
    }

    @Test
    @DisplayName("@C1 returns exactly 6 supported error pattern types")
    void returnsExactlySixSupportedErrorPatternTypes() {
        // Given: User has at least 10 recorded wrong attempts across multiple categories
        createWrongAttempts(testUserId, 10);

        // When: I request error patterns
        List<ErrorPatternResponse> patterns = analyticsService.getErrorPatterns(testUserId);

        // Then: Response should contain exactly 6 items
        assertThat(patterns).hasSize(6);

        // And: Each item should have required fields
        for (ErrorPatternResponse pattern : patterns) {
            assertThat(pattern.getPatternType()).isNotNull();
            assertThat(pattern.getCount()).isNotNull();
            assertThat(pattern.getPercentage()).isNotNull();
            assertThat(pattern.getDescription()).isNotEmpty();
            assertThat(pattern.getExampleQuestions()).isNotNull();
        }

        // And: All 6 supported pattern types should be present
        assertThat(patterns.stream().map(ErrorPatternResponse::getPatternType))
            .containsExactlyInAnyOrder(
                TypicalErrorType.SIGN_CONFUSION,
                TypicalErrorType.SUPPLEMENTARY_IGNORED,
                TypicalErrorType.PRIORITY_MISUNDERSTANDING,
                TypicalErrorType.SPEED_LIMIT_ERROR,
                TypicalErrorType.ZONE_CONFUSION,
                TypicalErrorType.RULE_OVERGENERALIZATION
            );
    }

    @Test
    @DisplayName("@C1 patterns are sorted by frequency descending")
    void patternsAreSortedByFrequencyDescending() {
        // Given: User has wrong attempts where SIGN_CONFUSION count is higher than SPEED_LIMIT_ERROR
        // Create 6 SIGN_CONFUSION errors
        for (int i = 0; i < 6; i++) {
            createWrongAttemptWithPattern(testUserId, TypicalErrorType.SIGN_CONFUSION);
        }

        // Create 3 SPEED_LIMIT_ERROR errors
        for (int i = 0; i < 3; i++) {
            createWrongAttemptWithPattern(testUserId, TypicalErrorType.SPEED_LIMIT_ERROR);
        }

        // Create 1 PRIORITY_MISUNDERSTANDING error
        createWrongAttemptWithPattern(testUserId, TypicalErrorType.PRIORITY_MISUNDERSTANDING);

        // When: I request error patterns
        List<ErrorPatternResponse> patterns = analyticsService.getErrorPatterns(testUserId);

        // Then: Response should be sorted by count descending
        assertThat(patterns).hasSize(6);

        // And: First item count should be greater than or equal to second item count
        for (int i = 0; i < patterns.size() - 1; i++) {
            assertThat(patterns.get(i).getCount())
                .isGreaterThanOrEqualTo(patterns.get(i + 1).getCount());
        }

        // And: Specifically, SIGN_CONFUSION should be first (6 errors)
        assertThat(patterns.get(0).getPatternType()).isEqualTo(TypicalErrorType.SIGN_CONFUSION);
        assertThat(patterns.get(0).getCount()).isEqualTo(6);

        // And: SPEED_LIMIT_ERROR should be second (3 errors)
        assertThat(patterns.get(1).getPatternType()).isEqualTo(TypicalErrorType.SPEED_LIMIT_ERROR);
        assertThat(patterns.get(1).getCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("@C1 percentage is calculated correctly")
    void percentageIsCalculatedCorrectly() {
        // Given: User has 10 total wrong attempts
        // And: 4 of them are classified as SIGN_CONFUSION
        for (int i = 0; i < 4; i++) {
            createWrongAttemptWithPattern(testUserId, TypicalErrorType.SIGN_CONFUSION);
        }

        // And: 6 are other types
        for (int i = 0; i < 3; i++) {
            createWrongAttemptWithPattern(testUserId, TypicalErrorType.SPEED_LIMIT_ERROR);
        }
        for (int i = 0; i < 3; i++) {
            createWrongAttemptWithPattern(testUserId, TypicalErrorType.ZONE_CONFUSION);
        }

        // When: I request error patterns
        List<ErrorPatternResponse> patterns = analyticsService.getErrorPatterns(testUserId);

        // Then: SIGN_CONFUSION item percentage should be 40.0 (4 out of 10)
        ErrorPatternResponse signConfusion = patterns.stream()
            .filter(p -> p.getPatternType() == TypicalErrorType.SIGN_CONFUSION)
            .findFirst()
            .orElseThrow();

        assertThat(signConfusion.getPercentage()).isEqualTo(40.0);
        assertThat(signConfusion.getCount()).isEqualTo(4);
    }

    @Test
    @DisplayName("@C1 includes examples for each pattern")
    void includesExamplesForEachPattern() {
        // Given: User has at least 2 wrong attempts mapped to each pattern type
        for (TypicalErrorType type : List.of(
            TypicalErrorType.SIGN_CONFUSION,
            TypicalErrorType.SUPPLEMENTARY_IGNORED,
            TypicalErrorType.PRIORITY_MISUNDERSTANDING,
            TypicalErrorType.SPEED_LIMIT_ERROR,
            TypicalErrorType.ZONE_CONFUSION,
            TypicalErrorType.RULE_OVERGENERALIZATION
        )) {
            createWrongAttemptWithPattern(testUserId, type);
            createWrongAttemptWithPattern(testUserId, type);
        }

        // When: I request error patterns
        List<ErrorPatternResponse> patterns = analyticsService.getErrorPatterns(testUserId);

        // Then: Each pattern item should include exampleQuestions
        // And: exampleQuestions should not be empty
        for (ErrorPatternResponse pattern : patterns) {
            assertThat(pattern.getExampleQuestions())
                .as("Pattern %s should have examples", pattern.getPatternType())
                .isNotEmpty();

            // And: Each example should have required fields
            for (ErrorPatternResponse.ExampleQuestionDTO example : pattern.getExampleQuestions()) {
                assertThat(example.getQuestionId()).isNotNull();
                assertThat(example.getQuestionTextEn()).isNotEmpty();
                assertThat(example.getCategoryName()).isNotEmpty();
                assertThat(example.getTimesWrong()).isGreaterThan(0);
            }
        }
    }

    @Test
    @DisplayName("@C1 user isolation is enforced")
    void userIsolationIsEnforced() {
        // Given: User 888 has their own wrong attempts
        Long user888 = 888L;
        for (int i = 0; i < 5; i++) {
            createWrongAttemptWithPattern(user888, TypicalErrorType.SIGN_CONFUSION);
        }

        // And: Another user (999) has different wrong attempts
        Long user999 = 999L;
        for (int i = 0; i < 10; i++) {
            createWrongAttemptWithPattern(user999, TypicalErrorType.SPEED_LIMIT_ERROR);
        }

        // When: I request error patterns for user 888
        List<ErrorPatternResponse> patterns = analyticsService.getErrorPatterns(user888);

        // Then: Analytics must reflect only user 888 history
        ErrorPatternResponse signConfusion = patterns.stream()
            .filter(p -> p.getPatternType() == TypicalErrorType.SIGN_CONFUSION)
            .findFirst()
            .orElseThrow();

        assertThat(signConfusion.getCount()).isEqualTo(5);

        ErrorPatternResponse speedLimit = patterns.stream()
            .filter(p -> p.getPatternType() == TypicalErrorType.SPEED_LIMIT_ERROR)
            .findFirst()
            .orElseThrow();

        // User 888 should have 0 SPEED_LIMIT_ERROR attempts
        assertThat(speedLimit.getCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("@C1 @empty: returns empty list for user with no wrong attempts")
    void returnsEmptyListForUserWithNoWrongAttempts() {
        // Given: User has no wrong attempts (new user or perfect scores)
        Long newUserId = 999L;

        // When: I request error patterns
        List<ErrorPatternResponse> patterns = analyticsService.getErrorPatterns(newUserId);

        // Then: Response should be an empty list
        assertThat(patterns).isEmpty();
    }

    // Helper methods

    private void createWrongAttempts(Long userId, int count) {
        for (int i = 0; i < count; i++) {
            // Distribute across different pattern types
            TypicalErrorType type = TypicalErrorType.values()[i % 6];
            createWrongAttemptWithPattern(userId, type);
        }
    }

    private void createWrongAttemptWithPattern(Long userId, TypicalErrorType errorType) {
        // Create question with specific error type
        QuizQuestion question = new QuizQuestion();
        question.setQuestionEn("Test question for " + errorType);
        question.setQuestionAr("سؤال اختبار لـ " + errorType);
        question.setQuestionNl("Test vraag voor " + errorType);
        question.setQuestionFr("Question de test pour " + errorType);
        question.setExplanationEn("Explanation");
        question.setExplanationAr("شرح");
        question.setExplanationNl("Uitleg");
        question.setExplanationFr("Explication");
        question.setQuestionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE);
        question.setDifficultyLevel(QuizQuestion.DifficultyLevel.EASY);
        question.setCategory(testCategory);
        question.setTypicalErrorType(errorType);
        question.setIsActive(true);
        question.setOptions(new ArrayList<>());

        // Add options
        QuizAnswerOption correctOption = new QuizAnswerOption();
        correctOption.setOptionTextEn("Correct");
        correctOption.setOptionTextAr("صحيح");
        correctOption.setOptionTextNl("Correct");
        correctOption.setOptionTextFr("Correct");
        correctOption.setIsCorrect(true);
        correctOption.setDisplayOrder(1);
        correctOption.setQuestion(question);

        QuizAnswerOption wrongOption = new QuizAnswerOption();
        wrongOption.setOptionTextEn("Wrong");
        wrongOption.setOptionTextAr("خطأ");
        wrongOption.setOptionTextNl("Fout");
        wrongOption.setOptionTextFr("Faux");
        wrongOption.setIsCorrect(false);
        wrongOption.setDisplayOrder(2);
        wrongOption.setQuestion(question);

        question.getOptions().add(correctOption);
        question.getOptions().add(wrongOption);

        question = questionRepository.save(question);

        // Record wrong attempt in history
        UserQuestionHistory history = UserQuestionHistory.builder()
            .userId(userId)
            .questionId(question.getId())
            .answeredAt(LocalDateTime.now())
            .isCorrect(false)
            .timeTakenSeconds(30)
            .build();

        historyRepository.save(history);
    }
}
