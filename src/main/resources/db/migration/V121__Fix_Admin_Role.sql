-- V121: Fix Admin Role
-- Bug: V28's UPDATE sets role='USER' for an existing admin@readyroad.be row,
--      then INSERT IGNORE does nothing (duplicate). Admin ends up as USER.
-- Fix: Ensure the admin user has ADMIN role and is active.

UPDATE users
SET role      = 'ADMIN',
    is_active = TRUE,
    is_locked = FALSE
WHERE username = 'admin'
   OR email    = 'admin@readyroad.be';
