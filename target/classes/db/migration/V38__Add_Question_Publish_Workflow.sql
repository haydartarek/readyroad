-- Story D4: Question Publishing Workflow
-- Add status and published_at fields for Belgian compliance enforcement

-- Add status column (defaults to DRAFT)
ALTER TABLE quiz_questions
    ADD COLUMN status VARCHAR(20) DEFAULT 'DRAFT';

-- Add published_at timestamp
ALTER TABLE quiz_questions
    ADD COLUMN published_at TIMESTAMP(6);

-- Create index for filtering published questions
CREATE INDEX idx_quiz_questions_status
    ON quiz_questions(status);

-- Create index for published questions with traffic signs (MySQL doesn't support WHERE clause in CREATE INDEX)
CREATE INDEX idx_quiz_questions_published_with_sign
    ON quiz_questions(status, traffic_sign_id);

-- MySQL doesn't support COMMENT ON COLUMN, use ALTER TABLE instead
ALTER TABLE quiz_questions MODIFY COLUMN status VARCHAR(20) DEFAULT 'DRAFT' COMMENT 'Question lifecycle status: DRAFT allows editing, PUBLISHED is compliance-locked';
ALTER TABLE quiz_questions MODIFY COLUMN published_at TIMESTAMP(6) COMMENT 'Timestamp when question was published and compliance-locked';
