-- ============================================================
-- Migration: V137
-- Description: Normalize sign codes and stabilize traffic sign data model
--   1. Add normalized_sign_code to traffic_signs
--   2. Add normalized_sign_code to road_signs
--   3. Add image_path to traffic_signs (copy from image_url)
--   4. Populate normalized_sign_code for traffic_signs = sign_code
--   5. Populate normalized_sign_code for road_signs with canonical codes
--   6. Enforce NOT NULL + UNIQUE on road_signs.normalized_sign_code
--   7. Enforce NOT NULL on road_signs.image_path
--   8. Add CHECK constraint on road_signs.sign_code format
--   9. Add performance index on traffic_signs.normalized_sign_code
-- All ALTER steps are idempotent via INFORMATION_SCHEMA checks.
-- ============================================================

-- ── 1. Add normalized_sign_code to traffic_signs ─────────────────────────────
SET @col = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'traffic_signs'
  AND COLUMN_NAME = 'normalized_sign_code');
SET @sql = IF(@col = 0,
  'ALTER TABLE traffic_signs ADD COLUMN normalized_sign_code VARCHAR(100) NULL AFTER sign_code',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ── 2. Add normalized_sign_code to road_signs ────────────────────────────────
SET @col = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'road_signs'
  AND COLUMN_NAME = 'normalized_sign_code');
SET @sql = IF(@col = 0,
  'ALTER TABLE road_signs ADD COLUMN normalized_sign_code VARCHAR(100) NULL AFTER sign_code',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ── 3. Add image_path to traffic_signs (unify image column naming) ────────────
SET @col = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'traffic_signs'
  AND COLUMN_NAME = 'image_path');
SET @sql = IF(@col = 0,
  'ALTER TABLE traffic_signs ADD COLUMN image_path VARCHAR(500) NULL AFTER image_url',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- Copy image_url into image_path where image_path is still NULL
UPDATE traffic_signs
SET    image_path = image_url
WHERE  image_path IS NULL AND image_url IS NOT NULL;

-- ── 4. Populate traffic_signs.normalized_sign_code = sign_code ───────────────
UPDATE traffic_signs
SET    normalized_sign_code = sign_code
WHERE  normalized_sign_code IS NULL;

-- ── 5. Populate road_signs.normalized_sign_code ──────────────────────────────
-- Default pass: non-onderbord codes keep their sign_code as the canonical form
UPDATE road_signs
SET    normalized_sign_code = sign_code
WHERE  normalized_sign_code IS NULL;

-- M-series onderbord sub-signs
UPDATE road_signs SET normalized_sign_code = 'M1'             WHERE sign_code = 'onderbord_m1';
UPDATE road_signs SET normalized_sign_code = 'M2'             WHERE sign_code = 'onderbord_m2';
UPDATE road_signs SET normalized_sign_code = 'M3bis'          WHERE sign_code = 'onderbord_m3bis';
UPDATE road_signs SET normalized_sign_code = 'M4'             WHERE sign_code = 'onderbord_m4';
UPDATE road_signs SET normalized_sign_code = 'M5'             WHERE sign_code = 'onderbord_m5';
UPDATE road_signs SET normalized_sign_code = 'M7'             WHERE sign_code = 'onderbord_m7';
UPDATE road_signs SET normalized_sign_code = 'M9'             WHERE sign_code = 'onderbord_m9';
UPDATE road_signs SET normalized_sign_code = 'M10'            WHERE sign_code = 'onderbord_m10';
UPDATE road_signs SET normalized_sign_code = 'M12-30min'      WHERE sign_code = 'onderbord_m12_30min';
UPDATE road_signs SET normalized_sign_code = 'M12-fiets-brom' WHERE sign_code = 'onderbord_m12_fiets_brom';
UPDATE road_signs SET normalized_sign_code = 'M12-richtingen' WHERE sign_code = 'onderbord_m12_richtingen';
UPDATE road_signs SET normalized_sign_code = 'M13'            WHERE sign_code = 'onderbord_m13';
UPDATE road_signs SET normalized_sign_code = 'M15'            WHERE sign_code = 'onderbord_m15';
UPDATE road_signs SET normalized_sign_code = 'M17'            WHERE sign_code = 'onderbord_m17';

-- G-series onderbord sub-signs
UPDATE road_signs SET normalized_sign_code = 'GIa'               WHERE sign_code = 'onderbord_gia';
UPDATE road_signs SET normalized_sign_code = 'GIb'               WHERE sign_code = 'onderbord_gib';
UPDATE road_signs SET normalized_sign_code = 'GII'               WHERE sign_code = 'onderbord_gii';
UPDATE road_signs SET normalized_sign_code = 'GIII-aquaplaning'  WHERE sign_code = 'onderbord_giii_aquaplaning';
UPDATE road_signs SET normalized_sign_code = 'GIII-ijzel'        WHERE sign_code = 'onderbord_giii_ijzel';
UPDATE road_signs SET normalized_sign_code = 'GIII-industriezone' WHERE sign_code = 'onderbord_giii_industriezone';
UPDATE road_signs SET normalized_sign_code = 'GIII-uitrit'       WHERE sign_code = 'onderbord_giii_uitrit';
UPDATE road_signs SET normalized_sign_code = 'GIV'               WHERE sign_code = 'onderbord_giv';
UPDATE road_signs SET normalized_sign_code = 'GIX'               WHERE sign_code = 'onderbord_gix';
UPDATE road_signs SET normalized_sign_code = 'GV'                WHERE sign_code = 'onderbord_gv';
UPDATE road_signs SET normalized_sign_code = 'GVIIa'             WHERE sign_code = 'onderbord_gviia';
UPDATE road_signs SET normalized_sign_code = 'GVIIb'             WHERE sign_code = 'onderbord_gviib';
UPDATE road_signs SET normalized_sign_code = 'GVIId'             WHERE sign_code = 'onderbord_gviid';
UPDATE road_signs SET normalized_sign_code = 'GVIId-CARPOOL'     WHERE sign_code = 'onderbord_gviid_carpool';
UPDATE road_signs SET normalized_sign_code = 'GVIId-PR'          WHERE sign_code = 'onderbord_gviid_pr';
UPDATE road_signs SET normalized_sign_code = 'GVIII'             WHERE sign_code = 'onderbord_gviii';
UPDATE road_signs SET normalized_sign_code = 'GXa'               WHERE sign_code = 'onderbord_gxa';
UPDATE road_signs SET normalized_sign_code = 'GXb'               WHERE sign_code = 'onderbord_gxb';
UPDATE road_signs SET normalized_sign_code = 'GXc'               WHERE sign_code = 'onderbord_gxc';
UPDATE road_signs SET normalized_sign_code = 'GXd'               WHERE sign_code = 'onderbord_gxd';
UPDATE road_signs SET normalized_sign_code = 'GXI'               WHERE sign_code = 'onderbord_gxi';

-- ── 6. Enforce NOT NULL on road_signs.normalized_sign_code ───────────────────
-- Safety fallback: any remaining NULL row falls back to sign_code
UPDATE road_signs
SET    normalized_sign_code = sign_code
WHERE  normalized_sign_code IS NULL OR normalized_sign_code = '';

ALTER TABLE road_signs
    MODIFY normalized_sign_code VARCHAR(100) NOT NULL;

-- ── 7. Add UNIQUE constraint on road_signs.normalized_sign_code ──────────────
SET @idx = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'road_signs'
  AND INDEX_NAME = 'uq_road_sign_normalized_code');
SET @sql = IF(@idx = 0,
  'ALTER TABLE road_signs ADD CONSTRAINT uq_road_sign_normalized_code UNIQUE (normalized_sign_code)',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ── 8. Enforce NOT NULL on road_signs.image_path ─────────────────────────────
-- Replace NULL with empty string to satisfy NOT NULL without losing data
UPDATE road_signs
SET    image_path = ''
WHERE  image_path IS NULL;

ALTER TABLE road_signs
    MODIFY image_path VARCHAR(500) NOT NULL DEFAULT '';

-- ── 9. Add CHECK constraint for sign_code format on road_signs ───────────────
SET @chk = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'road_signs'
  AND CONSTRAINT_NAME = 'chk_sign_code_format');
SET @sql = IF(@chk = 0,
  "ALTER TABLE road_signs ADD CONSTRAINT chk_sign_code_format CHECK (sign_code REGEXP '^[A-Za-z0-9_-]+$')",
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ── 10. Performance index on traffic_signs.normalized_sign_code ──────────────
SET @idx = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'traffic_signs'
  AND INDEX_NAME = 'idx_ts_normalized_sign_code');
SET @sql = IF(@idx = 0,
  'CREATE INDEX idx_ts_normalized_sign_code ON traffic_signs (normalized_sign_code)',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ── 11. Performance index on road_signs for normalized lookup ─────────────────
SET @idx = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'road_signs'
  AND INDEX_NAME = 'idx_rs_normalized_sign_code');
SET @sql = IF(@idx = 0,
  'CREATE INDEX idx_rs_normalized_sign_code ON road_signs (normalized_sign_code)',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;
