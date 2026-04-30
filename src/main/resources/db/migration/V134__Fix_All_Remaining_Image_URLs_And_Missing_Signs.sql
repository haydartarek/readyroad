-- =============================================================================
-- V134: Fix all remaining broken image_url values (assets/traffic_signs/ → images/signs/)
-- Covers: A-series (18), C-series (26), D-series (5), F-series (3)
-- Also INSERTs missing D5 and D7
-- =============================================================================

-- =====================
-- A-series — danger_signs/
-- (V122 fixed A1a-d, A7b, A7c, A11, A17, A19, A21, A23, A25)
-- (V130 fixed A33, A17 again, inserted A9)
-- Below: the 18 remaining A-signs not covered by V122/V130/V118
-- =====================
UPDATE traffic_signs SET image_url = 'images/signs/danger_signs/A3 Gevaarlijke daling.png', updated_at = NOW() WHERE sign_code = 'A3';
UPDATE traffic_signs SET image_url = 'images/signs/danger_signs/A5 Steile helling.png', updated_at = NOW() WHERE sign_code = 'A5';
UPDATE traffic_signs SET image_url = 'images/signs/danger_signs/A7a Rijbaanversmalling.png', updated_at = NOW() WHERE sign_code = 'A7a';
UPDATE traffic_signs SET image_url = 'images/signs/danger_signs/A9 Beweegbare brug.png', updated_at = NOW() WHERE sign_code = 'A9';
UPDATE traffic_signs SET image_url = 'images/signs/danger_signs/A13 Dwarse uitholling of ezelsrug.png', updated_at = NOW() WHERE sign_code = 'A13';
UPDATE traffic_signs SET image_url = 'images/signs/danger_signs/A14 Verhoogde inrichting.png', updated_at = NOW() WHERE sign_code = 'A14';
UPDATE traffic_signs SET image_url = 'images/signs/danger_signs/A15 Slipgevaar - glad wegdek.png', updated_at = NOW() WHERE sign_code = 'A15';
UPDATE traffic_signs SET image_url = 'images/signs/danger_signs/A27 Overstekend groot wild.png', updated_at = NOW() WHERE sign_code = 'A27';
UPDATE traffic_signs SET image_url = 'images/signs/danger_signs/A29 Overstekend vee.png', updated_at = NOW() WHERE sign_code = 'A29';
UPDATE traffic_signs SET image_url = 'images/signs/danger_signs/A31 Werken.png', updated_at = NOW() WHERE sign_code = 'A31';
UPDATE traffic_signs SET image_url = 'images/signs/danger_signs/A35 Laagvliegende vliegtuigen.png', updated_at = NOW() WHERE sign_code = 'A35';
UPDATE traffic_signs SET image_url = 'images/signs/danger_signs/A37 Zijwind.png', updated_at = NOW() WHERE sign_code = 'A37';
UPDATE traffic_signs SET image_url = 'images/signs/danger_signs/A39 Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer.png', updated_at = NOW() WHERE sign_code = 'A39';
UPDATE traffic_signs SET image_url = 'images/signs/danger_signs/A41 Overweg met slagbomen.png', updated_at = NOW() WHERE sign_code = 'A41';
UPDATE traffic_signs SET image_url = 'images/signs/danger_signs/A43 Overweg zonder slagbomen.png', updated_at = NOW() WHERE sign_code = 'A43';
UPDATE traffic_signs SET image_url = 'images/signs/danger_signs/A49 Kruising van een openbare weg door een of meer in de rijbaan aangelegde sporen.png', updated_at = NOW() WHERE sign_code = 'A49';
UPDATE traffic_signs SET image_url = 'images/signs/danger_signs/A50 Opgelet file.png', updated_at = NOW() WHERE sign_code = 'A50';
UPDATE traffic_signs SET image_url = 'images/signs/danger_signs/A51 Gevaar dat niet door een speciaal symbool wordt bepaald.png', updated_at = NOW() WHERE sign_code = 'A51';

-- =====================
-- C-series — prohibition_signs/
-- (V118 fixed: C35 and C6)
-- (V124 fixed: C11 and C43)
-- Below: all remaining C-signs with old assets/ paths
-- =====================

-- C1: Verboden richting voor iedere bestuurder
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C1 Verboden richting voor iedere bestuurder.png',
    updated_at = NOW()
WHERE sign_code = 'C1';

-- C3: Verboden toegang in beide richtingen
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C3 Verboden toegang in beide richtingen voor iedere bestuurder.png',
    updated_at = NOW()
WHERE sign_code = 'C3';

-- C5: Verboden toegang motorvoertuigen — no disk image, normalise URL format
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C5 Verboden toegang voor bestuurders van motorvoertuigen met meer dan twee wielen..png',
    updated_at = NOW()
WHERE sign_code = 'C5';

-- C7: Verboden toegang motorfietsen — no disk image, normalise URL format
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C7 Verboden toegang voor bestuurders van motorfietsen.png',
    updated_at = NOW()
WHERE sign_code = 'C7';

-- C9: Verboden toegang bromfietsen
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C9 Verboden toegang voor bestuurders van bromfietsen.png',
    updated_at = NOW()
WHERE sign_code = 'C9';

-- C13: Verboden toegang gespannen
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C13 Verboden toegang voor bestuurders van gespannen..png',
    updated_at = NOW()
WHERE sign_code = 'C13';

-- C15: Verboden toegang ruiters
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C15 Verboden toegang voor ruiters.png',
    updated_at = NOW()
WHERE sign_code = 'C15';

-- C17: Verboden toegang handkarren
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C17 Verboden toegang voor bestuurders van handkarren.png',
    updated_at = NOW()
WHERE sign_code = 'C17';

-- C19: Verboden toegang voetgangers
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C19 Verboden toegang voor voetgangers.png',
    updated_at = NOW()
WHERE sign_code = 'C19';

-- C21: Verboden toegang voertuigen boven massa-grens
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C21 Verboden toegang voor voertuigen met een massa groter dan aangeduid.png',
    updated_at = NOW()
WHERE sign_code = 'C21';

-- C22: Verboden toegang voor bestuurders van autocars
UPDATE traffic_signs
SET image_url       = 'images/signs/prohibition_signs/C22 Verboden toegang voor bestuurders van autocars.png',
    name_nl         = 'Verboden toegang voor bestuurders van autocars',
    name_en         = 'No entry for coaches',
    name_fr         = 'Accès interdit aux conducteurs d''autocars',
    name_ar         = 'ممنوع دخول الحافلات السياحية',
    updated_at      = NOW()
WHERE sign_code = 'C22';

-- C23: Verboden toegang vrachtverkeer
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C23 Verboden toegang voor bestuurders van voertuigen bestemd of gebruikt voor het vervoer van zaken.png',
    updated_at = NOW()
WHERE sign_code = 'C23';

-- C24a: Verboden toegang gevaarlijke goederen
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C24a Verboden toegang voor bestuurders van voertuigen die gevaarlijke goederen vervoeren.png',
    updated_at = NOW()
WHERE sign_code = 'C24a';

-- C24b: Verboden ontvlambaar/ontplofbaar
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C24b Verboden toegang voor bestuurders van voertuigen die gevaarlijke ontvlambare of ontplofbare stoffen vervoeren.png',
    updated_at = NOW()
WHERE sign_code = 'C24b';

-- C24c: Verboden verontreinigende stoffen
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C24c Verboden toegang voor bestuurders van voertuigen die gevaarlijke verontreinigende stoffen vervoeren.png',
    updated_at = NOW()
WHERE sign_code = 'C24c';

-- C25: Verboden — te lang
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C25 Verboden voor voertuigen langer dan het aangeduide.png',
    updated_at = NOW()
WHERE sign_code = 'C25';

-- C27: Verboden — te breed
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C27 Verboden voor voertuigen breder dan het aangeduide.png',
    updated_at = NOW()
WHERE sign_code = 'C27';

-- C29: Verboden — te hoog
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C29 Verboden voor voertuigen hoger dan het aangeduide.png',
    updated_at = NOW()
WHERE sign_code = 'C29';

-- C31a: Verbod links afslaan
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C31a Verbod om links af te slaan.png',
    updated_at = NOW()
WHERE sign_code = 'C31a';

-- C31b: Verbod rechts afslaan
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C31b Verbod rechts af te slaan.png',
    updated_at = NOW()
WHERE sign_code = 'C31b';

-- C33: Verbod keren
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C33 Verbod om te keren.png',
    updated_at = NOW()
WHERE sign_code = 'C33';

-- C37: Einde verbod C35
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C37 Einde verbod opgelegd door het verkeersbord C35.png',
    updated_at = NOW()
WHERE sign_code = 'C37';

-- C39: Verbod inhalen vrachtwagens
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C39 Verbod voertuigen met toegelaten massa groter dan 3500 kg in te halen.png',
    updated_at = NOW()
WHERE sign_code = 'C39';

-- C41: Einde verbod C39 — no disk image, normalise URL format
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C41 Einde van het verbod opgelegd door het verkeersbord C39.png',
    updated_at = NOW()
WHERE sign_code = 'C41';

-- C45: Einde snelheidsbeperking C43
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C45 Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.png',
    updated_at = NOW()
WHERE sign_code = 'C45';

-- C46: Einde alle verboden
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C46 Einde van alle plaatselijke verbodsbepalingen opgelegd aan de voertuigen in beweging.png',
    updated_at = NOW()
WHERE sign_code = 'C46';

-- C47: Tolpost
UPDATE traffic_signs
SET image_url  = 'images/signs/prohibition_signs/C47 Tolpost. Verbod voorbij te rijden zonder te stoppen.png',
    updated_at = NOW()
WHERE sign_code = 'C47';

-- =====================
-- D-series — mandatory_signs/
-- (V118 fixed: D1f, D3a, D3b, D4-rechtdoor, D4-links, D4-rechts, D11, D1c, D1d)
-- Below: remaining D-signs with old assets/ paths
-- =====================

-- D1a: Verplichting rechtdoor
UPDATE traffic_signs
SET image_url  = 'images/signs/mandatory_signs/D1a Verplichting rechtdoor.png',
    updated_at = NOW()
WHERE sign_code = 'D1a';

-- D1b: Verplichting rechts afslaan (canonical — rechts as main)
UPDATE traffic_signs
SET image_url  = 'images/signs/mandatory_signs/D1b Verplichting rechts afslaan.png',
    updated_at = NOW()
WHERE sign_code = 'D1b-links';

-- D1d: Verplichting rechts aanhouden
UPDATE traffic_signs
SET image_url  = 'images/signs/mandatory_signs/D1d Verplichting rechts aanhouden.png',
    updated_at = NOW()
WHERE sign_code = 'D1d';

-- D1e: Verplicht linksaf volgen
UPDATE traffic_signs
SET image_url  = 'images/signs/mandatory_signs/D1e Verplicht links afslaan.png',
    updated_at = NOW()
WHERE sign_code = 'D1e';

-- D9a: Verplicht fietspad — no disk image
UPDATE traffic_signs
SET image_url  = 'images/signs/mandatory_signs/D9a Verplicht fietspad.png',
    updated_at = NOW()
WHERE sign_code = 'D9a';



-- D10: Voetgangers en fietsers
UPDATE traffic_signs
SET image_url  = 'images/signs/mandatory_signs/D10 Deel van de weg voorbehouden voor voetgangers en fietsers.png',
    updated_at = NOW()
WHERE sign_code = 'D10';

-- D13: Geen disk image
UPDATE traffic_signs
SET image_url  = 'images/signs/mandatory_signs/D13 Verplichte weg voor ruiters.png',
    updated_at = NOW()
WHERE sign_code = 'D13';

-- =====================
-- F-series — information_signs/
-- V118 fixed F117-v1 and F118-v1 (variant codes) but NOT the main F117/F118/F97 codes
-- =====================

-- F117: Begin van lage emissiezone (main sign code, not the -v1 variant)
UPDATE traffic_signs
SET image_url  = 'images/signs/information_signs/F117 Begin van lage emissiezone.png',
    updated_at = NOW()
WHERE sign_code = 'F117';

-- F118: Einde van lage emissiezone (main sign code)
UPDATE traffic_signs
SET image_url  = 'images/signs/information_signs/F118 Einde van lage emissiezone.png',
    updated_at = NOW()
WHERE sign_code = 'F118';

-- F97: Rijstrook versmalling (main sign code, V118 only fixed F97-v1)
UPDATE traffic_signs
SET image_url  = 'images/signs/information_signs/F97 Rijstrook versmalling.png',
    updated_at = NOW()
WHERE sign_code = 'F97';

-- F signs with no disk images — normalise URL format (service/facility signs)
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F29 Wegwijzer.png', updated_at = NOW() WHERE sign_code = 'F29';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F33a Wegwijzer verplegingsinrichting.png', updated_at = NOW() WHERE sign_code = 'F33a';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F33c Wegwijzer hulppost voor motorrijtuigen.png', updated_at = NOW() WHERE sign_code = 'F33c';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F34a Wegwijzer brandblusapparaat.png', updated_at = NOW() WHERE sign_code = 'F34a';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F34b Wegwijzer telefoon.png', updated_at = NOW() WHERE sign_code = 'F34b';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F34c Wegwijzer toiletten.png', updated_at = NOW() WHERE sign_code = 'F34c';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F35 Wegwijzer parkeergelegenheid.png', updated_at = NOW() WHERE sign_code = 'F35';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F37 Wegwijzer hotel.png', updated_at = NOW() WHERE sign_code = 'F37';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F41 Wegwijzer restaurant.png', updated_at = NOW() WHERE sign_code = 'F41';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F53 Informatiepaneel.png', updated_at = NOW() WHERE sign_code = 'F53';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F55 Doorrijdbaar water.png', updated_at = NOW() WHERE sign_code = 'F55';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F56 Voetgangersoversteekplaats.png', updated_at = NOW() WHERE sign_code = 'F56';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F59 Verblijfszone.png', updated_at = NOW() WHERE sign_code = 'F59';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F59a Begin verblijfszone.png', updated_at = NOW() WHERE sign_code = 'F59a';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F59b Einde verblijfszone.png', updated_at = NOW() WHERE sign_code = 'F59b';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F60 Begin schoolomgeving.png', updated_at = NOW() WHERE sign_code = 'F60';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F61 Einde schoolomgeving.png', updated_at = NOW() WHERE sign_code = 'F61';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F62 Begin fietszone.png', updated_at = NOW() WHERE sign_code = 'F62';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F63 Einde fietszone.png', updated_at = NOW() WHERE sign_code = 'F63';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F65 Parkeerplaats.png', updated_at = NOW() WHERE sign_code = 'F65';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F67 Parkeerplaats voorbehouden voor invaliden.png', updated_at = NOW() WHERE sign_code = 'F67';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F69 Parkeerplaats met beperkte parkeertijd.png', updated_at = NOW() WHERE sign_code = 'F69';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F71 Tankstation.png', updated_at = NOW() WHERE sign_code = 'F71';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F73 Laadpunt elektrische voertuigen.png', updated_at = NOW() WHERE sign_code = 'F73';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F75 Wegrestaurant.png', updated_at = NOW() WHERE sign_code = 'F75';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F77 Motel.png', updated_at = NOW() WHERE sign_code = 'F77';

-- E-series
-- E9j: no disk image — normalise URL format
UPDATE traffic_signs
SET image_url  = 'images/signs/parking_signs/E9j Parkeren uitsluitend voor speed pedelecs.png',
    updated_at = NOW()
WHERE sign_code = 'E9j';

-- =====================
-- INSERT missing D5 and D7 (official Belgian signs not in DB at all)
-- No disk images exist — will show fallback
-- =====================
INSERT INTO traffic_signs (sign_code, category_id, name_nl, name_en, name_fr, name_ar,
    description_nl, description_en, description_fr, description_ar,
    image_url, is_active, created_at, updated_at)
VALUES (
    'D5',
    (SELECT id FROM categories WHERE code = 'D'),
    'Verplicht fietspad',
    'Mandatory bicycle path',
    'Piste cyclable obligatoire',
    'مسار دراجات إجباري',
    'Verplicht fietspad voor fietsers en bromfietsers klasse A.',
    'Mandatory bicycle path for cyclists and class A mopeds.',
    'Piste cyclable obligatoire pour cyclistes et cyclomoteurs de classe A.',
    'مسار الدراجات الإجباري لراكبي الدراجات والدراجات البخارية من الفئة أ.',
    'images/signs/mandatory_signs/D5 Verplicht fietspad.png',
    TRUE, NOW(), NOW()
)
ON DUPLICATE KEY UPDATE updated_at = NOW();

INSERT INTO traffic_signs (sign_code, category_id, name_nl, name_en, name_fr, name_ar,
    description_nl, description_en, description_fr, description_ar,
    image_url, is_active, created_at, updated_at)
VALUES (
    'D7',
    (SELECT id FROM categories WHERE code = 'D'),
    'Verplicht rijwielpad',
    'Mandatory cycle and moped path',
    'Chemin obligatoire pour cycles et cyclomoteurs',
    'مسار دراجات وبخاريات إجباري',
    'Verplicht pad voor rijwielen en bromfietsen klasse A en B.',
    'Mandatory path for bicycles and class A and B mopeds.',
    'Chemin obligatoire pour bicyclettes et cyclomoteurs de classe A et B.',
    'مسار إجباري للدراجات والدراجات البخارية من الفئة أ وب.',
    'images/signs/mandatory_signs/D7 Verplicht rijwielpad.png',
    TRUE, NOW(), NOW()
)
ON DUPLICATE KEY UPDATE updated_at = NOW();
