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
                    SELECT 1 FROM agent_tasks
                    WHERE id = ?
                      AND agent_type = 'EDITORIAL'
                      AND correlation_id = ?
                      AND source_type = 'ARTICLE'
                      AND source_id = ?
                )
                """, Boolean.class, taskId, correlationId, String.valueOf(articleId)));
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
