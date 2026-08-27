package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.enums.CategoryContentScope;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.dto.admin.AdminTheoryBankHealthDtos.BankHealthResponse;
import com.readyroad.readyroadbackend.dto.admin.AdminTheoryCategoryRequest;
import com.readyroad.readyroadbackend.service.AdminTheoryBankHealthStore.PerformanceRow;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminTheoryBankHealthServiceTest {

    @Mock QuizQuestionRepository questionRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock AdminTheoryBankHealthStore store;

    private AdminTheoryBankHealthService service;
    private Category category;

    @BeforeEach
    void setUp() {
        service = new AdminTheoryBankHealthService(questionRepository, categoryRepository, store);
        category = category(1L, "TH01", 100);
    }

    @Test
    void reportsDynamicLocaleGapsInvalidStructureAndRealExposure() {
        QuizQuestion valid = question(1L, category, "Question", true);
        QuizQuestion missingFrench = question(2L, category, "Question", true);
        missingFrench.setQuestionFr("Option A");
        QuizQuestion invalid = question(3L, category, "Question", false);

        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(questionRepository.findAllForTheoryBankHealth()).thenReturn(List.of(valid, missingFrench, invalid));
        when(store.theoryPresentations()).thenReturn(Map.of(1L, 7L, 2L, 2L));
        when(store.completedPerformanceByLocale()).thenReturn(Map.of());

        BankHealthResponse response = service.bankHealth();

        assertThat(response.summary().totalQuestions()).isEqualTo(3);
        assertThat(response.summary().eligibleAllLocales()).isEqualTo(1);
        assertThat(response.summary().translationGapQuestions()).isEqualTo(1);
        assertThat(response.summary().invalidQuestions()).isEqualTo(1);
        assertThat(response.locales()).filteredOn(locale -> locale.locale().equals("fr"))
                .singleElement().extracting(locale -> locale.eligibleQuestions()).isEqualTo(1L);
        assertThat(response.categories()).singleElement()
                .satisfies(health -> {
                    assertThat(health.totalPresentations()).isEqualTo(9);
                    assertThat(health.representationStatus()).isEqualTo("UNDERREPRESENTED");
                });
        assertThat(response.heavilyExposedQuestions()).singleElement()
                .satisfies(exposure -> {
                    assertThat(exposure.questionId()).isEqualTo(1L);
                    assertThat(exposure.presentations()).isEqualTo(7L);
                });
    }

    @Test
    void doesNotLabelNeverPresentedQuestionsAsHeavilyExposed() {
        QuizQuestion question = question(1L, category, "Question", true);

        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(questionRepository.findAllForTheoryBankHealth()).thenReturn(List.of(question));
        when(store.theoryPresentations()).thenReturn(Map.of());
        when(store.completedPerformanceByLocale()).thenReturn(Map.of());

        BankHealthResponse response = service.bankHealth();

        assertThat(response.rarelyExposedQuestions()).singleElement();
        assertThat(response.heavilyExposedQuestions()).isEmpty();
    }

    @Test
    void flagsStatisticallySeparatedLocalePerformanceWithoutRewritingContent() {
        QuizQuestion question = question(1L, category, "Question", true);
        Map<String, PerformanceRow> localeRows = new LinkedHashMap<>();
        localeRows.put("ar", new PerformanceRow(100, 90, 8.0));
        localeRows.put("fr", new PerformanceRow(100, 10, 9.0));

        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(questionRepository.findAllForTheoryBankHealth()).thenReturn(List.of(question));
        when(store.theoryPresentations()).thenReturn(Map.of(1L, 200L));
        when(store.completedPerformanceByLocale()).thenReturn(Map.of(1L, localeRows));

        BankHealthResponse response = service.bankHealth();

        assertThat(response.questionsNeedingReview()).singleElement()
                .satisfies(quality -> {
                    assertThat(quality.flags()).contains("LOCALE_DIVERGENCE");
                    assertThat(quality.performanceByLocale().get("ar").correctRate()).isEqualTo(90.0);
                    assertThat(quality.performanceByLocale().get("fr").correctRate()).isEqualTo(10.0);
                });
        assertThat(question.getQuestionFr()).isEqualTo("Question");
    }

    @Test
    void fiveEligibleQuestionsMeetTheSharedBlueprintMinimum() {
        List<QuizQuestion> questions = List.of(
                question(1L, category, "Question 1", true),
                question(2L, category, "Question 2", true),
                question(3L, category, "Question 3", true),
                question(4L, category, "Question 4", true),
                question(5L, category, "Question 5", true));

        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(questionRepository.findAllForTheoryBankHealth()).thenReturn(questions);
        when(store.theoryPresentations()).thenReturn(Map.of());
        when(store.completedPerformanceByLocale()).thenReturn(Map.of());

        BankHealthResponse response = service.bankHealth();

        assertThat(response.categories()).singleElement().satisfies(health -> {
            assertThat(health.eligibleAllLocales()).isEqualTo(5);
            assertThat(health.representationStatus()).isEqualTo("BALANCED");
        });
    }
    @Test
    void categoryUpdatesPreserveTheStableCodeAndPersistAdminConfiguration() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));
        AdminTheoryCategoryRequest request = request("TH01", 16, false);

        var response = service.updateCategory(1L, request);

        assertThat(response.code()).isEqualTo("TH01");
        assertThat(response.examTargetWeight()).isEqualTo(16);
        assertThat(response.active()).isFalse();
        verify(categoryRepository).save(category);

        assertThatThrownBy(() -> service.updateCategory(1L, request("CHANGED", 16, true)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("stable identifier");
    }

    private static Category category(long id, String code, Integer weight) {
        Category category = new Category();
        category.setId(id);
        category.setCode(code);
        category.setNameEn("Rules");
        category.setNameNl("Regels");
        category.setNameFr("Regles");
        category.setNameAr("Rules AR");
        category.setDisplayOrder(1);
        category.setIsActive(true);
        category.setContentScope(CategoryContentScope.THEORETICAL_EXAM);
        category.setExamTargetWeight(weight);
        return category;
    }

    private static QuizQuestion question(long id, Category category, String text, boolean validCorrectCount) {
        QuizQuestion question = new QuizQuestion();
        question.setId(id);
        question.setCategory(category);
        question.setQuestionEn(text);
        question.setQuestionNl(text);
        question.setQuestionFr(text);
        question.setQuestionAr(text);
        question.setExplanationEn("Explanation");
        question.setExplanationNl("Explanation");
        question.setExplanationFr("Explanation");
        question.setExplanationAr("Explanation");
        question.setDifficultyLevel(QuizQuestion.DifficultyLevel.MEDIUM);
        question.setIsActive(true);
        question.setStatus(QuizQuestion.QuestionStatus.PUBLISHED);
        question.addOption(option(11L + id * 10, "Correct", true));
        question.addOption(option(12L + id * 10, "Incorrect", !validCorrectCount));
        return question;
    }

    private static QuizAnswerOption option(long id, String text, boolean correct) {
        QuizAnswerOption option = new QuizAnswerOption();
        option.setId(id);
        option.setOptionTextEn(text);
        option.setOptionTextNl(text);
        option.setOptionTextFr(text);
        option.setOptionTextAr(text);
        option.setIsCorrect(correct);
        option.setIsActive(true);
        option.setDisplayOrder(correct ? 1 : 2);
        return option;
    }

    private static AdminTheoryCategoryRequest request(String code, Integer weight, boolean active) {
        return new AdminTheoryCategoryRequest(
                code, "Rules", "Regels", "Regles", "Rules AR",
                null, null, null, null, 1, active, "THEORETICAL_EXAM", weight);
    }
}
