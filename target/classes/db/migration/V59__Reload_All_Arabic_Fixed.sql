-- V59__Reload_All_Arabic_Fixed.sql
-- Clear and reload all traffic signs with properly encoded Arabic text

DELETE FROM traffic_signs;
ALTER TABLE traffic_signs AUTO_INCREMENT = 1;
