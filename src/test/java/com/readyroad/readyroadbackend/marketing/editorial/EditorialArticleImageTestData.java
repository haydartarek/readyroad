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
                    original_file_name, stored_file_name, original_content_type,
                    original_width, original_height, status, created_by
                ) VALUES (?, ?, ?, ?, ?, ?, 'image/jpeg', 1800, 1000,
                          'APPROVED', 'test-editor')
                RETURNING id
                """, Long.class,
                articleId,
                "test-image-" + articleId,
                String.format(Locale.ROOT, "%064x", articleId),
                "archive/test-image-" + articleId + ".jpg",
                "test-image-" + articleId + ".jpg",
                "test-image-" + articleId);

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
