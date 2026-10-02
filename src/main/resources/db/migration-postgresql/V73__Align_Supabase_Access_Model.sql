-- Align the remote database with the documented Spring Boot-only access model:
-- API roles have no direct table access and RLS is defense in depth without
-- application policies because readyroad_app owns the application tables.

DO $$
DECLARE
    target record;
    api_role text;
BEGIN
    FOR target IN
        SELECT schemaname, tablename
        FROM pg_policies
        WHERE schemaname = current_schema()
          AND policyname = 'readyroad_app_full_access'
    LOOP
        EXECUTE format(
            'DROP POLICY IF EXISTS readyroad_app_full_access ON %I.%I',
            target.schemaname,
            target.tablename
        );
    END LOOP;

    FOREACH api_role IN ARRAY ARRAY['anon', 'authenticated', 'service_role']
    LOOP
        IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname = api_role) THEN
            EXECUTE format('REVOKE ALL ON SCHEMA %I FROM %I', current_schema(), api_role);
            EXECUTE format('REVOKE ALL ON ALL TABLES IN SCHEMA %I FROM %I', current_schema(), api_role);
            EXECUTE format('REVOKE ALL ON ALL SEQUENCES IN SCHEMA %I FROM %I', current_schema(), api_role);
        END IF;
    END LOOP;
END;
$$;

-- These public forms submit through SECURITY DEFINER RPCs; direct table access
-- would expose submitted personal data and is not part of the client contract.
DO $$
BEGIN
    IF to_regclass('public.contact_messages') IS NOT NULL THEN
        REVOKE ALL ON TABLE public.contact_messages FROM anon, authenticated;
    END IF;

    IF to_regclass('public.intake_registrations') IS NOT NULL THEN
        REVOKE ALL ON TABLE public.intake_registrations FROM anon, authenticated;
    END IF;

    IF to_regclass('public.newsletter_subscribers') IS NOT NULL THEN
        REVOKE ALL ON TABLE public.newsletter_subscribers FROM anon, authenticated;
    END IF;
END;
$$;
