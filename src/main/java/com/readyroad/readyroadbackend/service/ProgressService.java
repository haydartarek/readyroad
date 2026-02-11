package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.entity.UserCategoryProgress;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.UserCategoryProgressRepository;
import com.readyroad.readyroadbackend.dto.CategoryProgressResponse;
import com.readyroad.readyroadbackend.dto.OverallProgressResponse;
import com.readyroad.readyroadbackend.dto.OverallProgressResponse.CategoryProgressSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for Story B2: View Overall Progress
 * Calculates and returns user's overall learning progress metrics
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProgressService {

    private final UserCategoryProgressRepository progressRepository;
    private final CategoryRepository categoryRepository;
    private final QuizQuestionRepository questionRepository;

    private static final int TOTAL_QUESTIONS_GOAL = 500;
    private static final int MIN_ATTEMPTS_FOR_CATEGORIZATION = 5;
    private static final BigDecimal WEAK_THRESHOLD = BigDecimal.valueOf(70.00);
    private static final BigDecimal STRONG_THRESHOLD = BigDecimal.valueOf(85.00);
    private static final int MIN_ATTEMPTS_FOR_DIFFICULTY = 10;

    /**
     * Get overall progress for a user
     *
     * @param userId The user ID
     * @return Overall progress response with statistics
     */
    @Transactional(readOnly = true)
    public OverallProgressResponse getOverallProgress(Long userId) {
        log.info("Getting overall progress for user {}", userId);

        // Get all progress records for the user
        List<UserCategoryProgress> progressRecords = progressRepository.findByUserId(userId);

        if (progressRecords.isEmpty()) {
            log.info("User {} has no progress yet, returning zero progress", userId);
            return buildZeroProgressResponse();
        }

        // Calculate aggregated statistics
        int totalAttempted = progressRecords.stream()
                .mapToInt(UserCategoryProgress::getQuestionsAttempted)
                .sum();

        int totalCorrect = progressRecords.stream()
                .mapToInt(UserCategoryProgress::getCorrectAnswers)
                .sum();

        BigDecimal overallAccuracy = calculateOverallAccuracy(totalAttempted, totalCorrect);

        // Get category map for names
        Map<Long, Category> categoryMap = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(Category::getId, c -> c));

        // Identify weak and strong categories
        List<CategoryProgressSummary> weakCategories = identifyWeakCategories(progressRecords, categoryMap);
        List<CategoryProgressSummary> strongCategories = identifyStrongCategories(progressRecords, categoryMap);

        // Calculate remaining questions
        int questionsRemaining = TOTAL_QUESTIONS_GOAL - totalAttempted;

        // Calculate study streak
        int studyStreak = calculateStudyStreak(progressRecords);

        // Recommend difficulty
        QuizQuestion.DifficultyLevel recommendedDifficulty = recommendDifficulty(totalAttempted, overallAccuracy);

        log.info("User {} progress: attempted={}, correct={}, accuracy={}%, streak={}",
                userId, totalAttempted, totalCorrect, overallAccuracy, studyStreak);

        return OverallProgressResponse.builder()
                .totalAttempted(totalAttempted)
                .totalCorrect(totalCorrect)
                .overallAccuracy(overallAccuracy)
                .weakCategories(weakCategories)
                .strongCategories(strongCategories)
                .questionsRemaining(questionsRemaining)
                .studyStreak(studyStreak)
                .recommendedDifficulty(recommendedDifficulty)
                .build();
    }

    /**
     * Build zero progress response for new users
     */
    private OverallProgressResponse buildZeroProgressResponse() {
        return OverallProgressResponse.builder()
                .totalAttempted(0)
                .totalCorrect(0)
                .overallAccuracy(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                .weakCategories(new ArrayList<>())
                .strongCategories(new ArrayList<>())
                .questionsRemaining(TOTAL_QUESTIONS_GOAL)
                .studyStreak(0)
                .recommendedDifficulty(QuizQuestion.DifficultyLevel.EASY)
                .build();
    }

    /**
     * Calculate overall accuracy percentage
     */
    private BigDecimal calculateOverallAccuracy(int totalAttempted, int totalCorrect) {
        if (totalAttempted == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return BigDecimal.valueOf(totalCorrect)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalAttempted), 2, RoundingMode.HALF_UP);
    }

    /**
     * Identify weak categories (<70% accuracy, ≥5 attempts)
     * Sorted by lowest accuracy first
     */
    private List<CategoryProgressSummary> identifyWeakCategories(
            List<UserCategoryProgress> progressRecords,
            Map<Long, Category> categoryMap) {

        return progressRecords.stream()
                .filter(p -> p.getQuestionsAttempted() >= MIN_ATTEMPTS_FOR_CATEGORIZATION)
                .filter(p -> {
                    BigDecimal accuracy = p.getAccuracyRate();
                    return accuracy.compareTo(WEAK_THRESHOLD) < 0;
                })
                .map(p -> buildCategorySummary(p, categoryMap))
                .sorted(Comparator.comparing(CategoryProgressSummary::getAccuracy))
                .collect(Collectors.toList());
    }

    /**
     * Identify strong categories (>85% accuracy, ≥5 attempts)
     * Sorted by highest accuracy first
     */
    private List<CategoryProgressSummary> identifyStrongCategories(
            List<UserCategoryProgress> progressRecords,
            Map<Long, Category> categoryMap) {

        return progressRecords.stream()
                .filter(p -> p.getQuestionsAttempted() >= MIN_ATTEMPTS_FOR_CATEGORIZATION)
                .filter(p -> {
                    BigDecimal accuracy = p.getAccuracyRate();
                    return accuracy.compareTo(STRONG_THRESHOLD) > 0;
                })
                .map(p -> buildCategorySummary(p, categoryMap))
                .sorted(Comparator.comparing(CategoryProgressSummary::getAccuracy).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Build category progress summary
     */
    private CategoryProgressSummary buildCategorySummary(
            UserCategoryProgress progress,
            Map<Long, Category> categoryMap) {

        Category category = categoryMap.get(progress.getCategoryId());
        String categoryName = (category != null) ? category.getNameEn() : "Unknown Category";

        return CategoryProgressSummary.builder()
                .categoryName(categoryName)
                .accuracy(progress.getAccuracyRate().setScale(2, RoundingMode.HALF_UP))
                .attempted(progress.getQuestionsAttempted())
                .build();
    }

    /**
     * Calculate study streak (consecutive days)
     * Returns 0 if last practice was >24h ago
     */
    private int calculateStudyStreak(List<UserCategoryProgress> progressRecords) {
        // Find most recent practice date
        LocalDateTime mostRecent = progressRecords.stream()
                .map(UserCategoryProgress::getLastPracticed)
                .filter(date -> date != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        if (mostRecent == null) {
            return 0;
        }

        // Check if practiced within last 24 hours
        long hoursSinceLastPractice = ChronoUnit.HOURS.between(mostRecent, LocalDateTime.now());
        if (hoursSinceLastPractice > 24) {
            return 0;
        }

        // For now, return 1 if practiced today
        // TODO: Implement full consecutive days calculation by querying practice history
        return 1;
    }

    /**
     * Recommend difficulty level based on performance
     *
     * Rules:
     * - <10 attempts: EASY (insufficient data)
     * - ≥10 attempts, <70%: EASY (struggling)
     * - ≥10 attempts, 70-85%: MEDIUM (average)
     * - ≥10 attempts, >85%: HARD (high performer)
     */
    private QuizQuestion.DifficultyLevel recommendDifficulty(int totalAttempted, BigDecimal overallAccuracy) {
        // Insufficient data
        if (totalAttempted < MIN_ATTEMPTS_FOR_DIFFICULTY) {
            return QuizQuestion.DifficultyLevel.EASY;
        }

        // High performer
        if (overallAccuracy.compareTo(STRONG_THRESHOLD) > 0) {
            return QuizQuestion.DifficultyLevel.HARD;
        }

        // Struggling user
        if (overallAccuracy.compareTo(WEAK_THRESHOLD) < 0) {
            return QuizQuestion.DifficultyLevel.EASY;
        }

        // Average performer
        return QuizQuestion.DifficultyLevel.MEDIUM;
    }

    /**
     * Get category-level progress for a user
     * Story B3: View Category-Level Progress
     *
     * @param userId The user ID
     * @return List of category progress responses
     */
    @Transactional(readOnly = true)
    public List<CategoryProgressResponse> getCategoryProgress(Long userId) {
        log.info("Getting category progress for user {}", userId);

        // Get all progress records for user
        List<UserCategoryProgress> progressRecords = progressRepository.findByUserId(userId);

        if (progressRecords.isEmpty()) {
            log.info("User {} has no category progress", userId);
            return new ArrayList<>();
        }

        // Get all categories for name mapping
        Map<Long, Category> categoryMap = categoryRepository.findAll().stream()
            .collect(Collectors.toMap(Category::getId, c -> c));

        // Convert to response DTOs
        List<CategoryProgressResponse> responses = progressRecords.stream()
            .map(progress -> mapToCategoryProgressResponse(progress, categoryMap.get(progress.getCategoryId())))
            .collect(Collectors.toList());

        log.info("Returning {} category progress records for user {}", responses.size(), userId);
        return responses;
    }

    /**
     * Map UserCategoryProgress entity to CategoryProgressResponse DTO
     */
    private CategoryProgressResponse mapToCategoryProgressResponse(
            UserCategoryProgress progress,
            Category category
    ) {
        BigDecimal accuracyRate = progress.getAccuracyRate()
            .setScale(2, RoundingMode.HALF_UP);

        int questionsAttempted = progress.getQuestionsAttempted();

        // Determine if weak category (< 70% AND >= 5 attempts)
        boolean isWeak = accuracyRate.compareTo(WEAK_THRESHOLD) < 0
            && questionsAttempted >= MIN_ATTEMPTS_FOR_CATEGORIZATION;

        // Determine if strong category (> 85% AND >= 5 attempts)
        boolean isStrong = accuracyRate.compareTo(STRONG_THRESHOLD) > 0
            && questionsAttempted >= MIN_ATTEMPTS_FOR_CATEGORIZATION;

        // Recommend difficulty for this category
        String recommendedDifficulty = recommendCategoryDifficulty(
            accuracyRate,
            questionsAttempted
        ).name();

        return CategoryProgressResponse.builder()
            .categoryId(progress.getCategoryId())
            .categoryName(category != null ? category.getNameEn() : "Unknown")
            .categoryCode(category != null ? category.getCode() : null)
            .questionsAttempted(questionsAttempted)
            .correctAnswers(progress.getCorrectAnswers())
            .accuracyRate(accuracyRate)
            .masteryLevel(progress.getMasteryLevel())
            .lastPracticed(progress.getLastPracticed())
            .isWeakCategory(isWeak)
            .isStrongCategory(isStrong)
            .questionsRemaining(null)  // TODO: Calculate based on category question count
            .recommendedDifficulty(recommendedDifficulty)
            .build();
    }

    /**
     * Recommend difficulty level for a specific category
     */
    private QuizQuestion.DifficultyLevel recommendCategoryDifficulty(
            BigDecimal accuracy,
            int attempts
    ) {
        // Not enough data
        if (attempts < MIN_ATTEMPTS_FOR_DIFFICULTY) {
            return QuizQuestion.DifficultyLevel.EASY;
        }

        // High performer in this category
        if (accuracy.compareTo(STRONG_THRESHOLD) > 0) {
            return QuizQuestion.DifficultyLevel.HARD;
        }

        // Struggling in this category
        if (accuracy.compareTo(WEAK_THRESHOLD) < 0) {
            return QuizQuestion.DifficultyLevel.EASY;
        }

        // Average performer in this category
        return QuizQuestion.DifficultyLevel.MEDIUM;
    }
}


