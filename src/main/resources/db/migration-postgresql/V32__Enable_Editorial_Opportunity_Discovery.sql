ALTER TABLE article_topics
    DROP CONSTRAINT IF EXISTS article_topics_official_backlog_order_check,
    DROP CONSTRAINT IF EXISTS article_topics_cluster_order_check,
    DROP CONSTRAINT IF EXISTS article_topics_source_type_check;

ALTER TABLE article_topics
    ALTER COLUMN cluster_order DROP NOT NULL,
    ALTER COLUMN cluster_key DROP NOT NULL,
    ALTER COLUMN cluster_name DROP NOT NULL;

ALTER TABLE article_topics
    ADD CONSTRAINT chk_article_topics_source_type
        CHECK (source_type IN ('OFFICIAL_STRATEGIC_BACKLOG', 'SEARCH_CONSOLE_OPPORTUNITY')),
    ADD CONSTRAINT chk_article_topics_backlog_order
        CHECK (
            (source_type = 'OFFICIAL_STRATEGIC_BACKLOG'
                AND official_backlog_order BETWEEN 1 AND 40
                AND cluster_order BETWEEN 1 AND 6
                AND cluster_key IS NOT NULL
                AND cluster_name IS NOT NULL)
            OR
            (source_type = 'SEARCH_CONSOLE_OPPORTUNITY'
                AND official_backlog_order > 40
                AND cluster_order IS NULL
                AND cluster_key IS NULL
                AND cluster_name IS NULL)
        );

CREATE UNIQUE INDEX uq_article_topics_source_opportunity
    ON article_topics (source_opportunity_id)
    WHERE source_opportunity_id IS NOT NULL;
