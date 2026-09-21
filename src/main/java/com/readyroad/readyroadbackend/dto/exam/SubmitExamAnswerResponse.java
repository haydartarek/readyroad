package com.readyroad.readyroadbackend.dto.exam;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for exam answer submission.
 *
 * Full paid exam:
 * correctness is NEVER exposed during the exam.
 *
 * Free preview:
 * correctness may be returned for questions 1..10 only.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitExamAnswerResponse {

    private Long answerId;

    private Long examId;

    private Long questionId;

    private Long selectedOptionId;

    private Instant submittedAt;

    private String message;

    private Integer totalAnswered;

    /**
     * Always 50 for the complete Belgian theory exam.
     */
    private Integer totalQuestions;

    /**
     * Present only for FREE preview answers.
     * Omitted for full paid exam answers.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean correct;

    /**
     * Present only for FREE preview answers.
     * Omitted for full paid exam answers.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long correctOptionId;

    /**
     * Allows the frontend to transition directly to the paywall
     * after question 10 without requesting question 11.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ExamAccessState accessState;
}