-- V60__Reload_With_Manual_Fix.sql
-- Clear and reload with manually corrected A1a and A1b

DELETE FROM traffic_signs;
ALTER TABLE traffic_signs AUTO_INCREMENT = 1;
