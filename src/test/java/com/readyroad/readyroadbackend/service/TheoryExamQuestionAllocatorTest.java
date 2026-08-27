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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
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
    void categoryWithFourQuestionsIsExcludedWhileFiveOrMoreParticipates() {
        Category excluded = category(1, "TH_SMALL", 10);
        Category eligible = category(2, "TH_READY", 10);
        Category support = category(3, "TH_SUPPORT", 10);
        List<QuizQuestion> pool = new ArrayList<>();
        pool.addAll(categoryPool(excluded, 2, 1, 1));
        pool.addAll(categoryPool(eligible, 2, 2, 1));
        pool.addAll(categoryPool(support, 20, 20, 20));

        TheoryExamQuestionAllocator.Allocation allocation = allocator.allocateEligibleQuestions(pool);

        assertThat(allocation.questions()).hasSize(50);
        assertThat(allocation.bankEligibleCounts()).doesNotContainKey(excluded.getId());
        assertThat(allocation.bankEligibleCounts()).containsEntry(eligible.getId(), 5);
        assertThat(allocation.categoryTargets()).doesNotContainKey(excluded.getId());
        assertThat(allocation.categoryTargets()).containsKey(eligible.getId());
        assertThat(countByCategory(allocation.questions()).getOrDefault("TH_READY", 0))
                .isGreaterThanOrEqualTo(1);
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

    @Test
    void seededSimulationKeepsFiftyUniqueQuestionsAndCirculatesTheCurrentDynamicBank() {
        TheoryExamQuestionAllocator simulationAllocator = new TheoryExamQuestionAllocator(
                questionRepository, messages, new Random(20260824L));
        List<QuizQuestion> bank = new ArrayList<>();
        bank.addAll(categoryPool(category(1, "TH01", 14), 20, 15, 10));
        bank.addAll(categoryPool(category(2, "TH02", 16), 20, 19, 3));
        bank.addAll(categoryPool(category(3, "TH03", 14), 9, 7, 6));
        bank.addAll(categoryPool(category(4, "TH04", 10), 2, 0, 0));
        bank.addAll(categoryPool(category(5, "TH05", 16), 19, 11, 4));
        bank.addAll(categoryPool(category(6, "TH06", 10), 5, 12, 5));
        bank.addAll(categoryPool(category(7, "TH07", 10), 5, 2, 0));
        bank.addAll(categoryPool(category(8, "TH08", 10), 9, 17, 4));

        Random seededRandom = new Random(20260824L);
        Map<Long, Integer> presentations = new HashMap<>();
        Map<String, Integer> categoryPresentations = new LinkedHashMap<>();
        Map<QuizQuestion.DifficultyLevel, Integer> difficultyPresentations = new LinkedHashMap<>();
        Set<Long> previous = Set.of();
        int totalAdjacentOverlap = 0;
        int duplicateViolations = 0;
        int categoryQuotaRelaxations = 0;
        int difficultyRelaxations = 0;
        int simulations = 500;

        for (int iteration = 0; iteration < simulations; iteration++) {
            List<QuizQuestion> ranked = new ArrayList<>(bank);
            Collections.shuffle(ranked, seededRandom);
            TheoryExamQuestionAllocator.Allocation allocation =
                    simulationAllocator.allocateEligibleQuestions(ranked, ranked, "en");

            assertThat(allocation.questions()).hasSize(50);
            Set<Long> selected = allocation.questions().stream()
                    .map(QuizQuestion::getId)
                    .collect(Collectors.toSet());
            if (selected.size() != allocation.questions().size()) {
                duplicateViolations++;
            }
            assertThat(selected).hasSize(50);
            assertThat(allocation.bankEligibleCounts()).doesNotContainKey(4L);
            assertThat(allocation.difficultyCounts().values().stream()
                    .mapToInt(Integer::intValue).sum()).isEqualTo(50);
            allocation.questions().forEach(question -> {
                assertThat(question.getCategory().getContentScope().supportsTheoreticalExam()).isTrue();
                assertThat(question.getDifficultyLevel()).isNotNull();
                assertThat(question.getQuestionEn()).isNotBlank();
                assertThat(question.getOptions().stream().filter(QuizAnswerOption::getIsActive)).hasSizeBetween(2, 3);
                assertThat(question.getOptions().stream()
                        .filter(QuizAnswerOption::getIsActive)
                        .filter(QuizAnswerOption::getIsCorrect)).hasSize(1);
            });
            countByCategory(allocation.questions()).forEach(
                    (category, count) -> categoryPresentations.merge(category, count, Integer::sum));
            countByDifficulty(allocation.questions()).forEach(
                    (difficulty, count) -> difficultyPresentations.merge(difficulty, count, Integer::sum));
            if (!allocation.categoryTargets().equals(allocation.blueprintCategoryTargets())) {
                categoryQuotaRelaxations++;
            }
            if (allocation.difficultyRelaxed()) {
                difficultyRelaxations++;
            }
            selected.forEach(id -> presentations.merge(id, 1, Integer::sum));
            if (!previous.isEmpty()) {
                Set<Long> overlap = new HashSet<>(previous);
                overlap.retainAll(selected);
                totalAdjacentOverlap += overlap.size();
            }
            previous = selected;
        }

        Set<Long> blueprintInventory = bank.stream()
                .filter(question -> question.getCategory().getId() != 4L)
                .map(QuizQuestion::getId)
                .collect(Collectors.toSet());
        assertThat(presentations.keySet()).containsExactlyInAnyOrderElementsOf(blueprintInventory);
        assertThat(presentations.values()).allMatch(count -> count > 0);
        assertThat(duplicateViolations).isZero();
        assertThat(categoryPresentations.values()).allMatch(count -> count > 0);
        assertThat(difficultyPresentations.values().stream().mapToInt(Integer::intValue).sum())
                .isEqualTo(simulations * 50);
        double averageAdjacentOverlap = totalAdjacentOverlap / (double) (simulations - 1);
        assertThat(averageAdjacentOverlap).isLessThan(35.0);

        List<QuizQuestion> firstRanking = new ArrayList<>(bank);
        Collections.shuffle(firstRanking, new Random(7L));
        TheoryExamQuestionAllocator.Allocation first =
                simulationAllocator.allocateEligibleQuestions(firstRanking, firstRanking, "en");
        Set<Long> coolingDown = first.questions().stream()
                .map(QuizQuestion::getId)
                .collect(Collectors.toSet());
        List<QuizQuestion> secondAvailable = bank.stream()
                .filter(question -> !coolingDown.contains(question.getId()))
                .toList();
        TheoryExamQuestionAllocator.Allocation second =
                simulationAllocator.allocateEligibleQuestions(bank, secondAvailable, "en");
        Set<Long> cooldownViolations = second.questions().stream()
                .map(QuizQuestion::getId)
                .filter(coolingDown::contains)
                .collect(Collectors.toSet());
        assertThat(cooldownViolations).isEmpty();

        int leastQuestionExposure = presentations.values().stream().mapToInt(Integer::intValue).min().orElse(0);
        int mostQuestionExposure = presentations.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        double inventoryUtilization = presentations.size() * 100.0 / blueprintInventory.size();
        Map<String, Double> averageCategoryDistribution = categoryPresentations.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() / (double) simulations,
                        (firstValue, ignored) -> firstValue,
                        LinkedHashMap::new));
        Map<QuizQuestion.DifficultyLevel, Double> averageDifficultyDistribution =
                difficultyPresentations.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue() / (double) simulations,
                                (firstValue, ignored) -> firstValue,
                                LinkedHashMap::new));

        System.out.printf(
                "Theory allocator simulation: runs=%d, bank=%d, examSize=50, inventoryUtilization=%.2f%%, "
                        + "questionExposure[min=%d,max=%d], avgAdjacentOverlap=%.2f, duplicateViolations=%d, "
                        + "cooldownViolations=%d, categoryQuotaRelaxations=%d, difficultyRelaxations=%d, "
                        + "avgCategoryDistribution=%s, avgDifficultyDistribution=%s%n",
                simulations,
                blueprintInventory.size(),
                inventoryUtilization,
                leastQuestionExposure,
                mostQuestionExposure,
                averageAdjacentOverlap,
                duplicateViolations,
                cooldownViolations.size(),
                categoryQuotaRelaxations,
                difficultyRelaxations,
                averageCategoryDistribution,
                averageDifficultyDistribution);
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
