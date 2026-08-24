CREATE TABLE article_image_assets (
    id BIGSERIAL PRIMARY KEY,
    article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE RESTRICT,
    storage_key VARCHAR(64) NOT NULL UNIQUE,
    content_sha256 CHAR(64) NOT NULL UNIQUE,
    original_storage_path TEXT NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    original_content_type VARCHAR(64) NOT NULL CHECK (
        original_content_type IN ('image/jpeg', 'image/png')
    ),
    original_width INTEGER NOT NULL CHECK (original_width >= 1600),
    original_height INTEGER NOT NULL CHECK (original_height >= 900),
    focal_point_x NUMERIC(5, 4) NOT NULL CHECK (focal_point_x BETWEEN 0 AND 1),
    focal_point_y NUMERIC(5, 4) NOT NULL CHECK (focal_point_y BETWEEN 0 AND 1),
    status VARCHAR(32) NOT NULL CHECK (
        status IN ('PENDING_LICENSE', 'APPROVED', 'SUPERSEDED')
    ),
    created_by VARCHAR(160) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_article_image_assets_identity UNIQUE (id, article_id),
    CONSTRAINT chk_article_image_sha256_lowercase CHECK (
        content_sha256 ~ '^[0-9a-f]{64}$'
    )
);

CREATE UNIQUE INDEX uq_article_image_assets_active
    ON article_image_assets (article_id)
    WHERE status = 'APPROVED';

CREATE INDEX idx_article_image_assets_article_history
    ON article_image_assets (article_id, created_at DESC, id DESC);

CREATE TABLE article_image_variants (
    id BIGSERIAL PRIMARY KEY,
    image_asset_id BIGINT NOT NULL REFERENCES article_image_assets(id) ON DELETE RESTRICT,
    variant_type VARCHAR(16) NOT NULL CHECK (
        variant_type IN ('HERO', 'CARD', 'MOBILE', 'OG')
    ),
    format VARCHAR(16) NOT NULL CHECK (format = 'JPEG'),
    public_path TEXT NOT NULL UNIQUE,
    width INTEGER NOT NULL CHECK (width > 0),
    height INTEGER NOT NULL CHECK (height > 0),
    byte_size INTEGER NOT NULL CHECK (byte_size > 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_article_image_variant UNIQUE (image_asset_id, variant_type, format),
    CONSTRAINT chk_article_image_variant_dimensions CHECK (
        (variant_type = 'HERO' AND width = 1600 AND height = 900)
        OR (variant_type = 'CARD' AND width = 800 AND height = 450)
        OR (variant_type = 'MOBILE' AND width = 480 AND height = 270)
        OR (variant_type = 'OG' AND width = 1200 AND height = 630)
    ),
    CONSTRAINT chk_article_image_variant_budget CHECK (
        (variant_type = 'HERO' AND byte_size < 256000)
        OR (variant_type = 'CARD' AND byte_size < 143360)
        OR (variant_type = 'MOBILE' AND byte_size < 81920)
        OR (variant_type = 'OG' AND byte_size < 307200)
    )
);

CREATE TABLE article_image_localizations (
    image_asset_id BIGINT NOT NULL REFERENCES article_image_assets(id) ON DELETE RESTRICT,
    language VARCHAR(8) NOT NULL CHECK (language IN ('AR', 'NL', 'FR', 'EN')),
    alt_text VARCHAR(500) NOT NULL CHECK (btrim(alt_text) <> ''),
    caption TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (image_asset_id, language),
    CONSTRAINT chk_article_image_caption_nonblank CHECK (
        caption IS NULL OR btrim(caption) <> ''
    )
);
