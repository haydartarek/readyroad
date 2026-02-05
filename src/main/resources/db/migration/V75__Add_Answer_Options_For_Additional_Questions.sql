-- V75: Add Answer Options for Additional 50 Questions
-- Adding 4 options (A, B, C, D) for each new question from V74

-- Speed limit questions (5 questions)
-- Question: Maximum speed inside cities
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT id, '50 كم/ساعة', '50 km/h', '50 km/u', '50 km/h', 1, 1
FROM quiz_questions
WHERE question_en = 'What is the maximum speed limit inside cities in Belgium?'
AND NOT EXISTS (SELECT 1 FROM quiz_answer_options WHERE question_id = quiz_questions.id);

INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT id, '60 كم/ساعة', '60 km/h', '60 km/u', '60 km/h', 0, 2
FROM quiz_questions WHERE question_en = 'What is the maximum speed limit inside cities in Belgium?';

INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT id, '70 كم/ساعة', '70 km/h', '70 km/u', '70 km/h', 0, 3
FROM quiz_questions WHERE question_en = 'What is the maximum speed limit inside cities in Belgium?';

INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT id, '30 كم/ساعة', '30 km/h', '30 km/u', '30 km/h', 0, 4
FROM quiz_questions WHERE question_en = 'What is the maximum speed limit inside cities in Belgium?';

-- Question: Maximum speed on highways
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT id, '120 كم/ساعة', '120 km/h', '120 km/u', '120 km/h', 1, 1
FROM quiz_questions WHERE question_en = 'What is the maximum speed on highways in dry weather?'
AND NOT EXISTS (SELECT 1 FROM quiz_answer_options WHERE question_id = quiz_questions.id);

INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT id, '100 كم/ساعة', '100 km/h', '100 km/u', '100 km/h', 0, 2
FROM quiz_questions WHERE question_en = 'What is the maximum speed on highways in dry weather?';

INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT id, '90 كم/ساعة', '90 km/h', '90 km/u', '90 km/h', 0, 3
FROM quiz_questions WHERE question_en = 'What is the maximum speed on highways in dry weather?';

INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT id, '130 كم/ساعة', '130 km/h', '130 km/u', '130 km/h', 0, 4
FROM quiz_questions WHERE question_en = 'What is the maximum speed on highways in dry weather?';

-- Add generic options for all remaining questions without specific answers
-- This will add 4 options to each of the remaining 48 questions

-- Option A (correct answer)
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT q.id, 'الإجابة الصحيحة', 'Correct answer', 'Correct antwoord', 'Réponse correcte', 1, 1
FROM quiz_questions q
WHERE q.created_at >= (SELECT created_at FROM quiz_questions WHERE question_en = 'What is the maximum speed limit inside cities in Belgium?')
AND NOT EXISTS (
    SELECT 1 FROM quiz_answer_options qao WHERE qao.question_id = q.id
);

-- Option B
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT q.id, 'خيار ب', 'Option B', 'Optie B', 'Option B', 0, 2
FROM quiz_questions q
WHERE q.created_at >= (SELECT created_at FROM quiz_questions WHERE question_en = 'What is the maximum speed limit inside cities in Belgium?')
AND EXISTS (
    SELECT 1 FROM quiz_answer_options qao
    WHERE qao.question_id = q.id AND qao.display_order = 1
)
AND NOT EXISTS (
    SELECT 1 FROM quiz_answer_options qao
    WHERE qao.question_id = q.id AND qao.display_order = 2
);

-- Option C
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT q.id, 'خيار ج', 'Option C', 'Optie C', 'Option C', 0, 3
FROM quiz_questions q
WHERE q.created_at >= (SELECT created_at FROM quiz_questions WHERE question_en = 'What is the maximum speed limit inside cities in Belgium?')
AND EXISTS (
    SELECT 1 FROM quiz_answer_options qao
    WHERE qao.question_id = q.id AND qao.display_order = 2
)
AND NOT EXISTS (
    SELECT 1 FROM quiz_answer_options qao
    WHERE qao.question_id = q.id AND qao.display_order = 3
);

-- Option D
INSERT INTO quiz_answer_options (question_id, option_text_ar, option_text_en, option_text_nl, option_text_fr, is_correct, display_order)
SELECT q.id, 'خيار د', 'Option D', 'Optie D', 'Option D', 0, 4
FROM quiz_questions q
WHERE q.created_at >= (SELECT created_at FROM quiz_questions WHERE question_en = 'What is the maximum speed limit inside cities in Belgium?')
AND EXISTS (
    SELECT 1 FROM quiz_answer_options qao
    WHERE qao.question_id = q.id AND qao.display_order = 3
)
AND NOT EXISTS (
    SELECT 1 FROM quiz_answer_options qao
    WHERE qao.question_id = q.id AND qao.display_order = 4
);
