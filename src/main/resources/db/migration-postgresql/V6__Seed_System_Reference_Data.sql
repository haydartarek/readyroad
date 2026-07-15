-- Reference rows owned by Flyway. Canonical lessons and all traffic-sign
-- content are intentionally imported by their existing startup services.

INSERT INTO categories (
    id, code, name_ar, name_en, name_nl, name_fr,
    description_ar, description_en, description_nl, description_fr,
    display_order, is_active, created_at, updated_at
) VALUES
    (1, 'A', 'علامات الخطر', 'Danger Signs', 'Gevaarsborden', 'Panneaux de danger', 'علامات تحذيرية للإشارة إلى المخاطر على الطريق', 'Warning signs indicating road hazards', 'Waarschuwingsborden voor gevaren op de weg', 'Panneaux d''avertissement des dangers sur la route', 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'B', 'علامات الأولوية', 'Priority Signs', 'Voorrangsborden', 'Panneaux de priorité', 'علامات تحدد حق الأولوية على الطريق', 'Signs determining priority on the road', 'Borden die voorrang op de weg bepalen', 'Panneaux déterminant la priorité sur la route', 2, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'C', 'علامات المنع', 'Prohibition Signs', 'Verbodsborden', 'Panneaux d''interdiction', 'علامات تمنع أو تحظر إجراءات معينة', 'Signs prohibiting certain actions', 'Borden die bepaalde handelingen verbieden', 'Panneaux interdisant certaines actions', 3, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 'D', 'العلامات الإجبارية', 'Mandatory Signs', 'Gebodsborden', 'Panneaux d''obligation', 'علامات تفرض سلوكاً معيناً', 'Signs imposing specific behavior', 'Borden die specifiek gedrag opleggen', 'Panneaux imposant un comportement spécifique', 4, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 'E', 'علامات الوقوف والتوقف', 'Parking and Standing Signs', 'Parkeer- en stilstaanborden', 'Panneaux de stationnement et d''arrêt', 'علامات تنظم الوقوف والانتظار', 'Signs regulating stopping and parking', 'Borden die stilstaan en parkeren regelen', 'Panneaux réglementant l''arrêt et le stationnement', 5, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (6, 'F', 'العلامات الإرشادية', 'Information Signs', 'Aanwijzingsborden', 'Panneaux d''indication', 'علامات توفر معلومات ودلالات', 'Signs providing information and directions', 'Borden die informatie en aanwijzingen geven', 'Panneaux fournissant des informations et des indications', 6, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (7, 'G', 'العلامات التكميلية', 'Supplementary Signs', 'Onderborden', 'Panneaux complémentaires', 'إشارات إضافية توضيحية', 'Additional information signs', 'Aanvullende informatieborden', 'Panneaux d''information supplémentaires', 7, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (8, 'Z', 'علامات المناطق المرورية', 'Zone Signs', 'Zoneborden', 'Panneaux de zone', 'إشارات المناطق الخاصة', 'Special zone signs', 'Speciale zoneborden', 'Panneaux de zone spéciaux', 8, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (9, 'M', 'علامات الدراجات والدراجات النارية', 'Cyclist & Moped Advisory Signs', 'Fietsersborden', 'Panneaux cyclistes et vélomoteurs', 'لوحات خاصة بالدراجات والدراجات النارية', 'Advisory and regulatory signs for cyclists and moped riders', 'Borden specifiek voor fietsen en bromfietsen', 'Panneaux additionnels spécifiques aux vélos et cyclomoteurs', 9, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (17, 'H', 'علامات المعلومات والإجراءات المرورية المؤقتة', 'Information and Temporary Traffic Signs', 'Informatieborden en tijdelijke verkeersmaatregelen', 'Panneaux d''information et mesures de circulation temporaires', 'علامات المعلومات والإجراءات المرورية المؤقتة', 'Information and temporary traffic measure signs', 'Informatieborden en tijdelijke verkeersmaatregelen', 'Panneaux d''information et mesures de circulation temporaires', 10, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (28, 'T', 'علامات التحديد', 'Delineation Signs', 'Afbakeningsborden', 'Panneaux de balisage', NULL, 'Road delineation and guidance markers (TYPE-I, TYPE-II, TYPE-V, MARK)', NULL, NULL, 10, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO user_roles (id, role_name, description, permissions, created_at) VALUES
    (1, 'USER', 'Standard learner user', '{"quiz":true,"profile":true,"progress":true}', CURRENT_TIMESTAMP),
    (2, 'ADMIN', 'Full system administrator', '{"all":true}', CURRENT_TIMESTAMP),
    (3, 'INSTRUCTOR', 'Lesson content manager', '{"lessons":true,"questions":true,"users:read":true}', CURRENT_TIMESTAMP);

INSERT INTO admin_system_settings (
    id, site_name, default_language, maintenance_mode, allow_registrations,
    exam_questions, exam_duration_minutes, passing_score_percent, created_at, updated_at
) VALUES (
    1, 'ReadyRoad', 'en', FALSE, TRUE,
    50, 30, 82, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

SELECT setval(pg_get_serial_sequence('categories', 'id'), COALESCE(MAX(id), 1), TRUE) FROM categories;
SELECT setval(pg_get_serial_sequence('user_roles', 'id'), COALESCE(MAX(id), 1), TRUE) FROM user_roles;
SELECT setval(pg_get_serial_sequence('admin_system_settings', 'id'), COALESCE(MAX(id), 1), TRUE) FROM admin_system_settings;
