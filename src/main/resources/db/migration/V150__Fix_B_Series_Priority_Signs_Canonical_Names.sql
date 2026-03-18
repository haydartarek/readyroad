-- V150: Fix B-series priority_signs image paths to match exact disk filenames
-- Canonical source of truth: readyroad_front_end/web_app/public/images/signs/priority_signs/
-- Previous V147 migration used incorrect names; this corrects them.
-- Rules: never rename files on disk; only update DB references to match disk.

-- ─── traffic_signs (image_url + image_path) ──────────────────────────────────

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B9 Voorrang van rechts.png',
    image_path = 'images/signs/priority_signs/B9 Voorrang van rechts.png'
WHERE sign_code = 'B9';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B11 Voorrang op tegenliggers.png',
    image_path = 'images/signs/priority_signs/B11 Voorrang op tegenliggers.png'
WHERE sign_code = 'B11';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15A Versmalling van rechts.png',
    image_path = 'images/signs/priority_signs/B15A Versmalling van rechts.png'
WHERE sign_code = 'B15A-v2';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15B Versmalling van links.png',
    image_path = 'images/signs/priority_signs/B15B Versmalling van links.png'
WHERE sign_code = 'B15B';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15C Versmalling beide zijden.png',
    image_path = 'images/signs/priority_signs/B15C Versmalling beide zijden.png'
WHERE sign_code = 'B15C';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15D Voorrang van rechts bij versmalling.png',
    image_path = 'images/signs/priority_signs/B15D Voorrang van rechts bij versmalling.png'
WHERE sign_code = 'B15D';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15E Voorrang van links bij versmalling.png',
    image_path = 'images/signs/priority_signs/B15E Voorrang van links bij versmalling.png'
WHERE sign_code = 'B15E';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15F Voorrang voor tegenliggers.png',
    image_path = 'images/signs/priority_signs/B15F Voorrang voor tegenliggers.png'
WHERE sign_code = 'B15F';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15G Tegenliggers hebben voorrang.png',
    image_path = 'images/signs/priority_signs/B15G Tegenliggers hebben voorrang.png'
WHERE sign_code = 'B15G';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B17 Voorrangskruispunt.png',
    image_path = 'images/signs/priority_signs/B17 Voorrangskruispunt.png'
WHERE sign_code = 'B17';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B19 Verbod voor voertuigen elkaar niet te kruisen.png',
    image_path = 'images/signs/priority_signs/B19 Verbod voor voertuigen elkaar niet te kruisen.png'
WHERE sign_code = 'B19';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B21 Voorrang op tegenliggers.png',
    image_path = 'images/signs/priority_signs/B21 Voorrang op tegenliggers.png'
WHERE sign_code = 'B21';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B22 Fietsers en speedpedelecs rechtdoor bij rood.png',
    image_path = 'images/signs/priority_signs/B22 Fietsers en speedpedelecs rechtdoor bij rood.png'
WHERE sign_code = 'B22';

-- ─── road_signs (image_path only) ────────────────────────────────────────────

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B9 Voorrang van rechts.png'
WHERE sign_code = 'B9';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B11 Voorrang op tegenliggers.png'
WHERE sign_code = 'B11';

-- B15a (lowercase) and B15A-v2 both previously pointed to the missing file
UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15A Versmalling van rechts.png'
WHERE sign_code IN ('B15a', 'B15A-v2');

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15B Versmalling van links.png'
WHERE sign_code = 'B15b';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15C Versmalling beide zijden.png'
WHERE sign_code = 'B15c';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15D Voorrang van rechts bij versmalling.png'
WHERE sign_code = 'B15d';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15E Voorrang van links bij versmalling.png'
WHERE sign_code = 'B15e';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15F Voorrang voor tegenliggers.png'
WHERE sign_code = 'B15f';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15G Tegenliggers hebben voorrang.png'
WHERE sign_code = 'B15g';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B17 Voorrangskruispunt.png'
WHERE sign_code = 'B17';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B19 Verbod voor voertuigen elkaar niet te kruisen.png'
WHERE sign_code = 'B19';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B21 Voorrang op tegenliggers.png'
WHERE sign_code = 'B21';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B22 Fietsers en speedpedelecs rechtdoor bij rood.png'
WHERE sign_code = 'B22';
