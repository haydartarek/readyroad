-- V151: Remove A33-v1 sign from the database completely.
-- A33-v1 was a duplicate/variant entry. A33 remains.

DELETE FROM road_signs    WHERE sign_code = 'A33-v1';
DELETE FROM traffic_signs WHERE sign_code = 'A33-v1';
