package com.readyroad.readyroadbackend.marketing.content;

public class OpenAIContentGenerationException extends RuntimeException {
    private final String errorCode;

    public OpenAIContentGenerationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String errorCode() {
        return errorCode;
    }
}
