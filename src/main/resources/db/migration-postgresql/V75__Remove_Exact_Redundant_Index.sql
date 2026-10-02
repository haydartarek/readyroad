-- Remove the only exact duplicate found during index inspection. The unique
-- constraint index already covers the same key columns and ordering.

DO $$
BEGIN
    EXECUTE format(
        'DROP INDEX IF EXISTS %I.%I',
        current_schema(),
        'idx_agent_task_attempts_task'
    );
END;
$$;
