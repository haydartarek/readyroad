package com.readyroad.readyroadbackend.service;

/**
 * Single source of truth for theoretical-exam blueprint rules shared by
 * question allocation, bank-health reporting, and category management.
 */
public final class TheoryExamBlueprintPolicy {

    public static final int EXAM_SIZE = 50;
    public static final int MIN_ELIGIBLE_QUESTIONS_PER_CATEGORY = 5;
    public static final int DEFAULT_CATEGORY_WEIGHT = 10;

    private TheoryExamBlueprintPolicy() {
        // Utility class.
    }
}
