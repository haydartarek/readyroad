-- Persist production image-path cleanup against the official backend image source.
-- Official source: readyroad/public/images/signs, exposed only as /images/signs/**.

UPDATE road_signs
SET image_path = CONCAT('/', image_path)
WHERE image_path IS NOT NULL
  AND image_path <> ''
  AND image_path LIKE 'images/signs/%';

UPDATE road_signs
SET image_path = CASE sign_code
    WHEN 'A11' THEN '/images/signs/danger_signs/A11 Uitweg op een kaai of een oever.png'
    WHEN 'A15' THEN '/images/signs/danger_signs/A15 Glibberige rijbaan.png'
    WHEN 'A19' THEN '/images/signs/danger_signs/A19 Vallende stenen.png'
    WHEN 'A1b' THEN '/images/signs/danger_signs/A1b Gevaarlijke bocht naar rechts.png'
    WHEN 'A1d' THEN '/images/signs/danger_signs/A1d Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts.png'
    WHEN 'A23' THEN '/images/signs/danger_signs/A23 Plaats waar speciaal veel kinderen komen.png'
    WHEN 'A39' THEN '/images/signs/danger_signs/A39 Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer.png'
    WHEN 'A45' THEN '/images/signs/danger_signs/A45 Overweg voor enkel spoor.png'
    WHEN 'A47' THEN '/images/signs/danger_signs/A47 Overweg voor twee of meer sporen.png'
    WHEN 'A49' THEN '/images/signs/danger_signs/A49 Kruising van een openbare weg door een of meer in de rijbaan aangelegde sporen.png'
    WHEN 'A5' THEN '/images/signs/danger_signs/A5 Steile helling.png'
    WHEN 'A53' THEN '/images/signs/danger_signs/A53 Verzinkbare paaltjes.png'
    WHEN 'A7a' THEN '/images/signs/danger_signs/A7a Rijbaanversmalling.png'
    WHEN 'A7b' THEN '/images/signs/danger_signs/A7b Rijbaanversmalling langs links.png'
    WHEN 'A7c' THEN '/images/signs/danger_signs/A7c Rijbaanversmalling langs rechts.png'
    WHEN 'B11' THEN '/images/signs/priority_signs/B11 Einde van voorrangsweg.png'
    WHEN 'B15a' THEN '/images/signs/priority_signs/B15a Voorrang op het eerstvolgende kruispunt - variant schuine zijweg links.png'
    WHEN 'B15b' THEN '/images/signs/priority_signs/B15b Voorrang op het eerstvolgende kruispunt - variant schuine zijweg rechts.png'
    WHEN 'B15c' THEN '/images/signs/priority_signs/B15c Voorrang op het eerstvolgende kruispunt.png'
    WHEN 'B15d' THEN '/images/signs/priority_signs/B15d Voorrang op het eerstvolgende kruispunt - variant zijweg links.png'
    WHEN 'B15e' THEN '/images/signs/priority_signs/B15e Voorrang op het eerstvolgende kruispunt - variant T-kruispunt.png'
    WHEN 'B15f' THEN '/images/signs/priority_signs/B15f Voorrang op het eerstvolgende kruispunt - variant Y-kruispunt.png'
    WHEN 'B15g' THEN '/images/signs/priority_signs/B15g Voorrang op kruisende zijweg.png'
    WHEN 'B17' THEN '/images/signs/priority_signs/B17 Kruispunt waar de voorrang van rechts geldt.png'
    WHEN 'B19' THEN '/images/signs/priority_signs/B19 Smalle doorgang. Voorrang verlenen aan de bestuurders die uit de tegenovergestelde richting komen.png'
    WHEN 'B21' THEN '/images/signs/priority_signs/B21 Smalle doorgang. Voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen.png'
    WHEN 'B22' THEN '/images/signs/priority_signs/B22 Fietsers en speed pedelecs mogen rechts afslaan en de verkeerslichten voorbijrijden.png'
    WHEN 'B9' THEN '/images/signs/priority_signs/B9 Voorrangsweg.png'
    WHEN 'C11' THEN '/images/signs/prohibition_signs/C11 Verboden toegang voor bestuurders van rijwielen.png'
    WHEN 'C43' THEN '/images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 50 km.png'
    WHEN 'D1e' THEN '/images/signs/mandatory_signs/D1e Verplicht links afslaan.png'
    WHEN 'D1f' THEN '/images/signs/mandatory_signs/D1f Verplicht rechts afslaan.png'
    WHEN 'D4-straight' THEN '/images/signs/mandatory_signs/D4 Verplicht rechtdoor gevaarlijke goederen.png'
    WHEN 'D4-left' THEN '/images/signs/mandatory_signs/D4 Verplicht links afslaan gevaarlijke goederen.png'
    WHEN 'D4-right' THEN '/images/signs/mandatory_signs/D4 Verplicht rechts afslaan gevaarlijke goederen.png'
    WHEN 'D4-rechtdoor' THEN '/images/signs/mandatory_signs/D4 Verplicht rechtdoor gevaarlijke goederen.png'
    WHEN 'D4-links' THEN '/images/signs/mandatory_signs/D4 Verplicht links afslaan gevaarlijke goederen.png'
    WHEN 'D4-rechts' THEN '/images/signs/mandatory_signs/D4 Verplicht rechts afslaan gevaarlijke goederen.png'
    WHEN 'E9a' THEN '/images/signs/parking_signs/E9a Parkeren toegelaten.png'
    WHEN 'E9a-disabled' THEN '/images/signs/parking_signs/E9a-mindervaliden Parkeren enkel toegelaten voor mindervaliden.png'
    WHEN 'E9a-disc' THEN '/images/signs/parking_signs/E9a-parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.png'
    WHEN 'E9a-electric' THEN '/images/signs/parking_signs/E9a-elektrisch laden Parkeerplaats voorbehouden voor het elektrisch opladen van je wagen.png'
    WHEN 'E9a-mindervaliden' THEN '/images/signs/parking_signs/E9a-mindervaliden Parkeren enkel toegelaten voor mindervaliden.png'
    WHEN 'E9a-parkeerschijf' THEN '/images/signs/parking_signs/E9a-parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.png'
    WHEN 'E9a-elektrisch' THEN '/images/signs/parking_signs/E9a-elektrisch laden Parkeerplaats voorbehouden voor het elektrisch opladen van je wagen.png'
    WHEN 'E9c' THEN '/images/signs/parking_signs/E9c Parkeren uitsluitend voor vrachtwagens.png'
    WHEN 'E9g' THEN '/images/signs/parking_signs/E9g Verplicht parkeren op de rijbaan.png'
    WHEN 'F103' THEN '/images/signs/zone_signs/F103 Begin van een voetgangerszone.png'
    WHEN 'F105' THEN '/images/signs/zone_signs/F105-Einde zone van een voetgangerszone.png'
    WHEN 'F117' THEN '/images/signs/zone_signs/F117 Begin van lage emissiezone.png'
    WHEN 'F118' THEN '/images/signs/zone_signs/F118-Einde van lage emissiezone.png'
    WHEN 'F39' THEN '/images/signs/road_markings/F39 Aankondiging van een omleiding.png'
    WHEN 'F4a' THEN '/images/signs/zone_signs/Zone-F4a Zone 30 km.png'
    WHEN 'F4b' THEN '/images/signs/zone_signs/Zone-F4b-Einde zone 30 km.png'
    WHEN 'F50bis' THEN '/images/signs/information_signs/F50-bis Opgepast als je van richting verandert voetgangers.png'
    WHEN 'F79' THEN '/images/signs/road_markings/F79 Tijdelijke verdeling van de rijstroken (met afstandsaanduiding).png'
    WHEN 'F81' THEN '/images/signs/road_markings/F81 Voorwegwijzer uitwijking.png'
    WHEN 'F83' THEN '/images/signs/road_markings/F83 Versmalling van de rijbaan.png'
    WHEN 'F85' THEN '/images/signs/road_markings/F85 Verlegging van de rijbaan.png'
    WHEN 'F89' THEN '/images/signs/road_markings/F89 Aanduiding van de maximumsnelheid per rijstrook.png'
    WHEN 'F91' THEN '/images/signs/road_markings/F91 Aanduiding van de maximumsnelheid per rijstrook (zonder afstand).png'
    WHEN 'F95' THEN '/images/signs/road_markings/F95 Einde van een rijstrook.png'
    WHEN 'F98' THEN '/images/signs/road_markings/F98 Bijzondere rijstrookregeling.png'
    WHEN 'ZC21-' THEN '/images/signs/zone_signs/ZC21-Einde zone verboden toegang voor bestuurders van voertuigen waarvan de massa hoger dan 3500 kg.png'
    WHEN 'ZC21-Einde' THEN '/images/signs/zone_signs/ZC21-Einde zone verboden toegang voor bestuurders van voertuigen waarvan de massa hoger dan 3500 kg.png'
    WHEN 'ZC35-' THEN '/images/signs/zone_signs/ZC35-Einde zone verboden inhalen.png'
    WHEN 'ZC35-Einde' THEN '/images/signs/zone_signs/ZC35-Einde zone verboden inhalen.png'
    WHEN 'ZC45' THEN '/images/signs/zone_signs/ZC45-Einde zone met een snelheidsbeperking.png'
    WHEN 'ZC5-' THEN '/images/signs/zone_signs/ZC5-Einde zone verboden toegang voor motorvoertuigen met meer dan 2 wielen.png'
    WHEN 'ZC5-Einde' THEN '/images/signs/zone_signs/ZC5-Einde zone verboden toegang voor motorvoertuigen met meer dan 2 wielen.png'
    WHEN 'ZE1-' THEN '/images/signs/zone_signs/ZE1-Einde zone parkeerverbod.png'
    WHEN 'ZE1-Einde' THEN '/images/signs/zone_signs/ZE1-Einde zone parkeerverbod.png'
    WHEN 'ZE9a' THEN '/images/signs/zone_signs/ZE9a Zone parkeren uitsluitend voor auto''s.png'
    WHEN 'ZE9a-' THEN '/images/signs/zone_signs/ZE9a-Einde zone parkeren uitsluitend voor auto''s.png'
    WHEN 'ZE9a-Einde' THEN '/images/signs/zone_signs/ZE9a-Einde zone parkeren uitsluitend voor auto''s.png'
    WHEN 'ZE9aT' THEN '/images/signs/zone_signs/ZE9aT Zone parkeren uitsluitend voor auto''s.png'
    WHEN 'ZE9aT-' THEN '/images/signs/zone_signs/ZE9aT-Einde zone parkeren uitsluitend voor auto''s.png'
    WHEN 'ZE9aT-Einde' THEN '/images/signs/zone_signs/ZE9aT-Einde zone parkeren uitsluitend voor auto''s.png'
    WHEN 'ZONE-F111' THEN '/images/signs/zone_signs/Zone-F111 Zone Fietsstraat.png'
    WHEN 'ZONE-F113' THEN '/images/signs/zone_signs/Zone-F111-Eind zone Fietsstraat.png'
    WHEN 'Zone-F111-Eind' THEN '/images/signs/zone_signs/Zone-F111-Eind zone Fietsstraat.png'
    ELSE image_path
END
WHERE sign_code IN (
    'A11','A15','A19','A1b','A1d','A23','A39','A45','A47','A49','A5','A53','A7a','A7b','A7c',
    'B11','B15a','B15b','B15c','B15d','B15e','B15f','B15g','B17','B19','B21','B22','B9',
    'C11','C43',
    'D1e','D1f','D4-straight','D4-left','D4-right','D4-rechtdoor','D4-links','D4-rechts',
    'E9a','E9a-disabled','E9a-disc','E9a-electric','E9a-mindervaliden','E9a-parkeerschijf','E9a-elektrisch','E9c','E9g',
    'F103','F105','F117','F118','F39','F4a','F4b','F50bis','F79','F81','F83','F85','F89','F91','F95','F98',
    'ZC21-','ZC21-Einde','ZC35-','ZC35-Einde','ZC45','ZC5-','ZC5-Einde','ZE1-','ZE1-Einde',
    'ZE9a','ZE9a-','ZE9a-Einde','ZE9aT','ZE9aT-','ZE9aT-Einde','ZONE-F111','ZONE-F113','Zone-F111-Eind'
);

UPDATE road_signs
SET image_path = NULL
WHERE sign_code IN (
    'C11a','C11b','C28a','C43_70','C43_90','D9b','E11','E9i','F45b','F87'
);
