package com.readyroad.readyroadbackend.exception;

/**
 * Exception thrown when an invalid answer is submitted
 * Story A2: Submit Exam Answer
 */
public class InvalidAnswerException extends RuntimeException {

    public InvalidAnswerException(String message) {
        super(message);
    }

    public InvalidAnswerException(String message, Throwable cause) {
        super(message, cause);
    }
}
