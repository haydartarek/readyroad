-- Remove non-canonical road_sign rows that do not exist in the manually curated disk set.
-- These obsolete rows were previously kept as inactive, which left road_signs at 209 rows
-- even though the canonical sign set contains 203 files.

DELETE FROM road_signs
WHERE sign_code IN (
    'C22a',
    'C43_90',
    'D1a-links',
    'D1a-rechts',
    'D4-left',
    'D4-right'
);