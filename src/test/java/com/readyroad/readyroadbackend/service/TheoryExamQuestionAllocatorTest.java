package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.enums.CategoryContentScope;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.exception.ExamQuestionPoolUnavailableException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TheoryExamQuestionAllocatorTest {

    @Mock QuizQuestionRepository questionRepository;
    @Mock BackendMessageService messages;

    private TheoryExamQuestionAllocator allocator;
    private AtomicLong questionIds;

    @BeforeEach
    void setUp() {
        allocator = new TheoryExamQuestionAllocator(questionRepository, messages);
        questionIds = new AtomicLong(1);
    }

    @Test
    void canonicalWeightsAndAdminDifficultiesProduceTheReadyRoadTargets() {
        List<QuizQuestion> eligible = new ArrayList<>();
        eligible.addAll(categoryPool(category(1, "TH01", 14), 8, 10, 8));
        eligible.addAll(categoryPool(category(2, "TH02", 16), 8, 10, 8));
        eligible.addAll(categoryPool(category(3, "TH03", 14), 8, 10, 8));
        eligible.addAll(categoryPool(category(4, "TH04", 10), 8, 10, 8));
        eligible.addAll(categoryPool(category(5, "TH05", 16), 8, 10, 8));
        eligible.addAll(categoryPool(category(6, "TH06", 10), 8, 10, 8));
        eligible.addAll(categoryPool(category(7, "TH07", 10), 8, 10, 8));
        eligible.addAll(categoryPool(category(8, "TH08", 10), 8, 10, 8));

        TheoryExamQuestionAllocator.Allocation allocation = allocator.allocateEligibleQuestions(eligible);

        assertThat(allocation.questions()).hasSize(50);
        assertThat(allocation.questions()).extracting(QuizQuestion::getId).doesNotHaveDuplicates();
        assertThat(countByCategory(allocation.questions())).containsExactlyInAnyOrderEntriesOf(Map.of(
                "TH01", 7,
                "TH02", 8,
                "TH03", 7,
                "TH04", 5,
                "TH05", 8,
                "TH06", 5,
                "TH07", 5,
                "TH08", 5));
        assertThat(countByDifficulty(allocation.questions())).containsExactlyInAnyOrderEntriesOf(Map.of(
                QuizQuestion.DifficultyLevel.EASY, 15,
                QuizQuestion.DifficultyLevel.MEDIUM, 20,
                QuizQuestion.DifficultyLevel.HARD, 15));
        assertThat(allocation.difficultyRelaxed()).isFalse();
    }

    @Test
    void categoryWithFiveQuestionsIsExcludedWhileSixOrMoreParticipates() {
        Category excluded = category(1, "TH_SMALL", 10);
        Category eligible = category(2, "TH_READY", 10);
        List<QuizQuestion> pool = new ArrayList<>();
        pool.addAll(categoryPool(excluded, 2, 2, 1));
        pool.addAll(categoryPool(eligible, 20, 20, 20));

        TheoryExamQuestionAllocator.Allocation allocation = allocator.allocateEligibleQuestions(pool);

        assertThat(allocation.questions()).hasSize(50)
                .allSatisfy(question -> assertThat(question.getCategory().getCode()).isEqualTo("TH_READY"));
        assertThat(allocation.categoryTargets()).doesNotContainKey(excluded.getId());
        assertThat(allocation.categoryTargets()).containsEntry(eligible.getId(), 50);
    }

    @Test
    void difficultyPreferenceRelaxesBeforeEligibleCategoryCoverage() {
        Category first = category(1, "TH_A", 10);
        Category second = category(2, "TH_B", 10);
        List<QuizQuestion> pool = new ArrayList<>();
        pool.addAll(categoryPool(first, 30, 0, 0));
        pool.addAll(categoryPool(second, 30, 0, 0));

        TheoryExamQuestionAllocator.Allocation allocation = allocator.allocateEligibleQuestions(pool);

        assertThat(allocation.questions()).hasSize(50);
        assertThat(countByCategory(allocation.questions())).containsExactlyInAnyOrderEntriesOf(
                Map.of("TH_A", 25, "TH_B", 25));
        assertThat(countByDifficulty(allocation.questions()))
                .containsEntry(QuizQuestion.DifficultyLevel.EASY, 50);
        assertThat(allocation.difficultyRelaxed()).isTrue();
    }

    @Test
    void bankEligibilityRemainsStableWhenOneUserHasOnlyFourQuestionsAvailable() {
        Category constrained = category(1, "TH_CONSTRAINED", 10);
        Category available = category(2, "TH_AVAILABLE", 10);
        List<QuizQuestion> constrainedBank = categoryPool(constrained, 6, 5, 5);
        List<QuizQuestion> availableBank = categoryPool(available, 30, 30, 30);
        List<QuizQuestion> bank = new ArrayList<>(constrainedBank);
        bank.addAll(availableBank);
        List<QuizQuestion> userAvailable = new ArrayList<>(constrainedBank.subList(0, 4));
        userAvailable.addAll(availableBank);

        TheoryExamQuestionAllocator.Allocation allocation =
                allocator.allocateEligibleQuestions(bank, userAvailable);

        assertThat(allocation.bankEligibleCounts()).containsEntry(constrained.getId(), 16);
        assertThat(allocation.userAvailableCounts()).containsEntry(constrained.getId(), 4);
        assertThat(allocation.blueprintCategoryTargets()).containsEntry(constrained.getId(), 16);
        assertThat(allocation.categoryTargets()).containsEntry(constrained.getId(), 4);
        assertThat(countByCategory(allocation.questions())).containsExactlyInAnyOrderEntriesOf(
                Map.of("TH_CONSTRAINED", 4, "TH_AVAILABLE", 46));
        assertThat(allocation.questions()).hasSize(50);
    }

    @Test
    void categoryWithoutConfiguredWeightRemainsInventoryOnly() {
        Category unconfigured = category(1, "TH_UNCONFIGURED", null);
        Category configured = category(2, "TH_CONFIGURED", 10);
        List<QuizQuestion> bank = new ArrayList<>();
        bank.addAll(categoryPool(unconfigured, 20, 20, 20));
        bank.addAll(categoryPool(configured, 20, 20, 20));

        TheoryExamQuestionAllocator.Allocation allocation =
                allocator.allocateEligibleQuestions(bank, bank);

        assertThat(allocation.bankEligibleCounts()).containsEntry(unconfigured.getId(), 60);
        assertThat(allocation.unconfiguredCategoryCodes()).containsExactly("TH_UNCONFIGURED");
        assertThat(allocation.blueprintCategoryTargets()).doesNotContainKey(unconfigured.getId());
        assertThat(allocation.categoryTargets()).doesNotContainKey(unconfigured.getId());
        assertThat(allocation.questions())
                .hasSize(50)
                .allSatisfy(question ->
                        assertThat(question.getCategory().getCode()).isEqualTo("TH_CONFIGURED"));
    }

    @Test
    void insufficientEligibleCapacityReturnsAControlledUnavailableState() {
        List<QuizQuestion> pool = new ArrayList<>();
        pool.addAll(categoryPool(category(1, "TH_A", 10), 8, 8, 8));
        pool.addAll(categoryPool(category(2, "TH_B", 10), 8, 8, 8));
        when(messages.get("exam.pool.unavailable", 50, 48)).thenReturn("Theory exam temporarily unavailable");

        assertThatThrownBy(() -> allocator.allocateEligibleQuestions(pool))
                .isInstanceOf(ExamQuestionPoolUnavailableException.class)
                .satisfies(error -> {
                    ExamQuestionPoolUnavailableException unavailable =
                            (ExamQuestionPoolUnavailableException) error;
                    assertThat(unavailable.getRequiredQuestions()).isEqualTo(50);
                    assertThat(unavailable.getEligibleCapacity()).isEqualTo(48);
                });
    }

    private Category category(long id, String code, Integer weight) {
        Category category = new Category();
        category.setId(id);
        category.setCode(code);
        category.setDisplayOrder((int) id);
        category.setExamTargetWeight(weight);
        category.setIsActive(true);
        category.setContentScope(CategoryContentScope.THEORETICAL_EXAM);
        return category;
    }

    private List<QuizQuestion> categoryPool(Category category, int easy, int medium, int hard) {
        List<QuizQuestion> questions = new ArrayList<>();
        addQuestions(questions, category, QuizQuestion.DifficultyLevel.EASY, easy);
        addQuestions(questions, category, QuizQuestion.DifficultyLevel.MEDIUM, medium);
        addQuestions(questions, category, QuizQuestion.DifficultyLevel.HARD, hard);
        return questions;
    }

    private void addQuestions(
            List<QuizQuestion> target,
            Category category,
            QuizQuestion.DifficultyLevel difficulty,
            int count) {
        for (int index = 0; index < count; index++) {
            QuizQuestion question = new QuizQuestion();
            question.setId(questionIds.getAndIncrement());
            question.setCategory(category);
            question.setDifficultyLevel(difficulty);
            question.setIsActive(true);
            question.setStatus(QuizQuestion.QuestionStatus.PUBLISHED);
            question.setQuestionAr("Theory question " + question.getId());
            question.setQuestionEn("Theory question " + question.getId());
            question.setQuestionNl("Theory question " + question.getId());
            question.setQuestionFr("Theory question " + question.getId());
            question.addOption(option("First choice", true, 1));
            question.addOption(option("Second choice", false, 2));
            target.add(question);
        }
    }

    private QuizAnswerOption option(String text, boolean correct, int order) {
        QuizAnswerOption option = new QuizAnswerOption();
        option.setOptionTextAr(text);
        option.setOptionTextEn(text);
        option.setOptionTextNl(text);
        option.setOptionTextFr(text);
        option.setIsCorrect(correct);
        option.setIsActive(true);
        option.setDisplayOrder(order);
        return option;
    }

    private Map<String, Integer> countByCategory(List<QuizQuestion> questions) {
        return questions.stream().collect(Collectors.toMap(
                question -> question.getCategory().getCode(),
                question -> 1,
                Integer::sum,
                LinkedHashMap::new));
    }

    private Map<QuizQuestion.DifficultyLevel, Integer> countByDifficulty(List<QuizQuestion> questions) {
        return questions.stream().collect(Collectors.toMap(
                QuizQuestion::getDifficultyLevel,
                question -> 1,
                Integer::sum,
                () -> new LinkedHashMap<>()));
    }
}
