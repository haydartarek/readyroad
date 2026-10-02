-- Switch database metadata to the object-storage runtime after the S3 smoke
-- test has verified the corresponding objects are present.
-- A clean installation has no legacy media to cut over. Unexpected partial
-- inventories remain hard failures.

DO $$
DECLARE
    local_media_count INTEGER;
    object_media_count INTEGER;
BEGIN
    SELECT COUNT(*)
    INTO local_media_count
    FROM lesson_media_assets
    WHERE storage_provider = 'LOCAL';

    SELECT COUNT(*)
    INTO object_media_count
    FROM lesson_media_assets
    WHERE storage_provider = 'OBJECT_STORAGE';

    IF local_media_count = 0 AND object_media_count = 0 THEN
        NULL;
    ELSIF local_media_count = 54 AND object_media_count = 0 THEN
        UPDATE lesson_media_assets
        SET storage_provider = 'OBJECT_STORAGE'
        WHERE storage_provider = 'LOCAL';

        IF (SELECT COUNT(*)
            FROM lesson_media_assets
            WHERE storage_provider = 'OBJECT_STORAGE') <> 54 THEN
            RAISE EXCEPTION
                'Lesson media provider cutover did not produce exactly 54 OBJECT_STORAGE assets';
        END IF;
    ELSIF local_media_count = 0 AND object_media_count = 54 THEN
        NULL;
    ELSE
        RAISE EXCEPTION
            'Unexpected lesson media inventory before cutover: LOCAL=%, OBJECT_STORAGE=%',
            local_media_count, object_media_count;
    END IF;
END
$$;

DO $$
DECLARE
    legacy_article_count INTEGER;
    migrated_article_count INTEGER;
BEGIN
    SELECT COUNT(*)
    INTO legacy_article_count
    FROM article_image_assets
    WHERE original_storage_path LIKE 'archive/%';

    SELECT COUNT(*)
    INTO migrated_article_count
    FROM article_image_assets
    WHERE original_storage_path LIKE 'originals/articles/%';

    IF legacy_article_count = 0 AND migrated_article_count = 0 THEN
        NULL;
    ELSIF legacy_article_count = 8 AND migrated_article_count = 0 THEN
        UPDATE article_image_assets
        SET original_storage_path = regexp_replace(
                original_storage_path,
                '^archive/',
                'originals/articles/')
        WHERE original_storage_path LIKE 'archive/%';

        IF (SELECT COUNT(*)
            FROM article_image_assets
            WHERE original_storage_path LIKE 'originals/articles/%') <> 8 THEN
            RAISE EXCEPTION 'Expected 8 migrated article original paths';
        END IF;

        IF EXISTS (SELECT 1 FROM article_image_assets
                   WHERE original_storage_path LIKE 'archive/%') THEN
            RAISE EXCEPTION 'Legacy archive article original paths remain after cutover';
        END IF;
    ELSIF legacy_article_count = 0 AND migrated_article_count = 8 THEN
        NULL;
    ELSE
        RAISE EXCEPTION
            'Unexpected article image inventory before cutover: archive=%, migrated=%',
            legacy_article_count, migrated_article_count;
    END IF;
END
$$;

DO $$
DECLARE
    target_sign_count INTEGER;
    aligned_sign_count INTEGER;
BEGIN
    SELECT COUNT(*)
    INTO target_sign_count
    FROM road_signs
    WHERE sign_code IN ('A39', 'D3a', 'D3b');

    IF target_sign_count = 0 THEN
        NULL;
    ELSIF target_sign_count = 3 THEN
        UPDATE road_signs
        SET image_path = '/images/signs/danger_signs/A39 Twee richtingsverkeer toegelaten na een stuk eenrichtingsverkeer.png'
        WHERE sign_code = 'A39';

        UPDATE road_signs
        SET image_path = '/images/signs/mandatory_signs/D3a Verplicht een van de pijlen te volgen.png'
        WHERE sign_code = 'D3a';

        UPDATE road_signs
        SET image_path = '/images/signs/mandatory_signs/D3b Verplicht een van de pijlen te volgen.png'
        WHERE sign_code = 'D3b';

        SELECT COUNT(*)
        INTO aligned_sign_count
        FROM road_signs
        WHERE (sign_code = 'A39' AND image_path = '/images/signs/danger_signs/A39 Twee richtingsverkeer toegelaten na een stuk eenrichtingsverkeer.png')
           OR (sign_code = 'D3a' AND image_path = '/images/signs/mandatory_signs/D3a Verplicht een van de pijlen te volgen.png')
           OR (sign_code = 'D3b' AND image_path = '/images/signs/mandatory_signs/D3b Verplicht een van de pijlen te volgen.png');

        IF aligned_sign_count <> 3 THEN
            RAISE EXCEPTION 'A39, D3a and D3b image paths were not aligned';
        END IF;
    ELSE
        RAISE EXCEPTION
            'Unexpected road-sign cutover inventory: expected 0 or 3 target signs, found %',
            target_sign_count;
    END IF;
END
$$;
