-- Add the exact leading-column indexes requested by the Supabase advisor for
-- foreign keys that currently have no covering index.

DO $$
DECLARE
    target record;
    index_name text;
    column_list text;
BEGIN
    FOR target IN
        SELECT
            con.conrelid,
            con.conkey,
            n.nspname AS schema_name,
            c.relname AS table_name,
            con.conname
        FROM pg_constraint con
        JOIN pg_class c ON c.oid = con.conrelid
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE con.contype = 'f'
          AND n.nspname = current_schema()
          AND NOT EXISTS (
              SELECT 1
              FROM pg_index i
              WHERE i.indrelid = con.conrelid
                AND i.indisvalid
                AND (i.indkey::smallint[])[0:cardinality(con.conkey) - 1] = con.conkey
          )
    LOOP
        SELECT string_agg(format('%I', a.attname), ', ' ORDER BY keys.ordinality)
        INTO column_list
        FROM unnest(target.conkey) WITH ORDINALITY AS keys(attnum, ordinality)
        JOIN pg_attribute a
          ON a.attrelid = target.conrelid
         AND a.attnum = keys.attnum;

        index_name := left(
            format('idx_fk_%s_%s', target.table_name, substr(md5(target.conname), 1, 12)),
            63
        );

        EXECUTE format(
            'CREATE INDEX IF NOT EXISTS %I ON %I.%I (%s)',
            index_name,
            target.schema_name,
            target.table_name,
            column_list
        );
    END LOOP;
END;
$$;
