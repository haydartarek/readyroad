-- V159__Fix_E9b_E9h_E9j_Apostrophe_In_Image_Path.sql
--
-- Next.js static file serving cannot resolve files whose names contain
-- an apostrophe (ASCII ' or Unicode U+2019) when they are URL-encoded.
-- Rename the three affected parking sign image references to apostrophe-free
-- equivalents. The physical files have been copied under the new names in
-- readyroad_front_end/web_app/public/images/signs/parking_signs/.

-- ── traffic_signs ──────────────────────────────────────────────────────────

UPDATE traffic_signs
SET   image_url  = 'images/signs/parking_signs/E9b Parkeren uitsluitend voor autos.png',
      image_path = 'images/signs/parking_signs/E9b Parkeren uitsluitend voor autos.png',
      updated_at = NOW()
WHERE sign_code = 'E9b';

UPDATE traffic_signs
SET   image_url  = 'images/signs/parking_signs/E9h Parkeren uitsluitend voor kampeerautos.png',
      image_path = 'images/signs/parking_signs/E9h Parkeren uitsluitend voor kampeerautos.png',
      updated_at = NOW()
WHERE sign_code = 'E9h';

UPDATE traffic_signs
SET   image_url  = 'images/signs/parking_signs/E9j wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en autos.png',
      image_path = 'images/signs/parking_signs/E9j wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en autos.png',
      updated_at = NOW()
WHERE sign_code = 'E9j';

-- ── road_signs ─────────────────────────────────────────────────────────────

UPDATE road_signs
SET   image_path = 'images/signs/parking_signs/E9b Parkeren uitsluitend voor autos.png',
      updated_at = NOW()
WHERE sign_code = 'E9b';

UPDATE road_signs
SET   image_path = 'images/signs/parking_signs/E9h Parkeren uitsluitend voor kampeerautos.png',
      updated_at = NOW()
WHERE sign_code = 'E9h';

UPDATE road_signs
SET   image_path = 'images/signs/parking_signs/E9j wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en autos.png',
      updated_at = NOW()
WHERE sign_code = 'E9j';
