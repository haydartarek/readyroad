-- V42__Fix_Image_Paths.sql
-- Fix image paths that don't match actual files

-- Fix M3b -> M3 (bicycle_signs)
UPDATE traffic_signs
SET image_url = 'assets/traffic_signs/bicycle_signs/M3.png'
WHERE image_url = 'assets/traffic_signs/bicycle_signs/M3b.png';

-- Fix M5b -> M5 (bicycle_signs)
UPDATE traffic_signs
SET image_url = 'assets/traffic_signs/bicycle_signs/M5.png'
WHERE image_url = 'assets/traffic_signs/bicycle_signs/M5b.png';

-- Fix F34b -> F34a (information_signs)
UPDATE traffic_signs
SET image_url = 'assets/traffic_signs/information_signs/F34a.png'
WHERE image_url = 'assets/traffic_signs/information_signs/F34b.png';

-- Fix F34c -> F34a (information_signs)
UPDATE traffic_signs
SET image_url = 'assets/traffic_signs/information_signs/F34a.png'
WHERE image_url = 'assets/traffic_signs/information_signs/F34c.png';

-- Fix F50b -> F50.png (information_signs)
UPDATE traffic_signs
SET image_url = 'assets/traffic_signs/information_signs/F50.png'
WHERE image_url = 'assets/traffic_signs/information_signs/F50b.png';

-- Fix E9j -> E9i (parking_signs) - closest available
UPDATE traffic_signs
SET image_url = 'assets/traffic_signs/parking_signs/E9i.png'
WHERE image_url = 'assets/traffic_signs/parking_signs/E9j.png';
