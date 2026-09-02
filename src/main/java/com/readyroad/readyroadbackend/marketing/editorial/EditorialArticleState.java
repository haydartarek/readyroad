package com.readyroad.readyroadbackend.marketing.editorial;

enum EditorialArticleState {
    IDEA,
    PLANNED,
    BRIEF_READY,
    DRAFTING,
    DRAFT_READY,
    FACT_CHECK_REQUIRED,
    LEGAL_REVIEW_REQUIRED,
    TRANSLATION_REQUIRED,
    IMAGE_REQUIRED,
    WAITING_APPROVAL,
    APPROVED,
    SCHEDULED,
    PUBLISHED,
    UPDATE_RECOMMENDED,
    ARCHIVED,
    REJECTED;

    boolean allowsDraftPreparation() {
        return switch (this) {
            case DRAFT_READY, FACT_CHECK_REQUIRED, LEGAL_REVIEW_REQUIRED,
                    TRANSLATION_REQUIRED, IMAGE_REQUIRED -> true;
            default -> false;
        };
    }
}
