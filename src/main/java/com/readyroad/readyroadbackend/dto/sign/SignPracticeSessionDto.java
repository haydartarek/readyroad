package com.readyroad.readyroadbackend.dto.sign;

import com.readyroad.readyroadbackend.domain.entity.SignPracticeSession;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Returned when a practice session is started.
 * Contains the session ID (needed for answer submission) and
 * all shuffled questions for the sign.
 */
public record SignPracticeSessionDto(
        Long             sessionId,
        String           signCode,
        SignCategory     category,
        String           imagePath,
        String           nameNl,
        String           nameEn,
        String           nameFr,
        String           nameAr,
        String           status,
        int              totalQuestions,
        int              correctCount,
        LocalDateTime    startedAt,

        /** All questions for this session — presented to user in shuffled order. */
        List<SignQuizQuestionDto> questions
) {
    public static SignPracticeSessionDto from(SignPracticeSession s,
                                              List<SignQuizQuestionDto> questions) {
        return new SignPracticeSessionDto(
                s.getId(),
                s.getSignCode(),
                s.getSign().getCategory(),
                s.getSign().getImagePath(),
                s.getSign().getNameNl(),
                s.getSign().getNameEn(),
                s.getSign().getNameFr(),
                s.getSign().getNameAr(),
                s.getStatus().name(),
                s.getTotalQuestions(),
                s.getCorrectCount(),
                s.getStartedAt(),
                questions
        );
    }
}
