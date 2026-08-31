CREATE OR REPLACE FUNCTION public.protect_article_version_history()
RETURNS trigger
LANGUAGE plpgsql
AS $function$
BEGIN
    IF TG_OP = 'DELETE' THEN
        IF current_setting(
            'rijvia.allow_article_version_delete',
            true
        ) IS DISTINCT FROM 'on' THEN
            RAISE EXCEPTION 'Article version history cannot be deleted'
                USING ERRCODE = '23503';
        END IF;

        RETURN OLD;
    END IF;

    IF NEW.article_id IS DISTINCT FROM OLD.article_id
       OR NEW.version_number IS DISTINCT FROM OLD.version_number
       OR NEW.language IS DISTINCT FROM OLD.language
       OR NEW.title IS DISTINCT FROM OLD.title
       OR NEW.slug IS DISTINCT FROM OLD.slug
       OR NEW.summary IS DISTINCT FROM OLD.summary
       OR NEW.body IS DISTINCT FROM OLD.body
       OR NEW.metadata IS DISTINCT FROM OLD.metadata
       OR NEW.generation_metadata IS DISTINCT FROM OLD.generation_metadata
       OR NEW.created_at IS DISTINCT FROM OLD.created_at
       OR NEW.created_by IS DISTINCT FROM OLD.created_by THEN
        RAISE EXCEPTION 'Article version content is immutable; create a new version instead'
            USING ERRCODE = '23514';
    END IF;

    RETURN NEW;
END;
$function$;