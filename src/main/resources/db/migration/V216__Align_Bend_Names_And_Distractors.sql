UPDATE road_signs
SET name_ar = 'منعطف مزدوج خطير أو تعاقب منعطفات، الأول إلى اليسار',
    name_en = 'Dangerous double bend or succession of bends, first to the left',
    name_nl = 'Gevaarlijke dubbele bocht of opeenvolgende bochten, de eerste naar links',
    name_fr = 'Double virage dangereux ou succession de virages, le premier à gauche'
WHERE sign_code = 'A1c';

UPDATE road_signs
SET name_ar = 'منعطف مزدوج خطير أو تعاقب منعطفات، الأول إلى اليمين',
    name_en = 'Dangerous double bend or succession of bends, first to the right',
    name_nl = 'Gevaarlijke dubbele bocht of opeenvolgende bochten, de eerste naar rechts',
    name_fr = 'Double virage dangereux ou succession de virages, le premier à droite'
WHERE sign_code = 'A1d';

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
    WHERE rs.sign_code = 'A1c'
);

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
    WHERE rs.sign_code = 'A1d'
);
