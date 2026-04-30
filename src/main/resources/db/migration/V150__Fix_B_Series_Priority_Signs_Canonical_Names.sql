-- V150: Fix B-series priority_signs image paths to match exact disk filenames
-- Canonical source of truth: readyroad_front_end/web_app/public/images/signs/priority_signs/
-- Previous V147 migration used incorrect names; this corrects them.
-- Rules: never rename files on disk; only update DB references to match disk.

-- ─── traffic_signs (image_url + image_path) ──────────────────────────────────

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B9 Voorrangsweg.png',
    image_path = 'images/signs/priority_signs/B9 Voorrangsweg.png'
WHERE sign_code = 'B9';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B11 Einde van voorrangsweg.png',
    image_path = 'images/signs/priority_signs/B11 Einde van voorrangsweg.png'
WHERE sign_code = 'B11';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15c Voorrang op het eerstvolgende kruispunt.png',
    image_path = 'images/signs/priority_signs/B15c Voorrang op het eerstvolgende kruispunt.png'
WHERE sign_code = 'B15c';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15d Voorrang op het eerstvolgende kruispunt - variant zijweg links.png',
    image_path = 'images/signs/priority_signs/B15d Voorrang op het eerstvolgende kruispunt - variant zijweg links.png'
WHERE sign_code = 'B15d';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15e Voorrang op het eerstvolgende kruispunt - variant T-kruispunt.png',
    image_path = 'images/signs/priority_signs/B15e Voorrang op het eerstvolgende kruispunt - variant T-kruispunt.png'
WHERE sign_code = 'B15e';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15f Voorrang op het eerstvolgende kruispunt - variant Y-kruispunt.png',
    image_path = 'images/signs/priority_signs/B15f Voorrang op het eerstvolgende kruispunt - variant Y-kruispunt.png'
WHERE sign_code = 'B15f';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15g Voorrang op kruisende zijweg.png',
    image_path = 'images/signs/priority_signs/B15g Voorrang op kruisende zijweg.png'
WHERE sign_code = 'B15g';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B21 Smalle doorgang. Voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen.png',
    image_path = 'images/signs/priority_signs/B21 Smalle doorgang. Voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen.png'
WHERE sign_code = 'B21';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15g Voorrang op kruisende zijweg.png',
    image_path = 'images/signs/priority_signs/B15g Voorrang op kruisende zijweg.png'
WHERE sign_code = 'B15g';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B17 Kruispunt waar de voorrang van rechts geldt.png',
    image_path = 'images/signs/priority_signs/B17 Kruispunt waar de voorrang van rechts geldt.png'
WHERE sign_code = 'B17';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B19 Smalle doorgang. Voorrang verlenen aan de bestuurders die uit de tegenovergestelde richting komen.png',
    image_path = 'images/signs/priority_signs/B19 Smalle doorgang. Voorrang verlenen aan de bestuurders die uit de tegenovergestelde richting komen.png'
WHERE sign_code = 'B19';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B21 Voorrang op tegenliggers.png',
    image_path = 'images/signs/priority_signs/B21 Voorrang op tegenliggers.png'
WHERE sign_code = 'B21';

UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B22 Fietsers en speed pedelecs mogen rechts afslaan en de verkeerslichten voorbijrijden.png',
    image_path = 'images/signs/priority_signs/B22 Fietsers en speed pedelecs mogen rechts afslaan en de verkeerslichten voorbijrijden.png'
WHERE sign_code = 'B22';

-- ─── road_signs (image_path only) ────────────────────────────────────────────

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B9 Voorrangsweg.png'
WHERE sign_code = 'B9';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B11 Einde van voorrangsweg.png'
WHERE sign_code = 'B11';

-- B15a and B15c use their current authoritative files
UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15c Voorrang op het eerstvolgende kruispunt.png'
WHERE sign_code = 'B15c';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15a Voorrang op het eerstvolgende kruispunt - variant schuine zijweg links.png'
WHERE sign_code = 'B15a';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15d Voorrang op het eerstvolgende kruispunt - variant zijweg links.png'
WHERE sign_code = 'B15b';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15e Voorrang op het eerstvolgende kruispunt - variant T-kruispunt.png'
WHERE sign_code = 'B15c';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15f Voorrang op het eerstvolgende kruispunt - variant Y-kruispunt.png'
WHERE sign_code = 'B15d';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15g Voorrang op kruisende zijweg.png'
WHERE sign_code = 'B15e';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B21 Smalle doorgang. Voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen.png'
WHERE sign_code = 'B15f';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15g Voorrang op kruisende zijweg.png'
WHERE sign_code = 'B15g';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B17 Kruispunt waar de voorrang van rechts geldt.png'
WHERE sign_code = 'B17';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B19 Smalle doorgang. Voorrang verlenen aan de bestuurders die uit de tegenovergestelde richting komen.png'
WHERE sign_code = 'B19';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B21 Voorrang op tegenliggers.png'
WHERE sign_code = 'B21';

UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B22 Fietsers en speed pedelecs mogen rechts afslaan en de verkeerslichten voorbijrijden.png'
WHERE sign_code = 'B22';

