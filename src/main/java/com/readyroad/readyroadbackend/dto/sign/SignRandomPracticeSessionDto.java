package com.readyroad.readyroadbackend.dto.sign;

import com.readyroad.readyroadbackend.domain.entity.SignRandomPracticeSession;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Stateful mixed-sign session DTO for /practice/random.
 */
public record SignRandomPracticeSessionDto(
        Long sessionId,
        String status,
        int totalQuestions,
        int passingScore,
        LocalDateTime startedAt,
        LocalDateTime expiresAt,
        List<SignQuizQuestionDto> questions) {

    public static SignRandomPracticeSessionDto from(
            SignRandomPracticeSession session,
            List<SignQuizQuestionDto> questions) {
        return new SignRandomPracticeSessionDto(
                session.getId(),
                session.getStatus().name(),
                session.getTotalQuestions(),
                session.getPassingScore(),
                session.getStartedAt(),
                session.getExpiresAt(),
                questions);
    }
}
