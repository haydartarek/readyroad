-- Add warning_summary column to import_history for full audit trail
ALTER TABLE import_history ADD COLUMN warning_summary TEXT AFTER error_summary;
