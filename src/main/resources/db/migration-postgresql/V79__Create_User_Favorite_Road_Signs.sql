-- Favorites are owned by a user and reference canonical road_signs IDs.
-- No existing user data is changed. Deleting a user/sign cleans up dependent favorites.
CREATE TABLE user_favorite_road_signs (
    user_id BIGINT NOT NULL,
    road_sign_id BIGINT NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_user_favorite_road_signs PRIMARY KEY (user_id, road_sign_id),
    CONSTRAINT fk_user_favorite_road_signs_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_favorite_road_signs_sign FOREIGN KEY (road_sign_id)
        REFERENCES road_signs(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_favorite_road_signs_sign
    ON user_favorite_road_signs (road_sign_id);
