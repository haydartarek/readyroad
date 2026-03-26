-- V181: Add SIGN_PLACEMENT_DISTANCE to sign_questions.question_type ENUM
--       Add ROAD_MANAGEMENT to road_signs.category ENUM

ALTER TABLE sign_questions
    MODIFY question_type ENUM(
        'WHAT_DOES_IT_MEAN',
        'WHICH_SIGN',
        'WHAT_MUST_YOU_DO',
        'IS_IT_ALLOWED',
        'HAZARD_IDENTIFICATION',
        'DRIVER_ACTION',
        'SIGN_PLACEMENT_DISTANCE'
    ) NOT NULL;

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
