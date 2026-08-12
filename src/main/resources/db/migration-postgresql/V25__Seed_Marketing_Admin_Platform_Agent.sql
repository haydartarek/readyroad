INSERT INTO agent_definitions (agent_type, display_name, description, enabled)
VALUES (
    'ADMIN_PLATFORM',
    'ReadyRoad Marketing Admin Platform',
    'Executes approved administrative control tasks for the marketing platform.',
    TRUE
)
ON CONFLICT (agent_type) DO NOTHING;
