ALTER TABLE article_publications
    ADD COLUMN published_slug VARCHAR(255);

UPDATE article_publications publication
SET published_slug = version.slug
FROM article_versions version
WHERE version.id = publication.article_version_id;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM article_publications
        WHERE published_slug IS NULL OR btrim(published_slug) = ''
    ) THEN
        RAISE EXCEPTION 'Published article version is missing a route slug';
    END IF;
END
$$;

ALTER TABLE article_publications
    ALTER COLUMN published_slug SET NOT NULL,
    ADD CONSTRAINT chk_article_publications_slug_safe CHECK (
        published_slug = btrim(published_slug)
        AND published_slug !~ '[[:space:]/?#]'
        AND position(E'\\\\' IN published_slug) = 0
    );

CREATE UNIQUE INDEX uq_article_publications_language_slug
    ON article_publications (language, lower(published_slug));

CREATE FUNCTION protect_article_publication_route()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.article_id IS DISTINCT FROM OLD.article_id
       OR NEW.article_version_id IS DISTINCT FROM OLD.article_version_id
       OR NEW.language IS DISTINCT FROM OLD.language
       OR NEW.approval_task_id IS DISTINCT FROM OLD.approval_task_id
       OR NEW.publication_task_id IS DISTINCT FROM OLD.publication_task_id
       OR NEW.published_slug IS DISTINCT FROM OLD.published_slug
       OR NEW.published_at IS DISTINCT FROM OLD.published_at THEN
        RAISE EXCEPTION 'Article publication routes are immutable'
            USING ERRCODE = '23514';
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_protect_article_publication_route
    BEFORE UPDATE ON article_publications
    FOR EACH ROW
    EXECUTE FUNCTION protect_article_publication_route();
