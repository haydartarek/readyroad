package com.readyroad.readyroadbackend.marketing.editorial;

import java.util.Arrays;
import java.util.Optional;

public enum EditorialTaskType {
    ARTICLE_OPPORTUNITY_DISCOVERY(Coverage.EXECUTABLE_HANDLER,
            "ARTICLE_OPPORTUNITY_DISCOVERY", "EditorialOpportunityTaskHandler"),
    ARTICLE_KEYWORD_CLUSTERING(Coverage.DATA_MODEL,
            "ARTICLE_KEYWORD_CLUSTERING", "article_keyword_clusters and official backlog clusters"),
    ARTICLE_BRIEF_CREATE(Coverage.EXECUTABLE_HANDLER,
            "ARTICLE_BRIEF_CREATE", "EditorialBriefTaskHandler"),
    ARTICLE_SOURCE_COLLECT(Coverage.EXECUTABLE_HANDLER,
            "ARTICLE_SOURCE_COLLECT", "EditorialSourceCollectionTaskHandler"),
    ARTICLE_DRAFT_CREATE(Coverage.EXECUTABLE_HANDLER,
            "ARTICLE_DRAFT_CREATE", "EditorialDraftTaskHandler"),
    ARTICLE_FACT_CHECK(Coverage.WORKFLOW_GATE,
            "ARTICLE_FACT_CHECK", "FACT_CHECK_REQUIRED and SOURCE_VERIFICATION quality gate"),
    ARTICLE_LEGAL_REVIEW(Coverage.WORKFLOW_GATE,
            "ARTICLE_LEGAL_REVIEW", "LEGAL_REVIEW_REQUIRED and verified legal sources"),
    ARTICLE_TRANSLATION_ADAPT(Coverage.WORKFLOW_GATE,
            "ARTICLE_TRANSLATION_ADAPT", "TRANSLATION_REQUIRED and localized immutable versions"),
    ARTICLE_DUPLICATE_CHECK(Coverage.QUALITY_GATE,
            "ARTICLE_DUPLICATE_CHECK", "DUPLICATE_CHECK quality gate"),
    ARTICLE_CANNIBALIZATION_CHECK(Coverage.QUALITY_GATE,
            "ARTICLE_CANNIBALIZATION_CHECK", "CANNIBALIZATION_CHECK quality gate"),
    ARTICLE_INTERNAL_LINK_PLAN(Coverage.POLICY,
            "ARTICLE_INTERNAL_LINK_PLAN", "EditorialInternalLinkPolicy"),
    ARTICLE_WAITING_APPROVAL(Coverage.LEGACY_RUNTIME_ALIAS,
            "ARTICLE_APPROVAL", "EditorialArticleApprovalTaskHandler"),
    ARTICLE_PUBLISH(Coverage.EXECUTABLE_HANDLER,
            "ARTICLE_PUBLISH", "EditorialArticlePublicationTaskHandler"),
    ARTICLE_UPDATE(Coverage.WORKFLOW_GATE,
            "ARTICLE_UPDATE", "UPDATE_RECOMMENDED to DRAFTING workflow"),
    ARTICLE_PERFORMANCE_SNAPSHOT(Coverage.EXECUTABLE_HANDLER,
            "ARTICLE_PERFORMANCE_SNAPSHOT", "EditorialPerformanceTaskHandler"),
    ARTICLE_REFRESH_RECOMMENDATION(Coverage.EXECUTABLE_HANDLER,
            "ARTICLE_REFRESH_RECOMMENDATION", "EditorialPerformanceTaskHandler"),
    ARTICLE_ARCHIVE_RECOMMENDATION(Coverage.WORKFLOW_GATE,
            "ARTICLE_ARCHIVE_RECOMMENDATION", "UPDATE_RECOMMENDED to ARCHIVED workflow");

    private final Coverage coverage;
    private final String runtimeTaskType;
    private final String evidenceComponent;

    EditorialTaskType(Coverage coverage, String runtimeTaskType, String evidenceComponent) {
        this.coverage = coverage;
        this.runtimeTaskType = runtimeTaskType;
        this.evidenceComponent = evidenceComponent;
    }

    public Coverage coverage() {
        return coverage;
    }

    public String runtimeTaskType() {
        return runtimeTaskType;
    }

    public String evidenceComponent() {
        return evidenceComponent;
    }

    public static Optional<EditorialTaskType> fromRuntimeTaskType(String taskType) {
        return Arrays.stream(values())
                .filter(value -> value.runtimeTaskType.equals(taskType))
                .findFirst();
    }

    public enum Coverage {
        EXECUTABLE_HANDLER,
        LEGACY_RUNTIME_ALIAS,
        WORKFLOW_GATE,
        QUALITY_GATE,
        POLICY,
        DATA_MODEL
    }
}
