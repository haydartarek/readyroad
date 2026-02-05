package com.readyroad.readyroadbackend.dto.exam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for exam answer submission
 * Story A2: Submit Exam Answer
 *
 * Security Note: Does NOT reveal if answer is correct during exam
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitExamAnswerResponse {

    /**
     * The ID of the saved answer
     */
    private Long answerId;

    /**
     * The exam simulation ID
     */
    private Long examId;

    /**
     * The question ID
     */
    private Long questionId;

    /**
     * The selected option ID
     */
    private Long selectedOptionId;

    /**
     * Timestamp when answer was submitted
     */
    private Instant submittedAt;

    /**
     * Success message
     */
    private String message;

    /**
     * Total answers submitted so far in this exam
     */
    private Integer totalAnswered;

    /**
     * Total questions in exam (always 50 for Belgian exams)
     */
    private Integer totalQuestions;
}
