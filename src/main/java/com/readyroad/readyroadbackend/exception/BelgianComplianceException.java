package com.readyroad.readyroadbackend.exception;

/**
 * Exception thrown when Belgian compliance rules are violated - Story D1
 *
 * Examples:
 * - Question has 4 options (should be 2-3)
 * - Question has 1 option (should be 2-3)
 * - Missing required translations (NL/FR)
 */
public class BelgianComplianceException extends RuntimeException {

    public BelgianComplianceException(String message) {
        super(message);
    }

    public BelgianComplianceException(String message, Throwable cause) {
        super(message, cause);
    }
}
