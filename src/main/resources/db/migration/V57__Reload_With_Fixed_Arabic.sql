-- V57__Reload_With_Fixed_Arabic.sql
-- Clear traffic signs to reload with properly encoded Arabic text

DELETE FROM traffic_signs;
ALTER TABLE traffic_signs AUTO_INCREMENT = 1;
