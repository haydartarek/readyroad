-- Distinguish categories that are actually used by the theoretical quiz bank
-- without changing the canonical traffic-sign category records.

ALTER TABLE categories
    ADD COLUMN content_scope VARCHAR(30) NOT NULL DEFAULT 'TRAFFIC_SIGN';

UPDATE categories category
SET content_scope = 'BOTH'
WHERE EXISTS (
    SELECT 1
    FROM quiz_questions question
    WHERE question.category_id = category.id
);

ALTER TABLE categories
    ADD CONSTRAINT chk_categories_content_scope
    CHECK (content_scope IN ('TRAFFIC_SIGN', 'THEORETICAL_EXAM', 'BOTH'));

CREATE INDEX idx_categories_content_scope_active
    ON categories (content_scope, is_active, display_order);
