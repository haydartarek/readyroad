package com.readyroad.readyroadbackend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating / updating an exam simulation question.
 *
 * Exam questions use 4 fixed inline answer options (option1–option4),
 * and correctAnswer is an integer 1–4 indicating the correct option.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminExamQuestionRequest {

    @NotBlank(message = "Category code is required")
    @Size(max = 10)
    private String categoryCode;

    /** EASY, MEDIUM, HARD – defaults to MEDIUM if null */
    private String difficulty;

    @NotBlank(message = "English question text is required")
    private String questionEn;

    private String questionAr;
    private String questionNl;
    private String questionFr;

    // ── Option 1 ──────────────────────────────────────────
    @NotBlank(message = "Option 1 English text is required")
    private String option1En;
    private String option1Ar;
    private String option1Nl;
    private String option1Fr;

    // ── Option 2 ──────────────────────────────────────────
    @NotBlank(message = "Option 2 English text is required")
    private String option2En;
    private String option2Ar;
    private String option2Nl;
    private String option2Fr;

    // ── Option 3 (optional — question can have 2 or 3 options) ────────────
    private String option3En;
    private String option3Ar;
    private String option3Nl;
    private String option3Fr;

    // option4 is intentionally removed from admin DTO — max 3 options allowed.

    /** Which option is correct: 1, 2, or 3 */
    @NotNull(message = "Correct answer is required")
    @Min(value = 1, message = "Correct answer must be 1, 2, or 3")
    @Max(value = 3, message = "Correct answer must be 1, 2, or 3")
    private Integer correctAnswer;

    private String explanationEn;
    private String explanationAr;
    private String explanationNl;
    private String explanationFr;

    private String imageUrl;

    private Boolean isImportant;
    private Boolean isActive;
}
