-- V180: Add HAZARD_IDENTIFICATION and DRIVER_ACTION to sign_questions.question_type ENUM
ALTER TABLE sign_questions
    MODIFY question_type ENUM(
        'WHAT_DOES_IT_MEAN',
        'WHICH_SIGN',
        'WHAT_MUST_YOU_DO',
        'IS_IT_ALLOWED',
        'HAZARD_IDENTIFICATION',
        'DRIVER_ACTION'
    ) NOT NULL;
