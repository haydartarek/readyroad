-- V191__Fix_Image_Paths_To_Match_Frontend_Files.sql
-- Corrects road_signs.image_path values to match actual filenames
-- in readyroad_front_end/web_app/public/images/signs/

-- Prohibition signs
UPDATE road_signs SET image_path = 'images/signs/prohibition_signs/C22 Verboden toegang voor bestuurders van autocars.png'
WHERE sign_code = 'C22';

UPDATE road_signs SET image_path = 'images/signs/prohibition_signs/C39 Verbod voertuigen met toegelaten massa groter dan 3500 kg in te halen.png'
WHERE sign_code = 'C39';

UPDATE road_signs SET image_path = 'images/signs/prohibition_signs/C41 Einde van het verbod opgelegd door het verkeersbord C39.png'
WHERE sign_code = 'C41';

UPDATE road_signs SET image_path = 'images/signs/prohibition_signs/C43 Verbod te rijden met een grotere snelheid dan 50 km.png'
WHERE sign_code = 'C43';

-- Parking signs
UPDATE road_signs SET image_path = 'images/signs/parking_signs/E9b Parkeren uitsluitend voor autos.png'
WHERE sign_code = 'E9b';

UPDATE road_signs SET image_path = 'images/signs/parking_signs/E9h Parkeren uitsluitend voor kampeerautos.png'
WHERE sign_code = 'E9h';

UPDATE road_signs SET image_path = 'images/signs/parking_signs/E9j wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en autos.png'
WHERE sign_code = 'E9j';

-- Information signs
UPDATE road_signs SET image_path = 'images/signs/information_signs/F101a Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.png'
WHERE sign_code = 'F101a';

UPDATE road_signs SET image_path = 'images/signs/information_signs/F101c Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.png'
WHERE sign_code = 'F101c';

UPDATE road_signs SET image_path = 'images/signs/information_signs/F1b Begin van een bebouwde kom.png'
WHERE sign_code = 'F1b';

UPDATE road_signs SET image_path = 'images/signs/information_signs/F4a Zone 30 km.png'
WHERE sign_code = 'F4a';

UPDATE road_signs SET image_path = 'images/signs/information_signs/F4b - Einde zone 30 km.png'
WHERE sign_code = 'F4b';

UPDATE road_signs SET image_path = 'images/signs/information_signs/F99a Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.png'
WHERE sign_code = 'F99a';

UPDATE road_signs SET image_path = 'images/signs/information_signs/F99c Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.png'
WHERE sign_code = 'F99c';
