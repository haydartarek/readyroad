-- Optimistic locking for ExamSimulation (cancel vs complete race protection)
ALTER TABLE exam_simulations ADD COLUMN version INTEGER NOT NULL DEFAULT 0;
