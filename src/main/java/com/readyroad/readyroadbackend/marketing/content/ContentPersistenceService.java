package com.readyroad.readyroadbackend.marketing.content;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.marketing.content.ContentQualityValidator.ValidatedContent;
import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentPersistenceService {

    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Optional<ContentPackageResult> existing(
            VerifiedContentSource source,
            MarketingStrategyContext strategy) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
                SELECT parent_item_id, count(*) AS variant_count
                FROM content_items
                WHERE item_type = 'CONTENT_VARIANT'
                  AND source_type = ? AND source_id = ? AND source_hash = ?
                  AND usp_id = ? AND icp_id = ? AND content_pillar_id = ?
                  AND funnel_stage_id = ? AND conversion_goal_id = ?
                  AND strategy_context->>'positioningId' = ?
                GROUP BY parent_item_id
                HAVING count(*) = 4
                """, source.type().name(), source.id(), source.sourceHash(),
                strategy.usp().id(), strategy.icp().id(), strategy.contentPillar().id(),
                strategy.funnelStage().id(), strategy.conversionGoal().id(),
                String.valueOf(strategy.positioning().id()));
        if (rows.isEmpty()) {
            return Optional.empty();
        }
        Number parentId = (Number) rows.getFirst().get("parent_item_id");
        Number variants = (Number) rows.getFirst().get("variant_count");
        return Optional.of(new ContentPackageResult(parentId.longValue(), variants.intValue(), true));
    }

    @Transactional
    public ContentPackageResult persist(
            VerifiedContentSource source,
            MarketingStrategyContext strategy,
            List<ValidatedContent> variants,
            Long taskId) {
        if (variants.size() != ContentLocale.SUPPORTED.size()) {
            throw new ContentValidationException("MALFORMED_STRUCTURED_OUTPUT", "All four language variants are required");
        }
        Optional<ContentPackageResult> existing = existing(source, strategy);
        if (existing.isPresent()) {
            return existing.get();
        }
        for (ValidatedContent variant : variants) {
            Integer duplicates = jdbc.queryForObject("""
                    SELECT count(*) FROM content_items
                    WHERE item_type = 'CONTENT_VARIANT' AND language = ? AND content_fingerprint = ?
                    """, Integer.class, variant.locale().name(), variant.fingerprint());
            if (duplicates != null && duplicates > 0) {
                throw new ContentValidationException("DUPLICATE_CONTENT", "Generated content duplicates an existing draft");
            }
        }

        String packageKey = packageKey(source, strategy);
        ValidatedContent primary = variants.stream()
                .filter(variant -> variant.locale() == ContentLocale.EN)
                .findFirst()
                .orElse(variants.getFirst());
        List<Long> inserted = jdbc.queryForList("""
                INSERT INTO content_items (
                    item_key, item_type, source_type, source_id, language, status, title, body,
                    usp_id, icp_id, content_pillar_id, funnel_stage_id, conversion_goal_id,
                    primary_cta, strategy_context, metadata, task_id, source_hash)
                VALUES (?, 'CONTENT_PACKAGE', ?, ?, 'UNKNOWN', 'DRAFT', ?, ?, ?, ?, ?, ?, ?, ?,
                        ?::jsonb, ?::jsonb, ?, ?)
                ON CONFLICT (item_key) DO NOTHING RETURNING id
                """, Long.class,
                packageKey, source.type().name(), source.id(), primary.title(), primary.summary(),
                strategy.usp().id(), strategy.icp().id(), strategy.contentPillar().id(),
                strategy.funnelStage().id(), strategy.conversionGoal().id(),
                strategy.conversionGoal().primaryCta(), strategyJson(strategy),
                json(Map.of(
                        "provider", "OPENAI",
                        "api", "RESPONSES",
                        "sourceReference", source.sourceReference(),
                        "languages", ContentLocale.SUPPORTED.stream().map(Enum::name).toList())),
                taskId, source.sourceHash());
        Long packageId = inserted.isEmpty()
                ? jdbc.queryForObject("SELECT id FROM content_items WHERE item_key = ?", Long.class, packageKey)
                : inserted.getFirst();
        if (inserted.isEmpty()) {
            Integer existingVariants = jdbc.queryForObject("""
                    SELECT count(*) FROM content_items
                    WHERE parent_item_id = ? AND item_type = 'CONTENT_VARIANT'
                    """, Integer.class, packageId);
            if (existingVariants != null && existingVariants == ContentLocale.SUPPORTED.size()) {
                return new ContentPackageResult(packageId, existingVariants, true);
            }
        }
        int created = 0;
        for (ValidatedContent variant : variants) {
            int rows = jdbc.update("""
                    INSERT INTO content_items (
                        item_key, item_type, source_type, source_id, parent_item_id, language, status,
                        title, body, usp_id, icp_id, content_pillar_id, funnel_stage_id,
                        conversion_goal_id, primary_cta, strategy_context, metadata, task_id,
                        source_hash, content_fingerprint)
                    VALUES (?, 'CONTENT_VARIANT', ?, ?, ?, ?, 'DRAFT', ?, ?, ?, ?, ?, ?, ?, ?,
                            ?::jsonb, ?::jsonb, ?, ?, ?)
                    ON CONFLICT DO NOTHING
                    """, packageKey + ":" + variant.locale().name().toLowerCase(),
                    source.type().name(), source.id(), packageId, variant.locale().name(),
                    variant.title(), variant.body(), strategy.usp().id(), strategy.icp().id(),
                    strategy.contentPillar().id(), strategy.funnelStage().id(), strategy.conversionGoal().id(),
                    variant.cta(), strategyJson(strategy), json(Map.of(
                            "provider", "OPENAI",
                            "api", "RESPONSES",
                            "model", safe(variant.model()),
                            "inputTokens", variant.inputTokens(),
                            "outputTokens", variant.outputTokens(),
                            "requestOutcome", safe(variant.requestOutcome()),
                            "summary", variant.summary(),
                            "sourceReference", source.sourceReference())),
                    taskId, source.sourceHash(), variant.fingerprint());
            if (rows != 1) {
                throw new ContentValidationException("DUPLICATE_CONTENT", "Content package already exists for this source context");
            }
            created++;
        }
        return new ContentPackageResult(packageId, created, false);
    }

    private String strategyJson(MarketingStrategyContext strategy) {
        return json(Map.of(
                "uspId", strategy.usp().id(),
                "icpId", strategy.icp().id(),
                "positioningId", strategy.positioning().id(),
                "contentPillarId", strategy.contentPillar().id(),
                "funnelStageId", strategy.funnelStage().id(),
                "conversionGoalId", strategy.conversionGoal().id(),
                "primaryCta", strategy.conversionGoal().primaryCta()));
    }

    private static String packageKey(VerifiedContentSource source, MarketingStrategyContext strategy) {
        String identity = String.join("|", source.type().name(), source.id(), source.sourceHash(),
                String.valueOf(strategy.usp().id()), strategy.icp().id(),
                String.valueOf(strategy.positioning().id()), String.valueOf(strategy.contentPillar().id()),
                String.valueOf(strategy.funnelStage().id()), String.valueOf(strategy.conversionGoal().id()));
        return "content-package:" + ContentHashing.sha256(identity);
    }

    private String json(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException error) {
            throw new IllegalStateException("Unable to serialize content package metadata", error);
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
