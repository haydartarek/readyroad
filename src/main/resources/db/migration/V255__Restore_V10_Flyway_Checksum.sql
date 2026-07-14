-- V93 records the historical V10 checksum after rebuilding traffic_rules.
-- The immutable V10 file now resolves to 754376312, so a clean database
-- otherwise fails Flyway validation on its second startup.
UPDATE flyway_schema_history
SET checksum = 754376312
WHERE version = '10'
  AND script = 'V10__Add_Traffic_Rules.sql'
  AND checksum = 1766337887;
