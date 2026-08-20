-- Theory Exam Engine V2 / Phase 2:
-- make question-option ownership and historical references database-enforced.

ALTER TABLE quiz_answer_options
    ADD CONSTRAINT uq_quiz_answer_option_question_id
    UNIQUE (question_id, id);

CREATE UNIQUE INDEX uq_quiz_answer_options_one_active_correct
    ON quiz_answer_options (question_id)
    WHERE is_active = TRUE AND is_correct = TRUE;

ALTER TABLE exam_simulation_questions
    DROP CONSTRAINT fk_esq_question;

ALTER TABLE exam_simulation_answers
    DROP CONSTRAINT fk_esa_question,
    DROP CONSTRAINT fk_esa_option,
    DROP CONSTRAINT fk_esa_correct_option;

ALTER TABLE exam_simulation_questions
    ADD CONSTRAINT fk_esq_question
    FOREIGN KEY (question_id)
    REFERENCES quiz_questions (id)
    ON DELETE RESTRICT
    NOT VALID;

ALTER TABLE exam_simulation_answers
    ADD CONSTRAINT fk_esa_question
    FOREIGN KEY (question_id)
    REFERENCES quiz_questions (id)
    ON DELETE RESTRICT
    NOT VALID,
    ADD CONSTRAINT fk_esa_selected_option_question
    FOREIGN KEY (question_id, selected_option_id)
    REFERENCES quiz_answer_options (question_id, id)
    ON DELETE RESTRICT
    NOT VALID,
    ADD CONSTRAINT fk_esa_correct_option_question
    FOREIGN KEY (question_id, correct_option_id)
    REFERENCES quiz_answer_options (question_id, id)
    ON DELETE RESTRICT
    NOT VALID;

ALTER TABLE exam_simulation_questions
    VALIDATE CONSTRAINT fk_esq_question;

ALTER TABLE exam_simulation_answers
    VALIDATE CONSTRAINT fk_esa_question,
    VALIDATE CONSTRAINT fk_esa_selected_option_question,
    VALIDATE CONSTRAINT fk_esa_correct_option_question;

COMMENT ON CONSTRAINT fk_esa_selected_option_question ON exam_simulation_answers IS
    'Selected option must be owned by the answered theory question.';

COMMENT ON CONSTRAINT fk_esa_correct_option_question ON exam_simulation_answers IS
    'Historical correct option must be owned by the answered theory question.';
