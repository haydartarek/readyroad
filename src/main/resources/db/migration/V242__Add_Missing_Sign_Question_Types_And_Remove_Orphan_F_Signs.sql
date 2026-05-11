-- V242: Align sign quiz question types with the Java enum and remove orphan F seeds.
--
-- WHERE_DOES_IT_APPLY and WHAT_HAPPENS_IF are valid SignQuestionType values used
-- by the signs_import JSON files. The database enum lagged behind the code.
ALTER TABLE sign_questions
  MODIFY COLUMN question_type ENUM(
    'IS_IT_ALLOWED',
    'WHAT_DOES_IT_MEAN',
    'WHAT_MUST_YOU_DO',
    'WHICH_SIGN',
    'HAZARD_IDENTIFICATION',
    'DRIVER_ACTION',
    'WHERE_DOES_IT_APPLY',
    'WHAT_HAPPENS_IF',
    'SIGN_PLACEMENT_DISTANCE'
  ) NOT NULL;

-- F45b and F87 are no longer canonical ReadyRoad sign records. Their import
-- directories were removed; this keeps fresh or upgraded databases aligned.
DELETE sc
FROM sign_choices sc
JOIN sign_questions sq ON sq.id = sc.question_id
JOIN road_signs rs ON rs.id = sq.sign_id
WHERE rs.sign_code IN ('F45b', 'F87');

DELETE sq
FROM sign_questions sq
JOIN road_signs rs ON rs.id = sq.sign_id
WHERE rs.sign_code IN ('F45b', 'F87');

DELETE se
FROM sign_exams se
JOIN road_signs rs ON rs.id = se.sign_id
WHERE rs.sign_code IN ('F45b', 'F87');

DELETE FROM road_signs
WHERE sign_code IN ('F45b', 'F87');
