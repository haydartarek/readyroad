-- V147: Fix B-series priority_signs canonical image filenames
-- All image paths corrected to exactly match files in public/images/signs/priority_signs/
-- No image files renamed; only DB records updated.

-- ─── traffic_signs ────────────────────────────────────────────────────────────

-- B9: was "B9 Voorrangsweg.png" → canonical file is "B9 Voorrangsweg.png"
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B9 Voorrangsweg.png',
    image_path = 'images/signs/priority_signs/B9 Voorrangsweg.png'
WHERE sign_code = 'B9';

-- B11: was "B11 Einde van voorrangsweg.png" → canonical "B11 Einde van voorrangsweg.png"
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B11 Einde van voorrangsweg.png',
    image_path = 'images/signs/priority_signs/B11 Einde van voorrangsweg.png'
WHERE sign_code = 'B11';

-- B15c: keep the authoritative right-narrowing variant filename
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15c Voorrang op het eerstvolgende kruispunt.png',
    image_path = 'images/signs/priority_signs/B15c Voorrang op het eerstvolgende kruispunt.png'
WHERE sign_code = 'B15c';

-- B15d: was "B15d Voorrang op het eerstvolgende kruispunt - variant zijweg links.png" → canonical "B15d Voorrang op het eerstvolgende kruispunt - variant zijweg links.png"
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15d Voorrang op het eerstvolgende kruispunt - variant zijweg links.png',
    image_path = 'images/signs/priority_signs/B15d Voorrang op het eerstvolgende kruispunt - variant zijweg links.png'
WHERE sign_code = 'B15d';

-- B15e: was "B15e Voorrang op het eerstvolgende kruispunt - variant T-kruispunt.png" → canonical "B15e Voorrang op het eerstvolgende kruispunt - variant T-kruispunt.png"
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15e Voorrang op het eerstvolgende kruispunt - variant T-kruispunt.png',
    image_path = 'images/signs/priority_signs/B15e Voorrang op het eerstvolgende kruispunt - variant T-kruispunt.png'
WHERE sign_code = 'B15e';

-- B15f: was "B15f Voorrang op het eerstvolgende kruispunt - variant Y-kruispunt.png" → canonical "B15f Voorrang op het eerstvolgende kruispunt - variant Y-kruispunt.png"
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15f Voorrang op het eerstvolgende kruispunt - variant Y-kruispunt.png',
    image_path = 'images/signs/priority_signs/B15f Voorrang op het eerstvolgende kruispunt - variant Y-kruispunt.png'
WHERE sign_code = 'B15f';

-- B15g: was "B15g Voorrang op kruisende zijweg.png" → canonical "B15g Voorrang op kruisende zijweg.png"
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15g Voorrang op kruisende zijweg.png',
    image_path = 'images/signs/priority_signs/B15g Voorrang op kruisende zijweg.png'
WHERE sign_code = 'B15g';

-- B21: was "B21 Smalle doorgang. Voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen.png" → canonical "B21 Smalle doorgang. Voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen.png"
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B21 Smalle doorgang. Voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen.png',
    image_path = 'images/signs/priority_signs/B21 Smalle doorgang. Voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen.png'
WHERE sign_code = 'B21';

-- B15g: was "B15g Voorrang op kruisende zijweg.png" → canonical "B15g Voorrang op kruisende zijweg.png"
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15g Voorrang op kruisende zijweg.png',
    image_path = 'images/signs/priority_signs/B15g Voorrang op kruisende zijweg.png'
WHERE sign_code = 'B15g';

-- B17: was "B17 Kruispunt waar de voorrang van rechts geldt.png" → canonical "B17 Kruispunt waar de voorrang van rechts geldt.png"
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B17 Kruispunt waar de voorrang van rechts geldt.png',
    image_path = 'images/signs/priority_signs/B17 Kruispunt waar de voorrang van rechts geldt.png'
WHERE sign_code = 'B17';

-- B19: remove colon from name_nl; fix image from wrong filename to canonical
UPDATE traffic_signs
SET name_nl    = 'Smalle doorgang voorrang verlenen aan de bestuurders die uit de tegenovergestelde richting komen',
    image_url  = 'images/signs/priority_signs/B19 Smalle doorgang. Voorrang verlenen aan de bestuurders die uit de tegenovergestelde richting komen.png',
    image_path = 'images/signs/priority_signs/B19 Smalle doorgang. Voorrang verlenen aan de bestuurders die uit de tegenovergestelde richting komen.png'
WHERE sign_code = 'B19';

-- B21: remove colon from name_nl; fix image from wrong filename to canonical
UPDATE traffic_signs
SET name_nl    = 'Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen',
    image_url  = 'images/signs/priority_signs/B21 Voorrang op tegenliggers.png',
    image_path = 'images/signs/priority_signs/B21 Voorrang op tegenliggers.png'
WHERE sign_code = 'B21';

-- B22: was "B22 Fietsers en speed pedelecs mogen rechts afslaan en de verkeerslichten voorbijrijden.png" → canonical long name
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B22 Fietsers en speed pedelecs mogen rechts afslaan en de verkeerslichten voorbijrijden.png',
    image_path = 'images/signs/priority_signs/B22 Fietsers en speed pedelecs mogen rechts afslaan en de verkeerslichten voorbijrijden.png'
WHERE sign_code = 'B22';

-- ─── road_signs ───────────────────────────────────────────────────────────────

-- B9
UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B9 Voorrangsweg.png'
WHERE sign_code = 'B9';

-- B11
UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B11 Einde van voorrangsweg.png'
WHERE sign_code = 'B11';

-- B15a: keep the authoritative left-diagonal variant filename
UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15a Voorrang op het eerstvolgende kruispunt - variant schuine zijweg links.png'
WHERE sign_code = 'B15a';

-- B15b: wrong folder (voorrangsborden) → correct folder (priority_signs), image filename is correct
UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15b Voorrang op het eerstvolgende kruispunt - variant schuine zijweg rechts.png'
WHERE sign_code = 'B15b';

-- B15c: keep the authoritative right-narrowing variant filename
UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15c Voorrang op het eerstvolgende kruispunt.png'
WHERE sign_code = 'B15c';

-- B15b
UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15d Voorrang op het eerstvolgende kruispunt - variant zijweg links.png'
WHERE sign_code = 'B15b';

-- B15c
UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15e Voorrang op het eerstvolgende kruispunt - variant T-kruispunt.png'
WHERE sign_code = 'B15c';

-- B15d
UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15f Voorrang op het eerstvolgende kruispunt - variant Y-kruispunt.png'
WHERE sign_code = 'B15d';

-- B15e
UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15g Voorrang op kruisende zijweg.png'
WHERE sign_code = 'B15e';

-- B15f
UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B21 Smalle doorgang. Voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen.png'
WHERE sign_code = 'B15f';

-- B15g
UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B15g Voorrang op kruisende zijweg.png'
WHERE sign_code = 'B15g';

-- B17
UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B17 Kruispunt waar de voorrang van rechts geldt.png'
WHERE sign_code = 'B17';

-- B19
UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B19 Smalle doorgang. Voorrang verlenen aan de bestuurders die uit de tegenovergestelde richting komen.png'
WHERE sign_code = 'B19';

-- B21
UPDATE road_signs
SET image_path = 'priority_signs/B21 Voorrang op tegenliggers.png'
WHERE sign_code = 'B21';

-- B22
UPDATE road_signs
SET image_path = 'images/signs/priority_signs/B22 Fietsers en speed pedelecs mogen rechts afslaan en de verkeerslichten voorbijrijden.png'
WHERE sign_code = 'B22';

