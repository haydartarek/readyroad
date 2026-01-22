package com.readyroad.readyroadbackend.dto;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion.DifficultyLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Story C2: Recommend Weak Areas - Response DTO
 * Provides personalized recommendations for areas that need improvement
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeakAreaRecommendationResponse {

    /**
     * Category name (e.g., "Speed Limits", "Parking Rules")
     */
    private String categoryName;

    /**
     * Current accuracy percentage in this category (0-100)
     */
    private Double currentAccuracy;

    /**
     * Target accuracy to reach (always 80.0 as per Belgian standards)
     */
    private Double targetAccuracy;

    /**
     * Gap between current and target accuracy (targetAccuracy - currentAccuracy)
     */
    private Double accuracyGap;

    /**
     * Number of questions recommended to practice
     */
    private Integer recommendedQuestions;

    /**
     * Recommended difficulty level based on current performance
     */
    private String recommendedDifficulty;

    /**
     * Estimated time in minutes to complete recommended practice
     */
    private Integer estimatedTimeMinutes;

    /**
     * Priority level (1 = highest priority, 3 = lowest)
     * Based on how weak this area is compared to others
     */
    private Integer priority;

    /**
     * Category ID for API navigation
     */
    private Long categoryId;

    /**
     * Category code for filtering
     */
    private String categoryCode;
}
