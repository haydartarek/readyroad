-- V174: Delete all T-series delineation signs (category_id=28, code='T')

DELETE FROM sign_exam_questions
WHERE question_id IN (
    SELECT id FROM sign_questions
    WHERE sign_id IN (
        SELECT id FROM traffic_signs WHERE category_id = 28
    )
);

DELETE FROM sign_choices
WHERE question_id IN (
    SELECT id FROM sign_questions
    WHERE sign_id IN (
        SELECT id FROM traffic_signs WHERE category_id = 28
    )
);

DELETE FROM sign_questions
WHERE sign_id IN (
    SELECT id FROM traffic_signs WHERE category_id = 28
);

DELETE FROM traffic_signs WHERE category_id = 28;
