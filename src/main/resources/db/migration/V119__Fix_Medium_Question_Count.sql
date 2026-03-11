-- V119: Fix MEDIUM question shortage.
-- Exam requires 20 EASY + 20 MEDIUM + 10 HARD = 50 questions.
-- Current state: 20 EASY, 18 MEDIUM, 12 HARD.
-- We have 2 extra HARD questions, so we reclassify 2 borderline HARD -> MEDIUM.
-- Selected questions are moderate-difficulty conceptual questions:
--   ID 103: Supplementary panel time-period format (G category) - conceptually MEDIUM
--   ID 136: When does a speed limit sign end? (C category) - conceptually MEDIUM
-- After change: 20 EASY, 20 MEDIUM, 10 HARD = 50 total (correct distribution).

UPDATE quiz_questions
SET difficulty_level = 'MEDIUM'
WHERE id IN (103, 136)
  AND difficulty_level = 'HARD'
  AND is_active = 1;
