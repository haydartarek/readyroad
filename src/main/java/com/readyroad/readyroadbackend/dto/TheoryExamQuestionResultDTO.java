package com.readyroad.readyroadbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Per-question result returned after checking a Belgian theory exam (practice
 * mode).
 * Includes multilingual question text, the correct option, and the user's
 * selected option.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TheoryExamQuestionResultDTO {
    private Long questionId;

    // Multilingual question text
    private String questionEn;
    private String questionAr;
    private String questionNl;
    private String questionFr;

    private Long selectedOptionId; // null when user timed out
    private Long correctOptionId;

    // Multilingual correct option text (for review screen)
    private String correctOptionEn;
    private String correctOptionAr;
    private String correctOptionNl;
    private String correctOptionFr;

    private Boolean isCorrect;
    private Boolean wasTimeout; // true when selectedOptionId == null

    // Category metadata for grouping in results
    private String categoryNameEn;
    private String categoryNameAr;
    private String categoryNameNl;
    private String categoryNameFr;

    private String difficultyLevel; // "EASY" | "MEDIUM" | "HARD"
}
