-- V50__Fix_Traffic_Sign_Image_Paths.sql
-- Fix all traffic sign image URLs to use English folder names and only sign codes
-- This migration corrects paths from Dutch folder names with full names to English folder names with codes only

-- The correct format is: assets/traffic_signs/{english_folder_name}/{sign_code}.png
-- Example: assets/traffic_signs/priority_signs/B1.png (NOT voorrangsborden/B1 Voorrang verlenen.png)

-- Update danger_signs (Category A) - was gevaarsborden
UPDATE traffic_signs
SET image_url = CONCAT('assets/traffic_signs/danger_signs/', sign_code, '.png')
WHERE category_id = (SELECT id FROM categories WHERE code = 'A')
  AND (image_url IS NULL
       OR image_url = ''
       OR image_url LIKE '%gevaarsborden%'
       OR image_url NOT LIKE 'assets/traffic_signs/danger_signs/%');

-- Update priority_signs (Category B) - was voorrangsborden
UPDATE traffic_signs
SET image_url = CONCAT('assets/traffic_signs/priority_signs/', sign_code, '.png')
WHERE category_id = (SELECT id FROM categories WHERE code = 'B')
  AND (image_url IS NULL
       OR image_url = ''
       OR image_url LIKE '%voorrangsborden%'
       OR image_url NOT LIKE 'assets/traffic_signs/priority_signs/%');

-- Update prohibition_signs (Category C) - was verbodsborden
UPDATE traffic_signs
SET image_url = CONCAT('assets/traffic_signs/prohibition_signs/', sign_code, '.png')
WHERE category_id = (SELECT id FROM categories WHERE code = 'C')
  AND (image_url IS NULL
       OR image_url = ''
       OR image_url LIKE '%verbodsborden%'
       OR image_url NOT LIKE 'assets/traffic_signs/prohibition_signs/%');

-- Update mandatory_signs (Category D) - was gebodsborden
UPDATE traffic_signs
SET image_url = CONCAT('assets/traffic_signs/mandatory_signs/', sign_code, '.png')
WHERE category_id = (SELECT id FROM categories WHERE code = 'D')
  AND (image_url IS NULL
       OR image_url = ''
       OR image_url LIKE '%gebodsborden%'
       OR image_url NOT LIKE 'assets/traffic_signs/mandatory_signs/%');

-- Update parking_signs (Category E) - was parkeren
UPDATE traffic_signs
SET image_url = CONCAT('assets/traffic_signs/parking_signs/', sign_code, '.png')
WHERE category_id = (SELECT id FROM categories WHERE code = 'E')
  AND (image_url IS NULL
       OR image_url = ''
       OR image_url LIKE '%parkeren%'
       OR image_url NOT LIKE 'assets/traffic_signs/parking_signs/%');

-- Update information_signs (Category F) - was aanwijzingsborden
UPDATE traffic_signs
SET image_url = CONCAT('assets/traffic_signs/information_signs/', sign_code, '.png')
WHERE category_id = (SELECT id FROM categories WHERE code = 'F')
  AND (image_url IS NULL
       OR image_url = ''
       OR image_url LIKE '%aanwijzingsborden%'
       OR image_url LIKE '%Informatieborden%'
       OR image_url NOT LIKE 'assets/traffic_signs/information_signs/%');

-- Update additional_signs (Category G) - was onderborden
UPDATE traffic_signs
SET image_url = CONCAT('assets/traffic_signs/additional_signs/', sign_code, '.png')
WHERE category_id = (SELECT id FROM categories WHERE code = 'G')
  AND (image_url IS NULL
       OR image_url = ''
       OR image_url LIKE '%onderborden%'
       OR image_url NOT LIKE 'assets/traffic_signs/additional_signs/%');

-- Update zone_signs (Category Z) - was zoneborden
UPDATE traffic_signs
SET image_url = CONCAT('assets/traffic_signs/zone_signs/', sign_code, '.png')
WHERE category_id = (SELECT id FROM categories WHERE code = 'Z')
  AND (image_url IS NULL
       OR image_url = ''
       OR image_url LIKE '%zoneborden%'
       OR image_url NOT LIKE 'assets/traffic_signs/zone_signs/%');

-- Update delineation_signs (Category M) - was afbakeningsborden
UPDATE traffic_signs
SET image_url = CONCAT('assets/traffic_signs/delineation_signs/', sign_code, '.png')
WHERE category_id = (SELECT id FROM categories WHERE code = 'M')
  AND (image_url IS NULL
       OR image_url = ''
       OR image_url LIKE '%afbakeningsborden%'
       OR image_url NOT LIKE 'assets/traffic_signs/delineation_signs/%');

-- Update bicycle_signs if exists
UPDATE traffic_signs
SET image_url = CONCAT('assets/traffic_signs/bicycle_signs/', sign_code, '.png')
WHERE sign_code LIKE 'FI%'
  AND (image_url IS NULL
       OR image_url = ''
       OR image_url NOT LIKE 'assets/traffic_signs/bicycle_signs/%');
