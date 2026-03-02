-- V62__Fix_Image_Paths.sql
-- Clear and reload with corrected image paths

DELETE FROM traffic_signs;
ALTER TABLE traffic_signs AUTO_INCREMENT = 1;
