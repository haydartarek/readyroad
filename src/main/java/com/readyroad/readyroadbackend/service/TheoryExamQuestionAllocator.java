package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.enums.CategoryContentScope;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.exception.ExamQuestionPoolUnavailableException;
import com.readyroad.readyroadbackend.util.PlaceholderDetector;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TheoryExamQuestionAllocator {

    private static final List<QuizQuestion.DifficultyLevel> DIFFICULTIES = List.of(
            QuizQuestion.DifficultyLevel.EASY,
            QuizQuestion.DifficultyLevel.MEDIUM,
            QuizQuestion.DifficultyLevel.HARD);
    private static final Map<QuizQuestion.DifficultyLevel, Integer> DIFFICULTY_TARGETS = Map.of(
            QuizQuestion.DifficultyLevel.EASY, 15,
            QuizQuestion.DifficultyLevel.MEDIUM, 20,
            QuizQuestion.DifficultyLevel.HARD, 15);

    private final QuizQuestionRepository questionRepository;
    private final BackendMessageService messages;
    private final Supplier<RandomGenerator> randomProvider;

    @Autowired
    public TheoryExamQuestionAllocator(
            QuizQuestionRepository questionRepository,
            BackendMessageService messages) {
        this(questionRepository, messages, ThreadLocalRandom::current);
    }

    TheoryExamQuestionAllocator(
            QuizQuestionRepository questionRepository,
            BackendMessageService messages,
            RandomGenerator randomGenerator) {
        this(questionRepository, messages, () -> randomGenerator);
    }

    private TheoryExamQuestionAllocator(
            QuizQuestionRepository questionRepository,
            BackendMessageService messages,
            Supplier<RandomGenerator> randomProvider) {
        this.questionRepository = questionRepository;
        this.messages = messages;
        this.randomProvider = randomProvider;
    }

    @Transactional(readOnly = true)
    public Allocation allocate(Long userId, String languageCode, LocalDateTime cooldownCutoff) {
        return allocateEligibleQuestions(
                questionRepository.findTheoryQuestionBankCandidates(languageCode),
                questionRepository.findCooldownEligibleTheoryQuestions(userId, languageCode, cooldownCutoff),
                languageCode);
    }

    Allocation allocateEligibleQuestions(List<QuizQuestion> candidates) {
        return allocateEligibleQuestions(candidates, candidates, "en");
    }

    Allocation allocateEligibleQuestions(
            List<QuizQuestion> bankCandidates,
            List<QuizQuestion> userAvailableCandidates) {
        return allocateEligibleQuestions(bankCandidates, userAvailableCandidates, "en");
    }

    Allocation allocateEligibleQuestions(
            List<QuizQuestion> bankCandidates,
            List<QuizQuestion> userAvailableCandidates,
            String languageCode) {
        Map<Long, QuizQuestion> bankQuestions = uniqueDeliveryEligible(bankCandidates, languageCode);
        Map<Long, CategoryPool> bankPools = groupByCategory(bankQuestions.values());
        List<CategoryPool> bankEligibleCategories = bankPools.values().stream()
                .filter(pool -> pool.capacity() >= TheoryExamBlueprintPolicy.MIN_ELIGIBLE_QUESTIONS_PER_CATEGORY)
                .sorted(CategoryPool.ORDER)
                .toList();

        Map<Long, Integer> bankEligibleCounts = categoryCounts(bankEligibleCategories);
        List<String> defaultedWeightCategoryCodes = bankEligibleCategories.stream()
                .filter(pool -> !pool.hasConfiguredWeight())
                .map(pool -> pool.category().getCode())
                .toList();
        List<CategoryPool> participatingBankCategories = bankEligibleCategories;

        if (participatingBankCategories.size() > TheoryExamBlueprintPolicy.EXAM_SIZE) {
            throw new IllegalStateException(
                    "Theory exam blueprint has "
                            + participatingBankCategories.size()
                            + " eligible categories but only "
                            + TheoryExamBlueprintPolicy.EXAM_SIZE
                            + " exam slots");
        }

        Set<Long> bankQuestionIds = bankQuestions.keySet();
        Set<Long> participatingCategoryIds = participatingBankCategories.stream()
                .map(pool -> pool.category().getId())
                .collect(java.util.stream.Collectors.toSet());

        Map<Long, QuizQuestion> userQuestions = eligibleBankSubset(
                userAvailableCandidates, bankQuestionIds, languageCode);
        Map<Long, CategoryPool> userPoolsByCategory = groupByCategory(userQuestions.values());

        Map<Long, Integer> userAvailableCounts = new LinkedHashMap<>();
        for (CategoryPool bankCategory : bankEligibleCategories) {
            CategoryPool userPool = userPoolsByCategory.get(bankCategory.category().getId());
            userAvailableCounts.put(
                    bankCategory.category().getId(),
                    userPool == null ? 0 : userPool.capacity());
        }

        Map<Long, QuizQuestion> effectiveUserQuestions = new LinkedHashMap<>();
        userQuestions.values().stream()
                .filter(question -> participatingCategoryIds.contains(question.getCategory().getId()))
                .forEach(question -> effectiveUserQuestions.putIfAbsent(question.getId(), question));

        Map<Long, CategoryPool> effectivePoolsByCategory =
                groupByCategory(effectiveUserQuestions.values());
        List<CategoryPool> effectiveUserCategories = participatingBankCategories.stream()
                .map(pool -> effectivePoolsByCategory.getOrDefault(
                        pool.category().getId(),
                        new CategoryPool(pool.category())))
                .toList();

        int effectiveEligibleCapacity = effectiveUserCategories.stream()
                .mapToInt(CategoryPool::capacity)
                .sum();
        int participatingBankCapacity = participatingBankCategories.stream()
                .mapToInt(CategoryPool::capacity)
                .sum();
        if (participatingBankCapacity < TheoryExamBlueprintPolicy.EXAM_SIZE
                || effectiveEligibleCapacity < TheoryExamBlueprintPolicy.EXAM_SIZE) {
            throw unavailable(effectiveEligibleCapacity);
        }

        Map<Long, Integer> blueprintCategoryTargets =
                allocateCategoryTargets(participatingBankCategories);
        Map<Long, Integer> categoryTargets =
                applyUserAvailability(effectiveUserCategories, blueprintCategoryTargets);
        DifficultyAllocation difficultyAllocation =
                allocateDifficulties(
                        effectiveUserCategories,
                        categoryTargets,
                        randomProvider.get());
        List<QuizQuestion> selected =
                selectQuestions(effectiveUserCategories, difficultyAllocation.counts());

        Set<Long> uniqueQuestionIds = new HashSet<>();
        boolean allUnique = selected.stream().map(QuizQuestion::getId).allMatch(uniqueQuestionIds::add);
        if (selected.size() != TheoryExamBlueprintPolicy.EXAM_SIZE || !allUnique) {
            throw new IllegalStateException("Theory exam preflight produced an invalid allocation");
        }

        return new Allocation(
                selected,
                bankEligibleCounts,
                userAvailableCounts,
                defaultedWeightCategoryCodes,
                blueprintCategoryTargets,
                categoryTargets,
                difficultyAllocation.globalCounts(),
                difficultyAllocation.relaxed(),
                effectiveEligibleCapacity);
    }

    private Map<Long, QuizQuestion> uniqueDeliveryEligible(
            List<QuizQuestion> candidates,
            String languageCode) {
        Map<Long, QuizQuestion> uniqueCandidates = new LinkedHashMap<>();
        for (QuizQuestion question : candidates) {
            if (isDeliveryEligible(question, languageCode)) {
                uniqueCandidates.putIfAbsent(question.getId(), question);
            }
        }
        return uniqueCandidates;
    }

    private Map<Long, QuizQuestion> eligibleBankSubset(
            List<QuizQuestion> candidates,
            Set<Long> bankQuestionIds,
            String languageCode) {
        Map<Long, QuizQuestion> result = new LinkedHashMap<>();
        for (QuizQuestion question : candidates) {
            if (isDeliveryEligible(question, languageCode)
                    && bankQuestionIds.contains(question.getId())) {
                result.putIfAbsent(question.getId(), question);
            }
        }
        return result;
    }
    private Map<Long, CategoryPool> groupByCategory(Iterable<QuizQuestion> questions) {
        Map<Long, CategoryPool> grouped = new LinkedHashMap<>();
        for (QuizQuestion question : questions) {
            Category category = question.getCategory();
            grouped.computeIfAbsent(category.getId(), ignored -> new CategoryPool(category))
                    .add(question);
        }
        return grouped;
    }

    private Map<Long, Integer> categoryCounts(List<CategoryPool> categories) {
        Map<Long, Integer> counts = new LinkedHashMap<>();
        categories.forEach(pool -> counts.put(pool.category().getId(), pool.capacity()));
        return Map.copyOf(counts);
    }

    private Map<Long, Integer> allocateCategoryTargets(List<CategoryPool> categories) {
        Map<Long, Integer> targets = new LinkedHashMap<>();
        categories.forEach(pool -> targets.put(pool.category().getId(), 0));

        int assigned = 0;
        if (categories.size() <= TheoryExamBlueprintPolicy.EXAM_SIZE) {
            for (CategoryPool pool : categories) {
                targets.put(pool.category().getId(), 1);
                assigned++;
            }
        }

        while (assigned < TheoryExamBlueprintPolicy.EXAM_SIZE) {
            CategoryPool selected = null;
            for (CategoryPool candidate : categories) {
                int current = targets.get(candidate.category().getId());
                if (current >= candidate.capacity()) {
                    continue;
                }
                if (selected == null || hasHigherWeightedNeed(candidate, selected, targets)) {
                    selected = candidate;
                }
            }
            if (selected == null) {
                throw unavailable(categories.stream().mapToInt(CategoryPool::capacity).sum());
            }
            Long categoryId = selected.category().getId();
            targets.put(categoryId, targets.get(categoryId) + 1);
            assigned++;
        }
        return Map.copyOf(targets);
    }

    private boolean hasHigherWeightedNeed(
            CategoryPool candidate,
            CategoryPool currentBest,
            Map<Long, Integer> targets) {
        long candidateScore = (long) candidate.weight()
                * (targets.get(currentBest.category().getId()) + 1L);
        long currentBestScore = (long) currentBest.weight()
                * (targets.get(candidate.category().getId()) + 1L);
        if (candidateScore != currentBestScore) {
            return candidateScore > currentBestScore;
        }
        return CategoryPool.ORDER.compare(candidate, currentBest) < 0;
    }

    private Map<Long, Integer> applyUserAvailability(
            List<CategoryPool> userCategories,
            Map<Long, Integer> blueprintTargets) {
        Map<Long, Integer> targets = new LinkedHashMap<>();
        int assigned = 0;
        for (CategoryPool pool : userCategories) {
            Long categoryId = pool.category().getId();
            int availableTarget = Math.min(blueprintTargets.get(categoryId), pool.capacity());
            targets.put(categoryId, availableTarget);
            assigned += availableTarget;
        }

        while (assigned < TheoryExamBlueprintPolicy.EXAM_SIZE) {
            CategoryPool selected = null;
            for (CategoryPool candidate : userCategories) {
                int current = targets.get(candidate.category().getId());
                if (current >= candidate.capacity()) {
                    continue;
                }
                if (selected == null || hasHigherWeightedNeed(candidate, selected, targets)) {
                    selected = candidate;
                }
            }
            if (selected == null) {
                throw unavailable(userCategories.stream().mapToInt(CategoryPool::capacity).sum());
            }
            Long categoryId = selected.category().getId();
            targets.put(categoryId, targets.get(categoryId) + 1);
            assigned++;
        }
        return Map.copyOf(targets);
    }

    private DifficultyAllocation allocateDifficulties(
            List<CategoryPool> categories,
            Map<Long, Integer> categoryTargets,
            RandomGenerator random) {
        List<CategoryPool> allocationOrder = new ArrayList<>(categories);
        shuffle(allocationOrder, random);
        int categoryCount = allocationOrder.size();
        int difficultyCount = DIFFICULTIES.size();
        int source = 0;
        int firstCategory = 1;
        int firstDifficulty = firstCategory + categoryCount;
        int sink = firstDifficulty + difficultyCount;
        int nodeCount = sink + 1;
        int[][] capacity = new int[nodeCount][nodeCount];
        int[][] original = new int[nodeCount][nodeCount];

        for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++) {
            CategoryPool pool = allocationOrder.get(categoryIndex);
            int categoryNode = firstCategory + categoryIndex;
            addCapacity(capacity, original, source, categoryNode,
                    categoryTargets.get(pool.category().getId()));
            for (int difficultyIndex = 0; difficultyIndex < difficultyCount; difficultyIndex++) {
                QuizQuestion.DifficultyLevel difficulty = DIFFICULTIES.get(difficultyIndex);
                addCapacity(capacity, original, categoryNode, firstDifficulty + difficultyIndex,
                        pool.questions(difficulty).size());
            }
        }
        for (int difficultyIndex = 0; difficultyIndex < difficultyCount; difficultyIndex++) {
            QuizQuestion.DifficultyLevel difficulty = DIFFICULTIES.get(difficultyIndex);
            addCapacity(capacity, original, firstDifficulty + difficultyIndex, sink,
                    DIFFICULTY_TARGETS.get(difficulty));
        }

        int exactFlow = maxFlow(capacity, source, sink);
        Map<Long, EnumMap<QuizQuestion.DifficultyLevel, Integer>> counts = new LinkedHashMap<>();
        EnumMap<QuizQuestion.DifficultyLevel, Integer> globalCounts = zeroDifficultyCounts();
        for (int categoryIndex = 0; categoryIndex < categoryCount; categoryIndex++) {
            CategoryPool pool = allocationOrder.get(categoryIndex);
            int categoryNode = firstCategory + categoryIndex;
            EnumMap<QuizQuestion.DifficultyLevel, Integer> categoryCounts = zeroDifficultyCounts();
            for (int difficultyIndex = 0; difficultyIndex < difficultyCount; difficultyIndex++) {
                QuizQuestion.DifficultyLevel difficulty = DIFFICULTIES.get(difficultyIndex);
                int difficultyNode = firstDifficulty + difficultyIndex;
                int used = original[categoryNode][difficultyNode] - capacity[categoryNode][difficultyNode];
                categoryCounts.put(difficulty, used);
                globalCounts.put(difficulty, globalCounts.get(difficulty) + used);
            }
            counts.put(pool.category().getId(), categoryCounts);
        }

        if (exactFlow < TheoryExamBlueprintPolicy.EXAM_SIZE) {
            fillDifficultyShortage(allocationOrder, categoryTargets, counts, globalCounts);
        }
        return new DifficultyAllocation(
                counts,
                Map.copyOf(globalCounts),
                !globalCounts.equals(DIFFICULTY_TARGETS));
    }

    private void fillDifficultyShortage(
            List<CategoryPool> categories,
            Map<Long, Integer> categoryTargets,
            Map<Long, EnumMap<QuizQuestion.DifficultyLevel, Integer>> counts,
            EnumMap<QuizQuestion.DifficultyLevel, Integer> globalCounts) {
        for (CategoryPool pool : categories) {
            Long categoryId = pool.category().getId();
            EnumMap<QuizQuestion.DifficultyLevel, Integer> categoryCounts = counts.get(categoryId);
            int remaining = categoryTargets.get(categoryId)
                    - categoryCounts.values().stream().mapToInt(Integer::intValue).sum();
            while (remaining > 0) {
                QuizQuestion.DifficultyLevel selectedDifficulty = DIFFICULTIES.stream()
                        .filter(difficulty -> categoryCounts.get(difficulty) < pool.questions(difficulty).size())
                        .max(Comparator
                                .comparingInt((QuizQuestion.DifficultyLevel difficulty) ->
                                        DIFFICULTY_TARGETS.get(difficulty) - globalCounts.get(difficulty))
                                .thenComparingInt(difficulty ->
                                        pool.questions(difficulty).size() - categoryCounts.get(difficulty))
                                .thenComparingInt(difficulty -> -difficulty.ordinal()))
                        .orElseThrow(() -> new IllegalStateException(
                                "Category allocation exceeds its eligible inventory"));
                categoryCounts.put(selectedDifficulty, categoryCounts.get(selectedDifficulty) + 1);
                globalCounts.put(selectedDifficulty, globalCounts.get(selectedDifficulty) + 1);
                remaining--;
            }
        }
    }

    private List<QuizQuestion> selectQuestions(
            List<CategoryPool> categories,
            Map<Long, EnumMap<QuizQuestion.DifficultyLevel, Integer>> counts) {
        List<QuizQuestion> selected = new ArrayList<>(TheoryExamBlueprintPolicy.EXAM_SIZE);
        for (CategoryPool pool : categories) {
            EnumMap<QuizQuestion.DifficultyLevel, Integer> categoryCounts = counts.get(pool.category().getId());
            for (QuizQuestion.DifficultyLevel difficulty : DIFFICULTIES) {
                int amount = categoryCounts.get(difficulty);
                selected.addAll(pool.questions(difficulty).subList(0, amount));
            }
        }
        return selected;
    }

    private int maxFlow(int[][] capacity, int source, int sink) {
        int flow = 0;
        int[] parent = new int[capacity.length];
        while (findAugmentingPath(capacity, source, sink, parent)) {
            int pathFlow = Integer.MAX_VALUE;
            for (int node = sink; node != source; node = parent[node]) {
                pathFlow = Math.min(pathFlow, capacity[parent[node]][node]);
            }
            for (int node = sink; node != source; node = parent[node]) {
                capacity[parent[node]][node] -= pathFlow;
                capacity[node][parent[node]] += pathFlow;
            }
            flow += pathFlow;
        }
        return flow;
    }

    private boolean findAugmentingPath(int[][] capacity, int source, int sink, int[] parent) {
        Arrays.fill(parent, -1);
        parent[source] = source;
        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(source);
        while (!queue.isEmpty()) {
            int node = queue.remove();
            for (int next = 0; next < capacity.length; next++) {
                if (parent[next] == -1 && capacity[node][next] > 0) {
                    parent[next] = node;
                    if (next == sink) {
                        return true;
                    }
                    queue.add(next);
                }
            }
        }
        return false;
    }

    private void addCapacity(int[][] capacity, int[][] original, int from, int to, int value) {
        capacity[from][to] = value;
        original[from][to] = value;
    }

    private EnumMap<QuizQuestion.DifficultyLevel, Integer> zeroDifficultyCounts() {
        EnumMap<QuizQuestion.DifficultyLevel, Integer> counts =
                new EnumMap<>(QuizQuestion.DifficultyLevel.class);
        DIFFICULTIES.forEach(difficulty -> counts.put(difficulty, 0));
        return counts;
    }

    private boolean isDeliveryEligible(QuizQuestion question, String languageCode) {
        if (question == null || question.getId() == null || question.getCategory() == null
                || question.getDifficultyLevel() == null
                || !Boolean.TRUE.equals(question.getIsActive())
                || question.getStatus() != QuizQuestion.QuestionStatus.PUBLISHED) {
            return false;
        }
        Category category = question.getCategory();
        CategoryContentScope scope = category.getContentScope();
        if (!Boolean.TRUE.equals(category.getIsActive()) || scope == null || !scope.supportsTheoreticalExam()) {
            return false;
        }
        if (PlaceholderDetector.isPlaceholder(localizedQuestion(question, languageCode))) {
            return false;
        }
        List<QuizAnswerOption> options = question.getDeliverableOptions();
        if (options.size() < 2 || options.size() > 3
                || options.stream().anyMatch(option ->
                        PlaceholderDetector.isPlaceholder(localizedOption(option, languageCode)))) {
            return false;
        }
        return options.stream().filter(option -> Boolean.TRUE.equals(option.getIsCorrect())).count() == 1;
    }

    private static <T> void shuffle(List<T> values, RandomGenerator random) {
        for (int index = values.size() - 1; index > 0; index--) {
            Collections.swap(values, index, random.nextInt(index + 1));
        }
    }

    private static String localizedQuestion(QuizQuestion question, String languageCode) {
        return switch (normalizedLanguage(languageCode)) {
            case "ar" -> question.getQuestionAr();
            case "nl" -> question.getQuestionNl();
            case "fr" -> question.getQuestionFr();
            default -> question.getQuestionEn();
        };
    }

    private static String localizedOption(QuizAnswerOption option, String languageCode) {
        return switch (normalizedLanguage(languageCode)) {
            case "ar" -> option.getOptionTextAr();
            case "nl" -> option.getOptionTextNl();
            case "fr" -> option.getOptionTextFr();
            default -> option.getOptionTextEn();
        };
    }

    private static String normalizedLanguage(String languageCode) {
        return languageCode == null ? "en" : languageCode.trim().toLowerCase(java.util.Locale.ROOT);
    }

    private ExamQuestionPoolUnavailableException unavailable(int eligibleCapacity) {
        return new ExamQuestionPoolUnavailableException(
                messages.get("exam.pool.unavailable", TheoryExamBlueprintPolicy.EXAM_SIZE, eligibleCapacity),
                TheoryExamBlueprintPolicy.EXAM_SIZE,
                eligibleCapacity);
    }

    public record Allocation(
            List<QuizQuestion> questions,
            Map<Long, Integer> bankEligibleCounts,
            Map<Long, Integer> userAvailableCounts,
            List<String> defaultedWeightCategoryCodes,
            Map<Long, Integer> blueprintCategoryTargets,
            Map<Long, Integer> categoryTargets,
            Map<QuizQuestion.DifficultyLevel, Integer> difficultyCounts,
            boolean difficultyRelaxed,
            int eligibleCapacity) {

        public Allocation {
            questions = List.copyOf(questions);
            bankEligibleCounts = Map.copyOf(bankEligibleCounts);
            userAvailableCounts = Map.copyOf(userAvailableCounts);
            defaultedWeightCategoryCodes = List.copyOf(defaultedWeightCategoryCodes);
            blueprintCategoryTargets = Map.copyOf(blueprintCategoryTargets);
            categoryTargets = Map.copyOf(categoryTargets);
            difficultyCounts = Map.copyOf(difficultyCounts);
        }
    }

    private record DifficultyAllocation(
            Map<Long, EnumMap<QuizQuestion.DifficultyLevel, Integer>> counts,
            Map<QuizQuestion.DifficultyLevel, Integer> globalCounts,
            boolean relaxed) {
    }

    private static final class CategoryPool {
        private static final Comparator<CategoryPool> ORDER = Comparator
                .comparing((CategoryPool pool) -> pool.category().getDisplayOrder(),
                        Comparator.nullsLast(Integer::compareTo))
                .thenComparing(pool -> pool.category().getId());

        private final Category category;
        private final EnumMap<QuizQuestion.DifficultyLevel, List<QuizQuestion>> questions =
                new EnumMap<>(QuizQuestion.DifficultyLevel.class);

        private CategoryPool(Category category) {
            this.category = category;
            DIFFICULTIES.forEach(difficulty -> questions.put(difficulty, new ArrayList<>()));
        }

        private void add(QuizQuestion question) {
            questions.get(question.getDifficultyLevel()).add(question);
        }

        private Category category() {
            return category;
        }

        private List<QuizQuestion> questions(QuizQuestion.DifficultyLevel difficulty) {
            return questions.get(difficulty);
        }

        private int capacity() {
            return questions.values().stream().mapToInt(List::size).sum();
        }

        private int weight() {
            return TheoryExamBlueprintPolicy.effectiveCategoryWeight(
                    category.getExamTargetWeight());
        }

        private boolean hasConfiguredWeight() {
            return category.getExamTargetWeight() != null && category.getExamTargetWeight() > 0;
        }
    }
}
