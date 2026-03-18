-- V160: Remove E9a and E9g signs (images deleted from project)

-- 1. Remove from sign_questions first (foreign key safety)
DELETE FROM sign_questions 
WHERE sign_id IN (
    SELECT id FROM traffic_signs WHERE sign_code IN ('E9a', 'E9g')
);

-- 2. Remove from road_signs
DELETE FROM road_signs 
WHERE sign_code IN ('E9a', 'E9g');

-- 3. Remove from traffic_signs
DELETE FROM traffic_signs 
WHERE sign_code IN ('E9a', 'E9g');
