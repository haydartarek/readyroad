package com.readyroad.readyroadbackend.dto.exam;

import jakarta.validation.constraints.NotNull;
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
}
