-- ReadyRoad Supabase RLS rollback
-- Use only if V12 prevents the Spring Boot owner role from operating.
-- The pre-change API grants were empty, so this rollback grants nothing.

BEGIN;

DO $$
DECLARE
    table_record RECORD;
BEGIN
    FOR table_record IN
        SELECT schemaname, tablename
        FROM pg_tables
        WHERE schemaname = 'readyroad'
    LOOP
        EXECUTE format(
            'ALTER TABLE %I.%I DISABLE ROW LEVEL SECURITY',
            table_record.schemaname,
            table_record.tablename);
    END LOOP;
END
$$;

COMMIT;
