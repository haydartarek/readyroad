-- ─────────────────────────────────────────────────────────────────────────────
-- V100 — Increase sign_questions.question_ref from VARCHAR(20) to VARCHAR(100)
-- Some sign codes (e.g. onderbord_giii_aquaplaning) produce refs longer than 20 chars.
-- ─────────────────────────────────────────────────────────────────────────────
ALTER TABLE sign_questions
    MODIFY COLUMN question_ref VARCHAR(100) NOT NULL;
