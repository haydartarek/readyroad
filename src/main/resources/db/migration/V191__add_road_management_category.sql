-- Add ROAD_MANAGEMENT to road_signs category ENUM
ALTER TABLE road_signs
  MODIFY COLUMN category ENUM(
    'DANGER',
    'PRIORITY',
    'PROHIBITION',
    'MANDATORY',
    'PARKING',
    'INFORMATION',
    'ADDITIONAL',
    'CYCLIST',
    'DELINEATION',
    'ZONE',
    'ROAD_MANAGEMENT'
  ) NOT NULL;
