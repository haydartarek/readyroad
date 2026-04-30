-- V68__Fix_Missing_Image_Paths.sql
-- إضافة المسارات الصحيحة للصور المفقودة (B15 variants, C43 speeds, D1/D4 directions)

DELETE FROM traffic_signs;
ALTER TABLE traffic_signs AUTO_INCREMENT = 1;
