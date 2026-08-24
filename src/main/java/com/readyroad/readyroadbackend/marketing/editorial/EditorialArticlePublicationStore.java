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
            long imageAssetId,
            List<EditorialArticleApprovalStore.VersionSnapshot> versions) {
        OffsetDateTime publishedAt = OffsetDateTime.now(ZoneOffset.UTC);
        for (var version : versions) {
            jdbc.update("""
                    INSERT INTO article_publications (
                        article_id, article_version_id, language, approval_task_id,
                        publication_task_id, image_asset_id, status, published_at, published_slug
                    )
                    SELECT ?, version.id, version.language, ?, ?, ?, 'PUBLISHED', ?, version.slug
                    FROM article_versions version
                    WHERE version.id = ?
                    ON CONFLICT (article_version_id) DO NOTHING
                    """, articleId, approvalTaskId, publicationTaskId, imageAssetId, publishedAt, version.id());
        }
        if (!hasExactPublications(articleId, approvalTaskId, publicationTaskId, imageAssetId, versions)) {
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
            long imageAssetId,
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
                  AND publication.image_asset_id = ?
                  AND publication.status = 'PUBLISHED'
                  AND publication.published_slug = version.slug
                ORDER BY publication.language
                """, (result, rowNumber) -> new EditorialArticleApprovalStore.VersionSnapshot(
                        result.getLong("id"),
                        result.getString("language"),
                        result.getInt("version_number")),
                articleId, approvalTaskId, publicationTaskId, imageAssetId);
        return persisted.equals(versions);
    }

    List<PublicationRoute> currentRoutes(long articleId) {
        return jdbc.query("""
                SELECT id, language, slug
                FROM article_versions
                WHERE article_id = ? AND is_current
                ORDER BY language
                """, (result, rowNumber) -> new PublicationRoute(
                result.getLong("id"),
                result.getString("language"),
                result.getString("slug")), articleId);
    }

    record PublicationRoute(long versionId, String language, String slug) {}
}
