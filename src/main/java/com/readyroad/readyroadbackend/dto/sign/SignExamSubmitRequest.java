package com.readyroad.readyroadbackend.dto.sign;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Full exam submission — all answers in one request (stateless exam mode).
 */
public record SignExamSubmitRequest(
        @NotEmpty(message = "answers list must not be empty")
        @Valid
        List<SignExamAnswerItem> answers
) {}
