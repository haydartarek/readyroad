ALTER TABLE sign_practice_sessions
    DROP CONSTRAINT IF EXISTS chk_sps_status;

ALTER TABLE sign_practice_sessions
    ADD CONSTRAINT chk_sps_status
    CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'ABANDONED'));
