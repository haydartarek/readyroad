package com.readyroad.readyroadbackend.dto.sign;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for submitting one practice answer.
 */
public record SignPracticeAnswerRequest(

        @NotNull(message = "choiceId is required")
        Long choiceId,

        /** Optional — how long the user spent on this question (seconds). */
        @Min(1) @Max(600)
        Integer timeTakenSeconds
) {}
