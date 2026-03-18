-- ============================================================
-- V132: Fix Z-Series (Zoneborden) — Begin/Einde image swaps
-- ============================================================
-- Root cause: V103 accidentally set each begin sign to the einde
-- disk image and vice versa for ZC5, ZC21T, ZC35, ZC35T pairs.
-- V118 preserved those wrong URLs (it only changed the path prefix
-- from assets/signs/zoneborden/ → images/signs/zone_signs/).
-- Additional bug: ZC21-zone (einde) was mapped to the begin image.
-- Additional bug: ZE9a-v3 (einde) pointed to a begin _0 variant.
-- ============================================================

-- =====================
-- 1. ZC5  →  Zone begin  (was wrongly showing Einde image)
-- =====================
UPDATE traffic_signs
SET image_url  = 'images/signs/zone_signs/ZC5 Zone verboden toegang voor motorvoertuigen met meer dan 2 wielen.png',
    updated_at = NOW()
WHERE sign_code = 'ZC5';

-- =====================
-- 2. ZC5-v1  →  Einde zone  (was wrongly showing begin image)
-- =====================
UPDATE traffic_signs
SET image_url  = 'images/signs/zone_signs/ZC5 Einde zone verboden toegang voor motorvoertuigen met meer dan 2 wielen.png',
    updated_at = NOW()
WHERE sign_code = 'ZC5-v1';

-- =====================
-- 3. ZC21  →  Zone begin  (was pointing to the dash-variant filename)
--    Clean up: use the clean filename without the extra " - "
-- =====================
UPDATE traffic_signs
SET image_url  = 'images/signs/zone_signs/ZC21 Zone verboden toegang voor bestuurders van voertuigen waarvan de massa hoger is dan de aangeduide massa.png',
    updated_at = NOW()
WHERE sign_code = 'ZC21';

-- =====================
-- 4. ZC21T  →  Zone begin  (was wrongly showing Einde image)
-- =====================
UPDATE traffic_signs
SET image_url  = 'images/signs/zone_signs/ZC21T Zone verboden toegang voor bestuurders van voertuigen waarvan de massa hoger is dan de aangeduide massa.png',
    updated_at = NOW()
WHERE sign_code = 'ZC21T';

-- =====================
-- 5. ZC21T-v2  →  Einde zone  (was wrongly showing begin image)
-- =====================
UPDATE traffic_signs
SET image_url  = 'images/signs/zone_signs/ZC21T Einde zone verboden toegang voor bestuurders van voertuigen waarvan de massa hoger is dan de aangeduide massa.png',
    updated_at = NOW()
WHERE sign_code = 'ZC21T-v2';

-- =====================
-- 6. ZC21-zone  →  Einde zone  (was pointing to ZC21 begin image with dash)
-- =====================
UPDATE traffic_signs
SET image_url  = 'images/signs/zone_signs/ZC21 Einde zone verboden toegang voor bestuurders van voertuigen waarvan de massa hoger is dan de aangeduide massa.png',
    updated_at = NOW()
WHERE sign_code = 'ZC21-zone';

-- =====================
-- 7. ZC35  →  Zone begin  (was wrongly showing Einde image)
-- =====================
UPDATE traffic_signs
SET image_url  = 'images/signs/zone_signs/ZC35 Zone verboden inhalen.png',
    updated_at = NOW()
WHERE sign_code = 'ZC35';

-- =====================
-- 8. ZC35-v1  →  Einde zone  (was wrongly showing begin image)
-- =====================
UPDATE traffic_signs
SET image_url  = 'images/signs/zone_signs/ZC35 Einde zone verboden inhalen.png',
    updated_at = NOW()
WHERE sign_code = 'ZC35-v1';

-- =====================
-- 9. ZC35T  →  Zone begin  (was wrongly showing Einde image)
-- =====================
UPDATE traffic_signs
SET image_url  = 'images/signs/zone_signs/ZC35T Zone verboden inhalen.png',
    updated_at = NOW()
WHERE sign_code = 'ZC35T';

-- =====================
-- 10. ZE9a-v3  →  Einde zone uitsluitend voor auto's
--     Was pointing to _0 variant of begin sign.
--     Use the ZE9aT Einde image (closest available einde image).
-- =====================
UPDATE traffic_signs
SET image_url  = 'images/signs/zone_signs/ZE9aT Einde zone parkeren uitsluitend voor auto''s.png',
    updated_at = NOW()
WHERE sign_code = 'ZE9a-v3';
