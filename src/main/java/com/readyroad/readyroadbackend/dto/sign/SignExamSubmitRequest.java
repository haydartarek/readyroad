package com.readyroad.readyroadbackend.dto.sign;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Full exam submission — all answers in one request (stateless exam mode).
 * The answers field is required, but it may be an empty list when the user
 * submits without answering any question.
 */
public record SignExamSubmitRequest(
        @NotNull(message = "answers list is required")
        @Valid
        List<SignExamAnswerItem> answers
) {}
