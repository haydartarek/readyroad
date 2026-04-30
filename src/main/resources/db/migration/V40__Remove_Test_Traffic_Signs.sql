-- V40__Remove_Test_Traffic_Signs.sql
-- Remove test traffic signs that don't have corresponding image files
-- These were created in V2__Seed_Initial_Data.sql as test data
-- The real signs were added in V6__Add_All_Traffic_Signs.sql

-- Delete test signs that don't have actual image files
-- B1.png, C1.png, E1.png, M1.png, M2.png exist, so we keep those
-- But they might be duplicates - remove if real signs exist

-- First, remove A signs that are not present in the current signs_import disk allowlist.
DELETE FROM traffic_signs
WHERE sign_code LIKE 'A%'
  AND sign_code NOT IN (
    'A1a','A1b','A1c','A1d','A3','A5','A7a','A7b','A7c','A9','A11','A13',
    'A14','A15','A17','A19','A21','A23','A25','A27','A29','A31','A33','A35',
    'A37','A39','A41','A43','A45','A47','A49','A50','A51','A53'
  );

-- Remove signs without real images
DELETE FROM traffic_signs WHERE sign_code IN ('D1', 'D2', 'F1', 'F2', 'G1', 'G2', 'Z1', 'Z2');

-- For B1, C1, E1, M1, M2 - keep only one entry per sign_code (the one with proper image_url if exists)
-- Remove duplicates keeping the one with non-null image_url or lowest id

-- Remove duplicate B1 (keep one with image or lowest id)
DELETE t1 FROM traffic_signs t1
INNER JOIN traffic_signs t2
WHERE t1.id > t2.id AND t1.sign_code = t2.sign_code AND t1.sign_code IN ('B1', 'C1', 'C2', 'E1', 'E2', 'M1', 'M2');
