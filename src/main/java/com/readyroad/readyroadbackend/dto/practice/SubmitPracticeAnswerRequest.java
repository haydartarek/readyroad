package com.readyroad.readyroadbackend.dto.practice;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for submitting practice quiz answer
 * Story B1: Submit Practice Answer
 *
 * @author ReadyRoad Team
 * @since Phase 5 - Story B1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitPracticeAnswerRequest {

    @NotNull(message = "Selected option ID is required")
    private Long selectedOptionId;

    @Min(value = 1, message = "Time taken must be at least 1 second")
    @Max(value = 300, message = "Time taken must be less than 5 minutes (300 seconds)")
    private Integer timeTakenSeconds; // Optional - defaults to 0 if not provided
}
