package com.readyroad.readyroadbackend.dto.sign;

import jakarta.validation.constraints.NotNull;

/**
 * One answer within an exam submission — maps a question ID to the chosen choice ID.
 */
public record SignExamAnswerItem(
        @NotNull(message = "questionId is required")
        Long questionId,

        @NotNull(message = "choiceId is required")
        Long choiceId
) {}
