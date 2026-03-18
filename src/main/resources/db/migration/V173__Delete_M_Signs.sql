-- V173: Delete all M-series signs (Fietsersborden, category_id=9)

DELETE FROM sign_exam_questions
WHERE question_id IN (
    SELECT id FROM sign_questions
    WHERE sign_id IN (
        SELECT id FROM traffic_signs WHERE category_id = 9
    )
);

DELETE FROM sign_choices
WHERE question_id IN (
    SELECT id FROM sign_questions
    WHERE sign_id IN (
        SELECT id FROM traffic_signs WHERE category_id = 9
    )
);

DELETE FROM sign_questions
WHERE sign_id IN (
    SELECT id FROM traffic_signs WHERE category_id = 9
);

DELETE FROM traffic_signs WHERE category_id = 9;
