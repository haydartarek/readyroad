-- ════════════════════════════════════════════════════════════════════════════════
-- V251__Align_V144_Traffic_Signs_Names_With_JSON_Truth.sql
--
-- CORRECTIVE MIGRATION
-- Purpose : Fix traffic_signs name fields for A1b, A7a, A7b, A7c, A11, A45, and
--           A47 where V144 set names from image-filename labels or incorrect
--           identifiers rather than the canonical JSON i18n names.
--
-- Root cause (V144)
--   V144 updated sign names to "match filename labels" — a policy that diverges
--   from the signs_import JSON which uses the official Belgian highway code names.
--   Most critical: A45/A47 were given the name of the Saint Andrew's cross warning
--   symbol ("waarschuwings kruis" / "Warning cross") instead of the level crossing
--   sign names ("Overweg voor enkel spoor" / "Overweg voor twee of meer sporen").
--
-- Section map
--   §1  traffic_signs — A1b: name_nl (missing trailing 's' introduced by V144)
--   §2  traffic_signs — A7a: all 4 names (V144 used wrong NL/EN/FR; AR unset)
--   §3  traffic_signs — A7b: NL/FR/AR names (EN was already correct)
--   §4  traffic_signs — A7c: NL/FR/AR names (EN was already correct)
--   §5  traffic_signs — A11: all 4 names (V144 dropped articles and wrong EN)
--   §6  traffic_signs — A45: all 4 names (V144 used warning-cross names)
--   §7  traffic_signs — A47: all 4 names (V144 used warning-cross names)
-- ════════════════════════════════════════════════════════════════════════════════

START TRANSACTION;

-- ────────────────────────────────────────────────────────────────────────────────
-- §1  traffic_signs — A1b name_nl
--     V144 set 'Gevaarlijke bocht naar recht' (missing trailing 's').
--     JSON name_nl: 'Gevaarlijke bocht naar rechts'.
--     Only name_nl is fixed here; EN/FR/AR for A1b were not changed by V144.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    name_nl = 'Gevaarlijke bocht naar rechts'
WHERE  sign_code = 'A1b';

-- ────────────────────────────────────────────────────────────────────────────────
-- §2  traffic_signs — A7a all names
--     V144 set "Versmalling langs beide zijden" / "Road narrows on both sides" /
--     "Rétrécissement des deux côtés" — all expanded over the JSON concise forms.
--     JSON: "Rijbaanversmalling" / "Road narrows" / "Chaussée rétrécie" / "تضييق الطريق"
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    name_nl = 'Rijbaanversmalling',
       name_en = 'Road narrows',
       name_fr = 'Chaussée rétrécie',
       name_ar = 'تضييق الطريق'
WHERE  sign_code = 'A7a';

-- ────────────────────────────────────────────────────────────────────────────────
-- §3  traffic_signs — A7b NL/FR/AR names
--     V144 shortened NL to 'Versmalling links' and FR to 'Rétrécissement à gauche'
--     and set AR 'تضيق الطريق من جهة اليسار' (incorrect form).
--     JSON NL: 'Rijbaanversmalling langs links', FR: 'Chaussée rétrécie à gauche',
--     AR: 'تضييق الطريق من اليسار'.
--     EN 'Road narrows on the left' was already correct.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    name_nl = 'Rijbaanversmalling langs links',
       name_fr = 'Chaussée rétrécie à gauche',
       name_ar = 'تضييق الطريق من اليسار'
WHERE  sign_code = 'A7b';

-- ────────────────────────────────────────────────────────────────────────────────
-- §4  traffic_signs — A7c NL/FR/AR names
--     V144 shortened NL to 'Versmalling recht' and FR to 'Rétrécissement à droite'
--     and set AR 'تضيق الطريق من جهة اليمين' (incorrect form).
--     JSON NL: 'Rijbaanversmalling langs rechts', FR: 'Chaussée rétrécie à droite',
--     AR: 'تضييق الطريق من اليمين'.
--     EN 'Road narrows on the right' was already correct.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    name_nl = 'Rijbaanversmalling langs rechts',
       name_fr = 'Chaussée rétrécie à droite',
       name_ar = 'تضييق الطريق من اليمين'
WHERE  sign_code = 'A7c';

-- ────────────────────────────────────────────────────────────────────────────────
-- §5  traffic_signs — A11 all names
--     V144 dropped the "een" articles from NL, used a wrong EN translation
--     "Exit to quay or embankment" (JSON: "Quay or riverbank ahead"), and set
--     FR/AR that diverge from JSON.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    name_nl = 'Uitweg op een kaai of een oever',
       name_en = 'Quay or riverbank ahead',
       name_fr = 'Quai ou berge',
       name_ar = 'نهاية الطريق عند رصيف أو ضفة'
WHERE  sign_code = 'A11';

-- ────────────────────────────────────────────────────────────────────────────────
-- §6  traffic_signs — A45 all names
--     V144 set the name of the Saint Andrew's cross symbol ("waarschuwings kruis"
--     / "Warning cross") instead of the official level crossing sign name.
--     JSON: "Overweg voor enkel spoor" / "Single-track level crossing (Saint
--     Andrew's cross)" / "Passage à niveau à une voie" / "معبر سكة حديد بمسار واحد"
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    name_nl = 'Overweg voor enkel spoor',
       name_en = 'Single-track level crossing (Saint Andrew''s cross)',
       name_fr = 'Passage à niveau à une voie',
       name_ar = 'معبر سكة حديد بمسار واحد'
WHERE  sign_code = 'A45';

-- ────────────────────────────────────────────────────────────────────────────────
-- §7  traffic_signs — A47 all names
--     V144 set "waarschuwingskruis meerdere sporen" / "Warning cross multiple
--     tracks" — again using the warning-cross conceptual name instead of the
--     official level crossing sign name from JSON.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    name_nl = 'Overweg voor twee of meer sporen',
       name_en = 'Level crossing with multiple tracks',
       name_fr = 'Passage à niveau à plusieurs voies',
       name_ar = 'معبر سكة حديد بعدة مسارات'
WHERE  sign_code = 'A47';

COMMIT;
