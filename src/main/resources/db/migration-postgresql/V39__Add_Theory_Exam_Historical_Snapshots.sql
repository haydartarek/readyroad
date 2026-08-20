-- Forward-only historical content snapshots for newly created theory exams.
-- Existing attempts intentionally remain NULL; no historical content is fabricated.

ALTER TABLE exam_simulation_questions
    ADD COLUMN historical_snapshot_version SMALLINT,
    ADD COLUMN historical_snapshot_json TEXT;

ALTER TABLE exam_simulation_questions
    ADD CONSTRAINT chk_exam_question_historical_snapshot
    CHECK (
        (historical_snapshot_version IS NULL AND historical_snapshot_json IS NULL)
        OR
        (historical_snapshot_version = 1
            AND historical_snapshot_json IS NOT NULL
            AND BTRIM(historical_snapshot_json) <> ''
            AND JSONB_TYPEOF(historical_snapshot_json::jsonb) = 'object'
            AND historical_snapshot_json::jsonb ->> 'version' = '1'
            AND (historical_snapshot_json::jsonb ->> 'questionId')::bigint = question_id)
    );

COMMENT ON COLUMN exam_simulation_questions.historical_snapshot_json IS
    'Immutable localized question, option, category and difficulty content captured when the exam is created.';
