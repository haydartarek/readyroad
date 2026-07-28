-- ReadyRoad clients use Spring Boot exclusively. Supabase Data API roles do not
-- need direct access. RLS is enabled as defense in depth without FORCE, so the
-- table-owning readyroad_app role used by Spring Boot remains operational.

DO $$
DECLARE
    table_record RECORD;
    api_role TEXT;
BEGIN
    FOR table_record IN
        SELECT schemaname, tablename
        FROM pg_tables
        WHERE schemaname = current_schema()
          AND tablename <> 'flyway_schema_history'
    LOOP
        EXECUTE format(
            'ALTER TABLE %I.%I ENABLE ROW LEVEL SECURITY',
            table_record.schemaname,
            table_record.tablename);
    END LOOP;

    FOREACH api_role IN ARRAY ARRAY['anon', 'authenticated', 'service_role']
    LOOP
        IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname = api_role) THEN
            EXECUTE format(
                'REVOKE ALL ON SCHEMA %I FROM %I',
                current_schema(),
                api_role);
            EXECUTE format(
                'REVOKE ALL ON ALL TABLES IN SCHEMA %I FROM %I',
                current_schema(),
                api_role);
            EXECUTE format(
                'REVOKE ALL ON ALL SEQUENCES IN SCHEMA %I FROM %I',
                current_schema(),
                api_role);
        END IF;
    END LOOP;
END
$$;
