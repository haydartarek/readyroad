package com.readyroad.readyroadbackend.integration;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.exception.BelgianComplianceException;
import com.readyroad.readyroadbackend.exception.QuestionNotFoundException;
import com.readyroad.readyroadbackend.service.QuestionPublishService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Story D3 & D4: Belgian Compliance Enforcement - BDD Integration Tests
 *
 * D3: Traffic sign integration
 * D4: Content validation gates
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Stories D3/D4: Belgian Compliance Gates - Integration Tests")
public class BelgianComplianceGatesIntegrationTest {

    @Autowired
    private QuestionPublishService publishService;

    @Autowired
    private QuizQuestionRepository questionRepository;

    @Autowired
    private RoadSignRepository RoadSignRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;
    private RoadSign testSign;

    @BeforeEach
    void setUp() {
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

        // Create test traffic sign
        testSign = new RoadSign();
        testSign.setSignCode("A1");
        testSign.setNormalizedSignCode("a1");
        testSign.setNameEn("Danger Ahead");
        testSign.setNameAr("خطر أمامك");
        testSign.setNameNl("Gevaar vooruit");
        testSign.setNameFr("Danger devant");
        testSign.setCategory(SignCategory.DANGER);
        testSign.setIsActive(true);
        testSign = RoadSignRepository.save(testSign);
    }

    // ==========================================
    // Story D3: Traffic Sign Integration Tests
    // ==========================================

    @Test
    @DisplayName("@D3 Cannot publish question without traffic sign")
    void cannotPublishWithoutTrafficSign() {
        // Given: A valid question WITHOUT traffic sign
        QuizQuestion question = createValidQuestion();
        question.setRoadSign(null); // Remove traffic sign
        question = questionRepository.save(question);

        Long questionId = question.getId();

        // When/Then: Publishing fails with Belgian compliance error
        assertThatThrownBy(() -> publishService.publishQuestion(questionId))
            .isInstanceOf(BelgianComplianceException.class)
            .hasMessageContaining("traffic sign")
            .hasMessageContaining("legal context");
    }

    @Test
    @DisplayName("@D3 Can publish question with traffic sign")
    void canPublishWithTrafficSign() {
        // Given: A valid question WITH traffic sign
        QuizQuestion question = createValidQuestion();
        question.setRoadSign(testSign);
        question = questionRepository.save(question);

        // When: Publishing
        publishService.publishQuestion(question.getId());

        // Then: Success
        QuizQuestion published = questionRepository.findById(question.getId()).orElseThrow();
        assertThat(published.getStatus()).isEqualTo(QuizQuestion.QuestionStatus.PUBLISHED);
        assertThat(published.getPublishedAt()).isNotNull();
        assertThat(published.getRoadSign()).isNotNull();
    }

    @Test
    @DisplayName("@D3 Traffic sign linkage is stable after publish")
    void trafficSignLinkageIsStable() {
        // Given: A published question
        QuizQuestion question = createValidQuestion();
        question.setRoadSign(testSign);
        question = questionRepository.save(question);
        publishService.publishQuestion(question.getId());

        Long questionId = question.getId();
        Long expectedSignId = testSign.getId();

        // When: Retrieving multiple times
        QuizQuestion fetch1 = questionRepository.findById(questionId).orElseThrow();
        QuizQuestion fetch2 = questionRepository.findById(questionId).orElseThrow();

        // Then: Same traffic sign every time
        assertThat(fetch1.getRoadSign().getId()).isEqualTo(expectedSignId);
        assertThat(fetch2.getRoadSign().getId()).isEqualTo(expectedSignId);
    }

    // ==========================================
    // Story D4: Content Validation Gate Tests
    // ==========================================

    @Test
    @DisplayName("@D4 Publishing validates full Belgian compliance")
    void publishingValidatesFullCompliance() {
        // Given: A question with NL for DB save, but will fail publish validation
        QuizQuestion question = createValidQuestion();
        question.setQuestionNl("Wat betekent dit bord?"); // ✅ Set for DB NOT NULL constraint
        question = questionRepository.save(question);

        // Now remove NL to trigger publish validation failure
        question.setQuestionNl(null);
        question = questionRepository.save(question);

        Long questionId = question.getId();

        // When/Then: Publishing fails
        assertThatThrownBy(() -> publishService.publishQuestion(questionId))
            .isInstanceOf(BelgianComplianceException.class)
            .hasMessageContaining("NL");
    }

    @Test
    @DisplayName("@D4 Cannot publish question with 4 options")
    void cannotPublishWith4Options() {
        // Given: A question with 4 options (violates D1)
        QuizQuestion question = createQuestionWithOptions(4);

        // When/Then: Saving fails with validation error
        assertThatThrownBy(() -> questionRepository.saveAndFlush(question))
            .isInstanceOf(jakarta.validation.ConstraintViolationException.class)
            .hasMessageContaining("2-3 options");
    }

    @Test
    @DisplayName("@D4 Cannot publish question with 1 option")
    void cannotPublishWith1Option() {
        // Given: A question with 1 option (violates D1)
        QuizQuestion question = createQuestionWithOptions(1);

        // When/Then: Saving fails with validation error
        assertThatThrownBy(() -> questionRepository.saveAndFlush(question))
            .isInstanceOf(jakarta.validation.ConstraintViolationException.class)
            .hasMessageContaining("2-3 options");
    }

    @Test
    @DisplayName("@D4 Published questions cannot be republished")
    void cannotRepublishPublishedQuestion() {
        // Given: An already published question
        QuizQuestion question = createValidQuestion();
        question = questionRepository.save(question);
        publishService.publishQuestion(question.getId());

        Long questionId = question.getId();

        // When/Then: Re-publishing fails
        assertThatThrownBy(() -> publishService.publishQuestion(questionId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("already published");
    }

    @Test
    @DisplayName("@D4 canPublish returns false for invalid questions")
    void canPublishReturnsFalseForInvalid() {
        // Given: A question without traffic sign
        QuizQuestion question = createValidQuestion();
        question.setRoadSign(null);
        question = questionRepository.save(question);

        // When: Checking if publishable
        boolean canPublish = publishService.canPublish(question.getId());

        // Then: Cannot publish
        assertThat(canPublish).isFalse();
    }

    @Test
    @DisplayName("@D4 canPublish returns true for valid questions")
    void canPublishReturnsTrueForValid() {
        // Given: A fully compliant question
        QuizQuestion question = createValidQuestion();
        question = questionRepository.save(question);

        // When: Checking if publishable
        boolean canPublish = publishService.canPublish(question.getId());

        // Then: Can publish
        assertThat(canPublish).isTrue();
    }

    @Test
    @DisplayName("@D4 getPublishValidationErrors returns detailed errors")
    void getPublishValidationErrorsReturnsDetails() {
        // Given: A question with all required DB fields, but publish violations
        QuizQuestion question = createValidQuestion();
        question.setQuestionNl("Wat betekent dit bord?"); // ✅ Set for DB
        question = questionRepository.save(question);

        // Now violate publish rules
        question.setRoadSign(null); // Missing sign
        question.setQuestionNl(null);  // Missing NL
        question = questionRepository.save(question);

        // When: Getting validation errors
        var errors = publishService.getPublishValidationErrors(question.getId());

        // Then: Multiple errors returned
        assertThat(errors).isNotEmpty();
        assertThat(errors.size()).isGreaterThanOrEqualTo(2);
    }

    // Helper methods

    private QuizQuestion createValidQuestion() {
        QuizQuestion question = new QuizQuestion();
        question.setQuestionEn("What does this sign mean?");
        question.setQuestionAr("ما معنى هذه الإشارة؟");
        question.setQuestionNl("Wat betekent dit bord?");
        question.setQuestionFr("Que signifie ce panneau?");
        question.setQuestionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE);
        question.setDifficultyLevel(QuizQuestion.DifficultyLevel.MEDIUM);
        question.setCategory(testCategory);
        question.setRoadSign(testSign);
        question.setIsActive(true);
        question.setStatus(QuizQuestion.QuestionStatus.DRAFT);

        // Add 2 options (valid per D1)
        addOption(question, "Stop", true);
        addOption(question, "Yield", false);

        return question;
    }

    private QuizQuestion createQuestionWithOptions(int optionCount) {
        QuizQuestion question = createValidQuestion();
        question.getOptions().clear(); // Remove existing options

        // Add specified number of options
        for (int i = 0; i < optionCount; i++) {
            addOption(question, "Option " + (i + 1), i == 0);
        }

        return question;
    }

    private void addOption(QuizQuestion question, String text, boolean correct) {
        QuizAnswerOption option = new QuizAnswerOption();
        option.setOptionTextEn(text);
        option.setOptionTextAr(text + " AR");
        option.setOptionTextNl(text + " NL");
        option.setOptionTextFr(text + " FR");
        option.setIsCorrect(correct);
        option.setDisplayOrder(question.getOptions().size() + 1);
        question.addOption(option);
    }
}
