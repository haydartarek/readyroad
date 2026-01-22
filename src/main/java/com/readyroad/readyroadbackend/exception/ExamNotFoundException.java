package com.readyroad.readyroadbackend.exception;

/**
 * Exception thrown when an exam is not found
 * Story A2: Submit Exam Answer
 */
public class ExamNotFoundException extends RuntimeException {

    public ExamNotFoundException(String message) {
        super(message);
    }

    public ExamNotFoundException(Long examId) {
        super(String.format("Exam with ID %d not found", examId));
    }

    public ExamNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
