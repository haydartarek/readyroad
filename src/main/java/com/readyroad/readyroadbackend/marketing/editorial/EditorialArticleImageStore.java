package com.readyroad.readyroadbackend.marketing.editorial;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

    boolean duplicate(String sha256, EditorialArticleImageDtos.SourcePlatform platform, String sourceAssetId) {
        Integer count = jdbc.queryForObject("""
                SELECT count(*)
                FROM article_image_assets asset
                LEFT JOIN article_image_licenses license ON license.image_asset_id = asset.id
                WHERE asset.content_sha256 = ?
                   OR (license.source_platform = ? AND license.source_asset_id = ?)
                """, Integer.class, sha256, platform.name(), sourceAssetId);
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
                    original_width, original_height,
                    focal_point_x, focal_point_y, status, created_by
                ) VALUES (
                    :articleId, :storageKey, :sha256, :originalPath,
                    :originalFileName, :storedFileName, :contentType, :width, :height,
                    :focalX, :focalY, 'PENDING_LICENSE', :actor
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
                .param("focalX", metadata.focalPointX())
                .param("focalY", metadata.focalPointY())
                .param("actor", metadata.approvedBy())
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
        insertLocalization(assetId, "AR", metadata.altTextAr(), metadata.captionAr());
        insertLocalization(assetId, "NL", metadata.altTextNl(), metadata.captionNl());
        insertLocalization(assetId, "FR", metadata.altTextFr(), metadata.captionFr());
        insertLocalization(assetId, "EN", metadata.altTextEn(), metadata.captionEn());
    }

    long insertLicense(
            long assetId,
            long articleId,
            EditorialArticleImagePolicy.Normalized metadata,
            Instant approvedAt) {
        return jdbcClient.sql("""
                INSERT INTO article_image_licenses (
                    image_asset_id, article_id, source_platform, source_asset_id, source_url,
                    photographer_name, photographer_url, license_name, license_url,
                    license_verified_at, downloaded_at, original_file_name,
                    approved_by, approved_at, approval_reason
                ) VALUES (
                    :assetId, :articleId, :platform, :sourceAssetId, :sourceUrl,
                    :photographerName, :photographerUrl, :licenseName, :licenseUrl,
                    :licenseVerifiedAt, :downloadedAt, :originalFileName,
                    :approvedBy, :approvedAt, :approvalReason
                )
                RETURNING id
                """)
                .param("assetId", assetId)
                .param("articleId", articleId)
                .param("platform", metadata.sourcePlatform().name())
                .param("sourceAssetId", metadata.sourceAssetId())
                .param("sourceUrl", metadata.sourceUrl())
                .param("photographerName", metadata.photographerName())
                .param("photographerUrl", metadata.photographerUrl())
                .param("licenseName", metadata.licenseName())
                .param("licenseUrl", metadata.licenseUrl())
                .param("licenseVerifiedAt", Timestamp.from(metadata.licenseVerifiedAt()))
                .param("downloadedAt", Timestamp.from(metadata.downloadedAt()))
                .param("originalFileName", metadata.originalFileName())
                .param("approvedBy", metadata.approvedBy())
                .param("approvedAt", Timestamp.from(approvedAt))
                .param("approvalReason", metadata.approvalReason())
                .query(Long.class)
                .single();
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
                WHERE id = ? AND article_id = ? AND status = 'PENDING_LICENSE'
                """, assetId, articleId);
        if (changed != 1) {
            throw new IllegalStateException("Unable to activate the approved article image");
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
                        "No approved article image is attached"));
    }

    Optional<EditorialArticleImageDtos.Asset> current(long articleId) {
        return jdbc.query("""
                SELECT id, article_id, status, original_file_name, stored_file_name,
                       original_width, original_height, focal_point_x, focal_point_y,
                       created_at, created_by
                FROM article_image_assets
                WHERE article_id = ? AND status = 'APPROVED'
                """, this::assetRow, articleId).stream().findFirst().map(this::asset);
    }

    ApprovedImage requireApprovalReady(long articleId) {
        return jdbc.query("""
                SELECT asset.id AS asset_id,
                       license.id AS license_id,
                       count(DISTINCT variant.variant_type) AS variant_count,
                       count(DISTINCT localization.language) AS localization_count
                FROM article_image_assets asset
                JOIN article_image_licenses license
                  ON license.image_asset_id = asset.id AND license.article_id = asset.article_id
                JOIN article_image_variants variant ON variant.image_asset_id = asset.id
                JOIN article_image_localizations localization ON localization.image_asset_id = asset.id
                WHERE asset.article_id = ? AND asset.status = 'APPROVED'
                GROUP BY asset.id, license.id
                """, (result, rowNumber) -> new ApprovalRow(
                result.getLong("asset_id"),
                result.getLong("license_id"),
                result.getInt("variant_count"),
                result.getInt("localization_count")), articleId).stream()
                .filter(row -> (row.variantCount() == 4 || row.variantCount() == 5)
                        && row.localizationCount() == 4)
                .map(row -> new ApprovedImage(row.assetId(), row.licenseId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "An approved licensed image with all variants and localized alt text is required"));
    }

    Optional<EditorialArticleImageDtos.PublicImage> publicImage(long assetId, String language) {
        List<VariantRow> variants = variants(assetId);
        Map<String, String> paths = new LinkedHashMap<>();
        variants.forEach(variant -> paths.put(variant.type(), variant.publicPath()));
        if (!paths.keySet().containsAll(List.of("HERO", "CARD", "MOBILE", "OG"))) {
            return Optional.empty();
        }
        return jdbc.query("""
                SELECT localization.alt_text,
                       localization.caption,
                       license.source_platform,
                       license.source_url,
                       license.photographer_name,
                       license.photographer_url,
                       license.license_name,
                       license.license_url
                FROM article_image_assets asset
                JOIN article_image_localizations localization
                  ON localization.image_asset_id = asset.id AND localization.language = ?
                JOIN article_image_licenses license ON license.image_asset_id = asset.id
                WHERE asset.id = ? AND asset.status = 'APPROVED'
                """, (result, rowNumber) -> new EditorialArticleImageDtos.PublicImage(
                assetId,
                paths.get("HERO"),
                paths.get("CARD"),
                paths.get("MOBILE"),
                paths.get("MOBILE"),
                paths.get("OG"),
                result.getString("alt_text"),
                result.getString("caption"),
                result.getString("source_platform"),
                result.getString("source_url"),
                result.getString("photographer_name"),
                result.getString("photographer_url"),
                result.getString("license_name"),
                result.getString("license_url")), language, assetId).stream().findFirst();
    }

    private EditorialArticleImageDtos.Asset asset(AssetRow row) {
        return new EditorialArticleImageDtos.Asset(
                row.id(), row.articleId(), row.status(), row.originalFileName(),
                row.storedFileName(),
                row.originalWidth(), row.originalHeight(), row.focalPointX(), row.focalPointY(),
                variants(row.id()).stream().map(value -> new EditorialArticleImageDtos.Variant(
                        value.type(), value.format(), value.publicPath(),
                        value.width(), value.height(), value.byteSize())).toList(),
                localizations(row.id()),
                license(row.id()).orElse(null),
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
                SELECT language, alt_text, caption
                FROM article_image_localizations
                WHERE image_asset_id = ?
                ORDER BY CASE language WHEN 'AR' THEN 1 WHEN 'NL' THEN 2 WHEN 'FR' THEN 3 ELSE 4 END
                """, (result, rowNumber) -> new EditorialArticleImageDtos.Localization(
                result.getString("language"),
                result.getString("alt_text"),
                result.getString("caption")), assetId);
    }

    private Optional<EditorialArticleImageDtos.License> license(long assetId) {
        return jdbc.query("""
                SELECT id, source_platform, source_asset_id, source_url,
                       photographer_name, photographer_url, license_name, license_url,
                       license_verified_at, downloaded_at, original_file_name,
                       approved_by, approved_at, approval_reason
                FROM article_image_licenses
                WHERE image_asset_id = ?
                """, (result, rowNumber) -> new EditorialArticleImageDtos.License(
                result.getLong("id"),
                result.getString("source_platform"),
                result.getString("source_asset_id"),
                result.getString("source_url"),
                result.getString("photographer_name"),
                result.getString("photographer_url"),
                result.getString("license_name"),
                result.getString("license_url"),
                instant(result, "license_verified_at"),
                instant(result, "downloaded_at"),
                result.getString("original_file_name"),
                result.getString("approved_by"),
                instant(result, "approved_at"),
                result.getString("approval_reason")), assetId).stream().findFirst();
    }

    private void insertLocalization(long assetId, String language, String altText, String caption) {
        jdbc.update("""
                INSERT INTO article_image_localizations (image_asset_id, language, alt_text, caption)
                VALUES (?, ?, ?, ?)
                """, assetId, language, altText, caption);
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
                result.getDouble("focal_point_x"),
                result.getDouble("focal_point_y"),
                instant(result, "created_at"),
                result.getString("created_by"));
    }

    private static Instant instant(ResultSet result, String column) throws SQLException {
        return result.getObject(column, OffsetDateTime.class).toInstant();
    }

    record ArticleLock(long id, String canonicalKey, String lifecycleState) {}

    record ApprovedImage(long assetId, long licenseId) {}

    private record ApprovalRow(long assetId, long licenseId, int variantCount, int localizationCount) {}

    private record AssetRow(
            long id,
            long articleId,
            String status,
            String originalFileName,
            String storedFileName,
            int originalWidth,
            int originalHeight,
            double focalPointX,
            double focalPointY,
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
