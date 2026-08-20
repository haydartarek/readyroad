-- Keep local MySQL Admin question editing aligned with PostgreSQL V34.

ALTER TABLE quiz_questions
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
