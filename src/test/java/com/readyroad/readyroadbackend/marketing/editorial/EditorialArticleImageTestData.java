package com.readyroad.readyroadbackend.marketing.editorial;

import java.util.Locale;
import org.springframework.jdbc.core.JdbcTemplate;

final class EditorialArticleImageTestData {

    private EditorialArticleImageTestData() {
    }

    static long seedApprovedImage(JdbcTemplate jdbc, long articleId) {
        Long assetId = jdbc.queryForObject("""
                INSERT INTO article_image_assets (
                    article_id, storage_key, content_sha256, original_storage_path,
                    original_file_name, original_content_type, original_width, original_height,
                    focal_point_x, focal_point_y, status, created_by
                ) VALUES (?, ?, ?, ?, ?, 'image/jpeg', 1800, 1000, 0.5000, 0.5000,
                          'APPROVED', 'test-editor')
                RETURNING id
                """, Long.class,
                articleId,
                "test-image-" + articleId,
                String.format(Locale.ROOT, "%064x", articleId),
                "archive/test-image-" + articleId + ".jpg",
                "test-image-" + articleId + ".jpg");

        insertVariant(jdbc, assetId, "HERO", 1600, 900);
        insertVariant(jdbc, assetId, "CARD", 800, 450);
        insertVariant(jdbc, assetId, "MOBILE", 480, 270);
        insertVariant(jdbc, assetId, "OG", 1200, 630);

        for (String language : new String[] {"AR", "NL", "FR", "EN"}) {
            jdbc.update("""
                    INSERT INTO article_image_localizations (image_asset_id, language, alt_text)
                    VALUES (?, ?, ?)
                    """, assetId, language, language + " approved article image");
        }

        jdbc.update("""
                INSERT INTO article_image_licenses (
                    image_asset_id, article_id, source_platform, source_asset_id, source_url,
                    photographer_name, photographer_url, license_name, license_url,
                    license_verified_at, downloaded_at, original_file_name,
                    approved_by, approved_at, approval_reason
                ) VALUES (?, ?, 'UNSPLASH', ?, ?, 'Test Photographer', ?,
                          'Unsplash License', 'https://unsplash.com/license',
                          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?,
                          'test-owner', CURRENT_TIMESTAMP, 'Verified test license evidence')
                """,
                assetId,
                articleId,
                "test-source-" + articleId,
                "https://unsplash.com/photos/test-source-" + articleId,
                "https://unsplash.com/@test-photographer",
                "test-image-" + articleId + ".jpg");
        return assetId;
    }

    private static void insertVariant(
            JdbcTemplate jdbc,
            long assetId,
            String type,
            int width,
            int height) {
        jdbc.update("""
                INSERT INTO article_image_variants (
                    image_asset_id, variant_type, format, public_path, width, height, byte_size
                ) VALUES (?, ?, 'JPEG', ?, ?, ?, 1024)
                """,
                assetId,
                type,
                "/images/articles/test-" + assetId + "-" + type.toLowerCase(Locale.ROOT) + ".jpg",
                width,
                height);
    }
}
