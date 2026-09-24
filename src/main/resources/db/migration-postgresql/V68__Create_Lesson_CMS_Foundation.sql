-- ============================================================
-- RijVia Lesson CMS Foundation
--
-- Published runtime compatibility:
--   lessons + lesson_pages remain the public published model.
--
-- CMS layer:
--   lesson_categories   = published lesson/category mapping
--   lesson_drafts       = one mutable working draft per lesson
--   lesson_versions     = immutable publication history
--   lesson_media_assets = immutable uploaded lesson image assets
--
-- Existing lesson IDs and user_lesson_progress are untouched.
-- ============================================================


-- ============================================================
-- 1. PUBLISHED LESSON <-> THEORY CATEGORY MAPPING
-- ============================================================

CREATE TABLE lesson_categories (
    lesson_id BIGINT NOT NULL
        REFERENCES lessons(id)
        ON DELETE CASCADE,

    category_id BIGINT NOT NULL
        REFERENCES categories(id)
        ON DELETE RESTRICT,

    is_primary BOOLEAN NOT NULL DEFAULT FALSE,

    display_order INTEGER NOT NULL DEFAULT 0
        CHECK (display_order >= 0),

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_lesson_categories
        PRIMARY KEY (lesson_id, category_id)
);

CREATE INDEX idx_lesson_categories_category
    ON lesson_categories (category_id, lesson_id);

CREATE INDEX idx_lesson_categories_lesson_order
    ON lesson_categories (lesson_id, display_order, category_id);

CREATE UNIQUE INDEX uq_lesson_categories_primary
    ON lesson_categories (lesson_id)
    WHERE is_primary = TRUE;


-- Only active theoretical-exam categories may be assigned to lessons.
CREATE OR REPLACE FUNCTION validate_lesson_theory_category()
RETURNS TRIGGER
LANGUAGE plpgsql
SET search_path = pg_catalog, readyroad
AS $$
DECLARE
    category_code VARCHAR;
    category_scope VARCHAR;
    category_active BOOLEAN;
BEGIN
    SELECT code, content_scope, is_active
      INTO category_code, category_scope, category_active
      FROM readyroad.categories
     WHERE id = NEW.category_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION
            'Lesson category % does not exist',
            NEW.category_id
            USING ERRCODE = '23503';
    END IF;

    IF category_code !~ '^TH[0-9]{2}$'
       OR category_scope IS DISTINCT FROM 'THEORETICAL_EXAM'
       OR category_active IS DISTINCT FROM TRUE THEN

        RAISE EXCEPTION
            'Lessons may only use active canonical THxx categories; category_id=% code=% scope=% active=%',
            NEW.category_id,
            category_code,
            category_scope,
            category_active
            USING ERRCODE = '23514';
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_validate_lesson_theory_category
BEFORE INSERT OR UPDATE OF category_id
ON lesson_categories
FOR EACH ROW
EXECUTE FUNCTION validate_lesson_theory_category();


CREATE TRIGGER trg_lesson_categories_updated_at
BEFORE UPDATE ON lesson_categories
FOR EACH ROW
EXECUTE FUNCTION set_updated_at_column();



-- ============================================================
-- 2. MUTABLE LESSON DRAFT
-- ============================================================

CREATE TABLE lesson_drafts (
    lesson_id BIGINT PRIMARY KEY
        REFERENCES lessons(id)
        ON DELETE CASCADE,

    -- Version from which this draft was created.
    base_version_number INTEGER NOT NULL
        CHECK (base_version_number > 0),

    -- Optimistic revision for concurrent admin edits.
    revision BIGINT NOT NULL DEFAULT 0
        CHECK (revision >= 0),

    -- Entire editable CMS document:
    -- multilingual content, structured educational blocks,
    -- SEO fields, image references and pending category mapping.
    document JSONB NOT NULL,

    created_by_user_id BIGINT
        REFERENCES users(id)
        ON DELETE SET NULL,

    updated_by_user_id BIGINT
        REFERENCES users(id)
        ON DELETE SET NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_lesson_drafts_document_object
        CHECK (jsonb_typeof(document) = 'object')
);

CREATE INDEX idx_lesson_drafts_updated
    ON lesson_drafts (updated_at DESC, lesson_id);

CREATE TRIGGER trg_lesson_drafts_updated_at
BEFORE UPDATE ON lesson_drafts
FOR EACH ROW
EXECUTE FUNCTION set_updated_at_column();



-- ============================================================
-- 3. IMMUTABLE PUBLISHED VERSION HISTORY
-- ============================================================

CREATE TABLE lesson_versions (
    id BIGSERIAL PRIMARY KEY,

    lesson_id BIGINT NOT NULL
        REFERENCES lessons(id)
        ON DELETE RESTRICT,

    version_number INTEGER NOT NULL
        CHECK (version_number > 0),

    document JSONB NOT NULL,

    source VARCHAR(32) NOT NULL DEFAULT 'CMS'
        CHECK (source IN ('BASELINE', 'CMS')),

    change_note TEXT,

    published_by_user_id BIGINT
        REFERENCES users(id)
        ON DELETE SET NULL,

    published_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_lesson_versions_identity
        UNIQUE (lesson_id, version_number),

    CONSTRAINT chk_lesson_versions_document_object
        CHECK (jsonb_typeof(document) = 'object')
);

CREATE INDEX idx_lesson_versions_history
    ON lesson_versions (
        lesson_id,
        version_number DESC
    );


-- Published history must never be rewritten.
CREATE OR REPLACE FUNCTION protect_lesson_version_history()
RETURNS TRIGGER
LANGUAGE plpgsql
SET search_path = pg_catalog, readyroad
AS $$
BEGIN
    IF TG_OP = 'DELETE' THEN
        RAISE EXCEPTION
            'Lesson version history cannot be deleted'
            USING ERRCODE = '23503';
    END IF;

    RAISE EXCEPTION
        'Lesson version history is immutable; create a new version instead'
        USING ERRCODE = '23514';
END;
$$;

CREATE TRIGGER trg_protect_lesson_version_history
BEFORE UPDATE OR DELETE
ON lesson_versions
FOR EACH ROW
EXECUTE FUNCTION protect_lesson_version_history();



-- ============================================================
-- 4. LESSON MEDIA ASSETS
-- ============================================================

CREATE TABLE lesson_media_assets (
    id BIGSERIAL PRIMARY KEY,

    lesson_id BIGINT NOT NULL
        REFERENCES lessons(id)
        ON DELETE RESTRICT,

    -- Environment-independent relative key, for example:
    -- lessons/les-17/overtaking-curve-01.webp
    storage_key VARCHAR(512) NOT NULL UNIQUE,

    storage_provider VARCHAR(32) NOT NULL DEFAULT 'LOCAL'
        CHECK (storage_provider IN ('LOCAL', 'OBJECT_STORAGE')),

    original_filename TEXT NOT NULL,

    mime_type VARCHAR(100) NOT NULL
        CHECK (mime_type LIKE 'image/%'),

    size_bytes BIGINT NOT NULL
        CHECK (size_bytes > 0),

    width INTEGER
        CHECK (width IS NULL OR width > 0),

    height INTEGER
        CHECK (height IS NULL OR height > 0),

    sha256 CHAR(64)
        CHECK (
            sha256 IS NULL
            OR sha256 ~ '^[0-9A-Fa-f]{64}$'
        ),

    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE'
        CHECK (status IN ('ACTIVE', 'ARCHIVED')),

    uploaded_by_user_id BIGINT
        REFERENCES users(id)
        ON DELETE SET NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    archived_at TIMESTAMPTZ,

    CONSTRAINT chk_lesson_media_archive_state
        CHECK (
            (status = 'ACTIVE' AND archived_at IS NULL)
            OR
            (status = 'ARCHIVED' AND archived_at IS NOT NULL)
        )
);

CREATE INDEX idx_lesson_media_assets_lesson
    ON lesson_media_assets (
        lesson_id,
        status,
        id
    );

CREATE INDEX idx_lesson_media_assets_sha256
    ON lesson_media_assets (sha256)
    WHERE sha256 IS NOT NULL;


-- Media referenced by historical lesson versions must not be hard-deleted.
CREATE OR REPLACE FUNCTION prevent_lesson_media_asset_delete()
RETURNS TRIGGER
LANGUAGE plpgsql
SET search_path = pg_catalog, readyroad
AS $$
BEGIN
    RAISE EXCEPTION
        'Lesson media assets cannot be hard-deleted; archive the asset instead'
        USING ERRCODE = '23503';
END;
$$;

CREATE TRIGGER trg_prevent_lesson_media_asset_delete
BEFORE DELETE
ON lesson_media_assets
FOR EACH ROW
EXECUTE FUNCTION prevent_lesson_media_asset_delete();



-- ============================================================
-- 5. CAPTURE CURRENT 30 LESSONS AS BASELINE VERSION 1
-- ============================================================

INSERT INTO lesson_versions (
    lesson_id,
    version_number,
    document,
    source,
    change_note,
    published_by_user_id,
    published_at
)
SELECT
    l.id,
    1,

    jsonb_build_object(
        'schemaVersion', 1,

        'lesson',
        jsonb_build_object(
            'lessonCode', l.lesson_code,

            'title',
            jsonb_build_object(
                'ar', l.title_ar,
                'nl', l.title_nl,
                'fr', l.title_fr,
                'en', l.title_en
            ),

            'description',
            jsonb_build_object(
                'ar', l.description_ar,
                'nl', l.description_nl,
                'fr', l.description_fr,
                'en', l.description_en
            ),

            'icon', l.icon,
            'displayOrder', l.display_order,
            'estimatedMinutes', l.estimated_minutes,
            'isActive', l.is_active
        ),

        'pages',
        COALESCE(
            (
                SELECT jsonb_agg(
                    jsonb_build_object(
                        'pageNumber', p.page_number,

                        'title',
                        jsonb_build_object(
                            'ar', p.title_ar,
                            'nl', p.title_nl,
                            'fr', p.title_fr,
                            'en', p.title_en
                        ),

                        'content',
                        jsonb_build_object(
                            'ar', p.content_ar,
                            'nl', p.content_nl,
                            'fr', p.content_fr,
                            'en', p.content_en
                        ),

                        -- Stored exactly as current legacy values.
                        -- Application code may parse these JSON-array strings.
                        'bulletPointsRaw',
                        jsonb_build_object(
                            'ar', p.bullet_points_ar,
                            'nl', p.bullet_points_nl,
                            'fr', p.bullet_points_fr,
                            'en', p.bullet_points_en
                        )
                    )
                    ORDER BY p.page_number
                )
                FROM lesson_pages p
                WHERE p.lesson_id = l.id
            ),
            '[]'::jsonb
        ),

        'structuredSections',
        '[]'::jsonb,

        'seo',
        jsonb_build_object(
            'ar', '{}'::jsonb,
            'nl', '{}'::jsonb,
            'fr', '{}'::jsonb,
            'en', '{}'::jsonb
        ),

        'media',
        '[]'::jsonb,

        'categoryLinks',
        '[]'::jsonb
    ),

    'BASELINE',
    'Pre-CMS published lesson baseline',
    NULL,
    COALESCE(
        l.updated_at AT TIME ZONE 'UTC',
        l.created_at AT TIME ZONE 'UTC',
        CURRENT_TIMESTAMP
    )

FROM lessons l

ON CONFLICT (lesson_id, version_number)
DO NOTHING;



-- ============================================================
-- 6. BASELINE VALIDATION
-- ============================================================

DO $$
DECLARE
    lesson_count INTEGER;
    baseline_count INTEGER;
BEGIN
    SELECT COUNT(*)
      INTO lesson_count
      FROM lessons;

    SELECT COUNT(*)
      INTO baseline_count
      FROM lesson_versions
     WHERE version_number = 1
       AND source = 'BASELINE';

    IF lesson_count <> baseline_count THEN
        RAISE EXCEPTION
            'Lesson CMS baseline mismatch: lessons=% baseline_versions=%',
            lesson_count,
            baseline_count;
    END IF;
END;
$$;



-- ============================================================
-- 7. SECURITY
--
-- Direct Supabase API roles are not used by RijVia clients.
-- Spring Boot accesses the schema through the owning app role.
-- ============================================================

ALTER TABLE lesson_categories
    ENABLE ROW LEVEL SECURITY;

ALTER TABLE lesson_drafts
    ENABLE ROW LEVEL SECURITY;

ALTER TABLE lesson_versions
    ENABLE ROW LEVEL SECURITY;

ALTER TABLE lesson_media_assets
    ENABLE ROW LEVEL SECURITY;


DO $$
DECLARE
    api_role TEXT;
    protected_table TEXT;
    protected_sequence TEXT;
BEGIN

    FOREACH api_role IN ARRAY ARRAY[
        'anon',
        'authenticated',
        'service_role'
    ]
    LOOP

        IF EXISTS (
            SELECT 1
            FROM pg_roles
            WHERE rolname = api_role
        ) THEN

            FOREACH protected_table IN ARRAY ARRAY[
                'lesson_categories',
                'lesson_drafts',
                'lesson_versions',
                'lesson_media_assets'
            ]
            LOOP

                EXECUTE format(
                    'REVOKE ALL ON TABLE readyroad.%I FROM %I',
                    protected_table,
                    api_role
                );

            END LOOP;


            FOREACH protected_sequence IN ARRAY ARRAY[
                'lesson_versions_id_seq',
                'lesson_media_assets_id_seq'
            ]
            LOOP

                EXECUTE format(
                    'REVOKE ALL ON SEQUENCE readyroad.%I FROM %I',
                    protected_sequence,
                    api_role
                );

            END LOOP;

        END IF;

    END LOOP;

END;
$$;


-- ============================================================
-- END V68
-- ============================================================
