-- V66__Fix_Image_Paths_With_Full_Names.sql
-- Update image paths to use full Dutch filenames from D:\driving_school_app\assets\signs\

DELETE FROM traffic_signs;
ALTER TABLE traffic_signs AUTO_INCREMENT = 1;
