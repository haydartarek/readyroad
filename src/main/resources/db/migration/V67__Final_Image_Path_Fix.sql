-- V67__Final_Image_Path_Fix.sql
-- Reload with correct /images/signs/ paths (no path conversion in DataInitializer)

DELETE FROM traffic_signs;
ALTER TABLE traffic_signs AUTO_INCREMENT = 1;
