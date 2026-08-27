ALTER TABLE article_image_licenses
    DROP CONSTRAINT article_image_licenses_source_platform_check;

ALTER TABLE article_image_licenses
    ADD CONSTRAINT article_image_licenses_source_platform_check CHECK (
        source_platform IN ('UNSPLASH', 'PIXABAY', 'PEXELS', 'LOCAL_UPLOAD')
    );

ALTER TABLE article_image_assets
    ADD COLUMN stored_file_name VARCHAR(128),
    ADD CONSTRAINT chk_article_image_stored_file_name CHECK (
        stored_file_name IS NULL OR stored_file_name ~ '^[a-z0-9]+(?:-[a-z0-9]+)*$'
    );

ALTER TABLE article_image_licenses
    ALTER COLUMN source_url DROP NOT NULL,
    ALTER COLUMN photographer_url DROP NOT NULL,
    ALTER COLUMN license_url DROP NOT NULL;

ALTER TABLE article_image_licenses
    ADD CONSTRAINT chk_article_image_local_upload_metadata CHECK (
        source_platform <> 'LOCAL_UPLOAD'
        OR (
            source_asset_id ~ '^[0-9a-f]{64}$'
            AND (source_url IS NULL OR source_url ~ '^https://')
            AND photographer_url IS NULL
            AND (license_url IS NULL OR license_url ~ '^https://')
        )
    );

ALTER TABLE article_image_variants
    DROP CONSTRAINT article_image_variants_variant_type_check,
    DROP CONSTRAINT chk_article_image_variant_dimensions,
    DROP CONSTRAINT chk_article_image_variant_budget;

ALTER TABLE article_image_variants
    ADD CONSTRAINT article_image_variants_variant_type_check CHECK (
        variant_type IN ('HERO', 'CARD', 'MEDIUM', 'MOBILE', 'OG')
    ),
    ADD CONSTRAINT chk_article_image_variant_dimensions CHECK (
        (variant_type = 'HERO' AND (
            (width = 1600 AND height = 900)
            OR (width = 1920 AND height = 1080)
        ))
        OR (variant_type = 'CARD' AND (
            (width = 800 AND height = 450)
            OR (width = 1200 AND height = 675)
        ))
        OR (variant_type = 'MEDIUM' AND width = 800 AND height = 450)
        OR (variant_type = 'MOBILE' AND width = 480 AND height = 270)
        OR (variant_type = 'OG' AND width = 1200 AND height = 630)
    ),
    ADD CONSTRAINT chk_article_image_variant_budget CHECK (
        (variant_type = 'HERO' AND byte_size < 420000)
        OR (variant_type = 'CARD' AND byte_size < 260000)
        OR (variant_type = 'MEDIUM' AND byte_size < 143360)
        OR (variant_type = 'MOBILE' AND byte_size < 81920)
        OR (variant_type = 'OG' AND byte_size < 307200)
    );
