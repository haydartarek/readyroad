-- ============================================================
-- Migration: V141
-- Description: Fix all stale/wrong image URLs in traffic_signs and road_signs
--   that produce 404 errors in the frontend.
--
-- Root causes:
--   A) traffic_signs: image_url retained old descriptive filenames after
--      asset files were renamed to short canonical names.
--   B) road_signs: image_path uses non-existent folders ('aanwijzingsborden',
--      'zoneborden') instead of the correct ones ('information_signs', 'zone_signs').
--   C) D5/D7 have no image assets and no quiz questions; remove them.
-- ============================================================

-- ── A. traffic_signs: individual path fixes ──────────────────────────────────

-- C43: current local reference for the prohibition sign
UPDATE traffic_signs
SET image_url = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 50 km.png'
WHERE sign_code = 'C43';

-- D1b: was titled "rechts afslaan"; actual sign and file is "links afslaan"
UPDATE traffic_signs
SET image_url = 'images/signs/mandatory_signs/D1b Verplichting links afslaan.png'
WHERE sign_code = 'D1b-links';

-- F45b: long descriptive name → canonical short name
UPDATE traffic_signs
SET image_url = 'images/signs/information_signs/F45b.png'
WHERE sign_code = 'F45b';

-- F50b: was pointing at "F50bis...fietsers" (non-existent) → canonical F50b.png
UPDATE traffic_signs
SET image_url = 'images/signs/information_signs/F50b.png'
WHERE sign_code = 'F50b';

-- F79: missing "(met afstandsaanduiding)" suffix
UPDATE traffic_signs
SET image_url = 'images/signs/information_signs/F79 Tijdelijke verdeling van de rijstroken (met afstandsaanduiding).png'
WHERE sign_code = 'F79';

-- GIII-aquaplaning: old descriptive name → canonical short name
UPDATE traffic_signs
SET image_url = 'images/signs/additional_signs/GIII-aquaplaning.png'
WHERE sign_code = 'GIII-aquaplaning';

-- GVIId-CARPOOL
UPDATE traffic_signs
SET image_url = 'images/signs/additional_signs/GVIId-CARPOOL.png'
WHERE sign_code = 'GVIId-CARPOOL';

-- GVIId-PR
UPDATE traffic_signs
SET image_url = 'images/signs/additional_signs/GVIId-PR.png'
WHERE sign_code = 'GVIId-PR';

-- M12-richtingen
UPDATE traffic_signs
SET image_url = 'images/signs/additional_signs/M12-richtingen.png'
WHERE sign_code = 'M12-richtingen';

-- MARK-R3
UPDATE traffic_signs
SET image_url = 'images/signs/delineation_signs/MARK-R3.png'
WHERE sign_code = 'MARK-R3';

-- TYPE delineation signs: replace verbose descriptive names with short canonical names
UPDATE traffic_signs SET image_url = 'images/signs/delineation_signs/TYPE-IA1.png' WHERE sign_code = 'TYPE-IA1';
UPDATE traffic_signs SET image_url = 'images/signs/delineation_signs/TYPE-IA2.png' WHERE sign_code = 'TYPE-IA2';
UPDATE traffic_signs SET image_url = 'images/signs/delineation_signs/TYPE-IB1.png' WHERE sign_code = 'TYPE-IB1';
UPDATE traffic_signs SET image_url = 'images/signs/delineation_signs/TYPE-IB2.png' WHERE sign_code = 'TYPE-IB2';
UPDATE traffic_signs SET image_url = 'images/signs/delineation_signs/TYPE-IC.png'  WHERE sign_code = 'TYPE-IC';
UPDATE traffic_signs SET image_url = 'images/signs/delineation_signs/TYPE-IIB.png' WHERE sign_code = 'TYPE-IIB';
UPDATE traffic_signs SET image_url = 'images/signs/delineation_signs/TYPE-V.png'   WHERE sign_code = 'TYPE-V';
UPDATE traffic_signs SET image_url = 'images/signs/delineation_signs/TYPE-VA.png'  WHERE sign_code = 'TYPE-VA';

-- ZC21 (begin zone): missing dash before description
UPDATE traffic_signs
SET image_url = 'images/signs/zone_signs/ZC21 - Zone verboden toegang voor bestuurders van voertuigen waarvan de massa hoger is dan de aangeduide massa.png'
WHERE sign_code = 'ZC21';

-- ZC21-zone: was pointing at "Einde zone..." (wrong) → begin-zone file
UPDATE traffic_signs
SET image_url = 'images/signs/zone_signs/ZC21 - Zone verboden toegang voor bestuurders van voertuigen waarvan de massa hoger is dan de aangeduide massa.png'
WHERE sign_code = 'ZC21-zone';

-- ZC21T (einde zone): said "Zone" → should be "Einde zone"
UPDATE traffic_signs
SET image_url = 'images/signs/zone_signs/ZC21T Einde zone verboden toegang voor bestuurders van voertuigen waarvan de massa hoger is dan de aangeduide massa.png'
WHERE sign_code = 'ZC21T';

-- ZC35 (begin zone): non-existent descriptive name → canonical short name
UPDATE traffic_signs
SET image_url = 'images/signs/zone_signs/ZC35.png'
WHERE sign_code = 'ZC35';

-- ZC35T (einde zone): said "Zone" (begin) → should be "Einde zone"
UPDATE traffic_signs
SET image_url = 'images/signs/zone_signs/ZC35T Einde zone verboden inhalen.png'
WHERE sign_code = 'ZC35T';

-- ── B. road_signs: individual path fixes ─────────────────────────────────────

-- C43: same as traffic_signs fix
UPDATE road_signs
SET image_path = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 50 km.png'
WHERE sign_code = 'C43';

-- F45b: long descriptive name → short canonical
UPDATE road_signs
SET image_path = 'images/signs/information_signs/F45b.png'
WHERE sign_code = 'F45b';

-- MARK-R3
UPDATE road_signs
SET image_path = 'images/signs/delineation_signs/MARK-R3.png'
WHERE sign_code = 'MARK-R3';

-- TYPE delineation signs
UPDATE road_signs SET image_path = 'images/signs/delineation_signs/TYPE-IA1.png' WHERE sign_code = 'TYPE-IA1';
UPDATE road_signs SET image_path = 'images/signs/delineation_signs/TYPE-IA2.png' WHERE sign_code = 'TYPE-IA2';
UPDATE road_signs SET image_path = 'images/signs/delineation_signs/TYPE-IB1.png' WHERE sign_code = 'TYPE-IB1';
UPDATE road_signs SET image_path = 'images/signs/delineation_signs/TYPE-IB2.png' WHERE sign_code = 'TYPE-IB2';
UPDATE road_signs SET image_path = 'images/signs/delineation_signs/TYPE-IC.png'  WHERE sign_code = 'TYPE-IC';
UPDATE road_signs SET image_path = 'images/signs/delineation_signs/TYPE-IIB.png' WHERE sign_code = 'TYPE-IIB';
UPDATE road_signs SET image_path = 'images/signs/delineation_signs/TYPE-V.png'   WHERE sign_code = 'TYPE-V';
UPDATE road_signs SET image_path = 'images/signs/delineation_signs/TYPE-VA.png'  WHERE sign_code = 'TYPE-VA';

-- ZC21-zone (wrong folder 'zoneborden' and wrong file)
UPDATE road_signs
SET image_path = 'images/signs/zone_signs/ZC21 - Zone verboden toegang voor bestuurders van voertuigen waarvan de massa hoger is dan de aangeduide massa.png'
WHERE sign_code = 'ZC21-zone';

-- ZE1-v1: wrong folder 'zoneborden' and wrong filename → canonical ZE1.png (begin-zone)
UPDATE road_signs
SET image_path = 'images/signs/zone_signs/ZE1.png'
WHERE sign_code = 'ZE1-v1';

-- ── B2. road_signs: bulk folder name fixes ────────────────────────────────────

-- 'aanwijzingsborden' folder does not exist → correct folder is 'information_signs'
UPDATE road_signs
SET image_path = REPLACE(image_path, 'images/signs/aanwijzingsborden/', 'images/signs/information_signs/')
WHERE image_path LIKE 'images/signs/aanwijzingsborden/%';

-- 'zoneborden' folder does not exist → correct folder is 'zone_signs'
-- (ZC21-zone and ZE1-v1 already fixed above; this catches any remaining rows)
UPDATE road_signs
SET image_path = REPLACE(image_path, 'images/signs/zoneborden/', 'images/signs/zone_signs/')
WHERE image_path LIKE 'images/signs/zoneborden/%';

-- ── C. Remove D5/D7: no image assets exist, no quiz questions, no road_signs rows ──
DELETE FROM traffic_signs WHERE sign_code IN ('D5', 'D7');
