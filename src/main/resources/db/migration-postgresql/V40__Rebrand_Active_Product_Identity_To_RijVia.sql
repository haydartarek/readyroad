UPDATE admin_system_settings
SET site_name = 'RijVia'
WHERE site_name = 'ReadyRoad';

UPDATE agent_definitions
SET display_name = REPLACE(display_name, 'ReadyRoad', 'RijVia'),
    description = REPLACE(description, 'ReadyRoad', 'RijVia'),
    updated_at = CURRENT_TIMESTAMP
WHERE display_name LIKE '%ReadyRoad%'
   OR description LIKE '%ReadyRoad%';

UPDATE marketing_positioning
SET statement = REPLACE(statement, 'ReadyRoad', 'RijVia'),
    updated_at = CURRENT_TIMESTAMP,
    version = version + 1
WHERE active = TRUE
  AND statement LIKE '%ReadyRoad%';

UPDATE marketing_content_pillars
SET name = REPLACE(name, 'ReadyRoad', 'RijVia'),
    updated_at = CURRENT_TIMESTAMP,
    version = version + 1
WHERE active = TRUE
  AND name LIKE '%ReadyRoad%';
