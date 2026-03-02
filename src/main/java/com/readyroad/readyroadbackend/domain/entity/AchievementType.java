package com.readyroad.readyroadbackend.domain.entity;

/**
 * One-time achievement types that a user can earn.
 * Each type is awarded at most once per user (enforced by DB UNIQUE constraint).
 */
public enum AchievementType {

    /** User completed their very first exam (passed or failed). */
    FIRST_EXAM,

    /** User has passed 5 exams in total. */
    FIVE_EXAMS_PASSED,

    /** User has passed 10 exams in total. */
    TEN_EXAMS_PASSED,

    /** User scored a perfect 50/50 on an exam. */
    PERFECT_SCORE,

    /** User passed 3 exams in a row (consecutive completions that passed). */
    PASSING_STREAK_3
}
