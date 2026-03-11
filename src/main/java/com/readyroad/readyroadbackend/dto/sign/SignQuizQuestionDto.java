package com.readyroad.readyroadbackend.dto.sign;

import com.readyroad.readyroadbackend.domain.entity.SignQuestion;
import com.readyroad.readyroadbackend.domain.enums.SignDifficulty;
import com.readyroad.readyroadbackend.domain.enums.SignQuestionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A road-sign quiz question served to the user.
 * Choices are included WITHOUT the {@code isCorrect} flag.
 */
public record SignQuizQuestionDto(
        Long id,
        String questionRef,
        SignQuestionType questionType,
        SignDifficulty difficulty,
        boolean isCritical,
        boolean showSign,

        String questionNl,
        String questionEn,
        String questionFr,
        String questionAr,

        /** Sign code — useful for the client to display the sign image. */
        String signCode,
        /** Sign image path — directly embedded so the client has one object. */
        String signImagePath,

        List<SignChoiceDto> choices) {
    public static SignQuizQuestionDto from(SignQuestion q) {
        // Shuffle choices so the correct answer is not always in position 1.
        // Validation uses stable choice IDs (FK), never visual position.
        List<SignChoiceDto> choices = q.getChoices().stream()
                .map(SignChoiceDto::from)
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(choices);
        return new SignQuizQuestionDto(
                q.getId(),
                q.getQuestionRef(),
                q.getQuestionType(),
                q.getDifficulty(),
                Boolean.TRUE.equals(q.getIsCritical()),
                Boolean.TRUE.equals(q.getShowSign()),
                q.getQuestionNl(),
                q.getQuestionEn(),
                q.getQuestionFr(),
                q.getQuestionAr(),
                q.getSign() != null ? q.getSign().getSignCode() : null,
                q.getSign() != null ? q.getSign().getImagePath() : null,
                choices);
    }
}
