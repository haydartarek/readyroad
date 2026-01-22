package com.readyroad.readyroadbackend.exception;

/**
 * Exception thrown when a question is not found
 * Story A2: Submit Exam Answer
 */
public class QuestionNotFoundException extends RuntimeException {

    public QuestionNotFoundException(String message) {
        super(message);
    }

    public QuestionNotFoundException(Long questionId) {
        super(String.format("Question with ID %d not found", questionId));
    }

    public QuestionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
