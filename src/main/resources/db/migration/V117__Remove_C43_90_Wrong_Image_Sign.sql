-- Remove C43_90 sign (90 km speed limit) — image file contains wrong content (danger sign instead of prohibition sign).
DELETE FROM traffic_signs WHERE sign_code = 'C43_90';
