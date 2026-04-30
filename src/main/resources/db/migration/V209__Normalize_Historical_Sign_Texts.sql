-- Normalize historical persisted sign-learning content without touching
-- previously applied migrations. This migration focuses on learner-facing
-- texts that still expose legacy series wording or raw sign codes.

SET SESSION group_concat_max_len = 1024 * 1024 * 16;

-- ---------------------------------------------------------------------------
-- 1) Remove legacy "series/reeks/série/السلسلة" wording from visible choices
-- ---------------------------------------------------------------------------

UPDATE sign_choices
SET text_ar = TRIM(REPLACE(REGEXP_REPLACE(text_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Z0-9]+\\)', ''), 'لافتات', 'علامات'))
WHERE text_ar REGEXP '(السلسلة|الفئة)[[:space:]]*[A-Z0-9]';

UPDATE sign_choices
SET text_en = REPLACE(text_en, 'Priority over intersecting side road (B15 series)', 'Priority over intersecting side road')
WHERE text_en LIKE '%(B15 series)%';

UPDATE sign_choices
SET text_en = REPLACE(text_en, 'C-series: prohibitory signs for moving traffic', 'Prohibitory signs')
WHERE text_en LIKE '%C-series: prohibitory signs for moving traffic%';

UPDATE sign_choices
SET text_en = REPLACE(text_en, 'D-series: mandatory signs for specific users', 'Mandatory signs')
WHERE text_en LIKE '%D-series: mandatory signs for specific users%';

UPDATE sign_choices
SET text_en = REPLACE(text_en, 'E-series: parking and stopping regulation signs', 'Parking and stopping signs')
WHERE text_en LIKE '%E-series: parking and stopping regulation signs%';

UPDATE sign_choices
SET text_en = REPLACE(text_en, 'F-series: information signs', 'Information signs')
WHERE text_en LIKE '%F-series: information signs%';

UPDATE sign_choices
SET text_en = REPLACE(text_en, 'G-series: signs for special roads', 'Signs for special roads')
WHERE text_en LIKE '%G-series: signs for special roads%';

UPDATE sign_choices
SET text_en = TRIM(REGEXP_REPLACE(text_en, '^[[:space:]]*[A-Z]+-series:[[:space:]]*', ''))
WHERE text_en REGEXP '^[[:space:]]*[A-Z]+-series:[[:space:]]*';

UPDATE sign_choices
SET text_nl = REPLACE(text_nl, 'Voorrang op kruisende zijweg (B15-serie)', 'Voorrang op kruisende zijweg')
WHERE text_nl LIKE '%(B15-serie)%';

UPDATE sign_choices
SET text_nl = REPLACE(text_nl, 'C-reeks: verbodsborden voor rijverkeer', 'Verbodsborden')
WHERE text_nl LIKE '%C-reeks: verbodsborden voor rijverkeer%';

UPDATE sign_choices
SET text_nl = REPLACE(text_nl, 'D-reeks: gebodsborden voor specifieke gebruikers', 'Gebodsborden')
WHERE text_nl LIKE '%D-reeks: gebodsborden voor specifieke gebruikers%';

UPDATE sign_choices
SET text_nl = REPLACE(text_nl, 'E-reeks: parkeer- en stilstandsreglementsborden', 'Parkeer- en stilstandsborden')
WHERE text_nl LIKE '%E-reeks: parkeer- en stilstandsreglementsborden%';

UPDATE sign_choices
SET text_nl = REPLACE(text_nl, 'F-reeks: informatieborden', 'Informatieborden')
WHERE text_nl LIKE '%F-reeks: informatieborden%';

UPDATE sign_choices
SET text_nl = REPLACE(text_nl, 'G-reeks: borden voor speciale rijwegen', 'Borden voor speciale wegen')
WHERE text_nl LIKE '%G-reeks: borden voor speciale rijwegen%';

UPDATE sign_choices
SET text_nl = TRIM(REGEXP_REPLACE(text_nl, '^[[:space:]]*[A-Z]+-reeks:[[:space:]]*', ''))
WHERE text_nl REGEXP '^[[:space:]]*[A-Z]+-reeks:[[:space:]]*';

UPDATE sign_choices
SET text_fr = REPLACE(text_fr, 'Priorité sur la route latérale de croisement (série B15)', 'Priorité sur la route latérale de croisement')
WHERE text_fr LIKE '%(série B15)%';

UPDATE sign_choices
SET text_fr = REPLACE(text_fr, 'Série C: panneaux d''interdiction pour la circulation', 'Panneaux d''interdiction')
WHERE text_fr LIKE '%Série C: panneaux d''interdiction pour la circulation%';

UPDATE sign_choices
SET text_fr = REPLACE(text_fr, 'Serie C: panneaux d''interdiction pour la circulation', 'Panneaux d''interdiction')
WHERE text_fr LIKE '%Serie C: panneaux d''interdiction pour la circulation%';

UPDATE sign_choices
SET text_fr = REPLACE(text_fr, 'Série D: panneaux d''obligation pour des utilisateurs spécifiques', 'Panneaux d''obligation')
WHERE text_fr LIKE '%Série D: panneaux d''obligation pour des utilisateurs spécifiques%';

UPDATE sign_choices
SET text_fr = REPLACE(text_fr, 'Serie D: panneaux d''obligation pour des utilisateurs specifiques', 'Panneaux d''obligation')
WHERE text_fr LIKE '%Serie D: panneaux d''obligation pour des utilisateurs specifiques%';

UPDATE sign_choices
SET text_fr = REPLACE(text_fr, 'Série E: panneaux de réglementation de stationnement et d''arrêt', 'Panneaux de stationnement et d''arrêt')
WHERE text_fr LIKE '%Série E: panneaux de réglementation de stationnement et d''arrêt%';

UPDATE sign_choices
SET text_fr = REPLACE(text_fr, 'Serie E: panneaux de reglementation de stationnement et d''arret', 'Panneaux de stationnement et d''arrêt')
WHERE text_fr LIKE '%Serie E: panneaux de reglementation de stationnement et d''arret%';

UPDATE sign_choices
SET text_fr = REPLACE(text_fr, 'Série F: panneaux d''information', 'Panneaux d''information')
WHERE text_fr LIKE '%Série F: panneaux d''information%';

UPDATE sign_choices
SET text_fr = REPLACE(text_fr, 'Serie F: panneaux d''information', 'Panneaux d''information')
WHERE text_fr LIKE '%Serie F: panneaux d''information%';

UPDATE sign_choices
SET text_fr = REPLACE(text_fr, 'Série G: panneaux pour routes spéciales', 'Panneaux pour routes spéciales')
WHERE text_fr LIKE '%Série G: panneaux pour routes spéciales%';

UPDATE sign_choices
SET text_fr = REPLACE(text_fr, 'Serie G: panneaux pour routes speciales', 'Panneaux pour routes spéciales')
WHERE text_fr LIKE '%Serie G: panneaux pour routes speciales%';

UPDATE sign_choices
SET text_fr = TRIM(REGEXP_REPLACE(text_fr, '^[[:space:]]*(?:Série|Serie|série|serie)[[:space:]]*[A-Z]+[[:space:]]*:[[:space:]]*', ''))
WHERE text_fr REGEXP '^[[:space:]]*(Série|Serie|série|serie)[[:space:]]*[A-Z]+[[:space:]]*:[[:space:]]*';

UPDATE quiz_answer_options
SET option_text_ar = TRIM(REPLACE(REGEXP_REPLACE(option_text_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Z0-9]+\\)', ''), 'لافتات', 'علامات'))
WHERE option_text_ar REGEXP '(السلسلة|الفئة)[[:space:]]*[A-Z0-9]';

UPDATE quiz_answer_options
SET option_text_en = REPLACE(option_text_en, 'Priority over intersecting side road (B15 series)', 'Priority over intersecting side road')
WHERE option_text_en LIKE '%(B15 series)%';

UPDATE quiz_answer_options
SET option_text_en = REPLACE(option_text_en, 'C-series: prohibitory signs for moving traffic', 'Prohibitory signs')
WHERE option_text_en LIKE '%C-series: prohibitory signs for moving traffic%';

UPDATE quiz_answer_options
SET option_text_en = REPLACE(option_text_en, 'D-series: mandatory signs for specific users', 'Mandatory signs')
WHERE option_text_en LIKE '%D-series: mandatory signs for specific users%';

UPDATE quiz_answer_options
SET option_text_en = REPLACE(option_text_en, 'E-series: parking and stopping regulation signs', 'Parking and stopping signs')
WHERE option_text_en LIKE '%E-series: parking and stopping regulation signs%';

UPDATE quiz_answer_options
SET option_text_en = REPLACE(option_text_en, 'F-series: information signs', 'Information signs')
WHERE option_text_en LIKE '%F-series: information signs%';

UPDATE quiz_answer_options
SET option_text_en = REPLACE(option_text_en, 'G-series: signs for special roads', 'Signs for special roads')
WHERE option_text_en LIKE '%G-series: signs for special roads%';

UPDATE quiz_answer_options
SET option_text_en = TRIM(REGEXP_REPLACE(option_text_en, '^[[:space:]]*[A-Z]+-series:[[:space:]]*', ''))
WHERE option_text_en REGEXP '^[[:space:]]*[A-Z]+-series:[[:space:]]*';

UPDATE quiz_answer_options
SET option_text_nl = REPLACE(option_text_nl, 'Voorrang op kruisende zijweg (B15-serie)', 'Voorrang op kruisende zijweg')
WHERE option_text_nl LIKE '%(B15-serie)%';

UPDATE quiz_answer_options
SET option_text_nl = REPLACE(option_text_nl, 'C-reeks: verbodsborden voor rijverkeer', 'Verbodsborden')
WHERE option_text_nl LIKE '%C-reeks: verbodsborden voor rijverkeer%';

UPDATE quiz_answer_options
SET option_text_nl = REPLACE(option_text_nl, 'D-reeks: gebodsborden voor specifieke gebruikers', 'Gebodsborden')
WHERE option_text_nl LIKE '%D-reeks: gebodsborden voor specifieke gebruikers%';

UPDATE quiz_answer_options
SET option_text_nl = REPLACE(option_text_nl, 'E-reeks: parkeer- en stilstandsreglementsborden', 'Parkeer- en stilstandsborden')
WHERE option_text_nl LIKE '%E-reeks: parkeer- en stilstandsreglementsborden%';

UPDATE quiz_answer_options
SET option_text_nl = REPLACE(option_text_nl, 'F-reeks: informatieborden', 'Informatieborden')
WHERE option_text_nl LIKE '%F-reeks: informatieborden%';

UPDATE quiz_answer_options
SET option_text_nl = REPLACE(option_text_nl, 'G-reeks: borden voor speciale rijwegen', 'Borden voor speciale wegen')
WHERE option_text_nl LIKE '%G-reeks: borden voor speciale rijwegen%';

UPDATE quiz_answer_options
SET option_text_nl = TRIM(REGEXP_REPLACE(option_text_nl, '^[[:space:]]*[A-Z]+-reeks:[[:space:]]*', ''))
WHERE option_text_nl REGEXP '^[[:space:]]*[A-Z]+-reeks:[[:space:]]*';

UPDATE quiz_answer_options
SET option_text_fr = REPLACE(option_text_fr, 'Priorité sur la route latérale de croisement (série B15)', 'Priorité sur la route latérale de croisement')
WHERE option_text_fr LIKE '%(série B15)%';

UPDATE quiz_answer_options
SET option_text_fr = REPLACE(option_text_fr, 'Série C: panneaux d''interdiction pour la circulation', 'Panneaux d''interdiction')
WHERE option_text_fr LIKE '%Série C: panneaux d''interdiction pour la circulation%';

UPDATE quiz_answer_options
SET option_text_fr = REPLACE(option_text_fr, 'Serie C: panneaux d''interdiction pour la circulation', 'Panneaux d''interdiction')
WHERE option_text_fr LIKE '%Serie C: panneaux d''interdiction pour la circulation%';

UPDATE quiz_answer_options
SET option_text_fr = REPLACE(option_text_fr, 'Série D: panneaux d''obligation pour des utilisateurs spécifiques', 'Panneaux d''obligation')
WHERE option_text_fr LIKE '%Série D: panneaux d''obligation pour des utilisateurs spécifiques%';

UPDATE quiz_answer_options
SET option_text_fr = REPLACE(option_text_fr, 'Serie D: panneaux d''obligation pour des utilisateurs specifiques', 'Panneaux d''obligation')
WHERE option_text_fr LIKE '%Serie D: panneaux d''obligation pour des utilisateurs specifiques%';

UPDATE quiz_answer_options
SET option_text_fr = REPLACE(option_text_fr, 'Série E: panneaux de réglementation de stationnement et d''arrêt', 'Panneaux de stationnement et d''arrêt')
WHERE option_text_fr LIKE '%Série E: panneaux de réglementation de stationnement et d''arrêt%';

UPDATE quiz_answer_options
SET option_text_fr = REPLACE(option_text_fr, 'Serie E: panneaux de reglementation de stationnement et d''arret', 'Panneaux de stationnement et d''arrêt')
WHERE option_text_fr LIKE '%Serie E: panneaux de reglementation de stationnement et d''arret%';

UPDATE quiz_answer_options
SET option_text_fr = REPLACE(option_text_fr, 'Série F: panneaux d''information', 'Panneaux d''information')
WHERE option_text_fr LIKE '%Série F: panneaux d''information%';

UPDATE quiz_answer_options
SET option_text_fr = REPLACE(option_text_fr, 'Serie F: panneaux d''information', 'Panneaux d''information')
WHERE option_text_fr LIKE '%Serie F: panneaux d''information%';

UPDATE quiz_answer_options
SET option_text_fr = REPLACE(option_text_fr, 'Série G: panneaux pour routes spéciales', 'Panneaux pour routes spéciales')
WHERE option_text_fr LIKE '%Série G: panneaux pour routes spéciales%';

UPDATE quiz_answer_options
SET option_text_fr = REPLACE(option_text_fr, 'Serie G: panneaux pour routes speciales', 'Panneaux pour routes spéciales')
WHERE option_text_fr LIKE '%Serie G: panneaux pour routes speciales%';

UPDATE quiz_answer_options
SET option_text_fr = TRIM(REGEXP_REPLACE(option_text_fr, '^[[:space:]]*(?:Série|Serie|série|serie)[[:space:]]*[A-Z]+[[:space:]]*:[[:space:]]*', ''))
WHERE option_text_fr REGEXP '^[[:space:]]*(Série|Serie|série|serie)[[:space:]]*[A-Z]+[[:space:]]*:[[:space:]]*';

-- ---------------------------------------------------------------------------
-- 2) Normalize recurring "series of bends" wording in persisted data
-- ---------------------------------------------------------------------------

UPDATE sign_questions
SET
    question_ar = REPLACE(REPLACE(REPLACE(question_ar,
            'سلسلة منعطفات خطيرة متتالية', 'منعطفات خطيرة متتالية'),
            'طوال السلسلة', 'على امتداد جميع المنعطفات'),
            'لكامل السلسلة', 'على امتداد جميع المنعطفات'),
    explanation_ar = REPLACE(REPLACE(REPLACE(explanation_ar,
            'سلسلة منعطفات خطيرة متتالية', 'منعطفات خطيرة متتالية'),
            'طوال السلسلة', 'على امتداد جميع المنعطفات'),
            'لكامل السلسلة', 'على امتداد جميع المنعطفات'),
    question_en = REPLACE(REPLACE(REPLACE(question_en,
            'A series of consecutive dangerous bends', 'Consecutive dangerous bends'),
            'throughout the entire series of bends', 'through all the bends'),
            'throughout the series', 'through all the bends'),
    explanation_en = REPLACE(REPLACE(REPLACE(explanation_en,
            'A series of consecutive dangerous bends', 'Consecutive dangerous bends'),
            'throughout the entire series of bends', 'through all the bends'),
            'throughout the series', 'through all the bends'),
    question_nl = REPLACE(REPLACE(REPLACE(question_nl,
            'Een reeks opeenvolgende gevaarlijke bochten', 'Opeenvolgende gevaarlijke bochten'),
            'voor de volledige reeks bochten', 'over alle bochten'),
            'voor de hele reeks', 'over alle bochten'),
    explanation_nl = REPLACE(REPLACE(REPLACE(explanation_nl,
            'Een reeks opeenvolgende gevaarlijke bochten', 'Opeenvolgende gevaarlijke bochten'),
            'voor de volledige reeks bochten', 'over alle bochten'),
            'voor de hele reeks', 'over alle bochten'),
    question_fr = REPLACE(REPLACE(REPLACE(question_fr,
            'Une série de virages dangereux consécutifs', 'Des virages dangereux consécutifs'),
            'sur toute la série', 'sur l''ensemble des virages'),
            'pour toute la série de virages', 'pour l''ensemble des virages'),
    explanation_fr = REPLACE(REPLACE(REPLACE(explanation_fr,
            'Une série de virages dangereux consécutifs', 'Des virages dangereux consécutifs'),
            'sur toute la série', 'sur l''ensemble des virages'),
            'pour toute la série de virages', 'pour l''ensemble des virages')
WHERE question_ar LIKE '%السلسلة%'
   OR explanation_ar LIKE '%السلسلة%'
   OR question_en LIKE '%series%'
   OR explanation_en LIKE '%series%'
   OR question_nl LIKE '%reeks%'
   OR explanation_nl LIKE '%reeks%'
   OR question_fr LIKE '%série%'
   OR explanation_fr LIKE '%série%'
   OR question_fr LIKE '%serie%'
   OR explanation_fr LIKE '%serie%';

UPDATE sign_choices
SET
    text_ar = REPLACE(REPLACE(REPLACE(text_ar,
            'سلسلة منعطفات خطيرة متتالية', 'منعطفات خطيرة متتالية'),
            'طوال السلسلة', 'على امتداد جميع المنعطفات'),
            'لكامل السلسلة', 'على امتداد جميع المنعطفات'),
    text_en = REPLACE(REPLACE(REPLACE(text_en,
            'series of consecutive dangerous bends', 'consecutive dangerous bends'),
            'throughout the entire series of bends', 'through all the bends'),
            'throughout the series', 'through all the bends'),
    text_nl = REPLACE(REPLACE(REPLACE(text_nl,
            'reeks opeenvolgende gevaarlijke bochten', 'opeenvolgende gevaarlijke bochten'),
            'voor de volledige reeks bochten', 'over alle bochten'),
            'voor de hele reeks', 'over alle bochten'),
    text_fr = REPLACE(REPLACE(REPLACE(text_fr,
            'série de virages dangereux consécutifs', 'virages dangereux consécutifs'),
            'sur toute la série', 'sur l''ensemble des virages'),
            'pour toute la série de virages', 'pour l''ensemble des virages')
WHERE text_ar LIKE '%السلسلة%'
   OR text_en LIKE '%series%'
   OR text_nl LIKE '%reeks%'
   OR text_fr LIKE '%série%'
   OR text_fr LIKE '%serie%';

UPDATE quiz_questions
SET
    question_ar = REPLACE(REPLACE(REPLACE(question_ar,
            'سلسلة منعطفات خطيرة متتالية', 'منعطفات خطيرة متتالية'),
            'طوال السلسلة', 'على امتداد جميع المنعطفات'),
            'لكامل السلسلة', 'على امتداد جميع المنعطفات'),
    explanation_ar = REPLACE(REPLACE(REPLACE(explanation_ar,
            'سلسلة منعطفات خطيرة متتالية', 'منعطفات خطيرة متتالية'),
            'طوال السلسلة', 'على امتداد جميع المنعطفات'),
            'لكامل السلسلة', 'على امتداد جميع المنعطفات'),
    error_explanation_ar = REPLACE(REPLACE(REPLACE(error_explanation_ar,
            'سلسلة منعطفات خطيرة متتالية', 'منعطفات خطيرة متتالية'),
            'طوال السلسلة', 'على امتداد جميع المنعطفات'),
            'لكامل السلسلة', 'على امتداد جميع المنعطفات')
WHERE question_ar LIKE '%السلسلة%'
   OR explanation_ar LIKE '%السلسلة%'
   OR error_explanation_ar LIKE '%السلسلة%';

-- ---------------------------------------------------------------------------
-- 3) Replace raw sign codes in learner-facing texts with sign names
-- ---------------------------------------------------------------------------

DROP TEMPORARY TABLE IF EXISTS tmp_sign_text_replacements;

CREATE TEMPORARY TABLE tmp_sign_text_replacements (
    search_key VARCHAR(512) NOT NULL PRIMARY KEY,
    replace_ar TEXT NOT NULL,
    replace_en TEXT NOT NULL,
    replace_nl TEXT NOT NULL,
    replace_fr TEXT NOT NULL
) ENGINE=InnoDB;

INSERT IGNORE INTO tmp_sign_text_replacements (search_key, replace_ar, replace_en, replace_nl, replace_fr)
SELECT
    sign_code,
    CONCAT('العلامة المرورية: ', name_ar),
    CONCAT('the traffic sign \"', name_en, '\"'),
    CONCAT('het verkeersbord \"', name_nl, '\"'),
    CONCAT('le panneau \"', name_fr, '\"')
FROM road_signs
WHERE is_active = 1
  AND sign_code IS NOT NULL
  AND sign_code <> ''
  AND name_ar IS NOT NULL
  AND name_en IS NOT NULL
  AND name_nl IS NOT NULL
  AND name_fr IS NOT NULL;

INSERT IGNORE INTO tmp_sign_text_replacements (search_key, replace_ar, replace_en, replace_nl, replace_fr)
SELECT
    CONCAT(sign_code, ' (', name_ar, ')'),
    CONCAT('العلامة المرورية: ', name_ar),
    CONCAT('the traffic sign \"', name_en, '\"'),
    CONCAT('het verkeersbord \"', name_nl, '\"'),
    CONCAT('le panneau \"', name_fr, '\"')
FROM road_signs
WHERE is_active = 1
  AND sign_code IS NOT NULL
  AND sign_code <> ''
  AND name_ar IS NOT NULL
  AND name_en IS NOT NULL
  AND name_nl IS NOT NULL
  AND name_fr IS NOT NULL;

INSERT IGNORE INTO tmp_sign_text_replacements (search_key, replace_ar, replace_en, replace_nl, replace_fr)
SELECT
    CONCAT(sign_code, ' (', name_en, ')'),
    CONCAT('العلامة المرورية: ', name_ar),
    CONCAT('the traffic sign \"', name_en, '\"'),
    CONCAT('het verkeersbord \"', name_nl, '\"'),
    CONCAT('le panneau \"', name_fr, '\"')
FROM road_signs
WHERE is_active = 1
  AND sign_code IS NOT NULL
  AND sign_code <> ''
  AND name_ar IS NOT NULL
  AND name_en IS NOT NULL
  AND name_nl IS NOT NULL
  AND name_fr IS NOT NULL;

INSERT IGNORE INTO tmp_sign_text_replacements (search_key, replace_ar, replace_en, replace_nl, replace_fr)
SELECT
    CONCAT(sign_code, ' (', name_nl, ')'),
    CONCAT('العلامة المرورية: ', name_ar),
    CONCAT('the traffic sign \"', name_en, '\"'),
    CONCAT('het verkeersbord \"', name_nl, '\"'),
    CONCAT('le panneau \"', name_fr, '\"')
FROM road_signs
WHERE is_active = 1
  AND sign_code IS NOT NULL
  AND sign_code <> ''
  AND name_ar IS NOT NULL
  AND name_en IS NOT NULL
  AND name_nl IS NOT NULL
  AND name_fr IS NOT NULL;

INSERT IGNORE INTO tmp_sign_text_replacements (search_key, replace_ar, replace_en, replace_nl, replace_fr)
SELECT
    CONCAT(sign_code, ' (', name_fr, ')'),
    CONCAT('العلامة المرورية: ', name_ar),
    CONCAT('the traffic sign \"', name_en, '\"'),
    CONCAT('het verkeersbord \"', name_nl, '\"'),
    CONCAT('le panneau \"', name_fr, '\"')
FROM road_signs
WHERE is_active = 1
  AND sign_code IS NOT NULL
  AND sign_code <> ''
  AND name_ar IS NOT NULL
  AND name_en IS NOT NULL
  AND name_nl IS NOT NULL
  AND name_fr IS NOT NULL;

INSERT IGNORE INTO tmp_sign_text_replacements (search_key, replace_ar, replace_en, replace_nl, replace_fr)
SELECT
    alt_code,
    CONCAT('العلامة المرورية: ', name_ar),
    CONCAT('the traffic sign \"', name_en, '\"'),
    CONCAT('het verkeersbord \"', name_nl, '\"'),
    CONCAT('le panneau \"', name_fr, '\"')
FROM (
    SELECT
        sign_code,
        CASE
            WHEN sign_code REGEXP '^[A-Z]+[0-9]+[A-Z]$'
                THEN CONCAT(LEFT(sign_code, CHAR_LENGTH(sign_code) - 1), LOWER(RIGHT(sign_code, 1)))
            ELSE NULL
        END AS alt_code,
        name_ar,
        name_en,
        name_nl,
        name_fr,
        is_active
    FROM road_signs
) codes
WHERE is_active = 1
  AND alt_code IS NOT NULL
  AND alt_code <> sign_code
  AND name_ar IS NOT NULL
  AND name_en IS NOT NULL
  AND name_nl IS NOT NULL
  AND name_fr IS NOT NULL;

INSERT IGNORE INTO tmp_sign_text_replacements (search_key, replace_ar, replace_en, replace_nl, replace_fr)
SELECT
    CONCAT(alt_code, ' (', name_ar, ')'),
    CONCAT('العلامة المرورية: ', name_ar),
    CONCAT('the traffic sign \"', name_en, '\"'),
    CONCAT('het verkeersbord \"', name_nl, '\"'),
    CONCAT('le panneau \"', name_fr, '\"')
FROM (
    SELECT
        sign_code,
        CASE
            WHEN sign_code REGEXP '^[A-Z]+[0-9]+[A-Z]$'
                THEN CONCAT(LEFT(sign_code, CHAR_LENGTH(sign_code) - 1), LOWER(RIGHT(sign_code, 1)))
            ELSE NULL
        END AS alt_code,
        name_ar,
        name_en,
        name_nl,
        name_fr,
        is_active
    FROM road_signs
) codes
WHERE is_active = 1
  AND alt_code IS NOT NULL
  AND alt_code <> sign_code
  AND name_ar IS NOT NULL
  AND name_en IS NOT NULL
  AND name_nl IS NOT NULL
  AND name_fr IS NOT NULL;

INSERT IGNORE INTO tmp_sign_text_replacements (search_key, replace_ar, replace_en, replace_nl, replace_fr)
SELECT
    CONCAT(alt_code, ' (', name_en, ')'),
    CONCAT('العلامة المرورية: ', name_ar),
    CONCAT('the traffic sign \"', name_en, '\"'),
    CONCAT('het verkeersbord \"', name_nl, '\"'),
    CONCAT('le panneau \"', name_fr, '\"')
FROM (
    SELECT
        sign_code,
        CASE
            WHEN sign_code REGEXP '^[A-Z]+[0-9]+[A-Z]$'
                THEN CONCAT(LEFT(sign_code, CHAR_LENGTH(sign_code) - 1), LOWER(RIGHT(sign_code, 1)))
            ELSE NULL
        END AS alt_code,
        name_ar,
        name_en,
        name_nl,
        name_fr,
        is_active
    FROM road_signs
) codes
WHERE is_active = 1
  AND alt_code IS NOT NULL
  AND alt_code <> sign_code
  AND name_ar IS NOT NULL
  AND name_en IS NOT NULL
  AND name_nl IS NOT NULL
  AND name_fr IS NOT NULL;

INSERT IGNORE INTO tmp_sign_text_replacements (search_key, replace_ar, replace_en, replace_nl, replace_fr)
SELECT
    CONCAT(alt_code, ' (', name_nl, ')'),
    CONCAT('العلامة المرورية: ', name_ar),
    CONCAT('the traffic sign \"', name_en, '\"'),
    CONCAT('het verkeersbord \"', name_nl, '\"'),
    CONCAT('le panneau \"', name_fr, '\"')
FROM (
    SELECT
        sign_code,
        CASE
            WHEN sign_code REGEXP '^[A-Z]+[0-9]+[A-Z]$'
                THEN CONCAT(LEFT(sign_code, CHAR_LENGTH(sign_code) - 1), LOWER(RIGHT(sign_code, 1)))
            ELSE NULL
        END AS alt_code,
        name_ar,
        name_en,
        name_nl,
        name_fr,
        is_active
    FROM road_signs
) codes
WHERE is_active = 1
  AND alt_code IS NOT NULL
  AND alt_code <> sign_code
  AND name_ar IS NOT NULL
  AND name_en IS NOT NULL
  AND name_nl IS NOT NULL
  AND name_fr IS NOT NULL;

INSERT IGNORE INTO tmp_sign_text_replacements (search_key, replace_ar, replace_en, replace_nl, replace_fr)
SELECT
    CONCAT(alt_code, ' (', name_fr, ')'),
    CONCAT('العلامة المرورية: ', name_ar),
    CONCAT('the traffic sign \"', name_en, '\"'),
    CONCAT('het verkeersbord \"', name_nl, '\"'),
    CONCAT('le panneau \"', name_fr, '\"')
FROM (
    SELECT
        sign_code,
        CASE
            WHEN sign_code REGEXP '^[A-Z]+[0-9]+[A-Z]$'
                THEN CONCAT(LEFT(sign_code, CHAR_LENGTH(sign_code) - 1), LOWER(RIGHT(sign_code, 1)))
            ELSE NULL
        END AS alt_code,
        name_ar,
        name_en,
        name_nl,
        name_fr,
        is_active
    FROM road_signs
) codes
WHERE is_active = 1
  AND alt_code IS NOT NULL
  AND alt_code <> sign_code
  AND name_ar IS NOT NULL
  AND name_en IS NOT NULL
  AND name_nl IS NOT NULL
  AND name_fr IS NOT NULL;

SET @expr_template_ar = (
    SELECT CONCAT(
        REPEAT('REPLACE(', COUNT(*)),
        '__COLUMN__',
        GROUP_CONCAT(
            CONCAT(', ''', REPLACE(search_key, '''', ''''''), ''', ''', REPLACE(replace_ar, '''', ''''''), ''')')
            ORDER BY CHAR_LENGTH(search_key) DESC
            SEPARATOR ''
        )
    )
    FROM tmp_sign_text_replacements
);

SET @expr_template_en = (
    SELECT CONCAT(
        REPEAT('REPLACE(', COUNT(*)),
        '__COLUMN__',
        GROUP_CONCAT(
            CONCAT(', ''', REPLACE(search_key, '''', ''''''), ''', ''', REPLACE(replace_en, '''', ''''''), ''')')
            ORDER BY CHAR_LENGTH(search_key) DESC
            SEPARATOR ''
        )
    )
    FROM tmp_sign_text_replacements
);

SET @expr_template_nl = (
    SELECT CONCAT(
        REPEAT('REPLACE(', COUNT(*)),
        '__COLUMN__',
        GROUP_CONCAT(
            CONCAT(', ''', REPLACE(search_key, '''', ''''''), ''', ''', REPLACE(replace_nl, '''', ''''''), ''')')
            ORDER BY CHAR_LENGTH(search_key) DESC
            SEPARATOR ''
        )
    )
    FROM tmp_sign_text_replacements
);

SET @expr_template_fr = (
    SELECT CONCAT(
        REPEAT('REPLACE(', COUNT(*)),
        '__COLUMN__',
        GROUP_CONCAT(
            CONCAT(', ''', REPLACE(search_key, '''', ''''''), ''', ''', REPLACE(replace_fr, '''', ''''''), ''')')
            ORDER BY CHAR_LENGTH(search_key) DESC
            SEPARATOR ''
        )
    )
    FROM tmp_sign_text_replacements
);

SET @sql = CONCAT(
    'UPDATE sign_questions SET ',
    'question_ar = ', REPLACE(@expr_template_ar, '__COLUMN__', 'question_ar'), ', ',
    'question_en = ', REPLACE(@expr_template_en, '__COLUMN__', 'question_en'), ', ',
    'question_nl = ', REPLACE(@expr_template_nl, '__COLUMN__', 'question_nl'), ', ',
    'question_fr = ', REPLACE(@expr_template_fr, '__COLUMN__', 'question_fr'), ', ',
    'explanation_ar = ', REPLACE(@expr_template_ar, '__COLUMN__', 'explanation_ar'), ', ',
    'explanation_en = ', REPLACE(@expr_template_en, '__COLUMN__', 'explanation_en'), ', ',
    'explanation_nl = ', REPLACE(@expr_template_nl, '__COLUMN__', 'explanation_nl'), ', ',
    'explanation_fr = ', REPLACE(@expr_template_fr, '__COLUMN__', 'explanation_fr'),
    ' WHERE ',
    '(question_ar REGEXP ''[A-Z][0-9]'') OR ',
    '(question_en REGEXP ''[A-Z][0-9]'') OR ',
    '(question_nl REGEXP ''[A-Z][0-9]'') OR ',
    '(question_fr REGEXP ''[A-Z][0-9]'') OR ',
    '(explanation_ar REGEXP ''[A-Z][0-9]'') OR ',
    '(explanation_en REGEXP ''[A-Z][0-9]'') OR ',
    '(explanation_nl REGEXP ''[A-Z][0-9]'') OR ',
    '(explanation_fr REGEXP ''[A-Z][0-9]'');'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT(
    'UPDATE sign_choices SET ',
    'text_ar = ', REPLACE(@expr_template_ar, '__COLUMN__', 'text_ar'), ', ',
    'text_en = ', REPLACE(@expr_template_en, '__COLUMN__', 'text_en'), ', ',
    'text_nl = ', REPLACE(@expr_template_nl, '__COLUMN__', 'text_nl'), ', ',
    'text_fr = ', REPLACE(@expr_template_fr, '__COLUMN__', 'text_fr'),
    ' WHERE ',
    '(text_ar REGEXP ''[A-Z][0-9]'') OR ',
    '(text_en REGEXP ''[A-Z][0-9]'') OR ',
    '(text_nl REGEXP ''[A-Z][0-9]'') OR ',
    '(text_fr REGEXP ''[A-Z][0-9]'');'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT(
    'UPDATE quiz_questions SET ',
    'question_ar = ', REPLACE(@expr_template_ar, '__COLUMN__', 'question_ar'), ', ',
    'question_en = ', REPLACE(@expr_template_en, '__COLUMN__', 'question_en'), ', ',
    'question_nl = ', REPLACE(@expr_template_nl, '__COLUMN__', 'question_nl'), ', ',
    'question_fr = ', REPLACE(@expr_template_fr, '__COLUMN__', 'question_fr'), ', ',
    'explanation_ar = ', REPLACE(@expr_template_ar, '__COLUMN__', 'explanation_ar'), ', ',
    'explanation_en = ', REPLACE(@expr_template_en, '__COLUMN__', 'explanation_en'), ', ',
    'explanation_nl = ', REPLACE(@expr_template_nl, '__COLUMN__', 'explanation_nl'), ', ',
    'explanation_fr = ', REPLACE(@expr_template_fr, '__COLUMN__', 'explanation_fr'), ', ',
    'error_explanation_ar = ', REPLACE(@expr_template_ar, '__COLUMN__', 'error_explanation_ar'), ', ',
    'error_explanation_en = ', REPLACE(@expr_template_en, '__COLUMN__', 'error_explanation_en'), ', ',
    'error_explanation_nl = ', REPLACE(@expr_template_nl, '__COLUMN__', 'error_explanation_nl'), ', ',
    'error_explanation_fr = ', REPLACE(@expr_template_fr, '__COLUMN__', 'error_explanation_fr'),
    ' WHERE ',
    '(question_ar REGEXP ''[A-Z][0-9]'') OR ',
    '(question_en REGEXP ''[A-Z][0-9]'') OR ',
    '(question_nl REGEXP ''[A-Z][0-9]'') OR ',
    '(question_fr REGEXP ''[A-Z][0-9]'') OR ',
    '(explanation_ar REGEXP ''[A-Z][0-9]'') OR ',
    '(explanation_en REGEXP ''[A-Z][0-9]'') OR ',
    '(explanation_nl REGEXP ''[A-Z][0-9]'') OR ',
    '(explanation_fr REGEXP ''[A-Z][0-9]'') OR ',
    '(error_explanation_ar REGEXP ''[A-Z][0-9]'') OR ',
    '(error_explanation_en REGEXP ''[A-Z][0-9]'') OR ',
    '(error_explanation_nl REGEXP ''[A-Z][0-9]'') OR ',
    '(error_explanation_fr REGEXP ''[A-Z][0-9]'');'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT(
    'UPDATE quiz_answer_options SET ',
    'option_text_ar = ', REPLACE(@expr_template_ar, '__COLUMN__', 'option_text_ar'), ', ',
    'option_text_en = ', REPLACE(@expr_template_en, '__COLUMN__', 'option_text_en'), ', ',
    'option_text_nl = ', REPLACE(@expr_template_nl, '__COLUMN__', 'option_text_nl'), ', ',
    'option_text_fr = ', REPLACE(@expr_template_fr, '__COLUMN__', 'option_text_fr'),
    ' WHERE ',
    '(option_text_ar REGEXP ''[A-Z][0-9]'') OR ',
    '(option_text_en REGEXP ''[A-Z][0-9]'') OR ',
    '(option_text_nl REGEXP ''[A-Z][0-9]'') OR ',
    '(option_text_fr REGEXP ''[A-Z][0-9]'');'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

DROP TEMPORARY TABLE IF EXISTS tmp_sign_text_replacements;
