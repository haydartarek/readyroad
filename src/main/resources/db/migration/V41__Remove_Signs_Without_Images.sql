-- V41__Remove_Signs_Without_Images.sql
-- Remove traffic signs that don't have corresponding image files

-- These sign codes exist in DB but have no actual image files:
DELETE FROM traffic_signs WHERE sign_code = 'C2';
DELETE FROM traffic_signs WHERE sign_code = 'E2';
