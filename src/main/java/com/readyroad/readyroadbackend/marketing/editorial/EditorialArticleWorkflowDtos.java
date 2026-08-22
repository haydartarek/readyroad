package com.readyroad.readyroadbackend.marketing.editorial;

import java.time.Instant;
import java.util.Set;

final class EditorialArticleWorkflowDtos {

    private EditorialArticleWorkflowDtos() {}

    record TransitionRequest(
            long articleId,
            EditorialArticleState targetState,
            long taskId,
            String correlationId,
            String actor,
            String reason,
            Set<EditorialArticleQualityGate> passedQualityGates) {}

    record TransitionResult(
            long articleId,
            EditorialArticleState state,
            boolean changed,
            Instant updatedAt) {}
}
