-- ReadyRoad Theory Exam Blueprint:
-- store category weights as data so active categories can be normalized dynamically.

ALTER TABLE categories
    ADD COLUMN exam_target_weight INTEGER,
    ADD CONSTRAINT chk_categories_exam_target_weight
        CHECK (exam_target_weight IS NULL OR exam_target_weight > 0);

UPDATE categories
SET exam_target_weight = CASE code
    WHEN 'TH01' THEN 14
    WHEN 'TH02' THEN 16
    WHEN 'TH03' THEN 14
    WHEN 'TH04' THEN 10
    WHEN 'TH05' THEN 16
    WHEN 'TH06' THEN 10
    WHEN 'TH07' THEN 10
    WHEN 'TH08' THEN 10
    ELSE NULL
END;

COMMENT ON COLUMN categories.exam_target_weight IS
    'Explicit ReadyRoad theory-exam allocation weight; NULL means inventory-only until configured';
