package com.readyroad.readyroadbackend.domain.repository.custom;

import java.time.LocalDateTime;

public interface UserQuestionHistoryUpsertRepository {

    void upsertQuestionPresented(
            Long userId,
            Long questionId,
            LocalDateTime presentedAt,
            String presentationContext);

    void upsertQuestionAnswered(
            Long userId,
            Long questionId,
            LocalDateTime answeredAt,
            boolean isCorrect,
            int timeTaken);
}
