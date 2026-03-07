package com.readyroad.readyroadbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Full result of a Belgian theory exam practice session.
 * Returned by POST /api/quiz/theory-exam/check — stateless, no DB session.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TheoryExamResultDTO {
    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private int unanswered;           // timed-out questions
    private double scorePercentage;   // correctAnswers / totalQuestions * 100
    private boolean passed;           // correctAnswers >= passingScore
    private int passingScore;         // 41 (Belgian standard)
    private List<TheoryExamQuestionResultDTO> questions;
}
