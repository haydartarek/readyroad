-- ════════════════════════════════════════════════════════════════════════════════
-- V252__Align_V127_F_Series_Traffic_Signs_Names_With_JSON_Truth.sql
--
-- CORRECTIVE MIGRATION
-- Purpose : Fix traffic_signs name fields for F12a, F12b, F50bis, F101a, F101b,
--           and F101c where V127 set verbose or incorrect names that diverge from
--           the canonical signs_import JSON source of truth.
--
-- Root cause (V127)
--   V127 updated traffic_signs names for F-series signs using verbose descriptive
--   forms or incorrect text instead of the official concise names from the JSON.
--   Key examples:
--     F12a/F12b: set "Begin/Einde van een woonerf of van een erf" (verbose) instead
--                of the official "Begin/Einde woonerf"
--     F50bis: set shorter/incorrect advisory form instead of the JSON's specific
--             "Opgepast bij richtingsverandering - voetgangers rechts"
--     F101a/b/c: set verbose forms with "speed pedelecs" or incorrect content
--                instead of the official JSON concise names
--
-- Section map
--   §1  traffic_signs — F12a: all 4 names (V127 used verbose "woonerf of erf" form)
--   §2  traffic_signs — F12b: all 4 names (V127 used verbose "woonerf of erf" form)
--   §3  traffic_signs — F50bis: all 4 names (V127 set wrong advisory form)
--   §4  traffic_signs — F101a: all 4 names (V127 added "speed pedelecs")
--   §5  traffic_signs — F101b: all 4 names (V127 set completely different text)
--   §6  traffic_signs — F101c: all 4 names (V127 wrong order/content)
-- ════════════════════════════════════════════════════════════════════════════════

START TRANSACTION;

-- ────────────────────────────────────────────────────────────────────────────────
-- §1  traffic_signs — F12a all names
--     V127 set "Begin van een woonerf of van een erf" (NL) and the equivalent
--     verbose forms in EN/FR/AR.
--     JSON name_nl: "Begin woonerf", EN: "Start of residential area",
--     FR: "Début de zone résidentielle", AR: "بداية الحي السكني".
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    name_nl = 'Begin woonerf',
       name_en = 'Start of residential area',
       name_fr = 'Début de zone résidentielle',
       name_ar = 'بداية الحي السكني'
WHERE  sign_code = 'F12a';

-- ────────────────────────────────────────────────────────────────────────────────
-- §2  traffic_signs — F12b all names
--     V127 set "Einde van een woonerf of van een erf" (NL) and equivalent verbose
--     forms in EN/FR/AR.
--     JSON name_nl: "Einde woonerf", EN: "End of residential area",
--     FR: "Fin de zone résidentielle", AR: "نهاية الحي السكني".
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    name_nl = 'Einde woonerf',
       name_en = 'End of residential area',
       name_fr = 'Fin de zone résidentielle',
       name_ar = 'نهاية الحي السكني'
WHERE  sign_code = 'F12b';

-- ────────────────────────────────────────────────────────────────────────────────
-- §3  traffic_signs — F50bis all names
--     V127 set a shorter/incorrect advisory form for this pedestrian caution sign.
--     JSON name_nl: "Opgepast bij richtingsverandering - voetgangers rechts",
--     EN: "Caution when changing direction - pedestrians on the right",
--     FR: "Attention au changement de direction - piétons à droite",
--     AR: "تحذير من عبور المشاة على يمين الطريق".
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    name_nl = 'Opgepast bij richtingsverandering - voetgangers rechts',
       name_en = 'Caution when changing direction - pedestrians on the right',
       name_fr = 'Attention au changement de direction - piétons à droite',
       name_ar = 'تحذير من عبور المشاة على يمين الطريق'
WHERE  sign_code = 'F50bis';

-- ────────────────────────────────────────────────────────────────────────────────
-- §4  traffic_signs — F101a all names
--     V127 added "speed pedelecs" (snelheidsgemachtigde pedelecs) to the sign name,
--     which is not present in the JSON.
--     JSON name_nl: "Einde weg voorbehouden voor voetgangers, fietsers en ruiters",
--     EN: "End of path reserved for pedestrians, cyclists and horse riders",
--     FR: "Fin de chemin réservé aux piétons, cyclistes et cavaliers",
--     AR: "نهاية المسار المشترك للمشاة والدراجات والخيالة".
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    name_nl = 'Einde weg voorbehouden voor voetgangers, fietsers en ruiters',
       name_en = 'End of path reserved for pedestrians, cyclists and horse riders',
       name_fr = 'Fin de chemin réservé aux piétons, cyclistes et cavaliers',
       name_ar = 'نهاية المسار المشترك للمشاة والدراجات والخيالة'
WHERE  sign_code = 'F101a';

-- ────────────────────────────────────────────────────────────────────────────────
-- §5  traffic_signs — F101b all names
--     V127 set "Einde deel van de openbare weg voorbehouden voor fietsers en
--     voetgangers" — a completely different description than the JSON.
--     JSON name_nl: "Einde gescheiden pad voor voetgangers en fietsers",
--     EN: "End of separated path for pedestrians and cyclists",
--     FR: "Fin du chemin séparé pour piétons et cyclistes",
--     AR: "نهاية المسار المنفصل للمشاة وراكبي الدراجات".
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    name_nl = 'Einde gescheiden pad voor voetgangers en fietsers',
       name_en = 'End of separated path for pedestrians and cyclists',
       name_fr = 'Fin du chemin séparé pour piétons et cyclistes',
       name_ar = 'نهاية المسار المنفصل للمشاة وراكبي الدراجات'
WHERE  sign_code = 'F101b';

-- ────────────────────────────────────────────────────────────────────────────────
-- §6  traffic_signs — F101c all names
--     V127 set a verbose form with incorrect ordering and "speed pedelecs" content
--     not present in the JSON.
--     JSON name_nl: "Einde weg voorbehouden voor voetgangers, fietsers, ruiters en
--     landbouwvoertuigen",
--     EN: "End of path reserved for pedestrians, cyclists, horse riders and
--     agricultural vehicles",
--     FR: "Fin de chemin réservé aux piétons, cyclistes, cavaliers et véhicules
--     agricoles",
--     AR: "نهاية المسار للمشاة والدراجات والمركبات الزراعية".
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    name_nl = 'Einde weg voorbehouden voor voetgangers, fietsers, ruiters en landbouwvoertuigen',
       name_en = 'End of path reserved for pedestrians, cyclists, horse riders and agricultural vehicles',
       name_fr = 'Fin de chemin réservé aux piétons, cyclistes, cavaliers et véhicules agricoles',
       name_ar = 'نهاية المسار للمشاة والدراجات والمركبات الزراعية'
WHERE  sign_code = 'F101c';

COMMIT;
