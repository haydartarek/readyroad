package com.readyroad.readyroadbackend.domain.repository.custom;

import java.time.LocalDateTime;

public interface UserQuestionHistoryUpsertRepository {

    void upsertQuestionShown(
            Long userId,
            Long questionId,
            LocalDateTime lastShownAt,
            String lastShownType);

    void upsertQuestionAnswered(
            Long userId,
            Long questionId,
            LocalDateTime answeredAt,
            boolean isCorrect,
            int timeTaken);
}
