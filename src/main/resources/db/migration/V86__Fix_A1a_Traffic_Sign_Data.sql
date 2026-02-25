-- ============================================================================
-- V86: Permanent normalization of traffic sign A1a
-- ============================================================================
-- Root cause: V60/V61 migrations deleted all traffic signs and reset
-- AUTO_INCREMENT. External data reload inserted WRONG content for A1a:
--   WRONG:   "Speed bump / مطب / Verkeersdrempel / Ralentisseur"
--   CORRECT: "Dangerous bend to the left / منعطف خطر لليسار / Gevaarlijke bocht naar links / Virage dangereux à gauche"
-- Canonical source: V6__Add_All_Traffic_Signs.sql
-- ============================================================================

UPDATE traffic_signs
SET
  name_ar        = 'منعطف خطر لليسار',
  name_en        = 'Dangerous bend to the left',
  name_nl        = 'Gevaarlijke bocht naar links.',
  name_fr        = 'Virage dangereux à gauche',
  description_ar = 'منعطف خطر لليسار',
  description_en = 'Dangerous bend to the left',
  description_nl = 'Gevaarlijke bocht naar links.',
  description_fr = 'Virage dangereux à gauche',
  is_active      = TRUE,
  updated_at     = NOW()
WHERE sign_code = 'A1a';
