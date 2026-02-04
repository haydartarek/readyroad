-- V55__Clear_Traffic_Signs_For_Encoding_Fix.sql
-- Clear traffic signs to reload with fixed Arabic encoding

DELETE FROM traffic_signs;
ALTER TABLE traffic_signs AUTO_INCREMENT = 1;
