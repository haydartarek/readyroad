-- V65__Replace_All_Images.sql
-- Clear and reload with all images from D:\ source

DELETE FROM traffic_signs;
ALTER TABLE traffic_signs AUTO_INCREMENT = 1;
