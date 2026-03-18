-- V148: Add missing 'images/signs/' prefix to road_signs.image_path
-- 15 B-series rows were set by V147 (and pre-existing) using short 'priority_signs/...'
-- format. All other series consistently use 'images/signs/<category>/...' format.
-- This single UPDATE fixes all affected rows in one pass.

UPDATE road_signs
SET image_path = CONCAT('images/signs/', image_path)
WHERE image_path NOT LIKE 'images/signs/%';
