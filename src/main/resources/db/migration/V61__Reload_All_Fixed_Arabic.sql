-- V61__Reload_All_Fixed_Arabic.sql
-- Clear and reload all traffic signs with corrected Arabic text

DELETE FROM traffic_signs;
ALTER TABLE traffic_signs AUTO_INCREMENT = 1;
