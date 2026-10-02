-- Permanently remove the legacy lesson bullet-points feature.
--
-- This migration:
-- 1. Removes bulletPointsRaw from mutable lesson drafts.
-- 2. Removes bulletPointsRaw from all immutable lesson-version documents
--    using the existing guarded maintenance bypass.
-- 3. Drops the four legacy lesson_pages bullet columns.

DO $$
DECLARE
    bullet_column_count INTEGER;
BEGIN
    SELECT COUNT(*)
    INTO bullet_column_count
    FROM information_schema.columns
    WHERE table_schema = 'readyroad'
      AND table_name = 'lesson_pages'
      AND column_name IN (
          'bullet_points_nl',
          'bullet_points_en',
          'bullet_points_fr',
          'bullet_points_ar'
      );

    IF bullet_column_count <> 4 THEN
        RAISE EXCEPTION
            'Expected 4 legacy lesson bullet columns, found %',
            bullet_column_count;
    END IF;
END
$$;


UPDATE readyroad.lesson_drafts AS d
SET document = jsonb_set(
    d.document,
    '{pages}',
    (
        SELECT jsonb_agg(
            CASE
                WHEN jsonb_typeof(p.value) = 'object'
                    THEN p.value - 'bulletPointsRaw'
                ELSE p.value
            END
            ORDER BY p.ordinality
        )
        FROM jsonb_array_elements(d.document -> 'pages')
             WITH ORDINALITY AS p(value, ordinality)
    ),
    false
)
WHERE jsonb_typeof(d.document -> 'pages') = 'array'
  AND EXISTS (
      SELECT 1
      FROM jsonb_array_elements(d.document -> 'pages') AS p(value)
      WHERE jsonb_typeof(p.value) = 'object'
        AND p.value ? 'bulletPointsRaw'
  );


SELECT set_config(
    'readyroad.media_purge',
    'on',
    true
);


UPDATE readyroad.lesson_versions AS v
SET document = jsonb_set(
    v.document,
    '{pages}',
    (
        SELECT jsonb_agg(
            CASE
                WHEN jsonb_typeof(p.value) = 'object'
                    THEN p.value - 'bulletPointsRaw'
                ELSE p.value
            END
            ORDER BY p.ordinality
        )
        FROM jsonb_array_elements(v.document -> 'pages')
             WITH ORDINALITY AS p(value, ordinality)
    ),
    false
)
WHERE jsonb_typeof(v.document -> 'pages') = 'array'
  AND EXISTS (
      SELECT 1
      FROM jsonb_array_elements(v.document -> 'pages') AS p(value)
      WHERE jsonb_typeof(p.value) = 'object'
        AND p.value ? 'bulletPointsRaw'
  );


SELECT set_config(
    'readyroad.media_purge',
    'off',
    true
);


DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM readyroad.lesson_drafts AS d
        WHERE jsonb_typeof(d.document -> 'pages') = 'array'
          AND EXISTS (
              SELECT 1
              FROM jsonb_array_elements(d.document -> 'pages') AS p(value)
              WHERE jsonb_typeof(p.value) = 'object'
                AND p.value ? 'bulletPointsRaw'
          )
    ) THEN
        RAISE EXCEPTION
            'bulletPointsRaw still exists in lesson_drafts';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM readyroad.lesson_versions AS v
        WHERE jsonb_typeof(v.document -> 'pages') = 'array'
          AND EXISTS (
              SELECT 1
              FROM jsonb_array_elements(v.document -> 'pages') AS p(value)
              WHERE jsonb_typeof(p.value) = 'object'
                AND p.value ? 'bulletPointsRaw'
          )
    ) THEN
        RAISE EXCEPTION
            'bulletPointsRaw still exists in lesson_versions';
    END IF;
END
$$;


ALTER TABLE readyroad.lesson_pages
    DROP COLUMN bullet_points_nl,
    DROP COLUMN bullet_points_en,
    DROP COLUMN bullet_points_fr,
    DROP COLUMN bullet_points_ar;