-- V112: Belgian driving exam standard: max 3 options per question.
-- Make option3 and option4 columns nullable so new questions can have 2 or 3 options.
-- Existing data with 4 options is preserved (option4 columns still have data for old rows).

ALTER TABLE exam_questions
    MODIFY COLUMN option3_en TEXT NULL,
    MODIFY COLUMN option3_ar TEXT NULL,
    MODIFY COLUMN option3_nl TEXT NULL,
    MODIFY COLUMN option3_fr TEXT NULL,
    MODIFY COLUMN option4_en TEXT NULL,
    MODIFY COLUMN option4_ar TEXT NULL,
    MODIFY COLUMN option4_nl TEXT NULL,
    MODIFY COLUMN option4_fr TEXT NULL;
