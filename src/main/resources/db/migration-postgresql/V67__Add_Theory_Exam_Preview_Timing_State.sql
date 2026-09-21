-- Persistent lifecycle state for Rijvia's free theory-exam preview.
--
-- Existing attempts remain legacy/full attempts because preview_attempt
-- defaults to FALSE. New API attempts opt into the entitlement/paywall lifecycle.
--
-- A preview attempt is paused at the paywall when paywall_reached_at is set
-- and full_access_resumed_at is still NULL.
ALTER TABLE exam_simulations
    ADD COLUMN preview_attempt BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN paywall_reached_at TIMESTAMPTZ,
    ADD COLUMN full_access_resumed_at TIMESTAMPTZ,
    ADD COLUMN paywall_paused_seconds BIGINT NOT NULL DEFAULT 0;