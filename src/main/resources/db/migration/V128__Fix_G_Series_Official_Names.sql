-- ============================================================
-- V128: Fix G-series sign names to match official Belgian PDF
-- ============================================================
-- V104 inserted some G-series signs with wrong/over-specific names.
-- This migration corrects them to official names per Belgian law.
-- ============================================================

-- GVIIa: was "Aanvulling parkeren elektrische voertuigen" (too specific)
-- Official name per Belgian traffic sign legislation
UPDATE traffic_signs SET
  name_nl    = 'Aanvulling van de verkeersborden voor parkeren',
  name_en    = 'Supplement to traffic signs for parking',
  name_fr    = 'Complément des panneaux de stationnement',
  name_ar    = 'ملحق علامات الوقوف',
  updated_at = NOW()
WHERE sign_code = 'GVIIa';

-- GVIIb: was "Zone met parkeerschijf" (too specific)
UPDATE traffic_signs SET
  name_nl    = 'Aanvulling van de verkeersborden voor parkeren',
  name_en    = 'Supplement to traffic signs for parking',
  name_fr    = 'Complément des panneaux de stationnement',
  name_ar    = 'ملحق علامات الوقوف',
  updated_at = NOW()
WHERE sign_code = 'GVIIb';

-- GVIId (base): was "Parkeerplaats voor personen met een handicap" (wrong)
-- The base GVIId is a generic parking supplement; specific variants are GVIId-PR and GVIId-CARPOOL.
UPDATE traffic_signs SET
  name_nl    = 'Aanvulling van de verkeersborden voor parkeren',
  name_en    = 'Supplement to traffic signs for parking',
  name_fr    = 'Complément des panneaux de stationnement',
  name_ar    = 'ملحق علامات الوقوف',
  updated_at = NOW()
WHERE sign_code = 'GVIId';

-- GXI: was "Afrit rechts" — official name is simply "Afrit"
UPDATE traffic_signs SET
  name_nl    = 'Afrit',
  name_en    = 'Exit',
  name_fr    = 'Sortie',
  name_ar    = 'مخرج',
  updated_at = NOW()
WHERE sign_code = 'GXI';
