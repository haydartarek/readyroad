package com.readyroad.readyroadbackend.dto.practice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for submitting practice quiz answer
 * Story B1: Submit Practice Answer
 *
 * Provides immediate feedback with:
 * - Correctness of answer
 * - Correct option details (multi-language)
 * - Explanation (multi-language)
 * - Updated category progress
 *
 * @author ReadyRoad Team
 * @since Phase 5 - Story B1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitPracticeAnswerResponse {

    // ============================================================================
    // Question Info
    // ============================================================================

    private Long questionId;

    // ============================================================================
    // Correctness
    // ============================================================================

    private Boolean isCorrect;

    // ============================================================================
    // Selected Option (multi-language)
    // ============================================================================

    private Long selectedOptionId;
    private String selectedOptionTextEn;
    private String selectedOptionTextAr;
    private String selectedOptionTextNl;
    private String selectedOptionTextFr;

    // ============================================================================
    // Correct Option (multi-language)
    // ============================================================================

    private Long correctOptionId;
    private String correctOptionTextEn;
    private String correctOptionTextAr;
    private String correctOptionTextNl;
    private String correctOptionTextFr;

    // ============================================================================
    // Explanation (multi-language)
    // ============================================================================

    private String explanationEn;
    private String explanationAr;
    private String explanationNl;
    private String explanationFr;

    // ============================================================================
    // Category Info (multi-language)
    // ============================================================================

    private Long categoryId;
    private String categoryNameEn;
    private String categoryNameAr;
    private String categoryNameNl;
    private String categoryNameFr;

    // ============================================================================
    // Updated Progress (after this answer)
    // ============================================================================

    /**
     * User's updated accuracy rate in this category (0-100)
     */
    private BigDecimal updatedAccuracy;

    /**
     * Total questions attempted in this category
     */
    private Integer totalAttempts;

    /**
     * Total correct answers in this category
     */
    private Integer correctAttempts;

    /**
     * Current mastery level: BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
     */
    private String masteryLevel;
}
