package com.readyroad.readyroadbackend.dto.sign;

/**
 * One answer in the random sign practice check request.
 * {@code selectedChoiceId} is {@code null} when the user timed out.
 */
public record SignRandomPracticeAnswerRequest(
        Long questionId,
        Long selectedChoiceId) {}
