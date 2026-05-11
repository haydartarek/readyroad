-- Align the remaining DB sign image path after V238.
-- The signs.json canonical sync owns road_signs rows at startup.

UPDATE road_signs
SET image_path = '/images/signs/information_signs/F50-bis Opgepast als je van richting verandert voetgangers.png'
WHERE sign_code = 'F50bis';
