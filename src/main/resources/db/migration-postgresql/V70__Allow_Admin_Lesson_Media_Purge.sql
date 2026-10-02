-- Allow the dedicated ADMIN media-purge transaction to override the normal
-- immutability guards without weakening those guards for regular CMS writes.

CREATE OR REPLACE FUNCTION protect_lesson_version_history()
RETURNS TRIGGER
LANGUAGE plpgsql
SET search_path = pg_catalog, readyroad
AS $$
BEGIN
    IF current_setting('readyroad.media_purge', true) = 'on' THEN
        IF TG_OP = 'DELETE' THEN
            RETURN OLD;
        END IF;
        RETURN NEW;
    END IF;

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

CREATE OR REPLACE FUNCTION prevent_lesson_media_asset_delete()
RETURNS TRIGGER
LANGUAGE plpgsql
SET search_path = pg_catalog, readyroad
AS $$
BEGIN
    IF current_setting('readyroad.media_purge', true) = 'on' THEN
        RETURN OLD;
    END IF;

    RAISE EXCEPTION
        'Lesson media assets cannot be hard-deleted; archive the asset instead'
        USING ERRCODE = '23503';
END;
$$;