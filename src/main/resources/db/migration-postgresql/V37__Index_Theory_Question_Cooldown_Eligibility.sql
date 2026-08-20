-- Theory Exam Engine V2 / Phase 3B:
-- support per-user eight-hour eligibility without scanning history in memory.

CREATE INDEX idx_quiz_questions_exam_eligibility
    ON quiz_questions (difficulty_level, id)
    WHERE is_active = TRUE AND status = 'PUBLISHED';

CREATE INDEX idx_uqh_theory_presentation_eligibility
    ON user_question_history (user_id, question_ref_id, last_presented_at)
    WHERE question_type = 'THEORY';
