-- ============================================================
-- V139: Add CYCLIST and DELINEATION to road_signs.category ENUM
--       and reclassify M-series and TYPE-* road signs.
--
-- Canonical Belgian taxonomy:
--   M-series (M1–M20) = Fiets/Bromfiets onderborden → CYCLIST
--   TYPE-*             = Afbakeningsborden           → DELINEATION
--
-- Previously, both were incorrectly categorised as ADDITIONAL
-- (the signs_import JSON files used ADDITIONAL for all G/M/TYPE-* signs).
-- ============================================================

-- ─── Step 1: Extend the ENUM to include CYCLIST and DELINEATION ──────────────
-- MySQL ENUM columns must be explicitly ALTERed to add new values.
-- This is safe and non-destructive; existing rows are unaffected.
ALTER TABLE road_signs
    MODIFY COLUMN category ENUM(
        'DANGER',
        'PRIORITY',
        'PROHIBITION',
        'MANDATORY',
        'PARKING',
        'INFORMATION',
        'ADDITIONAL',
        'CYCLIST',
        'DELINEATION',
        'ZONE'
    ) NOT NULL;

-- ─── Step 2: Reclassify M-series signs as CYCLIST ────────────────────────────
-- Matches: M1, M2, M3bis, M4, M5, M7, M9, M10, M12-30min,
--          M12-fiets-brom, M12-richtingen, M13, M15, M17, etc.
-- REGEXP '^M[0-9]' captures all codes starting with M + digit.
UPDATE road_signs
SET    category   = 'CYCLIST',
       updated_at = NOW()
WHERE  category   = 'ADDITIONAL'
  AND  sign_code  REGEXP '^M[0-9]';

-- ─── Step 3: Reclassify TYPE-* signs as DELINEATION ─────────────────────────
-- Matches: TYPE-IA1, TYPE-IA2, TYPE-IB1, TYPE-IB2, TYPE-IC,
--          TYPE-IIA, TYPE-IIB, TYPE-V, TYPE-VA, etc.
UPDATE road_signs
SET    category   = 'DELINEATION',
       updated_at = NOW()
WHERE  category   = 'ADDITIONAL'
  AND  sign_code  LIKE 'TYPE-%';
