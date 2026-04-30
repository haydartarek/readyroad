-- V149: Enforce exact canonical prohibition_signs filenames across DB
-- Canonical source of truth: readyroad_front_end/web_app/public/images/signs/prohibition_signs/
-- Rules: Never rename image files. Fix all DB references to match exact disk filenames.

-- ─── traffic_signs ────────────────────────────────────────────────────────────

-- C22: align with the curated coach-prohibition sign
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C22 Verboden toegang voor bestuurders van autocars.png',
    image_path = 'images/signs/prohibition_signs/C22 Verboden toegang voor bestuurders van autocars.png'
WHERE sign_code = 'C22';

-- C39: was double-space, missing "groter dan" → canonical "groter dan 3500 kg"
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C39 Verbod voertuigen met toegelaten massa groter dan 3500 kg in te halen.png',
    image_path = 'images/signs/prohibition_signs/C39 Verbod voertuigen met toegelaten massa groter dan 3500 kg in te halen.png'
WHERE sign_code = 'C39';

-- C43: align with the curated 50 km/h canonical sign
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 50 km.png',
    image_path = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 50 km.png'
WHERE sign_code = 'C43';

-- C47: name_nl used dash ("Tolpost - ...") → canonical uses period ("Tolpost. ...")
UPDATE traffic_signs
SET name_nl = 'Tolpost. Verbod voorbij te rijden zonder te stoppen'
WHERE sign_code = 'C47';

-- ─── road_signs ───────────────────────────────────────────────────────────────

-- C22: align with the curated coach-prohibition sign
UPDATE road_signs
SET image_path = 'images/signs/prohibition_signs/C22 Verboden toegang voor bestuurders van autocars.png'
WHERE sign_code = 'C22';

-- C39: was double-space missing "groter dan"
UPDATE road_signs
SET image_path = 'images/signs/prohibition_signs/C39 Verbod voertuigen met toegelaten massa groter dan 3500 kg in te halen.png'
WHERE sign_code = 'C39';

-- C43: align with the curated 50 km/h canonical sign
UPDATE road_signs
SET image_path = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 50 km.png'
WHERE sign_code = 'C43';
