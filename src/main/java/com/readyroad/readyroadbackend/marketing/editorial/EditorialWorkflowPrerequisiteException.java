package com.readyroad.readyroadbackend.marketing.editorial;

public final class EditorialWorkflowPrerequisiteException extends RuntimeException {

    public static final String ERROR_CODE = "EDITORIAL_WORKFLOW_PREREQUISITE";
    private final String messageKey;
    private final String detail;

    public EditorialWorkflowPrerequisiteException(String message) {
        this(message, null, "");
    }

    public EditorialWorkflowPrerequisiteException(String message, String messageKey, String detail) {
        super(message);
        this.messageKey = messageKey;
        this.detail = detail;
    }

    public String messageKey() { return messageKey; }

    public String detail() { return detail; }
}
