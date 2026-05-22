-- ════════════════════════════════════════════════════════════════════════════════
-- V253__Align_Traffic_Signs_Zone_And_F50bis_Image_Paths.sql
--
-- CORRECTIVE MIGRATION — signs_184 asset-sync audit
-- Purpose : Fix traffic_signs image_url/image_path and sign_code/category
--           discrepancies discovered by comparing the canonical asset catalog
--           (public/images/signs) against the DB state left by V170 and earlier
--           migrations.  Also restores the F50bis-fietsers (cyclist) record in
--           road_signs that was deleted by V247.
--
-- Root causes
--   V170 reset zone signs by DELETE + re-INSERT using image paths with a space
--     between the dash and "Einde" (e.g. "ZE1- Einde …") but every file on disk
--     omits that space ("ZE1-Einde …").  V170 also used stale filenames for
--     ZONE-F111 and ZONE-F113, and set sign_code to uppercase ZONE-F111/F113
--     rather than the canonical Zone-F111/Zone-F111-Einde.
--   V162 / V127 left Zone-F103, Zone-F105-Einde, Zone-F117, Zone-F118-Einde,
--     Zone-F4a, and Zone-F4b-Einde in traffic_signs under bare F-codes with
--     category INFORMATION and image_url pointing to non-existent paths in
--     information_signs/ rather than zone_signs/Zone-F…
--   V179 set traffic_signs.image_url for F50bis (pedestrian) to
--     "F50bis Opgepast als je van richting verandert voetgangers.png" (no hyphen)
--     but the actual file on disk is "F50-bis … voetgangers.png" (with hyphen).
--   V247 deleted F50bis-fietsers (cyclist) from road_signs; the physical asset
--     and signs_import JSON still exist so the road_signs record must be restored.
--
-- Section map
--   §1   traffic_signs — F50bis image_url (missing hyphen)
--   §2   traffic_signs — ZE1-Einde / ZE9a-Einde / ZE9aT-Einde: sign_code +
--                        image_path (space-after-dash → no-space)
--   §3   traffic_signs — ZC5-Einde / ZC21-Einde / ZC35-Einde: same space fix
--   §4   traffic_signs — ZC45: sign_code ZC45 → ZC45-Einde + image_path
--   §5   traffic_signs — ZONE-F111 → Zone-F111: sign_code + image_path
--   §6   traffic_signs — ZONE-F113 → Zone-F111-Einde: sign_code + image_path
--   §7   traffic_signs — F103 → Zone-F103: sign_code + category + image paths
--   §8   traffic_signs — F105 → Zone-F105-Einde: sign_code + category + images
--   §9   traffic_signs — F117 → Zone-F117: sign_code + category + image paths
--   §10  traffic_signs — F118 → Zone-F118-Einde: sign_code + category + images
--   §11  traffic_signs — F4a → Zone-F4a: sign_code + category + image paths
--   §12  traffic_signs — F4b → Zone-F4b-Einde: sign_code + category + images
--   §13  road_signs — restore F50bis-fietsers (cyclist, deleted by V247)
-- ════════════════════════════════════════════════════════════════════════════════

START TRANSACTION;

-- ────────────────────────────────────────────────────────────────────────────────
-- §1  traffic_signs — F50bis image_url
--     V179 set the pedestrian sign image_url without the hyphen in the filename.
--     Actual disk file: "F50-bis Opgepast als je van richting verandert voetgangers.png"
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    image_path = '/images/signs/information_signs/F50-bis Opgepast als je van richting verandert voetgangers.png'
WHERE  sign_code = 'F50bis';

-- ────────────────────────────────────────────────────────────────────────────────
-- §2  traffic_signs — ZE1-Einde / ZE9a-Einde / ZE9aT-Einde
--     V170 inserted image_path with a space between the dash and "Einde"
--     (e.g. "ZE1- Einde zone …").  Disk files never have that space.
--     Also corrects the sign_code from the truncated "-" suffix form to the
--     canonical "-Einde" suffix form that V247 already applied to road_signs.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    sign_code            = 'ZE1-Einde',
       normalized_sign_code = 'ze1_einde',
       image_path           = '/images/signs/zone_signs/ZE1-Einde zone parkeerverbod.png'
WHERE  sign_code = 'ZE1-';

UPDATE road_signs
SET    sign_code            = 'ZE9a-Einde',
       normalized_sign_code = 'ze9a_einde',
       image_path           = '/images/signs/zone_signs/ZE9a-Einde zone parkeren uitsluitend voor auto''s.png'
WHERE  sign_code = 'ZE9a-';

UPDATE road_signs
SET    sign_code            = 'ZE9aT-Einde',
       normalized_sign_code = 'ze9at_einde',
       image_path           = '/images/signs/zone_signs/ZE9aT-Einde zone parkeren uitsluitend voor auto''s.png'
WHERE  sign_code = 'ZE9aT-';

-- ────────────────────────────────────────────────────────────────────────────────
-- §3  traffic_signs — ZC5-Einde / ZC21-Einde / ZC35-Einde
--     Same space-after-dash and sign_code suffix corrections as §2.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    sign_code            = 'ZC5-Einde',
       normalized_sign_code = 'zc5_einde',
       image_path           = '/images/signs/zone_signs/ZC5-Einde zone verboden toegang voor motorvoertuigen met meer dan 2 wielen.png'
WHERE  sign_code = 'ZC5-';

UPDATE road_signs
SET    sign_code            = 'ZC21-Einde',
       normalized_sign_code = 'zc21_einde',
       image_path           = '/images/signs/zone_signs/ZC21-Einde zone verboden toegang voor bestuurders van voertuigen waarvan de massa hoger dan 3500 kg.png'
WHERE  sign_code = 'ZC21-';

UPDATE road_signs
SET    sign_code            = 'ZC35-Einde',
       normalized_sign_code = 'zc35_einde',
       image_path           = '/images/signs/zone_signs/ZC35-Einde zone verboden inhalen.png'
WHERE  sign_code = 'ZC35-';

-- ────────────────────────────────────────────────────────────────────────────────
-- §4  traffic_signs — ZC45-Einde
--     V170 used sign_code 'ZC45' and image_path
--     "ZC45 Einde zone met een snelheidsbeperking.png" (no dash before Einde).
--     Disk file: "ZC45-Einde zone met een snelheidsbeperking.png".
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    sign_code            = 'ZC45-Einde',
       normalized_sign_code = 'zc45_einde',
       image_path           = '/images/signs/zone_signs/ZC45-Einde zone met een snelheidsbeperking.png'
WHERE  sign_code = 'ZC45';

-- ────────────────────────────────────────────────────────────────────────────────
-- §5  traffic_signs — Zone-F111
--     V170 used sign_code 'ZONE-F111' and image_path
--     "ZONE F111- ZONE Fietsstraat.png".
--     Disk file: "Zone-F111 Zone Fietsstraat.png".
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    sign_code            = 'Zone-F111',
       normalized_sign_code = 'zone_f111',
       image_path           = '/images/signs/zone_signs/Zone-F111 Zone Fietsstraat.png'
WHERE  sign_code = 'ZONE-F111';

-- ────────────────────────────────────────────────────────────────────────────────
-- §6  traffic_signs — Zone-F111-Einde
--     V170 used sign_code 'ZONE-F113' and image_path
--     "ZONE F113- Einde ZONE Fietsstraat.png".
--     Canonical sign_code: Zone-F111-Einde.
--     Disk file: "Zone-F111-Eind zone Fietsstraat.png".
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    sign_code            = 'Zone-F111-Einde',
       normalized_sign_code = 'zone_f111_einde',
       image_path           = '/images/signs/zone_signs/Zone-F111-Eind zone Fietsstraat.png'
WHERE  sign_code = 'ZONE-F113';

-- ────────────────────────────────────────────────────────────────────────────────
-- §7  traffic_signs — Zone-F103
--     V127/V162 set sign_code 'F103', category INFORMATION, and image_url
--     'images/signs/information_signs/F103 Begin van een voetgangerszone.png'.
--     Disk file: zone_signs/Zone-F103 Begin van een voetgangerszone.png.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    sign_code            = 'Zone-F103',
       normalized_sign_code = 'zone_f103',
       category             = 'ZONE',
       image_path           = '/images/signs/zone_signs/Zone-F103 Begin van een voetgangerszone.png'
WHERE  sign_code = 'F103';

-- ────────────────────────────────────────────────────────────────────────────────
-- §8  traffic_signs — Zone-F105-Einde
--     V127/V162 set sign_code 'F105', category INFORMATION, and image_url
--     'images/signs/information_signs/F105 Einde van een voetgangerszone.png'.
--     Disk file: zone_signs/Zone-F105-Einde zone van een voetgangerszone.png.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    sign_code            = 'Zone-F105-Einde',
       normalized_sign_code = 'zone_f105_einde',
       category             = 'ZONE',
       image_path           = '/images/signs/zone_signs/Zone-F105-Einde zone van een voetgangerszone.png'
WHERE  sign_code = 'F105';

-- ────────────────────────────────────────────────────────────────────────────────
-- §9  traffic_signs — Zone-F117
--     V162 set sign_code 'F117', category INFORMATION, and image_url
--     'images/signs/information_signs/F117 Begin van lage emissiezone.png'.
--     Disk file: zone_signs/Zone-F117 Begin van lage emissiezone.png.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    sign_code            = 'Zone-F117',
       normalized_sign_code = 'zone_f117',
       category             = 'ZONE',
       image_path           = '/images/signs/zone_signs/Zone-F117 Begin van lage emissiezone.png'
WHERE  sign_code = 'F117';

-- ────────────────────────────────────────────────────────────────────────────────
-- §10  traffic_signs — Zone-F118-Einde
--      V162 set sign_code 'F118', category INFORMATION, and image_url
--      'images/signs/information_signs/F118 Einde van lage emissiezone.png'.
--      Disk file: zone_signs/Zone-F118-Einde van lage emissiezone.png.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    sign_code            = 'Zone-F118-Einde',
       normalized_sign_code = 'zone_f118_einde',
       category             = 'ZONE',
       image_path           = '/images/signs/zone_signs/Zone-F118-Einde van lage emissiezone.png'
WHERE  sign_code = 'F118';

-- ────────────────────────────────────────────────────────────────────────────────
-- §11  traffic_signs — Zone-F4a
--      V162/V118 set sign_code 'F4a', category INFORMATION, and image_url
--      'images/signs/information_signs/F4a Zone 30 km.png'.
--      Disk file: zone_signs/Zone-F4a Zone 30 km.png.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    sign_code            = 'Zone-F4a',
       normalized_sign_code = 'zone_f4a',
       category             = 'ZONE',
       image_path           = '/images/signs/zone_signs/Zone-F4a Zone 30 km.png'
WHERE  sign_code = 'F4a';

-- ────────────────────────────────────────────────────────────────────────────────
-- §12  traffic_signs — Zone-F4b-Einde
--      V162/V118 set sign_code 'F4b', category INFORMATION, and image_url
--      'images/signs/information_signs/F4b - Einde zone 30 km.png'.
--      Disk file: zone_signs/Zone-F4b-Einde zone 30 km.png.
-- ────────────────────────────────────────────────────────────────────────────────

UPDATE road_signs
SET    sign_code            = 'Zone-F4b-Einde',
       normalized_sign_code = 'zone_f4b_einde',
       category             = 'ZONE',
       image_path           = '/images/signs/zone_signs/Zone-F4b-Einde zone 30 km.png'
WHERE  sign_code = 'F4b';

-- ────────────────────────────────────────────────────────────────────────────────
-- §13  road_signs — restore F50bis-fietsers (cyclist)
--      V247 deleted this record ("orphan fabricated by V243/V245") but the
--      physical asset and signs_import JSON both exist.  The cyclist sign
--      'F50bis Opgepast als je van richting veranderd, fietsers.png' must have
--      a road_signs record so it is reachable through the sign browser and quiz
--      systems.
-- ────────────────────────────────────────────────────────────────────────────────

INSERT INTO road_signs (sign_code, normalized_sign_code, category, image_path, serious_violation, is_active)
VALUES ('F50bis-fietsers', 'f50bis_fietsers', 'INFORMATION',
        '/images/signs/information_signs/F50bis Opgepast als je van richting veranderd, fietsers.png',
        0, 1)
AS new_values
ON DUPLICATE KEY UPDATE
    normalized_sign_code = new_values.normalized_sign_code,
    category             = new_values.category,
    image_path           = new_values.image_path,
    serious_violation    = new_values.serious_violation,
    is_active            = new_values.is_active;

COMMIT;
