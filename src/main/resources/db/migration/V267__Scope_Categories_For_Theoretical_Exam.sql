-- Keep local MySQL category scope aligned with PostgreSQL V17.

ALTER TABLE categories
    ADD COLUMN content_scope VARCHAR(30) NOT NULL DEFAULT 'TRAFFIC_SIGN';

UPDATE categories category_row
SET content_scope = 'BOTH'
WHERE EXISTS (
    SELECT 1
    FROM quiz_questions question_row
    WHERE question_row.category_id = category_row.id
);

ALTER TABLE categories
    ADD CONSTRAINT chk_categories_content_scope
        CHECK (content_scope IN ('TRAFFIC_SIGN', 'THEORETICAL_EXAM', 'BOTH'));

CREATE INDEX idx_categories_content_scope_active
    ON categories (content_scope, is_active, display_order);
