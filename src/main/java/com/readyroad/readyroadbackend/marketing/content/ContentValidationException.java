package com.readyroad.readyroadbackend.marketing.content;

public class ContentValidationException extends RuntimeException {
    private final String errorCode;

    public ContentValidationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String errorCode() {
        return errorCode;
    }
}
