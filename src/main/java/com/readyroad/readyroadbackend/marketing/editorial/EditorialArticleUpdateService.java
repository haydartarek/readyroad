package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class EditorialArticleUpdateService {

    static final String AUDIT_EVENT = "EDITORIAL_ARTICLE_UPDATE_STARTED";

    private final EditorialArticleWorkflowStore workflowStore;
    private final EditorialArticleWorkflowStateMachine stateMachine;
    private final MarketingAuditService auditService;
    private final ObjectMapper objectMapper;

    @Transactional
    public StartResult start(long articleId, String actor) {
        if (articleId <= 0) {
            throw new IllegalArgumentException("articleId must be positive");
        }
        if (actor == null || actor.isBlank() || actor.trim().length() > 160) {
            throw new IllegalArgumentException("A valid editor actor is required");
        }

        var article = workflowStore.lock(articleId);
        EditorialArticleState initial = article.state();
        if (initial == EditorialArticleState.DRAFTING) {
            return new StartResult(articleId, EditorialArticleState.DRAFTING, false, article.updatedAt());
        }
        if (initial != EditorialArticleState.PUBLISHED
                && initial != EditorialArticleState.UPDATE_RECOMMENDED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only published articles can be reopened for an update");
        }

        if (initial == EditorialArticleState.PUBLISHED) {
            stateMachine.validate(initial, EditorialArticleState.UPDATE_RECOMMENDED);
            workflowStore.updateState(articleId, EditorialArticleState.UPDATE_RECOMMENDED);
        }
        stateMachine.validate(EditorialArticleState.UPDATE_RECOMMENDED, EditorialArticleState.DRAFTING);
        Instant updatedAt = workflowStore.updateState(articleId, EditorialArticleState.DRAFTING);

        ObjectNode details = objectMapper.createObjectNode();
        details.put("fromState", initial.name());
        details.put("toState", EditorialArticleState.DRAFTING.name());
        details.put("reason", "Admin started an article update session");
        auditService.recordEntityEvent(
                AUDIT_EVENT,
                actor.trim(),
                "EDITORIAL_ARTICLE",
                String.valueOf(articleId),
                null,
                "editorial-update-" + articleId + "-" + updatedAt.toEpochMilli(),
                details);

        return new StartResult(articleId, EditorialArticleState.DRAFTING, true, updatedAt);
    }

    public record StartResult(
            long articleId,
            EditorialArticleState state,
            boolean changed,
            Instant updatedAt
    ) {}
}
