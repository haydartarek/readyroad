-- V56__Force_Clear_Traffic_Signs.sql
-- Force clear traffic signs to reload with fixed UTF-8 Arabic encoding

DELETE FROM traffic_signs;
ALTER TABLE traffic_signs AUTO_INCREMENT = 1;
