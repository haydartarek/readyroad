package com.readyroad.readyroadbackend.dto.sign;

import com.readyroad.readyroadbackend.domain.enums.SignCategory;

/**
 * Per-sign progress snapshot for the authenticated user.
 * Returned by:
 *   GET /api/sign-quiz/signs/{signCode}/status
 *   GET /api/sign-quiz/user-progress  (as a list)
 */
public record SignUserProgressDto(
        Long         signId,
        String       signCode,
        SignCategory category,
        String       imagePath,
        String       nameNl,
        String       nameEn,
        String       nameFr,
        String       nameAr,

        /** Whether the user has ever started a practice session for this sign. */
        boolean practiceStarted,
        /** Whether the user has completed (reached COMPLETED status) a practice session. */
        boolean practiceCompleted,
        /** Best practice score percentage (0–100), null if never practiced. */
        Double  practiceBestScorePct,

        /** Whether exam 1 has ever been attempted. */
        boolean exam1Attempted,
        /** Whether exam 1 has been passed (score ≥ passing threshold). */
        boolean exam1Passed,
        /** Best exam 1 score percentage, null if never attempted. */
        Double  exam1BestScorePct,
        /** Number of exam 1 attempts. */
        int     exam1Attempts,

        /** Whether exam 2 is unlocked (requires exam1Passed == true). */
        boolean exam2Unlocked,
        /** Whether exam 2 has ever been attempted. */
        boolean exam2Attempted,
        /** Whether exam 2 has been passed. */
        boolean exam2Passed,
        /** Best exam 2 score percentage, null if never attempted. */
        Double  exam2BestScorePct,
        /** Number of exam 2 attempts. */
        int     exam2Attempts
) {}
