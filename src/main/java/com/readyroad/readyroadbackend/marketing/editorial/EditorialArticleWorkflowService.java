package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class EditorialArticleWorkflowService {

    static final String AUDIT_EVENT = "EDITORIAL_ARTICLE_STATE_CHANGED";
    private static final Set<EditorialArticleQualityGate> REQUIRED_QUALITY_GATES =
            Set.copyOf(EnumSet.allOf(EditorialArticleQualityGate.class));

    private final EditorialArticleWorkflowStore store;
    private final EditorialArticleWorkflowStateMachine stateMachine;
    private final MarketingAuditService auditService;
    private final ObjectMapper objectMapper;

    @Transactional
    public EditorialArticleWorkflowDtos.TransitionResult transition(
            EditorialArticleWorkflowDtos.TransitionRequest request) {
        validate(request);
        Set<EditorialArticleQualityGate> qualityGates = request.passedQualityGates() == null
                ? Set.of()
                : Set.copyOf(request.passedQualityGates());
        var article = store.lock(request.articleId());
        if (!store.matchesEditorialTask(
                request.taskId(), request.correlationId().trim(), article.id())) {
            throw new IllegalArgumentException("Task context does not match the editorial article transition");
        }
        if (article.state() == request.targetState()) {
            return new EditorialArticleWorkflowDtos.TransitionResult(
                    article.id(), article.state(), false, article.updatedAt());
        }

        stateMachine.validate(article.state(), request.targetState());
        validatePrerequisites(article, request.targetState(), qualityGates);
        var updatedAt = store.updateState(article.id(), request.targetState());
        auditService.recordEntityEvent(
                AUDIT_EVENT,
                request.actor().trim(),
                "EDITORIAL_ARTICLE",
                String.valueOf(article.id()),
                request.taskId(),
                request.correlationId().trim(),
                details(article, request, qualityGates));
        return new EditorialArticleWorkflowDtos.TransitionResult(
                article.id(), request.targetState(), true, updatedAt);
    }

    private void validatePrerequisites(
            EditorialArticleWorkflowStore.LockedArticle article,
            EditorialArticleState target,
            Set<EditorialArticleQualityGate> qualityGates) {
        if (target == EditorialArticleState.BRIEF_READY && !store.hasApprovedBrief(article.topicId())) {
            throw new IllegalStateException("An approved brief is required before BRIEF_READY");
        }
        if (article.state() == EditorialArticleState.FACT_CHECK_REQUIRED) {
            stateMachine.validateLegalBranch(
                    article.state(), target, store.approvedBriefRequiresLegalReview(article.topicId()));
        }
        if (target == EditorialArticleState.DRAFT_READY
                && !store.hasFreshCurrentCanonicalDraft(
                        article.id(), article.canonicalLanguage(), article.updatedAt())) {
            throw new IllegalStateException(
                    "A fresh canonical current version is required before DRAFT_READY");
        }
        if (target == EditorialArticleState.IMAGE_REQUIRED
                && !store.hasAllRequiredCurrentLanguages(article.id())) {
            throw new IllegalStateException(
                    "Current AR, NL, FR and EN article versions are required before IMAGE_REQUIRED");
        }
        if (target == EditorialArticleState.WAITING_APPROVAL
                && !qualityGates.containsAll(REQUIRED_QUALITY_GATES)) {
            throw new IllegalStateException("All editorial quality gates must pass before WAITING_APPROVAL");
        }
    }

    private ObjectNode details(
            EditorialArticleWorkflowStore.LockedArticle article,
            EditorialArticleWorkflowDtos.TransitionRequest request,
            Set<EditorialArticleQualityGate> qualityGates) {
        ObjectNode details = objectMapper.createObjectNode();
        details.put("fromState", article.state().name());
        details.put("toState", request.targetState().name());
        details.put("reason", request.reason().trim());
        ArrayNode gates = details.putArray("passedQualityGates");
        qualityGates.stream()
                .sorted(Comparator.comparing(Enum::name))
                .map(Enum::name)
                .forEach(gates::add);
        return details;
    }

    private static void validate(EditorialArticleWorkflowDtos.TransitionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Transition request is required");
        }
        if (request.articleId() <= 0) {
            throw new IllegalArgumentException("articleId must be positive");
        }
        if (request.targetState() == null) {
            throw new IllegalArgumentException("targetState is required");
        }
        if (request.taskId() <= 0) {
            throw new IllegalArgumentException("taskId must be positive");
        }
        if (request.correlationId() == null || request.correlationId().isBlank()) {
            throw new IllegalArgumentException("correlationId is required");
        }
        if (request.actor() == null || request.actor().isBlank()) {
            throw new IllegalArgumentException("actor is required");
        }
        if (request.actor().trim().length() > 160) {
            throw new IllegalArgumentException("actor must not exceed 160 characters");
        }
        if (request.reason() == null || request.reason().isBlank()) {
            throw new IllegalArgumentException("reason is required");
        }
    }
}
