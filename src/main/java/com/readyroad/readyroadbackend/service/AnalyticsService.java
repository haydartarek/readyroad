package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationAnswer;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion.DifficultyLevel;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion.TypicalErrorType;
import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.entity.UserCategoryProgress;
import com.readyroad.readyroadbackend.domain.entity.UserErrorPattern;
import com.readyroad.readyroadbackend.domain.entity.UserQuestionHistory;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationAnswerRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.domain.repository.UserCategoryProgressRepository;
import com.readyroad.readyroadbackend.domain.repository.UserErrorPatternRepository;
import com.readyroad.readyroadbackend.domain.repository.UserQuestionHistoryRepository;
import com.readyroad.readyroadbackend.dto.ErrorPatternResponse;
import com.readyroad.readyroadbackend.dto.ErrorPatternResponse.ErrorGroupDTO;
import com.readyroad.readyroadbackend.dto.ErrorPatternResponse.ExampleQuestionDTO;
import com.readyroad.readyroadbackend.dto.WeakAreaRecommendationResponse;
import com.readyroad.readyroadbackend.dto.WeakAreasOverviewResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Story C1: View Error Patterns
 * Story C2: Recommend Weak Areas
 * Analyzes user's mistake patterns to help them improve efficiently
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final UserQuestionHistoryRepository historyRepository;
    private final QuizQuestionRepository questionRepository;
    private final UserCategoryProgressRepository progressRepository;
    private final CategoryRepository categoryRepository;
    private final UserErrorPatternRepository errorPatternRepository;
    private final RoadSignRepository roadSignRepository;
    private final RoadSignReferenceTextResolver roadSignReferenceTextResolver;
    private final BackendMessageService messages;
    private final ExamSimulationAnswerRepository examAnswerRepository;

    // Supported error pattern types (6 types as per requirements)
    private static final List<TypicalErrorType> SUPPORTED_PATTERNS = Arrays.asList(
            TypicalErrorType.SIGN_CONFUSION,
            TypicalErrorType.SUPPLEMENTARY_IGNORED,
            TypicalErrorType.PRIORITY_MISUNDERSTANDING,
            TypicalErrorType.SPEED_LIMIT_ERROR,
            TypicalErrorType.ZONE_CONFUSION,
            TypicalErrorType.RULE_OVERGENERALIZATION);

    /**
     * Get error patterns for a user
     *
     * @param userId User ID
     * @return List of error patterns sorted by frequency descending
     */
    @Transactional(readOnly = true)
    public List<ErrorPatternResponse> getErrorPatterns(Long userId) {
        log.info("Getting error patterns for user {}", userId);
        List<UserErrorPattern> rawPatterns = errorPatternRepository.findAllByUserIdOrderByOccurredAtDesc(userId);
        List<UserQuestionHistory> wrongAttempts = historyRepository
                .findByUserIdAndTimesIncorrectGreaterThan(userId, 0);
        List<ExamSimulationAnswer> completedExamAnswers = examAnswerRepository.findHistoryForUser(
                userId,
                ExamSimulation.ExamStatus.COMPLETED);
        RecentErrorComparison recentComparison = buildRecentErrorComparison(completedExamAnswers);

        if (rawPatterns.isEmpty() && wrongAttempts.isEmpty() && !recentComparison.hasCurrentAttempt()) {
            log.info("No wrong attempts found for user {} in any source, returning empty list", userId);
            return new ArrayList<>();
        }

        List<Long> questionIds = wrongAttempts.stream()
                .map(UserQuestionHistory::getQuestionId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, QuizQuestion> questionMap = questionRepository
                .findAllById(questionIds)
                .stream()
                .collect(Collectors.toMap(QuizQuestion::getId, q -> q));
        Map<Long, Integer> wrongCountPerQuestion = wrongAttempts.stream()
                .collect(Collectors.toMap(
                        UserQuestionHistory::getQuestionId,
                        attempt -> attempt.getTimesIncorrect() != null ? attempt.getTimesIncorrect() : 0,
                        Integer::sum));
        Set<String> signCodes = rawPatterns.stream()
                .map(UserErrorPattern::getTrafficSignCode)
                .filter(Objects::nonNull)
                .filter(code -> !code.isBlank())
                .collect(Collectors.toSet());
        Map<String, RoadSign> signsByCode = roadSignRepository.findBySignCodeIn(signCodes).stream()
                .collect(Collectors.toMap(RoadSign::getSignCode, sign -> sign, (first, ignored) -> first));

        Map<TypicalErrorType, PatternAggregate> aggregates = SUPPORTED_PATTERNS.stream()
                .collect(Collectors.toMap(pattern -> pattern, ignored -> new PatternAggregate()));

        for (UserErrorPattern raw : rawPatterns) {
            TypicalErrorType type = toTypicalErrorType(raw.getErrorType());
            if (type == null || !SUPPORTED_PATTERNS.contains(type)) {
                continue;
            }
            PatternAggregate aggregate = aggregates.get(type);
            aggregate.count++;
            if (raw.getQuestionRefId() != null) {
                aggregate.uniqueReferences.add(
                        (raw.getQuestionRefType() != null ? raw.getQuestionRefType() : "PATTERN")
                                + ":" + raw.getQuestionRefId());
            } else if (raw.getTrafficSignCode() != null) {
                aggregate.uniqueReferences.add("SIGN:" + raw.getTrafficSignCode());
            }
            String referenceKey = raw.getTrafficSignCode() != null
                    ? "SIGN:" + raw.getTrafficSignCode()
                    : (raw.getQuestionRefType() != null ? raw.getQuestionRefType() : "PATTERN")
                            + ":" + raw.getQuestionRefId();
            aggregate.addReference(referenceKey, 1);
            RoadSign sign = signsByCode.get(raw.getTrafficSignCode());
            if (sign != null && sign.getCategory() != null) {
                aggregate.addGroup(signFamilyGroup(sign.getCategory()), 1);
            }
            if (raw.getRuleCategory() != null && !raw.getRuleCategory().isBlank()) {
                aggregate.addGroup(ErrorGroupDTO.builder()
                        .groupType("CATEGORY")
                        .code(raw.getRuleCategory())
                        .count(0)
                        .build(), 1);
            }
            aggregate.includeDate(raw.getOccurredAt());
        }

        for (UserQuestionHistory attempt : wrongAttempts) {
            QuizQuestion question = questionMap.get(attempt.getQuestionId());
            if (question != null) {
                TypicalErrorType errorType = question.getTypicalErrorType();
                if (errorType == null || errorType == TypicalErrorType.OTHER) {
                    errorType = inferErrorTypeFromCategory(question);
                }
                if (SUPPORTED_PATTERNS.contains(errorType)) {
                    PatternAggregate aggregate = aggregates.get(errorType);
                    int wrongCount = attempt.getTimesIncorrect() != null ? attempt.getTimesIncorrect() : 0;
                    aggregate.count += wrongCount;
                    aggregate.questions.add(question);
                    aggregate.uniqueReferences.add("QUIZ:" + question.getId());
                    aggregate.addReference("QUIZ:" + question.getId(), wrongCount);
                    if (question.getCategory() != null) {
                        aggregate.addGroup(categoryGroup(question.getCategory()), wrongCount);
                    }
                    aggregate.includeDate(attempt.getCreatedAt());
                    aggregate.includeDate(attempt.getAnsweredAt());
                }
            }
        }

        int totalWrongAttempts = recentComparison.hasCurrentAttempt()
                ? SUPPORTED_PATTERNS.stream().mapToInt(recentComparison::currentCount).sum()
                : aggregates.values().stream().mapToInt(aggregate -> aggregate.count).sum();
        List<ErrorPatternResponse> responses = new ArrayList<>();

        for (TypicalErrorType pattern : SUPPORTED_PATTERNS) {
            PatternAggregate aggregate = aggregates.get(pattern);
            int count = recentComparison.hasCurrentAttempt()
                    ? recentComparison.currentCount(pattern)
                    : aggregate.count;
            Integer previousCount = recentComparison.hasPreviousAttempt()
                    ? recentComparison.previousCount(pattern)
                    : null;
            Integer delta = previousCount != null ? count - previousCount : null;
            String trend = determineTrend(delta);
            double percentage = totalWrongAttempts > 0
                    ? (count * 100.0 / totalWrongAttempts)
                    : 0.0;
            List<ExampleQuestionDTO> examples = aggregate.questions.stream()
                    .distinct()
                    .limit(3)
                    .map(q -> buildExampleQuestion(q, wrongCountPerQuestion.getOrDefault(q.getId(), 0)))
                    .collect(Collectors.toList());
            List<ErrorGroupDTO> groups = buildGroups(pattern, aggregate, count);

            responses.add(ErrorPatternResponse.builder()
                    .patternType(pattern)
                    .count(count)
                    .previousCount(previousCount)
                    .currentCount(count)
                    .delta(delta)
                    .trend(trend)
                    .recentAttemptsCount(recentComparison.attemptCount())
                    .lastCalculatedAt(recentComparison.lastCalculatedAt())
                    .percentage(Math.round(percentage * 10.0) / 10.0)
                    .description(getPatternDescription(pattern))
                    .severity(determineSeverity(count, percentage))
                    .uniqueQuestions(aggregate.uniqueReferences.size())
                    .firstOccurredAt(aggregate.firstOccurredAt)
                    .lastOccurredAt(aggregate.lastOccurredAt)
                    .recommendationKey("error_patterns.rec_" + pattern.name().toLowerCase(Locale.ROOT))
                    .sourceScope(recentComparison.hasCurrentAttempt()
                            ? "LAST_TWO_COMPLETED_EXAMS"
                            : "COMPLETE_HISTORY")
                    .groups(groups)
                    .exampleQuestions(examples)
                    .build());
        }

        responses.sort(Comparator.comparing(ErrorPatternResponse::getCount).reversed());
        log.info(
                "Returning {} combined error patterns for user {} from {} sign rows and {} theory rows",
                responses.size(),
                userId,
                rawPatterns.size(),
                wrongAttempts.size());
        return responses;
    }

    private RecentErrorComparison buildRecentErrorComparison(List<ExamSimulationAnswer> answers) {
        if (answers == null || answers.isEmpty()) {
            return RecentErrorComparison.empty();
        }

        Map<Long, List<ExamSimulationAnswer>> answersByExam = answers.stream()
                .filter(answer -> answer.getExam() != null && answer.getExam().getId() != null)
                .collect(Collectors.groupingBy(answer -> answer.getExam().getId()));

        List<List<ExamSimulationAnswer>> recentAttempts = answersByExam.values().stream()
                .sorted(Comparator
                        .comparing(
                                (List<ExamSimulationAnswer> attempt) -> attempt.get(0).getExam().getCompletedAt(),
                                Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(
                                attempt -> attempt.get(0).getExam().getId(),
                                Comparator.reverseOrder()))
                .limit(2)
                .toList();

        if (recentAttempts.isEmpty()) {
            return RecentErrorComparison.empty();
        }

        Map<TypicalErrorType, Integer> current = countAttemptErrors(recentAttempts.get(0));
        Map<TypicalErrorType, Integer> previous = recentAttempts.size() > 1
                ? countAttemptErrors(recentAttempts.get(1))
                : Map.of();
        LocalDateTime lastCalculatedAt = recentAttempts.get(0).get(0).getExam().getCompletedAt() == null
                ? null
                : LocalDateTime.ofInstant(
                        recentAttempts.get(0).get(0).getExam().getCompletedAt(),
                        ZoneOffset.UTC);

        return new RecentErrorComparison(
                current,
                previous,
                recentAttempts.size(),
                lastCalculatedAt);
    }

    private Map<TypicalErrorType, Integer> countAttemptErrors(List<ExamSimulationAnswer> answers) {
        Map<TypicalErrorType, Integer> counts = new EnumMap<>(TypicalErrorType.class);
        for (ExamSimulationAnswer answer : answers) {
            if (Boolean.TRUE.equals(answer.getIsCorrect()) || answer.getQuestion() == null) {
                continue;
            }
            TypicalErrorType type = answer.getQuestion().getTypicalErrorType();
            if (type == null || type == TypicalErrorType.OTHER) {
                type = inferErrorTypeFromCategory(answer.getQuestion());
            }
            if (SUPPORTED_PATTERNS.contains(type)) {
                counts.merge(type, 1, Integer::sum);
            }
        }
        return counts;
    }

    private String determineTrend(Integer delta) {
        if (delta == null) {
            return "INSUFFICIENT_DATA";
        }
        if (delta < 0) {
            return "IMPROVED";
        }
        if (delta > 0) {
            return "WORSENED";
        }
        return "UNCHANGED";
    }

    private record RecentErrorComparison(
            Map<TypicalErrorType, Integer> current,
            Map<TypicalErrorType, Integer> previous,
            int attemptCount,
            LocalDateTime lastCalculatedAt) {

        private static RecentErrorComparison empty() {
            return new RecentErrorComparison(Map.of(), Map.of(), 0, null);
        }

        private boolean hasCurrentAttempt() {
            return attemptCount > 0;
        }

        private boolean hasPreviousAttempt() {
            return attemptCount > 1;
        }

        private int currentCount(TypicalErrorType type) {
            return current.getOrDefault(type, 0);
        }

        private int previousCount(TypicalErrorType type) {
            return previous.getOrDefault(type, 0);
        }
    }

    private TypicalErrorType toTypicalErrorType(UserErrorPattern.ErrorType type) {
        if (type == null) {
            return null;
        }
        try {
            return TypicalErrorType.valueOf(type.name());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private String determineSeverity(int count, double percentage) {
        if (count >= 10 || percentage >= 35.0) {
            return "CRITICAL";
        }
        if (count >= 5 || percentage >= 20.0) {
            return "HIGH";
        }
        if (count >= 2) {
            return "MODERATE";
        }
        return count == 1 ? "LOW" : "NONE";
    }

    private static class PatternAggregate {
        private int count;
        private final Set<String> uniqueReferences = new HashSet<>();
        private final List<QuizQuestion> questions = new ArrayList<>();
        private final Map<String, ErrorGroupDTO> groups = new HashMap<>();
        private final Map<String, Integer> referenceCounts = new HashMap<>();
        private LocalDateTime firstOccurredAt;
        private LocalDateTime lastOccurredAt;

        private void addGroup(ErrorGroupDTO group, int occurrenceCount) {
            String key = group.getGroupType() + ":" + group.getCode();
            ErrorGroupDTO existing = groups.get(key);
            if (existing == null) {
                group.setCount(occurrenceCount);
                groups.put(key, group);
            } else {
                existing.setCount(existing.getCount() + occurrenceCount);
            }
        }

        private void addReference(String reference, int occurrenceCount) {
            if (reference == null || reference.endsWith(":null")) {
                return;
            }
            referenceCounts.merge(reference, occurrenceCount, Integer::sum);
        }

        private void includeDate(LocalDateTime value) {
            if (value == null) {
                return;
            }
            if (firstOccurredAt == null || value.isBefore(firstOccurredAt)) {
                firstOccurredAt = value;
            }
            if (lastOccurredAt == null || value.isAfter(lastOccurredAt)) {
                lastOccurredAt = value;
            }
        }
    }

    private List<ErrorGroupDTO> buildGroups(
            TypicalErrorType pattern,
            PatternAggregate aggregate,
            int totalCount) {
        List<ErrorGroupDTO> groups = new ArrayList<>(aggregate.groups.values());
        groups.add(ErrorGroupDTO.builder()
                .groupType("LEGAL_CONCEPT")
                .code(pattern.name())
                .count(totalCount)
                .build());

        int repeatedCount = aggregate.referenceCounts.values().stream()
                .filter(count -> count >= 2)
                .mapToInt(Integer::intValue)
                .sum();
        if (repeatedCount > 0) {
            groups.add(ErrorGroupDTO.builder()
                    .groupType("REPEATED_MISCONCEPTION")
                    .code(pattern.name())
                    .count(repeatedCount)
                    .build());
        }

        groups.sort(Comparator
                .comparing(ErrorGroupDTO::getGroupType)
                .thenComparing(ErrorGroupDTO::getCount, Comparator.reverseOrder())
                .thenComparing(ErrorGroupDTO::getCode));
        return groups;
    }

    private ErrorGroupDTO categoryGroup(Category category) {
        return ErrorGroupDTO.builder()
                .groupType("CATEGORY")
                .code(category.getCode())
                .nameEn(category.getNameEn())
                .nameAr(category.getNameAr())
                .nameNl(category.getNameNl())
                .nameFr(category.getNameFr())
                .count(0)
                .build();
    }

    private ErrorGroupDTO signFamilyGroup(SignCategory category) {
        return ErrorGroupDTO.builder()
                .groupType("TRAFFIC_SIGN_FAMILY")
                .code(category.name())
                .count(0)
                .build();
    }

    /**
     * Infer error type from question category when typicalErrorType is null or
     * OTHER
     */
    private TypicalErrorType inferErrorTypeFromCategory(QuizQuestion question) {
        if (question.getCategory() == null) {
            return TypicalErrorType.RULE_OVERGENERALIZATION; // Default fallback
        }

        String categoryCode = question.getCategory().getCode();
        if (categoryCode == null) {
            return TypicalErrorType.RULE_OVERGENERALIZATION;
        }

        // Map Belgian driving test category codes to error types.
        // Codes: A=Warning, B=Priority, C=Prohibition, D=Obligation,
        // E=Parking/Stopping, F=Info, G=Supplementary, Z=Zone, M=Mandatory,
        // H=Indication
        switch (categoryCode.toUpperCase()) {
            case "A": // Warning signs – easily confused triangular shapes
            case "F": // Information / direction signs
            case "M": // Mandatory direction signs
            case "H": // Indication / blue panel signs
                return TypicalErrorType.SIGN_CONFUSION;
            case "B": // Priority / right-of-way signs
                return TypicalErrorType.PRIORITY_MISUNDERSTANDING;
            case "E": // Parking / stopping restrictions
            case "Z": // Zone entry/exit signs
                return TypicalErrorType.ZONE_CONFUSION;
            case "G": // Supplementary panels below main signs
                return TypicalErrorType.SUPPLEMENTARY_IGNORED;
            case "C": // Prohibition signs – rules misapplied in wrong context
            case "D": // Obligation / mandatory signs
            default:
                return TypicalErrorType.RULE_OVERGENERALIZATION;
        }
    }

    /**
     * Build example question DTO
     */
    private ExampleQuestionDTO buildExampleQuestion(QuizQuestion question, int timesWrong) {
        return ExampleQuestionDTO.builder()
                .questionId(question.getId())
                .questionTextEn(roadSignReferenceTextResolver.resolveEn(question.getQuestionEn()))
                .questionTextAr(roadSignReferenceTextResolver.resolveAr(question.getQuestionAr()))
                .questionTextNl(roadSignReferenceTextResolver.resolveNl(question.getQuestionNl()))
                .questionTextFr(roadSignReferenceTextResolver.resolveFr(question.getQuestionFr()))
                .categoryName(question.getCategory() != null
                        ? question.getCategory().getNameEn()
                        : messages.get("analytics.category.unknown"))
                .categoryNameEn(question.getCategory() != null ? question.getCategory().getNameEn() : null)
                .categoryNameAr(question.getCategory() != null ? question.getCategory().getNameAr() : null)
                .categoryNameNl(question.getCategory() != null ? question.getCategory().getNameNl() : null)
                .categoryNameFr(question.getCategory() != null ? question.getCategory().getNameFr() : null)
                .contentImageUrl(question.getContentImageUrl())
                .timesWrong(timesWrong)
                .build();
    }

    /**
     * Get human-readable description for error pattern type
     */
    private String getPatternDescription(TypicalErrorType pattern) {
        switch (pattern) {
            case SIGN_CONFUSION:
                return messages.get("analytics.pattern.sign_confusion");
            case SUPPLEMENTARY_IGNORED:
                return messages.get("analytics.pattern.supplementary_ignored");
            case PRIORITY_MISUNDERSTANDING:
                return messages.get("analytics.pattern.priority_misunderstanding");
            case SPEED_LIMIT_ERROR:
                return messages.get("analytics.pattern.speed_limit_error");
            case ZONE_CONFUSION:
                return messages.get("analytics.pattern.zone_confusion");
            case RULE_OVERGENERALIZATION:
                return messages.get("analytics.pattern.rule_overgeneralization");
            default:
                return messages.get("analytics.pattern.other");
        }
    }

    // ============================================================================
    // Story C2: Recommend Weak Areas
    // ============================================================================

    private static final double TARGET_ACCURACY = 80.0;
    private static final int MIN_ATTEMPTS_FOR_RECOMMENDATION = 5;
    private static final int RECOMMENDED_QUESTIONS_BASE = 15;
    private static final int AVERAGE_TIME_PER_QUESTION_SECONDS = 45;

    /**
     * Get weak area recommendations for a user.
     * Returns ALL categories below the 80% target (not capped at 3) together
     * with accurate summary statistics so the frontend can show:
     * - real overall accuracy (not just the average of weak areas)
     * - correct "Strong Areas" count (total practiced − weak count)
     *
     * @param userId User ID
     * @return Wrapper with weak-area list and summary stats
     */
    @Transactional(readOnly = true)
    public WeakAreasOverviewResponse getWeakAreaRecommendations(Long userId) {
        log.info("Getting weak area recommendations for user {}", userId);

        // Get all category progress for user
        List<UserCategoryProgress> progressRecords = progressRepository.findByUserId(userId).stream()
                .filter(this::isActiveTheoreticalProgress)
                .toList();

        log.debug("Found {} progress records for user {}", progressRecords.size(), userId);

        if (progressRecords.isEmpty()) {
            log.info("User {} has no progress yet, returning empty recommendations", userId);
            return WeakAreasOverviewResponse.builder()
                    .weakAreas(new ArrayList<>())
                    .totalPracticedCategories(0)
                    .overallAccuracy(null)
                    .build();
        }

        // Filter categories with measurable accuracy (>= 5 attempts)
        List<UserCategoryProgress> measurableCategories = progressRecords.stream()
                .filter(p -> p.getQuestionsAttempted() >= MIN_ATTEMPTS_FOR_RECOMMENDATION)
                .collect(Collectors.toList());

        log.debug("Found {} measurable categories (>= {} attempts)", measurableCategories.size(),
                MIN_ATTEMPTS_FOR_RECOMMENDATION);

        // Compute summary stats across ALL measurable categories
        int totalPracticed = measurableCategories.size();
        int totalAttempted = measurableCategories.stream().mapToInt(UserCategoryProgress::getQuestionsAttempted).sum();
        int totalCorrect = measurableCategories.stream().mapToInt(UserCategoryProgress::getCorrectAnswers).sum();
        double overallAccuracy = totalAttempted > 0
                ? Math.round((totalCorrect * 100.0 / totalAttempted) * 10.0) / 10.0
                : 0.0;

        if (measurableCategories.isEmpty()) {
            log.info("User {} has no categories with sufficient attempts, returning empty recommendations", userId);
            return WeakAreasOverviewResponse.builder()
                    .weakAreas(new ArrayList<>())
                    .totalPracticedCategories(0)
                    .overallAccuracy(null)
                    .build();
        }

        // Keep only categories below target. Final ordering uses weighted risk,
        // not raw accuracy alone.
        List<UserCategoryProgress> weakCategories = measurableCategories.stream()
                .filter(p -> p.getAccuracyRate().doubleValue() < TARGET_ACCURACY)
                .collect(Collectors.toList());

        // Load category map (since relationship may not be loaded due to
        // insertable=false)
        Map<Long, Category> categoryMap = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(Category::getId, c -> c));

        // Build recommendations for ALL weak categories (no artificial cap)
        List<WeakAreaRecommendationResponse> recommendations = new ArrayList<>();
        int priority = 1;

        for (UserCategoryProgress progress : weakCategories) {
            Category category = categoryMap.get(progress.getCategoryId());
            if (category == null) {
                log.warn("Category not found for progress record: userId={}, categoryId={}",
                        progress.getUserId(), progress.getCategoryId());
                continue;
            }

            double currentAccuracy = progress.getAccuracyRate().doubleValue();

            // Calculate accuracy gap
            double accuracyGap = TARGET_ACCURACY - currentAccuracy;

            // Calculate recommended questions based on accuracy gap
            int recommendedQuestions = calculateRecommendedQuestions(currentAccuracy);

            // Determine recommended difficulty
            String recommendedDifficulty = determineRecommendedDifficulty(currentAccuracy);

            // Estimate time to complete recommended practice
            int estimatedTimeMinutes = (recommendedQuestions * AVERAGE_TIME_PER_QUESTION_SECONDS) / 60;
            int daysSincePractice = progress.getLastPracticed() == null
                    ? -1
                    : (int) Math.max(0, ChronoUnit.DAYS.between(
                            progress.getLastPracticed().toLocalDate(),
                            java.time.LocalDate.now()));
            int confidenceScore = Math.min(100, progress.getQuestionsAttempted() * 5);
            double frequencyScore = Math.min(100.0, progress.getQuestionsAttempted() * 100.0 / 30.0);
            double recencyScore = daysSincePractice < 0
                    ? 0.0
                    : daysSincePractice <= 7 ? 0.0
                    : daysSincePractice <= 30 ? 35.0
                    : daysSincePractice <= 90 ? 70.0
                    : 100.0;
            double knownWeight = daysSincePractice < 0 ? 85.0 : 100.0;
            double priorityScore = ((100.0 - currentAccuracy) * 55.0
                    + frequencyScore * 20.0
                    + confidenceScore * 10.0
                    + (daysSincePractice < 0 ? 0.0 : recencyScore * 15.0))
                    / knownWeight;

            WeakAreaRecommendationResponse recommendation = WeakAreaRecommendationResponse.builder()
                    .categoryId(category.getId())
                    .categoryCode(category.getCode())
                    .categoryName(category.getNameEn())
                    .categoryNameEn(category.getNameEn())
                    .categoryNameNl(category.getNameNl())
                    .categoryNameFr(category.getNameFr())
                    .categoryNameAr(category.getNameAr())
                    .currentAccuracy(Math.round(currentAccuracy * 10.0) / 10.0)
                    .targetAccuracy(TARGET_ACCURACY)
                    .accuracyGap(Math.round(accuracyGap * 10.0) / 10.0)
                    .recommendedQuestions(recommendedQuestions)
                    .recommendedDifficulty(recommendedDifficulty)
                    .estimatedTimeMinutes(estimatedTimeMinutes)
                    .priority(priority++)
                    .questionsAttempted(progress.getQuestionsAttempted())
                    .priorityScore(Math.round(priorityScore * 10.0) / 10.0)
                    .confidenceScore(confidenceScore)
                    .trend("INSUFFICIENT_DATA")
                    .daysSincePractice(daysSincePractice < 0 ? null : daysSincePractice)
                    .build();

            recommendations.add(recommendation);
        }

        recommendations.sort(Comparator
                .comparing(WeakAreaRecommendationResponse::getPriorityScore)
                .reversed()
                .thenComparing(WeakAreaRecommendationResponse::getCurrentAccuracy));
        for (int index = 0; index < recommendations.size(); index++) {
            recommendations.get(index).setPriority(index + 1);
        }

        log.info("Returning {} weak area recommendations for user {} (total practiced: {}, overallAccuracy: {}%)",
                recommendations.size(), userId, totalPracticed, overallAccuracy);
        return WeakAreasOverviewResponse.builder()
                .weakAreas(recommendations)
                .totalPracticedCategories(totalPracticed)
                .overallAccuracy(overallAccuracy)
                .build();
    }

    private boolean isActiveTheoreticalProgress(UserCategoryProgress progress) {
        Category category = progress.getCategory();
        return category != null
                && Boolean.TRUE.equals(category.getIsActive())
                && category.getContentScope() != null
                && category.getContentScope().supportsTheoreticalExam();
    }

    /**
     * Calculate number of questions to recommend based on accuracy gap
     */
    private int calculateRecommendedQuestions(double currentAccuracy) {
        double accuracyGap = TARGET_ACCURACY - currentAccuracy;

        if (accuracyGap >= 40) {
            // Very weak: 25 questions
            return 25;
        } else if (accuracyGap >= 25) {
            // Moderately weak: 20 questions
            return 20;
        } else {
            // Slightly weak: 15 questions
            return RECOMMENDED_QUESTIONS_BASE;
        }
    }

    /**
     * Determine recommended difficulty based on current accuracy
     */
    private String determineRecommendedDifficulty(double currentAccuracy) {
        if (currentAccuracy < 70.0) {
            return DifficultyLevel.EASY.name();
        } else if (currentAccuracy < 80.0) {
            return DifficultyLevel.MEDIUM.name();
        } else {
            return DifficultyLevel.HARD.name();
        }
    }
}
