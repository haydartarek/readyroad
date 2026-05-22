-- ════════════════════════════════════════════════════════════════════════════════
-- V250__Align_A1c_A1d_Names_With_JSON_Truth.sql
--
-- CORRECTIVE MIGRATION
-- Purpose : Align sign names for A1c and A1d in both traffic_signs and road_signs
--           with the canonical signs_import JSON source of truth.
--
-- Root causes
--   V144 set A1c traffic_signs names to the full image-filename label
--   ("Gevaarlijke dubbele of meer dan twee bochten, de eerste naar links") and A1d
--   EN/FR names to a similarly verbose form.  The JSON i18n names are shorter
--   official names used on sign posts and in the Belgian highway code.
--
--   V236 §1 set road_signs names to short forms for BOTH A1c and A1d.  The A1d
--   JSON however has a long Dutch name ("Gevaarlijke dubbele of meer dan twee
--   bochten, de eerste naar rechts") — so V236 left road_signs.name_nl for A1d
--   diverged from JSON.
--
-- Corrections
--   §1  traffic_signs — A1c: all 4 names → JSON short form
--   §2  traffic_signs — A1d: EN/FR/AR names → JSON short form
--                            (NL was already correct per JSON long form)
--   §3  road_signs    — A1d: name_nl → JSON long form
-- ════════════════════════════════════════════════════════════════════════════════

START TRANSACTION;

-- ────────────────────────────────────────────────────────────────────────────────
-- §1  road_signs — A1c names
--     V144 set the full image-filename label as the display name.
--     JSON i18n uses the concise parenthetical form.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    name_nl = 'Dubbele bocht (eerste naar links)',
       name_en = 'Double bend (first to the left)',
       name_fr = 'Double virage (premier à gauche)',
       name_ar = 'منعطف مزدوج (الأول إلى اليسار)'
WHERE  sign_code = 'A1c';

-- ────────────────────────────────────────────────────────────────────────────────
-- §2  road_signs — A1d EN/FR/AR names
--     V144 set "Dangerous double or more than two bends, first to the right" (EN)
--     and "Double virage dangereux ou plus de deux virages, le premier à droite" (FR).
--     JSON says "Double bend (first to the right)" and "Double virage (premier à droite)".
--     NL is already correct ("Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts")
--     and is NOT touched here.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    name_en = 'Double bend (first to the right)',
       name_fr = 'Double virage (premier à droite)',
       name_ar = 'منعطف مزدوج (الأول إلى اليمين)'
WHERE  sign_code = 'A1d';

-- ────────────────────────────────────────────────────────────────────────────────
-- §3  road_signs — A1d name_nl
--     V236 §1 set road_signs.name_nl for A1d to the short form "Dubbele bocht
--     (eerste naar rechts)".  The A1d JSON has the long Dutch name
--     "Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts".
--     EN/FR/AR road_signs names for A1d already match JSON and are not touched.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    name_nl = 'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts'
WHERE  sign_code = 'A1d';

COMMIT;
