package com.readyroad.readyroadbackend.dto.exam;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for submitting an exam answer
 * Story A2: Submit Exam Answer
 *
 * Security Note: Only accepts option ID, does not reveal correctness
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitExamAnswerRequest {

    /**
     * The ID of the selected answer option
     */
    @NotNull(message = "Selected option ID is required")
    private Long selectedOptionId;

    /**
     * Time spent on the current question in seconds.
     * Optional for backwards compatibility with older clients.
     */
    @Min(value = 0, message = "Answer time cannot be negative")
    @Max(value = 1800, message = "Answer time is outside the supported range")
    private Integer timeTakenSeconds;
}
