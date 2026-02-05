-- V73: Add Answer Options for Test Questions
-- Adding 4 options (A, B, C, D) for each question

-- This script adds answer options dynamically based on question IDs
-- We'll add generic options for testing purposes

-- For questions about speed limits
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT id, '50 كم/ساعة', '50 km/h', '50 km/u', '50 km/h', 1, 1
FROM quiz_questions
WHERE question_en LIKE '%speed limit in residential%'
AND NOT EXISTS (SELECT 1 FROM quiz_answer_options WHERE question_id = quiz_questions.id);

INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT id, '70 كم/ساعة', '70 km/h', '70 km/u', '70 km/h', 0, 2
FROM quiz_questions
WHERE question_en LIKE '%speed limit in residential%';

INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT id, '90 كم/ساعة', '90 km/h', '90 km/u', '90 km/h', 0, 3
FROM quiz_questions
WHERE question_en LIKE '%speed limit in residential%';

INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT id, '30 كم/ساعة', '30 km/h', '30 km/u', '30 km/h', 0, 4
FROM quiz_questions
WHERE question_en LIKE '%speed limit in residential%';

-- For red light question
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT id, 'دائماً', 'Always', 'Altijd', 'Toujours', 1, 1
FROM quiz_questions
WHERE question_en LIKE '%stop at a red light%'
AND NOT EXISTS (SELECT 1 FROM quiz_answer_options WHERE question_id = quiz_questions.id);

INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT id, 'إذا لم يكن هناك سيارات', 'If there are no cars', 'Als er geen auto''s zijn', 'S''il n''y a pas de voitures', 0, 2
FROM quiz_questions
WHERE question_en LIKE '%stop at a red light%';

INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT id, 'فقط في النهار', 'Only during daytime', 'Alleen overdag', 'Seulement pendant la journée', 0, 3
FROM quiz_questions
WHERE question_en LIKE '%stop at a red light%';

INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT id, 'اختياري', 'Optional', 'Optioneel', 'Facultatif', 0, 4
FROM quiz_questions
WHERE question_en LIKE '%stop at a red light%';

-- Add generic options for all remaining questions without options
-- Option A (usually correct)
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT q.id, 'الخيار أ', 'Option A', 'Optie A', 'Option A', 1, 1
FROM quiz_questions q
WHERE NOT EXISTS (
    SELECT 1 FROM quiz_answer_options qao WHERE qao.question_id = q.id
)
LIMIT 50;

-- Option B
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT q.id, 'الخيار ب', 'Option B', 'Optie B', 'Option B', 0, 2
FROM quiz_questions q
WHERE EXISTS (
    SELECT 1 FROM quiz_answer_options qao
    WHERE qao.question_id = q.id AND qao.display_order = 1
)
AND NOT EXISTS (
    SELECT 1 FROM quiz_answer_options qao
    WHERE qao.question_id = q.id AND qao.display_order = 2
);

-- Option C
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT q.id, 'الخيار ج', 'Option C', 'Optie C', 'Option C', 0, 3
FROM quiz_questions q
WHERE EXISTS (
    SELECT 1 FROM quiz_answer_options qao
    WHERE qao.question_id = q.id AND qao.display_order = 2
)
AND NOT EXISTS (
    SELECT 1 FROM quiz_answer_options qao
    WHERE qao.question_id = q.id AND qao.display_order = 3
);

-- Option D
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT q.id, 'الخيار د', 'Option D', 'Optie D', 'Option D', 0, 4
FROM quiz_questions q
WHERE EXISTS (
    SELECT 1 FROM quiz_answer_options qao
    WHERE qao.question_id = q.id AND qao.display_order = 3
)
AND NOT EXISTS (
    SELECT 1 FROM quiz_answer_options qao
    WHERE qao.question_id = q.id AND qao.display_order = 4
);
