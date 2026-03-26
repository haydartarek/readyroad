ALTER TABLE quiz_questions
    ADD COLUMN road_sign_id BIGINT NULL,
    ADD CONSTRAINT fk_quiz_questions_road_sign
        FOREIGN KEY (road_sign_id) REFERENCES road_signs(id)
        ON DELETE SET NULL;

ALTER TABLE user_weak_areas
    ADD COLUMN road_sign_id BIGINT NULL,
    ADD CONSTRAINT fk_user_weak_areas_road_sign
        FOREIGN KEY (road_sign_id) REFERENCES road_signs(id)
        ON DELETE SET NULL;
