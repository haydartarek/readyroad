-- Keep immutable publication history while reserving each live localized route.
ALTER TABLE article_publications DROP CONSTRAINT article_publications_status_check;
ALTER TABLE article_publications ADD CONSTRAINT article_publications_status_check
    CHECK (status IN ('PUBLISHED', 'SUPERSEDED'));

DROP INDEX uq_article_publications_language_slug;
CREATE UNIQUE INDEX uq_article_publications_language_slug
    ON article_publications (language, lower(published_slug))
    WHERE status = 'PUBLISHED';
