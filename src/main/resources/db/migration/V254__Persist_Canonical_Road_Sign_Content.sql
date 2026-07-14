-- Persist every localized sign-content field so road_signs is the complete
-- operational projection of signs_import/*/sign.json.
ALTER TABLE road_signs
    ADD COLUMN summary_nl LONGTEXT NULL,
    ADD COLUMN summary_en LONGTEXT NULL,
    ADD COLUMN summary_fr LONGTEXT NULL,
    ADD COLUMN summary_ar LONGTEXT NULL,
    ADD COLUMN driver_guidance_nl LONGTEXT NULL,
    ADD COLUMN driver_guidance_en LONGTEXT NULL,
    ADD COLUMN driver_guidance_fr LONGTEXT NULL,
    ADD COLUMN driver_guidance_ar LONGTEXT NULL,
    ADD COLUMN exceptions_nl LONGTEXT NULL,
    ADD COLUMN exceptions_en LONGTEXT NULL,
    ADD COLUMN exceptions_fr LONGTEXT NULL,
    ADD COLUMN exceptions_ar LONGTEXT NULL;
