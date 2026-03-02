-- ============================================================================
-- V88: Seed A1a canonical long descriptions from signs.json
-- ============================================================================
-- Canonical source: readyroad/data/signs.json (A1a entry)
-- These are the extended educational descriptions for the sign detail page.
-- ============================================================================

UPDATE traffic_signs
SET
  long_description_en = 'Warning for a dangerous curve to the left at approximately 150 meters. Reduce your speed and pay attention.',
  long_description_nl = 'Waarschuwing voor een gevaarlijke bocht naar links op ongeveer 150 meter afstand. Verminder uw snelheid en let goed op.',
  long_description_fr = 'Avertissement d''un virage dangereux à gauche à environ 150 mètres. Réduisez votre vitesse et soyez attentif.',
  long_description_ar = 'تحذير من منعطف خطير إلى اليسار على مسافة 150 متر تقريباً. خفف من سرعتك وانتبه.',
  updated_at = NOW()
WHERE sign_code = 'A1a';
