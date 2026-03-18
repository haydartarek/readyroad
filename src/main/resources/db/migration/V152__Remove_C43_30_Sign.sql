-- Remove C43_30 sign from all tables.
-- C43_30 is a duplicate of C43 (both share the same 30 km/h image).
-- The canonical C43 row is kept; only the C43_30 duplicate is removed.

DELETE FROM road_signs    WHERE sign_code = 'C43_30';
DELETE FROM traffic_signs WHERE sign_code = 'C43_30';
