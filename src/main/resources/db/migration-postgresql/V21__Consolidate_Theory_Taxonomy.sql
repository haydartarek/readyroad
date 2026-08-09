-- Consolidate the theoretical-exam taxonomy into eight learner-facing subjects.
-- Question and answer identities are intentionally unchanged. Historical exam rows
-- are not updated; only the current question classification and derived progress
-- aggregates are migrated.

INSERT INTO categories (
    code, name_ar, name_en, name_nl, name_fr,
    description_ar, description_en, description_nl, description_fr,
    display_order, is_active, content_scope, created_at, updated_at
) VALUES
    ('TH01', 'الأولوية والتقاطعات', 'Priority and intersections', 'Voorrang en kruispunten', 'Priorité et carrefours', NULL, NULL, NULL, NULL, 101, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TH02', 'السرعة والطرق والمسافات', 'Speed, roads and distances', 'Snelheid, wegen en afstanden', 'Vitesse, routes et distances', NULL, NULL, NULL, NULL, 102, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TH03', 'المناورات والتجاوز والمسارات', 'Manoeuvres, overtaking and lanes', 'Manoeuvres, inhalen en rijstroken', 'Manœuvres, dépassement et voies', NULL, NULL, NULL, NULL, 103, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TH04', 'الوقوف والتوقف والاصطفاف', 'Parking, stopping and standing', 'Parkeren, stoppen en stilstaan', 'Stationnement, arrêt et immobilisation', NULL, NULL, NULL, NULL, 104, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TH05', 'العلامات والإشارات وتنظيم المرور', 'Signs, signals and traffic control', 'Verkeersborden, signalen en verkeersregeling', 'Panneaux, signaux et gestion de la circulation', NULL, NULL, NULL, NULL, 105, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TH06', 'مستخدمو الطريق والنقل العام', 'Road users and public transport', 'Weggebruikers en openbaar vervoer', 'Usagers de la route et transports publics', NULL, NULL, NULL, NULL, 106, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TH07', 'المركبة والسلامة التقنية', 'Vehicle and technical safety', 'Voertuig en technische veiligheid', 'Véhicule et sécurité technique', NULL, NULL, NULL, NULL, 107, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TH08', 'السائق والقانون والسلامة', 'Driver, law and safety', 'Bestuurder, wetgeving en veiligheid', 'Conducteur, législation et sécurité', NULL, NULL, NULL, NULL, 108, TRUE, 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (code) DO UPDATE SET
    name_ar = EXCLUDED.name_ar,
    name_en = EXCLUDED.name_en,
    name_nl = EXCLUDED.name_nl,
    name_fr = EXCLUDED.name_fr,
    display_order = EXCLUDED.display_order,
    is_active = TRUE,
    content_scope = 'THEORETICAL_EXAM',
    updated_at = CURRENT_TIMESTAMP;

CREATE TEMPORARY TABLE theory_taxonomy_map (
    question_id BIGINT PRIMARY KEY,
    previous_category_id BIGINT NOT NULL,
    target_code VARCHAR(10) NOT NULL
) ON COMMIT DROP;

-- Primary learning objective: priority and intersection rules.
INSERT INTO theory_taxonomy_map (question_id, previous_category_id, target_code)
SELECT q.id, q.category_id, 'TH01'
FROM quiz_questions q
WHERE q.id IN (5, 13, 27, 28, 38, 39);

-- Primary learning objective: speed, road type and safe-distance rules.
INSERT INTO theory_taxonomy_map (question_id, previous_category_id, target_code)
SELECT q.id, q.category_id, 'TH02'
FROM quiz_questions q
WHERE q.id IN (2, 4, 10, 11, 12, 21, 42, 55, 56, 57, 59, 60, 61, 63);

-- Primary learning objective: manoeuvres, overtaking and lane use.
INSERT INTO theory_taxonomy_map (question_id, previous_category_id, target_code)
SELECT q.id, q.category_id, 'TH03'
FROM quiz_questions q
WHERE q.id IN (18, 22, 30, 32, 36, 37, 40, 52, 54, 62);

-- Primary learning objective: parking, stopping and standing.
INSERT INTO theory_taxonomy_map (question_id, previous_category_id, target_code)
SELECT q.id, q.category_id, 'TH04'
FROM quiz_questions q
WHERE q.id IN (3);

-- Primary learning objective: signs, signals and traffic control.
INSERT INTO theory_taxonomy_map (question_id, previous_category_id, target_code)
SELECT q.id, q.category_id, 'TH05'
FROM quiz_questions q
WHERE q.id IN (1, 6, 7, 8, 9, 20, 25, 29, 33, 34);

-- Primary learning objective: road users and public transport.
INSERT INTO theory_taxonomy_map (question_id, previous_category_id, target_code)
SELECT q.id, q.category_id, 'TH06'
FROM quiz_questions q
WHERE q.id IN (14, 16, 26, 31, 35);

-- Primary learning objective: vehicle condition and technical safety.
INSERT INTO theory_taxonomy_map (question_id, previous_category_id, target_code)
SELECT q.id, q.category_id, 'TH07'
FROM quiz_questions q
WHERE q.id IN (15, 17, 41, 43, 48);

-- Primary learning objective: driver obligations, law and personal safety.
INSERT INTO theory_taxonomy_map (question_id, previous_category_id, target_code)
SELECT q.id, q.category_id, 'TH08'
FROM quiz_questions q
WHERE q.id IN (19, 23, 24, 44, 45, 46, 47, 49, 50, 58);

DO $$
DECLARE
    question_count INTEGER;
    mapped_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO question_count FROM quiz_questions;
    SELECT COUNT(*) INTO mapped_count FROM theory_taxonomy_map;

    IF question_count <> mapped_count THEN
        RAISE EXCEPTION
            'Theory taxonomy migration requires every existing question to be mapped; found % questions and % mappings',
            question_count, mapped_count;
    END IF;
END $$;

-- Remove the theoretical attempts that were historically aggregated into the
-- traffic-sign category rows. user_question_history is the cumulative source
-- for theory practice and completed theory-exam answers.
WITH theory_by_legacy_category AS (
    SELECT
        h.user_id,
        m.previous_category_id AS category_id,
        SUM(h.times_correct + h.times_wrong)::INTEGER AS attempted,
        SUM(h.times_correct)::INTEGER AS correct
    FROM user_question_history h
    JOIN theory_taxonomy_map m ON m.question_id = h.question_id
    GROUP BY h.user_id, m.previous_category_id
), adjusted AS (
    SELECT
        p.id,
        GREATEST(0, p.questions_attempted - t.attempted) AS attempted,
        LEAST(
            GREATEST(0, p.questions_attempted - t.attempted),
            GREATEST(0, p.correct_answers - t.correct)
        ) AS correct
    FROM user_category_progress p
    JOIN theory_by_legacy_category t
      ON t.user_id = p.user_id
     AND t.category_id = p.category_id
)
UPDATE user_category_progress p
SET questions_attempted = a.attempted,
    correct_answers = a.correct,
    accuracy_rate = CASE
        WHEN a.attempted = 0 THEN NULL
        ELSE ROUND(a.correct * 100.0 / a.attempted, 2)
    END,
    mastery_level = CASE
        WHEN a.attempted = 0 THEN 'BEGINNER'
        WHEN a.correct * 100.0 / a.attempted >= 80 THEN 'ADVANCED'
        WHEN a.correct * 100.0 / a.attempted >= 50 THEN 'INTERMEDIATE'
        ELSE 'BEGINNER'
    END,
    updated_at = CURRENT_TIMESTAMP
FROM adjusted a
WHERE p.id = a.id;

UPDATE quiz_questions q
SET category_id = c.id,
    updated_at = CURRENT_TIMESTAMP
FROM theory_taxonomy_map m
JOIN categories c ON c.code = m.target_code
WHERE q.id = m.question_id;

-- Rebuild theory-only category aggregates under TH01-TH08 without modifying
-- the canonical answer history or any historical exam/result row.
INSERT INTO user_category_progress (
    user_id, category_id, questions_attempted, correct_answers,
    accuracy_rate, last_practiced, mastery_level, created_at, updated_at
)
SELECT
    h.user_id,
    c.id,
    SUM(h.times_correct + h.times_wrong)::INTEGER AS attempted,
    SUM(h.times_correct)::INTEGER AS correct,
    ROUND(SUM(h.times_correct) * 100.0 / SUM(h.times_correct + h.times_wrong), 2),
    MAX(h.answered_at),
    CASE
        WHEN SUM(h.times_correct) * 100.0 / SUM(h.times_correct + h.times_wrong) >= 80 THEN 'ADVANCED'
        WHEN SUM(h.times_correct) * 100.0 / SUM(h.times_correct + h.times_wrong) >= 50 THEN 'INTERMEDIATE'
        ELSE 'BEGINNER'
    END,
    MIN(h.created_at),
    CURRENT_TIMESTAMP
FROM user_question_history h
JOIN theory_taxonomy_map m ON m.question_id = h.question_id
JOIN categories c ON c.code = m.target_code
WHERE h.times_correct + h.times_wrong > 0
GROUP BY h.user_id, c.id
ON CONFLICT (user_id, category_id) DO UPDATE SET
    questions_attempted = EXCLUDED.questions_attempted,
    correct_answers = EXCLUDED.correct_answers,
    accuracy_rate = EXCLUDED.accuracy_rate,
    last_practiced = EXCLUDED.last_practiced,
    mastery_level = EXCLUDED.mastery_level,
    updated_at = CURRENT_TIMESTAMP;

UPDATE categories
SET content_scope = 'TRAFFIC_SIGN',
    updated_at = CURRENT_TIMESTAMP
WHERE code IN ('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'M', 'Z');

UPDATE categories
SET is_active = FALSE,
    updated_at = CURRENT_TIMESTAMP
WHERE code IN (
    'TH_PRI', 'TH_SPEED', 'TH_PARK', 'TH_RULES', 'TH_POS', 'TH_OVTK',
    'TH_VRU', 'TH_BEHAV', 'TH_VEH', 'TH_SAFE', 'TH_SIGNS', 'TH_ECO',
    'TH_MWAY', 'TH_LEGAL'
);

DO $$
DECLARE
    canonical_count INTEGER;
    uncategorized_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO canonical_count
    FROM categories
    WHERE code IN ('TH01', 'TH02', 'TH03', 'TH04', 'TH05', 'TH06', 'TH07', 'TH08')
      AND is_active = TRUE
      AND content_scope = 'THEORETICAL_EXAM';

    SELECT COUNT(*) INTO uncategorized_count
    FROM quiz_questions q
    JOIN categories c ON c.id = q.category_id
    WHERE c.code NOT IN ('TH01', 'TH02', 'TH03', 'TH04', 'TH05', 'TH06', 'TH07', 'TH08');

    IF canonical_count <> 8 OR uncategorized_count <> 0 THEN
        RAISE EXCEPTION
            'Theory taxonomy validation failed: active categories=%, uncategorized questions=%',
            canonical_count, uncategorized_count;
    END IF;
END $$;
