-- V113: Add audience column to notifications table
-- Values: USER (default), ADMIN, ALL
ALTER TABLE notifications
    ADD COLUMN audience VARCHAR(20) NOT NULL DEFAULT 'USER';
