package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration Test for Story D1: Enforce 2-3 Options Rule
 *
 * Tests Belgian compliance validation for quiz questions.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BelgianComplianceIntegrationTest {

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizQuestionRepository questionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setCode("SIGNS"); // 5 chars - fits VARCHAR(10) limit
        testCategory.setNameEn("Traffic Signs");
        testCategory.setNameAr("إشارات المرور");
        testCategory.setNameNl("Verkeersborden");
        testCategory.setNameFr("Panneaux de signalisation");
        testCategory.setIsActive(true);
        testCategory = categoryRepository.save(testCategory);
    }

    @Test
    @DisplayName("Story D1: Cannot create question with 4 options")
    void testCreateQuestionWith4Options() {
        // Given - Question with 4 options (invalid)
        QuizQuestion question = createQuestion(4);

        // When & Then - Should fail at save time due to @BelgianOptionsCount validation
        assertThatThrownBy(() -> questionRepository.save(question))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class)
                .hasMessageContaining("Belgian standard requires 2-3 options");
    }

    @Test
    @DisplayName("Story D1: Cannot create question with 1 option")
    void testCannotCreateWithOneOption() {
        // Given - Question with 1 option (invalid)
        QuizQuestion question = createQuestion(1);

        // When & Then - Should fail at save time due to @BelgianOptionsCount validation
        assertThatThrownBy(() -> questionRepository.save(question))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class)
                .hasMessageContaining("Belgian standard requires 2-3 options");
    }

    @Test
    @DisplayName("Story D1: Can create question with 2 options")
    void testCanCreateWith2Options() {
        // Given - Question with 2 options (valid)
        QuizQuestion question = createQuestion(2);
        QuizQuestion savedQuestion = questionRepository.save(question);

        // When & Then - Should not throw
        assertThatCode(() -> quizService.validateBelgianCompliance(savedQuestion))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Story D1: Can create question with 3 options")
    void testCanCreateWith3Options() {
        // Given - Question with 3 options (valid)
        QuizQuestion question = createQuestion(3);
        QuizQuestion savedQuestion = questionRepository.save(question);

        // When & Then - Should not throw
        assertThatCode(() -> quizService.validateBelgianCompliance(savedQuestion))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Story D1: isCompliantQuestion uses deliverable options for medium questions")
    void testIsCompliantUsesDeliverableOptionsForMediumQuestion() {
        // Given
        QuizQuestion question = createQuestion(4);

        // When
        boolean isCompliant = quizService.isCompliantQuestion(question);

        // Then
        assertThat(isCompliant).isTrue();
    }

    @Test
    @DisplayName("Story D1: isCompliantQuestion returns true for 2 options")
    void testIsCompliantReturnsTrueFor2Options() {
        // Given
        QuizQuestion question = createQuestion(2);

        // When
        boolean isCompliant = quizService.isCompliantQuestion(question);

        // Then
        assertThat(isCompliant).isTrue();
    }

    @Test
    @DisplayName("Story D1: isCompliantQuestion returns true for 3 options")
    void testIsCompliantReturnsTrueFor3Options() {
        // Given
        QuizQuestion question = createQuestion(3);

        // When
        boolean isCompliant = quizService.isCompliantQuestion(question);

        // Then
        assertThat(isCompliant).isTrue();
    }

    // Helper methods
    private QuizQuestion createQuestion(int optionCount) {
        QuizQuestion question = new QuizQuestion();
        question.setQuestionEn("What is the speed limit?");
        question.setQuestionAr("ما هي السرعة؟");
        question.setQuestionNl("Wat is de snelheidslimiet?");
        question.setQuestionFr("Quelle est la limite?");
        question.setQuestionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE);
        question.setDifficultyLevel(QuizQuestion.DifficultyLevel.MEDIUM);
        question.setCategory(testCategory);
        question.setIsActive(true);
        question.setOptions(new ArrayList<>());

        // Add requested number of options
        for (int i = 1; i <= optionCount; i++) {
            QuizAnswerOption option = new QuizAnswerOption();
            option.setOptionTextEn("Option " + i);
            option.setOptionTextAr("الخيار " + i);
            option.setOptionTextNl("Optie " + i);
            option.setOptionTextFr("Option " + i);
            option.setIsCorrect(i == 1); // First option is correct
            option.setQuestion(question);
            question.getOptions().add(option);
        }

        return question;
    }
}
