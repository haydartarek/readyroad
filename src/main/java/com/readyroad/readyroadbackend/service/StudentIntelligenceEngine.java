package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.dto.StudentIntelligenceResponse;
import com.readyroad.readyroadbackend.dto.StudentIntelligenceResponse.CategoryTiming;
import com.readyroad.readyroadbackend.dto.StudentIntelligenceResponse.ExamAnalytics;
import com.readyroad.readyroadbackend.dto.StudentIntelligenceResponse.LearningPriority;
import com.readyroad.readyroadbackend.dto.StudentIntelligenceResponse.ProgressJourney;
import com.readyroad.readyroadbackend.dto.StudentIntelligenceResponse.Recommendation;
import com.readyroad.readyroadbackend.dto.StudentIntelligenceResponse.TimingAnalytics;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Deterministic learning-intelligence calculations.
 *
 * The engine is deliberately persistence-free: every value is reproducible
 * from the supplied historical evidence and no derived score is stored.
 */
@Component
public class StudentIntelligenceEngine {

    private static final double PASS_THRESHOLD = 82.0;
    private static final int CONSISTENCY_TARGET_DAYS = 12;
    private static final int MIN_CATEGORY_ATTEMPTS = 5;
    private static final int MIN_READINESS_QUESTIONS = 20;

    public StudentIntelligenceResponse analyze(AnalyticsInput input) {
        return analyze(input, LocalDate.now());
    }

    StudentIntelligenceResponse analyze(AnalyticsInput input, LocalDate today) {
        List<ScoredActivity> scoredActivities = input.activities().stream()
                .filter(activity -> activity.date() != null)
                .filter(activity -> Double.isFinite(activity.score()))
                .filter(activity -> activity.score() >= 0.0 && activity.score() <= 100.0)
                .sorted(Comparator.comparing(ScoredActivity::date))
                .toList();

        List<ScoredActivity> officialExams = scoredActivities.stream()
                .filter(activity -> "OFFICIAL_EXAM".equals(activity.type()))
                .toList();

        int questionHistoryAttempts = input.questions().stream()
                .mapToInt(question -> Math.max(0, question.attempts()))
                .sum();
        int categoryHistoryAttempts = input.categories().stream()
                .mapToInt(category -> Math.max(0, category.attempted()))
                .sum();
        int evidenceQuestions = Math.max(questionHistoryAttempts, categoryHistoryAttempts);
        int activeDaysLast28 = (int) input.activityDates().stream()
                .filter(date -> !date.isAfter(today))
                .filter(date -> !date.isBefore(today.minusDays(27)))
                .count();
        int totalLearningActivities = evidenceQuestions
                + scoredActivities.size()
                + input.lessonsStarted();

        String dataStatus = determineDataStatus(
                evidenceQuestions,
                officialExams.size(),
                activeDaysLast28,
                totalLearningActivities);

        ExamAnalytics examAnalytics = calculateExamAnalytics(input.totalOfficialExams(), officialExams);
        Integer consistency = input.activityDates().isEmpty()
                ? null
                : clampScore(activeDaysLast28 * 100.0 / CONSISTENCY_TARGET_DAYS);
        Integer retention = calculateRetention(input.questions());
        Integer confidence = calculateConfidence(
                evidenceQuestions,
                scoredActivities.size(),
                activeDaysLast28);

        Double practiceAccuracy = weightedCategoryAccuracy(input.categories());
        Double categoryMastery = categoryMasteryScore(input.categories());
        Double recentOfficialAverage = average(
                officialExams.stream()
                        .sorted(Comparator.comparing(ScoredActivity::date).reversed())
                        .limit(5)
                        .map(ScoredActivity::score)
                        .toList());
        Integer readiness = calculateReadiness(
                evidenceQuestions,
                officialExams.size(),
                recentOfficialAverage,
                practiceAccuracy,
                categoryMastery,
                consistency,
                retention,
                input.lessonsStarted(),
                input.lessonsCompleted());
        Integer passProbability = calculatePassProbability(
                readiness,
                confidence,
                officialExams);
        String overallTrend = classifyOverallTrend(scoredActivities);
        String studentLevel = determineStudentLevel(
                readiness,
                evidenceQuestions,
                officialExams,
                recentOfficialAverage);

        List<LearningPriority> priorities = calculateLearningPriorities(input.categories(), today);
        List<LearningPriority> strongestCategories = calculateStrongestCategories(input.categories(), today);
        List<Recommendation> recommendations = buildRecommendations(
                dataStatus,
                studentLevel,
                readiness,
                confidence,
                overallTrend,
                officialExams.size(),
                priorities,
                input.questions());
        TimingAnalytics timingAnalytics = calculateTimingAnalytics(input.questions(), officialExams);
        ProgressJourney progressJourney = calculateProgressJourney(input, officialExams.size(), today);

        return StudentIntelligenceResponse.builder()
                .dataStatus(dataStatus)
                .studentLevel(studentLevel)
                .examReadinessScore(readiness)
                .confidenceScore(confidence)
                .learningConsistencyScore(consistency)
                .knowledgeRetentionScore(retention)
                .estimatedPassProbability(passProbability)
                .weeklyProgress(compareWindows(scoredActivities, today, 7))
                .monthlyProgress(compareWindows(scoredActivities, today, 30))
                .overallLearningTrend(overallTrend)
                .totalLearningActivities(totalLearningActivities)
                .activeDaysLast28(activeDaysLast28)
                .evidenceQuestions(evidenceQuestions)
                .examAnalytics(examAnalytics)
                .timingAnalytics(timingAnalytics)
                .progressJourney(progressJourney)
                .learningPriorities(priorities)
                .strongestCategories(strongestCategories)
                .recommendations(recommendations)
                .build();
    }

    private ExamAnalytics calculateExamAnalytics(int totalOfficialExams, List<ScoredActivity> exams) {
        List<Double> scores = exams.stream().map(ScoredActivity::score).toList();
        int passed = (int) exams.stream().filter(ScoredActivity::passed).count();
        int completed = exams.size();
        List<Integer> durations = exams.stream()
                .map(ScoredActivity::durationSeconds)
                .filter(duration -> duration != null && duration > 0)
                .toList();

        BigDecimal passRate = completed == 0
                ? null
                : decimal(passed * 100.0 / completed);
        BigDecimal scoreTrend = splitWindowChange(scores);
        List<Double> passValues = exams.stream()
                .map(exam -> exam.passed() ? 100.0 : 0.0)
                .toList();

        return ExamAnalytics.builder()
                .totalExams(totalOfficialExams)
                .completedExams(completed)
                .passedExams(passed)
                .failedExams(completed - passed)
                .passRate(passRate)
                .averageScore(decimalOrNull(average(scores)))
                .highestScore(scores.isEmpty() ? null : decimal(scores.stream().mapToDouble(Double::doubleValue).max().orElse(0)))
                .lowestScore(scores.isEmpty() ? null : decimal(scores.stream().mapToDouble(Double::doubleValue).min().orElse(0)))
                .averageCompletionTimeSeconds(durations.isEmpty()
                        ? null
                        : (int) Math.round(durations.stream().mapToInt(Integer::intValue).average().orElse(0)))
                .fastestCompletionTimeSeconds(durations.stream().mapToInt(Integer::intValue).min().stream().boxed().findFirst().orElse(null))
                .slowestCompletionTimeSeconds(durations.stream().mapToInt(Integer::intValue).max().stream().boxed().findFirst().orElse(null))
                .scoreTrend(scoreTrend)
                .passTrend(splitWindowChange(passValues))
                .recentScores(exams.stream()
                        .sorted(Comparator.comparing(ScoredActivity::date).reversed())
                        .limit(10)
                        .map(activity -> decimal(activity.score()))
                        .toList())
                .build();
    }

    private String determineDataStatus(
            int evidenceQuestions,
            int completedOfficialExams,
            int activeDaysLast28,
            int totalLearningActivities) {
        if (evidenceQuestions == 0 && completedOfficialExams == 0 && totalLearningActivities == 0) {
            return "NO_DATA";
        }
        if (evidenceQuestions >= 100 && completedOfficialExams >= 3 && activeDaysLast28 >= 4) {
            return "SUFFICIENT";
        }
        return "LIMITED";
    }

    private Integer calculateRetention(List<QuestionEvidence> questions) {
        int totalAttempts = questions.stream()
                .filter(question -> question.attempts() >= 2)
                .mapToInt(QuestionEvidence::attempts)
                .sum();
        if (totalAttempts == 0) {
            return null;
        }
        int correct = questions.stream()
                .filter(question -> question.attempts() >= 2)
                .mapToInt(QuestionEvidence::correct)
                .sum();
        return clampScore(correct * 100.0 / totalAttempts);
    }

    private Integer calculateConfidence(int evidenceQuestions, int activities, int activeDaysLast28) {
        if (evidenceQuestions == 0 && activities == 0 && activeDaysLast28 == 0) {
            return null;
        }
        double evidence = Math.min(1.0, evidenceQuestions / 200.0) * 50.0;
        double sourceDepth = Math.min(1.0, activities / 10.0) * 25.0;
        double consistency = Math.min(1.0, activeDaysLast28 / (double) CONSISTENCY_TARGET_DAYS) * 25.0;
        return clampScore(evidence + sourceDepth + consistency);
    }

    private Integer calculateReadiness(
            int evidenceQuestions,
            int officialExamCount,
            Double officialAverage,
            Double practiceAccuracy,
            Double categoryMastery,
            Integer consistency,
            Integer retention,
            int lessonsStarted,
            int lessonsCompleted) {
        if (evidenceQuestions < MIN_READINESS_QUESTIONS && officialExamCount == 0) {
            return null;
        }

        List<WeightedValue> values = new ArrayList<>();
        add(values, officialAverage, 40);
        add(values, practiceAccuracy, 25);
        add(values, categoryMastery, 15);
        add(values, consistency == null ? null : consistency.doubleValue(), 10);
        add(values, retention == null ? null : retention.doubleValue(), 10);
        if (lessonsStarted > 0) {
            add(values, Math.min(100.0, lessonsCompleted * 100.0 / lessonsStarted), 5);
        }
        return values.isEmpty() ? null : clampScore(weightedAverage(values));
    }

    private Integer calculatePassProbability(
            Integer readiness,
            Integer confidence,
            List<ScoredActivity> officialExams) {
        if (readiness == null || officialExams.size() < 2) {
            return null;
        }
        List<ScoredActivity> recent = officialExams.stream()
                .sorted(Comparator.comparing(ScoredActivity::date).reversed())
                .limit(5)
                .toList();
        double passRate = recent.stream().filter(ScoredActivity::passed).count() * 100.0 / recent.size();
        double evidenceConfidence = confidence == null ? 0.0 : confidence;
        return clampScore(readiness * 0.55 + passRate * 0.30 + evidenceConfidence * 0.15);
    }

    private String determineStudentLevel(
            Integer readiness,
            int evidenceQuestions,
            List<ScoredActivity> officialExams,
            Double recentOfficialAverage) {
        if (readiness == null || evidenceQuestions < MIN_READINESS_QUESTIONS) {
            return "BEGINNER";
        }
        if (readiness < 40) {
            return "BEGINNER";
        }
        if (readiness < 55) {
            return "BASIC";
        }
        if (readiness < 70) {
            return "INTERMEDIATE";
        }
        if (readiness < 82 || officialExams.size() < 3 || recentOfficialAverage == null
                || recentOfficialAverage < PASS_THRESHOLD) {
            return "ADVANCED";
        }
        if (readiness >= 90 && officialExams.size() >= 5 && recentOfficialAverage >= 90) {
            return "EXPERT";
        }
        return "EXAM_READY";
    }

    private List<LearningPriority> calculateLearningPriorities(
            List<CategoryEvidence> categories,
            LocalDate today) {
        return categories.stream()
                .filter(category -> category.attempted() >= MIN_CATEGORY_ATTEMPTS)
                .map(category -> buildPriority(category, today))
                .filter(priority -> priority.getAccuracy().doubleValue() < PASS_THRESHOLD)
                .sorted(Comparator.comparing(LearningPriority::getPriorityScore).reversed())
                .limit(5)
                .toList();
    }

    private List<LearningPriority> calculateStrongestCategories(
            List<CategoryEvidence> categories,
            LocalDate today) {
        return categories.stream()
                .filter(category -> category.attempted() >= MIN_CATEGORY_ATTEMPTS)
                .map(category -> buildPriority(category, today))
                .filter(category -> category.getAccuracy().doubleValue() >= PASS_THRESHOLD)
                .sorted(Comparator.comparing(LearningPriority::getAccuracy).reversed())
                .limit(3)
                .toList();
    }

    private LearningPriority buildPriority(CategoryEvidence category, LocalDate today) {
        double accuracy = category.attempted() == 0
                ? 0
                : category.correct() * 100.0 / category.attempted();
        int daysSincePractice = category.lastPracticed() == null
                ? -1
                : (int) Math.max(0, ChronoUnit.DAYS.between(category.lastPracticed(), today));
        double frequencyScore = Math.min(100.0, category.attempted() * 100.0 / 30.0);
        double recencyScore = recencyScore(daysSincePractice);
        BigDecimal trendChange = categoryTrend(category);
        String trend = classifyTrend(trendChange);

        List<WeightedValue> riskParts = new ArrayList<>();
        add(riskParts, 100.0 - accuracy, 50);
        add(riskParts, frequencyScore, 20);
        if (daysSincePractice >= 0) {
            add(riskParts, recencyScore, 15);
        }
        if (trendChange != null) {
            add(riskParts, trendRisk(trendChange.doubleValue()), 15);
        }

        return LearningPriority.builder()
                .categoryId(category.id())
                .categoryCode(category.code())
                .categoryNameEn(category.nameEn())
                .categoryNameNl(category.nameNl())
                .categoryNameFr(category.nameFr())
                .categoryNameAr(category.nameAr())
                .accuracy(decimal(accuracy))
                .questionsAttempted(category.attempted())
                .priorityScore(decimal(weightedAverage(riskParts)))
                .confidenceScore(clampScore(Math.min(100.0, category.attempted() * 100.0 / 20.0)))
                .trend(trend)
                .trendChange(trendChange)
                .daysSincePractice(daysSincePractice < 0 ? null : daysSincePractice)
                .build();
    }

    private List<Recommendation> buildRecommendations(
            String dataStatus,
            String studentLevel,
            Integer readiness,
            Integer confidence,
            String overallTrend,
            int officialExamCount,
            List<LearningPriority> priorities,
            List<QuestionEvidence> questions) {
        List<Recommendation> result = new ArrayList<>();
        if ("NO_DATA".equals(dataStatus)) {
            result.add(recommendation(
                    "student_intelligence.recommendation.start_learning",
                    null,
                    "/lessons",
                    1));
            return result;
        }

        if (!priorities.isEmpty()) {
            LearningPriority weakest = priorities.getFirst();
            result.add(recommendation(
                    "student_intelligence.recommendation.focus_weak_category",
                    weakest.getCategoryCode(),
                    "/practice/" + weakest.getCategoryCode(),
                    1));
        }

        boolean hasRepeatedErrors = questions.stream()
                .anyMatch(question -> question.attempts() >= 2
                        && question.correct() * 100.0 / question.attempts() < 70.0);
        if (hasRepeatedErrors) {
            result.add(recommendation(
                    "student_intelligence.recommendation.review_repeated_errors",
                    null,
                    "/analytics/error-patterns",
                    result.size() + 1));
        }

        if ("DECLINING".equals(overallTrend)) {
            result.add(recommendation(
                    "student_intelligence.recommendation.reverse_decline",
                    null,
                    "/practice/random",
                    result.size() + 1));
        } else if (confidence != null && confidence < 50 && !"BEGINNER".equals(studentLevel)) {
            result.add(recommendation(
                    "student_intelligence.recommendation.build_consistency",
                    null,
                    "/lessons",
                    result.size() + 1));
        }

        if (officialExamCount < 3 || readiness == null || readiness < 82) {
            result.add(recommendation(
                    "student_intelligence.recommendation.take_practice_exam",
                    null,
                    "/exam",
                    result.size() + 1));
        } else {
            result.add(recommendation(
                    "student_intelligence.recommendation.maintain_readiness",
                    null,
                    "/practice/random",
                    result.size() + 1));
        }
        return result.stream().limit(3).toList();
    }

    private TimingAnalytics calculateTimingAnalytics(
            List<QuestionEvidence> questions,
            List<ScoredActivity> officialExams) {
        List<QuestionEvidence> timedQuestions = questions.stream()
                .filter(question -> question.timeTakenSeconds() != null && question.timeTakenSeconds() > 0)
                .toList();
        Integer averageAnswerTime = timedQuestions.isEmpty()
                ? null
                : (int) Math.round(timedQuestions.stream()
                        .mapToInt(QuestionEvidence::timeTakenSeconds)
                        .average()
                        .orElse(0));

        List<Double> orderedAnswerTimes = timedQuestions.stream()
                .filter(question -> question.lastAnswered() != null)
                .sorted(Comparator.comparing(QuestionEvidence::lastAnswered))
                .map(question -> question.timeTakenSeconds().doubleValue())
                .toList();
        List<Double> orderedExamTimes = officialExams.stream()
                .filter(exam -> exam.durationSeconds() != null && exam.durationSeconds() > 0)
                .sorted(Comparator.comparing(ScoredActivity::date))
                .map(exam -> exam.durationSeconds().doubleValue())
                .toList();

        Map<String, List<QuestionEvidence>> byCategory = timedQuestions.stream()
                .filter(question -> question.categoryCode() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        QuestionEvidence::categoryCode,
                        LinkedHashMap::new,
                        java.util.stream.Collectors.toList()));
        List<CategoryTiming> categoryTimings = byCategory.values().stream()
                .map(samples -> {
                    QuestionEvidence first = samples.getFirst();
                    int average = (int) Math.round(samples.stream()
                            .mapToInt(QuestionEvidence::timeTakenSeconds)
                            .average()
                            .orElse(0));
                    return CategoryTiming.builder()
                            .categoryId(first.categoryId())
                            .categoryCode(first.categoryCode())
                            .categoryNameEn(first.categoryNameEn())
                            .categoryNameNl(first.categoryNameNl())
                            .categoryNameFr(first.categoryNameFr())
                            .categoryNameAr(first.categoryNameAr())
                            .averageAnswerTimeSeconds(average)
                            .samples(samples.size())
                            .build();
                })
                .sorted(Comparator.comparing(CategoryTiming::getSamples).reversed())
                .toList();

        return TimingAnalytics.builder()
                .averageAnswerTimeSeconds(averageAnswerTime)
                .answerTimeTrendSeconds(splitWindowChange(orderedAnswerTimes))
                .examTimeTrendSeconds(splitWindowChange(orderedExamTimes))
                .answerTimingSamples(timedQuestions.size())
                .answerTimingScope(timedQuestions.isEmpty()
                        ? "UNAVAILABLE"
                        : "LATEST_RECORDED_PER_QUESTION")
                .categoryTimings(categoryTimings)
                .build();
    }

    private ProgressJourney calculateProgressJourney(
            AnalyticsInput input,
            int completedOfficialExams,
            LocalDate today) {
        int activeDaysLast7 = countActiveDays(input.activityDates(), today, 7);
        int activeDaysLast30 = countActiveDays(input.activityDates(), today, 30);
        int masteredCategories = (int) input.categories().stream()
                .filter(category -> category.attempted() >= MIN_CATEGORY_ATTEMPTS)
                .filter(category -> category.correct() * 100.0 / category.attempted() >= PASS_THRESHOLD)
                .count();

        return ProgressJourney.builder()
                .lessonsStarted(input.lessonsStarted())
                .lessonsCompleted(input.lessonsCompleted())
                .lessonRevisitCount(null)
                .currentStudyStreak(calculateCurrentStreak(input.activityDates(), today))
                .activeToday(input.activityDates().contains(today))
                .activeDaysLast7(activeDaysLast7)
                .activeDaysLast30(activeDaysLast30)
                .completedPracticeSessions(input.completedPracticeSessions())
                .completedOfficialExams(completedOfficialExams)
                .masteredCategories(masteredCategories)
                .masteredSigns(input.masteredSigns())
                .build();
    }

    private int countActiveDays(Set<LocalDate> activityDates, LocalDate today, int days) {
        LocalDate start = today.minusDays(days - 1L);
        return (int) activityDates.stream()
                .filter(date -> !date.isBefore(start) && !date.isAfter(today))
                .count();
    }

    private int calculateCurrentStreak(Set<LocalDate> activityDates, LocalDate today) {
        if (activityDates.isEmpty()) {
            return 0;
        }
        LocalDate cursor = activityDates.contains(today)
                ? today
                : activityDates.contains(today.minusDays(1)) ? today.minusDays(1) : null;
        if (cursor == null) {
            return 0;
        }
        int streak = 0;
        while (activityDates.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    private Recommendation recommendation(String key, String categoryCode, String path, int priority) {
        return Recommendation.builder()
                .key(key)
                .categoryCode(categoryCode)
                .actionPath(path)
                .priority(priority)
                .build();
    }

    private BigDecimal compareWindows(List<ScoredActivity> activities, LocalDate today, int days) {
        LocalDate currentStart = today.minusDays(days - 1L);
        LocalDate previousStart = currentStart.minusDays(days);
        List<Double> current = activities.stream()
                .filter(activity -> !activity.date().isBefore(currentStart) && !activity.date().isAfter(today))
                .map(ScoredActivity::score)
                .toList();
        List<Double> previous = activities.stream()
                .filter(activity -> !activity.date().isBefore(previousStart)
                        && activity.date().isBefore(currentStart))
                .map(ScoredActivity::score)
                .toList();
        Double currentAverage = average(current);
        Double previousAverage = average(previous);
        return currentAverage == null || previousAverage == null
                ? null
                : decimal(currentAverage - previousAverage);
    }

    private String classifyOverallTrend(List<ScoredActivity> activities) {
        BigDecimal change = splitWindowChange(activities.stream().map(ScoredActivity::score).toList());
        return classifyTrend(change);
    }

    private BigDecimal splitWindowChange(List<Double> values) {
        if (values.size() < 3) {
            return null;
        }
        int midpoint = values.size() / 2;
        Double older = average(values.subList(0, midpoint));
        Double newer = average(values.subList(midpoint, values.size()));
        return older == null || newer == null ? null : decimal(newer - older);
    }

    private BigDecimal categoryTrend(CategoryEvidence category) {
        if (category.recentAttempts() < MIN_CATEGORY_ATTEMPTS
                || category.previousAttempts() < MIN_CATEGORY_ATTEMPTS) {
            return null;
        }
        double recent = category.recentCorrect() * 100.0 / category.recentAttempts();
        double previous = category.previousCorrect() * 100.0 / category.previousAttempts();
        return decimal(recent - previous);
    }

    private String classifyTrend(BigDecimal change) {
        if (change == null) {
            return "INSUFFICIENT_DATA";
        }
        if (change.doubleValue() >= 5.0) {
            return "IMPROVING";
        }
        if (change.doubleValue() <= -5.0) {
            return "DECLINING";
        }
        return "STABLE";
    }

    private double trendRisk(double change) {
        if (change <= -10) {
            return 100;
        }
        if (change < 0) {
            return 75;
        }
        if (change < 5) {
            return 50;
        }
        return 0;
    }

    private double recencyScore(int days) {
        if (days <= 7) {
            return 0;
        }
        if (days <= 30) {
            return 35;
        }
        if (days <= 90) {
            return 70;
        }
        return 100;
    }

    private Double weightedCategoryAccuracy(List<CategoryEvidence> categories) {
        int attempts = categories.stream().mapToInt(CategoryEvidence::attempted).sum();
        if (attempts == 0) {
            return null;
        }
        int correct = categories.stream().mapToInt(CategoryEvidence::correct).sum();
        return correct * 100.0 / attempts;
    }

    private Double categoryMasteryScore(List<CategoryEvidence> categories) {
        List<CategoryEvidence> measurable = categories.stream()
                .filter(category -> category.attempted() >= MIN_CATEGORY_ATTEMPTS)
                .toList();
        if (measurable.isEmpty()) {
            return null;
        }
        long mastered = measurable.stream()
                .filter(category -> category.correct() * 100.0 / category.attempted() >= PASS_THRESHOLD)
                .count();
        return mastered * 100.0 / measurable.size();
    }

    private Double average(List<Double> values) {
        return values.isEmpty()
                ? null
                : values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    private void add(List<WeightedValue> values, Double value, double weight) {
        if (value != null && Double.isFinite(value)) {
            values.add(new WeightedValue(value, weight));
        }
    }

    private double weightedAverage(List<WeightedValue> values) {
        double weight = values.stream().mapToDouble(WeightedValue::weight).sum();
        if (weight == 0) {
            return 0;
        }
        return values.stream().mapToDouble(value -> value.value() * value.weight()).sum() / weight;
    }

    private int clampScore(double value) {
        return (int) Math.round(Math.max(0.0, Math.min(100.0, value)));
    }

    private BigDecimal decimalOrNull(Double value) {
        return value == null ? null : decimal(value);
    }

    private BigDecimal decimal(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    private record WeightedValue(double value, double weight) {
    }

    public record AnalyticsInput(
            int totalOfficialExams,
            List<ScoredActivity> activities,
            List<CategoryEvidence> categories,
            List<QuestionEvidence> questions,
            Set<LocalDate> activityDates,
            int lessonsStarted,
            int lessonsCompleted,
            int completedPracticeSessions,
            int masteredSigns) {
    }

    public record ScoredActivity(
            LocalDate date,
            double score,
            boolean passed,
            String type,
            Integer durationSeconds) {
    }

    public record CategoryEvidence(
            Long id,
            String code,
            String nameEn,
            String nameNl,
            String nameFr,
            String nameAr,
            int attempted,
            int correct,
            LocalDate lastPracticed,
            int recentAttempts,
            int recentCorrect,
            int previousAttempts,
            int previousCorrect) {
    }

    public record QuestionEvidence(
            int attempts,
            int correct,
            LocalDate lastAnswered,
            Integer timeTakenSeconds,
            Long categoryId,
            String categoryCode,
            String categoryNameEn,
            String categoryNameNl,
            String categoryNameFr,
            String categoryNameAr) {
    }
}
