-- V9__Fix_Traffic_Sign_Translations.sql
-- Ø¥ØµÙ„Ø§Ø­ Ø§Ù„ØªØ±Ø¬Ù…Ø§Øª Ø§Ù„Ø®Ø§Ø·Ø¦Ø© Ù„Ù„Ø¥Ø´Ø§Ø±Ø§Øª Ø§Ù„Ù…Ø±ÙˆØ±ÙŠØ©
-- Fix incorrect translations for traffic signs
-- Updated: 2026-02-27 - Added real translations, name_nl, updated_at, B11/F7 fixes

USE readyroad_prod;

-- ========================================
-- Category A: Warning Signs
-- ========================================

-- A14: Verhoogde inrichting
UPDATE traffic_signs SET
  name_ar = 'Ù…Ù†Ø´Ø£Ø© Ù…Ø±ØªÙØ¹Ø©',
  name_en = 'Raised facility',
  name_nl = 'Verhoogde inrichting',
  name_fr = 'Installation surÃ©levÃ©e',
  updated_at = NOW()
WHERE sign_code = 'A14';

-- A15: Gladde weg
UPDATE traffic_signs SET
  name_ar = 'Ø·Ø±ÙŠÙ‚ Ø²Ù„Ù‚',
  name_en = 'Slippery road',
  name_nl = 'Gladde weg',
  name_fr = 'ChaussÃ©e glissante',
  updated_at = NOW()
WHERE sign_code = 'A15';

-- A27: Wild gedierte
UPDATE traffic_signs SET
  name_ar = 'Ø­ÙŠÙˆØ§Ù†Ø§Øª Ø¨Ø±ÙŠØ© ØªØ¹Ø¨Ø± Ø§Ù„Ø·Ø±ÙŠÙ‚',
  name_en = 'Wild animals crossing',
  name_nl = 'Overstekend wild gedierte',
  name_fr = 'Animaux sauvages',
  updated_at = NOW()
WHERE sign_code = 'A27';

-- A35: Laagvliegende luchtvaartuigen
UPDATE traffic_signs SET
  name_ar = 'Ø·Ø§Ø¦Ø±Ø§Øª Ù…Ù†Ø®ÙØ¶Ø© Ø§Ù„Ø§Ø±ØªÙØ§Ø¹',
  name_en = 'Low-flying aircraft',
  name_nl = 'Laagvliegende luchtvaartuigen',
  name_fr = 'AÃ©ronefs Ã  basse altitude',
  updated_at = NOW()
WHERE sign_code = 'A35';

-- A39: Tweerichtingsverkeer
UPDATE traffic_signs SET
  name_ar = 'Ù…Ø±ÙˆØ± Ø°Ùˆ Ø§ØªØ¬Ø§Ù‡ÙŠÙ†',
  name_en = 'Two-way traffic',
  name_nl = 'Tweerichtingsverkeer',
  name_fr = 'Circulation Ã  double sens',
  updated_at = NOW()
WHERE sign_code = 'A39';

-- A41: Overweg met slagbomen
UPDATE traffic_signs SET
  name_ar = 'Ù…Ø¹Ø¨Ø± Ø³ÙƒØ© Ø­Ø¯ÙŠØ¯ Ø¨Ø­ÙˆØ§Ø¬Ø²',
  name_en = 'Level crossing with barriers',
  name_nl = 'Overweg met slagbomen',
  name_fr = 'Passage Ã  niveau avec barriÃ¨res',
  updated_at = NOW()
WHERE sign_code = 'A41';

-- A43: Overweg zonder slagbomen
UPDATE traffic_signs SET
  name_ar = 'Ù…Ø¹Ø¨Ø± Ø³ÙƒØ© Ø­Ø¯ÙŠØ¯ Ø¨Ø¯ÙˆÙ† Ø­ÙˆØ§Ø¬Ø²',
  name_en = 'Level crossing without barriers',
  name_nl = 'Overweg zonder slagbomen',
  name_fr = 'Passage Ã  niveau sans barriÃ¨res',
  updated_at = NOW()
WHERE sign_code = 'A43';

-- A49: Tramsporen in de weg
UPDATE traffic_signs SET
  name_ar = 'Ù…Ø³Ø§Ø±Ø§Øª Ø§Ù„ØªØ±Ø§Ù… ÙÙŠ Ø§Ù„Ø·Ø±ÙŠÙ‚',
  name_en = 'Tram tracks in road',
  name_nl = 'Tramsporen in de weg',
  name_fr = 'Voies de tram dans la chaussÃ©e',
  updated_at = NOW()
WHERE sign_code = 'A49';

-- A51: Andere gevaren
UPDATE traffic_signs SET
  name_ar = 'Ø£Ø®Ø·Ø§Ø± Ø£Ø®Ø±Ù‰',
  name_en = 'Other dangers',
  name_nl = 'Andere gevaren',
  name_fr = 'Autres dangers',
  updated_at = NOW()
WHERE sign_code = 'A51';

-- ========================================
-- Category B: Priority Signs
-- ========================================

-- B11: Einde voorrangsweg
UPDATE traffic_signs SET
  name_ar = 'Ù†Ù‡Ø§ÙŠØ© Ø·Ø±ÙŠÙ‚ Ø°Ùˆ Ø£ÙˆÙ„ÙˆÙŠØ©',
  name_en = 'End of priority road',
  name_nl = 'Einde voorrangsweg',
  name_fr = 'Fin de route prioritaire',
  updated_at = NOW()
WHERE sign_code = 'B11';

-- B15a: Give way to oncoming traffic at narrowing (you yield)
UPDATE traffic_signs SET
  name_ar = 'Ø£Ø¹Ø·Ù Ø§Ù„Ø£ÙˆÙ„ÙˆÙŠØ© Ù„Ù„Ù…Ø±ÙƒØ¨Ø§Øª Ø§Ù„Ù‚Ø§Ø¯Ù…Ø© ÙÙŠ Ø§Ù„Ù…Ù…Ø± Ø§Ù„Ø¶ÙŠÙ‚',
  name_en = 'Give way to oncoming traffic at narrowing',
  name_nl = 'Voorrang verlenen aan tegenliggers bij versmalling',
  name_fr = 'CÃ©der la prioritÃ© aux vÃ©hicules venant en sens inverse',
  updated_at = NOW()
WHERE sign_code = 'B15a';

-- B15b: You have priority over oncoming traffic at narrowing
UPDATE traffic_signs SET
  name_ar = 'Ù„Ø¯ÙŠÙƒ Ø§Ù„Ø£ÙˆÙ„ÙˆÙŠØ© Ø¹Ù„Ù‰ Ø§Ù„Ù…Ø±ÙƒØ¨Ø§Øª Ø§Ù„Ù‚Ø§Ø¯Ù…Ø© ÙÙŠ Ø§Ù„Ù…Ù…Ø± Ø§Ù„Ø¶ÙŠÙ‚',
  name_en = 'You have priority over oncoming traffic at narrowing',
  name_nl = 'Voorrang op tegenliggers bij versmalling',
  name_fr = 'Vous avez la prioritÃ© sur les vÃ©hicules en sens inverse',
  updated_at = NOW()
WHERE sign_code = 'B15b';

-- B15c: Give way at narrow passage (type C)
UPDATE traffic_signs SET
  name_ar = 'Ø£Ø¹Ø·Ù Ø§Ù„Ø£ÙˆÙ„ÙˆÙŠØ© ÙÙŠ Ø§Ù„Ù…Ù…Ø± Ø§Ù„Ø¶ÙŠÙ‚ (Ù†ÙˆØ¹ C)',
  name_en = 'Give way at narrow passage (type C)',
  name_nl = 'Voorrang verlenen bij smalle doorgang (type C)',
  name_fr = 'CÃ©der la prioritÃ© au passage Ã©troit (type C)',
  updated_at = NOW()
WHERE sign_code = 'B15c';

-- B15d: Priority at narrow passage (type D)
UPDATE traffic_signs SET
  name_ar = 'Ù„Ø¯ÙŠÙƒ Ø§Ù„Ø£ÙˆÙ„ÙˆÙŠØ© ÙÙŠ Ø§Ù„Ù…Ù…Ø± Ø§Ù„Ø¶ÙŠÙ‚ (Ù†ÙˆØ¹ D)',
  name_en = 'Priority at narrow passage (type D)',
  name_nl = 'Voorrang bij smalle doorgang (type D)',
  name_fr = 'PrioritÃ© au passage Ã©troit (type D)',
  updated_at = NOW()
WHERE sign_code = 'B15d';

-- B15e: Give way at narrow passage (type E)
UPDATE traffic_signs SET
  name_ar = 'Ø£Ø¹Ø·Ù Ø§Ù„Ø£ÙˆÙ„ÙˆÙŠØ© ÙÙŠ Ø§Ù„Ù…Ù…Ø± Ø§Ù„Ø¶ÙŠÙ‚ (Ù†ÙˆØ¹ E)',
  name_en = 'Give way at narrow passage (type E)',
  name_nl = 'Voorrang verlenen bij smalle doorgang (type E)',
  name_fr = 'CÃ©der la prioritÃ© au passage Ã©troit (type E)',
  updated_at = NOW()
WHERE sign_code = 'B15e';

-- B15f: Priority at narrow passage (type F)
UPDATE traffic_signs SET
  name_ar = 'Ù„Ø¯ÙŠÙƒ Ø§Ù„Ø£ÙˆÙ„ÙˆÙŠØ© ÙÙŠ Ø§Ù„Ù…Ù…Ø± Ø§Ù„Ø¶ÙŠÙ‚ (Ù†ÙˆØ¹ F)',
  name_en = 'Priority at narrow passage (type F)',
  name_nl = 'Voorrang bij smalle doorgang (type F)',
  name_fr = 'PrioritÃ© au passage Ã©troit (type F)',
  updated_at = NOW()
WHERE sign_code = 'B15f';

-- B15g: Give way at narrow passage (type G)
UPDATE traffic_signs SET
  name_ar = 'Ø£Ø¹Ø·Ù Ø§Ù„Ø£ÙˆÙ„ÙˆÙŠØ© ÙÙŠ Ø§Ù„Ù…Ù…Ø± Ø§Ù„Ø¶ÙŠÙ‚ (Ù†ÙˆØ¹ G)',
  name_en = 'Give way at narrow passage (type G)',
  name_nl = 'Voorrang verlenen bij smalle doorgang (type G)',
  name_fr = 'CÃ©der la prioritÃ© au passage Ã©troit (type G)',
  updated_at = NOW()
WHERE sign_code = 'B15g';

-- B17: Cyclists crossing
UPDATE traffic_signs SET
  name_ar = 'Ù…Ø¹Ø¨Ø± Ù„Ù„Ø¯Ø±Ø§Ø¬ÙŠÙ†',
  name_en = 'Cyclists'' crossing',
  name_nl = 'Fietsersoversteekplaats',
  name_fr = 'Passage pour cyclistes',
  updated_at = NOW()
WHERE sign_code = 'B17';

-- B21: One-way street (straight ahead)
UPDATE traffic_signs SET
  name_ar = 'Ø­Ø±ÙƒØ© Ù…Ø±ÙˆØ± Ø£Ø­Ø§Ø¯ÙŠØ© Ø§Ù„Ø§ØªØ¬Ø§Ù‡ (Ù„Ù„Ø£Ù…Ø§Ù…)',
  name_en = 'One-way street (straight ahead)',
  name_nl = 'Eenrichtingsverkeer (rechtdoor)',
  name_fr = 'Sens unique (tout droit)',
  updated_at = NOW()
WHERE sign_code = 'B21';

-- B22: One-way street (left)
UPDATE traffic_signs SET
  name_ar = 'Ø­Ø±ÙƒØ© Ù…Ø±ÙˆØ± Ø£Ø­Ø§Ø¯ÙŠØ© Ø§Ù„Ø§ØªØ¬Ø§Ù‡ (ÙŠØ³Ø§Ø±)',
  name_en = 'One-way street (left)',
  name_nl = 'Eenrichtingsverkeer (links)',
  name_fr = 'Sens unique (gauche)',
  updated_at = NOW()
WHERE sign_code = 'B22';

-- B23: Dead end
UPDATE traffic_signs SET
  name_ar = 'Ø·Ø±ÙŠÙ‚ Ù…Ø³Ø¯ÙˆØ¯',
  name_en = 'Dead end',
  name_nl = 'Doodlopende weg',
  name_fr = 'Impasse (cul-de-sac)',
  updated_at = NOW()
WHERE sign_code = 'B23';

-- ========================================
-- Category C: Prohibition Signs
-- ========================================

-- C25: End of speed restriction
UPDATE traffic_signs SET
  name_ar = 'Ù†Ù‡Ø§ÙŠØ© Ù‚ÙŠÙˆØ¯ Ø§Ù„Ø³Ø±Ø¹Ø©',
  name_en = 'End of speed restriction',
  name_nl = 'Einde maximumsnelheid',
  name_fr = 'Fin de la limitation de vitesse',
  updated_at = NOW()
WHERE sign_code = 'C25';

-- C27: End of no overtaking
UPDATE traffic_signs SET
  name_ar = 'Ù†Ù‡Ø§ÙŠØ© Ø­Ø¸Ø± Ø§Ù„ØªØ¬Ø§ÙˆØ²',
  name_en = 'End of no overtaking',
  name_nl = 'Einde verbod tot inhalen',
  name_fr = 'Fin d''interdiction de dÃ©passement',
  updated_at = NOW()
WHERE sign_code = 'C27';

-- C29: End of no overtaking for trucks
UPDATE traffic_signs SET
  name_ar = 'Ù†Ù‡Ø§ÙŠØ© Ø­Ø¸Ø± ØªØ¬Ø§ÙˆØ² Ø§Ù„Ø´Ø§Ø­Ù†Ø§Øª',
  name_en = 'End of no overtaking for trucks',
  name_nl = 'Einde verbod tot inhalen voor vrachtwagens',
  name_fr = 'Fin d''interdiction de dÃ©passement pour camions',
  updated_at = NOW()
WHERE sign_code = 'C29';

-- C31a: End of all prohibitions (type A)
UPDATE traffic_signs SET
  name_ar = 'Ù†Ù‡Ø§ÙŠØ© Ø¬Ù…ÙŠØ¹ Ø§Ù„Ù‚ÙŠÙˆØ¯',
  name_en = 'End of all prohibitions',
  name_nl = 'Einde van alle geboden en verboden',
  name_fr = 'Fin de toutes les interdictions',
  updated_at = NOW()
WHERE sign_code = 'C31a';

-- C31b: End of all prohibitions (type B)
UPDATE traffic_signs SET
  name_ar = 'Ù†Ù‡Ø§ÙŠØ© Ø¬Ù…ÙŠØ¹ Ø§Ù„Ù‚ÙŠÙˆØ¯ (Ù†ÙˆØ¹ B)',
  name_en = 'End of all prohibitions (type B)',
  name_nl = 'Einde van alle geboden en verboden (type B)',
  name_fr = 'Fin de toutes les interdictions (type B)',
  updated_at = NOW()
WHERE sign_code = 'C31b';

-- C35: Minimum speed
UPDATE traffic_signs SET
  name_ar = 'Ø­Ø¯ Ø£Ø¯Ù†Ù‰ Ù„Ù„Ø³Ø±Ø¹Ø©',
  name_en = 'Minimum speed',
  name_nl = 'Minimumsnelheid',
  name_fr = 'Vitesse minimale',
  updated_at = NOW()
WHERE sign_code = 'C35';

-- C37: End of minimum speed
UPDATE traffic_signs SET
  name_ar = 'Ù†Ù‡Ø§ÙŠØ© Ø§Ù„Ø­Ø¯ Ø§Ù„Ø£Ø¯Ù†Ù‰ Ù„Ù„Ø³Ø±Ø¹Ø©',
  name_en = 'End of minimum speed',
  name_nl = 'Einde minimumsnelheid',
  name_fr = 'Fin de vitesse minimale',
  updated_at = NOW()
WHERE sign_code = 'C37';

-- C39: Snow chains compulsory
UPDATE traffic_signs SET
  name_ar = 'Ø³Ù„Ø§Ø³Ù„ Ø§Ù„Ø«Ù„Ø¬ Ø¥Ù„Ø²Ø§Ù…ÙŠØ©',
  name_en = 'Snow chains compulsory',
  name_nl = 'Sneeuwkettingen verplicht',
  name_fr = 'ChaÃ®nes Ã  neige obligatoires',
  updated_at = NOW()
WHERE sign_code = 'C39';

-- C41: End of compulsory snow chains
UPDATE traffic_signs SET
  name_ar = 'Ù†Ù‡Ø§ÙŠØ© Ø¥Ù„Ø²Ø§Ù…ÙŠØ© Ø³Ù„Ø§Ø³Ù„ Ø§Ù„Ø«Ù„Ø¬',
  name_en = 'End of compulsory snow chains',
  name_nl = 'Einde sneeuwkettingen verplicht',
  name_fr = 'Fin de chaÃ®nes Ã  neige obligatoires',
  updated_at = NOW()
WHERE sign_code = 'C41';

-- C43: Maximum speed limit
UPDATE traffic_signs SET
  name_ar = 'Ø­Ø¯ Ø£Ù‚ØµÙ‰ Ù„Ù„Ø³Ø±Ø¹Ø©',
  name_en = 'Maximum speed limit',
  name_nl = 'Maximumsnelheid',
  name_fr = 'Limitation de vitesse',
  updated_at = NOW()
WHERE sign_code = 'C43';

-- C45: End of speed limit imposed by sign C43
UPDATE traffic_signs SET
  name_ar = 'Ù†Ù‡Ø§ÙŠØ© Ø­Ø¯ Ø§Ù„Ø³Ø±Ø¹Ø© Ø§Ù„Ù…ÙØ±ÙˆØ¶ Ø¨ÙˆØ§Ø³Ø·Ø© Ø§Ù„Ø¹Ù„Ø§Ù…Ø© C43',
  name_en = 'End of speed limit imposed by sign C43',
  name_nl = 'Einde maximumsnelheid opgelegd door bord C43',
  name_fr = 'Fin de limitation de vitesse imposÃ©e par le panneau C43',
  updated_at = NOW()
WHERE sign_code = 'C45';

-- ========================================
-- Category D: Mandatory/Compulsory Signs
-- ========================================

-- D1a: Rechtdoor rijden verplicht
UPDATE traffic_signs SET
  name_ar = 'Ø¥Ù„Ø²Ø§Ù…ÙŠ Ø§Ù„Ù…Ø¶ÙŠ Ù„Ù„Ø£Ù…Ø§Ù… Ù…Ø¨Ø§Ø´Ø±Ø©',
  name_en = 'Compulsory straight ahead',
  name_nl = 'Rechtdoor rijden verplicht',
  name_fr = 'Direction obligatoire tout droit',
  updated_at = NOW()
WHERE sign_code = 'D1a';

-- D1b: Rechtsaf rijden verplicht
UPDATE traffic_signs SET
  name_ar = 'Ø¥Ù„Ø²Ø§Ù…ÙŠ Ø§Ù„Ø§Ù†Ø¹Ø·Ø§Ù ÙŠÙ…ÙŠÙ†Ø§Ù‹',
  name_en = 'Compulsory turn right',
  name_nl = 'Rechtsaf rijden verplicht',
  name_fr = 'Direction obligatoire Ã  droite',
  updated_at = NOW()
WHERE sign_code = 'D1b';

-- D1c: Links aanhouden verplicht
UPDATE traffic_signs SET
  name_ar = 'Ø¥Ù„Ø²Ø§Ù…ÙŠ Ø§Ù„Ø§Ø³ØªÙ…Ø±Ø§Ø± ÙŠØ³Ø§Ø±Ø§Ù‹',
  name_en = 'Keep left compulsory',
  name_nl = 'Links aanhouden verplicht',
  name_fr = 'Direction obligatoire Ã  gauche',
  updated_at = NOW()
WHERE sign_code = 'D1c';

-- D1d: Rechts aanhouden verplicht
UPDATE traffic_signs SET
  name_ar = 'Ø¥Ù„Ø²Ø§Ù…ÙŠ Ø§Ù„Ø§Ø³ØªÙ…Ø±Ø§Ø± ÙŠÙ…ÙŠÙ†Ø§Ù‹',
  name_en = 'Keep right compulsory',
  name_nl = 'Rechts aanhouden verplicht',
  name_fr = 'Obligation de tenir la droite',
  updated_at = NOW()
WHERE sign_code = 'D1d';

-- D1e: Rijrichting volgen verplicht (links)
UPDATE traffic_signs SET
  name_ar = 'Ø¥Ù„Ø²Ø§Ù…ÙŠ Ø§ØªØ¨Ø§Ø¹ Ø§Ù„Ø§ØªØ¬Ø§Ù‡ Ø§Ù„Ù…Ø­Ø¯Ø¯ (ÙŠØ³Ø§Ø±Ø§Ù‹)',
  name_en = 'Compulsory direction to follow (left)',
  name_nl = 'Rijrichting volgen verplicht (links)',
  name_fr = 'Direction obligatoire (Ã  gauche)',
  updated_at = NOW()
WHERE sign_code = 'D1e';

-- D1f: Rijrichting volgen verplicht (rechts)
UPDATE traffic_signs SET
  name_ar = 'Ø¥Ù„Ø²Ø§Ù…ÙŠ Ø§ØªØ¨Ø§Ø¹ Ø§Ù„Ø§ØªØ¬Ø§Ù‡ Ø§Ù„Ù…Ø­Ø¯Ø¯ (ÙŠÙ…ÙŠÙ†Ø§Ù‹)',
  name_en = 'Compulsory direction to follow (right)',
  name_nl = 'Rijrichting volgen verplicht (rechts)',
  name_fr = 'Direction obligatoire (Ã  droite)',
  updated_at = NOW()
WHERE sign_code = 'D1f';

-- D3a: EÃ©n van de rijrichtingen volgen (type A)
UPDATE traffic_signs SET
  name_ar = 'Ø¥Ù„Ø²Ø§Ù…ÙŠ Ø§ØªØ¨Ø§Ø¹ Ø£Ø­Ø¯ Ø§Ù„Ø£Ø³Ù‡Ù… (Ù†ÙˆØ¹ A)',
  name_en = 'Compulsory to follow one of the arrows (type A)',
  name_nl = 'EÃ©n van de rijrichtingen volgen (type A)',
  name_fr = 'Obligation de suivre une des flÃ¨ches (type A)',
  updated_at = NOW()
WHERE sign_code = 'D3a';

-- D3b: EÃ©n van de rijrichtingen volgen (type B)
UPDATE traffic_signs SET
  name_ar = 'Ø¥Ù„Ø²Ø§Ù…ÙŠ Ø§ØªØ¨Ø§Ø¹ Ø£Ø­Ø¯ Ø§Ù„Ø£Ø³Ù‡Ù… (Ù†ÙˆØ¹ B)',
  name_en = 'Compulsory to follow one of the arrows (type B)',
  name_nl = 'EÃ©n van de rijrichtingen volgen (type B)',
  name_fr = 'Obligation de suivre une des flÃ¨ches (type B)',
  updated_at = NOW()
WHERE sign_code = 'D3b';

-- D4: Verplicht rechts voor voertuigen met gevaarlijke goederen
UPDATE traffic_signs SET
  name_ar = 'Ø¥Ù„Ø²Ø§Ù…ÙŠ Ù„Ù„ÙŠÙ…ÙŠÙ† Ù„Ù„Ù…Ø±ÙƒØ¨Ø§Øª Ø§Ù„ØªÙŠ ØªÙ†Ù‚Ù„ Ø¨Ø¶Ø§Ø¦Ø¹ Ø®Ø·Ø±Ø©',
  name_en = 'Compulsory right for vehicles carrying dangerous goods',
  name_nl = 'Verplicht rechts voor voertuigen met gevaarlijke goederen',
  name_fr = 'Obligatoire Ã  droite pour vÃ©hicules transportant marchandises dangereuses',
  updated_at = NOW()
WHERE sign_code = 'D4';

-- D9a: Rijbaan gedeeld voor voetgangers en fietsers (voetgangers links)
UPDATE traffic_signs SET
  name_ar = 'Ø¬Ø²Ø¡ Ù…Ù† Ø§Ù„Ø·Ø±ÙŠÙ‚ Ù…Ø­Ø¬ÙˆØ² Ù„Ù„Ù…Ø´Ø§Ø© ÙˆØ§Ù„Ø¯Ø±Ø§Ø¬ÙŠÙ† (Ù…Ø´Ø§Ø© ÙŠØ³Ø§Ø±Ø§Ù‹)',
  name_en = 'Shared road section for pedestrians and cyclists (pedestrians left)',
  name_nl = 'Rijbaan gedeeld voor voetgangers en fietsers (voetgangers links)',
  name_fr = 'Partie de route rÃ©servÃ©e aux piÃ©tons et cyclistes (piÃ©tons Ã  gauche)',
  updated_at = NOW()
WHERE sign_code = 'D9a';

-- D9b: Rijbaan gedeeld voor voetgangers en fietsers (fietsers links)
UPDATE traffic_signs SET
  name_ar = 'Ø¬Ø²Ø¡ Ù…Ù† Ø§Ù„Ø·Ø±ÙŠÙ‚ Ù…Ø­Ø¬ÙˆØ² Ù„Ù„Ù…Ø´Ø§Ø© ÙˆØ§Ù„Ø¯Ø±Ø§Ø¬ÙŠÙ† (Ø¯Ø±Ø§Ø¬Ø§Øª ÙŠØ³Ø§Ø±Ø§Ù‹)',
  name_en = 'Shared road section for pedestrians and cyclists (cyclists left)',
  name_nl = 'Rijbaan gedeeld voor voetgangers en fietsers (fietsers links)',
  name_fr = 'Partie de route rÃ©servÃ©e aux piÃ©tons et cyclistes (cyclistes Ã  gauche)',
  updated_at = NOW()
WHERE sign_code = 'D9b';

-- D10: Gemengd fiets-/voetpad (shared use path, no separation)
UPDATE traffic_signs SET
  name_ar = 'Ù…Ø³Ø§Ø± Ù…Ø´ØªØ±Ùƒ Ù„Ù„Ù…Ø´Ø§Ø© ÙˆØ§Ù„Ø¯Ø±Ø§Ø¬ÙŠÙ†',
  name_en = 'Shared use path for pedestrians and cyclists',
  name_nl = 'Gemengd fiets- en voetpad',
  name_fr = 'Chemin partagÃ© pour piÃ©tons et cyclistes',
  updated_at = NOW()
WHERE sign_code = 'D10';

-- D11: Voetpad verplicht
UPDATE traffic_signs SET
  name_ar = 'Ù…Ù…Ø´Ù‰ Ø¥Ù„Ø²Ø§Ù…ÙŠ Ù„Ù„Ù…Ø´Ø§Ø©',
  name_en = 'Compulsory footpath',
  name_nl = 'Voetpad verplicht',
  name_fr = 'Chemin obligatoire pour piÃ©tons',
  updated_at = NOW()
WHERE sign_code = 'D11';

-- D13: Ruiterpad verplicht
UPDATE traffic_signs SET
  name_ar = 'Ù…Ø³Ø§Ø± Ø¥Ù„Ø²Ø§Ù…ÙŠ Ù„Ù„ÙØ±Ø³Ø§Ù†',
  name_en = 'Compulsory path for horse riders',
  name_nl = 'Ruiterpad verplicht',
  name_fr = 'Chemin obligatoire pour cavaliers',
  updated_at = NOW()
WHERE sign_code = 'D13';

-- ========================================
-- Category E: Parking/Standing Signs
-- ========================================

-- E9a: Parkeerplaats met tijdsmeting (parkeerschijf verplicht)
UPDATE traffic_signs SET
  name_ar = 'ÙˆÙ‚ÙˆÙ Ù…Ø­Ø¯ÙˆØ¯ Ø¨Ø§Ù„ÙˆÙ‚ØªØŒ Ù‚Ø±Øµ Ø§Ù„ÙˆÙ‚ÙˆÙ Ø¥Ù„Ø²Ø§Ù…ÙŠ',
  name_en = 'Parking limited in time, parking disc required',
  name_nl = 'Parkeerplaats met tijdsbeperking, parkeerschijf verplicht',
  name_fr = 'Stationnement limitÃ© dans le temps, disque obligatoire',
  updated_at = NOW()
WHERE sign_code = 'E9a';

-- E9b: Parkeerplaats voor personenwagens
UPDATE traffic_signs SET
  name_ar = 'ÙˆÙ‚ÙˆÙ Ù„Ù„Ø³ÙŠØ§Ø±Ø§Øª ÙÙ‚Ø·',
  name_en = 'Parking for cars only',
  name_nl = 'Parkeerplaats voor personenwagens',
  name_fr = 'Stationnement rÃ©servÃ© aux voitures',
  updated_at = NOW()
WHERE sign_code = 'E9b';

-- E9c: Parkeerplaats voor vrachtwagens
UPDATE traffic_signs SET
  name_ar = 'ÙˆÙ‚ÙˆÙ Ù„Ù„Ø´Ø§Ø­Ù†Ø§Øª ÙÙ‚Ø·',
  name_en = 'Parking for trucks only',
  name_nl = 'Parkeerplaats voor vrachtwagens',
  name_fr = 'Stationnement rÃ©servÃ© aux camions',
  updated_at = NOW()
WHERE sign_code = 'E9c';

-- E9d: Parkeerplaats voor autocars
UPDATE traffic_signs SET
  name_ar = 'ÙˆÙ‚ÙˆÙ Ù„Ù„Ø­Ø§ÙÙ„Ø§Øª ÙÙ‚Ø·',
  name_en = 'Parking for buses only',
  name_nl = 'Parkeerplaats voor autocars',
  name_fr = 'Stationnement rÃ©servÃ© aux autocars',
  updated_at = NOW()
WHERE sign_code = 'E9d';

-- E9e: Parkeren op berm of trottoir verplicht
UPDATE traffic_signs SET
  name_ar = 'Ø¥Ù„Ø²Ø§Ù…ÙŠ Ø§Ù„ÙˆÙ‚ÙˆÙ Ø¹Ù„Ù‰ Ø§Ù„Ø±ØµÙŠÙ Ø£Ùˆ Ø§Ù„Ø­Ø§ÙØ©',
  name_en = 'Compulsory parking on verge or pavement',
  name_nl = 'Parkeren op berm of trottoir verplicht',
  name_fr = 'Stationnement obligatoire sur accotement ou trottoir',
  updated_at = NOW()
WHERE sign_code = 'E9e';

-- E9f: Parkeren gedeeltelijk op trottoir verplicht
UPDATE traffic_signs SET
  name_ar = 'Ø¥Ù„Ø²Ø§Ù…ÙŠ Ø§Ù„ÙˆÙ‚ÙˆÙ Ø¬Ø²Ø¦ÙŠØ§Ù‹ Ø¹Ù„Ù‰ Ø§Ù„Ø±ØµÙŠÙ',
  name_en = 'Compulsory parking partly on pavement',
  name_nl = 'Parkeren gedeeltelijk op trottoir verplicht',
  name_fr = 'Stationnement partiellement sur trottoir',
  updated_at = NOW()
WHERE sign_code = 'E9f';

-- E9g: Parkeren op de rijbaan verplicht
UPDATE traffic_signs SET
  name_ar = 'Ø¥Ù„Ø²Ø§Ù…ÙŠ Ø§Ù„ÙˆÙ‚ÙˆÙ Ø¹Ù„Ù‰ Ø§Ù„Ø·Ø±ÙŠÙ‚',
  name_en = 'Compulsory parking on carriageway',
  name_nl = 'Parkeren op de rijbaan verplicht',
  name_fr = 'Stationnement obligatoire sur chaussÃ©e',
  updated_at = NOW()
WHERE sign_code = 'E9g';

-- E9h: Parkeerplaats uitsluitend voor kampeerauto's
UPDATE traffic_signs SET
  name_ar = 'ÙˆÙ‚ÙˆÙ Ù„Ù„ÙƒØ±ÙØ§Ù†Ø§Øª ÙÙ‚Ø·',
  name_en = 'Parking for motorhomes only',
  name_nl = 'Parkeerplaats uitsluitend voor kampeerauto''s',
  name_fr = 'Stationnement rÃ©servÃ© aux camping-cars',
  updated_at = NOW()
WHERE sign_code = 'E9h';

-- E9i: Parkeerplaats voor motorfietsen
UPDATE traffic_signs SET
  name_ar = 'ÙˆÙ‚ÙˆÙ Ù„Ù„Ø¯Ø±Ø§Ø¬Ø§Øª Ø§Ù„Ù†Ø§Ø±ÙŠØ© ÙÙ‚Ø·',
  name_en = 'Parking for motorcycles only',
  name_nl = 'Parkeerplaats voor motorfietsen',
  name_fr = 'Stationnement rÃ©servÃ© aux motos',
  updated_at = NOW()
WHERE sign_code = 'E9i';

-- E9j: Wisselend parkeren voor fietsers en auto's
UPDATE traffic_signs SET
  name_ar = 'Ø±ÙƒÙ† Ù…ØªÙ†Ø§ÙˆØ¨ Ù„Ù„Ø¯Ø±Ø§Ø¬Ø§Øª ÙˆØ§Ù„Ø³ÙŠØ§Ø±Ø§Øª',
  name_en = 'Alternating parking for cyclists and cars',
  name_nl = 'Wisselend parkeren voor fietsers en auto''s',
  name_fr = 'Stationnement alternÃ© pour cyclistes et voitures',
  updated_at = NOW()
WHERE sign_code = 'E9j';

-- E11: Halfmaandelijks parkeren in de bebouwde kom
UPDATE traffic_signs SET
  name_ar = 'ÙˆÙ‚ÙˆÙ Ù†ØµÙ Ø´Ù‡Ø±ÙŠ Ø¯Ø§Ø®Ù„ Ø§Ù„Ù…Ù†Ø·Ù‚Ø© Ø§Ù„Ù…Ø¨Ù†ÙŠØ© (ØªØ¨Ø¯ÙŠÙ„ Ø§Ù„Ø¬Ù‡Ø© Ø­Ø³Ø¨ Ø§Ù„Ù†ØµÙ Ø§Ù„Ø£ÙˆÙ„ ÙˆØ§Ù„Ø«Ø§Ù†ÙŠ Ù…Ù† Ø§Ù„Ø´Ù‡Ø±)',
  name_en = 'Fortnightly parking in built-up area (side changes between first and second half of the month)',
  name_nl = 'Halfmaandelijks parkeren in de bebouwde kom (wisselen tussen eerste en tweede helft van de maand)',
  name_fr = 'Stationnement bimensuel en agglomÃ©ration',
  updated_at = NOW()
WHERE sign_code = 'E11';

-- ========================================
-- Category F: Informational/Direction Signs
-- ========================================

-- F1a: Begin van de bebouwde kom
UPDATE traffic_signs SET
  name_ar = 'Ø¨Ø¯Ø§ÙŠØ© Ù…Ù†Ø·Ù‚Ø© Ù…Ø¨Ù†ÙŠØ©',
  name_en = 'Start of built-up area',
  name_nl = 'Begin van de bebouwde kom',
  name_fr = 'DÃ©but d''agglomÃ©ration',
  updated_at = NOW()
WHERE sign_code = 'F1a';

-- F1b: Begin van de bebouwde kom (variant)
UPDATE traffic_signs SET
  name_ar = 'Ø¨Ø¯Ø§ÙŠØ© Ù…Ù†Ø·Ù‚Ø© Ù…Ø¨Ù†ÙŠØ© (Ù†ÙˆØ¹ B)',
  name_en = 'Start of built-up area (type B)',
  name_nl = 'Begin van de bebouwde kom (type B)',
  name_fr = 'DÃ©but d''agglomÃ©ration (type B)',
  updated_at = NOW()
WHERE sign_code = 'F1b';

-- F3a: Einde van de bebouwde kom
UPDATE traffic_signs SET
  name_ar = 'Ù†Ù‡Ø§ÙŠØ© Ù…Ù†Ø·Ù‚Ø© Ù…Ø¨Ù†ÙŠØ©',
  name_en = 'End of built-up area',
  name_nl = 'Einde van de bebouwde kom',
  name_fr = 'Fin d''agglomÃ©ration',
  updated_at = NOW()
WHERE sign_code = 'F3a';

-- F3b: Einde van de bebouwde kom (variant)
UPDATE traffic_signs SET
  name_ar = 'Ù†Ù‡Ø§ÙŠØ© Ù…Ù†Ø·Ù‚Ø© Ù…Ø¨Ù†ÙŠØ© (Ù†ÙˆØ¹ B)',
  name_en = 'End of built-up area (type B)',
  name_nl = 'Einde van de bebouwde kom (type B)',
  name_fr = 'Fin d''agglomÃ©ration (type B)',
  updated_at = NOW()
WHERE sign_code = 'F3b';

-- F7: Einde autosnelweg
UPDATE traffic_signs SET
  name_ar = 'Ù†Ù‡Ø§ÙŠØ© Ø§Ù„Ø·Ø±ÙŠÙ‚ Ø§Ù„Ø³Ø±ÙŠØ¹',
  name_en = 'End of motorway',
  name_nl = 'Einde autosnelweg',
  name_fr = 'Fin d''autoroute',
  updated_at = NOW()
WHERE sign_code = 'F7';

-- F14: Wachtzone voor fietsers en bromfietsen klasse A
UPDATE traffic_signs SET
  name_ar = 'Ù…Ù†Ø·Ù‚Ø© Ø§Ù†ØªØ¸Ø§Ø± Ù„Ù„Ø¯Ø±Ø§Ø¬ÙŠÙ† ÙˆØ§Ù„Ø¯Ø±Ø§Ø¬Ø§Øª Ø§Ù„Ù†Ø§Ø±ÙŠØ© Ø§Ù„ØµØºÙŠØ±Ø©',
  name_en = 'Waiting area for cyclists and mopeds',
  name_nl = 'Wachtzone voor fietsers en bromfietsen klasse A',
  name_fr = 'Zone d''attente pour cyclistes et cyclomoteurs',
  updated_at = NOW()
WHERE sign_code = 'F14';

-- F17: Rijstrook voorbehouden voor lijnbussen
UPDATE traffic_signs SET
  name_ar = 'Ù…Ø³Ø§Ø± Ù…Ø­Ø¬ÙˆØ² Ù„Ù„Ø­Ø§ÙÙ„Ø§Øª',
  name_en = 'Lane reserved for buses',
  name_nl = 'Rijstrook voorbehouden voor lijnbussen',
  name_fr = 'Voie rÃ©servÃ©e aux bus',
  updated_at = NOW()
WHERE sign_code = 'F17';

-- F18: Franchiseerbare bijzondere bedding
UPDATE traffic_signs SET
  name_ar = 'Ù…Ù‚Ø·Ø¹ Ø·Ø±ÙŠÙ‚ Ø®Ø§Øµ Ù‚Ø§Ø¨Ù„ Ù„Ù„Ø¹Ø¨ÙˆØ± Ù…Ø®ØµÙ‘Øµ Ù„Ù…Ø±ÙƒØ¨Ø§Øª Ø§Ù„Ù†Ù‚Ù„ Ø§Ù„Ø¹Ù…ÙˆÙ…ÙŠ Ø§Ù„Ù…Ù†ØªØ¸Ù…',
  name_en = 'Special negotiable lane reserved for regular public transport vehicles',
  name_nl = 'Franchiseerbare bijzondere bedding voor voertuigen van geregeld openbaar vervoer',
  name_fr = 'Site spÃ©cial franchissable rÃ©servÃ© aux vÃ©hicules des services rÃ©guliers de transport en commun',
  updated_at = NOW()
WHERE sign_code = 'F18';

-- F21: Passeren toegestaan rechts of links
UPDATE traffic_signs SET
  name_ar = 'Ø§Ù„Ù…Ø±ÙˆØ± Ù…Ø³Ù…ÙˆØ­ ÙŠÙ…ÙŠÙ†Ø§Ù‹ Ø£Ùˆ ÙŠØ³Ø§Ø±Ø§Ù‹',
  name_en = 'Passing allowed on the right or on the left',
  name_nl = 'Passeren toegestaan rechts of links',
  name_fr = 'Passage autorisÃ© Ã  droite ou Ã  gauche',
  updated_at = NOW()
WHERE sign_code = 'F21';

-- F23a: Getal van een gewone weg
UPDATE traffic_signs SET
  name_ar = 'Ø±Ù‚Ù… Ø·Ø±ÙŠÙ‚ Ø¹Ø§Ø¯ÙŠ',
  name_en = 'Number of ordinary road',
  name_nl = 'Getal van een gewone weg',
  name_fr = 'NumÃ©ro de route ordinaire',
  updated_at = NOW()
WHERE sign_code = 'F23a';

-- F23c: Getal van een internationale weg
UPDATE traffic_signs SET
  name_ar = 'Ø±Ù‚Ù… Ø·Ø±ÙŠÙ‚ Ø¯ÙˆÙ„ÙŠ',
  name_en = 'International road number',
  name_nl = 'Getal van een internationale weg',
  name_fr = 'NumÃ©ro de route internationale',
  updated_at = NOW()
WHERE sign_code = 'F23c';

-- F23d: Getal van een ringweg
UPDATE traffic_signs SET
  name_ar = 'Ø±Ù‚Ù… Ø·Ø±ÙŠÙ‚ Ø¯Ø§Ø¦Ø±ÙŠ',
  name_en = 'Ring road number',
  name_nl = 'Getal van een ringweg',
  name_fr = 'NumÃ©ro de rocade',
  updated_at = NOW()
WHERE sign_code = 'F23d';

-- F33a: Vooraanwijzingsbord (type A)
UPDATE traffic_signs SET
  name_ar = 'Ù„ÙˆØ­Ø© Ø¥Ø±Ø´Ø§Ø¯ÙŠØ© Ù…Ø³Ø¨Ù‚Ø© (Ù†ÙˆØ¹ A)',
  name_en = 'Advance direction sign (type A)',
  name_nl = 'Vooraanwijzingsbord (type A)',
  name_fr = 'Panneau de direction Ã  distance (type A)',
  updated_at = NOW()
WHERE sign_code = 'F33a';

-- F33c: Vooraanwijzingsbord (type C)
UPDATE traffic_signs SET
  name_ar = 'Ù„ÙˆØ­Ø© Ø¥Ø±Ø´Ø§Ø¯ÙŠØ© Ù…Ø³Ø¨Ù‚Ø© (Ù†ÙˆØ¹ C)',
  name_en = 'Advance direction sign (type C)',
  name_nl = 'Vooraanwijzingsbord (type C)',
  name_fr = 'Panneau de direction Ã  distance (type C)',
  updated_at = NOW()
WHERE sign_code = 'F33c';

-- F34a: Nabijheid van een installatie van openbaar nut
UPDATE traffic_signs SET
  name_ar = 'Ù‚Ø±Ø¨ Ù…Ø±ÙÙ‚ Ø°Ùˆ Ù…ØµÙ„Ø­Ø© Ø¹Ø§Ù…Ø©',
  name_en = 'Proximity to public facility',
  name_nl = 'Nabijheid van een installatie van openbaar nut',
  name_fr = 'ProximitÃ© d''installation d''intÃ©rÃªt public',
  updated_at = NOW()
WHERE sign_code = 'F34a';

-- F34b: Aanbevolen route voor bepaalde weggebruikers (type B)
UPDATE traffic_signs SET
  name_ar = 'Ø·Ø±ÙŠÙ‚ Ù…ÙˆØµÙ‰ Ø¨Ù‡ Ù„Ù…Ø³ØªØ®Ø¯Ù…ÙŠ Ø§Ù„Ø·Ø±ÙŠÙ‚ Ø§Ù„Ù…Ø­Ø¯Ø¯ÙŠÙ† (Ù†ÙˆØ¹ B)',
  name_en = 'Recommended route for specific road users (type B)',
  name_nl = 'Aanbevolen route voor bepaalde weggebruikers (type B)',
  name_fr = 'ItinÃ©raire recommandÃ© pour certains usagers (type B)',
  updated_at = NOW()
WHERE sign_code = 'F34b';

-- F34c: Aanbevolen route voor bepaalde weggebruikers (type C)
UPDATE traffic_signs SET
  name_ar = 'Ø·Ø±ÙŠÙ‚ Ù…ÙˆØµÙ‰ Ø¨Ù‡ Ù„Ù…Ø³ØªØ®Ø¯Ù…ÙŠ Ø§Ù„Ø·Ø±ÙŠÙ‚ Ø§Ù„Ù…Ø­Ø¯Ø¯ÙŠÙ† (Ù†ÙˆØ¹ C)',
  name_en = 'Recommended route for specific road users (type C)',
  name_nl = 'Aanbevolen route voor bepaalde weggebruikers (type C)',
  name_fr = 'ItinÃ©raire recommandÃ© pour certains usagers (type C)',
  updated_at = NOW()
WHERE sign_code = 'F34c';

-- F35: Toeristisch of recreatief oord
UPDATE traffic_signs SET
  name_ar = 'Ù…ÙƒØ§Ù† Ù„Ù„Ø³ÙŠØ§Ø­Ø© Ø£Ùˆ Ø§Ù„Ø§Ø³ØªØ¬Ù…Ø§Ù…',
  name_en = 'Place for tourism or recreation',
  name_nl = 'Toeristisch of recreatief oord',
  name_fr = 'Lieu de tourisme ou de dÃ©tente',
  updated_at = NOW()
WHERE sign_code = 'F35';

-- F37: Wegwijzer naar hotels, campings, restaurant
UPDATE traffic_signs SET
  name_ar = 'Ù„Ø§ÙØªØ© Ø¥Ù„Ù‰ Ø§Ù„ÙÙ†Ø§Ø¯Ù‚ ÙˆØ§Ù„Ù…Ø®ÙŠÙ…Ø§Øª ÙˆØ§Ù„Ù…Ø·Ø§Ø¹Ù…',
  name_en = 'Sign to hotels, campsites, restaurant',
  name_nl = 'Wegwijzer naar hotels, campings, restaurant',
  name_fr = 'Indication vers hÃ´tels, campings, restaurant',
  updated_at = NOW()
WHERE sign_code = 'F37';

-- F39: Aankondiging van een omleiding
UPDATE traffic_signs SET
  name_ar = 'Ø¥Ø¹Ù„Ø§Ù† Ø¹Ù† ØªØ­ÙˆÙŠÙ„Ø©',
  name_en = 'Announcement of diversion',
  name_nl = 'Aankondiging van een omleiding',
  name_fr = 'Annonce de dÃ©viation',
  updated_at = NOW()
WHERE sign_code = 'F39';

-- F41: Omleidingsroute
UPDATE traffic_signs SET
  name_ar = 'Ù„Ø§ÙØªØ© Ø·Ø±ÙŠÙ‚ Ø§Ù„ØªØ­ÙˆÙŠÙ„Ø©',
  name_en = 'Diversion route sign',
  name_nl = 'Omleidingsroute',
  name_fr = 'Panneau d''itinÃ©raire de dÃ©viation',
  updated_at = NOW()
WHERE sign_code = 'F41';

-- F45: Doodlopende weg, doorgang rechts
UPDATE traffic_signs SET
  name_ar = 'Ø·Ø±ÙŠÙ‚ Ù…Ø³Ø¯ÙˆØ¯ØŒ Ù…Ù…Ø± Ø£ÙŠÙ…Ù†',
  name_en = 'Dead end, right passage',
  name_nl = 'Doodlopende weg, doorgang rechts',
  name_fr = 'Impasse, passage Ã  droite',
  updated_at = NOW()
WHERE sign_code = 'F45';

-- F45b: Doodlopende weg, behalve voor voetgangers en fietsers
UPDATE traffic_signs SET
  name_ar = 'Ø·Ø±ÙŠÙ‚ Ù…Ø³Ø¯ÙˆØ¯ØŒ Ø¨Ø§Ø³ØªØ«Ù†Ø§Ø¡ Ø§Ù„Ù…Ø´Ø§Ø© ÙˆØ§Ù„Ø¯Ø±Ø§Ø¬ÙŠÙ†',
  name_en = 'Dead end, except pedestrians and cyclists',
  name_nl = 'Doodlopende weg, behalve voor voetgangers en fietsers',
  name_fr = 'Impasse, sauf piÃ©tons et cyclistes',
  updated_at = NOW()
WHERE sign_code = 'F45b';

-- F50b: Opgelet bij richtingsverandering, fietsers
UPDATE traffic_signs SET
  name_ar = 'Ø§Ù†ØªØ¨Ù‡ Ø¹Ù†Ø¯ ØªØºÙŠÙŠØ± Ø§Ù„Ø§ØªØ¬Ø§Ù‡ØŒ Ø¯Ø±Ø§Ø¬ÙˆÙ†',
  name_en = 'Caution when changing direction, cyclists',
  name_nl = 'Opgelet bij richtingsverandering, fietsers',
  name_fr = 'Attention en changeant de direction, cyclistes',
  updated_at = NOW()
WHERE sign_code = 'F50b';

-- F53: Horecazaak
UPDATE traffic_signs SET
  name_ar = 'Ù…Ø±ÙÙ‚ Ø£ØºØ°ÙŠØ© ÙˆØªÙ‚Ø¯ÙŠÙ… Ø§Ù„Ø·Ø¹Ø§Ù…',
  name_en = 'Catering facility',
  name_nl = 'Horecazaak',
  name_fr = 'Ã‰tablissement de restauration',
  updated_at = NOW()
WHERE sign_code = 'F53';

-- F59: Aankondiging van parkeerplaats
UPDATE traffic_signs SET
  name_ar = 'Ø¥Ø¹Ù„Ø§Ù† Ø¹Ù† Ù…ÙˆÙ‚Ù Ø³ÙŠØ§Ø±Ø§Øª',
  name_en = 'Parking announcement',
  name_nl = 'Aankondiging van parkeerplaats',
  name_fr = 'Annonce de parking',
  updated_at = NOW()
WHERE sign_code = 'F59';

-- F59a: Aankondiging van parkeerplaats (type A)
UPDATE traffic_signs SET
  name_ar = 'Ø¥Ø¹Ù„Ø§Ù† Ø¹Ù† Ù…ÙˆÙ‚Ù Ø³ÙŠØ§Ø±Ø§Øª (Ù†ÙˆØ¹ A)',
  name_en = 'Parking announcement (type A)',
  name_nl = 'Aankondiging van parkeerplaats (type A)',
  name_fr = 'Annonce de parking (type A)',
  updated_at = NOW()
WHERE sign_code = 'F59a';

-- F59b: Aankondiging van fietsenstalling
UPDATE traffic_signs SET
  name_ar = 'Ø¥Ø¹Ù„Ø§Ù† Ø¹Ù† Ù…ÙˆÙ‚Ù Ø¯Ø±Ø§Ø¬Ø§Øª',
  name_en = 'Bicycle parking announcement',
  name_nl = 'Aankondiging van fietsenstalling',
  name_fr = 'Annonce de parking vÃ©los',
  updated_at = NOW()
WHERE sign_code = 'F59b';

-- F63: Tankstation met bepaalde brandstof
UPDATE traffic_signs SET
  name_ar = 'Ù…Ø­Ø·Ø© ÙˆÙ‚ÙˆØ¯ Ø¨ÙˆÙ‚ÙˆØ¯ Ù…Ø­Ø¯Ø¯',
  name_en = 'Gas station with specific fuel',
  name_nl = 'Tankstation met bepaalde brandstof',
  name_fr = 'Station-service avec carburant spÃ©cifique',
  updated_at = NOW()
WHERE sign_code = 'F63';

-- F77: Toeristische informatie
UPDATE traffic_signs SET
  name_ar = 'Ù…Ø¹Ù„ÙˆÙ…Ø§Øª Ø³ÙŠØ§Ø­ÙŠØ©',
  name_en = 'Tourist information',
  name_nl = 'Toeristische informatie',
  name_fr = 'Information touristique',
  updated_at = NOW()
WHERE sign_code = 'F77';

-- F87: Verhoogde inrichting (verkeerseiland)
UPDATE traffic_signs SET
  name_ar = 'Ù…Ù†Ø´Ø£Ø© Ù…Ø±ØªÙØ¹Ø© (Ø¬Ø²ÙŠØ±Ø© Ù…Ø±ÙˆØ±ÙŠØ©)',
  name_en = 'Raised facility (refuge island)',
  name_nl = 'Verhoogde inrichting (verkeerseiland)',
  name_fr = 'AmÃ©nagement surÃ©levÃ© (Ã®lot refuge)',
  updated_at = NOW()
WHERE sign_code = 'F87';

-- F97: Rijstrookversmalling
UPDATE traffic_signs SET
  name_ar = 'ØªØ¶ÙŠÙŠÙ‚ Ø§Ù„Ù…Ø³Ø§Ø±',
  name_en = 'Lane narrowing',
  name_nl = 'Rijstrookversmalling',
  name_fr = 'RÃ©trÃ©cissement de voie',
  updated_at = NOW()
WHERE sign_code = 'F97';

-- F99a: Rijbaan voorbehouden voor voetgangers, fietsers en ruiters
UPDATE traffic_signs SET
  name_ar = 'Ù…Ø­Ø¬ÙˆØ² Ù„Ù„Ù…Ø´Ø§Ø© ÙˆØ§Ù„Ø¯Ø±Ø§Ø¬ÙŠÙ† ÙˆØ§Ù„ÙØ±Ø³Ø§Ù†',
  name_en = 'Reserved for pedestrians, cyclists and horse riders',
  name_nl = 'Rijbaan voorbehouden voor voetgangers, fietsers en ruiters',
  name_fr = 'RÃ©servÃ© aux piÃ©tons, cyclistes et cavaliers',
  updated_at = NOW()
WHERE sign_code = 'F99a';

-- F99b: Gedeelte openbare weg voorbehouden voor fietsers en voetgangers
UPDATE traffic_signs SET
  name_ar = 'Ø¬Ø²Ø¡ Ù…Ù† Ø§Ù„Ø·Ø±ÙŠÙ‚ Ø§Ù„Ø¹Ø§Ù… Ù…Ø­Ø¬ÙˆØ² Ù„Ù„Ø¯Ø±Ø§Ø¬ÙŠÙ† ÙˆØ§Ù„Ù…Ø´Ø§Ø©',
  name_en = 'Part of public road reserved for cyclists and pedestrians',
  name_nl = 'Gedeelte openbare weg voorbehouden voor fietsers en voetgangers',
  name_fr = 'Partie de voie publique rÃ©servÃ©e aux cyclistes et piÃ©tons',
  updated_at = NOW()
WHERE sign_code = 'F99b';

-- F99c: Rijbaan voorbehouden voor landbouwvoertuigen en voetgangers
UPDATE traffic_signs SET
  name_ar = 'Ù…Ø­Ø¬ÙˆØ² Ù„Ù„Ù…Ø±ÙƒØ¨Ø§Øª Ø§Ù„Ø²Ø±Ø§Ø¹ÙŠØ© ÙˆØ§Ù„Ù…Ø´Ø§Ø©',
  name_en = 'Reserved for agricultural vehicles and pedestrians',
  name_nl = 'Rijbaan voorbehouden voor landbouwvoertuigen en voetgangers',
  name_fr = 'RÃ©servÃ© aux vÃ©hicules agricoles et piÃ©tons',
  updated_at = NOW()
WHERE sign_code = 'F99c';

-- F101a: Einde van voorbehouden gedeelte voor voetgangers, fietsers en ruiters
UPDATE traffic_signs SET
  name_ar = 'Ù†Ù‡Ø§ÙŠØ© Ø§Ù„Ù…Ù†Ø·Ù‚Ø© Ø§Ù„Ù…Ø­Ø¬ÙˆØ²Ø© Ù„Ù„Ù…Ø´Ø§Ø© ÙˆØ§Ù„Ø¯Ø±Ø§Ø¬ÙŠÙ† ÙˆØ§Ù„ÙØ±Ø³Ø§Ù†',
  name_en = 'End of area reserved for pedestrians, cyclists and riders',
  name_nl = 'Einde van voorbehouden gedeelte voor voetgangers, fietsers en ruiters',
  name_fr = 'Fin de zone rÃ©servÃ©e aux piÃ©tons, cyclistes et cavaliers',
  updated_at = NOW()
WHERE sign_code = 'F101a';

-- F101b: Einde van voorbehouden gedeelte voor fietsers en voetgangers
UPDATE traffic_signs SET
  name_ar = 'Ù†Ù‡Ø§ÙŠØ© Ø¬Ø²Ø¡ Ø§Ù„Ø·Ø±ÙŠÙ‚ Ø§Ù„Ù…Ø­Ø¬ÙˆØ² Ù„Ù„Ø¯Ø±Ø§Ø¬ÙŠÙ† ÙˆØ§Ù„Ù…Ø´Ø§Ø©',
  name_en = 'End of part reserved for cyclists and pedestrians',
  name_nl = 'Einde van voorbehouden gedeelte voor fietsers en voetgangers',
  name_fr = 'Fin de partie rÃ©servÃ©e aux cyclistes et piÃ©tons',
  updated_at = NOW()
WHERE sign_code = 'F101b';

-- F101c: Einde van voorbehouden gedeelte voor landbouwvoertuigen
UPDATE traffic_signs SET
  name_ar = 'Ù†Ù‡Ø§ÙŠØ© Ø§Ù„Ù…Ù†Ø·Ù‚Ø© Ø§Ù„Ù…Ø­Ø¬ÙˆØ²Ø© Ù„Ù„Ù…Ø±ÙƒØ¨Ø§Øª Ø§Ù„Ø²Ø±Ø§Ø¹ÙŠØ©',
  name_en = 'End of area reserved for agricultural vehicles',
  name_nl = 'Einde van voorbehouden gedeelte voor landbouwvoertuigen',
  name_fr = 'Fin de zone rÃ©servÃ©e aux vÃ©hicules agricoles',
  updated_at = NOW()
WHERE sign_code = 'F101c';

-- F103: Begin van de voetgangerszone
UPDATE traffic_signs SET
  name_ar = 'Ø¨Ø¯Ø§ÙŠØ© Ù…Ù†Ø·Ù‚Ø© Ø§Ù„Ù…Ø´Ø§Ø©',
  name_en = 'Start of pedestrian zone',
  name_nl = 'Begin van de voetgangerszone',
  name_fr = 'DÃ©but de zone piÃ©tonne',
  updated_at = NOW()
WHERE sign_code = 'F103';

-- F105: Einde van de voetgangerszone
UPDATE traffic_signs SET
  name_ar = 'Ù†Ù‡Ø§ÙŠØ© Ù…Ù†Ø·Ù‚Ø© Ø§Ù„Ù…Ø´Ø§Ø©',
  name_en = 'End of pedestrian zone',
  name_nl = 'Einde van de voetgangerszone',
  name_fr = 'Fin de zone piÃ©tonne',
  updated_at = NOW()
WHERE sign_code = 'F105';

-- F117: Begin van de lage-emissiezone
UPDATE traffic_signs SET
  name_ar = 'Ø¨Ø¯Ø§ÙŠØ© Ù…Ù†Ø·Ù‚Ø© Ø§Ù„Ø§Ù†Ø¨Ø¹Ø§Ø«Ø§Øª Ø§Ù„Ù…Ù†Ø®ÙØ¶Ø©',
  name_en = 'Start of low emission zone',
  name_nl = 'Begin van de lage-emissiezone',
  name_fr = 'DÃ©but de zone Ã  faibles Ã©missions',
  updated_at = NOW()
WHERE sign_code = 'F117';

-- F118: Einde van de lage-emissiezone
UPDATE traffic_signs SET
  name_ar = 'Ù†Ù‡Ø§ÙŠØ© Ù…Ù†Ø·Ù‚Ø© Ø§Ù„Ø§Ù†Ø¨Ø¹Ø§Ø«Ø§Øª Ø§Ù„Ù…Ù†Ø®ÙØ¶Ø©',
  name_en = 'End of low emission zone',
  name_nl = 'Einde van de lage-emissiezone',
  name_fr = 'Fin de zone Ã  faibles Ã©missions',
  updated_at = NOW()
WHERE sign_code = 'F118';

-- ========================================
-- Category M: Supplementary Bicycle Signs
-- ========================================

-- M2: Uitgezonderd fietsers
UPDATE traffic_signs SET
  name_ar = 'Ø¨Ø§Ø³ØªØ«Ù†Ø§Ø¡ Ø§Ù„Ø¯Ø±Ø§Ø¬ÙŠÙ†',
  name_en = 'Except cyclists',
  name_nl = 'Uitgezonderd fietsers',
  name_fr = 'Sauf cyclistes',
  updated_at = NOW()
WHERE sign_code = 'M2';

-- M4: Fietsers mogen in twee richtingen rijden
UPDATE traffic_signs SET
  name_ar = 'ÙŠÙØ³Ù…Ø­ Ù„Ù„Ø¯Ø±Ø§Ø¬ÙŠÙ† Ø¨Ø§Ù„Ø³ÙŠØ± ÙÙŠ Ø§ØªØ¬Ø§Ù‡ÙŠÙ†',
  name_en = 'Cyclists may go in two directions',
  name_nl = 'Fietsers mogen in twee richtingen rijden',
  name_fr = 'Cyclistes autorisÃ©s dans les deux sens',
  updated_at = NOW()
WHERE sign_code = 'M4';

-- M5b: Fietsers en bromfietsers mogen in twee richtingen rijden
UPDATE traffic_signs SET
  name_ar = 'ÙŠÙØ³Ù…Ø­ Ù„Ù„Ø¯Ø±Ø§Ø¬ÙŠÙ† ÙˆØ§Ù„Ø¯Ø±Ø§Ø¬Ø§Øª Ø§Ù„Ù†Ø§Ø±ÙŠØ© Ø§Ù„ØµØºÙŠØ±Ø© Ø¨Ø§Ù„Ø³ÙŠØ± ÙÙŠ Ø§ØªØ¬Ø§Ù‡ÙŠÙ†',
  name_en = 'Cyclists and mopeds may go in two directions',
  name_nl = 'Fietsers en bromfietsers mogen in twee richtingen rijden',
  name_fr = 'Cyclistes et cyclomoteurs dans les deux sens',
  updated_at = NOW()
WHERE sign_code = 'M5b';

-- M6: Verplicht voor bromfietsers klasse B
UPDATE traffic_signs SET
  name_ar = 'Ø¥Ù„Ø²Ø§Ù…ÙŠ Ù„Ù„Ø¯Ø±Ø§Ø¬Ø§Øª Ø§Ù„Ù†Ø§Ø±ÙŠØ© Ø§Ù„ØµØºÙŠØ±Ø© ÙØ¦Ø© B',
  name_en = 'Compulsory for class B mopeds',
  name_nl = 'Verplicht voor bromfietsers klasse B',
  name_fr = 'Obligatoire pour cyclomoteurs classe B',
  updated_at = NOW()
WHERE sign_code = 'M6';

-- M7: Verboden voor bromfietsers klasse B
UPDATE traffic_signs SET
  name_ar = 'Ù…Ù…Ù†ÙˆØ¹ Ù„Ù„Ø¯Ø±Ø§Ø¬Ø§Øª Ø§Ù„Ù†Ø§Ø±ÙŠØ© Ø§Ù„ØµØºÙŠØ±Ø© ÙØ¦Ø© B',
  name_en = 'Prohibited for class B mopeds',
  name_nl = 'Verboden voor bromfietsers klasse B',
  name_fr = 'Interdit aux cyclomoteurs classe B',
  updated_at = NOW()
WHERE sign_code = 'M7';

-- M9: Fietsers in twee richtingen op de kruisende weg
UPDATE traffic_signs SET
  name_ar = 'Ø¯Ø±Ø§Ø¬Ø§Øª ÙÙŠ Ø§ØªØ¬Ø§Ù‡ÙŠÙ† Ø¹Ù„Ù‰ Ø§Ù„Ø·Ø±ÙŠÙ‚ Ø§Ù„Ù…ØªÙ‚Ø§Ø·Ø¹',
  name_en = 'Cyclists in two directions on crossing road',
  name_nl = 'Fietsers in twee richtingen op de kruisende weg',
  name_fr = 'Cyclistes dans deux sens sur route transversale',
  updated_at = NOW()
WHERE sign_code = 'M9';

-- M10: Fietsers en bromfietsers in twee richtingen
UPDATE traffic_signs SET
  name_ar = 'Ø¯Ø±Ø§Ø¬Ø§Øª ÙˆØ¯Ø±Ø§Ø¬Ø§Øª Ù†Ø§Ø±ÙŠØ© ØµØºÙŠØ±Ø© ÙÙŠ Ø§ØªØ¬Ø§Ù‡ÙŠÙ†',
  name_en = 'Cyclists and mopeds in two directions',
  name_nl = 'Fietsers en bromfietsers in twee richtingen',
  name_fr = 'Cyclistes et cyclomoteurs dans deux sens',
  updated_at = NOW()
WHERE sign_code = 'M10';

-- M11: Uitgezonderd fietsers (type 11)
UPDATE traffic_signs SET
  name_ar = 'Ø¨Ø§Ø³ØªØ«Ù†Ø§Ø¡ Ø§Ù„Ø¯Ø±Ø§Ø¬ÙŠÙ† (Ù†ÙˆØ¹ 11)',
  name_en = 'Except cyclists (type 11)',
  name_nl = 'Uitgezonderd fietsers (type 11)',
  name_fr = 'Sauf cyclistes (type 11)',
  updated_at = NOW()
WHERE sign_code = 'M11';

-- M12: Uitgezonderd fietsers (type 12)
UPDATE traffic_signs SET
  name_ar = 'Ø¨Ø§Ø³ØªØ«Ù†Ø§Ø¡ Ø§Ù„Ø¯Ø±Ø§Ø¬ÙŠÙ† (Ù†ÙˆØ¹ 12)',
  name_en = 'Except cyclists (type 12)',
  name_nl = 'Uitgezonderd fietsers (type 12)',
  name_fr = 'Sauf cyclistes (type 12)',
  updated_at = NOW()
WHERE sign_code = 'M12';

-- M13: Verplicht voor speed pedelecs
UPDATE traffic_signs SET
  name_ar = 'Ø¥Ù„Ø²Ø§Ù…ÙŠ Ù„Ù„Ø¯Ø±Ø§Ø¬Ø§Øª Ø§Ù„ÙƒÙ‡Ø±Ø¨Ø§Ø¦ÙŠØ© Ø§Ù„Ø³Ø±ÙŠØ¹Ø©',
  name_en = 'Compulsory for speed pedelecs',
  name_nl = 'Verplicht voor speed pedelecs',
  name_fr = 'Obligatoire pour speed pedelecs',
  updated_at = NOW()
WHERE sign_code = 'M13';

-- M14: Verplicht voor bromfietsers klasse B (type 14)
UPDATE traffic_signs SET
  name_ar = 'Ø¥Ù„Ø²Ø§Ù…ÙŠ Ù„Ù„Ø¯Ø±Ø§Ø¬Ø§Øª Ø§Ù„Ù†Ø§Ø±ÙŠØ© Ø§Ù„ØµØºÙŠØ±Ø© ÙØ¦Ø© B (Ù†ÙˆØ¹ 14)',
  name_en = 'Compulsory for class B mopeds (type 14)',
  name_nl = 'Verplicht voor bromfietsers klasse B (type 14)',
  name_fr = 'Obligatoire pour cyclomoteurs classe B (type 14)',
  updated_at = NOW()
WHERE sign_code = 'M14';

-- M15: Verboden voor speed pedelecs
UPDATE traffic_signs SET
  name_ar = 'Ù…Ù…Ù†ÙˆØ¹ Ù„Ù„Ø¯Ø±Ø§Ø¬Ø§Øª Ø§Ù„ÙƒÙ‡Ø±Ø¨Ø§Ø¦ÙŠØ© Ø§Ù„Ø³Ø±ÙŠØ¹Ø©',
  name_en = 'Prohibited for speed pedelecs',
  name_nl = 'Verboden voor speed pedelecs',
  name_fr = 'Interdit aux speed pedelecs',
  updated_at = NOW()
WHERE sign_code = 'M15';

-- M16: Verboden voor bromfietsers klasse B (type 16)
UPDATE traffic_signs SET
  name_ar = 'Ù…Ù…Ù†ÙˆØ¹ Ù„Ù„Ø¯Ø±Ø§Ø¬Ø§Øª Ø§Ù„Ù†Ø§Ø±ÙŠØ© Ø§Ù„ØµØºÙŠØ±Ø© ÙØ¦Ø© B (Ù†ÙˆØ¹ 16)',
  name_en = 'Prohibited for class B mopeds (type 16)',
  name_nl = 'Verboden voor bromfietsers klasse B (type 16)',
  name_fr = 'Interdit aux cyclomoteurs classe B (type 16)',
  updated_at = NOW()
WHERE sign_code = 'M16';

-- M17: Fietsers en speed pedelecs mogen in twee richtingen rijden
UPDATE traffic_signs SET
  name_ar = 'ÙŠÙØ³Ù…Ø­ Ù„Ù„Ø¯Ø±Ø§Ø¬ÙŠÙ† ÙˆØ§Ù„Ø¯Ø±Ø§Ø¬Ø§Øª Ø§Ù„ÙƒÙ‡Ø±Ø¨Ø§Ø¦ÙŠØ© Ø§Ù„Ø³Ø±ÙŠØ¹Ø© Ø¨Ø§Ù„Ø³ÙŠØ± ÙÙŠ Ø§ØªØ¬Ø§Ù‡ÙŠÙ†',
  name_en = 'Cyclists and speed pedelecs may go in two directions',
  name_nl = 'Fietsers en speed pedelecs mogen in twee richtingen rijden',
  name_fr = 'Cyclistes et speed pedelecs dans les deux sens',
  updated_at = NOW()
WHERE sign_code = 'M17';

-- M18: Fietsers, bromfietsers en speed pedelecs mogen in twee richtingen
UPDATE traffic_signs SET
  name_ar = 'ÙŠÙØ³Ù…Ø­ Ù„Ù„Ø¯Ø±Ø§Ø¬Ø§Øª ÙˆØ§Ù„Ø¯Ø±Ø§Ø¬Ø§Øª Ø§Ù„Ù†Ø§Ø±ÙŠØ© ÙˆØ§Ù„Ø¯Ø±Ø§Ø¬Ø§Øª Ø§Ù„ÙƒÙ‡Ø±Ø¨Ø§Ø¦ÙŠØ© Ø§Ù„Ø³Ø±ÙŠØ¹Ø© ÙÙŠ Ø§ØªØ¬Ø§Ù‡ÙŠÙ†',
  name_en = 'Cyclists, mopeds and speed pedelecs in two directions',
  name_nl = 'Fietsers, bromfietsers en speed pedelecs mogen in twee richtingen',
  name_fr = 'Cyclistes, cyclomoteurs et speed pedelecs dans deux sens',
  updated_at = NOW()
WHERE sign_code = 'M18';

-- M19: Alleen voor speed pedelecs
UPDATE traffic_signs SET
  name_ar = 'Ù„Ù„Ø¯Ø±Ø§Ø¬Ø§Øª Ø§Ù„ÙƒÙ‡Ø±Ø¨Ø§Ø¦ÙŠØ© Ø§Ù„Ø³Ø±ÙŠØ¹Ø© ÙÙ‚Ø·',
  name_en = 'Only for speed pedelecs',
  name_nl = 'Alleen voor speed pedelecs',
  name_fr = 'Uniquement pour speed pedelecs',
  updated_at = NOW()
WHERE sign_code = 'M19';

-- M20: Alleen voor fietsers en speed pedelecs
UPDATE traffic_signs SET
  name_ar = 'Ù„Ù„Ø¯Ø±Ø§Ø¬ÙŠÙ† ÙˆØ§Ù„Ø¯Ø±Ø§Ø¬Ø§Øª Ø§Ù„ÙƒÙ‡Ø±Ø¨Ø§Ø¦ÙŠØ© Ø§Ù„Ø³Ø±ÙŠØ¹Ø© ÙÙ‚Ø·',
  name_en = 'Only for cyclists and speed pedelecs',
  name_nl = 'Alleen voor fietsers en speed pedelecs',
  name_fr = 'Uniquement pour cyclistes et speed pedelecs',
  updated_at = NOW()
WHERE sign_code = 'M20';