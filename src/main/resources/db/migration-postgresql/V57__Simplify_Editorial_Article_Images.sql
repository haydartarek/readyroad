ALTER TABLE article_image_assets
    DROP CONSTRAINT IF EXISTS article_image_assets_status_check;

UPDATE article_image_assets
SET status = 'PENDING'
WHERE status = 'PENDING_LICENSE';

ALTER TABLE article_image_assets
    ADD CONSTRAINT article_image_assets_status_check CHECK (
        status IN ('PENDING', 'APPROVED', 'SUPERSEDED')
    ),
    DROP COLUMN focal_point_x,
    DROP COLUMN focal_point_y;

ALTER TABLE article_image_localizations
    DROP CONSTRAINT IF EXISTS chk_article_image_caption_nonblank,
    DROP COLUMN caption;

DROP TABLE article_image_licenses;
