package com.readyroad.readyroadbackend.dto;

import com.readyroad.readyroadbackend.domain.entity.UserCategoryProgress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Category Progress Response DTO
 *
 * Story B3: View Category-Level Progress
 *
 * Represents detailed progress information for a single category.
 * Includes statistics, mastery level, and weak/strong identification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryProgressResponse {

    /**
     * Category identifier
     */
    private Long categoryId;

    /**
     * Category name (localized - English)
     */
    private String categoryName;

    private String categoryNameEn;
    private String categoryNameNl;
    private String categoryNameFr;
    private String categoryNameAr;

    /**
     * Category code (unique identifier)
     */
    private String categoryCode;

    /**
     * Total questions attempted in this category
     */
    private Integer questionsAttempted;

    /**
     * Number of correct answers in this category
     */
    private Integer correctAnswers;

    /**
     * Accuracy rate as percentage (0-100)
     */
    private BigDecimal accuracyRate;

    /**
     * Current mastery level for this category
     */
    private UserCategoryProgress.MasteryLevel masteryLevel;

    /**
     * Last time user practiced this category
     */
    private LocalDateTime lastPracticed;

    /**
     * Is this category identified as a weak area?
     * Weak: accuracy < 70% AND attempts >= 5
     */
    private boolean isWeakCategory;

    /**
     * Is this category identified as a strong area?
     * Strong: accuracy > 85% AND attempts >= 5
     */
    private boolean isStrongCategory;

    /**
     * Questions remaining in this category (if known)
     * Can be null if total questions in category is not tracked
     */
    private Integer questionsRemaining;

    /**
     * Recommended next difficulty level for this category
     */
    private String recommendedDifficulty;
}
