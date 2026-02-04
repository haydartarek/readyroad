-- V58__Reload_With_Correct_Arabic.sql
-- Clear traffic signs to reload with manually fixed Arabic encoding

DELETE FROM traffic_signs;
ALTER TABLE traffic_signs AUTO_INCREMENT = 1;
