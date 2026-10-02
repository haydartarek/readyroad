-- Switch database metadata to the object-storage runtime after the S3 smoke
-- test has verified the corresponding objects are present.

DO $$
DECLARE
    local_media_count INTEGER;
BEGIN
    SELECT COUNT(*)
    INTO local_media_count
    FROM readyroad.lesson_media_assets
    WHERE storage_provider = 'LOCAL';

    IF local_media_count <> 54 THEN
        RAISE EXCEPTION
            'Expected 54 LOCAL lesson media assets before cutover, found %',
            local_media_count;
    END IF;

    UPDATE readyroad.lesson_media_assets
    SET storage_provider = 'OBJECT_STORAGE'
    WHERE storage_provider = 'LOCAL';

    IF (SELECT COUNT(*)
        FROM readyroad.lesson_media_assets
        WHERE storage_provider = 'OBJECT_STORAGE') <> 54 THEN
        RAISE EXCEPTION
            'Lesson media provider cutover did not produce exactly 54 OBJECT_STORAGE assets';
    END IF;
END
$$;

UPDATE readyroad.article_image_assets
SET original_storage_path = regexp_replace(
        original_storage_path,
        '^archive/',
        'originals/articles/')
WHERE original_storage_path LIKE 'archive/%';

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM readyroad.article_image_assets
        WHERE original_storage_path LIKE 'archive/%'
    ) THEN
        RAISE EXCEPTION
            'Legacy archive article original paths remain after cutover';
    END IF;

    IF (
        SELECT COUNT(*)
        FROM readyroad.article_image_assets
        WHERE original_storage_path LIKE 'originals/articles/%'
    ) <> 8 THEN
        RAISE EXCEPTION
            'Expected 8 migrated article original paths';
    END IF;
END
$$;

UPDATE readyroad.road_signs
SET image_path = '/images/signs/danger_signs/A39 Twee richtingsverkeer toegelaten na een stuk eenrichtingsverkeer.png'
WHERE sign_code = 'A39';

UPDATE readyroad.road_signs
SET image_path = '/images/signs/mandatory_signs/D3a Verplicht een van de pijlen te volgen.png'
WHERE sign_code = 'D3a';

UPDATE readyroad.road_signs
SET image_path = '/images/signs/mandatory_signs/D3b Verplicht een van de pijlen te volgen.png'
WHERE sign_code = 'D3b';

DO $$
BEGIN
    IF (
        SELECT COUNT(*)
        FROM readyroad.road_signs
        WHERE sign_code IN ('A39', 'D3a', 'D3b')
          AND image_path IN (
              '/images/signs/danger_signs/A39 Twee richtingsverkeer toegelaten na een stuk eenrichtingsverkeer.png',
              '/images/signs/mandatory_signs/D3a Verplicht een van de pijlen te volgen.png',
              '/images/signs/mandatory_signs/D3b Verplicht een van de pijlen te volgen.png'
          )
    ) <> 3 THEN
        RAISE EXCEPTION
            'A39, D3a and D3b image paths were not aligned';
    END IF;
END
$$;
