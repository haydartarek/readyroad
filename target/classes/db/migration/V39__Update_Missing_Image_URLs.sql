-- V7__Update_Missing_Image_URLs.sql
-- Update image_url for traffic signs that have NULL or empty image_url
-- Based on sign_code and category mapping

-- Update danger_signs (Category A)
UPDATE traffic_signs
SET image_url = CONCAT('assets/traffic_signs/danger_signs/', sign_code, '.png')
WHERE (image_url IS NULL OR image_url = '')
  AND category_id = (SELECT id FROM categories WHERE code = 'A');

-- Update priority_signs (Category B)
UPDATE traffic_signs
SET image_url = CONCAT('assets/traffic_signs/priority_signs/', sign_code, '.png')
WHERE (image_url IS NULL OR image_url = '')
  AND category_id = (SELECT id FROM categories WHERE code = 'B');

-- Update prohibition_signs (Category C)
UPDATE traffic_signs
SET image_url = CONCAT('assets/traffic_signs/prohibition_signs/', sign_code, '.png')
WHERE (image_url IS NULL OR image_url = '')
  AND category_id = (SELECT id FROM categories WHERE code = 'C');

-- Update mandatory_signs (Category D)
UPDATE traffic_signs
SET image_url = CONCAT('assets/traffic_signs/mandatory_signs/', sign_code, '.png')
WHERE (image_url IS NULL OR image_url = '')
  AND category_id = (SELECT id FROM categories WHERE code = 'D');

-- Update parking_signs (Category E)
UPDATE traffic_signs
SET image_url = CONCAT('assets/traffic_signs/parking_signs/', sign_code, '.png')
WHERE (image_url IS NULL OR image_url = '')
  AND category_id = (SELECT id FROM categories WHERE code = 'E');

-- Update information_signs (Category F)
UPDATE traffic_signs
SET image_url = CONCAT('assets/traffic_signs/information_signs/', sign_code, '.png')
WHERE (image_url IS NULL OR image_url = '')
  AND category_id = (SELECT id FROM categories WHERE code = 'F');

-- Update additional_signs (Category G)
UPDATE traffic_signs
SET image_url = CONCAT('assets/traffic_signs/additional_signs/', sign_code, '.png')
WHERE (image_url IS NULL OR image_url = '')
  AND category_id = (SELECT id FROM categories WHERE code = 'G');

-- Update zone_signs (Category Z - if exists)
UPDATE traffic_signs
SET image_url = CONCAT('assets/traffic_signs/zone_signs/', sign_code, '.png')
WHERE (image_url IS NULL OR image_url = '')
  AND category_id = (SELECT id FROM categories WHERE code = 'Z');

-- Update bicycle_signs (Category M)
UPDATE traffic_signs
SET image_url = CONCAT('assets/traffic_signs/additional_signs/', sign_code, '.png')
WHERE (image_url IS NULL OR image_url = '')
  AND category_id = (SELECT id FROM categories WHERE code = 'M');
