CREATE TABLE article_publications (
    id BIGSERIAL PRIMARY KEY,
    article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE RESTRICT,
    article_version_id BIGINT NOT NULL REFERENCES article_versions(id) ON DELETE RESTRICT,
    language VARCHAR(8) NOT NULL CHECK (language IN ('AR', 'NL', 'FR', 'EN')),
    approval_task_id BIGINT NOT NULL REFERENCES agent_tasks(id) ON DELETE RESTRICT,
    publication_task_id BIGINT NOT NULL REFERENCES agent_tasks(id) ON DELETE RESTRICT,
    status VARCHAR(32) NOT NULL CHECK (status IN ('PUBLISHED')),
    published_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_article_publications_version UNIQUE (article_version_id),
    CONSTRAINT uq_article_publications_task_language UNIQUE (publication_task_id, language)
);

CREATE INDEX idx_article_publications_article_language
    ON article_publications (article_id, language, published_at DESC);

