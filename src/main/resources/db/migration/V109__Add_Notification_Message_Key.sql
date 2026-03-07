-- V109: Add messageKey and messageParams columns to notifications table.
-- These enable the frontend to render translated notification messages
-- instead of displaying the hardcoded English text stored in `message`.
-- Old rows (messageKey IS NULL) fall back to the existing `message` field.

ALTER TABLE notifications
    ADD COLUMN message_key    VARCHAR(200) NULL AFTER message,
    ADD COLUMN message_params TEXT         NULL AFTER message_key;
