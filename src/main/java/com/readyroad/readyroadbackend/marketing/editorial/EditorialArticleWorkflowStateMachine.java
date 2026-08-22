package com.readyroad.readyroadbackend.marketing.editorial;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
class EditorialArticleWorkflowStateMachine {

    private static final Map<EditorialArticleState, EnumSet<EditorialArticleState>> TRANSITIONS = transitions();

    void validate(EditorialArticleState from, EditorialArticleState to) {
        if (!TRANSITIONS.getOrDefault(from, EnumSet.noneOf(EditorialArticleState.class)).contains(to)) {
            throw new InvalidEditorialArticleStateTransitionException(from, to);
        }
    }

    void validateLegalBranch(
            EditorialArticleState from,
            EditorialArticleState to,
            boolean legalReviewRequired) {
        EditorialArticleState required = legalReviewRequired
                ? EditorialArticleState.LEGAL_REVIEW_REQUIRED
                : EditorialArticleState.TRANSLATION_REQUIRED;
        if (to != required) {
            throw new InvalidEditorialArticleStateTransitionException(from, to, required);
        }
    }

    private static Map<EditorialArticleState, EnumSet<EditorialArticleState>> transitions() {
        Map<EditorialArticleState, EnumSet<EditorialArticleState>> transitions =
                new EnumMap<>(EditorialArticleState.class);
        transitions.put(EditorialArticleState.IDEA,
                EnumSet.of(EditorialArticleState.PLANNED, EditorialArticleState.REJECTED));
        transitions.put(EditorialArticleState.PLANNED,
                EnumSet.of(EditorialArticleState.BRIEF_READY, EditorialArticleState.REJECTED));
        transitions.put(EditorialArticleState.BRIEF_READY,
                EnumSet.of(EditorialArticleState.DRAFTING, EditorialArticleState.REJECTED));
        transitions.put(EditorialArticleState.DRAFTING,
                EnumSet.of(EditorialArticleState.DRAFT_READY, EditorialArticleState.REJECTED));
        transitions.put(EditorialArticleState.DRAFT_READY,
                EnumSet.of(EditorialArticleState.FACT_CHECK_REQUIRED, EditorialArticleState.REJECTED));
        transitions.put(EditorialArticleState.FACT_CHECK_REQUIRED,
                EnumSet.of(
                        EditorialArticleState.LEGAL_REVIEW_REQUIRED,
                        EditorialArticleState.TRANSLATION_REQUIRED,
                        EditorialArticleState.REJECTED));
        transitions.put(EditorialArticleState.LEGAL_REVIEW_REQUIRED,
                EnumSet.of(EditorialArticleState.TRANSLATION_REQUIRED, EditorialArticleState.REJECTED));
        transitions.put(EditorialArticleState.TRANSLATION_REQUIRED,
                EnumSet.of(EditorialArticleState.IMAGE_REQUIRED, EditorialArticleState.REJECTED));
        transitions.put(EditorialArticleState.IMAGE_REQUIRED,
                EnumSet.of(EditorialArticleState.WAITING_APPROVAL, EditorialArticleState.REJECTED));
        transitions.put(EditorialArticleState.WAITING_APPROVAL,
                EnumSet.of(EditorialArticleState.APPROVED, EditorialArticleState.REJECTED));
        transitions.put(EditorialArticleState.APPROVED, EnumSet.of(EditorialArticleState.SCHEDULED));
        transitions.put(EditorialArticleState.SCHEDULED, EnumSet.of(EditorialArticleState.PUBLISHED));
        transitions.put(EditorialArticleState.PUBLISHED,
                EnumSet.of(EditorialArticleState.UPDATE_RECOMMENDED));
        transitions.put(EditorialArticleState.UPDATE_RECOMMENDED,
                EnumSet.of(EditorialArticleState.DRAFTING, EditorialArticleState.ARCHIVED));
        return Map.copyOf(transitions);
    }
}
