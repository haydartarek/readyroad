package com.readyroad.readyroadbackend.exception;

/**
 * Exception thrown when attempting to submit answer for an exam that is not active
 * Story A2: Submit Exam Answer
 */
public class ExamNotActiveException extends RuntimeException {

    public ExamNotActiveException(String message) {
        super(message);
    }

    public ExamNotActiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
