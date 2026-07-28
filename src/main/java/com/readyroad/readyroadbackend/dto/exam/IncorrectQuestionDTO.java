package com.readyroad.readyroadbackend.dto.exam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Incorrect Question DTO - Story A3 (Production Ready)
 *
 * Shows details of questions answered incorrectly,
 * including correct answer and explanation for learning
 *
 * Version: 2.0 - Enhanced with analytics fields and error categorization
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncorrectQuestionDTO {

    private Long questionId;

    // Question Text (all languages)
    private String questionTextEn;
    private String questionTextAr;
    private String questionTextNl;
    private String questionTextFr;

    // User's Answer
    private Long selectedOptionId;
    private String selectedOptionText;
    private String selectedOptionTextEn;
    private String selectedOptionTextAr;
    private String selectedOptionTextNl;
    private String selectedOptionTextFr;

    // Correct Answer
    private Long correctOptionId;
    private String correctOptionText;
    private String correctOptionTextEn;
    private String correctOptionTextAr;
    private String correctOptionTextNl;
    private String correctOptionTextFr;

    // Category
    private String categoryName;
    private String categoryNameEn;
    private String categoryNameAr;
    private String categoryNameNl;
    private String categoryNameFr;

    // ========== PRODUCTION ENHANCEMENTS (v2.0) ==========

    // Category Quick Reference
    private String categoryCode; // "A", "B", "C" for filtering

    // Visual Aid
    private String contentImageUrl; // Traffic sign image URL (nullable)

    // Analytics Fields (for future tracking)
    private Long userAnswerOptionId; // Same as selectedOptionId (for consistency)
    private Long correctAnswerOptionId; // Same as correctOptionId (for consistency)

    // Error Categorization
    private String typicalErrorType; // "SIGN_CONFUSION", "SPEED_LIMIT_ERROR", etc. (nullable)
}
