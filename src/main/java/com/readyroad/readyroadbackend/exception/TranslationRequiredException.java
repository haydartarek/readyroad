package com.readyroad.readyroadbackend.exception;

/**
 * Exception thrown when required translations are missing - Story D2
 *
 * Belgian law requires NL and FR translations for all published questions.
 */
public class TranslationRequiredException extends RuntimeException {

    public TranslationRequiredException(String message) {
        super(message);
    }

    public TranslationRequiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
