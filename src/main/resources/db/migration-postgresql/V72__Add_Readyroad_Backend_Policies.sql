-- Complete the backend policy for readyroad tables that already had RLS
-- enabled before V71 but did not yet have a policy for the application role.

DO $$
DECLARE
    target record;
BEGIN
    IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'readyroad_app') THEN
        FOR target IN
            SELECT n.nspname AS schema_name, c.relname AS table_name
            FROM pg_class c
            JOIN pg_namespace n ON n.oid = c.relnamespace
            WHERE n.nspname = current_schema()
              AND c.relkind = 'r'
              AND c.relname <> 'flyway_schema_history'
              AND c.relrowsecurity
              AND NOT EXISTS (
                  SELECT 1
                  FROM pg_policies p
                  WHERE p.schemaname = n.nspname
                    AND p.tablename = c.relname
                    AND p.policyname = 'readyroad_app_full_access'
              )
        LOOP
            EXECUTE format(
                'CREATE POLICY readyroad_app_full_access ON %I.%I FOR ALL TO readyroad_app USING (true) WITH CHECK (true)',
                target.schema_name,
                target.table_name
            );
        END LOOP;
    END IF;
END;
$$;
