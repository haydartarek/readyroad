UPDATE road_signs
SET description_ar = 'منعطف مزدوج خطير أو تعاقب منعطفات خطيرة، الأول إلى اليسار.',
    description_en = 'Dangerous double bend or succession of dangerous bends, the first to the left.',
    description_nl = 'Waarschuwing voor opeenvolgende gevaarlijke bochten, eerst naar links.',
    description_fr = 'Double virage dangereux ou succession de virages dangereux, le premier à gauche.'
WHERE sign_code = 'A1c';

UPDATE road_signs
SET description_ar = 'منعطف مزدوج خطير أو تعاقب منعطفات خطيرة، الأول إلى اليمين.',
    description_en = 'Dangerous double bend or succession of dangerous bends, the first to the right.',
    description_nl = 'Waarschuwing voor opeenvolgende gevaarlijke bochten, eerst naar rechts.',
    description_fr = 'Double virage dangereux ou succession de virages dangereux, le premier à droite.'
WHERE sign_code = 'A1d';

UPDATE sign_questions
SET question_nl = REPLACE(question_nl, 'een opeenvolgende gevaarlijke bochten', 'opeenvolgende gevaarlijke bochten'),
    explanation_nl = REPLACE(explanation_nl, 'een opeenvolgende gevaarlijke bochten', 'opeenvolgende gevaarlijke bochten')
WHERE sign_id IN (SELECT id FROM road_signs WHERE sign_code IN ('A1c', 'A1d'))
  AND (question_nl LIKE '%een opeenvolgende gevaarlijke bochten%'
       OR explanation_nl LIKE '%een opeenvolgende gevaarlijke bochten%');

UPDATE sign_questions
SET question_nl = REPLACE(question_nl, 'een opeenvolgende bochten', 'opeenvolgende bochten'),
    explanation_nl = REPLACE(explanation_nl, 'een opeenvolgende bochten', 'opeenvolgende bochten')
WHERE sign_id IN (SELECT id FROM road_signs WHERE sign_code IN ('A1c', 'A1d'))
  AND (question_nl LIKE '%een opeenvolgende bochten%'
       OR explanation_nl LIKE '%een opeenvolgende bochten%');

UPDATE sign_choices
SET text_nl = REPLACE(text_nl, 'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar links',
                             'Gevaarlijke dubbele bocht of opeenvolgende bochten, de eerste naar links'),
    text_en = REPLACE(text_en, 'Dangerous double or more bends, first to the left',
                             'Dangerous double bend or succession of bends, first to the left'),
    text_fr = REPLACE(text_fr, 'Double virage dangereux ou plus, le premier à gauche',
                             'Double virage dangereux ou succession de virages, le premier à gauche')
WHERE question_id IN (
    SELECT sq.id
    FROM sign_questions sq
    JOIN road_signs rs ON rs.id = sq.sign_id
    WHERE rs.sign_code = 'A1c'
);

UPDATE sign_choices
SET text_nl = REPLACE(text_nl, 'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts',
                             'Gevaarlijke dubbele bocht of opeenvolgende bochten, de eerste naar rechts'),
    text_en = REPLACE(text_en, 'Dangerous double or more bends, first to the right',
                             'Dangerous double bend or succession of bends, first to the right'),
    text_fr = REPLACE(text_fr, 'Double virage dangereux ou plus, le premier à droite',
                             'Double virage dangereux ou succession de virages, le premier à droite')
WHERE question_id IN (
    SELECT sq.id
    FROM sign_questions sq
    JOIN road_signs rs ON rs.id = sq.sign_id
    WHERE rs.sign_code = 'A1d'
);
