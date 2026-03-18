-- V129: Fix M-serie (onderborden fietsen/bromfietsen) — categories, codes, names, image_urls
-- Problems found:
--   1. V104 ON DUPLICATE KEY UPDATE wrongly changed category_id from M → G for existing signs
--   2. V104 inserted new M-series variants (M3bis, M12-*) also with category G instead of M
--   3. V6 used code 'M3b'  — official Belgian PDF uses 'M3bis' (V104 later added M3bis as duplicate → delete M3b)
--   4. V6 used code 'M5b'  — official Belgian PDF uses 'M5bis' → rename
--   5. V9 overwrote M11/M12 name_nl with wrong "(type N)" strings
--   6. M3, M5bis, M6, M8, M11, M12, M14, M16, M18, M19, M20 still have old bicycle_signs/ image_url
--      (bicycle_signs/ folder does not exist on disk — images live in additional_signs/)

-- ─────────────────────────────────────────────────────────────
-- PART 1 : Restore correct category M for all M-serie signs
--          (V104 ON DUPLICATE KEY UPDATE wrongly set these to category G)
-- ─────────────────────────────────────────────────────────────
UPDATE traffic_signs
SET    category_id  = (SELECT id FROM categories WHERE code = 'M'),
       updated_at   = NOW()
WHERE  sign_code IN (
    'M1','M2','M4','M5','M7','M9','M10','M13','M15','M17',
    'M3bis','M12-30min','M12-richtingen','M12-fiets-brom'
);

-- ─────────────────────────────────────────────────────────────
-- PART 2 : Remove wrong duplicate 'M3b'
--          M3bis (official code) was added by V104; M3b is orphaned
-- ─────────────────────────────────────────────────────────────
DELETE FROM traffic_signs WHERE sign_code = 'M3b';

-- ─────────────────────────────────────────────────────────────
-- PART 3 : Rename 'M5b' → 'M5bis'  (official Belgian PDF spelling)
-- ─────────────────────────────────────────────────────────────
UPDATE traffic_signs
SET    sign_code   = 'M5bis',
       updated_at  = NOW()
WHERE  sign_code = 'M5b';

-- ─────────────────────────────────────────────────────────────
-- PART 4 : Fix official names for M11 and M12
--          V9 wrongly set name_nl = 'Uitgezonderd fietsers (type N)'
-- ─────────────────────────────────────────────────────────────
UPDATE traffic_signs
SET    name_nl     = 'Uitgezonderd fietsers en speed pedelecs',
       name_en     = 'Except cyclists and speed pedelecs',
       name_fr     = 'Sauf cyclistes et speed pedelecs',
       name_ar     = 'باستثناء راكبي الدراجات وسبيد بيديليك',
       updated_at  = NOW()
WHERE  sign_code = 'M11';

UPDATE traffic_signs
SET    name_nl     = 'Uitgezonderd fietsers, bromfietsers klasse A en speed pedelecs',
       name_en     = 'Except cyclists, class A mopeds and speed pedelecs',
       name_fr     = 'Sauf cyclistes, cyclomoteurs classe A et speed pedelecs',
       name_ar     = 'باستثناء راكبي الدراجات والدراجات البخارية فئة أ وسبيد بيديليك',
       updated_at  = NOW()
WHERE  sign_code = 'M12';

-- ─────────────────────────────────────────────────────────────
-- PART 5 : Fix image_url for signs NOT covered by V118
--          All M-serie images live under additional_signs/
--          (Images without a disk file still get a correct URL format
--           so they show the standard fallback instead of a broken path)
-- ─────────────────────────────────────────────────────────────
UPDATE traffic_signs
SET    image_url   = 'images/signs/additional_signs/M3 Uitgezonderd fietsers en bromfietsers klasse A.png',
       updated_at  = NOW()
WHERE  sign_code = 'M3';

UPDATE traffic_signs
SET    image_url   = 'images/signs/additional_signs/M5bis fietsers, bromfietsers klasse A, B en speed pedelecs mogen in 2 richtingen.png',
       updated_at  = NOW()
WHERE  sign_code = 'M5bis';

UPDATE traffic_signs
SET    image_url   = 'images/signs/additional_signs/M6 Verplichting voor bromfietsen klasse B.png',
       updated_at  = NOW()
WHERE  sign_code = 'M6';

UPDATE traffic_signs
SET    image_url   = 'images/signs/additional_signs/M8 Enkel voor fietsers en bromfietsers.png',
       updated_at  = NOW()
WHERE  sign_code = 'M8';

UPDATE traffic_signs
SET    image_url   = 'images/signs/additional_signs/M11 Uitgezonderd fietsers en speed pedelecs.png',
       updated_at  = NOW()
WHERE  sign_code = 'M11';

UPDATE traffic_signs
SET    image_url   = 'images/signs/additional_signs/M12 Uitgezonderd fietsers, bromfietsers klasse A en speed pedelecs.png',
       updated_at  = NOW()
WHERE  sign_code = 'M12';

UPDATE traffic_signs
SET    image_url   = 'images/signs/additional_signs/M14 Verplichting voor bromfietsen klasse B en speed pedelecs.png',
       updated_at  = NOW()
WHERE  sign_code = 'M14';

UPDATE traffic_signs
SET    image_url   = 'images/signs/additional_signs/M16 Verbod voor bromfietsen klasse B en speed pedelecs.png',
       updated_at  = NOW()
WHERE  sign_code = 'M16';

UPDATE traffic_signs
SET    image_url   = 'images/signs/additional_signs/M18 Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.png',
       updated_at  = NOW()
WHERE  sign_code = 'M18';

UPDATE traffic_signs
SET    image_url   = 'images/signs/additional_signs/M19 Enkel voor speed pedelecs.png',
       updated_at  = NOW()
WHERE  sign_code = 'M19';

UPDATE traffic_signs
SET    image_url   = 'images/signs/additional_signs/M20 Enkel voor fietsers en speed pedelecs.png',
       updated_at  = NOW()
WHERE  sign_code = 'M20';

