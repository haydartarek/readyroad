-- V142: Fix stale image_path values in traffic_signs and road_signs
-- Root cause: V141 fixed image_url in traffic_signs but did NOT update image_path.
--             traffic_signs has BOTH image_url (used for sign detail pages)
--             and image_path (used for quiz/practice pages via SignQuizService).
--             These two must be in sync.
-- Also fixes: D1c and ZE1-v1 whose image_url was never corrected.

-- ── 1. Fix D1c: both columns still point to the non-existent "rechts afslaan" file ──
--    Correct image on disk: D1c Verplichting links aanhouden.png
UPDATE traffic_signs
SET   image_url  = 'images/signs/mandatory_signs/D1c Verplichting links aanhouden.png',
      image_path = 'images/signs/mandatory_signs/D1c Verplichting links aanhouden.png'
WHERE sign_code  = 'D1c';

UPDATE road_signs
SET   image_path = 'images/signs/mandatory_signs/D1c Verplichting links aanhouden.png'
WHERE sign_code  = 'D1c';

-- ── 2. Fix ZE1-v1: begin-zone sign; "ZE1 Zone parkeerverbod.png" does not exist on disk ──
--    Correct file is ZE1.png (the canonical short-name begin-zone image)
UPDATE traffic_signs
SET   image_url  = 'images/signs/zone_signs/ZE1.png',
      image_path = 'images/signs/zone_signs/ZE1.png'
WHERE sign_code  = 'ZE1-v1';

-- ── 3. Bulk sync: bring image_path in line with image_url for all remaining mismatches ──
--    V141 already corrected image_url; image_path was not updated by V141.
--    This covers: D1b, C43, F45b, F50b, F79, GIII-aquaplaning, GVIId-PR/CARPOOL,
--                 M12-richtingen, MARK-R3, TYPE-IA1/IA2/IB1/IB2/IC/IIB/V/VA,
--                 ZC21, ZC21-zone, ZC21T, ZC35, ZC35T and any others.
UPDATE traffic_signs
SET   image_path = image_url
WHERE image_path != image_url
  AND image_url   IS NOT NULL;
