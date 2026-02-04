-- V51__Clear_Traffic_Signs_For_Reload.sql
-- Clear traffic signs to reload with proper UTF-8 encoding

DELETE FROM traffic_signs;

-- Reset auto increment if needed
ALTER TABLE traffic_signs AUTO_INCREMENT = 1;
