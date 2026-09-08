-- Source dimensions describe the uploaded file, not the generated HERO rendition.
-- Existing image records and rendition constraints remain unchanged.
ALTER TABLE article_image_assets
    DROP CONSTRAINT article_image_assets_original_width_check,
    DROP CONSTRAINT article_image_assets_original_height_check,
    ADD CONSTRAINT article_image_assets_original_width_check CHECK (original_width > 0),
    ADD CONSTRAINT article_image_assets_original_height_check CHECK (original_height > 0);
