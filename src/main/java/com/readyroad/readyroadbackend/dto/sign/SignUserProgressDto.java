package com.readyroad.readyroadbackend.dto.sign;

import com.readyroad.readyroadbackend.domain.enums.SignCategory;

/**
 * Per-sign progress snapshot for the authenticated user.
 * Returned by:
 * GET /api/sign-quiz/signs/{signCode}/status
 * GET /api/sign-quiz/user-progress (as a list)
 */
public record SignUserProgressDto(
                Long signId,
                String signCode,
                String routeCode,
                SignCategory category,
                String imagePath,
                String nameNl,
                String nameEn,
                String nameFr,
                String nameAr,

                /** Whether the user has ever started a practice session for this sign. */
                boolean practiceStarted,
                /**
                 * Whether the user has completed (reached COMPLETED status) a practice session.
                 */
                boolean practiceCompleted,
                /** Best practice score percentage (0–100), null if never practiced. */
                Double practiceBestScorePct,

                /** Whether exam 1 has ever been attempted. */
                boolean exam1Attempted,
                /** Whether exam 1 has been passed (score ≥ passing threshold). */
                boolean exam1Passed,
                /** Best exam 1 score percentage, null if never attempted. */
                Double exam1BestScorePct,
                /** Number of exam 1 attempts. */
                int exam1Attempts,
                /** Configured number of questions for exam 1, null if no active exam exists. */
                Integer exam1TotalQuestions,
                /** Configured passing score for exam 1, null if no active exam exists. */
                Integer exam1PassingScore) {
}
