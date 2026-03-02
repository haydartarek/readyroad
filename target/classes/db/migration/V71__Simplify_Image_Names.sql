-- V71__Simplify_Image_Names.sql
-- تحديث المسارات لاستخدام أسماء بسيطة بدون مسافات للصور المتغيرة (variants)
-- الصور الأساسية تبقى بأسمائها الأصلية

DELETE FROM traffic_signs;
ALTER TABLE traffic_signs AUTO_INCREMENT = 1;
