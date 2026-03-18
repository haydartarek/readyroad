-- V127: Fix F-series (Aanwijzing) — official names, image paths, translation errors
-- Based on: "Overzicht alle officiële Belgische verkeersborden" (PDF reference)
-- Issues fixed:
--   1. image_url using wrong subfolder 'direction_signs' → 'information_signs'
--   2. Typo fixes in image filenames inserted by V103 (ddirection, AAutosnelweg, spaces)
--   3. F47 wrong EN/FR/AR translations
--   4. F117/F118 official Dutch names missing "een"
--   5. F45b correct official Dutch name from PDF

-- ─────────────────────────────────────────────────────────────────────────────
-- 1. Fix image_url: direction_signs → information_signs (not covered by V118)
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F5 Autosnelweg.png'
  WHERE sign_code = 'F5';

UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F7 Einde autosnelweg.png'
  WHERE sign_code = 'F7';

UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F8 Tunnel.png'
  WHERE sign_code = 'F8';

UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F9 Autoweg.png'
  WHERE sign_code = 'F9';

UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F11 Einde van de autoweg.png'
  WHERE sign_code = 'F11';

UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F13 Rijstrook keuze.png'
  WHERE sign_code = 'F13';

UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F14 Opstelvak voor fietsers en bromfietsen.png'
  WHERE sign_code = 'F14';

UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F19 Eenrichtingsverkeer.png'
  WHERE sign_code = 'F19';

UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F21 Rechts of links voorbijrijden toegelaten.png'
  WHERE sign_code = 'F21';

UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F31 Wegwijzer autostrade.png'
  WHERE sign_code = 'F31';

UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F43 Gemeentegrens.png'
  WHERE sign_code = 'F43';

UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F45 Doodlopende weg.png'
  WHERE sign_code = 'F45';

UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F47 Einde van de werken.png'
  WHERE sign_code = 'F47';

UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F49 Oversteekplaats voor voetgangers.png'
  WHERE sign_code = 'F49';

UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F50 Oversteekplaats voor fietsers en bromfietsers.png'
  WHERE sign_code = 'F50';

UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F87 Verhoogde inrichting (vluchtheuvel).png'
  WHERE sign_code = 'F87';

UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F103 Begin van een voetgangerszone.png'
  WHERE sign_code = 'F103';

UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F105 Einde van een voetgangerszone.png'
  WHERE sign_code = 'F105';

UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F111 Fietsstraat.png'
  WHERE sign_code = 'F111';

UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F113 Einde fietsstraat.png'
  WHERE sign_code = 'F113';

-- ─────────────────────────────────────────────────────────────────────────────
-- 2. Fix official Dutch names (from Royal Decree PDF reference)
-- ─────────────────────────────────────────────────────────────────────────────

-- F45b: official name is "Doodlopende weg, uitgezonderd voetgangers en fietsers"
UPDATE traffic_signs
SET name_nl = 'Doodlopende weg, uitgezonderd voetgangers en fietsers',
    name_fr = 'Voie sans issue, excepté piétons et cyclistes',
    name_en = 'Dead end road, except pedestrians and cyclists',
    name_ar = 'طريق مسدود، باستثناء المشاة وراكبي الدراجات',
    image_url = 'images/signs/information_signs/F45b Doodlopende weg, uitgezonderd voetgangers en fietsers.png'
WHERE sign_code = 'F45b';

-- F47: fix wrong English/French/Arabic translations (V103 had Dutch placeholders)
UPDATE traffic_signs
SET name_nl  = 'Einde van de werken',
    name_en  = 'End of road works',
    name_fr  = 'Fin des travaux',
    name_ar  = 'نهاية أشغال الطريق',
    description_nl = 'Einde van de werken.',
    description_en = 'End of road works.',
    description_fr = 'Fin des travaux.',
    description_ar = 'نهاية أشغال الطريق.'
WHERE sign_code = 'F47';

-- F117 / F117-v1: official name includes "een" — "Begin van een lage emissiezone"
UPDATE traffic_signs
SET name_nl = 'Begin van een lage emissiezone',
    name_en = 'Start of a low emission zone',
    name_fr = 'Début d''une zone de basses émissions',
    name_ar = 'بداية منطقة انبعاثات منخفضة',
    description_nl = 'Begin van een lage emissiezone.',
    description_en = 'Start of a low emission zone.',
    description_fr = 'Début d''une zone de basses émissions.',
    description_ar = 'بداية منطقة انبعاثات منخفضة.'
WHERE sign_code IN ('F117', 'F117-v1');

-- F118 / F118-v1: official name includes "een" — "Einde van een lage emissiezone"
UPDATE traffic_signs
SET name_nl = 'Einde van een lage emissiezone',
    name_en = 'End of a low emission zone',
    name_fr = 'Fin d''une zone de basses émissions',
    name_ar = 'نهاية منطقة انبعاثات منخفضة',
    description_nl = 'Einde van een lage emissiezone.',
    description_en = 'End of a low emission zone.',
    description_fr = 'Fin d''une zone de basses émissions.',
    description_ar = 'نهاية منطقة انبعاثات منخفضة.'
WHERE sign_code IN ('F118', 'F118-v1');

-- F45L: official name "Doodlopende weg, linkse doorgang"
UPDATE traffic_signs
SET name_nl = 'Doodlopende weg, linkse doorgang',
    name_en = 'Dead end road, left passage',
    name_fr = 'Voie sans issue, passage à gauche',
    name_ar = 'طريق مسدود، ممر على اليسار'
WHERE sign_code = 'F45L';

-- F45R: official name "Doodlopende weg, rechtse doorgang"
UPDATE traffic_signs
SET name_nl = 'Doodlopende weg, rechtse doorgang',
    name_en = 'Dead end road, right passage',
    name_fr = 'Voie sans issue, passage à droite',
    name_ar = 'طريق مسدود، ممر على اليمين'
WHERE sign_code = 'F45R';

-- F50bis: official name "Opgepast als je van richting veranderd, voetgangers/fietsers"
UPDATE traffic_signs
SET name_nl = 'Opgepast als je van richting veranderd',
    name_en = 'Caution when changing direction',
    name_fr = 'Attention lors d''un changement de direction',
    name_ar = 'تنبيه عند تغيير الاتجاه'
WHERE sign_code = 'F50bis';

-- F99a: official full name from PDF
UPDATE traffic_signs
SET name_nl = 'Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en speed pedelecs',
    name_en = 'Reserved for pedestrians, cyclists, horse riders and speed pedelecs',
    name_fr = 'Réservé à la circulation des piétons, cyclistes, cavaliers et speed pedelecs',
    name_ar = 'مخصص لحركة المشاة وراكبي الدراجات والفرسان ودراجات السرعة'
WHERE sign_code = 'F99a';

-- F99b: official full name from PDF
UPDATE traffic_signs
SET name_nl = 'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
    name_en = 'Part of the public road reserved for cyclists and pedestrians',
    name_fr = 'Partie de la voie publique réservée aux cyclistes et piétons',
    name_ar = 'جزء من الطريق العام مخصص للدراجات والمشاة'
WHERE sign_code = 'F99b';

-- F99c: official full name from PDF
UPDATE traffic_signs
SET name_nl = 'Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en speed pedelecs',
    name_en = 'Reserved for agricultural vehicles, pedestrians, cyclists, horse riders and speed pedelecs',
    name_fr = 'Réservé aux véhicules agricoles, piétons, cyclistes, cavaliers et speed pedelecs',
    name_ar = 'مخصص للمركبات الزراعية والمشاة وراكبي الدراجات والفرسان ودراجات السرعة'
WHERE sign_code = 'F99c';

-- F101a: official full name from PDF
UPDATE traffic_signs
SET name_nl = 'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en speed pedelecs',
    name_en = 'End of reservation for pedestrians, cyclists, horse riders and speed pedelecs',
    name_fr = 'Fin de réservation pour piétons, cyclistes, cavaliers et speed pedelecs',
    name_ar = 'نهاية المنطقة المخصصة للمشاة وراكبي الدراجات والفرسان ودراجات السرعة'
WHERE sign_code = 'F101a';

-- F101b: official full name from PDF
UPDATE traffic_signs
SET name_nl = 'Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers',
    name_en = 'End of part of public road reserved for cyclists and pedestrians',
    name_fr = 'Fin de partie de voie publique réservée aux cyclistes et piétons',
    name_ar = 'نهاية الجزء المخصص للدراجات والمشاة'
WHERE sign_code = 'F101b';

-- F101c: official full name from PDF
UPDATE traffic_signs
SET name_nl = 'Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en speed pedelecs',
    name_en = 'End of reservation for agricultural vehicles, pedestrians, cyclists, horse riders and speed pedelecs',
    name_fr = 'Fin de réservation pour véhicules agricoles, piétons, cyclistes, cavaliers et speed pedelecs',
    name_ar = 'نهاية المنطقة المخصصة للمركبات الزراعية والمشاة وراكبي الدراجات والفرسان'
WHERE sign_code = 'F101c';

-- F103: official name "Begin van een voetgangerszone"
UPDATE traffic_signs
SET name_nl = 'Begin van een voetgangerszone',
    name_en = 'Start of a pedestrian zone',
    name_fr = 'Début d''une zone piétonne',
    name_ar = 'بداية منطقة المشاة'
WHERE sign_code = 'F103';

-- F105: official name "Einde van een voetgangerszone"
UPDATE traffic_signs
SET name_nl = 'Einde van een voetgangerszone',
    name_en = 'End of a pedestrian zone',
    name_fr = 'Fin d''une zone piétonne',
    name_ar = 'نهاية منطقة المشاة'
WHERE sign_code = 'F105';

-- F12a: official name "Begin van een woonerf of van een erf"
UPDATE traffic_signs
SET name_nl = 'Begin van een woonerf of van een erf',
    name_en = 'Start of a residential zone or yard',
    name_fr = 'Début d''une zone résidentielle ou d''une cour',
    name_ar = 'بداية منطقة سكنية أو فناء'
WHERE sign_code = 'F12a';

-- F12b: official name "Einde van een woonerf of van een erf"
UPDATE traffic_signs
SET name_nl = 'Einde van een woonerf of van een erf',
    name_en = 'End of a residential zone or yard',
    name_fr = 'Fin d''une zone résidentielle ou d''une cour',
    name_ar = 'نهاية منطقة سكنية أو فناء'
WHERE sign_code = 'F12b';

-- F4a: official name "Zone 30 km/u"
UPDATE traffic_signs
SET name_nl = 'Zone 30 km/u',
    name_en = 'Zone 30 km/h',
    name_fr = 'Zone 30 km/h',
    name_ar = 'منطقة 30 كم/س'
WHERE sign_code = 'F4a';

-- F4b: official name "Einde zone 30 km/u"
UPDATE traffic_signs
SET name_nl = 'Einde zone 30 km/u',
    name_en = 'End of zone 30 km/h',
    name_fr = 'Fin de zone 30 km/h',
    name_ar = 'نهاية منطقة 30 كم/س'
WHERE sign_code = 'F4b';

-- ─────────────────────────────────────────────────────────────────────────────
-- 3. road_signs table (if exists) — apply same corrections
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE road_signs SET image_path = 'images/signs/information_signs/F5 Autosnelweg.png'
  WHERE sign_code = 'F5';
UPDATE road_signs SET image_path = 'images/signs/information_signs/F7 Einde autosnelweg.png'
  WHERE sign_code = 'F7';
UPDATE road_signs SET image_path = 'images/signs/information_signs/F8 Tunnel.png'
  WHERE sign_code = 'F8';
UPDATE road_signs SET image_path = 'images/signs/information_signs/F9 Autoweg.png'
  WHERE sign_code = 'F9';
UPDATE road_signs SET image_path = 'images/signs/information_signs/F11 Einde van de autoweg.png'
  WHERE sign_code = 'F11';
UPDATE road_signs SET image_path = 'images/signs/information_signs/F13 Rijstrook keuze.png'
  WHERE sign_code = 'F13';
UPDATE road_signs SET image_path = 'images/signs/information_signs/F14 Opstelvak voor fietsers en bromfietsen.png'
  WHERE sign_code = 'F14';
UPDATE road_signs SET image_path = 'images/signs/information_signs/F19 Eenrichtingsverkeer.png'
  WHERE sign_code = 'F19';
UPDATE road_signs SET image_path = 'images/signs/information_signs/F21 Rechts of links voorbijrijden toegelaten.png'
  WHERE sign_code = 'F21';
UPDATE road_signs SET image_path = 'images/signs/information_signs/F31 Wegwijzer autostrade.png'
  WHERE sign_code = 'F31';
UPDATE road_signs SET image_path = 'images/signs/information_signs/F43 Gemeentegrens.png'
  WHERE sign_code = 'F43';
UPDATE road_signs SET image_path = 'images/signs/information_signs/F45 Doodlopende weg.png'
  WHERE sign_code = 'F45';
UPDATE road_signs SET image_path = 'images/signs/information_signs/F47 Einde van de werken.png'
  WHERE sign_code = 'F47';
UPDATE road_signs SET image_path = 'images/signs/information_signs/F49 Oversteekplaats voor voetgangers.png'
  WHERE sign_code = 'F49';
UPDATE road_signs SET image_path = 'images/signs/information_signs/F50 Oversteekplaats voor fietsers en bromfietsers.png'
  WHERE sign_code = 'F50';
UPDATE road_signs SET image_path = 'images/signs/information_signs/F87 Verhoogde inrichting (vluchtheuvel).png'
  WHERE sign_code = 'F87';
UPDATE road_signs SET image_path = 'images/signs/information_signs/F103 Begin van een voetgangerszone.png'
  WHERE sign_code = 'F103';
UPDATE road_signs SET image_path = 'images/signs/information_signs/F105 Einde van een voetgangerszone.png'
  WHERE sign_code = 'F105';
UPDATE road_signs SET image_path = 'images/signs/information_signs/F111 Fietsstraat.png'
  WHERE sign_code = 'F111';
UPDATE road_signs SET image_path = 'images/signs/information_signs/F113 Einde fietsstraat.png'
  WHERE sign_code = 'F113';
UPDATE road_signs SET image_path = 'images/signs/information_signs/F45b Doodlopende weg, uitgezonderd voetgangers en fietsers.png'
  WHERE sign_code = 'F45b';
