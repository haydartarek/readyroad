UPDATE sign_choices c
JOIN sign_questions q ON q.id = c.question_id
JOIN road_signs rs ON rs.id = q.sign_id
SET c.text_ar = 'منعطف خطير إلى اليمين',
    c.text_en = 'Dangerous curve to the right',
    c.text_nl = 'Gevaarlijke bocht naar rechts',
    c.text_fr = 'Virage dangereux à droite'
WHERE rs.sign_code = 'A1d'
  AND q.question_ref = 'A1d_Q01'
  AND c.display_order = 3;
