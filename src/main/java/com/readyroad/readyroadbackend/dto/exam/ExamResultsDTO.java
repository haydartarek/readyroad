package com.readyroad.readyroadbackend.dto.exam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Exam Results DTO - Story A3 (Production Ready)
 *
 * Comprehensive exam results including:
 * - Overall score and pass/fail status
 * - Category-level breakdown
 * - Incorrect questions with explanations
 * - Time statistics
 * - Personalized recommendations
 *
 * Version: 2.0 (Production Enhancement)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultsDTO {

    // Basic Info
    private Long examId;
    private Long userId;
    private Instant completedAt;

    // Score Summary
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Double scorePercentage;
    private Boolean passed; // true if >= 41/50 (82%)

    // Belgian Standard
    private Integer passingScore; // 41 (Belgian passing threshold)

    // Time Statistics
    private Integer timeTakenSeconds;
    private Integer averageTimePerQuestion;

    // ========== PRODUCTION ENHANCEMENTS (v2.0) ==========

    // Duration
    private Integer durationMinutes; // How long exam took (calculated from timestamps)

    // Answer Statistics
    private Integer answeredCount; // Questions answered
    private Integer unansweredCount; // Questions skipped (should be 0 for completed exams)

    // Result Status
    private String resultStatus; // "PASSED" or "FAILED" (human-readable)

    // Passing Information
    private Integer passingThreshold; // 41 (duplicate of passingScore for clarity)
    private Integer pointsToPass; // How many more correct answers needed (0 if passed)

    // Weak Areas
    private List<String> weakCategories; // List of category names with <60% accuracy

    // Personalized Advice
    private String recommendedAction; // Personalized study recommendation

    // Detailed Breakdown
    private List<CategoryBreakdownDTO> categoryBreakdown;
    private List<IncorrectQuestionDTO> incorrectQuestions;
}
