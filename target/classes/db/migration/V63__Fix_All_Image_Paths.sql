-- V63__Fix_All_Image_Paths.sql
-- Clear and reload with all image paths fixed

DELETE FROM traffic_signs;
ALTER TABLE traffic_signs AUTO_INCREMENT = 1;
