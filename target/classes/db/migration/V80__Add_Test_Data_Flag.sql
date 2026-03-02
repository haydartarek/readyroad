-- V80: Add is_test_data flag to quiz_attempts and quiz_user_answers
-- Used by Admin "Reset Test Data" feature to safely clean up test records
-- without touching real user data. Default FALSE = production/real data.

ALTER TABLE quiz_attempts
    ADD COLUMN is_test_data BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE quiz_user_answers
    ADD COLUMN is_test_data BOOLEAN NOT NULL DEFAULT FALSE;

-- Index for efficient bulk-delete of test records
CREATE INDEX idx_quiz_attempts_test_data ON quiz_attempts (is_test_data);
CREATE INDEX idx_quiz_user_answers_test_data ON quiz_user_answers (is_test_data);
