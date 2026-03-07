-- V108 — Remove duplicate traffic_signs rows caused by overlapping migrations
--        (V103 and V104 re-inserted signs that already existed).
-- Strategy: keep the row with the lowest id per sign_code, delete the rest.
-- Then add a UNIQUE constraint to prevent future duplicates.

-- Step 1: Delete duplicate rows — keep min(id) per sign_code.
-- Wrapped in a derived table to avoid MySQL "can't modify target table in FROM" error.
DELETE FROM traffic_signs
WHERE id NOT IN (
    SELECT keep_id FROM (
        SELECT MIN(id) AS keep_id
        FROM traffic_signs
        GROUP BY sign_code
    ) AS keepers
);

-- Step 2: Add UNIQUE constraint on sign_code to enforce uniqueness going forward.
ALTER TABLE traffic_signs
    ADD CONSTRAINT uq_traffic_signs_sign_code UNIQUE (sign_code);
