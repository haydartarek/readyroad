-- V147: Fix B-series priority_signs canonical image filenames
-- All image paths corrected to exactly match files in public/images/signs/priority_signs/
-- No image files renamed; only DB records updated.

-- ─── traffic_signs ────────────────────────────────────────────────────────────

-- B9: was "B9 Voorrang van rechts.png" → canonical file is "B9 Voorrangsweg.png"
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B9 Voorrangsweg.png',
    image_path = 'images/signs/priority_signs/B9 Voorrangsweg.png'
WHERE sign_code = 'B9';

-- B11: was "B11 Voorrang op tegenliggers.png" → canonical "B11 Einde voorrangsweg.png"
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B11 Einde voorrangsweg.png',
    image_path = 'images/signs/priority_signs/B11 Einde voorrangsweg.png'
WHERE sign_code = 'B11';

-- B15A-v2: was "B15A Versmalling van rechts.png" → canonical "B15a Voorrang op de kruisende zijwegen.png"
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15a Voorrang op de kruisende zijwegen.png',
    image_path = 'images/signs/priority_signs/B15a Voorrang op de kruisende zijwegen.png'
WHERE sign_code = 'B15A-v2';

-- B15B: was "B15B Versmalling van links.png" → canonical "B15b Voorrang op kruisende zijweg.png"
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15b Voorrang op kruisende zijweg.png',
    image_path = 'images/signs/priority_signs/B15b Voorrang op kruisende zijweg.png'
WHERE sign_code = 'B15B';

-- B15C: was "B15C Versmalling beide zijden.png" → canonical "B15c Voorrang op kruisende zijweg.png"
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15c Voorrang op kruisende zijweg.png',
    image_path = 'images/signs/priority_signs/B15c Voorrang op kruisende zijweg.png'
WHERE sign_code = 'B15C';

-- B15D: was "B15D Voorrang van rechts bij versmalling.png" → canonical "B15d Voorrang op kruisende zijweg.png"
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15d Voorrang op kruisende zijweg.png',
    image_path = 'images/signs/priority_signs/B15d Voorrang op kruisende zijweg.png'
WHERE sign_code = 'B15D';

-- B15E: was "B15E Voorrang van links bij versmalling.png" → canonical "B15e Voorrang op kruisende zijweg.png"
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15e Voorrang op kruisende zijweg.png',
    image_path = 'images/signs/priority_signs/B15e Voorrang op kruisende zijweg.png'
WHERE sign_code = 'B15E';

-- B15F: was "B15F Voorrang voor tegenliggers.png" → canonical "B15f Voorrang op kruisende zijweg.png"
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15f Voorrang op kruisende zijweg.png',
    image_path = 'images/signs/priority_signs/B15f Voorrang op kruisende zijweg.png'
WHERE sign_code = 'B15F';

-- B15G: was "B15G Tegenliggers hebben voorrang.png" → canonical "B15g Voorrang op kruisende zijweg.png"
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B15g Voorrang op kruisende zijweg.png',
    image_path = 'images/signs/priority_signs/B15g Voorrang op kruisende zijweg.png'
WHERE sign_code = 'B15G';

-- B17: was "B17 Voorrangskruispunt.png" → canonical "B17 Kruispunt waar de voorrang van rechts geldt.png"
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B17 Kruispunt waar de voorrang van rechts geldt.png',
    image_path = 'images/signs/priority_signs/B17 Kruispunt waar de voorrang van rechts geldt.png'
WHERE sign_code = 'B17';

-- B19: remove colon from name_nl; fix image from wrong filename to canonical
UPDATE traffic_signs
SET name_nl    = 'Smalle doorgang voorrang verlenen aan de bestuurders die uit de tegenovergestelde richting komen',
    image_url  = 'images/signs/priority_signs/B19 Smalle doorgang voorrang verlenen aan de bestuurders die uit de tegenovergestelde richting komen.png',
    image_path = 'images/signs/priority_signs/B19 Smalle doorgang voorrang verlenen aan de bestuurders die uit de tegenovergestelde richting komen.png'
WHERE sign_code = 'B19';

-- B21: remove colon from name_nl; fix image from wrong filename to canonical
UPDATE traffic_signs
SET name_nl    = 'Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen',
    image_url  = 'images/signs/priority_signs/B21 Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen.png',
    image_path = 'images/signs/priority_signs/B21 Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen.png'
WHERE sign_code = 'B21';

-- B22: was "B22 Fietsers en speedpedelecs rechtdoor bij rood.png" → canonical long name
UPDATE traffic_signs
SET image_url  = 'images/signs/priority_signs/B22 Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden.png',
    image_path = 'images/signs/priority_signs/B22 Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden.png'
WHERE sign_code = 'B22';

-- ─── road_signs ───────────────────────────────────────────────────────────────

-- B9
UPDATE road_signs
SET image_path = 'priority_signs/B9 Voorrangsweg.png'
WHERE sign_code = 'B9';

-- B11
UPDATE road_signs
SET image_path = 'priority_signs/B11 Einde voorrangsweg.png'
WHERE sign_code = 'B11';

-- B15a: was pointing to B15A Variant schuine links.png → canonical B15a file
UPDATE road_signs
SET image_path = 'priority_signs/B15a Voorrang op de kruisende zijwegen.png'
WHERE sign_code = 'B15a';

-- B15A-v1: wrong folder (voorrangsborden) → correct folder (priority_signs), image filename is correct
UPDATE road_signs
SET image_path = 'priority_signs/B15A Variant schuine rechts.png'
WHERE sign_code = 'B15A-v1';

-- B15A-v2: wrong folder + wrong image → canonical B15a file
UPDATE road_signs
SET image_path = 'priority_signs/B15a Voorrang op de kruisende zijwegen.png'
WHERE sign_code = 'B15A-v2';

-- B15b
UPDATE road_signs
SET image_path = 'priority_signs/B15b Voorrang op kruisende zijweg.png'
WHERE sign_code = 'B15b';

-- B15c
UPDATE road_signs
SET image_path = 'priority_signs/B15c Voorrang op kruisende zijweg.png'
WHERE sign_code = 'B15c';

-- B15d
UPDATE road_signs
SET image_path = 'priority_signs/B15d Voorrang op kruisende zijweg.png'
WHERE sign_code = 'B15d';

-- B15e
UPDATE road_signs
SET image_path = 'priority_signs/B15e Voorrang op kruisende zijweg.png'
WHERE sign_code = 'B15e';

-- B15f
UPDATE road_signs
SET image_path = 'priority_signs/B15f Voorrang op kruisende zijweg.png'
WHERE sign_code = 'B15f';

-- B15g
UPDATE road_signs
SET image_path = 'priority_signs/B15g Voorrang op kruisende zijweg.png'
WHERE sign_code = 'B15g';

-- B17
UPDATE road_signs
SET image_path = 'priority_signs/B17 Kruispunt waar de voorrang van rechts geldt.png'
WHERE sign_code = 'B17';

-- B19
UPDATE road_signs
SET image_path = 'priority_signs/B19 Smalle doorgang voorrang verlenen aan de bestuurders die uit de tegenovergestelde richting komen.png'
WHERE sign_code = 'B19';

-- B21
UPDATE road_signs
SET image_path = 'priority_signs/B21 Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen.png'
WHERE sign_code = 'B21';

-- B22
UPDATE road_signs
SET image_path = 'priority_signs/B22 Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden.png'
WHERE sign_code = 'B22';
