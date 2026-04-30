-- Remove non-canonical road_sign rows that do not exist in the signs_import disk set.
-- Keep only the approved D allowlist that exists on disk.

DELETE FROM road_signs
WHERE sign_code LIKE 'D%'
   AND sign_code NOT IN (
      'D10','D11','D13','D1a','D1b-links','D1b-rechts','D1c','D1d','D1e','D1f',
      'D3a','D3b','D4-links','D4-rechtdoor','D4-rechts','D5','D7','D9a'
   );
