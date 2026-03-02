package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion.DifficultyLevel;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion.TypicalErrorType;
import com.readyroad.readyroadbackend.domain.entity.UserCategoryProgress;
import com.readyroad.readyroadbackend.domain.model.UserQuestionHistory;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.UserCategoryProgressRepository;
import com.readyroad.readyroadbackend.domain.repository.UserQuestionHistoryRepository;
import com.readyroad.readyroadbackend.dto.ErrorPatternResponse;
import com.readyroad.readyroadbackend.dto.ErrorPatternResponse.ExampleQuestionDTO;
import com.readyroad.readyroadbackend.dto.WeakAreaRecommendationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // Supported error pattern types (6 types as per requirements)
    private static final List<TypicalErrorType> SUPPORTED_PATTERNS = Arrays.asList(
        TypicalErrorType.SIGN_CONFUSION,
        TypicalErrorType.SUPPLEMENTARY_IGNORED,
        TypicalErrorType.PRIORITY_MISUNDERSTANDING,
        TypicalErrorType.SPEED_LIMIT_ERROR,
        TypicalErrorType.ZONE_CONFUSION,
        TypicalErrorType.RULE_OVERGENERALIZATION
    );

    /**
     * Get error patterns for a user
     *
     * @param userId User ID
     * @return List of error patterns sorted by frequency descending
     */
    @Transactional(readOnly = true)
    public List<ErrorPatternResponse> getErrorPatterns(Long userId) {
        log.info("Getting error patterns for user {}", userId);

        // Get all wrong attempts for user
        List<UserQuestionHistory> wrongAttempts = historyRepository
            .findByUserIdAndIsCorrect(userId, false);

        log.debug("Found {} wrong attempts for user {}", wrongAttempts.size(), userId);

        if (wrongAttempts.isEmpty()) {
            // Return empty list when no wrong attempts exist (as per BDD specification)
            log.info("No wrong attempts found for user {}, returning empty list", userId);
            return new ArrayList<>();
        }

        // Extract question IDs
        List<Long> questionIds = wrongAttempts.stream()
            .map(UserQuestionHistory::getQuestionId)
            .distinct()
            .collect(Collectors.toList());

        // Load questions with their error types
        Map<Long, QuizQuestion> questionMap = questionRepository
            .findAllById(questionIds)
            .stream()
            .collect(Collectors.toMap(QuizQuestion::getId, q -> q));

        // Count wrong attempts per question
        Map<Long, Long> wrongCountPerQuestion = wrongAttempts.stream()
            .collect(Collectors.groupingBy(
                UserQuestionHistory::getQuestionId,
                Collectors.counting()
            ));

        // Group by error pattern type
        Map<TypicalErrorType, List<QuizQuestion>> questionsByPattern = new HashMap<>();
        Map<TypicalErrorType, Integer> countByPattern = new HashMap<>();

        for (UserQuestionHistory attempt : wrongAttempts) {
            QuizQuestion question = questionMap.get(attempt.getQuestionId());
            if (question != null) {
                TypicalErrorType errorType = question.getTypicalErrorType();

                // If question has no error type or is OTHER, categorize based on category
                if (errorType == null || errorType == TypicalErrorType.OTHER) {
                    errorType = inferErrorTypeFromCategory(question);
                }

                // Only count supported patterns
                if (SUPPORTED_PATTERNS.contains(errorType)) {
                    questionsByPattern.computeIfAbsent(errorType, k -> new ArrayList<>()).add(question);
                    countByPattern.merge(errorType, 1, Integer::sum);
                }
            }
        }

        // Calculate total wrong attempts (only for supported patterns)
        int totalWrongAttempts = countByPattern.values().stream()
            .mapToInt(Integer::intValue)
            .sum();

        // Build response list for all 6 supported patterns
        List<ErrorPatternResponse> responses = new ArrayList<>();

        for (TypicalErrorType pattern : SUPPORTED_PATTERNS) {
            int count = countByPattern.getOrDefault(pattern, 0);
            double percentage = totalWrongAttempts > 0
                ? (count * 100.0 / totalWrongAttempts)
                : 0.0;

            List<QuizQuestion> questionsForPattern = questionsByPattern.getOrDefault(pattern, new ArrayList<>());

            // Get up to 3 example questions for this pattern
            List<ExampleQuestionDTO> examples = questionsForPattern.stream()
                .distinct()
                .limit(3)
                .map(q -> buildExampleQuestion(q, wrongCountPerQuestion.getOrDefault(q.getId(), 0L).intValue()))
                .collect(Collectors.toList());

            responses.add(ErrorPatternResponse.builder()
                .patternType(pattern)
                .count(count)
                .percentage(Math.round(percentage * 10.0) / 10.0) // 1 decimal place
                .description(getPatternDescription(pattern))
                .exampleQuestions(examples)
                .build());
        }

        // Sort by count descending (most frequent first)
        responses.sort(Comparator.comparing(ErrorPatternResponse::getCount).reversed());

        log.info("Returning {} error patterns for user {}", responses.size(), userId);
        return responses;
    }


    /**
     * Infer error type from question category when typicalErrorType is null or OTHER
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
        //        E=Parking/Stopping, F=Info, G=Supplementary, Z=Zone, M=Mandatory, H=Indication
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
            .questionTextEn(question.getQuestionEn())
            .questionTextAr(question.getQuestionAr())
            .questionTextNl(question.getQuestionNl())
            .questionTextFr(question.getQuestionFr())
            .categoryName(question.getCategory() != null ? question.getCategory().getNameEn() : "Unknown")
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
                return "Confusion between similar traffic signs";
            case SUPPLEMENTARY_IGNORED:
                return "Ignoring supplementary panels below main signs";
            case PRIORITY_MISUNDERSTANDING:
                return "Misunderstanding right-of-way rules";
            case SPEED_LIMIT_ERROR:
                return "Incorrect interpretation of speed limit rules";
            case ZONE_CONFUSION:
                return "Confusion between different zone types (parking, residential, etc.)";
            case RULE_OVERGENERALIZATION:
                return "Applying a rule incorrectly in the wrong context";
            default:
                return "Other type of error";
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
     * Get weak area recommendations for a user
     * Returns top 3 weakest categories with personalized improvement plan
     *
     * @param userId User ID
     * @return List of up to 3 weak area recommendations sorted by priority
     */
    @Transactional(readOnly = true)
    public List<WeakAreaRecommendationResponse> getWeakAreaRecommendations(Long userId) {
        log.info("Getting weak area recommendations for user {}", userId);

        // Get all category progress for user
        List<UserCategoryProgress> progressRecords = progressRepository.findByUserId(userId);

        log.debug("Found {} progress records for user {}", progressRecords.size(), userId);

        if (progressRecords.isEmpty()) {
            log.info("User {} has no progress yet, returning empty recommendations", userId);
            return new ArrayList<>();
        }

        // Filter categories with measurable accuracy (>= 5 attempts)
        List<UserCategoryProgress> measurableCategories = progressRecords.stream()
            .filter(p -> p.getQuestionsAttempted() >= MIN_ATTEMPTS_FOR_RECOMMENDATION)
            .collect(Collectors.toList());

        log.debug("Found {} measurable categories (>= {} attempts)", measurableCategories.size(), MIN_ATTEMPTS_FOR_RECOMMENDATION);

        if (measurableCategories.isEmpty()) {
            log.info("User {} has no categories with sufficient attempts, returning empty recommendations", userId);
            return new ArrayList<>();
        }

        // Sort by accuracy (lowest first) to identify weakest areas
        measurableCategories.sort(Comparator.comparing(UserCategoryProgress::getAccuracyRate));

        // Take top 3 weakest categories
        List<UserCategoryProgress> top3Weakest = measurableCategories.stream()
            .limit(3)
            .collect(Collectors.toList());

        // Load category map (since relationship may not be loaded due to insertable=false)
        Map<Long, Category> categoryMap = categoryRepository.findAll().stream()
            .collect(Collectors.toMap(Category::getId, c -> c));

        // Build recommendations
        List<WeakAreaRecommendationResponse> recommendations = new ArrayList<>();
        int priority = 1;

        for (UserCategoryProgress progress : top3Weakest) {
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

            // Estimate time (questions * avg time)
            int estimatedTimeMinutes = (recommendedQuestions * AVERAGE_TIME_PER_QUESTION_SECONDS) / 60;

            WeakAreaRecommendationResponse recommendation = WeakAreaRecommendationResponse.builder()
                .categoryId(category.getId())
                .categoryCode(category.getCode())
                .categoryName(category.getNameEn())
                .currentAccuracy(Math.round(currentAccuracy * 10.0) / 10.0) // 1 decimal
                .targetAccuracy(TARGET_ACCURACY)
                .accuracyGap(Math.round(accuracyGap * 10.0) / 10.0) // 1 decimal
                .recommendedQuestions(recommendedQuestions)
                .recommendedDifficulty(recommendedDifficulty)
                .estimatedTimeMinutes(estimatedTimeMinutes)
                .priority(priority++)
                .questionsAttempted(progress.getQuestionsAttempted())
                .build();

            recommendations.add(recommendation);
        }

        log.info("Returning {} weak area recommendations for user {}", recommendations.size(), userId);
        return recommendations;
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
