-- =============================================================================
-- V145: Fix stale name_nl in road_signs for all A-series danger signs
--       Fix A19 name_nl in traffic_signs
--       Fix A33-v1 image_path prefix in road_signs (gevaarsborden -> danger_signs)
-- =============================================================================
-- Root cause: road_signs.name_nl was never updated by V143/V144 which only
-- updated image_path. traffic_signs got name updates but road_signs keeps a
-- separate name_nl column that still holds pre-V143 stale values.
-- A33-v1 road_signs image_path still uses the old assets/signs/gevaarsborden prefix.
-- =============================================================================

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 1: traffic_signs — fix A19 name_nl (missing "links" direction)
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE traffic_signs
SET    name_nl     = 'Vallende stenen links',
       name_en     = 'Falling rocks on the left',
       name_fr     = 'Chutes de pierres à gauche',
       updated_at  = NOW()
WHERE  sign_code = 'A19';

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 2: road_signs — name_nl corrections (19 rows)
-- ─────────────────────────────────────────────────────────────────────────────

UPDATE road_signs SET name_nl = 'Gevaarlijke bocht naar links',   name_en = 'Dangerous curve to the left',   name_fr = 'Virage dangereux à gauche',   updated_at = NOW() WHERE sign_code = 'A1a';
UPDATE road_signs SET name_nl = 'Gevaarlijke bocht naar recht',   name_en = 'Dangerous curve to the right',  name_fr = 'Virage dangereux à droite',   updated_at = NOW() WHERE sign_code = 'A1b';

UPDATE road_signs
SET    name_nl = 'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar links',
       name_en = 'Dangerous double or more bends, first to the left',
       name_fr = 'Double virage dangereux ou plus, le premier à gauche',
       updated_at = NOW()
WHERE  sign_code = 'A1c';

UPDATE road_signs
SET    name_nl = 'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts',
       name_en = 'Dangerous double or more bends, first to the right',
       name_fr = 'Double virage dangereux ou plus, le premier à droite',
       updated_at = NOW()
WHERE  sign_code = 'A1d';

UPDATE road_signs SET name_nl = 'Versmalling links',              name_en = 'Road narrows on the left',      name_fr = 'Rétrécissement à gauche',     updated_at = NOW() WHERE sign_code = 'A7b';
UPDATE road_signs SET name_nl = 'Versmalling recht',              name_en = 'Road narrows on the right',     name_fr = 'Rétrécissement à droite',     updated_at = NOW() WHERE sign_code = 'A7c';
UPDATE road_signs SET name_nl = 'Uitweg op kaai of oever',        name_en = 'Exit to quay or embankment',    name_fr = 'Sortie sur quai ou berge',    updated_at = NOW() WHERE sign_code = 'A11';
UPDATE road_signs SET name_nl = 'Gladde rijbaan - Slipgevaar',    name_en = 'Slippery road',                 name_fr = 'Chaussée glissante',          updated_at = NOW() WHERE sign_code = 'A15';
UPDATE road_signs SET name_nl = 'Kiezelprojectie',                name_en = 'Gravel projection',             name_fr = 'Projection de gravillons',    updated_at = NOW() WHERE sign_code = 'A17';
UPDATE road_signs SET name_nl = 'Vallende stenen links',          name_en = 'Falling rocks on the left',    name_fr = 'Chutes de pierres à gauche',  updated_at = NOW() WHERE sign_code = 'A19';
UPDATE road_signs SET name_nl = 'Oversteekplaats voor voetgangers', name_en = 'Pedestrian crossing',         name_fr = 'Passage pour piétons',        updated_at = NOW() WHERE sign_code = 'A21';
UPDATE road_signs SET name_nl = 'Opgelet kinderen',               name_en = 'Watch out for children',       name_fr = 'Attention enfants',           updated_at = NOW() WHERE sign_code = 'A23';

UPDATE road_signs
SET    name_nl = 'Oversteekplaats voor fietsers en bromfietsers',
       name_en = 'Crossing for cyclists and moped riders',
       name_fr = 'Passage pour cyclistes et cyclomotoristes',
       updated_at = NOW()
WHERE  sign_code = 'A25';

UPDATE road_signs SET name_nl = 'Vliegtuigen op geringe hoogte',  name_en = 'Low-flying aircraft',           name_fr = 'Avions à basse altitude',     updated_at = NOW() WHERE sign_code = 'A35';

UPDATE road_signs
SET    name_nl = 'Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer',
       name_en = 'Two-way traffic allowed after one-way section',
       name_fr = 'Circulation à double sens après un tronçon à sens unique',
       updated_at = NOW()
WHERE  sign_code = 'A39';

UPDATE road_signs SET name_nl = 'waarschuwings kruis',            name_en = 'Warning cross',                 name_fr = 'Croix de signalisation',      updated_at = NOW() WHERE sign_code = 'A45';
UPDATE road_signs SET name_nl = 'waarschuwingskruis meerdere sporen', name_en = 'Warning cross multiple tracks', name_fr = 'Croix de signalisation plusieurs voies', updated_at = NOW() WHERE sign_code = 'A47';

UPDATE road_signs
SET    name_nl = 'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen',
       name_en = 'Public road crosses one or more tracks laid in the roadway',
       name_fr = 'Route publique croisant un ou plusieurs rails dans la chaussée',
       updated_at = NOW()
WHERE  sign_code = 'A49';

UPDATE road_signs
SET    name_nl = 'Gevaar dat niet door een speciaal symbool wordt bepaald',
       name_en = 'General hazard not indicated by a special symbol',
       name_fr = 'Danger non indiqué par un symbole particulier',
       updated_at = NOW()
WHERE  sign_code = 'A51';

-- ─────────────────────────────────────────────────────────────────────────────
-- SECTION 3: road_signs — fix A33-v1 image_path prefix
-- Was: images/signs/gevaarsborden/A33-v1 Verkeerslichten.png
-- Should be: images/signs/danger_signs/A33-v1 Verkeerslichten.png
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE road_signs
SET    image_path  = 'images/signs/danger_signs/A33-v1 Verkeerslichten.png',
       updated_at  = NOW()
WHERE  sign_code = 'A33-v1';
