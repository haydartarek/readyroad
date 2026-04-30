-- ============================================================================
-- V123: Fix B-series sign names and image paths to match official Belgian PDF
-- ============================================================================
-- Source: "Overzicht alle officiële Belgische verkeersborden" (official PDF)
-- All name_nl values corrected to official traffic sign names per Belgian law
-- (KB 1 december 1975 – Serie B, Voorrangsborden / Priority signs)
-- ============================================================================

-- ---- B1: Voorrang verlenen ----
UPDATE traffic_signs
SET name_nl    = 'Voorrang verlenen',
    name_en    = 'Give way',
    name_fr    = 'Cédez le passage',
    name_ar    = 'إعطاء الأولوية',
    image_url  = 'images/signs/priority_signs/B1 Voorrang verlenen.png',
    updated_at = NOW()
WHERE sign_code = 'B1';

-- ---- B5: Stoppen en voorrang verlenen ----
UPDATE traffic_signs
SET name_nl    = 'Stoppen en voorrang verlenen',
    name_en    = 'Stop and give way',
    name_fr    = 'Stop et cédez le passage',
    name_ar    = 'توقف وأعطِ الأولوية',
    image_url  = 'images/signs/priority_signs/B5 Stop en voorrang verlenen.png',
    updated_at = NOW()
WHERE sign_code = 'B5';

-- ---- B9: Voorrangsweg ----
UPDATE traffic_signs
SET name_nl    = 'Voorrangsweg',
    name_en    = 'Priority road',
    name_fr    = 'Route prioritaire',
    name_ar    = 'طريق الأولوية',
    image_url  = 'images/signs/priority_signs/B9 Voorrangsweg.png',
    updated_at = NOW()
WHERE sign_code = 'B9';

-- ---- B11: Einde voorrangsweg ----
UPDATE traffic_signs
SET name_nl    = 'Einde voorrangsweg',
    name_en    = 'End of priority road',
    name_fr    = 'Fin de route prioritaire',
    name_ar    = 'انتهاء طريق الأولوية',
    image_url  = 'images/signs/priority_signs/B11 Einde van voorrangsweg.png',
    updated_at = NOW()
WHERE sign_code = 'B11';

-- ---- B15a: Voorrang op de kruisende zijwegen (alle) ----
UPDATE traffic_signs
SET name_nl    = 'Voorrang op de kruisende zijwegen',
    name_en    = 'Priority over intersecting side roads',
    name_fr    = 'Priorité sur les routes latérales de croisement',
    name_ar    = 'أولوية على الطرق الجانبية المتقاطعة',
    image_url  = 'images/signs/priority_signs/B15a Voorrang op het eerstvolgende kruispunt - variant schuine zijweg links.png',
    updated_at = NOW()
WHERE sign_code = 'B15a';

-- ---- B15b: Voorrang op de kruisende zijwegen (variant 1) – fix wrong image prefix ----
UPDATE traffic_signs
SET name_nl    = 'Voorrang op de kruisende zijwegen',
    name_en    = 'Priority over intersecting side roads',
    name_fr    = 'Priorité sur les routes latérales de croisement',
    name_ar    = 'أولوية على الطرق الجانبية المتقاطعة',
    image_url  = 'images/signs/priority_signs/B15b Voorrang op het eerstvolgende kruispunt - variant schuine zijweg rechts.png',
    updated_at = NOW()
WHERE sign_code = 'B15b';

-- ---- B15c: Voorrang op kruisende zijwegen (variant 2) – fix wrong image prefix ----
UPDATE traffic_signs
SET name_nl    = 'Voorrang op kruisende zijwegen',
    name_en    = 'Priority over crossing side roads',
    name_fr    = 'Priorité sur les routes latérales',
    name_ar    = 'الأولوية على الطرق الجانبية المتقاطعة',
    image_url  = 'images/signs/priority_signs/B15c Voorrang op het eerstvolgende kruispunt.png',
    updated_at = NOW()
WHERE sign_code = 'B15c';

-- ---- B15b: Voorrang op kruisende zijweg (links van boven) ----
UPDATE traffic_signs
SET name_nl    = 'Voorrang op kruisende zijweg',
    name_en    = 'Priority over intersecting side road',
    name_fr    = 'Priorité sur la route latérale de croisement',
    name_ar    = 'الأولوية على الطريق الجانبي الأيسر (أعلى)',
    image_url  = 'images/signs/priority_signs/B15d Voorrang op het eerstvolgende kruispunt - variant zijweg links.png',
    updated_at = NOW()
WHERE sign_code = 'B15b';

-- ---- B15c: Voorrang op kruisende zijweg (links van onder) ----
UPDATE traffic_signs
SET name_nl    = 'Voorrang op kruisende zijweg',
    name_en    = 'Priority over intersecting side road',
    name_fr    = 'Priorité sur la route latérale de croisement',
    name_ar    = 'الأولوية على الطريق الجانبي الأيسر (أسفل)',
    image_url  = 'images/signs/priority_signs/B15e Voorrang op het eerstvolgende kruispunt - variant T-kruispunt.png',
    updated_at = NOW()
WHERE sign_code = 'B15c';

-- ---- B15d: Voorrang op kruisende zijweg (rechts van boven) ----
UPDATE traffic_signs
SET name_nl    = 'Voorrang op kruisende zijweg',
    name_en    = 'Priority over intersecting side road',
    name_fr    = 'Priorité sur la route latérale de croisement',
    name_ar    = 'الأولوية على الطريق الجانبي الأيمن (أعلى)',
    image_url  = 'images/signs/priority_signs/B15f Voorrang op het eerstvolgende kruispunt - variant Y-kruispunt.png',
    updated_at = NOW()
WHERE sign_code = 'B15d';

-- ---- B15e: Voorrang op kruisende zijweg (rechts van onder) ----
UPDATE traffic_signs
SET name_nl    = 'Voorrang op kruisende zijweg',
    name_en    = 'Priority over crossing side road',
    name_fr    = 'Priorité sur la route latérale de croisement',
    name_ar    = 'الأولوية على الطريق الجانبي الأيمن (أسفل)',
    image_url  = 'images/signs/priority_signs/B15g Voorrang op kruisende zijweg.png',
    updated_at = NOW()
WHERE sign_code = 'B15e';

-- ---- B15f: Voorrang op kruisende zijweg (links en rechts van boven) ----
UPDATE traffic_signs
SET name_nl    = 'Voorrang op kruisende zijweg',
    name_en    = 'Priority over intersecting side road',
    name_fr    = 'Priorité sur la route latérale de croisement',
    name_ar    = 'الأولوية على الطريق الجانبي (يمين ويسار أعلى)',
    image_url  = 'images/signs/priority_signs/B21 Smalle doorgang. Voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen.png',
    updated_at = NOW()
WHERE sign_code = 'B15f';

-- ---- B15g: Voorrang op kruisende zijweg (links en rechts van onder) ----
UPDATE traffic_signs
SET name_nl    = 'Voorrang op kruisende zijweg',
    name_en    = 'Priority over intersecting side road',
    name_fr    = 'Priorité sur la route latérale de croisement',
    name_ar    = 'الأولوية على الطريق الجانبي (يمين ويسار أسفل)',
    image_url  = 'images/signs/priority_signs/B15g Voorrang op kruisende zijweg.png',
    updated_at = NOW()
WHERE sign_code = 'B15g';

-- ---- B17: Kruispunt waar de voorrang van rechts geldt ----
UPDATE traffic_signs
SET name_nl    = 'Kruispunt waar de voorrang van rechts geldt',
    name_en    = 'Crossroads where priority from the right applies',
    name_fr    = 'Carrefour où la priorité de droite s''applique',
    name_ar    = 'تقاطع تُطبَّق فيه قاعدة الأولوية من اليمين',
    image_url  = 'images/signs/priority_signs/B17 Kruispunt waar de voorrang van rechts geldt.png',
    updated_at = NOW()
WHERE sign_code = 'B17';

-- ---- B19: Smalle doorgang – voorrang verlenen aan tegenliggers ----
UPDATE traffic_signs
SET name_nl    = 'Smalle doorgang: voorrang verlenen aan de bestuurders die uit de tegenovergestelde richting komen',
    name_en    = 'Narrow passage: give way to drivers coming from the opposite direction',
    name_fr    = 'Passage étroit: céder le passage aux conducteurs venant en sens inverse',
    name_ar    = 'ممر ضيق: إعطاء الأولوية للسائقين القادمين من الاتجاه المعاكس',
    image_url  = 'images/signs/priority_signs/B19 Smalle doorgang. Voorrang verlenen aan de bestuurders die uit de tegenovergestelde richting komen.png',
    updated_at = NOW()
WHERE sign_code = 'B19';

-- ---- B21: Smalle doorgang – voorrang ten opzichte van tegenliggers ----
UPDATE traffic_signs
SET name_nl    = 'Smalle doorgang: voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen',
    name_en    = 'Narrow passage: priority over drivers coming from the opposite direction',
    name_fr    = 'Passage étroit: priorité par rapport aux conducteurs venant en sens inverse',
    name_ar    = 'ممر ضيق: الأولوية على السائقين القادمين من الاتجاه المعاكس',
    image_url  = 'images/signs/priority_signs/B21 Voorrang op tegenliggers.png',
    updated_at = NOW()
WHERE sign_code = 'B21';

-- ---- B22: Fietsers en speed pedelecs mogen rechtsaf slaan bij rood ----
UPDATE traffic_signs
SET name_nl    = 'Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
    name_en    = 'Cyclists and speed pedelecs may turn right and pass traffic lights',
    name_fr    = 'Cyclistes et speed pedelecs peuvent tourner à droite et passer les feux',
    name_ar    = 'يجوز لراكبي الدراجات وسبيد بيدليك الانعطاف يميناً والمضي أمام إشارات المرور',
    image_url  = 'images/signs/priority_signs/B22 Fietsers en speed pedelecs mogen rechts afslaan en de verkeerslichten voorbijrijden.png',
    updated_at = NOW()
WHERE sign_code = 'B22';

-- ---- B23: Fietsers en speed pedelecs mogen rechtdoor rijden bij rood ----
UPDATE traffic_signs
SET name_nl    = 'Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
    name_en    = 'Cyclists and speed pedelecs may go straight and pass traffic lights',
    name_fr    = 'Les cyclistes et speed pedelecs peuvent aller tout droit et passer les feux',
    name_ar    = 'يجوز لراكبي الدراجات وسبيد بيدليك السير مباشرةً والمضي أمام إشارات المرور',
    image_url  = 'images/signs/priority_signs/B23 Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden.png',
    updated_at = NOW()
WHERE sign_code = 'B23';

-- ============================================================================
-- Also update sign.json image_path entries that used the wrong 'assets/' prefix
-- This fixes B15b and B15c which referenced assets/signs/voorrangsborden/
-- (the DB image_url is already corrected above via UPDATE statements)
-- ============================================================================

