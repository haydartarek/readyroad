package com.readyroad.readyroadbackend.service;

/**
 * Single source of truth for theoretical-exam blueprint rules shared by
 * question allocation, bank-health reporting, and category management.
 */
public final class TheoryExamBlueprintPolicy {

    public static final int EXAM_SIZE = 50;
    public static final int MIN_ELIGIBLE_QUESTIONS_PER_CATEGORY = 5;
    public static final int DEFAULT_CATEGORY_WEIGHT = 10;

    public static int effectiveCategoryWeight(Integer configuredWeight) {
        return configuredWeight != null && configuredWeight > 0
                ? configuredWeight
                : DEFAULT_CATEGORY_WEIGHT;
    }

    /**
     * Current Admin-managed theory taxonomy uses stable numeric TH codes
     * (TH01, TH02, ..., TH09, ...). Older symbolic codes such as TH_PRI
     * are legacy taxonomy and must not be reactivated or edited.
     */
    public static boolean isManagedTheoryCategoryCode(String code) {
        return code != null && code.matches("TH\\d+");
    }

    private TheoryExamBlueprintPolicy() {
        // Utility class.
    }
}
