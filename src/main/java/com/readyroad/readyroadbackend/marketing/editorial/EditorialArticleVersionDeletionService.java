package com.readyroad.readyroadbackend.marketing.editorial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.readyroad.readyroadbackend.marketing.audit.MarketingAuditService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
class EditorialArticleVersionDeletionService {

    static final String AUDIT_EVENT = "EDITORIAL_ARTICLE_VERSION_DELETED";

    private static final Set<String> CURRENT_DELETE_BLOCKED_STATES = Set.of(
            "WAITING_APPROVAL",
            "APPROVED",
            "SCHEDULED",
            "PUBLISHED");

    private final JdbcTemplate jdbc;
    private final MarketingAuditService auditService;
    private final ObjectMapper objectMapper;

    @Transactional
    public void delete(long articleId, long versionId, String actor) {
        validate(articleId, versionId, actor);
        String lifecycleState = jdbc.query("""
                SELECT lifecycle_state
                FROM articles
                WHERE id = ?
                FOR UPDATE
                """, (result, rowNumber) -> result.getString("lifecycle_state"), articleId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Editorial article was not found"));

        RemovalCandidate candidate = jdbc.query("""
                SELECT version.id, version.language, version.version_number,
                       version.is_current, version.status,
                       (SELECT count(*) FROM article_versions sibling
                        WHERE sibling.article_id = version.article_id
                          AND sibling.language = version.language)::int AS language_version_count,
                       EXISTS (SELECT 1 FROM article_publications publication
                               WHERE publication.article_version_id = version.id) AS publication_referenced
                FROM article_versions version
                WHERE version.id = ? AND version.article_id = ?
                FOR UPDATE
                """, (result, rowNumber) -> new RemovalCandidate(
                        result.getLong("id"),
                        result.getString("language"),
                        result.getInt("version_number"),
                        result.getBoolean("is_current"),
                        result.getString("status"),
                        result.getInt("language_version_count"),
                        result.getBoolean("publication_referenced")),
                versionId, articleId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Article version was not found"));

        if (candidate.publicationReferenced()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "A published article version cannot be permanently deleted");
        }
        if (candidate.languageVersionCount() <= 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "The last remaining version for a language cannot be deleted");
        }
        if (candidate.current() && CURRENT_DELETE_BLOCKED_STATES.contains(lifecycleState)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "The current article version cannot be deleted in this workflow state");
        }

        Replacement replacement = candidate.current()
                ? replacement(articleId, versionId, candidate.language())
                : null;
        jdbc.queryForObject(
                "SELECT set_config('rijvia.allow_article_version_delete', 'on', true)",
                String.class);
        int deleted = jdbc.update(
                "DELETE FROM article_versions WHERE id = ? AND article_id = ?",
                versionId,
                articleId);
        if (deleted != 1) {
            throw new IllegalStateException("Article version deletion did not affect exactly one row");
        }
        if (replacement != null) {
            int promoted = jdbc.update("""
                    UPDATE article_versions
                    SET is_current = TRUE
                    WHERE id = ? AND article_id = ?
                    """, replacement.id(), articleId);
            if (promoted != 1) {
                throw new IllegalStateException("Replacement article version could not be promoted");
            }
        }

        ObjectNode details = objectMapper.createObjectNode()
                .put("articleId", articleId)
                .put("deletedVersionId", candidate.id())
                .put("deletedVersionNumber", candidate.versionNumber())
                .put("language", candidate.language())
                .put("status", candidate.status())
                .put("wasCurrent", candidate.current())
                .put("permanent", true);
        if (replacement != null) {
            details.put("promotedVersionId", replacement.id());
            details.put("promotedVersionNumber", replacement.versionNumber());
        }
        auditService.recordEntityEvent(
                AUDIT_EVENT,
                actor.trim(),
                "EDITORIAL_ARTICLE_VERSION",
                String.valueOf(candidate.id()),
                null,
                "editorial-version-delete-" + candidate.id(),
                details);
    }

    private Replacement replacement(long articleId, long versionId, String language) {
        return jdbc.query("""
                SELECT id, version_number
                FROM article_versions
                WHERE article_id = ? AND language = ? AND id <> ?
                ORDER BY version_number DESC, id DESC
                LIMIT 1
                FOR UPDATE
                """, (result, rowNumber) -> new Replacement(
                        result.getLong("id"), result.getInt("version_number")),
                articleId, language, versionId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT, "No replacement article version is available"));
    }

    private static void validate(long articleId, long versionId, String actor) {
        if (articleId <= 0) {
            throw new IllegalArgumentException("articleId must be positive");
        }
        if (versionId <= 0) {
            throw new IllegalArgumentException("versionId must be positive");
        }
        if (actor == null || actor.isBlank() || actor.trim().length() > 160) {
            throw new IllegalArgumentException("A valid version remover is required");
        }
    }

    private record RemovalCandidate(
            long id,
            String language,
            int versionNumber,
            boolean current,
            String status,
            int languageVersionCount,
            boolean publicationReferenced) {}

    private record Replacement(long id, int versionNumber) {}
}
