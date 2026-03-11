-- Fix: E9a sign (electric vehicle parking) had the wrong image URL.
-- The sign described "Parkeerplaats voor elektrische voertuigen" but pointed
-- to the generic "Parkeren toegelaten" image. Corrected to the elektrisch laden image.
UPDATE traffic_signs
SET image_url  = 'images/signs/parkeren/E9a elektrisch laden Parkeerplaats voorbehouden voor het elektrisch opladen van je wagen.png',
    updated_at = NOW()
WHERE sign_code = 'E9a';
