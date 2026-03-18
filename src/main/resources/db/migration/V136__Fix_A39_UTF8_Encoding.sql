-- =============================================================================
-- V136: Fix A39 image_url double-encoded UTF-8 (Ã© instead of é)
-- =============================================================================
-- Root cause: V134 applied the A39 image_url string when the JDBC connection
-- did not have characterEncoding=UTF-8, causing the é (U+00E9, UTF-8: C3A9)
-- bytes to be stored as two Latin-1 characters Ã (C383) and © (C2A9).
--
-- Fix: Use UNHEX to write the exact UTF-8 bytes C3A9 for é, bypassing any
-- charset conversion done by the client connection.
--
-- Correct filename on disk:
--   A39 Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer.png
-- =============================================================================

-- Full hex of the correct image_url string (UTF-8 encoded):
-- images/signs/danger_signs/A39 Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer.png
-- where é = C3A9

UPDATE traffic_signs
SET    image_url  = CONVERT(UNHEX('696D616765732F7369676E732F64616E6765725F7369676E732F4133392054776565207269636874696E67737665726B65657220746F6567656C6174656E206E612065656E207374756B20C3A9C3A96E7269636874696E67737665726B6565722E706E67') USING utf8mb4),
       updated_at = NOW()
WHERE  sign_code  = 'A39';

UPDATE road_signs
SET    image_path = CONVERT(UNHEX('696D616765732F7369676E732F64616E6765725F7369676E732F4133392054776565207269636874696E67737665726B65657220746F6567656C6174656E206E612065656E207374756B20C3A9C3A96E7269636874696E67737665726B6565722E706E67') USING utf8mb4),
       updated_at = NOW()
WHERE  sign_code  = 'A39';
