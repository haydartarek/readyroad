-- V52__Fix_Database_Charset.sql
-- Fix database and table character sets to UTF-8

-- Fix traffic_signs table charset
ALTER TABLE traffic_signs CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Fix categories table charset
ALTER TABLE categories CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Fix lessons table charset
ALTER TABLE lessons CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Clear and reload traffic signs with proper encoding
DELETE FROM traffic_signs;
ALTER TABLE traffic_signs AUTO_INCREMENT = 1;
