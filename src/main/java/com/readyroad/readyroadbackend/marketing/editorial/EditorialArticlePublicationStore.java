package com.readyroad.readyroadbackend.marketing.editorial;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class EditorialArticlePublicationStore {

    private final JdbcTemplate jdbc;

    void publish(
            long articleId,
            long approvalTaskId,
            long publicationTaskId,
            List<EditorialArticleApprovalStore.VersionSnapshot> versions) {
        OffsetDateTime publishedAt = OffsetDateTime.now(ZoneOffset.UTC);
        for (var version : versions) {
            jdbc.update("""
                    INSERT INTO article_publications (
                        article_id, article_version_id, language, approval_task_id,
                        publication_task_id, status, published_at
                    ) VALUES (?, ?, ?, ?, ?, 'PUBLISHED', ?)
                    ON CONFLICT (article_version_id) DO NOTHING
                    """, articleId, version.id(), version.language(), approvalTaskId,
                    publicationTaskId, publishedAt);
        }
        if (!hasExactPublications(articleId, approvalTaskId, publicationTaskId, versions)) {
            throw new IllegalStateException("Published article versions do not match the approved snapshot");
        }
        for (var version : versions) {
            jdbc.update("UPDATE article_versions SET status = 'PUBLISHED' WHERE id = ?", version.id());
        }
    }

    boolean hasExactPublications(
            long articleId,
            long approvalTaskId,
            long publicationTaskId,
            List<EditorialArticleApprovalStore.VersionSnapshot> versions) {
        List<EditorialArticleApprovalStore.VersionSnapshot> persisted = jdbc.query("""
                SELECT publication.article_version_id AS id,
                       publication.language,
                       version.version_number
                FROM article_publications publication
                JOIN article_versions version ON version.id = publication.article_version_id
                WHERE publication.article_id = ?
                  AND publication.approval_task_id = ?
                  AND publication.publication_task_id = ?
                  AND publication.status = 'PUBLISHED'
                ORDER BY publication.language
                """, (result, rowNumber) -> new EditorialArticleApprovalStore.VersionSnapshot(
                        result.getLong("id"),
                        result.getString("language"),
                        result.getInt("version_number")),
                articleId, approvalTaskId, publicationTaskId);
        return persisted.equals(versions);
    }
}
