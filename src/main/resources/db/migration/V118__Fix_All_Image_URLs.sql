-- V118: Normalize all traffic sign image_url values to standard format:
-- images/signs/{english_folder}/{filename}.png

-- =====================
-- Danger Signs (A series) - gevaarsborden
-- =====================
UPDATE traffic_signs SET image_url = 'images/signs/danger_signs/A33-v1 Verkeerslichten.png' WHERE sign_code = 'A33-v1';

-- =====================
-- Priority Signs (B series) - voorrangsborden
-- =====================
UPDATE traffic_signs SET image_url = 'images/signs/priority_signs/B15A Variant schuine rechts.png' WHERE sign_code = 'B15A-v1';
UPDATE traffic_signs SET image_url = 'images/signs/priority_signs/B15A Versmalling van rechts.png' WHERE sign_code = 'B15A-v2';
UPDATE traffic_signs SET image_url = 'images/signs/priority_signs/B23 Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden.png' WHERE sign_code = 'B23';

-- =====================
-- Prohibition Signs (C series) - verbodsborden
-- =====================
UPDATE traffic_signs SET image_url = 'images/signs/prohibition_signs/C35 Verbod een voertuig links in te halen.png' WHERE sign_code = 'C35';
UPDATE traffic_signs SET image_url = 'images/signs/prohibition_signs/C43 - Verbod te rijden met een grotere snelheid dan is aangeduid 50 km.png' WHERE sign_code = 'C43_50';
UPDATE traffic_signs SET image_url = 'images/signs/prohibition_signs/C43 - Verbod te rijden met een grotere snelheid dan is aangeduid 70 km.png' WHERE sign_code = 'C43_70';
UPDATE traffic_signs SET image_url = 'images/signs/prohibition_signs/C6 Verboden toegang voor bestuurders van quads.png' WHERE sign_code = 'C6';

-- =====================
-- Mandatory Signs (D series) - gebodsborden
-- =====================
UPDATE traffic_signs SET image_url = 'images/signs/mandatory_signs/D11 Verplichte weg voor voetgangers.png' WHERE sign_code = 'D11';
UPDATE traffic_signs SET image_url = 'images/signs/mandatory_signs/D1c Verplichting links aanhouden.png' WHERE sign_code = 'D1a-links';
UPDATE traffic_signs SET image_url = 'images/signs/mandatory_signs/D1d Verplichting rechts aanhouden.png' WHERE sign_code = 'D1a-rechts';
UPDATE traffic_signs SET image_url = 'images/signs/mandatory_signs/D1f Verplicht de aangeduide richting te volgen (rechtsaf).png' WHERE sign_code = 'D1f';
UPDATE traffic_signs SET image_url = 'images/signs/mandatory_signs/D3a Verplicht één van de pijlen te volgen.png' WHERE sign_code = 'D3a';
UPDATE traffic_signs SET image_url = 'images/signs/mandatory_signs/D3b Verplicht één van de pijlen te volgen.png' WHERE sign_code = 'D3b';
UPDATE traffic_signs SET image_url = 'images/signs/mandatory_signs/D4 Verplicht rechtdoor voor voertuigen die gevaarlijke goederen vervoeren.png' WHERE sign_code = 'D4';
UPDATE traffic_signs SET image_url = 'images/signs/mandatory_signs/D4 Verplicht linksaf voor voertuigen die gevaarlijke goederen vervoeren.png' WHERE sign_code = 'D4-links';
UPDATE traffic_signs SET image_url = 'images/signs/mandatory_signs/D4 Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.png' WHERE sign_code = 'D4-rechts';

-- =====================
-- Parking Signs (E series) - parkeren
-- =====================
UPDATE traffic_signs SET image_url = 'images/signs/parking_signs/E9a Parkeren toegelaten.png' WHERE sign_code = 'E9a-v10';
UPDATE traffic_signs SET image_url = 'images/signs/parking_signs/E9a - Parkeren toegelaten.png' WHERE sign_code = 'E9a-v2';
UPDATE traffic_signs SET image_url = 'images/signs/parking_signs/E9a Elektrisch opladen.png' WHERE sign_code = 'E9a-v3';
UPDATE traffic_signs SET image_url = 'images/signs/parking_signs/E9a parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.png' WHERE sign_code = 'E9a-v6';
UPDATE traffic_signs SET image_url = 'images/signs/parking_signs/E9a mindervaliden Parkeren enkel toegelaten voor mindervaliden.png' WHERE sign_code = 'E9a-v7';
UPDATE traffic_signs SET image_url = 'images/signs/parking_signs/E9g Verplicht parkeren op de rijbaan.png' WHERE sign_code = 'E9g-v1';

-- =====================
-- Information Signs (F series) - aanwijzingsborden
-- =====================
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F101a Einde voorbehouden voor voetgangers fietsers ruiters en speed pedelecs.png' WHERE sign_code = 'F101a';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F101b Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.png' WHERE sign_code = 'F101b';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F101c Einde voorbehouden voor landbouwvoertuigen voetgangers fietsers ruiters en speed pedelecs.png' WHERE sign_code = 'F101c';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F117 Begin van lage emissiezone.png' WHERE sign_code = 'F117-v1';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F118 Einde van lage emissiezone.png' WHERE sign_code = 'F118-v1';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F12a Begin van een woonerf of van een erf.png' WHERE sign_code = 'F12a';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F12b Einde van een woonerf of van een erf.png' WHERE sign_code = 'F12b';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F17 Rijstrook aanduiding voorbehouden voor autobussen.png' WHERE sign_code = 'F17';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F18 Bijzondere overrijdbare bedding.png' WHERE sign_code = 'F18';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F1a Begin van een bebouwde kom-0.png' WHERE sign_code = 'F1a';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F1b Begin van een bebouwde kom-2.png' WHERE sign_code = 'F1b';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F23a Nummer van een gewone weg.png' WHERE sign_code = 'F23a';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F23b Nummer van een autosnelweg.png' WHERE sign_code = 'F23b';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F23c Nummer van een internationale weg.png' WHERE sign_code = 'F23c';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F23d Nummer van een ringweg.png' WHERE sign_code = 'F23d';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F39 Aankondiging van een omleiding.png' WHERE sign_code = 'F39';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F3a Einde van een bebouwde kom-2.png' WHERE sign_code = 'F3a';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F3b Einde van een bebouwde kom.png' WHERE sign_code = 'F3b';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F45b Doodlopende weg, uitgezonderd voetgangers en fietsers.png' WHERE sign_code = 'F45b';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F45b Doodlopende weg (rechts_links).png' WHERE sign_code = 'F45b-v2';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F45L Doodlopende weg, linkse doorgang.png' WHERE sign_code = 'F45L';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F45R Doodlopende weg, rechtse doorgang.png' WHERE sign_code = 'F45R';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F4a Zone 30 km u.png' WHERE sign_code = 'F4a';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F4b Einde zone 30 kmu.png' WHERE sign_code = 'F4b';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F50bis Opgepast als je van richting verandert fietsers.png' WHERE sign_code = 'F50b';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F50bis Opgepast als je van richting verandert voetgangers.png' WHERE sign_code = 'F50bis';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F79 Tijdelijke verdeling van de rijstroken (met afstandsaanduiding).png' WHERE sign_code = 'F79';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F79 Tijdelijke verdeling van de rijstroken (met afstandsaanduiding).png' WHERE sign_code = 'F79-V1';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F83 Versmalling van de rijbaan.png' WHERE sign_code = 'F83';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F85 Verlegging van de rijbaan.png' WHERE sign_code = 'F85';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F87 Verhoogde inrichting (vluchtheuvel).png' WHERE sign_code = 'F87';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F89 Aanduiding van de maximumsnelheid per rijstrook.png' WHERE sign_code = 'F89';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F91 Aanduiding van de maximumsnelheid per rijstrook (zonder afstand).png' WHERE sign_code = 'F91';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F95 Einde van een rijstrook.png' WHERE sign_code = 'F95';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F97 Rijstrook versmalling.png' WHERE sign_code = 'F97-v1';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F98 Bijzondere rijstrookregeling.png' WHERE sign_code = 'F98';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F99a Voorbehouden voor voetgangers fietsers ruiters en speed pedelecs.png' WHERE sign_code = 'F99a';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F99b Deel van de openbare weg voorbehouden voor fietsers en voetgangers.png' WHERE sign_code = 'F99b';
UPDATE traffic_signs SET image_url = 'images/signs/information_signs/F99c Voorbehouden voor landbouwvoertuigen voetgangers fietsers ruiters en speed pedelecs.png' WHERE sign_code = 'F99c';

-- =====================
-- Additional Signs (G and M series) - onderborden
-- =====================
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/GIa Aanduiding van een afstand.png' WHERE sign_code = 'GIa';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/GIb Aanduiding van een afstand.png' WHERE sign_code = 'GIb';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/GII Aanduiding van de lengte van een gedeelte van de openbare weg.png' WHERE sign_code = 'GII';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/GIII Opgepast kans op aquaplaning.png' WHERE sign_code = 'GIII-aquaplaning';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/GIII Opgepast kans op ijzel.png' WHERE sign_code = 'GIII-ijzel';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/GIII Opgepast industriezone.png' WHERE sign_code = 'GIII-industriezone';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/GIII Opgepast uitrit.png' WHERE sign_code = 'GIII-uitrit';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/GIV Enkel laden & lossen.png' WHERE sign_code = 'GIV';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/GIX Versmalling van een rijstrook.png' WHERE sign_code = 'GIX';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/GV Aanvulling op de verkeersborden stilstaan en parkeren.png' WHERE sign_code = 'GV';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/GVIIa Aanvulling van de verkeersborden voor parkeren.png' WHERE sign_code = 'GVIIa';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/GVIIb Aanvulling van de verkeersborden voor parkeren.png' WHERE sign_code = 'GVIIb';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/GVIId Aanvulling van de verkeersborden voor parkeren.png' WHERE sign_code = 'GVIId';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/GVIId Aanvulling van de verkeersborden voor parkerenCARPOOL.png' WHERE sign_code = 'GVIId-CARPOOL';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/GVIId - Aanvulling van de verkeersborden voor parkeren p + r.png' WHERE sign_code = 'GVIId-PR';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/GVIII Voorrangs aanduiding.png' WHERE sign_code = 'GVIII';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/GXa Begin van de reglementering.png' WHERE sign_code = 'GXa';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/GXb Einde van de reglementering.png' WHERE sign_code = 'GXb';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/GXc Reglementering op een korte afstand.png' WHERE sign_code = 'GXc';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/GXd Reglementering op een lange afstand.png' WHERE sign_code = 'GXd';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/GXI Afrit.png' WHERE sign_code = 'GXI';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/M1 Enkel voor fietsers.png' WHERE sign_code = 'M1';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/M10 Fietsers en bromfietser in twee richtingen op de dwarslopende weg die je gaat oprijden.png' WHERE sign_code = 'M10';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/M12 30 min parkeren.png' WHERE sign_code = 'M12-30min';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/M12 Uitzonderd fiets en bromfiets A_P.png' WHERE sign_code = 'M12-fiets-brom';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/M12 Fietsrichtingen links rechts.png' WHERE sign_code = 'M12-richtingen';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/M13 Verplichting voor speed pedelecs.png' WHERE sign_code = 'M13';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/M15 Verbod voor speed pedelecs.png' WHERE sign_code = 'M15';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/M17 Fietsers en speed pedelecs mogen in 2 richtingen.png' WHERE sign_code = 'M17';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/M2 Uitzonderd fiets.png' WHERE sign_code = 'M2';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/M3bis Uitgezonderd fietsers en bromfietsers.png' WHERE sign_code = 'M3bis';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/M4 Fietsers mogen in 2 richtingen.png' WHERE sign_code = 'M4';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/M5 Fietsers en bromfietsers Klasse A mogen in 2 richtingen.png' WHERE sign_code = 'M5';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/M7 Verbod voor bromfietsen klasse B.png' WHERE sign_code = 'M7';
UPDATE traffic_signs SET image_url = 'images/signs/additional_signs/M9 Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.png' WHERE sign_code = 'M9';

-- =====================
-- Delineation Signs (TYPE/MARK series) - afbakeningsborden
-- =====================
UPDATE traffic_signs SET image_url = 'images/signs/delineation_signs/markering_rechts_verticaal_3.png' WHERE sign_code = 'MARK-R3';
UPDATE traffic_signs SET image_url = 'images/signs/delineation_signs/Type Ia1 Bakens voor signalisatie op afstand, links.png' WHERE sign_code = 'TYPE-IA1';
UPDATE traffic_signs SET image_url = 'images/signs/delineation_signs/Type Ia2 Bakens voor signalisatie op afstand, splitsing links.png' WHERE sign_code = 'TYPE-IA2';
UPDATE traffic_signs SET image_url = 'images/signs/delineation_signs/Type Ib1 Bakens voor signalisatie op afstand, rechts.png' WHERE sign_code = 'TYPE-IB1';
UPDATE traffic_signs SET image_url = 'images/signs/delineation_signs/Type Ib2 Bakens voor signalisatie op afstand, splitsing rechts.png' WHERE sign_code = 'TYPE-IB2';
UPDATE traffic_signs SET image_url = 'images/signs/delineation_signs/Type Ic Bebakening van een bocht.png' WHERE sign_code = 'TYPE-IC';
UPDATE traffic_signs SET image_url = 'images/signs/delineation_signs/Type IIb Baken voor zijdelingse signalisatie, rechts.png' WHERE sign_code = 'TYPE-IIB';
UPDATE traffic_signs SET image_url = 'images/signs/delineation_signs/Type V Bebakening van een bocht.png' WHERE sign_code = 'TYPE-V';
UPDATE traffic_signs SET image_url = 'images/signs/delineation_signs/Type VaBebakening van een bocht.png' WHERE sign_code = 'TYPE-VA';

-- =====================
-- Zone Signs (Z series) - zoneborden
-- =====================
UPDATE traffic_signs SET image_url = 'images/signs/zone_signs/ZC21 - Zone verboden toegang voor bestuurders van voertuigen waarvan de massa hoger is dan de aangeduide massa.png' WHERE sign_code = 'ZC21-zone';
UPDATE traffic_signs SET image_url = 'images/signs/zone_signs/ZC21T Zone verboden toegang voor bestuurders van voertuigen waarvan de massa hoger is dan de aangeduide massa.png' WHERE sign_code = 'ZC21T-v2';
UPDATE traffic_signs SET image_url = 'images/signs/zone_signs/ZC35 Zone verboden inhalen.png' WHERE sign_code = 'ZC35-v1';
UPDATE traffic_signs SET image_url = 'images/signs/zone_signs/ZC35T Einde zone verboden inhalen.png' WHERE sign_code = 'ZC35T-v1';
UPDATE traffic_signs SET image_url = 'images/signs/zone_signs/ZC5 Zone verboden toegang voor motorvoertuigen met meer dan 2 wielen.png' WHERE sign_code = 'ZC5-v1';
UPDATE traffic_signs SET image_url = 'images/signs/zone_signs/ZE1 Zone parkeerverbod.png' WHERE sign_code = 'ZE1-v1';
UPDATE traffic_signs SET image_url = 'images/signs/zone_signs/ZE9a parkeerschijf Zone parkeren beperkt in tijd, parkeerschijf verplicht.png' WHERE sign_code = 'ZE9a-v1';
UPDATE traffic_signs SET image_url = 'images/signs/zone_signs/ZE9a Zone parkeren uitsluitend voor auto''s.png' WHERE sign_code = 'ZE9a-v2';
UPDATE traffic_signs SET image_url = 'images/signs/zone_signs/ZE9a Zone parkeren uitsluitend voor auto''s_0.png' WHERE sign_code = 'ZE9a-v3';
UPDATE traffic_signs SET image_url = 'images/signs/zone_signs/ZE9aT Zone parkeren uitsluitend voor auto''s.png' WHERE sign_code = 'ZE9aT-v1';
UPDATE traffic_signs SET image_url = 'images/signs/zone_signs/ZE9T Zone parkeren voor voertuigen met 3.5t uitzondering.png' WHERE sign_code = 'ZE9T-v1';
UPDATE traffic_signs SET image_url = 'images/signs/zone_signs/ZONE F113 Einde ZONE Fietsstraat.png' WHERE sign_code = 'ZONE';
