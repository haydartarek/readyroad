-- Step 1: Delete signs NOT in the approved whitelist
DELETE FROM traffic_signs
WHERE sign_code IN (
    'F1a', 'F3a', 'F31', 'F39', 'F45b-v2', 'F50b',
    'F79', 'F79-V1', 'F83', 'F85', 'F89', 'F91',
    'F95', 'F98', 'F117-v1', 'F118-v1', 'F97-v1'
);

-- Step 2: Fix sign_code for F50bis variants
UPDATE traffic_signs SET sign_code = 'F50bis-cyclists'
WHERE sign_code = 'F50bis'
  AND image_url LIKE '%fietsers%';

UPDATE traffic_signs SET sign_code = 'F50bis-pedestrians'
WHERE sign_code = 'F50bis'
  AND image_url LIKE '%voetgangers%';

-- Step 3: Fix sign_code F45b (keep only one, rename correctly)
UPDATE traffic_signs SET sign_code = 'F45b'
WHERE sign_code = 'F45b'
  AND image_url LIKE '%uitgezonderd%';

-- Step 4: Unify all image_url paths to images/signs/information_signs/
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F1b Begin van een bebouwde kom.png', updated_at = NOW() WHERE sign_code = 'F1b';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F3b Einde van een bebouwde kom.png', updated_at = NOW() WHERE sign_code = 'F3b';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F4a Zone 30 km.png', updated_at = NOW() WHERE sign_code = 'F4a';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F4b - Einde zone 30 km.png', updated_at = NOW() WHERE sign_code = 'F4b';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F5 Autosnelweg.png', updated_at = NOW() WHERE sign_code = 'F5';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F7 Einde autosnelweg.png', updated_at = NOW() WHERE sign_code = 'F7';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F8 Tunnel.png', updated_at = NOW() WHERE sign_code = 'F8';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F9 Autoweg.png', updated_at = NOW() WHERE sign_code = 'F9';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F11 Einde van de autoweg.png', updated_at = NOW() WHERE sign_code = 'F11';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F12a Begin van een woonerf of van een erf.png', updated_at = NOW() WHERE sign_code = 'F12a';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F12b Einde van een woonerf of van een erf.png', updated_at = NOW() WHERE sign_code = 'F12b';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F13 Rijstrook keuze.png', updated_at = NOW() WHERE sign_code = 'F13';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F14 Opstelvak voor fietsers en bromfietsen.png', updated_at = NOW() WHERE sign_code = 'F14';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F17 Rijstrook aanduiding voorbehouden voor autobussen.png', updated_at = NOW() WHERE sign_code = 'F17';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F18 Bijzondere overrijdbare bedding.png', updated_at = NOW() WHERE sign_code = 'F18';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F19 Eenrichtingsverkeer.png', updated_at = NOW() WHERE sign_code = 'F19';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F21 Rechts of links voorbijrijden toegelaten.png', updated_at = NOW() WHERE sign_code = 'F21';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F23a Nummer van een gewone weg.png', updated_at = NOW() WHERE sign_code = 'F23a';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F23b Nummer van een autosnelweg.png', updated_at = NOW() WHERE sign_code = 'F23b';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F23c Nummer van een internationale weg.png', updated_at = NOW() WHERE sign_code = 'F23c';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F23d Nummer van een ringweg.png', updated_at = NOW() WHERE sign_code = 'F23d';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F43 Gemeentegrens.png', updated_at = NOW() WHERE sign_code = 'F43';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F45 Doodlopende weg.png', updated_at = NOW() WHERE sign_code = 'F45';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F45b Doodlopende weg, uitgezonderd voetgangers en fietsers.png', updated_at = NOW() WHERE sign_code = 'F45b';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F45b Doodlopende weg (rechts_links).png', updated_at = NOW() WHERE sign_code = 'F45b-v2';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F45L Doodlopende weg, linkse doorgang.png', updated_at = NOW() WHERE sign_code = 'F45L';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F45R Doodlopende weg, rechtse doorgang.png', updated_at = NOW() WHERE sign_code = 'F45R';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F47 Einde van de werken.png', updated_at = NOW() WHERE sign_code = 'F47';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F49 Oversteekplaats voor voetgangers.png', updated_at = NOW() WHERE sign_code = 'F49';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F50 Oversteekplaats voor fietsers en bromfietsers.png', updated_at = NOW() WHERE sign_code = 'F50';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F50bis Opgepast als je van richting veranderd, fietsers.png', updated_at = NOW() WHERE sign_code = 'F50bis-cyclists';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F50bis Opgepast als je van richting veranderd, voetgangers.png', updated_at = NOW() WHERE sign_code = 'F50bis-pedestrians';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F87 Verhoogde inrichting (vluchtheuvel).png', updated_at = NOW() WHERE sign_code = 'F87';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F97 Rijstrook versmalling.png', updated_at = NOW() WHERE sign_code = 'F97-v1';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F99a Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.png', updated_at = NOW() WHERE sign_code = 'F99a';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F99b Deel van de openbare weg voorbehouden voor fietsers en voetgangers.png', updated_at = NOW() WHERE sign_code = 'F99b';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F99c Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.png', updated_at = NOW() WHERE sign_code = 'F99c';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F101a Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.png', updated_at = NOW() WHERE sign_code = 'F101a';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F101b Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.png', updated_at = NOW() WHERE sign_code = 'F101b';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F101c Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.png', updated_at = NOW() WHERE sign_code = 'F101c';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F103 Begin van een voetgangerszone.png', updated_at = NOW() WHERE sign_code = 'F103';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F105 Einde van een voetgangerszone.png', updated_at = NOW() WHERE sign_code = 'F105';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F111 Fietsstraat.png', updated_at = NOW() WHERE sign_code = 'F111';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F113 Einde fietsstraat.png', updated_at = NOW() WHERE sign_code = 'F113';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F117 Begin van lage emissiezone.png', updated_at = NOW() WHERE sign_code = 'F117-v1';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F118 Einde van lage emissiezone.png', updated_at = NOW() WHERE sign_code = 'F118-v1';

-- Step 5: Fix sign_codes that had -v1 suffix
UPDATE traffic_signs SET sign_code = 'F97', updated_at = NOW() WHERE sign_code = 'F97-v1';
UPDATE traffic_signs SET sign_code = 'F117', updated_at = NOW() WHERE sign_code = 'F117-v1';
UPDATE traffic_signs SET sign_code = 'F118', updated_at = NOW() WHERE sign_code = 'F118-v1';
