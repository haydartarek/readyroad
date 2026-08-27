package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.dto.admin.AdminTheoryBankHealthDtos.BankHealthResponse;
import com.readyroad.readyroadbackend.dto.admin.AdminTheoryBankHealthDtos.CategoryHealth;
import com.readyroad.readyroadbackend.dto.admin.AdminTheoryBankHealthDtos.LocaleHealth;
import com.readyroad.readyroadbackend.dto.admin.AdminTheoryBankHealthDtos.LocalePerformance;
import com.readyroad.readyroadbackend.dto.admin.AdminTheoryBankHealthDtos.QuestionExposure;
import com.readyroad.readyroadbackend.dto.admin.AdminTheoryBankHealthDtos.QuestionQuality;
import com.readyroad.readyroadbackend.dto.admin.AdminTheoryBankHealthDtos.Summary;
import com.readyroad.readyroadbackend.service.AdminTheoryBankHealthStore.PerformanceRow;
import com.readyroad.readyroadbackend.util.PlaceholderDetector;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminTheoryBankHealthService {

    private static final List<String> LOCALES = List.of("ar", "nl", "en", "fr");
    private static final int MIN_QUESTION_SAMPLE = 30;
    private static final int MIN_LOCALE_SAMPLE = 20;
    private static final double SIGNIFICANCE_Z = 2.576;
    private static final double OVERREPRESENTATION_FACTOR = 1.5;

    private final QuizQuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final AdminTheoryBankHealthStore store;

    public AdminTheoryBankHealthService(
            QuizQuestionRepository questionRepository,
            CategoryRepository categoryRepository,
            AdminTheoryBankHealthStore store) {
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
        this.store = store;
    }

    @Transactional(readOnly = true)
    public BankHealthResponse bankHealth() {
        List<QuizQuestion> questions = questionRepository.findAllForTheoryBankHealth();
        Map<Long, Long> presentations = store.theoryPresentations();
        Map<Long, Map<String, PerformanceRow>> performance = store.completedPerformanceByLocale();

        Map<Long, CategoryAccumulator> categories = categoryRepository.findAll().stream()
                .filter(category -> category.getContentScope().supportsTheoreticalExam()
                        || questions.stream().anyMatch(question -> question.getCategory() != null
                                && question.getCategory().getId().equals(category.getId())))
                .sorted(categoryOrder())
                .collect(Collectors.toMap(
                        Category::getId,
                        CategoryAccumulator::new,
                        (left, right) -> left,
                        LinkedHashMap::new));

        List<QuestionFacts> facts = new ArrayList<>();
        for (QuizQuestion question : questions) {
            if (question.getCategory() == null) {
                continue;
            }
            QuestionFacts questionFacts = facts(question, presentations.getOrDefault(question.getId(), 0L));
            facts.add(questionFacts);
            categories.computeIfAbsent(question.getCategory().getId(), ignored ->
                    new CategoryAccumulator(question.getCategory())).accept(questionFacts);
        }

        long totalEligible = facts.stream().filter(QuestionFacts::eligibleAllLocales).count();
        int totalWeight = categories.values().stream()
                .filter(CategoryAccumulator::configuredForBlueprint)
                .mapToInt(accumulator -> accumulator.category.getExamTargetWeight())
                .sum();

        List<CategoryHealth> categoryHealth = categories.values().stream()
                .map(accumulator -> accumulator.toResponse(totalEligible, totalWeight))
                .toList();
        List<LocaleHealth> localeHealth = LOCALES.stream()
                .map(locale -> new LocaleHealth(
                        locale,
                        facts.stream().filter(fact -> fact.eligible(locale)).count(),
                        facts.stream().filter(fact -> !fact.localeValid().get(locale)).count()))
                .toList();

        List<QuestionQuality> quality = quality(facts, performance);
        List<QuestionExposure> exposure = facts.stream()
                .filter(QuestionFacts::eligibleAllLocales)
                .map(fact -> new QuestionExposure(
                        fact.question().getId(),
                        fact.question().getCategory().getCode(),
                        fact.question().getDifficultyLevel().name(),
                        fact.presentations()))
                .toList();

        Summary summary = new Summary(
                facts.size(),
                facts.stream().filter(fact -> Boolean.TRUE.equals(fact.question().getIsActive())).count(),
                facts.stream().filter(fact -> !Boolean.TRUE.equals(fact.question().getIsActive())).count(),
                facts.stream().filter(fact -> fact.question().getStatus() == QuizQuestion.QuestionStatus.PUBLISHED).count(),
                totalEligible,
                facts.stream().filter(QuestionFacts::translationGap).count(),
                facts.stream().filter(QuestionFacts::explanationGap).count(),
                facts.stream().filter(fact -> !fact.structureValid()).count(),
                categoryHealth.stream().filter(category -> "UNDERREPRESENTED".equals(category.representationStatus())).count(),
                categoryHealth.stream().filter(category -> "OVERREPRESENTED".equals(category.representationStatus())).count());

        return new BankHealthResponse(
                Instant.now(),
                summary,
                localeHealth,
                categoryHealth,
                quality,
                exposure.stream()
                        .sorted(Comparator.comparingLong(QuestionExposure::presentations)
                                .thenComparingLong(QuestionExposure::questionId))
                        .limit(10)
                        .toList(),
                exposure.stream()
                        .filter(question -> question.presentations() > 0)
                        .sorted(Comparator.comparingLong(QuestionExposure::presentations).reversed()
                                .thenComparingLong(QuestionExposure::questionId))
                        .limit(10)
                        .toList());
    }


    @Transactional(readOnly = true)
    public List<CategoryHealth> categoryManagement() {
        return bankHealth().categories().stream()
                .filter(category ->
                        "THEORETICAL_EXAM".equals(category.contentScope())
                                || "BOTH".equals(category.contentScope()))
                .toList();
    }
    private static QuestionFacts facts(QuizQuestion question, long presentations) {
        List<QuizAnswerOption> options = question.getDeliverableOptions();
        boolean structureValid = question.getDifficultyLevel() != null
                && options.size() >= 2
                && options.size() <= 3
                && options.stream().map(QuizAnswerOption::getId).distinct().count() == options.size()
                && options.stream().filter(option -> Boolean.TRUE.equals(option.getIsCorrect())).count() == 1;
        Map<String, Boolean> localeValid = new LinkedHashMap<>();
        for (String locale : LOCALES) {
            localeValid.put(locale,
                    !PlaceholderDetector.isPlaceholder(questionText(question, locale))
                            && options.stream().noneMatch(option ->
                                    PlaceholderDetector.isPlaceholder(optionText(option, locale))));
        }
        boolean deliveryBase = structureValid
                && Boolean.TRUE.equals(question.getIsActive())
                && question.getStatus() == QuizQuestion.QuestionStatus.PUBLISHED
                && Boolean.TRUE.equals(question.getCategory().getIsActive())
                && question.getCategory().getContentScope().supportsTheoreticalExam();
        return new QuestionFacts(
                question,
                structureValid,
                deliveryBase,
                Map.copyOf(localeValid),
                localeValid.values().stream().anyMatch(valid -> !valid),
                LOCALES.stream().anyMatch(locale -> PlaceholderDetector.isPlaceholder(explanation(question, locale))),
                presentations);
    }

    private static List<QuestionQuality> quality(
            List<QuestionFacts> facts,
            Map<Long, Map<String, PerformanceRow>> performance) {
        Map<GroupKey, Totals> groupTotals = new HashMap<>();
        Map<Long, Totals> questionTotals = new HashMap<>();
        for (QuestionFacts fact : facts) {
            if (!fact.structureValid() || fact.question().getDifficultyLevel() == null) {
                continue;
            }
            Totals totals = totals(performance.getOrDefault(fact.question().getId(), Map.of()));
            questionTotals.put(fact.question().getId(), totals);
            groupTotals.computeIfAbsent(new GroupKey(
                            fact.question().getCategory().getId(), fact.question().getDifficultyLevel()),
                    ignored -> new Totals()).add(totals);
        }

        List<QuestionQuality> result = new ArrayList<>();
        for (QuestionFacts fact : facts) {
            if (!fact.structureValid() || fact.question().getDifficultyLevel() == null) {
                continue;
            }
            Map<String, PerformanceRow> localeRows = performance.getOrDefault(fact.question().getId(), Map.of());
            Totals totals = questionTotals.get(fact.question().getId());
            List<String> flags = new ArrayList<>();
            Totals group = groupTotals.get(new GroupKey(
                    fact.question().getCategory().getId(), fact.question().getDifficultyLevel()));
            long peerAnswered = group.answered - totals.answered;
            long peerCorrect = group.correct - totals.correct;
            if (totals.answered >= MIN_QUESTION_SAMPLE && peerAnswered >= MIN_QUESTION_SAMPLE
                    && materiallyDifferent(totals.correct, totals.answered, peerCorrect, peerAnswered)) {
                flags.add("ABNORMAL_CORRECT_RATE");
            }
            if (localeDivergence(localeRows)) {
                flags.add("LOCALE_DIVERGENCE");
            }
            if (flags.isEmpty()) {
                continue;
            }
            Map<String, LocalePerformance> byLocale = LOCALES.stream().collect(Collectors.toMap(
                    Function.identity(),
                    locale -> localePerformance(localeRows.get(locale)),
                    (left, right) -> left,
                    LinkedHashMap::new));
            result.add(new QuestionQuality(
                    fact.question().getId(),
                    fact.question().getCategory().getCode(),
                    fact.question().getDifficultyLevel().name(),
                    fact.presentations(),
                    totals.answered,
                    rate(totals.correct, totals.answered),
                    rate(totals.answered - totals.correct, totals.answered),
                    totals.averageTime(),
                    byLocale,
                    List.copyOf(flags)));
        }
        return result.stream().sorted(Comparator.comparingLong(QuestionQuality::answered).reversed()
                .thenComparingLong(QuestionQuality::questionId)).toList();
    }

    private static boolean materiallyDifferent(long correctA, long answeredA, long correctB, long answeredB) {
        double pA = correctA / (double) answeredA;
        double pB = correctB / (double) answeredB;
        double pooled = (correctA + correctB) / (double) (answeredA + answeredB);
        double standardError = Math.sqrt(pooled * (1 - pooled) * (1.0 / answeredA + 1.0 / answeredB));
        return standardError > 0 && Math.abs(pA - pB) / standardError >= SIGNIFICANCE_Z;
    }

    private static boolean localeDivergence(Map<String, PerformanceRow> rows) {
        List<Interval> intervals = rows.entrySet().stream()
                .filter(entry -> LOCALES.contains(entry.getKey()) && entry.getValue().answered() >= MIN_LOCALE_SAMPLE)
                .map(entry -> wilson(entry.getValue().correct(), entry.getValue().answered()))
                .toList();
        for (int i = 0; i < intervals.size(); i++) {
            for (int j = i + 1; j < intervals.size(); j++) {
                Interval left = intervals.get(i);
                Interval right = intervals.get(j);
                if (left.high < right.low || right.high < left.low) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Interval wilson(long correct, long answered) {
        double proportion = correct / (double) answered;
        double squared = SIGNIFICANCE_Z * SIGNIFICANCE_Z;
        double denominator = 1 + squared / answered;
        double center = (proportion + squared / (2 * answered)) / denominator;
        double margin = SIGNIFICANCE_Z
                * Math.sqrt(proportion * (1 - proportion) / answered + squared / (4.0 * answered * answered))
                / denominator;
        return new Interval(center - margin, center + margin);
    }

    private static LocalePerformance localePerformance(PerformanceRow row) {
        if (row == null) {
            return new LocalePerformance(0, 0, null, null);
        }
        return new LocalePerformance(
                row.answered(), row.correct(), rate(row.correct(), row.answered()), row.averageAnswerTimeSeconds());
    }

    private static Totals totals(Map<String, PerformanceRow> rows) {
        Totals totals = new Totals();
        rows.values().forEach(totals::add);
        return totals;
    }

    private static Double rate(long numerator, long denominator) {
        return denominator == 0 ? null : numerator * 100.0 / denominator;
    }

    private static Comparator<Category> categoryOrder() {
        return Comparator.comparing(Category::getDisplayOrder, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(Category::getId);
    }


    private static String questionText(QuizQuestion question, String locale) {
        return switch (locale) {
            case "ar" -> question.getQuestionAr();
            case "nl" -> question.getQuestionNl();
            case "fr" -> question.getQuestionFr();
            default -> question.getQuestionEn();
        };
    }

    private static String optionText(QuizAnswerOption option, String locale) {
        return switch (locale) {
            case "ar" -> option.getOptionTextAr();
            case "nl" -> option.getOptionTextNl();
            case "fr" -> option.getOptionTextFr();
            default -> option.getOptionTextEn();
        };
    }

    private static String explanation(QuizQuestion question, String locale) {
        return switch (locale) {
            case "ar" -> question.getExplanationAr();
            case "nl" -> question.getExplanationNl();
            case "fr" -> question.getExplanationFr();
            default -> question.getExplanationEn();
        };
    }

    private record QuestionFacts(
            QuizQuestion question,
            boolean structureValid,
            boolean deliveryBase,
            Map<String, Boolean> localeValid,
            boolean translationGap,
            boolean explanationGap,
            long presentations) {

        private boolean eligible(String locale) {
            return deliveryBase && Boolean.TRUE.equals(localeValid.get(locale));
        }

        private boolean eligibleAllLocales() {
            return deliveryBase && localeValid.values().stream().allMatch(Boolean.TRUE::equals);
        }
    }

    private static final class CategoryAccumulator {
        private final Category category;
        private long total;
        private long active;
        private long published;
        private long eligibleAll;
        private long translationGaps;
        private long explanationGaps;
        private long invalid;
        private long presentations;
        private final Map<String, Long> eligibleByLocale = new LinkedHashMap<>();
        private final EnumMap<QuizQuestion.DifficultyLevel, Long> eligibleByDifficulty =
                new EnumMap<>(QuizQuestion.DifficultyLevel.class);

        private CategoryAccumulator(Category category) {
            this.category = category;
            LOCALES.forEach(locale -> eligibleByLocale.put(locale, 0L));
            for (QuizQuestion.DifficultyLevel difficulty : QuizQuestion.DifficultyLevel.values()) {
                eligibleByDifficulty.put(difficulty, 0L);
            }
        }

        private void accept(QuestionFacts fact) {
            total++;
            if (Boolean.TRUE.equals(fact.question().getIsActive())) active++;
            if (fact.question().getStatus() == QuizQuestion.QuestionStatus.PUBLISHED) published++;
            if (fact.eligibleAllLocales()) {
                eligibleAll++;
                eligibleByDifficulty.computeIfPresent(
                        fact.question().getDifficultyLevel(), (ignored, count) -> count + 1);
            }
            LOCALES.forEach(locale -> {
                if (fact.eligible(locale)) eligibleByLocale.compute(locale, (ignored, count) -> count + 1);
            });
            if (fact.translationGap()) translationGaps++;
            if (fact.explanationGap()) explanationGaps++;
            if (!fact.structureValid()) invalid++;
            presentations += fact.presentations();
        }

        private boolean configuredForBlueprint() {
            return Boolean.TRUE.equals(category.getIsActive())
                    && category.getContentScope().supportsTheoreticalExam()
                    && category.getExamTargetWeight() != null
                    && category.getExamTargetWeight() > 0;
        }

        private CategoryHealth toResponse(long totalEligible, int totalWeight) {
            double inventoryShare = totalEligible == 0 ? 0 : eligibleAll * 100.0 / totalEligible;
            double targetShare = configuredForBlueprint() && totalWeight > 0
                    ? category.getExamTargetWeight() * 100.0 / totalWeight
                    : 0;
            int minimumRequired =
                    TheoryExamBlueprintPolicy.MIN_ELIGIBLE_QUESTIONS_PER_CATEGORY;
            long questionsNeeded = Math.max(0L, minimumRequired - eligibleAll);
            boolean examEligible =
                    configuredForBlueprint() && questionsNeeded == 0;
            String status;
            if (!Boolean.TRUE.equals(category.getIsActive())) {
                status = "INACTIVE";
            } else if (!configuredForBlueprint()) {
                status = "UNCONFIGURED";
            } else if (eligibleAll < TheoryExamBlueprintPolicy.MIN_ELIGIBLE_QUESTIONS_PER_CATEGORY) {
                status = "UNDERREPRESENTED";
            } else if (targetShare > 0 && inventoryShare > targetShare * OVERREPRESENTATION_FACTOR) {
                status = "OVERREPRESENTED";
            } else {
                status = "BALANCED";
            }
            Map<String, Long> difficulty = new LinkedHashMap<>();
            eligibleByDifficulty.forEach((key, value) -> difficulty.put(key.name(), value));
            return new CategoryHealth(
                    category.getId(), category.getCode(), category.getNameEn(), category.getNameNl(),
                    category.getNameFr(), category.getNameAr(), category.getDescriptionEn(),
                    category.getDescriptionNl(), category.getDescriptionFr(), category.getDescriptionAr(),
                    category.getDisplayOrder(), Boolean.TRUE.equals(category.getIsActive()),
                    category.getContentScope().name(), category.getExamTargetWeight(), total, active, published,
                    eligibleAll, Map.copyOf(eligibleByLocale), Map.copyOf(difficulty), translationGaps,
                    explanationGaps, invalid, presentations, inventoryShare, targetShare, status,
                    minimumRequired, questionsNeeded, examEligible);
        }
    }

    private static final class Totals {
        private long answered;
        private long correct;
        private double timeSum;
        private long timedAnswers;

        private void add(PerformanceRow row) {
            answered += row.answered();
            correct += row.correct();
            if (row.averageAnswerTimeSeconds() != null) {
                timeSum += row.averageAnswerTimeSeconds() * row.answered();
                timedAnswers += row.answered();
            }
        }

        private void add(Totals other) {
            answered += other.answered;
            correct += other.correct;
            timeSum += other.timeSum;
            timedAnswers += other.timedAnswers;
        }

        private Double averageTime() {
            return timedAnswers == 0 ? null : timeSum / timedAnswers;
        }
    }

    private record GroupKey(long categoryId, QuizQuestion.DifficultyLevel difficulty) {
    }

    private record Interval(double low, double high) {
    }
}
