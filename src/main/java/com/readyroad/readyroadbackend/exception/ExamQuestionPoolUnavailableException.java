package com.readyroad.readyroadbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ExamQuestionPoolUnavailableException extends ResponseStatusException {

    private final int requiredQuestions;
    private final int eligibleCapacity;

    public ExamQuestionPoolUnavailableException(
            String message,
            int requiredQuestions,
            int eligibleCapacity) {
        super(HttpStatus.CONFLICT, message);
        this.requiredQuestions = requiredQuestions;
        this.eligibleCapacity = eligibleCapacity;
    }

    public int getRequiredQuestions() {
        return requiredQuestions;
    }

    public int getEligibleCapacity() {
        return eligibleCapacity;
    }
}
