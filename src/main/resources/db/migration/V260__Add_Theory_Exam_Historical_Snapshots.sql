-- Forward-only historical content snapshots for newly created theory exams.
-- Existing attempts intentionally remain NULL; no historical content is fabricated.

ALTER TABLE exam_simulation_questions
    ADD COLUMN historical_snapshot_version SMALLINT NULL,
    ADD COLUMN historical_snapshot_json LONGTEXT NULL;
