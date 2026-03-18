-- V154__Fix_E9j_Image_Path_Apostrophe.sql
-- No-op: V153 already inserted E9j with the correct straight-apostrophe filename
-- (SQL-escaped as auto''s) in both traffic_signs and road_signs.
-- The curly-apostrophe correction originally attempted here would break the path;
-- the file on disk uses a plain straight apostrophe (U+0027).
-- This migration is intentionally kept as a no-op to preserve the Flyway version chain.
SELECT 1;
