-- Query-supporting indexes. Redundant MySQL indexes that duplicate a unique
-- constraint or another identical index are intentionally not recreated.

CREATE INDEX idx_users_active ON users (is_active);
CREATE INDEX idx_users_role ON users (role);
CREATE INDEX idx_auth_identities_provider_email ON auth_identities (provider, provider_email);
CREATE INDEX idx_prt_user_id ON password_reset_tokens (user_id);
CREATE INDEX idx_import_history_performed_at ON import_history (performed_at DESC);
CREATE INDEX idx_import_history_type ON import_history (import_type);
CREATE INDEX idx_notifications_created_at ON notifications (created_at);
CREATE INDEX idx_notifications_user_unread ON notifications (user_id, is_read);
CREATE INDEX idx_achievement_achieved_at ON achievements (achieved_at);
CREATE INDEX idx_categories_active ON categories (is_active);
CREATE INDEX idx_lessons_active ON lessons (is_active);
CREATE INDEX idx_lessons_display_order ON lessons (display_order);
CREATE INDEX idx_traffic_rules_category ON traffic_rules (category_id);
CREATE INDEX idx_traffic_rules_importance ON traffic_rules (importance_level);
CREATE INDEX idx_traffic_rules_active ON traffic_rules (is_active);
CREATE INDEX idx_dev_exam_questions_category_difficulty ON dev_exam_questions (category_id, difficulty);
CREATE INDEX idx_dev_exam_choices_question ON dev_exam_choices (question_id);

CREATE INDEX idx_road_signs_category ON road_signs (category);
CREATE INDEX idx_road_signs_active ON road_signs (is_active);
CREATE INDEX idx_quiz_questions_category ON quiz_questions (category_id);
CREATE INDEX idx_quiz_questions_road_sign ON quiz_questions (road_sign_id);
CREATE INDEX idx_quiz_questions_status ON quiz_questions (status);
CREATE INDEX idx_quiz_questions_published_sign ON quiz_questions (road_sign_id)
    WHERE status = 'PUBLISHED' AND road_sign_id IS NOT NULL;
CREATE INDEX idx_quiz_attempts_user ON quiz_attempts (user_id);
CREATE INDEX idx_quiz_attempts_completed ON quiz_attempts (completed_at);
CREATE INDEX idx_quiz_attempts_type ON quiz_attempts (quiz_type);
CREATE INDEX idx_quiz_attempts_user_completed ON quiz_attempts (user_id, completed_at);
CREATE INDEX idx_quiz_user_answers_question ON quiz_user_answers (question_type, question_ref_id);
CREATE INDEX idx_quiz_user_answers_lookup ON quiz_user_answers (attempt_id, question_type, question_ref_id);
CREATE INDEX idx_exam_questions_category ON exam_questions (category_id);
CREATE INDEX idx_exam_questions_difficulty ON exam_questions (difficulty);
CREATE INDEX idx_exam_questions_important ON exam_questions (is_important);
CREATE INDEX idx_exam_questions_active ON exam_questions (is_active);
CREATE INDEX idx_exam_questions_pool ON exam_questions (category_id, difficulty, is_active);
CREATE INDEX idx_exam_user_started ON exam_simulations (user_id, started_at DESC);
CREATE INDEX idx_exam_status_completed ON exam_simulations (status, completed_at DESC);
CREATE INDEX idx_exam_user_status ON exam_simulations (user_id, status);
CREATE INDEX idx_exam_sim_questions_question ON exam_simulation_questions (question_id);
CREATE INDEX idx_exam_answers_option ON exam_simulation_answers (selected_option_id);
CREATE INDEX idx_exam_answers_correct ON exam_simulation_answers (exam_id, is_correct);
CREATE INDEX idx_exam_answers_time ON exam_simulation_answers (exam_id, time_taken_seconds);
CREATE INDEX idx_exam_answers_question ON exam_simulation_answers (question_id, is_correct);
CREATE INDEX idx_ucp_user_accuracy ON user_category_progress (user_id, accuracy_rate);
CREATE INDEX idx_ucp_user_mastery ON user_category_progress (user_id, mastery_level);
CREATE INDEX idx_ucp_user_practiced ON user_category_progress (user_id, last_practiced DESC);
CREATE INDEX idx_ucp_category_accuracy ON user_category_progress (category_id, accuracy_rate DESC);
CREATE INDEX idx_uep_error_type ON user_error_patterns (error_type);
CREATE INDEX idx_uep_question_ref ON user_error_patterns (question_type, question_ref_type, question_ref_id);
CREATE INDEX idx_uep_user_date ON user_error_patterns (user_id, occurred_at);
CREATE INDEX idx_uep_user_error ON user_error_patterns (user_id, error_type);
CREATE INDEX idx_uqh_answered_at ON user_question_history (answered_at);
CREATE INDEX idx_uqh_question_stats ON user_question_history (question_type, question_ref_id);
CREATE INDEX idx_uqh_times_wrong ON user_question_history (times_wrong);
CREATE INDEX idx_uqh_question_id ON user_question_history (question_id);
CREATE INDEX idx_uqh_user_answered ON user_question_history (user_id, answered_at);
CREATE INDEX idx_uqh_question_answered ON user_question_history (question_id, answered_at);
CREATE INDEX idx_uqh_lookup ON user_question_history (user_id, question_id, answered_at);
CREATE INDEX idx_uqh_performance ON user_question_history (user_id, last_shown_at, is_correct);
CREATE INDEX idx_uqh_recent ON user_question_history (user_id, last_shown_at);
CREATE INDEX idx_uwa_road_sign ON user_weak_areas (road_sign_id);
CREATE INDEX idx_uwa_category_accuracy ON user_weak_areas (category, accuracy_percentage);
CREATE INDEX idx_uwa_user_accuracy ON user_weak_areas (user_id, accuracy_percentage);

CREATE INDEX idx_sign_questions_sign ON sign_questions (sign_id);
CREATE INDEX idx_sign_questions_type_difficulty ON sign_questions (question_type, difficulty);
CREATE INDEX idx_sign_questions_difficulty ON sign_questions (difficulty);
CREATE INDEX idx_sign_choices_correct ON sign_choices (is_correct);
CREATE INDEX idx_sign_exam_questions_question ON sign_exam_questions (question_id);
CREATE INDEX idx_sign_exam_results_user_sign ON sign_exam_results (user_id, sign_id);
CREATE INDEX idx_sign_exam_results_passed ON sign_exam_results (passed);
CREATE INDEX idx_sign_exam_results_completed ON sign_exam_results (completed_at DESC);
CREATE INDEX idx_sign_import_runs_created ON sign_import_runs (created_at DESC);
CREATE INDEX idx_sign_import_runs_status ON sign_import_runs (status);
CREATE INDEX idx_sign_practice_sessions_user_sign ON sign_practice_sessions (user_id, sign_id);
CREATE INDEX idx_sign_practice_sessions_user_status ON sign_practice_sessions (user_id, status);
CREATE INDEX idx_sign_practice_answers_question ON sign_practice_answers (question_id);
CREATE INDEX idx_sign_practice_answers_choice ON sign_practice_answers (choice_id);
CREATE INDEX idx_srps_user_status ON sign_random_practice_sessions (user_id, status);
CREATE INDEX idx_srps_user_started ON sign_random_practice_sessions (user_id, started_at DESC);
CREATE INDEX idx_srps_status_started ON sign_random_practice_sessions (status, started_at DESC);
CREATE INDEX idx_srps_completed_at ON sign_random_practice_sessions (completed_at DESC);
CREATE INDEX idx_srpq_question ON sign_random_practice_questions (question_id);
CREATE INDEX idx_srpq_selected_choice ON sign_random_practice_questions (selected_choice_id);
CREATE INDEX idx_srpq_answered ON sign_random_practice_questions (answered_at);

-- Preserve MySQL ON UPDATE CURRENT_TIMESTAMP semantics without relying on JPA.
CREATE OR REPLACE FUNCTION set_updated_at_column()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.updated_at IS NOT DISTINCT FROM OLD.updated_at THEN
        NEW.updated_at = CURRENT_TIMESTAMP;
    END IF;
    RETURN NEW;
END;
$$;

CREATE OR REPLACE FUNCTION set_last_updated_column()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.last_updated IS NOT DISTINCT FROM OLD.last_updated THEN
        NEW.last_updated = CURRENT_TIMESTAMP;
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION set_updated_at_column();
CREATE TRIGGER trg_auth_identities_updated_at BEFORE UPDATE ON auth_identities
    FOR EACH ROW EXECUTE FUNCTION set_updated_at_column();
CREATE TRIGGER trg_admin_settings_updated_at BEFORE UPDATE ON admin_system_settings
    FOR EACH ROW EXECUTE FUNCTION set_updated_at_column();
CREATE TRIGGER trg_import_history_updated_at BEFORE UPDATE ON import_history
    FOR EACH ROW EXECUTE FUNCTION set_updated_at_column();
CREATE TRIGGER trg_lessons_updated_at BEFORE UPDATE ON lessons
    FOR EACH ROW EXECUTE FUNCTION set_updated_at_column();
CREATE TRIGGER trg_lesson_pages_updated_at BEFORE UPDATE ON lesson_pages
    FOR EACH ROW EXECUTE FUNCTION set_updated_at_column();
CREATE TRIGGER trg_traffic_rules_updated_at BEFORE UPDATE ON traffic_rules
    FOR EACH ROW EXECUTE FUNCTION set_updated_at_column();
CREATE TRIGGER trg_quiz_questions_updated_at BEFORE UPDATE ON quiz_questions
    FOR EACH ROW EXECUTE FUNCTION set_updated_at_column();
CREATE TRIGGER trg_quiz_attempts_updated_at BEFORE UPDATE ON quiz_attempts
    FOR EACH ROW EXECUTE FUNCTION set_updated_at_column();
CREATE TRIGGER trg_exam_questions_updated_at BEFORE UPDATE ON exam_questions
    FOR EACH ROW EXECUTE FUNCTION set_updated_at_column();
CREATE TRIGGER trg_exam_simulations_updated_at BEFORE UPDATE ON exam_simulations
    FOR EACH ROW EXECUTE FUNCTION set_updated_at_column();
CREATE TRIGGER trg_exam_sim_questions_updated_at BEFORE UPDATE ON exam_simulation_questions
    FOR EACH ROW EXECUTE FUNCTION set_updated_at_column();
CREATE TRIGGER trg_exam_sim_answers_updated_at BEFORE UPDATE ON exam_simulation_answers
    FOR EACH ROW EXECUTE FUNCTION set_updated_at_column();
CREATE TRIGGER trg_ucp_updated_at BEFORE UPDATE ON user_category_progress
    FOR EACH ROW EXECUTE FUNCTION set_updated_at_column();
CREATE TRIGGER trg_ulp_updated_at BEFORE UPDATE ON user_lesson_progress
    FOR EACH ROW EXECUTE FUNCTION set_updated_at_column();
CREATE TRIGGER trg_uqh_updated_at BEFORE UPDATE ON user_question_history
    FOR EACH ROW EXECUTE FUNCTION set_updated_at_column();
CREATE TRIGGER trg_road_signs_updated_at BEFORE UPDATE ON road_signs
    FOR EACH ROW EXECUTE FUNCTION set_updated_at_column();
CREATE TRIGGER trg_sign_questions_updated_at BEFORE UPDATE ON sign_questions
    FOR EACH ROW EXECUTE FUNCTION set_updated_at_column();
CREATE TRIGGER trg_sign_exams_updated_at BEFORE UPDATE ON sign_exams
    FOR EACH ROW EXECUTE FUNCTION set_updated_at_column();
CREATE TRIGGER trg_srps_updated_at BEFORE UPDATE ON sign_random_practice_sessions
    FOR EACH ROW EXECUTE FUNCTION set_updated_at_column();
CREATE TRIGGER trg_uwa_last_updated BEFORE UPDATE ON user_weak_areas
    FOR EACH ROW EXECUTE FUNCTION set_last_updated_column();
