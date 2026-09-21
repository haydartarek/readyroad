package com.readyroad.readyroadbackend.dto.exam;

/**
 * Current access state of a theory exam attempt.
 *
 * This is intentionally separate from ExamSimulation.ExamStatus.
 * Reaching the free preview limit does NOT complete or terminate the exam.
 */
public enum ExamAccessState {
    PREVIEW_ACTIVE,
    FREE_LIMIT_REACHED,
    FULL_ACTIVE
}