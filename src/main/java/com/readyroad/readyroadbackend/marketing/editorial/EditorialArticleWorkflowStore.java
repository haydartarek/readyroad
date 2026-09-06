package com.readyroad.readyroadbackend.marketing.editorial;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class EditorialArticleWorkflowStore {

    private static final Set<String> REQUIRED_LANGUAGES = Set.of("AR", "NL", "FR", "EN");

    private final JdbcTemplate jdbc;

    LockedArticle lock(long articleId) {
        return jdbc.query("""
                SELECT id, article_topic_id, lifecycle_state, canonical_language, updated_at
                FROM articles
                WHERE id = ?
                FOR UPDATE
                """, (result, rowNumber) -> new LockedArticle(
                        result.getLong("id"),
                        result.getLong("article_topic_id"),
                        EditorialArticleState.valueOf(result.getString("lifecycle_state")),
                        result.getString("canonical_language"),
                        result.getObject("updated_at", OffsetDateTime.class).toInstant()),
                articleId).stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown article: " + articleId));
    }

    boolean hasApprovedBrief(long topicId) {
        return Boolean.TRUE.equals(jdbc.queryForObject("""
                SELECT EXISTS (
                    SELECT 1 FROM article_briefs
                    WHERE article_topic_id = ? AND status = 'APPROVED'
                )
                """, Boolean.class, topicId));
    }

    boolean matchesEditorialTask(
            long taskId,
            String correlationId,
            long articleId) {
        return Boolean.TRUE.equals(jdbc.queryForObject("""
                SELECT EXISTS (
                    SELECT 1
                    FROM agent_tasks task
                    JOIN articles article ON article.id = ?
                    WHERE task.id = ?
                      AND task.agent_type = 'EDITORIAL'
                      AND task.correlation_id = ?
                      AND (
                          (task.source_type = 'ARTICLE' AND task.source_id = article.id::text)
                          OR
                          (task.source_type = 'ARTICLE_TOPIC'
                           AND task.source_id = article.article_topic_id::text)
                      )
                )
                """, Boolean.class, articleId, taskId, correlationId));
    }

    boolean approvedBriefRequiresLegalReview(long topicId) {
        List<Boolean> values = jdbc.query("""
                SELECT bool_or(legal_review_required) AS legal_review_required
                FROM article_briefs
                WHERE article_topic_id = ? AND status = 'APPROVED'
                HAVING count(*) > 0
                """, (result, rowNumber) -> result.getBoolean("legal_review_required"), topicId);
        if (values.isEmpty()) {
            throw new IllegalStateException("An approved brief is required before fact-check routing");
        }
        return values.getFirst();
    }

    boolean hasFreshCurrentCanonicalDraft(
            long articleId,
            String canonicalLanguage,
            Instant draftingStartedAt) {
        return Boolean.TRUE.equals(jdbc.queryForObject("""
                SELECT EXISTS (
                    SELECT 1 FROM article_versions
                    WHERE article_id = ?
                      AND language = ?
                      AND is_current
                      AND status = 'DRAFT_READY'
                      AND created_at >= ?
                )
                """, Boolean.class, articleId, canonicalLanguage,
                OffsetDateTime.ofInstant(draftingStartedAt, ZoneOffset.UTC)));
    }

    void markSavedCanonicalDraftReady(long articleId, String language, Instant draftingStartedAt) {
        int updated = jdbc.update("""
                UPDATE article_versions SET status = 'DRAFT_READY'
                WHERE article_id = ? AND language = ? AND is_current
                  AND status = 'DRAFT' AND created_at >= ?
                  AND NULLIF(btrim(title), '') IS NOT NULL
                  AND NULLIF(btrim(body), '') IS NOT NULL
                  AND NULLIF(btrim(metadata ->> 'metaTitle'), '') IS NOT NULL
                  AND NULLIF(btrim(metadata ->> 'metaDescription'), '') IS NOT NULL
                """, articleId, language, OffsetDateTime.ofInstant(draftingStartedAt, ZoneOffset.UTC));
        if (updated != 1) {
            throw new IllegalStateException("Save a complete canonical draft before submitting it for review");
        }
    }

    boolean hasAllRequiredCurrentLanguages(long articleId) {
        Set<String> languages = jdbc.queryForList("""
                SELECT language FROM article_versions
                WHERE article_id = ? AND is_current
                """, String.class, articleId).stream().collect(Collectors.toUnmodifiableSet());
        return languages.containsAll(REQUIRED_LANGUAGES);
    }

    Instant updateState(long articleId, EditorialArticleState target) {
        return jdbc.queryForObject("""
                UPDATE articles
                SET lifecycle_state = ?, updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                RETURNING updated_at
                """, (result, rowNumber) -> result.getObject("updated_at", OffsetDateTime.class).toInstant(),
                target.name(), articleId);
    }

    record LockedArticle(
            long id,
            long topicId,
            EditorialArticleState state,
            String canonicalLanguage,
            Instant updatedAt) {}
}
