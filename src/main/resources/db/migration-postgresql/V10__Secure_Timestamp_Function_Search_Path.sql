-- Pin lookup paths for trigger functions that execute with database privileges.
-- Function bodies and trigger bindings remain unchanged.

DO $$
DECLARE
    application_schema NAME := current_schema();
BEGIN
    EXECUTE format(
        'ALTER FUNCTION %I.set_updated_at_column() SET search_path = %I, pg_temp',
        application_schema,
        application_schema
    );
    EXECUTE format(
        'ALTER FUNCTION %I.set_last_updated_column() SET search_path = %I, pg_temp',
        application_schema,
        application_schema
    );
END;
$$;
