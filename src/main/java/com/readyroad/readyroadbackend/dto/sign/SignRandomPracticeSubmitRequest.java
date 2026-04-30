package com.readyroad.readyroadbackend.dto.sign;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Submit request for the persisted mixed-sign random exam.
 */
public record SignRandomPracticeSubmitRequest(
        @NotNull Long sessionId,
        @Valid @NotNull List<SignRandomPracticeAnswerRequest> answers) {
}
