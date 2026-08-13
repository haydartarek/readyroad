-- PART-51: reject stale Admin edits without changing question or exam history.
ALTER TABLE quiz_questions
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
