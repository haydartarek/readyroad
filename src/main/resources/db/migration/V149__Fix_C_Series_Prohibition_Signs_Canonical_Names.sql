-- V149: Enforce exact canonical prohibition_signs filenames across DB
-- Canonical source of truth: readyroad_front_end/web_app/public/images/signs/prohibition_signs/
-- Rules: Never rename image files. Fix all DB references to match exact disk filenames.

-- ─── traffic_signs ────────────────────────────────────────────────────────────

-- C22a: was "C22a...autobussen.png" → canonical disk file is "C22 Verboden...autocars.png"
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C22 Verboden toegang voor bestuurders van autocars.png',
    image_path = 'images/signs/prohibition_signs/C22 Verboden toegang voor bestuurders van autocars.png'
WHERE sign_code = 'C22a';

-- C39: was double-space, missing "groter dan" → canonical "groter dan 3500 kg"
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C39 Verbod voertuigen met toegelaten massa groter dan 3500 kg in te halen.png',
    image_path = 'images/signs/prohibition_signs/C39 Verbod voertuigen met toegelaten massa groter dan 3500 kg in te halen.png'
WHERE sign_code = 'C39';

-- C43: was "C43 - Verbod...is aangeduid 30 km.png" → canonical "C43 Verbod...dan 30 km.png"
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 30 km.png',
    image_path = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 30 km.png'
WHERE sign_code = 'C43';

-- C43_30: same old format
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 30 km.png',
    image_path = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 30 km.png'
WHERE sign_code = 'C43_30';

-- C43_50: old format → canonical 50 km
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 50 km.png',
    image_path = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 50 km.png'
WHERE sign_code = 'C43_50';

-- C43_70: old format → canonical 70 km
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 70 km.png',
    image_path = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 70 km.png'
WHERE sign_code = 'C43_70';

-- C47: name_nl used dash ("Tolpost - ...") → canonical uses period ("Tolpost. ...")
UPDATE traffic_signs
SET name_nl = 'Tolpost. Verbod voorbij te rijden zonder te stoppen'
WHERE sign_code = 'C47';

-- ─── road_signs ───────────────────────────────────────────────────────────────

-- C22: was "C22a...autobussen.png" → canonical "C22 Verboden...autocars.png"
UPDATE road_signs
SET image_path = 'images/signs/prohibition_signs/C22 Verboden toegang voor bestuurders van autocars.png'
WHERE sign_code = 'C22';

-- C39: was double-space missing "groter dan"
UPDATE road_signs
SET image_path = 'images/signs/prohibition_signs/C39 Verbod voertuigen met toegelaten massa groter dan 3500 kg in te halen.png'
WHERE sign_code = 'C39';

-- C43: old format → canonical 30 km
UPDATE road_signs
SET image_path = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 30 km.png'
WHERE sign_code IN ('C43', 'C43_30');

-- C43_50: old format → canonical 50 km
UPDATE road_signs
SET image_path = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 50 km.png'
WHERE sign_code = 'C43_50';

-- C43_70: old format → canonical 70 km
UPDATE road_signs
SET image_path = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 70 km.png'
WHERE sign_code = 'C43_70';

-- C43_90: non-canonical (no 90 km file exists on disk), was in wrong verbodsborden folder
-- Map to the highest available canonical speed (70 km)
UPDATE road_signs
SET image_path = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 70 km.png'
WHERE sign_code = 'C43_90';
