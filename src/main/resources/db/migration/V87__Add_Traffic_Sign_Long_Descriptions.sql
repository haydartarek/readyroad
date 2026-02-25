-- ============================================================================
-- V87: Add long_description columns for 4-language detailed traffic sign text
-- ============================================================================
-- These columns store the extended educational descriptions imported from
-- the canonical signs.json file via the admin import pipeline.
-- NULLable so existing rows remain valid without a migration backfill.
-- ============================================================================

ALTER TABLE traffic_signs ADD COLUMN long_description_en TEXT NULL;
ALTER TABLE traffic_signs ADD COLUMN long_description_nl TEXT NULL;
ALTER TABLE traffic_signs ADD COLUMN long_description_fr TEXT NULL;
ALTER TABLE traffic_signs ADD COLUMN long_description_ar TEXT NULL;
