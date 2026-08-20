package com.readyroad.readyroadbackend.dto.exam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * All-Answered Question DTO
 *
 * Represents every question answered in an exam (both correct and incorrect).
 * Used to populate the full question review in exam history.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllAnsweredQuestionDTO {

    private Long questionId;

    // Question text (all languages)
    private String questionTextEn;
    private String questionTextAr;
    private String questionTextNl;
    private String questionTextFr;

    // User's selected answer
    private Long selectedOptionId;
    private String selectedOptionText;
    private String selectedOptionTextEn;
    private String selectedOptionTextAr;
    private String selectedOptionTextNl;
    private String selectedOptionTextFr;

    // Correct answer
    private Long correctOptionId;
    private String correctOptionText;
    private String correctOptionTextEn;
    private String correctOptionTextAr;
    private String correctOptionTextNl;
    private String correctOptionTextFr;

    // Current explanation saved on the question (all languages)
    private String explanationEn;
    private String explanationAr;
    private String explanationNl;
    private String explanationFr;

    // Category
    private String categoryName;
    private String categoryNameEn;
    private String categoryNameAr;
    private String categoryNameNl;
    private String categoryNameFr;
    private String categoryCode;

    // Visual aid
    private String contentImageUrl;

    // Result flag
    private Boolean isCorrect;
    private Boolean wasTimeout;
    private String difficulty;
}
