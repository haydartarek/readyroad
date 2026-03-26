UPDATE sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
SET
    sc.text_en = TRIM(REGEXP_REPLACE(sc.text_en, '[[:space:]]*\\([A-Z]+-series\\)', '')),
    sc.text_nl = TRIM(REGEXP_REPLACE(sc.text_nl, '[[:space:]]*\\([A-Z]+-reeks\\)', '')),
    sc.text_fr = TRIM(REGEXP_REPLACE(sc.text_fr, '[[:space:]]*\\((série|serie|sÃ©rie)[[:space:]]*[A-Z]+\\)', '')),
    sc.text_ar = TRIM(REGEXP_REPLACE(sc.text_ar, '[[:space:]]*\\(سلسلة[[:space:]]*[A-Z]+\\)', ''))
WHERE sq.question_type = 'WHICH_SIGN';

UPDATE sign_questions
SET
    explanation_en = TRIM(REGEXP_REPLACE(explanation_en, '[[:space:]]*\\([A-Z]+-series\\)', '')),
    explanation_nl = TRIM(REGEXP_REPLACE(explanation_nl, '[[:space:]]*\\([A-Z]+-reeks\\)', '')),
    explanation_fr = TRIM(REGEXP_REPLACE(explanation_fr, '[[:space:]]*\\((série|serie|sÃ©rie)[[:space:]]*[A-Z]+\\)', '')),
    explanation_ar = TRIM(REGEXP_REPLACE(explanation_ar, '[[:space:]]*\\(سلسلة[[:space:]]*[A-Z]+\\)', ''))
WHERE question_type = 'WHICH_SIGN';
