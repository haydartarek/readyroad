-- Fix failed V33 migration
DELETE FROM flyway_schema_history WHERE version = '33';
