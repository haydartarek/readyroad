ALTER TABLE sign_exams
    ALTER COLUMN passing_score SET DEFAULT 6,
    ALTER COLUMN total_questions SET DEFAULT 8,
    ALTER COLUMN easy_count SET DEFAULT 3,
    ALTER COLUMN medium_count SET DEFAULT 3,
    ALTER COLUMN hard_count SET DEFAULT 2;

UPDATE quiz_answer_options
SET display_order = 99
WHERE id IN (566,571,589,631,634,637,640,643,646,649);

UPDATE sign_choices
SET display_order = 99
WHERE id IN (9090,9135,9158,9181);
