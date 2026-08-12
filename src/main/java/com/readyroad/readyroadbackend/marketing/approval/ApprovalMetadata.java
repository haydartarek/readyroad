package com.readyroad.readyroadbackend.marketing.approval;

public record ApprovalMetadata(
        ApprovalMode approvalMode,
        String approvalSource,
        boolean approvalRequired) {

    public static final String MASTER_SPEC_V3 = "MASTER_SPEC_V3";

    public ApprovalMetadata {
        if (approvalMode == null) {
            throw new IllegalArgumentException("approvalMode is required");
        }
        if (approvalSource == null || approvalSource.isBlank()) {
            throw new IllegalArgumentException("approvalSource is required");
        }
    }

    public static ApprovalMetadata standingOwnerAuthorization() {
        return new ApprovalMetadata(ApprovalMode.STANDING_OWNER_AUTHORIZATION, MASTER_SPEC_V3, false);
    }

    public static ApprovalMetadata humanApproval(String source) {
        return new ApprovalMetadata(ApprovalMode.HUMAN_APPROVAL, source, true);
    }
}
