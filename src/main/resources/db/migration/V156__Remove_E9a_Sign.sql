-- V156: Remove E9a (Parkeren toegelaten) sign completely

-- 1. Remove sign_exam_questions linked to E9a's sign_questions
DELETE FROM sign_exam_questions
WHERE question_id IN (
    SELECT id FROM sign_questions WHERE sign_id = (
        SELECT id FROM traffic_signs WHERE sign_code = 'E9a'
    )
);

-- 2. Remove sign_exam_questions linked to E9a's sign_exams
DELETE FROM sign_exam_questions
WHERE exam_id IN (
    SELECT id FROM sign_exams WHERE sign_id = (
        SELECT id FROM traffic_signs WHERE sign_code = 'E9a'
    )
);

-- 3. Remove sign_choices linked to E9a's sign_questions
DELETE FROM sign_choices
WHERE question_id IN (
    SELECT id FROM sign_questions WHERE sign_id = (
        SELECT id FROM traffic_signs WHERE sign_code = 'E9a'
    )
);

-- 4. Remove sign_questions for E9a
DELETE FROM sign_questions
WHERE sign_id = (SELECT id FROM traffic_signs WHERE sign_code = 'E9a');

-- 5. Remove sign_exams for E9a
DELETE FROM sign_exams
WHERE sign_id = (SELECT id FROM traffic_signs WHERE sign_code = 'E9a');

-- 6. Remove from road_signs
DELETE FROM road_signs WHERE sign_code = 'E9a';

-- 7. Remove from traffic_signs
DELETE FROM traffic_signs WHERE sign_code = 'E9a';
