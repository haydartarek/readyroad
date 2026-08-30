package com.readyroad.readyroadbackend.marketing.editorial;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class EditorialArticleImageStore {

    private final JdbcTemplate jdbc;
    private final JdbcClient jdbcClient;

    ArticleLock lockArticle(long articleId) {
        return jdbc.query("""
                SELECT id, canonical_key, lifecycle_state
                FROM articles
                WHERE id = ?
                FOR UPDATE
                """, (result, rowNumber) -> new ArticleLock(
                result.getLong("id"),
                result.getString("canonical_key"),
                result.getString("lifecycle_state")), articleId).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown editorial article: " + articleId));
    }

    boolean duplicate(String sha256) {
        Integer count = jdbc.queryForObject("""
                SELECT count(*)
                FROM article_image_assets
                WHERE content_sha256 = ?
                """, Integer.class, sha256);
        return count != null && count > 0;
    }

    long insertPending(
            long articleId,
            EditorialArticleImageProcessor.Processed processed,
            EditorialArticleImagePolicy.Normalized metadata) {
        return jdbcClient.sql("""
                INSERT INTO article_image_assets (
                    article_id, storage_key, content_sha256, original_storage_path,
                    original_file_name, stored_file_name, original_content_type,
                    original_width, original_height, status, created_by
                ) VALUES (
                    :articleId, :storageKey, :sha256, :originalPath,
                    :originalFileName, :storedFileName, :contentType, :width, :height,
                    'PENDING', :actor
                )
                RETURNING id
                """)
                .param("articleId", articleId)
                .param("storageKey", processed.storageKey())
                .param("sha256", processed.sha256())
                .param("originalPath", processed.originalStoragePath())
                .param("originalFileName", metadata.originalFileName())
                .param("storedFileName", metadata.storedFileName())
                .param("contentType", metadata.contentType())
                .param("width", processed.originalWidth())
                .param("height", processed.originalHeight())
                .param("actor", metadata.uploadedBy())
                .query(Long.class)
                .single();
    }

    void insertVariants(long assetId, List<EditorialArticleImageProcessor.ProcessedVariant> variants) {
        variants.forEach(variant -> jdbc.update("""
                INSERT INTO article_image_variants (
                    image_asset_id, variant_type, format, public_path, width, height, byte_size
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """, assetId, variant.type(), variant.format(), variant.publicPath(),
                variant.width(), variant.height(), variant.byteSize()));
    }

    void insertLocalizations(long assetId, EditorialArticleImagePolicy.Normalized metadata) {
        insertLocalization(assetId, "AR", metadata.altTextAr());
        insertLocalization(assetId, "NL", metadata.altTextNl());
        insertLocalization(assetId, "FR", metadata.altTextFr());
        insertLocalization(assetId, "EN", metadata.altTextEn());
    }

    void activate(long articleId, long assetId) {
        jdbc.update("""
                UPDATE article_image_assets
                SET status = 'SUPERSEDED', updated_at = CURRENT_TIMESTAMP
                WHERE article_id = ? AND status = 'APPROVED'
                """, articleId);
        int changed = jdbc.update("""
                UPDATE article_image_assets
                SET status = 'APPROVED', updated_at = CURRENT_TIMESTAMP
                WHERE id = ? AND article_id = ? AND status = 'PENDING'
                """, assetId, articleId);
        if (changed != 1) {
            throw new IllegalStateException("Unable to activate the article image");
        }
    }

    long supersedeCurrent(long articleId) {
        return jdbc.query("""
                UPDATE article_image_assets
                SET status = 'SUPERSEDED', updated_at = CURRENT_TIMESTAMP
                WHERE article_id = ? AND status = 'APPROVED'
                RETURNING id
                """, (result, rowNumber) -> result.getLong("id"), articleId).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No article image is attached"));
    }

    Optional<EditorialArticleImageDtos.Asset> current(long articleId) {
        return jdbc.query("""
                SELECT id, article_id, status, original_file_name, stored_file_name,
                       original_width, original_height, created_at, created_by
                FROM article_image_assets
                WHERE article_id = ? AND status = 'APPROVED'
                """, this::assetRow, articleId).stream().findFirst().map(this::asset);
    }

    ApprovedImage requireApprovalReady(long articleId) {
        return jdbc.query("""
                SELECT asset.id AS asset_id,
                       count(DISTINCT variant.variant_type) AS variant_count,
                       count(DISTINCT localization.language) AS localization_count
                FROM article_image_assets asset
                JOIN article_image_variants variant ON variant.image_asset_id = asset.id
                JOIN article_image_localizations localization ON localization.image_asset_id = asset.id
                WHERE asset.article_id = ? AND asset.status = 'APPROVED'
                GROUP BY asset.id
                """, (result, rowNumber) -> new ApprovalRow(
                result.getLong("asset_id"),
                result.getInt("variant_count"),
                result.getInt("localization_count")), articleId).stream()
                .filter(row -> (row.variantCount() == 4 || row.variantCount() == 5)
                        && row.localizationCount() == 4)
                .map(row -> new ApprovedImage(row.assetId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "An article image with all variants and localized alt text is required"));
    }

    Optional<EditorialArticleImageDtos.PublicImage> publicImage(long assetId, String language) {
        List<VariantRow> variants = variants(assetId);
        Map<String, String> paths = new LinkedHashMap<>();
        variants.forEach(variant -> paths.put(variant.type(), variant.publicPath()));
        if (!paths.keySet().containsAll(List.of("HERO", "CARD", "MOBILE", "OG"))) {
            return Optional.empty();
        }
        return jdbc.query("""
                SELECT localization.alt_text
                FROM article_image_assets asset
                JOIN article_image_localizations localization
                  ON localization.image_asset_id = asset.id AND localization.language = ?
                WHERE asset.id = ? AND asset.status = 'APPROVED'
                """, (result, rowNumber) -> new EditorialArticleImageDtos.PublicImage(
                assetId,
                paths.get("HERO"),
                paths.get("CARD"),
                paths.get("MEDIUM"),
                paths.get("MOBILE"),
                paths.get("OG"),
                result.getString("alt_text")), language, assetId).stream().findFirst();
    }

    private EditorialArticleImageDtos.Asset asset(AssetRow row) {
        return new EditorialArticleImageDtos.Asset(
                row.id(), row.articleId(), row.status(), row.originalFileName(),
                row.storedFileName(), row.originalWidth(), row.originalHeight(),
                variants(row.id()).stream().map(value -> new EditorialArticleImageDtos.Variant(
                        value.type(), value.format(), value.publicPath(),
                        value.width(), value.height(), value.byteSize())).toList(),
                localizations(row.id()),
                row.createdAt(), row.createdBy());
    }

    private List<VariantRow> variants(long assetId) {
        return jdbc.query("""
                SELECT variant_type, format, public_path, width, height, byte_size
                FROM article_image_variants
                WHERE image_asset_id = ?
                ORDER BY CASE variant_type
                    WHEN 'HERO' THEN 1
                    WHEN 'CARD' THEN 2
                    WHEN 'MEDIUM' THEN 3
                    WHEN 'MOBILE' THEN 4
                    ELSE 5
                END
                """, (result, rowNumber) -> new VariantRow(
                result.getString("variant_type"),
                result.getString("format"),
                result.getString("public_path"),
                result.getInt("width"),
                result.getInt("height"),
                result.getInt("byte_size")), assetId);
    }

    private List<EditorialArticleImageDtos.Localization> localizations(long assetId) {
        return jdbc.query("""
                SELECT language, alt_text
                FROM article_image_localizations
                WHERE image_asset_id = ?
                ORDER BY CASE language WHEN 'AR' THEN 1 WHEN 'NL' THEN 2 WHEN 'FR' THEN 3 ELSE 4 END
                """, (result, rowNumber) -> new EditorialArticleImageDtos.Localization(
                result.getString("language"),
                result.getString("alt_text")), assetId);
    }

    private void insertLocalization(long assetId, String language, String altText) {
        jdbc.update("""
                INSERT INTO article_image_localizations (image_asset_id, language, alt_text)
                VALUES (?, ?, ?)
                """, assetId, language, altText);
    }

    private AssetRow assetRow(ResultSet result, int rowNumber) throws SQLException {
        return new AssetRow(
                result.getLong("id"),
                result.getLong("article_id"),
                result.getString("status"),
                result.getString("original_file_name"),
                result.getString("stored_file_name"),
                result.getInt("original_width"),
                result.getInt("original_height"),
                instant(result, "created_at"),
                result.getString("created_by"));
    }

    private static Instant instant(ResultSet result, String column) throws SQLException {
        return result.getObject(column, OffsetDateTime.class).toInstant();
    }

    record ArticleLock(long id, String canonicalKey, String lifecycleState) {}

    record ApprovedImage(long assetId) {}

    private record ApprovalRow(long assetId, int variantCount, int localizationCount) {}

    private record AssetRow(
            long id,
            long articleId,
            String status,
            String originalFileName,
            String storedFileName,
            int originalWidth,
            int originalHeight,
            Instant createdAt,
            String createdBy) {}

    private record VariantRow(
            String type,
            String format,
            String publicPath,
            int width,
            int height,
            int byteSize) {}
}
