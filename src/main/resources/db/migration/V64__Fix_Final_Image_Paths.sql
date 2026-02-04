-- V64__Fix_Final_Image_Paths.sql
-- Final reload with all image paths corrected

DELETE FROM traffic_signs;
ALTER TABLE traffic_signs AUTO_INCREMENT = 1;
