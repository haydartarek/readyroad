-- V69__Add_All_Missing_Images.sql
-- إضافة 30 صورة مفقودة (Zone signs, Additional signs, Delineation markers)

DELETE FROM traffic_signs;
ALTER TABLE traffic_signs AUTO_INCREMENT = 1;
