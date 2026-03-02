-- V83: Drop lessons and practice_questions tables
-- Lessons are now served from lessons_content.json on the frontend (no DB/REST).

DROP TABLE IF EXISTS practice_questions;
DROP TABLE IF EXISTS lessons;
