-- Keep the backend role working while protecting the readyroad schema from
-- accidental access through Supabase-facing roles.

DO $$
DECLARE
    target record;
BEGIN
    FOR target IN
        SELECT n.nspname AS schema_name, c.relname AS table_name
        FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE n.nspname = current_schema()
          AND c.relkind = 'r'
          AND c.relname <> 'flyway_schema_history'
          AND NOT c.relrowsecurity
    LOOP
        EXECUTE format(
            'ALTER TABLE %I.%I ENABLE ROW LEVEL SECURITY',
            target.schema_name,
            target.table_name
        );
    END LOOP;

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
                  FROM pg_policies
                  WHERE schemaname = n.nspname
                    AND tablename = c.relname
                    AND policyname = 'readyroad_app_full_access'
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

DO $$
DECLARE
    schema_name text := current_schema();
BEGIN
    IF to_regprocedure(format('%I.protect_article_version_history()', schema_name)) IS NOT NULL THEN
        EXECUTE format(
            'ALTER FUNCTION %I.protect_article_version_history() SET search_path = pg_catalog',
            schema_name
        );
    END IF;

    IF to_regprocedure(format('%I.protect_article_publication_route()', schema_name)) IS NOT NULL THEN
        EXECUTE format(
            'ALTER FUNCTION %I.protect_article_publication_route() SET search_path = pg_catalog',
            schema_name
        );
    END IF;

    IF to_regprocedure(format('%I.editorial_target_queries_are_valid(jsonb)', schema_name)) IS NOT NULL THEN
        EXECUTE format(
            'ALTER FUNCTION %I.editorial_target_queries_are_valid(jsonb) SET search_path = pg_catalog',
            schema_name
        );
    END IF;
END;
$$;
