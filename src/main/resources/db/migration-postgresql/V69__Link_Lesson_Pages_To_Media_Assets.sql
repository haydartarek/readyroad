-- Link published lesson pages to immutable CMS image assets.
-- Existing lesson pages remain unchanged because image_asset_id is nullable.

ALTER TABLE lesson_pages
    ADD COLUMN image_asset_id BIGINT NULL;

ALTER TABLE lesson_pages
    ADD CONSTRAINT fk_lesson_pages_image_asset
    FOREIGN KEY (image_asset_id)
    REFERENCES lesson_media_assets(id)
    ON DELETE RESTRICT;

CREATE INDEX idx_lesson_pages_image_asset
    ON lesson_pages (image_asset_id);
