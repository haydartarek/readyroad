-- Repair sign_code regressions introduced by V246.
--
-- V246 corrupted zone sign codes in two ways:
--   1. Truncated "Einde" suffix codes to bare dash codes (ZC21-Einde → ZC21-)
--   2. Renamed Zone-F* zone signs to wrong short information codes (Zone-F103 → F103)
--      and corrupted ZONE-F111 / ZONE-F113 case and identity.
--
-- This migration restores all canonical codes matching the disk-backed asset catalog.

-- ─── Restore truncated "Einde" zone codes ────────────────────────────────────
UPDATE road_signs SET sign_code = 'ZC21-Einde', normalized_sign_code = 'zc21_einde'
WHERE sign_code = 'ZC21-';

UPDATE road_signs SET sign_code = 'ZC35-Einde', normalized_sign_code = 'zc35_einde'
WHERE sign_code = 'ZC35-';

UPDATE road_signs SET sign_code = 'ZC5-Einde', normalized_sign_code = 'zc5_einde'
WHERE sign_code = 'ZC5-';

UPDATE road_signs SET sign_code = 'ZE1-Einde', normalized_sign_code = 'ze1_einde'
WHERE sign_code = 'ZE1-';

UPDATE road_signs SET sign_code = 'ZE9a-Einde', normalized_sign_code = 'ze9a_einde'
WHERE sign_code = 'ZE9a-';

UPDATE road_signs SET sign_code = 'ZE9aT-Einde', normalized_sign_code = 'ze9at_einde'
WHERE sign_code = 'ZE9aT-';

-- ─── Restore Zone-F* zone codes (wrongly shortened to bare F codes by V246) ──
UPDATE road_signs SET sign_code = 'Zone-F103', normalized_sign_code = 'zone_f103',
    category = 'ZONE'
WHERE sign_code = 'F103';

UPDATE road_signs SET sign_code = 'Zone-F105-Einde', normalized_sign_code = 'zone_f105_einde',
    category = 'ZONE'
WHERE sign_code = 'F105';

UPDATE road_signs SET sign_code = 'Zone-F117', normalized_sign_code = 'zone_f117',
    category = 'ZONE'
WHERE sign_code = 'F117';

UPDATE road_signs SET sign_code = 'Zone-F118-Einde', normalized_sign_code = 'zone_f118_einde',
    category = 'ZONE'
WHERE sign_code = 'F118';

UPDATE road_signs SET sign_code = 'Zone-F4a', normalized_sign_code = 'zone_f4a',
    category = 'ZONE'
WHERE sign_code = 'F4a';

UPDATE road_signs SET sign_code = 'Zone-F4b-Einde', normalized_sign_code = 'zone_f4b_einde',
    category = 'ZONE'
WHERE sign_code = 'F4b';

-- ─── Fix ZONE-F111 uppercase corruption → canonical Zone-F111 ────────────────
UPDATE road_signs SET sign_code = 'Zone-F111', normalized_sign_code = 'zone_f111'
WHERE sign_code = 'ZONE-F111';

-- ─── Fix ZONE-F113 (wrong code) → canonical Zone-F111-Einde ─────────────────
UPDATE road_signs SET sign_code = 'Zone-F111-Einde', normalized_sign_code = 'zone_f111_einde'
WHERE sign_code = 'ZONE-F113';

-- ─── Ensure ZC45-Einde uses canonical code (ZC45 if still present) ───────────
UPDATE road_signs SET sign_code = 'ZC45-Einde', normalized_sign_code = 'zc45_einde'
WHERE sign_code = 'ZC45';

-- ─── Remove orphan F50bis-fietsers (fabricated code inserted by V243/V245) ───
DELETE FROM road_signs WHERE sign_code = 'F50bis-fietsers';

-- ─── Ensure canonical F50-bis (voetgangers) sign exists ─────────────────────
INSERT INTO road_signs (sign_code, normalized_sign_code, category, image_path, serious_violation, is_active)
VALUES ('F50-bis', 'f50_bis', 'INFORMATION',
        '/images/signs/information_signs/F50-bis Opgepast als je van richting verandert voetgangers.png',
        0, 1) AS new_values
ON DUPLICATE KEY UPDATE
    normalized_sign_code = new_values.normalized_sign_code,
    category             = new_values.category,
    image_path           = new_values.image_path,
    serious_violation    = new_values.serious_violation,
    is_active            = new_values.is_active;
