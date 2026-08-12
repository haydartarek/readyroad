package com.readyroad.readyroadbackend.marketing.task;

public class MarketingTaskExecutionException extends RuntimeException {

    private final String errorCode;

    public MarketingTaskExecutionException(String errorCode, String safeMessage) {
        super(safeMessage);
        if (errorCode == null || errorCode.isBlank()) {
            throw new IllegalArgumentException("errorCode is required");
        }
        this.errorCode = errorCode;
    }

    public String errorCode() {
        return errorCode;
    }

}
