CREATE TABLE article_image_licenses (
    id BIGSERIAL PRIMARY KEY,
    image_asset_id BIGINT NOT NULL,
    article_id BIGINT NOT NULL,
    source_platform VARCHAR(32) NOT NULL CHECK (
        source_platform IN ('UNSPLASH', 'PIXABAY', 'PEXELS')
    ),
    source_asset_id VARCHAR(255) NOT NULL,
    source_url TEXT NOT NULL,
    photographer_name VARCHAR(255) NOT NULL,
    photographer_url TEXT NOT NULL,
    license_name VARCHAR(255) NOT NULL,
    license_url TEXT NOT NULL,
    license_verified_at TIMESTAMPTZ NOT NULL,
    downloaded_at TIMESTAMPTZ NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    approved_by VARCHAR(160) NOT NULL,
    approved_at TIMESTAMPTZ NOT NULL,
    approval_reason VARCHAR(1000) NOT NULL CHECK (btrim(approval_reason) <> ''),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_article_image_licenses_asset UNIQUE (image_asset_id),
    CONSTRAINT uq_article_image_licenses_source UNIQUE (source_platform, source_asset_id),
    CONSTRAINT fk_article_image_license_asset
        FOREIGN KEY (image_asset_id, article_id)
        REFERENCES article_image_assets(id, article_id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_article_image_licenses_article
    ON article_image_licenses (article_id, approved_at DESC, id DESC);

ALTER TABLE article_publications
    ADD COLUMN image_asset_id BIGINT REFERENCES article_image_assets(id) ON DELETE RESTRICT;

CREATE INDEX idx_article_publications_image_asset
    ON article_publications (image_asset_id)
    WHERE image_asset_id IS NOT NULL;

CREATE OR REPLACE FUNCTION protect_article_publication_route()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.article_id IS DISTINCT FROM OLD.article_id
       OR NEW.article_version_id IS DISTINCT FROM OLD.article_version_id
       OR NEW.language IS DISTINCT FROM OLD.language
       OR NEW.approval_task_id IS DISTINCT FROM OLD.approval_task_id
       OR NEW.publication_task_id IS DISTINCT FROM OLD.publication_task_id
       OR NEW.image_asset_id IS DISTINCT FROM OLD.image_asset_id
       OR NEW.published_slug IS DISTINCT FROM OLD.published_slug
       OR NEW.published_at IS DISTINCT FROM OLD.published_at THEN
        RAISE EXCEPTION 'Article publication routes are immutable'
            USING ERRCODE = '23514';
    END IF;

    RETURN NEW;
END;
$$;
