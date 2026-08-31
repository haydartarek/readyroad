ALTER TABLE article_topics
    ADD COLUMN keyword_cluster_id BIGINT
        REFERENCES article_keyword_clusters(id) ON DELETE RESTRICT;

CREATE INDEX idx_article_topics_keyword_cluster
    ON article_topics (keyword_cluster_id)
    WHERE keyword_cluster_id IS NOT NULL;
