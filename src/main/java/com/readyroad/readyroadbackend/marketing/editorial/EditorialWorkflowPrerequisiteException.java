package com.readyroad.readyroadbackend.marketing.editorial;

public final class EditorialWorkflowPrerequisiteException extends IllegalStateException {

    public static final String ERROR_CODE = "EDITORIAL_WORKFLOW_PREREQUISITE";

    public EditorialWorkflowPrerequisiteException(String message) {
        super(message);
    }
}
