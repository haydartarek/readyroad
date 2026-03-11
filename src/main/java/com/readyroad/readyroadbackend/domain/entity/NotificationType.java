package com.readyroad.readyroadbackend.domain.entity;

/**
 * Notification type categories.
 *
 * Used to:
 * - Filter notifications by category in the UI
 * - Choose the appropriate icon/colour in the frontend
 * - Drive business logic (e.g. "don't spam STUDY_REMINDER more than once a
 * day")
 */
public enum NotificationType {

    /** User completed an exam and scored ≥ 41/50 (PASSED) */
    EXAM_PASSED,

    /** User completed an exam and scored < 41/50 (FAILED) */
    EXAM_FAILED,

    /** Generic exam completion event (when pass/fail is not the focus) */
    EXAM_RESULT,

    /** User has achieved a study streak milestone (3, 7, 14, 30 days …) */
    STREAK_ACHIEVED,

    /** A weak area (< 60 % accuracy) has been detected or persists */
    WEAK_AREA,

    /** User unlocked an achievement/badge */
    ACHIEVEMENT,

    /** Periodic nudge to continue studying */
    STUDY_REMINDER,

    /** System-wide announcement (maintenance, new feature, etc.) */
    SYSTEM,

    /** Admin alert: a new user has registered on the platform */
    ADMIN_NEW_USER,

    /** Admin daily digest: summary of exam statistics */
    ADMIN_EXAM_STATS,

    /** Admin alert: manual platform-wide broadcast sent by an admin */
    ADMIN_PLATFORM_ALERT,

    /** User progress: a lesson has been fully read */
    LESSON_PROGRESS,

    /** Smart suggestion: recommended next learning step for the user */
    NEXT_STEP
}
