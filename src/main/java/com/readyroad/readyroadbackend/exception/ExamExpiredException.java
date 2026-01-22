package com.readyroad.readyroadbackend.exception;

/**
 * Exception thrown when attempting to interact with an expired exam
 * Story A4: Time Limit Enforcement
 */
public class ExamExpiredException extends RuntimeException {

    private final Long examId;

    public ExamExpiredException(String message, Long examId) {
        super(message);
        this.examId = examId;
    }

    public Long getExamId() {
        return examId;
    }
}
