package com.readyroad.readyroadbackend.integration;

import com.readyroad.readyroadbackend.domain.entity.*;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.entity.UserQuestionHistory;
import com.readyroad.readyroadbackend.domain.repository.*;
import com.readyroad.readyroadbackend.dto.practice.SubmitPracticeAnswerRequest;
import com.readyroad.readyroadbackend.dto.practice.SubmitPracticeAnswerResponse;
import com.readyroad.readyroadbackend.exception.InvalidAnswerException;
import com.readyroad.readyroadbackend.exception.QuestionNotFoundException;
import com.readyroad.readyroadbackend.service.PracticeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests for Story B1: Submit Practice Answer
 *
 * Tests:
 * 1. Submit correct answer
 * 2. Submit wrong answer
 * 3. Updates user question history (24h cooldown)
 * 4. Updates category progress
 * 5. Calculates mastery level correctly
 * 6. Invalid question ID throws exception
 * 7. Invalid option ID throws exception
 * 8. Option not belonging to question throws exception
 *
 * @author ReadyRoad Team
 * @since Phase 5 - Story B1
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Story B1: Submit Practice Answer - Integration Tests")
class PracticeAnswerSubmissionIntegrationTest {

    @Autowired
    private PracticeService practiceService;

    @Autowired
    private QuizQuestionRepository questionRepository;

    @Autowired
    private QuizAnswerOptionRepository optionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserQuestionHistoryRepository historyRepository;

    @Autowired
    private UserCategoryProgressRepository progressRepository;

    @Autowired
    private UserRepository userRepository;

    private Long testUserId;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Clean up
        historyRepository.deleteAll();
        progressRepository.deleteAll();
        optionRepository.deleteAll();
        questionRepository.deleteAll();

        // Create test user
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setPasswordHash("dummy_hash");
        testUser.setRole(Role.USER);
        testUser.setIsActive(true);
        testUser = userRepository.save(testUser);
        testUserId = testUser.getId();

        // Use a dedicated parent fixture and its generated ID. findAll() is cached in
        // production and can legitimately contain entries rolled back by another test.
        testCategory = categoryRepository.findByCode("PRACTICE")
                .orElseGet(() -> {
                    Category cat = new Category();
                    cat.setCode("PRACTICE");
                    cat.setNameEn("Test Category");
                    cat.setNameAr("فئة الاختبار");
                    cat.setNameNl("Testcategorie");
                    cat.setNameFr("Catégorie de test");
                    cat.setDisplayOrder(1);
                    cat.setIsActive(true);
                    return categoryRepository.saveAndFlush(cat);
                });
    }

    @Test
    @DisplayName("Test 1: Submit correct answer")
    void testSubmitCorrectAnswer() {
        // Given: Question with correct option
        QuizQuestion question = createTestQuestion();
        QuizAnswerOption correctOption = question.getOptions().stream()
                .filter(QuizAnswerOption::getIsCorrect)
                .findFirst()
                .orElseThrow();

        SubmitPracticeAnswerRequest request = SubmitPracticeAnswerRequest.builder()
                .selectedOptionId(correctOption.getId())
                .timeTakenSeconds(15)
                .build();

        // When: User submits correct answer
        SubmitPracticeAnswerResponse response = practiceService.submitPracticeAnswer(
                testUserId,
                question.getId(),
                request);

        // Then: Response shows correctness
        assertNotNull(response);
        assertTrue(response.getIsCorrect(), "Answer should be marked as correct");
        assertEquals(question.getId(), response.getQuestionId());
        assertEquals(correctOption.getId(), response.getSelectedOptionId());
        assertEquals(correctOption.getId(), response.getCorrectOptionId());

        // Verify progress updated
        assertNotNull(response.getUpdatedAccuracy());
        assertEquals(0, response.getUpdatedAccuracy().compareTo(BigDecimal.valueOf(100.0).setScale(2)));
        assertEquals(1, response.getTotalAttempts());
        assertEquals(1, response.getCorrectAttempts());
        assertEquals("ADVANCED", response.getMasteryLevel()); // 100% accuracy = ADVANCED (≥80%)
    }

    @Test
    @DisplayName("Test 2: Submit wrong answer")
    void testSubmitWrongAnswer() {
        // Given: Question with wrong option selected
        QuizQuestion question = createTestQuestion();
        QuizAnswerOption wrongOption = question.getOptions().stream()
                .filter(opt -> !opt.getIsCorrect())
                .findFirst()
                .orElseThrow();
        QuizAnswerOption correctOption = question.getOptions().stream()
                .filter(QuizAnswerOption::getIsCorrect)
                .findFirst()
                .orElseThrow();

        SubmitPracticeAnswerRequest request = SubmitPracticeAnswerRequest.builder()
                .selectedOptionId(wrongOption.getId())
                .timeTakenSeconds(20)
                .build();

        // When: User submits wrong answer
        SubmitPracticeAnswerResponse response = practiceService.submitPracticeAnswer(
                testUserId,
                question.getId(),
                request);

        // Then: Response shows incorrectness and provides correct answer
        assertNotNull(response);
        assertFalse(response.getIsCorrect(), "Answer should be marked as incorrect");
        assertEquals(wrongOption.getId(), response.getSelectedOptionId());
        assertEquals(correctOption.getId(), response.getCorrectOptionId());
        assertNotEquals(response.getSelectedOptionId(), response.getCorrectOptionId());

        // Verify explanation provided
        assertNotNull(response.getExplanationEn());

        // Verify progress updated
        assertEquals(0, response.getUpdatedAccuracy().compareTo(BigDecimal.ZERO.setScale(2)));
        assertEquals(1, response.getTotalAttempts());
        assertEquals(0, response.getCorrectAttempts());
    }

    @Test
    @DisplayName("Test 3: Updates user question history (24h cooldown)")
    void testUpdatesHistory() {
        // Given: Question and answer
        QuizQuestion question = createTestQuestion();
        QuizAnswerOption option = question.getOptions().get(0);

        SubmitPracticeAnswerRequest request = SubmitPracticeAnswerRequest.builder()
                .selectedOptionId(option.getId())
                .timeTakenSeconds(15)
                .build();

        // When: User submits answer
        practiceService.submitPracticeAnswer(testUserId, question.getId(), request);

        // Then: History record created
        List<UserQuestionHistory> history = historyRepository
                .findByUserIdAndAnsweredAtAfter(testUserId, LocalDateTime.now().minusMinutes(1));

        assertEquals(1, history.size());
        UserQuestionHistory record = history.get(0);
        assertEquals(testUserId, record.getUserId());
        assertEquals(question.getId(), record.getQuestionId());
        assertEquals(option.getIsCorrect(), record.getIsCorrect());
        assertEquals(15, record.getTimeTakenSeconds());
        assertNotNull(record.getAnsweredAt());
        assertEquals("THEORY", record.getQuestionType());
        assertNull(record.getLastPresentedAt());
        assertEquals(0, record.getTimesPresented());
    }

    @Test
    @DisplayName("Test 4: Updates category progress")
    void testUpdatesCategoryProgress() {
        // Given: Multiple questions in same category
        QuizQuestion q1 = createTestQuestion();
        QuizQuestion q2 = createTestQuestion();
        QuizQuestion q3 = createTestQuestion();

        // When: User answers 2 correct, 1 wrong
        submitAnswer(q1, true);
        submitAnswer(q2, true);
        submitAnswer(q3, false);

        // Then: Progress reflects 2/3 correct (66.67%)
        UserCategoryProgress progress = progressRepository
                .findByUserIdAndCategoryId(testUserId, testCategory.getId())
                .orElseThrow();

        assertEquals(3, progress.getQuestionsAttempted());
        assertEquals(2, progress.getCorrectAnswers());
        assertEquals(66.67, progress.getAccuracyRate().doubleValue(), 0.01); // Double comparison with delta
        assertNotNull(progress.getLastPracticed());
    }

    @Test
    @DisplayName("Test 5: Calculates mastery level correctly")
    void testCalculatesMasteryLevel() {
        // Given: Multiple questions to reach different mastery levels

        // Scenario 1: 5 attempts, all correct (100%) = ADVANCED
        for (int i = 0; i < 5; i++) {
            QuizQuestion q = createTestQuestion();
            submitAnswer(q, true);
        }
        UserCategoryProgress progress = progressRepository
                .findByUserIdAndCategoryId(testUserId, testCategory.getId())
                .orElseThrow();
        assertEquals(UserCategoryProgress.MasteryLevel.ADVANCED, progress.getMasteryLevel()); // 100% = ADVANCED

        // Scenario 2: Add more to get 10 attempts with 70% accuracy = INTERMEDIATE
        for (int i = 0; i < 2; i++) {
            submitAnswer(createTestQuestion(), true); // Total: 7 correct
        }
        for (int i = 0; i < 3; i++) {
            submitAnswer(createTestQuestion(), false); // Total: 3 wrong = 7/10 = 70%
        }
        progress = progressRepository
                .findByUserIdAndCategoryId(testUserId, testCategory.getId())
                .orElseThrow();
        assertEquals(10, progress.getQuestionsAttempted());
        assertEquals(UserCategoryProgress.MasteryLevel.INTERMEDIATE, progress.getMasteryLevel());
    }

    @Test
    @DisplayName("Test 6: Invalid question ID throws exception")
    void testInvalidQuestionId() {
        // Given: Non-existent question ID
        Long invalidQuestionId = 99999L;
        SubmitPracticeAnswerRequest request = SubmitPracticeAnswerRequest.builder()
                .selectedOptionId(1L)
                .timeTakenSeconds(15)
                .build();

        // When & Then: Throws QuestionNotFoundException
        assertThrows(QuestionNotFoundException.class, () -> {
            practiceService.submitPracticeAnswer(testUserId, invalidQuestionId, request);
        });
    }

    @Test
    @DisplayName("Test 7: Invalid option ID throws exception")
    void testInvalidOptionId() {
        // Given: Valid question but invalid option ID
        QuizQuestion question = createTestQuestion();
        Long invalidOptionId = 99999L;

        SubmitPracticeAnswerRequest request = SubmitPracticeAnswerRequest.builder()
                .selectedOptionId(invalidOptionId)
                .timeTakenSeconds(15)
                .build();

        // When & Then: Throws InvalidAnswerException
        assertThrows(InvalidAnswerException.class, () -> {
            practiceService.submitPracticeAnswer(testUserId, question.getId(), request);
        });
    }

    @Test
    @DisplayName("Test 8: Option not belonging to question throws exception")
    void testOptionNotBelongsToQuestion() {
        // Given: Two questions with their own options
        QuizQuestion q1 = createTestQuestion();
        QuizQuestion q2 = createTestQuestion();
        QuizAnswerOption q2Option = q2.getOptions().get(0);

        SubmitPracticeAnswerRequest request = SubmitPracticeAnswerRequest.builder()
                .selectedOptionId(q2Option.getId()) // Option from Q2
                .timeTakenSeconds(15)
                .build();

        // When & Then: Throws InvalidAnswerException (trying to use Q2's option for Q1)
        assertThrows(InvalidAnswerException.class, () -> {
            practiceService.submitPracticeAnswer(testUserId, q1.getId(), request);
        });
    }

    // ============================================================================
    // Helper Methods
    // ============================================================================

    /**
     * Create test question with 2 options (Belgian standard)
     */
    private QuizQuestion createTestQuestion() {
        QuizQuestion question = new QuizQuestion();
        question.setCategory(testCategory);
        question.setQuestionEn("Test question");
        question.setQuestionAr("سؤال اختبار");
        question.setQuestionNl("Testvraag");
        question.setQuestionFr("Question de test");
        question.setExplanationEn("Test explanation");
        question.setExplanationAr("شرح الاختبار");
        question.setExplanationNl("Test uitleg");
        question.setExplanationFr("Explication du test");
        question.setDifficultyLevel(QuizQuestion.DifficultyLevel.MEDIUM);
        question.setQuestionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE);
        question.setIsActive(true);
        question.setStatus(QuizQuestion.QuestionStatus.PUBLISHED);
        question.setOptions(new ArrayList<>());

        // Create 2 options (Belgian standard) - add to list BEFORE saving question
        QuizAnswerOption correctOption = new QuizAnswerOption();
        correctOption.setQuestion(question);
        correctOption.setOptionTextEn("Correct answer");
        correctOption.setOptionTextAr("إجابة صحيحة");
        correctOption.setOptionTextNl("Correct antwoord");
        correctOption.setOptionTextFr("Réponse correcte");
        correctOption.setIsCorrect(true);
        correctOption.setDisplayOrder(1);

        QuizAnswerOption wrongOption = new QuizAnswerOption();
        wrongOption.setQuestion(question);
        wrongOption.setOptionTextEn("Wrong answer");
        wrongOption.setOptionTextAr("إجابة خاطئة");
        wrongOption.setOptionTextNl("Fout antwoord");
        wrongOption.setOptionTextFr("Mauvaise réponse");
        wrongOption.setIsCorrect(false);
        wrongOption.setDisplayOrder(2);

        // Add options to question BEFORE saving (Belgian validation requires 2-3
        // options)
        question.getOptions().add(correctOption);
        question.getOptions().add(wrongOption);

        // Now save question with options
        question = questionRepository.save(question);

        return question;
    }

    /**
     * Submit answer (helper for batch testing)
     */
    private void submitAnswer(QuizQuestion question, boolean submitCorrectAnswer) {
        QuizAnswerOption option = question.getOptions().stream()
                .filter(opt -> opt.getIsCorrect() == submitCorrectAnswer)
                .findFirst()
                .orElseThrow();

        SubmitPracticeAnswerRequest request = SubmitPracticeAnswerRequest.builder()
                .selectedOptionId(option.getId())
                .timeTakenSeconds(15)
                .build();

        practiceService.submitPracticeAnswer(testUserId, question.getId(), request);
    }
}
