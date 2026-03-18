-- V140__Fix_Category_T_For_Delineation_Signs.sql
-- Fix category taxonomy for the legacy TrafficSign / categories table:
--   1. M was wrongly seeded as "Delineation Signs" — rename to "Cyclist & Moped Advisory Signs"
--   2. Add missing T category = "Delineation Signs / Afbakeningsborden"
--   3. Re-assign existing TYPE-* and MARK-* traffic_signs rows from M → T
--   4. Re-assign existing M-series traffic_signs rows from G → M

-- ── 1. Fix M category name ────────────────────────────────────────────────────
UPDATE categories
SET    name_en      = 'Cyclist & Moped Advisory Signs',
       name_nl      = 'Fietsersborden',
       name_fr      = 'Panneaux cyclistes et vélomoteurs',
       name_ar      = 'علامات الدراجات والدراجات النارية',
       description_en = 'Advisory and regulatory signs for cyclists and moped riders',
       updated_at   = NOW()
WHERE  code = 'M';

-- ── 2. Insert T category (Delineation / Afbakeningsborden) ───────────────────
INSERT INTO categories (code, name_en, name_nl, name_fr, name_ar, description_en,
                        display_order, is_active, created_at, updated_at)
VALUES ('T',
        'Delineation Signs',
        'Afbakeningsborden',
        'Panneaux de balisage',
        'علامات التحديد',
        'Road delineation and guidance markers (TYPE-I, TYPE-II, TYPE-V, MARK)',
        10, TRUE, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- ── 3. Move TYPE-* and MARK-* traffic_signs from M → T ───────────────────────
UPDATE traffic_signs ts
JOIN   categories cm ON cm.code = 'M'
JOIN   categories ct ON ct.code = 'T'
SET    ts.category_id = ct.id
WHERE  ts.category_id = cm.id
  AND  (ts.sign_code LIKE 'TYPE-%' OR ts.sign_code LIKE 'MARK-%');

-- ── 4. Move M-series traffic_signs from G → M ─────────────────────────────────
UPDATE traffic_signs ts
JOIN   categories cg ON cg.code = 'G'
JOIN   categories cm ON cm.code = 'M'
SET    ts.category_id = cm.id
WHERE  ts.category_id = cg.id
  AND  ts.sign_code   REGEXP '^M[0-9]';
