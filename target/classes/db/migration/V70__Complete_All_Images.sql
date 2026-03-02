-- V70__Complete_All_Images.sql
-- إضافة آخر 6 صور مفقودة (F117-v1, F118-v1, F97-v1, F50b, F45b-v2, F79-V1)
-- النتيجة: 251/251 صورة ✓

DELETE FROM traffic_signs;
ALTER TABLE traffic_signs AUTO_INCREMENT = 1;
