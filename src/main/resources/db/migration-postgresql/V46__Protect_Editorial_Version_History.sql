CREATE FUNCTION protect_article_version_history()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF TG_OP = 'DELETE' THEN
        RAISE EXCEPTION 'Article version history cannot be deleted'
            USING ERRCODE = '23503';
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
$$;

CREATE TRIGGER trg_protect_article_version_history
    BEFORE UPDATE OR DELETE ON article_versions
    FOR EACH ROW
    EXECUTE FUNCTION protect_article_version_history();
