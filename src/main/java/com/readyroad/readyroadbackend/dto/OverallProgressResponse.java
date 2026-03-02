package com.readyroad.readyroadbackend.dto;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for Story B2: View Overall Progress
 * Response containing user's overall learning progress metrics
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OverallProgressResponse {

    /**
     * Total number of questions attempted across all categories
     */
    private Integer totalAttempted;

    /**
     * Total number of questions answered correctly
     */
    private Integer totalCorrect;

    /**
     * Overall accuracy percentage (0-100)
     */
    private BigDecimal overallAccuracy;

    /**
     * Categories where user is struggling (<70% accuracy, ≥5 attempts)
     * Sorted by lowest accuracy first
     */
    private List<CategoryProgressSummary> weakCategories;

    /**
     * Categories where user excels (>85% accuracy, ≥5 attempts)
     * Sorted by highest accuracy first
     */
    private List<CategoryProgressSummary> strongCategories;

    /**
     * Number of questions remaining to reach 500 questions goal
     */
    private Integer questionsRemaining;

    /**
     * Consecutive days of study activity (real consecutive-day streak)
     */
    private Integer studyStreak;

    /**
     * Recommended difficulty level based on performance
     */
    private QuizQuestion.DifficultyLevel recommendedDifficulty;

    /**
     * Top 3 most-studied categories by questions attempted (descending).
     * Used by the dashboard "Most Studied" widget.
     */
    private List<CategoryProgressSummary> mostStudiedCategories;

    /**
     * Date of the user's last practice activity in ISO format (yyyy-MM-dd).
     * null if the user has never answered any question.
     */
    private String lastActivityDate;

    /**
     * Summary of progress for a specific category.
     * Used in weakCategories, strongCategories, and mostStudiedCategories lists.
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryProgressSummary {

        /**
         * Category name in English
         */
        private String categoryName;

        /**
         * Short category code (e.g. "A", "B", "C")
         */
        private String categoryCode;

        /**
         * Accuracy percentage for this category (0-100)
         */
        private BigDecimal accuracy;

        /**
         * Number of questions attempted in this category
         */
        private Integer attempted;
    }
}
