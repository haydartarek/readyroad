-- Theory Exam Engine V2 / Phase 3B portability indexes.

CREATE INDEX idx_quiz_questions_exam_eligibility
    ON quiz_questions (difficulty_level, is_active, status, id);

CREATE INDEX idx_uqh_theory_presentation_eligibility
    ON user_question_history (user_id, question_ref_id, question_type, last_presented_at);
