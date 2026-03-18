-- V155__Rename_E9a_Default_To_E9a.sql
-- Rename sign code E9a-default → E9a in traffic_signs and road_signs.
-- V153 introduced "E9a-default" as an internal code for the base E9a
-- "Parkeren toegelaten" sign. This migration restores the canonical code E9a.

UPDATE traffic_signs
  SET sign_code            = 'E9a',
      normalized_sign_code = 'E9a'
  WHERE sign_code = 'E9a-default';

UPDATE road_signs
  SET sign_code            = 'E9a',
      normalized_sign_code = 'E9a'
  WHERE sign_code = 'E9a-default';
