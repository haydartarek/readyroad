package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.content.BlockedContentSourceException;
import com.readyroad.readyroadbackend.marketing.content.ContentLocale;
import com.readyroad.readyroadbackend.marketing.content.ContentSourceType;
import com.readyroad.readyroadbackend.marketing.content.VerifiedContentSource;
import com.readyroad.readyroadbackend.marketing.strategy.BlockedStrategyContextException;
import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContext;
import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContextRequest;
import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContextService;
import com.readyroad.readyroadbackend.marketing.task.ClaimedTask;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class EditorialDraftPersistenceService {

    private static final Set<String> OFFICIAL_LEGAL_SOURCE_TYPES = Set.of(
            "OFFICIAL_LEGAL_SOURCE",
            "OFFICIAL_GOVERNMENT_SOURCE",
            "OFFICIAL_PUBLIC_AUTHORITY_SOURCE");

    private final EditorialDraftStore store;
    private final MarketingStrategyContextService strategyService;
    private final EditorialArticleWorkflowService workflowService;
    private final EditorialArticleVersionService versionService;
    private final MarketingAuditService auditService;
    private final MarketingProperties properties;
    private final ObjectMapper objectMapper;

    @Transactional
    Preparation prepare(ClaimedTask task) {
        long articleId = articleId(task);
        if (store.versionCreatedByTask(task.taskId()).isPresent()) {
            return Preparation.completed(articleId);
        }
        EditorialDraftStore.DraftContext context = store.lockContext(articleId);
        requireStrategy(context);
        MarketingStrategyContextRequest strategyRequest = new MarketingStrategyContextRequest(
                context.uspId(), context.icpId(), context.pillarId(), context.funnelId(), context.goalId());
        MarketingStrategyContext strategy = strategyService.resolve(strategyRequest);

        EditorialDraftStore.ClaimSummary summary = store.claimSummary(context.topicId(), context.language());
        if (summary.total() == 0) {
            throw new BlockedContentSourceException(
                    "No registered claims exist for the approved brief language");
        }
        if (summary.supported() != summary.total()) {
            throw new BlockedContentSourceException(
                    "Every registered claim must be supported before article draft creation");
        }
        List<EditorialDraftStore.EvidenceRow> evidence = store.verifiedEvidence(
                context.topicId(), context.language());
        Map<Long, List<EditorialDraftStore.EvidenceRow>> byClaim = new LinkedHashMap<>();
        evidence.forEach(row -> byClaim.computeIfAbsent(row.claimId(), ignored -> new ArrayList<>()).add(row));
        if (byClaim.size() != summary.total()) {
            throw new BlockedContentSourceException(
                    "Every supported claim must retain at least one current verified source");
        }
        requireLegalEvidence(byClaim);

        String facts = evidencePackage(context, byClaim);
        if (facts.length() > properties.getContent().getMaxSourceCharacters()) {
            throw new BlockedContentSourceException(
                    "Verified article evidence exceeds the configured source package limit");
        }
        String fingerprint = sha256(facts);
        String sourceReference = "ARTICLE_BRIEF:" + context.briefId() + ":" + fingerprint;
        ContentLocale locale = ContentLocale.valueOf(context.language());
        Map<ContentLocale, VerifiedContentSource.LocalizedFacts> localized = new EnumMap<>(ContentLocale.class);
        localized.put(locale, new VerifiedContentSource.LocalizedFacts(context.workingTitle(), facts));
        VerifiedContentSource source = new VerifiedContentSource(
                ContentSourceType.EDITORIAL_BRIEF,
                String.valueOf(context.briefId()),
                sourceReference,
                fingerprint,
                localized,
                strategyRequest);

        if ("BRIEF_READY".equals(context.state())) {
            workflowService.transition(new EditorialArticleWorkflowDtos.TransitionRequest(
                    articleId,
                    EditorialArticleState.DRAFTING,
                    task.taskId(),
                    task.correlationId(),
                    "EDITORIAL_WORKER",
                    "Verified brief and source package accepted for draft creation",
                    Set.of()));
        } else if (!"DRAFTING".equals(context.state())) {
            throw new IllegalStateException(
                    "Article is not eligible for draft creation in state " + context.state());
        }
        return new Preparation(context, strategy, source, false);
    }

    @Transactional
    long persist(
            ClaimedTask task,
            Preparation preparation,
            EditorialDraftQualityPolicy.ValidatedDraft draft) {
        var existing = store.versionCreatedByTask(task.taskId());
        if (existing.isPresent()) {
            return existing.get();
        }
        EditorialDraftStore.DraftContext current = store.lockContext(preparation.context().articleId());
        if (!"DRAFTING".equals(current.state())) {
            throw new IllegalStateException("Article is no longer in DRAFTING state");
        }
        if (store.duplicateFingerprint(current.articleId(), draft.fingerprint())) {
            throw new com.readyroad.readyroadbackend.marketing.content.ContentValidationException(
                    "DUPLICATE_CONTENT", "Generated article duplicates an existing editorial draft");
        }

        ObjectNode metadata = EditorialArticleMetadata.withSeoMetadata(
                objectMapper.createObjectNode(), draft.title(), draft.summary());
        metadata = EditorialArticleMetadata.withInternalLinks(metadata, List.of());
        metadata.put("primaryCta", draft.cta());
        metadata.put("focusKeyword", current.focusKeyword());
        ObjectNode generation = objectMapper.createObjectNode()
                .put("provider", "OPENAI")
                .put("model", draft.model())
                .put("inputTokens", draft.inputTokens())
                .put("outputTokens", draft.outputTokens())
                .put("requestOutcome", draft.requestOutcome())
                .put("wordCount", draft.wordCount())
                .put("contentFingerprint", draft.fingerprint())
                .put("sourceReference", preparation.source().sourceReference())
                .put("taskId", task.taskId())
                .put("briefId", current.briefId())
                .put("uspId", current.uspId())
                .put("icpId", current.icpId())
                .put("contentPillarId", current.pillarId())
                .put("funnelStageId", current.funnelId())
                .put("conversionGoalId", current.goalId());
        var version = versionService.append(new EditorialArticleVersionDtos.AppendRequest(
                current.articleId(), current.language(), draft.title(),
                seoSlug(current.focusKeyword()), draft.summary(),
                draft.body(), metadata, generation, "DRAFT_READY"), "EDITORIAL_WORKER");
        store.bindGeneratedTask(version.id(), task.taskId());
        workflowService.transition(new EditorialArticleWorkflowDtos.TransitionRequest(
                current.articleId(),
                EditorialArticleState.DRAFT_READY,
                task.taskId(),
                task.correlationId(),
                "EDITORIAL_WORKER",
                "Source-grounded canonical article draft created",
                Set.of()));
        auditService.recordEntityEvent(
                "EDITORIAL_ARTICLE_DRAFT_CREATED",
                "EDITORIAL_WORKER",
                "EDITORIAL_ARTICLE",
                String.valueOf(current.articleId()),
                task.taskId(),
                task.correlationId(),
                objectMapper.createObjectNode()
                        .put("versionId", version.id())
                        .put("briefId", current.briefId())
                        .put("language", current.language())
                        .put("wordCount", draft.wordCount())
                        .put("legalReviewRequired", current.legalReviewRequired()));
        return version.id();
    }

    private static void requireStrategy(EditorialDraftStore.DraftContext context) {
        if (context.uspId() == null || context.icpId() == null || context.pillarId() == null
                || context.funnelId() == null || context.goalId() == null) {
            throw new BlockedStrategyContextException("EDITORIAL_ARTICLE");
        }
    }

    private static void requireLegalEvidence(
            Map<Long, List<EditorialDraftStore.EvidenceRow>> byClaim) {
        for (List<EditorialDraftStore.EvidenceRow> sources : byClaim.values()) {
            EditorialDraftStore.EvidenceRow claim = sources.getFirst();
            if (!"LEGAL".equals(claim.claimType())) {
                continue;
            }
            boolean verifiedOfficialLegalSource = sources.stream().anyMatch(source ->
                    OFFICIAL_LEGAL_SOURCE_TYPES.contains(source.sourceType())
                            && "VERIFIED".equals(source.legalReviewStatus()));
            if (!verifiedOfficialLegalSource) {
                throw new BlockedContentSourceException(
                        "Legal claims require a verified official legal or public-authority source");
            }
        }
    }

    private static String evidencePackage(
            EditorialDraftStore.DraftContext context,
            Map<Long, List<EditorialDraftStore.EvidenceRow>> byClaim) {
        StringBuilder value = new StringBuilder()
                .append("BRIEF TITLE: ").append(context.workingTitle()).append('\n')
                .append("PURPOSE: ").append(context.purpose()).append('\n')
                .append("SEARCH INTENT: ").append(context.searchIntent()).append('\n')
                .append("VERIFIED CLAIMS:\n");
        for (List<EditorialDraftStore.EvidenceRow> rows : byClaim.values()) {
            EditorialDraftStore.EvidenceRow claim = rows.getFirst();
            value.append("- [").append(claim.claimType()).append("] ")
                    .append(claim.claimText()).append("\n  Sources:");
            rows.forEach(source -> value.append(" ")
                    .append(source.sourceTitle()).append(" (")
                    .append(source.sourceReference()).append(");"));
            value.append('\n');
        }
        return value.toString().trim();
    }

    private static String seoSlug(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Approved article brief has no focus keyword");
        }

        String slug = Normalizer.normalize(
                        value.trim().toLowerCase(Locale.ROOT),
                        Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "")
                .replaceAll("[^\\p{L}\\p{Nd}]+", "-")
                .replaceAll("^-+|-+$", "");

        if (slug.isBlank()) {
            throw new IllegalStateException("Approved article focus keyword cannot produce a slug");
        }

        return slug;
    }

    private static long articleId(ClaimedTask task) {
        long articleId = task.payload().path("articleId").asLong(0);
        if (articleId <= 0) {
            throw new IllegalArgumentException("Editorial draft task has no valid articleId");
        }
        return articleId;
    }

    private static String sha256(String value) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException("SHA-256 is unavailable", impossible);
        }
    }

    record Preparation(
            EditorialDraftStore.DraftContext context,
            MarketingStrategyContext strategy,
            VerifiedContentSource source,
            boolean completed) {

        static Preparation completed(long articleId) {
            return new Preparation(null, null, null, true);
        }
    }
}
