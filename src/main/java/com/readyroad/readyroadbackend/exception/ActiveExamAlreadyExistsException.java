package com.readyroad.readyroadbackend.exception;

/**
 * Exception thrown when user attempts to start an exam while already having an active exam.
 * This should result in HTTP 409 Conflict response.
 */
public class ActiveExamAlreadyExistsException extends RuntimeException {
    private final Long activeExamId;
    private final Long userId;

    public ActiveExamAlreadyExistsException(Long userId, Long activeExamId) {
        super(String.format("User %d already has an active exam with ID: %d", userId, activeExamId));
        this.userId = userId;
        this.activeExamId = activeExamId;
    }

    public Long getActiveExamId() {
        return activeExamId;
    }

    public Long getUserId() {
        return userId;
    }
}
