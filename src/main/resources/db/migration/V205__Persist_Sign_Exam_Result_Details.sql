ALTER TABLE sign_exam_results
    ADD COLUMN exam_number INT NOT NULL DEFAULT 1 AFTER sign_code,
    ADD COLUMN question_results_json LONGTEXT NULL AFTER passed;

UPDATE sign_exam_results
SET exam_number = 1
WHERE exam_number IS NULL OR exam_number = 0;
