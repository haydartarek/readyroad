package com.readyroad.readyroadbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when user attempts unauthorized action.
 *
 * Story A3: View Exam Results - Production Enhancement
 *
 * HTTP Status: 403 FORBIDDEN
 * Use Case: User tries to view another user's exam results
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(Long userId, Long examId) {
        super(String.format(
            "User %d is not authorized to view exam %d results",
            userId,
            examId
        ));
    }
}
