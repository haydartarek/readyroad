-- V8__Add_Quiz_Questions.sql
-- إضافة أسئلة الاختبار
-- Quiz Questions Data
-- Generated: 2026-01-14T15:41:57.397285
-- Total Questions: 504

-- ========================================
-- إدراج أسئلة الاختبار
-- Insert Quiz Questions
-- ========================================

INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  1,
  'ما هي العلامة المرورية A1a؟',
  'What does the traffic sign A1a mean?',
  'Wat betekent verkeersbord A1a?',
  'Que signifie le panneau de signalisation A1a?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A1a' LIMIT 1),
  'العلامة A1a تعني: منعطف خطر لليسار',
  'Sign A1a means: Dangerous bend to the left',
  'Bord A1a betekent: Gevaarlijke bocht naar links.',
  'Le panneau A1a signifie: Virage dangereux à gauche',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1,
  1,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van rijwielen.',
  'Accès interdit',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  2,
  1,
  'علامة C46',
  'Einde van alle plaatselijke verbodsbepalingen opgelegd aan de voertuigen in beweging.',
  'Einde van alle plaatselijke verbodsbepalingen opgelegd aan de voertuigen in beweging.',
  'Einde van alle plaatselijke verbodsbepalingen opgelegd aan de voertuigen in beweging.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  3,
  1,
  'طريق سريع',
  'Motorway',
  'Einde autosnelweg.',
  'Autoroute',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  4,
  1,
  'منعطف خطر لليسار',
  'Dangerous bend to the left',
  'Gevaarlijke bocht naar links.',
  'Virage dangereux à gauche',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  2,
  'إلى أي فئة تنتمي العلامة A1a؟',
  'Which category does sign A1a belong to?',
  'Tot welke categorie behoort bord A1a?',
  'À quelle catégorie appartient le panneau A1a?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A1a' LIMIT 1),
  'العلامة A1a تنتمي إلى فئة علامات الخطر',
  'Sign A1a belongs to Danger Signs',
  'Bord A1a behoort tot Gevaarborden',
  'Le panneau A1a appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  5,
  2,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  6,
  2,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  7,
  2,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  8,
  2,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  3,
  'العلامة A1a تعني: منعطف خطر لليسار. صحيح أم خطأ؟',
  'Sign A1a means: Dangerous bend to the left. True or False?',
  'Bord A1a betekent: Gevaarlijke bocht naar links.. Waar of Onwaar?',
  'Le panneau A1a signifie: Virage dangereux à gauche. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A1a' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  9,
  3,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  10,
  3,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  4,
  'العلامة A1b تعني: منعطف خطر لليمين. صحيح أم خطأ؟',
  'Sign A1b means: Dangerous bend to the right. True or False?',
  'Bord A1b betekent: Gevaarlijke bocht naar rechts.. Waar of Onwaar?',
  'Le panneau A1b signifie: Virage dangereux à droite. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A1b' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  11,
  4,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  12,
  4,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  5,
  'إلى أي فئة تنتمي العلامة A1b؟',
  'Which category does sign A1b belong to?',
  'Tot welke categorie behoort bord A1b?',
  'À quelle catégorie appartient le panneau A1b?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A1b' LIMIT 1),
  'العلامة A1b تنتمي إلى فئة علامات الخطر',
  'Sign A1b belongs to Danger Signs',
  'Bord A1b behoort tot Gevaarborden',
  'Le panneau A1b appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  13,
  5,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  14,
  5,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  15,
  5,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  16,
  5,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  6,
  'ما هي العلامة المرورية A1b؟',
  'What does the traffic sign A1b mean?',
  'Wat betekent verkeersbord A1b?',
  'Que signifie le panneau de signalisation A1b?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A1b' LIMIT 1),
  'العلامة A1b تعني: منعطف خطر لليمين',
  'Sign A1b means: Dangerous bend to the right',
  'Bord A1b betekent: Gevaarlijke bocht naar rechts.',
  'Le panneau A1b signifie: Virage dangereux à droite',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  17,
  6,
  'منعطفات خطرة، الأول لليسار',
  'Dangerous double or multiple bends, first to the left',
  'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar links.',
  'Virages dangereux, le premier à gauche',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  18,
  6,
  'منعطف خطر لليمين',
  'Dangerous bend to the right',
  'Gevaarlijke bocht naar rechts.',
  'Virage dangereux à droite',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  19,
  6,
  'علامة F77',
  'Toeristische informatie.',
  'Toeristische informatie.',
  'Toeristische informatie.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  20,
  6,
  'علامة F62',
  'Noodtelefoon.',
  'Noodtelefoon.',
  'Noodtelefoon.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  7,
  'إلى أي فئة تنتمي العلامة A1c؟',
  'Which category does sign A1c belong to?',
  'Tot welke categorie behoort bord A1c?',
  'À quelle catégorie appartient le panneau A1c?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A1c' LIMIT 1),
  'العلامة A1c تنتمي إلى فئة علامات الخطر',
  'Sign A1c belongs to Danger Signs',
  'Bord A1c behoort tot Gevaarborden',
  'Le panneau A1c appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  21,
  7,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  22,
  7,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  23,
  7,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  24,
  7,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  8,
  'العلامة A1c تعني: منعطفات خطرة، الأول لليسار. صحيح أم خطأ؟',
  'Sign A1c means: Dangerous double or multiple bends, first to the left. True or False?',
  'Bord A1c betekent: Gevaarlijke dubbele of meer dan twee bochten, de eerste naar links.. Waar of Onwaar?',
  'Le panneau A1c signifie: Virages dangereux, le premier à gauche. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A1c' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  25,
  8,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  26,
  8,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  9,
  'ما هي العلامة المرورية A1d؟',
  'What does the traffic sign A1d mean?',
  'Wat betekent verkeersbord A1d?',
  'Que signifie le panneau de signalisation A1d?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A1d' LIMIT 1),
  'العلامة A1d تعني: منعطفات خطرة، الأول لليمين',
  'Sign A1d means: Dangerous double or multiple bends, first to the right',
  'Bord A1d betekent: Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts.',
  'Le panneau A1d signifie: Virages dangereux, le premier à droite',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  27,
  9,
  'منعطفات خطرة، الأول لليمين',
  'Dangerous double or multiple bends, first to the right',
  'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts.',
  'Virages dangereux, le premier à droite',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  28,
  9,
  'علامة M20',
  'Enkel voor fietsers en speed pedelecs.',
  'Enkel voor fietsers en speed pedelecs.',
  'Enkel voor fietsers en speed pedelecs.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  29,
  9,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van autocars.',
  'Accès interdit',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  30,
  9,
  'علامة B23',
  'Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  10,
  'العلامة A1d تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign A1d means: This sign is optional. True or False?',
  'Bord A1d betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau A1d signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A1d' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  31,
  10,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  32,
  10,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  11,
  'العلامة A3 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign A3 means: This sign is optional. True or False?',
  'Bord A3 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau A3 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A3' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  33,
  11,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  34,
  11,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  12,
  'ما هي العلامة المرورية A3؟',
  'What does the traffic sign A3 mean?',
  'Wat betekent verkeersbord A3?',
  'Que signifie le panneau de signalisation A3?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A3' LIMIT 1),
  'العلامة A3 تعني: انحدار خطر',
  'Sign A3 means: Dangerous descent',
  'Bord A3 betekent: Gevaarlijke daling.',
  'Le panneau A3 signifie: Descente dangereuse',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  35,
  12,
  'علامة F29',
  'Wegwijzer',
  'Wegwijzer',
  'Wegwijzer',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  36,
  12,
  'انحدار خطر',
  'Dangerous descent',
  'Gevaarlijke daling.',
  'Descente dangereuse',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  37,
  12,
  'أعط الأولوية',
  'Give way',
  'Voorrang verlenen',
  'Cédez le passage',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  38,
  12,
  'علامة B15g',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  13,
  'إلى أي فئة تنتمي العلامة A5؟',
  'Which category does sign A5 belong to?',
  'Tot welke categorie behoort bord A5?',
  'À quelle catégorie appartient le panneau A5?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A5' LIMIT 1),
  'العلامة A5 تنتمي إلى فئة علامات الخطر',
  'Sign A5 belongs to Danger Signs',
  'Bord A5 behoort tot Gevaarborden',
  'Le panneau A5 appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  39,
  13,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  40,
  13,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  41,
  13,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  42,
  13,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  14,
  'العلامة A5 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign A5 means: This sign is optional. True or False?',
  'Bord A5 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau A5 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A5' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  43,
  14,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  44,
  14,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  15,
  'ما هي العلامة المرورية A5؟',
  'What does the traffic sign A5 mean?',
  'Wat betekent verkeersbord A5?',
  'Que signifie le panneau de signalisation A5?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A5' LIMIT 1),
  'العلامة A5 تعني: صعود خطر',
  'Sign A5 means: Dangerous ascent',
  'Bord A5 betekent: Gevaarlijke helling.',
  'Le panneau A5 signifie: Montée dangereuse',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  45,
  15,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van autocars.',
  'Accès interdit',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  46,
  15,
  'صعود خطر',
  'Dangerous ascent',
  'Gevaarlijke helling.',
  'Montée dangereuse',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  47,
  15,
  'علامة F13',
  'Rijstrook keuze.',
  'Rijstrook keuze.',
  'Rijstrook keuze.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  48,
  15,
  'علامة F56',
  'Brandblusapparaat.',
  'Brandblusapparaat.',
  'Brandblusapparaat.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  16,
  'العلامة A7a تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign A7a means: This sign is optional. True or False?',
  'Bord A7a betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau A7a signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A7a' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  49,
  16,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  50,
  16,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  17,
  'ما هي العلامة المرورية A7a؟',
  'What does the traffic sign A7a mean?',
  'Wat betekent verkeersbord A7a?',
  'Que signifie le panneau de signalisation A7a?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A7a' LIMIT 1),
  'العلامة A7a تعني: تضييق الطريق',
  'Sign A7a means: Road narrowing',
  'Bord A7a betekent: Rijbaanversmalling',
  'Le panneau A7a signifie: Rétrécissement de chaussée',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  51,
  17,
  'علامة M19',
  'Enkel voor speed pedelecs.',
  'Enkel voor speed pedelecs.',
  'Enkel voor speed pedelecs.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  52,
  17,
  'علامة A27',
  'Overstekend groot wild.',
  'Overstekend groot wild.',
  'Overstekend groot wild.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  53,
  17,
  'علامة A31',
  'Werken.',
  'Werken.',
  'Werken.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  54,
  17,
  'تضييق الطريق',
  'Road narrowing',
  'Rijbaanversmalling',
  'Rétrécissement de chaussée',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  18,
  'إلى أي فئة تنتمي العلامة A7b؟',
  'Which category does sign A7b belong to?',
  'Tot welke categorie behoort bord A7b?',
  'À quelle catégorie appartient le panneau A7b?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A7b' LIMIT 1),
  'العلامة A7b تنتمي إلى فئة علامات الخطر',
  'Sign A7b belongs to Danger Signs',
  'Bord A7b behoort tot Gevaarborden',
  'Le panneau A7b appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  55,
  18,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  56,
  18,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  57,
  18,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  58,
  18,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  19,
  'ما هي العلامة المرورية A7b؟',
  'What does the traffic sign A7b mean?',
  'Wat betekent verkeersbord A7b?',
  'Que signifie le panneau de signalisation A7b?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A7b' LIMIT 1),
  'العلامة A7b تعني: تضييق الطريق',
  'Sign A7b means: Road narrowing',
  'Bord A7b betekent: Rijbaanversmalling links',
  'Le panneau A7b signifie: Rétrécissement de chaussée',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  59,
  19,
  'علامة A31',
  'Werken.',
  'Werken.',
  'Werken.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  60,
  19,
  'تضييق الطريق',
  'Road narrowing',
  'Rijbaanversmalling links',
  'Rétrécissement de chaussée',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  61,
  19,
  'علامة F1b',
  'Begin van een bebouwde kom.',
  'Begin van een bebouwde kom.',
  'Begin van een bebouwde kom.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  62,
  19,
  'علامة M17',
  'Fietsers en speed pedelecs mogen in 2 richtingen.',
  'Fietsers en speed pedelecs mogen in 2 richtingen.',
  'Fietsers en speed pedelecs mogen in 2 richtingen.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  20,
  'العلامة A7c تعني: تضييق الطريق. صحيح أم خطأ؟',
  'Sign A7c means: Road narrowing. True or False?',
  'Bord A7c betekent: Rijbaanversmalling rechts. Waar of Onwaar?',
  'Le panneau A7c signifie: Rétrécissement de chaussée. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A7c' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  63,
  20,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  64,
  20,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  21,
  'إلى أي فئة تنتمي العلامة A7c؟',
  'Which category does sign A7c belong to?',
  'Tot welke categorie behoort bord A7c?',
  'À quelle catégorie appartient le panneau A7c?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A7c' LIMIT 1),
  'العلامة A7c تنتمي إلى فئة علامات الخطر',
  'Sign A7c belongs to Danger Signs',
  'Bord A7c behoort tot Gevaarborden',
  'Le panneau A7c appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  65,
  21,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  66,
  21,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  67,
  21,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  68,
  21,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  22,
  'ما هي العلامة المرورية A7c؟',
  'What does the traffic sign A7c mean?',
  'Wat betekent verkeersbord A7c?',
  'Que signifie le panneau de signalisation A7c?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A7c' LIMIT 1),
  'العلامة A7c تعني: تضييق الطريق',
  'Sign A7c means: Road narrowing',
  'Bord A7c betekent: Rijbaanversmalling rechts',
  'Le panneau A7c signifie: Rétrécissement de chaussée',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  69,
  22,
  'تضييق الطريق',
  'Road narrowing',
  'Rijbaanversmalling rechts',
  'Rétrécissement de chaussée',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  70,
  22,
  'شارع الدراجات',
  'Cycle street',
  'Einde fietsstraat.',
  'Rue cyclable',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  71,
  22,
  'علامة F55',
  'Hulppost.',
  'Hulppost.',
  'Hulppost.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  72,
  22,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen die gevaarlijke goederen vervoeren.',
  'Accès interdit',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  23,
  'إلى أي فئة تنتمي العلامة A9؟',
  'Which category does sign A9 belong to?',
  'Tot welke categorie behoort bord A9?',
  'À quelle catégorie appartient le panneau A9?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A9' LIMIT 1),
  'العلامة A9 تنتمي إلى فئة علامات الخطر',
  'Sign A9 belongs to Danger Signs',
  'Bord A9 behoort tot Gevaarborden',
  'Le panneau A9 appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  73,
  23,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  74,
  23,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  75,
  23,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  76,
  23,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  24,
  'العلامة A9 تعني: جسر متحرك. صحيح أم خطأ؟',
  'Sign A9 means: Movable bridge. True or False?',
  'Bord A9 betekent: Beweegbare brug.. Waar of Onwaar?',
  'Le panneau A9 signifie: Pont mobile. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A9' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  77,
  24,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  78,
  24,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  25,
  'ما هي العلامة المرورية A11؟',
  'What does the traffic sign A11 mean?',
  'Wat betekent verkeersbord A11?',
  'Que signifie le panneau de signalisation A11?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A11' LIMIT 1),
  'العلامة A11 تعني: طريق يؤدي إلى رصيف أو شاطئ',
  'Sign A11 means: Road leads to quay or waterside',
  'Bord A11 betekent: Uitweg op kaai of oever.',
  'Le panneau A11 signifie: Route menant au quai ou à la rive',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  79,
  25,
  'علامة F23a',
  'Nummer van een gewone weg.',
  'Nummer van een gewone weg.',
  'Nummer van een gewone weg.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  80,
  25,
  'علامة C43',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  81,
  25,
  'علامة M6',
  'Verplichting voor bromfietsen klasse B.',
  'Verplichting voor bromfietsen klasse B.',
  'Verplichting voor bromfietsen klasse B.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  82,
  25,
  'طريق يؤدي إلى رصيف أو شاطئ',
  'Road leads to quay or waterside',
  'Uitweg op kaai of oever.',
  'Route menant au quai ou à la rive',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  26,
  'العلامة A11 تعني: طريق يؤدي إلى رصيف أو شاطئ. صحيح أم خطأ؟',
  'Sign A11 means: Road leads to quay or waterside. True or False?',
  'Bord A11 betekent: Uitweg op kaai of oever.. Waar of Onwaar?',
  'Le panneau A11 signifie: Route menant au quai ou à la rive. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A11' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  83,
  26,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  84,
  26,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  27,
  'إلى أي فئة تنتمي العلامة A11؟',
  'Which category does sign A11 belong to?',
  'Tot welke categorie behoort bord A11?',
  'À quelle catégorie appartient le panneau A11?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A11' LIMIT 1),
  'العلامة A11 تنتمي إلى فئة علامات الخطر',
  'Sign A11 belongs to Danger Signs',
  'Bord A11 behoort tot Gevaarborden',
  'Le panneau A11 appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  85,
  27,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  86,
  27,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  87,
  27,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  88,
  27,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  28,
  'العلامة A13 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign A13 means: This sign is optional. True or False?',
  'Bord A13 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau A13 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A13' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  89,
  28,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  90,
  28,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  29,
  'ما هي العلامة المرورية A13؟',
  'What does the traffic sign A13 mean?',
  'Wat betekent verkeersbord A13?',
  'Que signifie le panneau de signalisation A13?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A13' LIMIT 1),
  'العلامة A13 تعني: حفرة عرضية أو مطب',
  'Sign A13 means: Transverse depression or hump',
  'Bord A13 betekent: Dwarse uitholling of ezelsrug.',
  'Le panneau A13 signifie: Dépression transversale ou dos d''âne',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  91,
  29,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van rijwielen.',
  'Accès interdit',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  92,
  29,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen die gevaarlijke ontvlambare of ontplofbare stoffen vervoeren.',
  'Accès interdit',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  93,
  29,
  'حفرة عرضية أو مطب',
  'Transverse depression or hump',
  'Dwarse uitholling of ezelsrug.',
  'Dépression transversale ou dos d''âne',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  94,
  29,
  'علامة C43',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  30,
  'إلى أي فئة تنتمي العلامة A13؟',
  'Which category does sign A13 belong to?',
  'Tot welke categorie behoort bord A13?',
  'À quelle catégorie appartient le panneau A13?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A13' LIMIT 1),
  'العلامة A13 تنتمي إلى فئة علامات الخطر',
  'Sign A13 belongs to Danger Signs',
  'Bord A13 behoort tot Gevaarborden',
  'Le panneau A13 appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  95,
  30,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  96,
  30,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  97,
  30,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  98,
  30,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  31,
  'ما هي العلامة المرورية A14؟',
  'What does the traffic sign A14 mean?',
  'Wat betekent verkeersbord A14?',
  'Que signifie le panneau de signalisation A14?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A14' LIMIT 1),
  'العلامة A14 تعني: علامة A14',
  'Sign A14 means: Verhoogde inrichting.',
  'Bord A14 betekent: Verhoogde inrichting.',
  'Le panneau A14 signifie: Verhoogde inrichting.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  99,
  31,
  'علامة A14',
  'Verhoogde inrichting.',
  'Verhoogde inrichting.',
  'Verhoogde inrichting.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  100,
  31,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van bromfietsen en fietsen.',
  'Accès interdit',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  101,
  31,
  'علامة D9b',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  102,
  31,
  'معبر للمشاة',
  'Pedestrian crossing',
  'Oversteekplaats voor voetgangers.',
  'Passage pour piétons',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  32,
  'إلى أي فئة تنتمي العلامة A14؟',
  'Which category does sign A14 belong to?',
  'Tot welke categorie behoort bord A14?',
  'À quelle catégorie appartient le panneau A14?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A14' LIMIT 1),
  'العلامة A14 تنتمي إلى فئة علامات الخطر',
  'Sign A14 belongs to Danger Signs',
  'Bord A14 behoort tot Gevaarborden',
  'Le panneau A14 appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  103,
  32,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  104,
  32,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  105,
  32,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  106,
  32,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  33,
  'العلامة A14 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign A14 means: This sign is optional. True or False?',
  'Bord A14 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau A14 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A14' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  107,
  33,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  108,
  33,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  34,
  'إلى أي فئة تنتمي العلامة A15؟',
  'Which category does sign A15 belong to?',
  'Tot welke categorie behoort bord A15?',
  'À quelle catégorie appartient le panneau A15?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A15' LIMIT 1),
  'العلامة A15 تنتمي إلى فئة علامات الخطر',
  'Sign A15 belongs to Danger Signs',
  'Bord A15 behoort tot Gevaarborden',
  'Le panneau A15 appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  109,
  34,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  110,
  34,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  111,
  34,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  112,
  34,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  35,
  'ما هي العلامة المرورية A15؟',
  'What does the traffic sign A15 mean?',
  'Wat betekent verkeersbord A15?',
  'Que signifie le panneau de signalisation A15?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A15' LIMIT 1),
  'العلامة A15 تعني: علامة A15',
  'Sign A15 means: Gladde rijbaan - Slipgevaar.',
  'Bord A15 betekent: Gladde rijbaan - Slipgevaar.',
  'Le panneau A15 signifie: Gladde rijbaan - Slipgevaar.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  113,
  35,
  'علامة E9a',
  'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  114,
  35,
  'علامة A15',
  'Gladde rijbaan - Slipgevaar.',
  'Gladde rijbaan - Slipgevaar.',
  'Gladde rijbaan - Slipgevaar.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  115,
  35,
  'علامة A51',
  'Gevaar dat niet door een speciaal symbool wordt bepaald.',
  'Gevaar dat niet door een speciaal symbool wordt bepaald.',
  'Gevaar dat niet door een speciaal symbool wordt bepaald.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  116,
  35,
  'علامة F87',
  'Verhoogde inrichting (vluchtheuvel).',
  'Verhoogde inrichting (vluchtheuvel).',
  'Verhoogde inrichting (vluchtheuvel).',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  36,
  'العلامة A17 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign A17 means: This sign is optional. True or False?',
  'Bord A17 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau A17 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A17' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  117,
  36,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  118,
  36,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  37,
  'ما هي العلامة المرورية A17؟',
  'What does the traffic sign A17 mean?',
  'Wat betekent verkeersbord A17?',
  'Que signifie le panneau de signalisation A17?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A17' LIMIT 1),
  'العلامة A17 تعني: علامة A17',
  'Sign A17 means: Kiezelprojectie',
  'Bord A17 betekent: Kiezelprojectie',
  'Le panneau A17 signifie: Kiezelprojectie',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  119,
  37,
  'علامة A17',
  'Kiezelprojectie',
  'Kiezelprojectie',
  'Kiezelprojectie',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  120,
  37,
  'علامة F34b',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  121,
  37,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van bromfietsen en fietsen.',
  'Accès interdit',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  122,
  37,
  'علامة F34c',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  38,
  'العلامة A19 تعني: علامة A19. صحيح أم خطأ؟',
  'Sign A19 means: Vallende stenen.. True or False?',
  'Bord A19 betekent: Vallende stenen.. Waar of Onwaar?',
  'Le panneau A19 signifie: Vallende stenen.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A19' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  123,
  38,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  124,
  38,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  39,
  'ما هي العلامة المرورية A19؟',
  'What does the traffic sign A19 mean?',
  'Wat betekent verkeersbord A19?',
  'Que signifie le panneau de signalisation A19?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A19' LIMIT 1),
  'العلامة A19 تعني: علامة A19',
  'Sign A19 means: Vallende stenen.',
  'Bord A19 betekent: Vallende stenen.',
  'Le panneau A19 signifie: Vallende stenen.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  125,
  39,
  'علامة A19',
  'Vallende stenen.',
  'Vallende stenen.',
  'Vallende stenen.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  126,
  39,
  'تضييق الطريق',
  'Road narrowing',
  'Rijbaanversmalling links',
  'Rétrécissement de chaussée',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  127,
  39,
  'علامة M13',
  'Verplichting voor speed pedelecs.',
  'Verplichting voor speed pedelecs.',
  'Verplichting voor speed pedelecs.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  128,
  39,
  'منعطف خطر لليمين',
  'Dangerous bend to the right',
  'Gevaarlijke bocht naar rechts.',
  'Virage dangereux à droite',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  40,
  'إلى أي فئة تنتمي العلامة A19؟',
  'Which category does sign A19 belong to?',
  'Tot welke categorie behoort bord A19?',
  'À quelle catégorie appartient le panneau A19?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A19' LIMIT 1),
  'العلامة A19 تنتمي إلى فئة علامات الخطر',
  'Sign A19 belongs to Danger Signs',
  'Bord A19 behoort tot Gevaarborden',
  'Le panneau A19 appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  129,
  40,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  130,
  40,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  131,
  40,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  132,
  40,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  41,
  'العلامة A21 تعني: معبر للمشاة. صحيح أم خطأ؟',
  'Sign A21 means: Pedestrian crossing. True or False?',
  'Bord A21 betekent: Oversteekplaats voor voetgangers.. Waar of Onwaar?',
  'Le panneau A21 signifie: Passage pour piétons. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A21' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  133,
  41,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  134,
  41,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  42,
  'ما هي العلامة المرورية A21؟',
  'What does the traffic sign A21 mean?',
  'Wat betekent verkeersbord A21?',
  'Que signifie le panneau de signalisation A21?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A21' LIMIT 1),
  'العلامة A21 تعني: معبر للمشاة',
  'Sign A21 means: Pedestrian crossing',
  'Bord A21 betekent: Oversteekplaats voor voetgangers.',
  'Le panneau A21 signifie: Passage pour piétons',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  135,
  42,
  'علامة F117',
  'Begin van een lage emissiezone',
  'Begin van een lage emissiezone',
  'Begin van een lage emissiezone',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  136,
  42,
  'معبر للمشاة',
  'Pedestrian crossing',
  'Oversteekplaats voor voetgangers.',
  'Passage pour piétons',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  137,
  42,
  'علامة F103',
  'Begin van een voetgangerszone',
  'Begin van een voetgangerszone',
  'Begin van een voetgangerszone',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  138,
  42,
  'منطقة سكنية',
  'Residential zone',
  'Begin van een woonerf of van een erf.',
  'Zone résidentielle',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  43,
  'إلى أي فئة تنتمي العلامة A21؟',
  'Which category does sign A21 belong to?',
  'Tot welke categorie behoort bord A21?',
  'À quelle catégorie appartient le panneau A21?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A21' LIMIT 1),
  'العلامة A21 تنتمي إلى فئة علامات الخطر',
  'Sign A21 belongs to Danger Signs',
  'Bord A21 behoort tot Gevaarborden',
  'Le panneau A21 appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  139,
  43,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  140,
  43,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  141,
  43,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  142,
  43,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  44,
  'إلى أي فئة تنتمي العلامة A23؟',
  'Which category does sign A23 belong to?',
  'Tot welke categorie behoort bord A23?',
  'À quelle catégorie appartient le panneau A23?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A23' LIMIT 1),
  'العلامة A23 تنتمي إلى فئة علامات الخطر',
  'Sign A23 belongs to Danger Signs',
  'Bord A23 behoort tot Gevaarborden',
  'Le panneau A23 appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  143,
  44,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  144,
  44,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  145,
  44,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  146,
  44,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  45,
  'ما هي العلامة المرورية A23؟',
  'What does the traffic sign A23 mean?',
  'Wat betekent verkeersbord A23?',
  'Que signifie le panneau de signalisation A23?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A23' LIMIT 1),
  'العلامة A23 تعني: أطفال',
  'Sign A23 means: Children',
  'Bord A23 betekent: Opgelet kinderen.',
  'Le panneau A23 signifie: Enfants',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  147,
  45,
  'علامة B22',
  'Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  148,
  45,
  'علامة B15f',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  149,
  45,
  'منعطف خطر لليمين',
  'Dangerous bend to the right',
  'Gevaarlijke bocht naar rechts.',
  'Virage dangereux à droite',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  150,
  45,
  'أطفال',
  'Children',
  'Opgelet kinderen.',
  'Enfants',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  46,
  'العلامة A23 تعني: أطفال. صحيح أم خطأ؟',
  'Sign A23 means: Children. True or False?',
  'Bord A23 betekent: Opgelet kinderen.. Waar of Onwaar?',
  'Le panneau A23 signifie: Enfants. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A23' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  151,
  46,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  152,
  46,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  47,
  'ما هي العلامة المرورية A25؟',
  'What does the traffic sign A25 mean?',
  'Wat betekent verkeersbord A25?',
  'Que signifie le panneau de signalisation A25?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A25' LIMIT 1),
  'العلامة A25 تعني: دراجات ودراجات نارية',
  'Sign A25 means: Cyclists and moped riders',
  'Bord A25 betekent: Oversteekplaats voor fietsers en bromfietsers.',
  'Le panneau A25 signifie: Cyclistes et cyclomotoristes',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  153,
  47,
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Oversteekplaats voor fietsers en bromfietsers.',
  'Cyclistes et cyclomotoristes',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  154,
  47,
  'علامة M1',
  'Enkel voor fietsers.',
  'Enkel voor fietsers.',
  'Enkel voor fietsers.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  155,
  47,
  'طريق ذو أولوية',
  'Priority road',
  'Voorrangsweg',
  'Route prioritaire',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  156,
  47,
  'علامة A51',
  'Gevaar dat niet door een speciaal symbool wordt bepaald.',
  'Gevaar dat niet door een speciaal symbool wordt bepaald.',
  'Gevaar dat niet door een speciaal symbool wordt bepaald.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  48,
  'إلى أي فئة تنتمي العلامة A25؟',
  'Which category does sign A25 belong to?',
  'Tot welke categorie behoort bord A25?',
  'À quelle catégorie appartient le panneau A25?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A25' LIMIT 1),
  'العلامة A25 تنتمي إلى فئة علامات الخطر',
  'Sign A25 belongs to Danger Signs',
  'Bord A25 behoort tot Gevaarborden',
  'Le panneau A25 appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  157,
  48,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  158,
  48,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  159,
  48,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  160,
  48,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  49,
  'العلامة A27 تعني: علامة A27. صحيح أم خطأ؟',
  'Sign A27 means: Overstekend groot wild.. True or False?',
  'Bord A27 betekent: Overstekend groot wild.. Waar of Onwaar?',
  'Le panneau A27 signifie: Overstekend groot wild.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A27' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  161,
  49,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  162,
  49,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  50,
  'إلى أي فئة تنتمي العلامة A27؟',
  'Which category does sign A27 belong to?',
  'Tot welke categorie behoort bord A27?',
  'À quelle catégorie appartient le panneau A27?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A27' LIMIT 1),
  'العلامة A27 تنتمي إلى فئة علامات الخطر',
  'Sign A27 belongs to Danger Signs',
  'Bord A27 behoort tot Gevaarborden',
  'Le panneau A27 appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  163,
  50,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  164,
  50,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  165,
  50,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  166,
  50,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  51,
  'ما هي العلامة المرورية A27؟',
  'What does the traffic sign A27 mean?',
  'Wat betekent verkeersbord A27?',
  'Que signifie le panneau de signalisation A27?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A27' LIMIT 1),
  'العلامة A27 تعني: علامة A27',
  'Sign A27 means: Overstekend groot wild.',
  'Bord A27 betekent: Overstekend groot wild.',
  'Le panneau A27 signifie: Overstekend groot wild.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  167,
  51,
  'علامة F99a',
  'Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  168,
  51,
  'علامة A27',
  'Overstekend groot wild.',
  'Overstekend groot wild.',
  'Overstekend groot wild.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  169,
  51,
  'علامة E9d',
  'Parkeren uitsluitend voor autocars.',
  'Parkeren uitsluitend voor autocars.',
  'Parkeren uitsluitend voor autocars.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  170,
  51,
  'علامة B17',
  'Kruispunt waar de voorrang van rechts geldt',
  'Kruispunt waar de voorrang van rechts geldt',
  'Kruispunt waar de voorrang van rechts geldt',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  52,
  'العلامة A29 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign A29 means: This sign is optional. True or False?',
  'Bord A29 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau A29 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A29' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  171,
  52,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  172,
  52,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  53,
  'إلى أي فئة تنتمي العلامة A29؟',
  'Which category does sign A29 belong to?',
  'Tot welke categorie behoort bord A29?',
  'À quelle catégorie appartient le panneau A29?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A29' LIMIT 1),
  'العلامة A29 تنتمي إلى فئة علامات الخطر',
  'Sign A29 belongs to Danger Signs',
  'Bord A29 behoort tot Gevaarborden',
  'Le panneau A29 appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  173,
  53,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  174,
  53,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  175,
  53,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  176,
  53,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  54,
  'العلامة A31 تعني: علامة A31. صحيح أم خطأ؟',
  'Sign A31 means: Werken.. True or False?',
  'Bord A31 betekent: Werken.. Waar of Onwaar?',
  'Le panneau A31 signifie: Werken.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A31' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  177,
  54,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  178,
  54,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  55,
  'ما هي العلامة المرورية A31؟',
  'What does the traffic sign A31 mean?',
  'Wat betekent verkeersbord A31?',
  'Que signifie le panneau de signalisation A31?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A31' LIMIT 1),
  'العلامة A31 تعني: علامة A31',
  'Sign A31 means: Werken.',
  'Bord A31 betekent: Werken.',
  'Le panneau A31 signifie: Werken.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  179,
  55,
  'علامة A31',
  'Werken.',
  'Werken.',
  'Werken.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  180,
  55,
  'علامة C25',
  'Verboden voor voertuigen langer dan het aangeduide',
  'Verboden voor voertuigen langer dan het aangeduide',
  'Verboden voor voertuigen langer dan het aangeduide',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  181,
  55,
  'علامة E9j',
  'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  182,
  55,
  'منطقة سكنية',
  'Residential zone',
  'Begin van een woonerf of van een erf.',
  'Zone résidentielle',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  56,
  'إلى أي فئة تنتمي العلامة A31؟',
  'Which category does sign A31 belong to?',
  'Tot welke categorie behoort bord A31?',
  'À quelle catégorie appartient le panneau A31?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A31' LIMIT 1),
  'العلامة A31 تنتمي إلى فئة علامات الخطر',
  'Sign A31 belongs to Danger Signs',
  'Bord A31 behoort tot Gevaarborden',
  'Le panneau A31 appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  183,
  56,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  184,
  56,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  185,
  56,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  186,
  56,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  57,
  'إلى أي فئة تنتمي العلامة A33؟',
  'Which category does sign A33 belong to?',
  'Tot welke categorie behoort bord A33?',
  'À quelle catégorie appartient le panneau A33?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A33' LIMIT 1),
  'العلامة A33 تنتمي إلى فئة علامات الخطر',
  'Sign A33 belongs to Danger Signs',
  'Bord A33 behoort tot Gevaarborden',
  'Le panneau A33 appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  187,
  57,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  188,
  57,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  189,
  57,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  190,
  57,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  58,
  'العلامة A33 تعني: علامة A33. صحيح أم خطأ؟',
  'Sign A33 means: Verkeerslichten.. True or False?',
  'Bord A33 betekent: Verkeerslichten.. Waar of Onwaar?',
  'Le panneau A33 signifie: Verkeerslichten.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A33' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  191,
  58,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  192,
  58,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  59,
  'ما هي العلامة المرورية A33؟',
  'What does the traffic sign A33 mean?',
  'Wat betekent verkeersbord A33?',
  'Que signifie le panneau de signalisation A33?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A33' LIMIT 1),
  'العلامة A33 تعني: علامة A33',
  'Sign A33 means: Verkeerslichten.',
  'Bord A33 betekent: Verkeerslichten.',
  'Le panneau A33 signifie: Verkeerslichten.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  193,
  59,
  'علامة A43',
  'Overweg zonder slagbomen.',
  'Overweg zonder slagbomen.',
  'Overweg zonder slagbomen.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  194,
  59,
  'علامة F61',
  'Telefoon.',
  'Telefoon.',
  'Telefoon.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  195,
  59,
  'علامة A33',
  'Verkeerslichten.',
  'Verkeerslichten.',
  'Verkeerslichten.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  196,
  59,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen die gevaarlijke goederen vervoeren.',
  'Accès interdit',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  60,
  'إلى أي فئة تنتمي العلامة A35؟',
  'Which category does sign A35 belong to?',
  'Tot welke categorie behoort bord A35?',
  'À quelle catégorie appartient le panneau A35?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A35' LIMIT 1),
  'العلامة A35 تنتمي إلى فئة علامات الخطر',
  'Sign A35 belongs to Danger Signs',
  'Bord A35 behoort tot Gevaarborden',
  'Le panneau A35 appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  197,
  60,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  198,
  60,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  199,
  60,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  200,
  60,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  61,
  'العلامة A35 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign A35 means: This sign is optional. True or False?',
  'Bord A35 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau A35 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A35' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  201,
  61,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  202,
  61,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  62,
  'ما هي العلامة المرورية A37؟',
  'What does the traffic sign A37 mean?',
  'Wat betekent verkeersbord A37?',
  'Que signifie le panneau de signalisation A37?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A37' LIMIT 1),
  'العلامة A37 تعني: علامة A37',
  'Sign A37 means: Zijwind.',
  'Bord A37 betekent: Zijwind.',
  'Le panneau A37 signifie: Zijwind.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  203,
  62,
  'علامة C27',
  'Verboden voor voertuigen breder dan het aangeduide.',
  'Verboden voor voertuigen breder dan het aangeduide.',
  'Verboden voor voertuigen breder dan het aangeduide.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  204,
  62,
  'علامة F87',
  'Verhoogde inrichting (vluchtheuvel).',
  'Verhoogde inrichting (vluchtheuvel).',
  'Verhoogde inrichting (vluchtheuvel).',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  205,
  62,
  'علامة F13',
  'Rijstrook keuze.',
  'Rijstrook keuze.',
  'Rijstrook keuze.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  206,
  62,
  'علامة A37',
  'Zijwind.',
  'Zijwind.',
  'Zijwind.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  63,
  'إلى أي فئة تنتمي العلامة A37؟',
  'Which category does sign A37 belong to?',
  'Tot welke categorie behoort bord A37?',
  'À quelle catégorie appartient le panneau A37?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A37' LIMIT 1),
  'العلامة A37 تنتمي إلى فئة علامات الخطر',
  'Sign A37 belongs to Danger Signs',
  'Bord A37 behoort tot Gevaarborden',
  'Le panneau A37 appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  207,
  63,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  208,
  63,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  209,
  63,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  210,
  63,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  64,
  'العلامة A39 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign A39 means: This sign is optional. True or False?',
  'Bord A39 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau A39 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A39' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  211,
  64,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  212,
  64,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  65,
  'إلى أي فئة تنتمي العلامة A39؟',
  'Which category does sign A39 belong to?',
  'Tot welke categorie behoort bord A39?',
  'À quelle catégorie appartient le panneau A39?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A39' LIMIT 1),
  'العلامة A39 تنتمي إلى فئة علامات الخطر',
  'Sign A39 belongs to Danger Signs',
  'Bord A39 behoort tot Gevaarborden',
  'Le panneau A39 appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  213,
  65,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  214,
  65,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  215,
  65,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  216,
  65,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  66,
  'ما هي العلامة المرورية A39؟',
  'What does the traffic sign A39 mean?',
  'Wat betekent verkeersbord A39?',
  'Que signifie le panneau de signalisation A39?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A39' LIMIT 1),
  'العلامة A39 تعني: علامة A39',
  'Sign A39 means: Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer.',
  'Bord A39 betekent: Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer.',
  'Le panneau A39 signifie: Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  217,
  66,
  'علامة F77',
  'Toeristische informatie.',
  'Toeristische informatie.',
  'Toeristische informatie.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  218,
  66,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen bestemd of gebruikt voor het vervoer van zaken.',
  'Accès interdit',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  219,
  66,
  'علامة F17',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  220,
  66,
  'علامة A39',
  'Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer.',
  'Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer.',
  'Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  67,
  'إلى أي فئة تنتمي العلامة A41؟',
  'Which category does sign A41 belong to?',
  'Tot welke categorie behoort bord A41?',
  'À quelle catégorie appartient le panneau A41?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A41' LIMIT 1),
  'العلامة A41 تنتمي إلى فئة علامات الخطر',
  'Sign A41 belongs to Danger Signs',
  'Bord A41 behoort tot Gevaarborden',
  'Le panneau A41 appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  221,
  67,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  222,
  67,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  223,
  67,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  224,
  67,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  68,
  'العلامة A41 تعني: علامة A41. صحيح أم خطأ؟',
  'Sign A41 means: Overweg met slagbomen.. True or False?',
  'Bord A41 betekent: Overweg met slagbomen.. Waar of Onwaar?',
  'Le panneau A41 signifie: Overweg met slagbomen.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A41' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  225,
  68,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  226,
  68,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  69,
  'ما هي العلامة المرورية A41؟',
  'What does the traffic sign A41 mean?',
  'Wat betekent verkeersbord A41?',
  'Que signifie le panneau de signalisation A41?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A41' LIMIT 1),
  'العلامة A41 تعني: علامة A41',
  'Sign A41 means: Overweg met slagbomen.',
  'Bord A41 betekent: Overweg met slagbomen.',
  'Le panneau A41 signifie: Overweg met slagbomen.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  227,
  69,
  'علامة D1a',
  'Verplichting rechtdoor.',
  'Verplichting rechtdoor.',
  'Verplichting rechtdoor.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  228,
  69,
  'علامة F60',
  'Overdekte parking.',
  'Overdekte parking.',
  'Overdekte parking.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  229,
  69,
  'علامة A41',
  'Overweg met slagbomen.',
  'Overweg met slagbomen.',
  'Overweg met slagbomen.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  230,
  69,
  'علامة F77',
  'Toeristische informatie.',
  'Toeristische informatie.',
  'Toeristische informatie.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  70,
  'العلامة A43 تعني: علامة A43. صحيح أم خطأ؟',
  'Sign A43 means: Overweg zonder slagbomen.. True or False?',
  'Bord A43 betekent: Overweg zonder slagbomen.. Waar of Onwaar?',
  'Le panneau A43 signifie: Overweg zonder slagbomen.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A43' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  231,
  70,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  232,
  70,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  71,
  'ما هي العلامة المرورية A43؟',
  'What does the traffic sign A43 mean?',
  'Wat betekent verkeersbord A43?',
  'Que signifie le panneau de signalisation A43?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A43' LIMIT 1),
  'العلامة A43 تعني: علامة A43',
  'Sign A43 means: Overweg zonder slagbomen.',
  'Bord A43 betekent: Overweg zonder slagbomen.',
  'Le panneau A43 signifie: Overweg zonder slagbomen.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  233,
  71,
  'علامة A43',
  'Overweg zonder slagbomen.',
  'Overweg zonder slagbomen.',
  'Overweg zonder slagbomen.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  234,
  71,
  'معبر للمشاة',
  'Pedestrian crossing',
  'Oversteekplaats voor voetgangers.',
  'Passage pour piétons',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  235,
  71,
  'علامة F101b',
  'Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.',
  'Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.',
  'Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  236,
  71,
  'علامة C43',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  72,
  'ما هي العلامة المرورية A49؟',
  'What does the traffic sign A49 mean?',
  'Wat betekent verkeersbord A49?',
  'Que signifie le panneau de signalisation A49?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A49' LIMIT 1),
  'العلامة A49 تعني: علامة A49',
  'Sign A49 means: Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  'Bord A49 betekent: Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  'Le panneau A49 signifie: Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  237,
  72,
  'علامة C37',
  'Einde verbod opgelegd door het verkeersbord C35',
  'Einde verbod opgelegd door het verkeersbord C35',
  'Einde verbod opgelegd door het verkeersbord C35',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  238,
  72,
  'منعطفات خطرة، الأول لليسار',
  'Dangerous double or multiple bends, first to the left',
  'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar links.',
  'Virages dangereux, le premier à gauche',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  239,
  72,
  'علامة F99a',
  'Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  240,
  72,
  'علامة A49',
  'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  73,
  'إلى أي فئة تنتمي العلامة A49؟',
  'Which category does sign A49 belong to?',
  'Tot welke categorie behoort bord A49?',
  'À quelle catégorie appartient le panneau A49?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A49' LIMIT 1),
  'العلامة A49 تنتمي إلى فئة علامات الخطر',
  'Sign A49 belongs to Danger Signs',
  'Bord A49 behoort tot Gevaarborden',
  'Le panneau A49 appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  241,
  73,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  242,
  73,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  243,
  73,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  244,
  73,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  74,
  'ما هي العلامة المرورية A50؟',
  'What does the traffic sign A50 mean?',
  'Wat betekent verkeersbord A50?',
  'Que signifie le panneau de signalisation A50?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A50' LIMIT 1),
  'العلامة A50 تعني: علامة A50',
  'Sign A50 means: Opgelet file',
  'Bord A50 betekent: Opgelet file',
  'Le panneau A50 signifie: Opgelet file',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  245,
  74,
  'علامة M2',
  'Uitgezonderd fietsers.',
  'Uitgezonderd fietsers.',
  'Uitgezonderd fietsers.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  246,
  74,
  'علامة F99c',
  'Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  247,
  74,
  'علامة A50',
  'Opgelet file',
  'Opgelet file',
  'Opgelet file',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  248,
  74,
  'علامة F34a',
  'Nabijheid van inrichting die van openbaar of algemeen belang is.',
  'Nabijheid van inrichting die van openbaar of algemeen belang is.',
  'Nabijheid van inrichting die van openbaar of algemeen belang is.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  75,
  'العلامة A50 تعني: علامة A50. صحيح أم خطأ؟',
  'Sign A50 means: Opgelet file. True or False?',
  'Bord A50 betekent: Opgelet file. Waar of Onwaar?',
  'Le panneau A50 signifie: Opgelet file. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A50' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  249,
  75,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  250,
  75,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  76,
  'ما هي العلامة المرورية A51؟',
  'What does the traffic sign A51 mean?',
  'Wat betekent verkeersbord A51?',
  'Que signifie le panneau de signalisation A51?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A51' LIMIT 1),
  'العلامة A51 تعني: علامة A51',
  'Sign A51 means: Gevaar dat niet door een speciaal symbool wordt bepaald.',
  'Bord A51 betekent: Gevaar dat niet door een speciaal symbool wordt bepaald.',
  'Le panneau A51 signifie: Gevaar dat niet door een speciaal symbool wordt bepaald.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  251,
  76,
  'منطقة 30',
  'Zone 30',
  'Zone 30 km/u.',
  'Zone 30',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  252,
  76,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen die gevaarlijke goederen vervoeren.',
  'Accès interdit',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  253,
  76,
  'انحدار خطر',
  'Dangerous descent',
  'Gevaarlijke daling.',
  'Descente dangereuse',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  254,
  76,
  'علامة A51',
  'Gevaar dat niet door een speciaal symbool wordt bepaald.',
  'Gevaar dat niet door een speciaal symbool wordt bepaald.',
  'Gevaar dat niet door een speciaal symbool wordt bepaald.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  77,
  'العلامة A51 تعني: علامة A51. صحيح أم خطأ؟',
  'Sign A51 means: Gevaar dat niet door een speciaal symbool wordt bepaald.. True or False?',
  'Bord A51 betekent: Gevaar dat niet door een speciaal symbool wordt bepaald.. Waar of Onwaar?',
  'Le panneau A51 signifie: Gevaar dat niet door een speciaal symbool wordt bepaald.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A51' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  255,
  77,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  256,
  77,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  78,
  'إلى أي فئة تنتمي العلامة A51؟',
  'Which category does sign A51 belong to?',
  'Tot welke categorie behoort bord A51?',
  'À quelle catégorie appartient le panneau A51?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'A'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'A51' LIMIT 1),
  'العلامة A51 تنتمي إلى فئة علامات الخطر',
  'Sign A51 belongs to Danger Signs',
  'Bord A51 behoort tot Gevaarborden',
  'Le panneau A51 appartient à Panneaux de danger',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  257,
  78,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  258,
  78,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  259,
  78,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  260,
  78,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  79,
  'ما هي العلامة المرورية B1؟',
  'What does the traffic sign B1 mean?',
  'Wat betekent verkeersbord B1?',
  'Que signifie le panneau de signalisation B1?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B1' LIMIT 1),
  'العلامة B1 تعني: أعط الأولوية',
  'Sign B1 means: Give way',
  'Bord B1 betekent: Voorrang verlenen',
  'Le panneau B1 signifie: Cédez le passage',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  261,
  79,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor voetgangers.',
  'Accès interdit',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  262,
  79,
  'علامة F23c',
  'Nummer van een internationale weg.',
  'Nummer van een internationale weg.',
  'Nummer van een internationale weg.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  263,
  79,
  'منعطف خطر لليسار',
  'Dangerous bend to the left',
  'Gevaarlijke bocht naar links.',
  'Virage dangereux à gauche',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  264,
  79,
  'أعط الأولوية',
  'Give way',
  'Voorrang verlenen',
  'Cédez le passage',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  80,
  'إلى أي فئة تنتمي العلامة B1؟',
  'Which category does sign B1 belong to?',
  'Tot welke categorie behoort bord B1?',
  'À quelle catégorie appartient le panneau B1?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B1' LIMIT 1),
  'العلامة B1 تنتمي إلى فئة علامات الأولوية',
  'Sign B1 belongs to Priority Signs',
  'Bord B1 behoort tot Voorrangsborden',
  'Le panneau B1 appartient à Panneaux de priorité',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  265,
  80,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  266,
  80,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  267,
  80,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  268,
  80,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  81,
  'العلامة B1 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign B1 means: This sign is optional. True or False?',
  'Bord B1 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau B1 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B1' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  269,
  81,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  270,
  81,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  82,
  'ما هي العلامة المرورية B5؟',
  'What does the traffic sign B5 mean?',
  'Wat betekent verkeersbord B5?',
  'Que signifie le panneau de signalisation B5?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B5' LIMIT 1),
  'العلامة B5 تعني: قف',
  'Sign B5 means: Stop',
  'Bord B5 betekent: Stoppen en voorrang verlenen',
  'Le panneau B5 signifie: Stop',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  271,
  82,
  'قف',
  'Stop',
  'Stoppen en voorrang verlenen',
  'Stop',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  272,
  82,
  'علامة F39',
  'Aankondiging van een omleiding.',
  'Aankondiging van een omleiding.',
  'Aankondiging van een omleiding.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  273,
  82,
  'علامة D9b',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  274,
  82,
  'علامة F17',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  83,
  'إلى أي فئة تنتمي العلامة B5؟',
  'Which category does sign B5 belong to?',
  'Tot welke categorie behoort bord B5?',
  'À quelle catégorie appartient le panneau B5?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B5' LIMIT 1),
  'العلامة B5 تنتمي إلى فئة علامات الأولوية',
  'Sign B5 belongs to Priority Signs',
  'Bord B5 behoort tot Voorrangsborden',
  'Le panneau B5 appartient à Panneaux de priorité',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  275,
  83,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  276,
  83,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  277,
  83,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  278,
  83,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  84,
  'ما هي العلامة المرورية B9؟',
  'What does the traffic sign B9 mean?',
  'Wat betekent verkeersbord B9?',
  'Que signifie le panneau de signalisation B9?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B9' LIMIT 1),
  'العلامة B9 تعني: طريق ذو أولوية',
  'Sign B9 means: Priority road',
  'Bord B9 betekent: Voorrangsweg',
  'Le panneau B9 signifie: Route prioritaire',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  279,
  84,
  'طريق ذو أولوية',
  'Priority road',
  'Voorrangsweg',
  'Route prioritaire',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  280,
  84,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van motorvoertuigen en motorfietsen.',
  'Accès interdit',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  281,
  84,
  'أطفال',
  'Children',
  'Opgelet kinderen.',
  'Enfants',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  282,
  84,
  'علامة F75',
  'Jeugdherberg.',
  'Jeugdherberg.',
  'Jeugdherberg.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  85,
  'العلامة B9 تعني: طريق ذو أولوية. صحيح أم خطأ؟',
  'Sign B9 means: Priority road. True or False?',
  'Bord B9 betekent: Voorrangsweg. Waar of Onwaar?',
  'Le panneau B9 signifie: Route prioritaire. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B9' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  283,
  85,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  284,
  85,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  86,
  'إلى أي فئة تنتمي العلامة B9؟',
  'Which category does sign B9 belong to?',
  'Tot welke categorie behoort bord B9?',
  'À quelle catégorie appartient le panneau B9?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B9' LIMIT 1),
  'العلامة B9 تنتمي إلى فئة علامات الأولوية',
  'Sign B9 belongs to Priority Signs',
  'Bord B9 behoort tot Voorrangsborden',
  'Le panneau B9 appartient à Panneaux de priorité',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  285,
  86,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  286,
  86,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  287,
  86,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  288,
  86,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  87,
  'العلامة B11 تعني: طريق ذو أولوية. صحيح أم خطأ؟',
  'Sign B11 means: Priority road. True or False?',
  'Bord B11 betekent: Einde voorrangsweg. Waar of Onwaar?',
  'Le panneau B11 signifie: Route prioritaire. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B11' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  289,
  87,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  290,
  87,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  88,
  'إلى أي فئة تنتمي العلامة B11؟',
  'Which category does sign B11 belong to?',
  'Tot welke categorie behoort bord B11?',
  'À quelle catégorie appartient le panneau B11?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B11' LIMIT 1),
  'العلامة B11 تنتمي إلى فئة علامات الأولوية',
  'Sign B11 belongs to Priority Signs',
  'Bord B11 behoort tot Voorrangsborden',
  'Le panneau B11 appartient à Panneaux de priorité',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  291,
  88,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  292,
  88,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  293,
  88,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  294,
  88,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  89,
  'إلى أي فئة تنتمي العلامة B15a؟',
  'Which category does sign B15a belong to?',
  'Tot welke categorie behoort bord B15a?',
  'À quelle catégorie appartient le panneau B15a?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B15a' LIMIT 1),
  'العلامة B15a تنتمي إلى فئة علامات الأولوية',
  'Sign B15a belongs to Priority Signs',
  'Bord B15a behoort tot Voorrangsborden',
  'Le panneau B15a appartient à Panneaux de priorité',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  295,
  89,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  296,
  89,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  297,
  89,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  298,
  89,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  90,
  'العلامة B15a تعني: علامة B15a. صحيح أم خطأ؟',
  'Sign B15a means: Voorrang op de kruisende zijwegen. True or False?',
  'Bord B15a betekent: Voorrang op de kruisende zijwegen. Waar of Onwaar?',
  'Le panneau B15a signifie: Voorrang op de kruisende zijwegen. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B15a' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  299,
  90,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  300,
  90,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  91,
  'العلامة B15b تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign B15b means: This sign is optional. True or False?',
  'Bord B15b betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau B15b signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B15b' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  301,
  91,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  302,
  91,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  92,
  'ما هي العلامة المرورية B15b؟',
  'What does the traffic sign B15b mean?',
  'Wat betekent verkeersbord B15b?',
  'Que signifie le panneau de signalisation B15b?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B15b' LIMIT 1),
  'العلامة B15b تعني: علامة B15b',
  'Sign B15b means: Voorrang op kruisende zijweg',
  'Bord B15b betekent: Voorrang op kruisende zijweg',
  'Le panneau B15b signifie: Voorrang op kruisende zijweg',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  303,
  92,
  'علامة B15b',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  304,
  92,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen die gevaarlijke verontreinigende stoffen vervoeren.',
  'Accès interdit',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  305,
  92,
  'علامة A41',
  'Overweg met slagbomen.',
  'Overweg met slagbomen.',
  'Overweg met slagbomen.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  306,
  92,
  'علامة D1c',
  'Verplichting links aanhouden.',
  'Verplichting links aanhouden.',
  'Verplichting links aanhouden.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  93,
  'إلى أي فئة تنتمي العلامة B15b؟',
  'Which category does sign B15b belong to?',
  'Tot welke categorie behoort bord B15b?',
  'À quelle catégorie appartient le panneau B15b?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B15b' LIMIT 1),
  'العلامة B15b تنتمي إلى فئة علامات الأولوية',
  'Sign B15b belongs to Priority Signs',
  'Bord B15b behoort tot Voorrangsborden',
  'Le panneau B15b appartient à Panneaux de priorité',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  307,
  93,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  308,
  93,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  309,
  93,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  310,
  93,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  94,
  'إلى أي فئة تنتمي العلامة B15c؟',
  'Which category does sign B15c belong to?',
  'Tot welke categorie behoort bord B15c?',
  'À quelle catégorie appartient le panneau B15c?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B15c' LIMIT 1),
  'العلامة B15c تنتمي إلى فئة علامات الأولوية',
  'Sign B15c belongs to Priority Signs',
  'Bord B15c behoort tot Voorrangsborden',
  'Le panneau B15c appartient à Panneaux de priorité',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  311,
  94,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  312,
  94,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  313,
  94,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  314,
  94,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  95,
  'ما هي العلامة المرورية B15c؟',
  'What does the traffic sign B15c mean?',
  'Wat betekent verkeersbord B15c?',
  'Que signifie le panneau de signalisation B15c?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B15c' LIMIT 1),
  'العلامة B15c تعني: علامة B15c',
  'Sign B15c means: Voorrang op kruisende zijweg',
  'Bord B15c betekent: Voorrang op kruisende zijweg',
  'Le panneau B15c signifie: Voorrang op kruisende zijweg',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  315,
  95,
  'علامة A33',
  'Verkeerslichten.',
  'Verkeerslichten.',
  'Verkeerslichten.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  316,
  95,
  'علامة B15c',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  317,
  95,
  'علامة D3b',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  318,
  95,
  'حفرة عرضية أو مطب',
  'Transverse depression or hump',
  'Dwarse uitholling of ezelsrug.',
  'Dépression transversale ou dos d''âne',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  96,
  'ما هي العلامة المرورية B15d؟',
  'What does the traffic sign B15d mean?',
  'Wat betekent verkeersbord B15d?',
  'Que signifie le panneau de signalisation B15d?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B15d' LIMIT 1),
  'العلامة B15d تعني: علامة B15d',
  'Sign B15d means: Voorrang op kruisende zijweg',
  'Bord B15d betekent: Voorrang op kruisende zijweg',
  'Le panneau B15d signifie: Voorrang op kruisende zijweg',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  319,
  96,
  'علامة F19',
  'Eenrichtingsverkeer.',
  'Eenrichtingsverkeer.',
  'Eenrichtingsverkeer.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  320,
  96,
  'علامة B15d',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  321,
  96,
  'منطقة سكنية',
  'Residential zone',
  'Begin van een woonerf of van een erf.',
  'Zone résidentielle',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  322,
  96,
  'علامة B15b',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  97,
  'العلامة B15d تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign B15d means: This sign is optional. True or False?',
  'Bord B15d betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau B15d signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B15d' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  323,
  97,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  324,
  97,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  98,
  'إلى أي فئة تنتمي العلامة B15d؟',
  'Which category does sign B15d belong to?',
  'Tot welke categorie behoort bord B15d?',
  'À quelle catégorie appartient le panneau B15d?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B15d' LIMIT 1),
  'العلامة B15d تنتمي إلى فئة علامات الأولوية',
  'Sign B15d belongs to Priority Signs',
  'Bord B15d behoort tot Voorrangsborden',
  'Le panneau B15d appartient à Panneaux de priorité',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  325,
  98,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  326,
  98,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  327,
  98,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  328,
  98,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  99,
  'ما هي العلامة المرورية B15e؟',
  'What does the traffic sign B15e mean?',
  'Wat betekent verkeersbord B15e?',
  'Que signifie le panneau de signalisation B15e?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B15e' LIMIT 1),
  'العلامة B15e تعني: علامة B15e',
  'Sign B15e means: Voorrang op kruisende zijweg',
  'Bord B15e betekent: Voorrang op kruisende zijweg',
  'Le panneau B15e signifie: Voorrang op kruisende zijweg',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  329,
  99,
  'طريق سريع',
  'Motorway',
  'Autosnelweg.',
  'Autoroute',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  330,
  99,
  'علامة B15e',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  331,
  99,
  'علامة M7',
  'Verbod voor bromfietsen klasse B.',
  'Verbod voor bromfietsen klasse B.',
  'Verbod voor bromfietsen klasse B.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  332,
  99,
  'علامة A15',
  'Gladde rijbaan - Slipgevaar.',
  'Gladde rijbaan - Slipgevaar.',
  'Gladde rijbaan - Slipgevaar.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  100,
  'إلى أي فئة تنتمي العلامة B15e؟',
  'Which category does sign B15e belong to?',
  'Tot welke categorie behoort bord B15e?',
  'À quelle catégorie appartient le panneau B15e?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B15e' LIMIT 1),
  'العلامة B15e تنتمي إلى فئة علامات الأولوية',
  'Sign B15e belongs to Priority Signs',
  'Bord B15e behoort tot Voorrangsborden',
  'Le panneau B15e appartient à Panneaux de priorité',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  333,
  100,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  334,
  100,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  335,
  100,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  336,
  100,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  101,
  'العلامة B15e تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign B15e means: This sign is optional. True or False?',
  'Bord B15e betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau B15e signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B15e' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  337,
  101,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  338,
  101,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  102,
  'العلامة B15f تعني: علامة B15f. صحيح أم خطأ؟',
  'Sign B15f means: Voorrang op kruisende zijweg. True or False?',
  'Bord B15f betekent: Voorrang op kruisende zijweg. Waar of Onwaar?',
  'Le panneau B15f signifie: Voorrang op kruisende zijweg. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B15f' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  339,
  102,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  340,
  102,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  103,
  'إلى أي فئة تنتمي العلامة B15f؟',
  'Which category does sign B15f belong to?',
  'Tot welke categorie behoort bord B15f?',
  'À quelle catégorie appartient le panneau B15f?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B15f' LIMIT 1),
  'العلامة B15f تنتمي إلى فئة علامات الأولوية',
  'Sign B15f belongs to Priority Signs',
  'Bord B15f behoort tot Voorrangsborden',
  'Le panneau B15f appartient à Panneaux de priorité',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  341,
  103,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  342,
  103,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  343,
  103,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  344,
  103,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  104,
  'إلى أي فئة تنتمي العلامة B15g؟',
  'Which category does sign B15g belong to?',
  'Tot welke categorie behoort bord B15g?',
  'À quelle catégorie appartient le panneau B15g?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B15g' LIMIT 1),
  'العلامة B15g تنتمي إلى فئة علامات الأولوية',
  'Sign B15g belongs to Priority Signs',
  'Bord B15g behoort tot Voorrangsborden',
  'Le panneau B15g appartient à Panneaux de priorité',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  345,
  104,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  346,
  104,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  347,
  104,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  348,
  104,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  105,
  'العلامة B15g تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign B15g means: This sign is optional. True or False?',
  'Bord B15g betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau B15g signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B15g' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  349,
  105,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  350,
  105,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  106,
  'ما هي العلامة المرورية B17؟',
  'What does the traffic sign B17 mean?',
  'Wat betekent verkeersbord B17?',
  'Que signifie le panneau de signalisation B17?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B17' LIMIT 1),
  'العلامة B17 تعني: علامة B17',
  'Sign B17 means: Kruispunt waar de voorrang van rechts geldt',
  'Bord B17 betekent: Kruispunt waar de voorrang van rechts geldt',
  'Le panneau B17 signifie: Kruispunt waar de voorrang van rechts geldt',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  351,
  106,
  'علامة D4',
  'Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.',
  'Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.',
  'Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  352,
  106,
  'علامة C43',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  353,
  106,
  'علامة F43',
  'Gemeentegrens',
  'Gemeentegrens',
  'Gemeentegrens',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  354,
  106,
  'علامة B17',
  'Kruispunt waar de voorrang van rechts geldt',
  'Kruispunt waar de voorrang van rechts geldt',
  'Kruispunt waar de voorrang van rechts geldt',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  107,
  'العلامة B17 تعني: علامة B17. صحيح أم خطأ؟',
  'Sign B17 means: Kruispunt waar de voorrang van rechts geldt. True or False?',
  'Bord B17 betekent: Kruispunt waar de voorrang van rechts geldt. Waar of Onwaar?',
  'Le panneau B17 signifie: Kruispunt waar de voorrang van rechts geldt. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B17' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  355,
  107,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  356,
  107,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  108,
  'إلى أي فئة تنتمي العلامة B19؟',
  'Which category does sign B19 belong to?',
  'Tot welke categorie behoort bord B19?',
  'À quelle catégorie appartient le panneau B19?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B19' LIMIT 1),
  'العلامة B19 تنتمي إلى فئة علامات الأولوية',
  'Sign B19 belongs to Priority Signs',
  'Bord B19 behoort tot Voorrangsborden',
  'Le panneau B19 appartient à Panneaux de priorité',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  357,
  108,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  358,
  108,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  359,
  108,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  360,
  108,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  109,
  'ما هي العلامة المرورية B19؟',
  'What does the traffic sign B19 mean?',
  'Wat betekent verkeersbord B19?',
  'Que signifie le panneau de signalisation B19?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B19' LIMIT 1),
  'العلامة B19 تعني: أعط الأولوية',
  'Sign B19 means: Give way',
  'Bord B19 betekent: Smalle doorgang voorrang verlenen aan de bestuurders die uit de tegenovergestelde richting komen',
  'Le panneau B19 signifie: Cédez le passage',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  361,
  109,
  'علامة F53',
  'Verplegingsinrichting.',
  'Verplegingsinrichting.',
  'Verplegingsinrichting.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  362,
  109,
  'علامة M6',
  'Verplichting voor bromfietsen klasse B.',
  'Verplichting voor bromfietsen klasse B.',
  'Verplichting voor bromfietsen klasse B.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  363,
  109,
  'أعط الأولوية',
  'Give way',
  'Smalle doorgang voorrang verlenen aan de bestuurders die uit de tegenovergestelde richting komen',
  'Cédez le passage',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  364,
  109,
  'علامة M2',
  'Uitgezonderd fietsers.',
  'Uitgezonderd fietsers.',
  'Uitgezonderd fietsers.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  110,
  'العلامة B21 تعني: علامة B21. صحيح أم خطأ؟',
  'Sign B21 means: Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen. True or False?',
  'Bord B21 betekent: Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen. Waar of Onwaar?',
  'Le panneau B21 signifie: Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B21' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  365,
  110,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  366,
  110,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  111,
  'إلى أي فئة تنتمي العلامة B21؟',
  'Which category does sign B21 belong to?',
  'Tot welke categorie behoort bord B21?',
  'À quelle catégorie appartient le panneau B21?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B21' LIMIT 1),
  'العلامة B21 تنتمي إلى فئة علامات الأولوية',
  'Sign B21 belongs to Priority Signs',
  'Bord B21 behoort tot Voorrangsborden',
  'Le panneau B21 appartient à Panneaux de priorité',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  367,
  111,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  368,
  111,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  369,
  111,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  370,
  111,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  112,
  'ما هي العلامة المرورية B21؟',
  'What does the traffic sign B21 mean?',
  'Wat betekent verkeersbord B21?',
  'Que signifie le panneau de signalisation B21?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B21' LIMIT 1),
  'العلامة B21 تعني: علامة B21',
  'Sign B21 means: Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen',
  'Bord B21 betekent: Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen',
  'Le panneau B21 signifie: Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  371,
  112,
  'علامة A15',
  'Gladde rijbaan - Slipgevaar.',
  'Gladde rijbaan - Slipgevaar.',
  'Gladde rijbaan - Slipgevaar.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  372,
  112,
  'علامة B21',
  'Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen',
  'Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen',
  'Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  373,
  112,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen waarvan de massa in beladen toestand hoger is dan de aangeduide massa.',
  'Accès interdit',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  374,
  112,
  'علامة C41',
  'Einde van het verbod opgelegd door het verkeersbord C39.',
  'Einde van het verbod opgelegd door het verkeersbord C39.',
  'Einde van het verbod opgelegd door het verkeersbord C39.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  113,
  'ما هي العلامة المرورية B22؟',
  'What does the traffic sign B22 mean?',
  'Wat betekent verkeersbord B22?',
  'Que signifie le panneau de signalisation B22?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B22' LIMIT 1),
  'العلامة B22 تعني: علامة B22',
  'Sign B22 means: Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
  'Bord B22 betekent: Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
  'Le panneau B22 signifie: Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  375,
  113,
  'علامة B22',
  'Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  376,
  113,
  'علامة B21',
  'Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen',
  'Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen',
  'Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  377,
  113,
  'تضييق الطريق',
  'Road narrowing',
  'Rijbaanversmalling rechts',
  'Rétrécissement de chaussée',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  378,
  113,
  'ممنوع الانتظار',
  'Parking prohibited',
  'Parkeerverbod van de 1e tot de 15e van de maand.',
  'Interdiction de stationner',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  114,
  'إلى أي فئة تنتمي العلامة B22؟',
  'Which category does sign B22 belong to?',
  'Tot welke categorie behoort bord B22?',
  'À quelle catégorie appartient le panneau B22?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B22' LIMIT 1),
  'العلامة B22 تنتمي إلى فئة علامات الأولوية',
  'Sign B22 belongs to Priority Signs',
  'Bord B22 behoort tot Voorrangsborden',
  'Le panneau B22 appartient à Panneaux de priorité',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  379,
  114,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  380,
  114,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  381,
  114,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  382,
  114,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  115,
  'العلامة B22 تعني: علامة B22. صحيح أم خطأ؟',
  'Sign B22 means: Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden. True or False?',
  'Bord B22 betekent: Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden. Waar of Onwaar?',
  'Le panneau B22 signifie: Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B22' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  383,
  115,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  384,
  115,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  116,
  'إلى أي فئة تنتمي العلامة B23؟',
  'Which category does sign B23 belong to?',
  'Tot welke categorie behoort bord B23?',
  'À quelle catégorie appartient le panneau B23?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B23' LIMIT 1),
  'العلامة B23 تنتمي إلى فئة علامات الأولوية',
  'Sign B23 belongs to Priority Signs',
  'Bord B23 behoort tot Voorrangsborden',
  'Le panneau B23 appartient à Panneaux de priorité',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  385,
  116,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  386,
  116,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  387,
  116,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  388,
  116,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  117,
  'ما هي العلامة المرورية B23؟',
  'What does the traffic sign B23 mean?',
  'Wat betekent verkeersbord B23?',
  'Que signifie le panneau de signalisation B23?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B23' LIMIT 1),
  'العلامة B23 تعني: علامة B23',
  'Sign B23 means: Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
  'Bord B23 betekent: Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
  'Le panneau B23 signifie: Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  389,
  117,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor voetgangers.',
  'Accès interdit',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  390,
  117,
  'علامة B23',
  'Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  391,
  117,
  'علامة F53',
  'Verplegingsinrichting.',
  'Verplegingsinrichting.',
  'Verplegingsinrichting.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  392,
  117,
  'علامة M9',
  'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  118,
  'العلامة B23 تعني: علامة B23. صحيح أم خطأ؟',
  'Sign B23 means: Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden. True or False?',
  'Bord B23 betekent: Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden. Waar of Onwaar?',
  'Le panneau B23 signifie: Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'B'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'B23' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  393,
  118,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  394,
  118,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  119,
  'ما هي العلامة المرورية C1؟',
  'What does the traffic sign C1 mean?',
  'Wat betekent verkeersbord C1?',
  'Que signifie le panneau de signalisation C1?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C1' LIMIT 1),
  'العلامة C1 تعني: اتجاه ممنوع',
  'Sign C1 means: Direction prohibited',
  'Bord C1 betekent: Verboden richting voor iedere bestuurder',
  'Le panneau C1 signifie: Direction interdite',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  395,
  119,
  'علامة M16',
  'Verbod voor bromfietsen klasse B en speed pedelecs.',
  'Verbod voor bromfietsen klasse B en speed pedelecs.',
  'Verbod voor bromfietsen klasse B en speed pedelecs.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  396,
  119,
  'علامة M4',
  'Fietsers mogen in 2 richtingen.',
  'Fietsers mogen in 2 richtingen.',
  'Fietsers mogen in 2 richtingen.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  397,
  119,
  'علامة C25',
  'Verboden voor voertuigen langer dan het aangeduide',
  'Verboden voor voertuigen langer dan het aangeduide',
  'Verboden voor voertuigen langer dan het aangeduide',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  398,
  119,
  'اتجاه ممنوع',
  'Direction prohibited',
  'Verboden richting voor iedere bestuurder',
  'Direction interdite',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  120,
  'إلى أي فئة تنتمي العلامة C1؟',
  'Which category does sign C1 belong to?',
  'Tot welke categorie behoort bord C1?',
  'À quelle catégorie appartient le panneau C1?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C1' LIMIT 1),
  'العلامة C1 تنتمي إلى فئة علامات المنع',
  'Sign C1 belongs to Prohibition Signs',
  'Bord C1 behoort tot Verbodsborden',
  'Le panneau C1 appartient à Panneaux d''interdiction',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  399,
  120,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  400,
  120,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  401,
  120,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  402,
  120,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  121,
  'إلى أي فئة تنتمي العلامة C3؟',
  'Which category does sign C3 belong to?',
  'Tot welke categorie behoort bord C3?',
  'À quelle catégorie appartient le panneau C3?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C3' LIMIT 1),
  'العلامة C3 تنتمي إلى فئة علامات المنع',
  'Sign C3 belongs to Prohibition Signs',
  'Bord C3 behoort tot Verbodsborden',
  'Le panneau C3 appartient à Panneaux d''interdiction',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  403,
  121,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  404,
  121,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  405,
  121,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  406,
  121,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  122,
  'العلامة C3 تعني: ممنوع الدخول. صحيح أم خطأ؟',
  'Sign C3 means: No entry. True or False?',
  'Bord C3 betekent: Verboden toegang, in beide richtingen, voor iedere bestuurder.. Waar of Onwaar?',
  'Le panneau C3 signifie: Accès interdit. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C3' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  407,
  122,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  408,
  122,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  123,
  'إلى أي فئة تنتمي العلامة C5؟',
  'Which category does sign C5 belong to?',
  'Tot welke categorie behoort bord C5?',
  'À quelle catégorie appartient le panneau C5?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C5' LIMIT 1),
  'العلامة C5 تنتمي إلى فئة علامات المنع',
  'Sign C5 belongs to Prohibition Signs',
  'Bord C5 behoort tot Verbodsborden',
  'Le panneau C5 appartient à Panneaux d''interdiction',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  409,
  123,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  410,
  123,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  411,
  123,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  412,
  123,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  124,
  'العلامة C5 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign C5 means: This sign is optional. True or False?',
  'Bord C5 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau C5 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C5' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  413,
  124,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  414,
  124,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  125,
  'إلى أي فئة تنتمي العلامة C7؟',
  'Which category does sign C7 belong to?',
  'Tot welke categorie behoort bord C7?',
  'À quelle catégorie appartient le panneau C7?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C7' LIMIT 1),
  'العلامة C7 تنتمي إلى فئة علامات المنع',
  'Sign C7 belongs to Prohibition Signs',
  'Bord C7 behoort tot Verbodsborden',
  'Le panneau C7 appartient à Panneaux d''interdiction',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  415,
  125,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  416,
  125,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  417,
  125,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  418,
  125,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  126,
  'العلامة C7 تعني: ممنوع الدخول. صحيح أم خطأ؟',
  'Sign C7 means: No entry. True or False?',
  'Bord C7 betekent: Verboden toegang voor bestuurders van motorfietsen.. Waar of Onwaar?',
  'Le panneau C7 signifie: Accès interdit. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C7' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  419,
  126,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  420,
  126,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  127,
  'ما هي العلامة المرورية C7؟',
  'What does the traffic sign C7 mean?',
  'Wat betekent verkeersbord C7?',
  'Que signifie le panneau de signalisation C7?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C7' LIMIT 1),
  'العلامة C7 تعني: ممنوع الدخول',
  'Sign C7 means: No entry',
  'Bord C7 betekent: Verboden toegang voor bestuurders van motorfietsen.',
  'Le panneau C7 signifie: Accès interdit',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  421,
  127,
  'تضييق الطريق',
  'Road narrowing',
  'Rijbaanversmalling',
  'Rétrécissement de chaussée',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  422,
  127,
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Enkel voor fietsers en bromfietsers.',
  'Cyclistes et cyclomotoristes',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  423,
  127,
  'علامة D3b',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  424,
  127,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van motorfietsen.',
  'Accès interdit',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  128,
  'العلامة C9 تعني: ممنوع الدخول. صحيح أم خطأ؟',
  'Sign C9 means: No entry. True or False?',
  'Bord C9 betekent: Verboden toegang voor bestuurders van bromfietsen en fietsen.. Waar of Onwaar?',
  'Le panneau C9 signifie: Accès interdit. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C9' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  425,
  128,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  426,
  128,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  129,
  'إلى أي فئة تنتمي العلامة C9؟',
  'Which category does sign C9 belong to?',
  'Tot welke categorie behoort bord C9?',
  'À quelle catégorie appartient le panneau C9?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C9' LIMIT 1),
  'العلامة C9 تنتمي إلى فئة علامات المنع',
  'Sign C9 belongs to Prohibition Signs',
  'Bord C9 behoort tot Verbodsborden',
  'Le panneau C9 appartient à Panneaux d''interdiction',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  427,
  129,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  428,
  129,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  429,
  129,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  430,
  129,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  130,
  'ما هي العلامة المرورية C9؟',
  'What does the traffic sign C9 mean?',
  'Wat betekent verkeersbord C9?',
  'Que signifie le panneau de signalisation C9?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C9' LIMIT 1),
  'العلامة C9 تعني: ممنوع الدخول',
  'Sign C9 means: No entry',
  'Bord C9 betekent: Verboden toegang voor bestuurders van bromfietsen en fietsen.',
  'Le panneau C9 signifie: Accès interdit',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  431,
  130,
  'صعود خطر',
  'Dangerous ascent',
  'Gevaarlijke helling.',
  'Montée dangereuse',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  432,
  130,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van bromfietsen en fietsen.',
  'Accès interdit',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  433,
  130,
  'علامة A41',
  'Overweg met slagbomen.',
  'Overweg met slagbomen.',
  'Overweg met slagbomen.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  434,
  130,
  'علامة A37',
  'Zijwind.',
  'Zijwind.',
  'Zijwind.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  131,
  'إلى أي فئة تنتمي العلامة C11؟',
  'Which category does sign C11 belong to?',
  'Tot welke categorie behoort bord C11?',
  'À quelle catégorie appartient le panneau C11?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C11' LIMIT 1),
  'العلامة C11 تنتمي إلى فئة علامات المنع',
  'Sign C11 belongs to Prohibition Signs',
  'Bord C11 behoort tot Verbodsborden',
  'Le panneau C11 appartient à Panneaux d''interdiction',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  435,
  131,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  436,
  131,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  437,
  131,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  438,
  131,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  132,
  'ما هي العلامة المرورية C11؟',
  'What does the traffic sign C11 mean?',
  'Wat betekent verkeersbord C11?',
  'Que signifie le panneau de signalisation C11?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C11' LIMIT 1),
  'العلامة C11 تعني: ممنوع الدخول',
  'Sign C11 means: No entry',
  'Bord C11 betekent: Verboden toegang voor bestuurders van rijwielen.',
  'Le panneau C11 signifie: Accès interdit',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  439,
  132,
  'شارع الدراجات',
  'Cycle street',
  'Einde fietsstraat.',
  'Rue cyclable',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  440,
  132,
  'منطقة سكنية',
  'Residential zone',
  'Begin van een woonerf of van een erf.',
  'Zone résidentielle',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  441,
  132,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van rijwielen.',
  'Accès interdit',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  442,
  132,
  'علامة M18',
  'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  133,
  'إلى أي فئة تنتمي العلامة C13؟',
  'Which category does sign C13 belong to?',
  'Tot welke categorie behoort bord C13?',
  'À quelle catégorie appartient le panneau C13?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C13' LIMIT 1),
  'العلامة C13 تنتمي إلى فئة علامات المنع',
  'Sign C13 belongs to Prohibition Signs',
  'Bord C13 behoort tot Verbodsborden',
  'Le panneau C13 appartient à Panneaux d''interdiction',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  443,
  133,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  444,
  133,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  445,
  133,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  446,
  133,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  134,
  'العلامة C13 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign C13 means: This sign is optional. True or False?',
  'Bord C13 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau C13 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C13' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  447,
  134,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  448,
  134,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  135,
  'ما هي العلامة المرورية C13؟',
  'What does the traffic sign C13 mean?',
  'Wat betekent verkeersbord C13?',
  'Que signifie le panneau de signalisation C13?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C13' LIMIT 1),
  'العلامة C13 تعني: ممنوع الدخول',
  'Sign C13 means: No entry',
  'Bord C13 betekent: Verboden toegang voor bestuurders van gespannen.',
  'Le panneau C13 signifie: Accès interdit',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  449,
  135,
  'علامة E9c',
  'Parkeren uitsluitend voorvrachtwagens.',
  'Parkeren uitsluitend voorvrachtwagens.',
  'Parkeren uitsluitend voorvrachtwagens.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  450,
  135,
  'علامة D3b',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  451,
  135,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van gespannen.',
  'Accès interdit',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  452,
  135,
  'علامة B23',
  'Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  136,
  'العلامة C15 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign C15 means: This sign is optional. True or False?',
  'Bord C15 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau C15 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C15' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  453,
  136,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  454,
  136,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  137,
  'إلى أي فئة تنتمي العلامة C15؟',
  'Which category does sign C15 belong to?',
  'Tot welke categorie behoort bord C15?',
  'À quelle catégorie appartient le panneau C15?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C15' LIMIT 1),
  'العلامة C15 تنتمي إلى فئة علامات المنع',
  'Sign C15 belongs to Prohibition Signs',
  'Bord C15 behoort tot Verbodsborden',
  'Le panneau C15 appartient à Panneaux d''interdiction',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  455,
  137,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  456,
  137,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  457,
  137,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  458,
  137,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  138,
  'العلامة C17 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign C17 means: This sign is optional. True or False?',
  'Bord C17 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau C17 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C17' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  459,
  138,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  460,
  138,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  139,
  'إلى أي فئة تنتمي العلامة C17؟',
  'Which category does sign C17 belong to?',
  'Tot welke categorie behoort bord C17?',
  'À quelle catégorie appartient le panneau C17?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C17' LIMIT 1),
  'العلامة C17 تنتمي إلى فئة علامات المنع',
  'Sign C17 belongs to Prohibition Signs',
  'Bord C17 behoort tot Verbodsborden',
  'Le panneau C17 appartient à Panneaux d''interdiction',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  461,
  139,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  462,
  139,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  463,
  139,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  464,
  139,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  140,
  'ما هي العلامة المرورية C19؟',
  'What does the traffic sign C19 mean?',
  'Wat betekent verkeersbord C19?',
  'Que signifie le panneau de signalisation C19?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C19' LIMIT 1),
  'العلامة C19 تعني: ممنوع الدخول',
  'Sign C19 means: No entry',
  'Bord C19 betekent: Verboden toegang voor voetgangers.',
  'Le panneau C19 signifie: Accès interdit',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  465,
  140,
  'علامة A50',
  'Opgelet file',
  'Opgelet file',
  'Opgelet file',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  466,
  140,
  'علامة D13',
  'Verplichte weg voor ruiters.',
  'Verplichte weg voor ruiters.',
  'Verplichte weg voor ruiters.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  467,
  140,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen die gevaarlijke verontreinigende stoffen vervoeren.',
  'Accès interdit',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  468,
  140,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor voetgangers.',
  'Accès interdit',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  141,
  'إلى أي فئة تنتمي العلامة C19؟',
  'Which category does sign C19 belong to?',
  'Tot welke categorie behoort bord C19?',
  'À quelle catégorie appartient le panneau C19?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C19' LIMIT 1),
  'العلامة C19 تنتمي إلى فئة علامات المنع',
  'Sign C19 belongs to Prohibition Signs',
  'Bord C19 behoort tot Verbodsborden',
  'Le panneau C19 appartient à Panneaux d''interdiction',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  469,
  141,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  470,
  141,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  471,
  141,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  472,
  141,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  142,
  'العلامة C19 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign C19 means: This sign is optional. True or False?',
  'Bord C19 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau C19 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C19' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  473,
  142,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  474,
  142,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  143,
  'ما هي العلامة المرورية C21؟',
  'What does the traffic sign C21 mean?',
  'Wat betekent verkeersbord C21?',
  'Que signifie le panneau de signalisation C21?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C21' LIMIT 1),
  'العلامة C21 تعني: ممنوع الدخول',
  'Sign C21 means: No entry',
  'Bord C21 betekent: Verboden toegang voor bestuurders van voertuigen waarvan de massa in beladen toestand hoger is dan de aangeduide massa.',
  'Le panneau C21 signifie: Accès interdit',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  475,
  143,
  'علامة F59a',
  'Aankondiging van een parking.',
  'Aankondiging van een parking.',
  'Aankondiging van een parking.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  476,
  143,
  'علامة E9a',
  'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  477,
  143,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen waarvan de massa in beladen toestand hoger is dan de aangeduide massa.',
  'Accès interdit',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  478,
  143,
  'علامة F34c',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  144,
  'العلامة C21 تعني: ممنوع الدخول. صحيح أم خطأ؟',
  'Sign C21 means: No entry. True or False?',
  'Bord C21 betekent: Verboden toegang voor bestuurders van voertuigen waarvan de massa in beladen toestand hoger is dan de aangeduide massa.. Waar of Onwaar?',
  'Le panneau C21 signifie: Accès interdit. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C21' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  479,
  144,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  480,
  144,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  145,
  'إلى أي فئة تنتمي العلامة C21؟',
  'Which category does sign C21 belong to?',
  'Tot welke categorie behoort bord C21?',
  'À quelle catégorie appartient le panneau C21?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C21' LIMIT 1),
  'العلامة C21 تنتمي إلى فئة علامات المنع',
  'Sign C21 belongs to Prohibition Signs',
  'Bord C21 behoort tot Verbodsborden',
  'Le panneau C21 appartient à Panneaux d''interdiction',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  481,
  145,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  482,
  145,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  483,
  145,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  484,
  145,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  146,
  'إلى أي فئة تنتمي العلامة C22؟',
  'Which category does sign C22 belong to?',
  'Tot welke categorie behoort bord C22?',
  'À quelle catégorie appartient le panneau C22?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C22' LIMIT 1),
  'العلامة C22 تنتمي إلى فئة علامات المنع',
  'Sign C22 belongs to Prohibition Signs',
  'Bord C22 behoort tot Verbodsborden',
  'Le panneau C22 appartient à Panneaux d''interdiction',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  485,
  146,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  486,
  146,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  487,
  146,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  488,
  146,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  147,
  'ما هي العلامة المرورية C22؟',
  'What does the traffic sign C22 mean?',
  'Wat betekent verkeersbord C22?',
  'Que signifie le panneau de signalisation C22?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C22' LIMIT 1),
  'العلامة C22 تعني: ممنوع الدخول',
  'Sign C22 means: No entry',
  'Bord C22 betekent: Verboden toegang voor bestuurders van autocars.',
  'Le panneau C22 signifie: Accès interdit',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  489,
  147,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van autocars.',
  'Accès interdit',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  490,
  147,
  'علامة C29',
  'Verboden voor voertuigen hoger dan het aangeduide.',
  'Verboden voor voertuigen hoger dan het aangeduide.',
  'Verboden voor voertuigen hoger dan het aangeduide.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  491,
  147,
  'علامة F60',
  'Overdekte parking.',
  'Overdekte parking.',
  'Overdekte parking.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  492,
  147,
  'علامة F19',
  'Eenrichtingsverkeer.',
  'Eenrichtingsverkeer.',
  'Eenrichtingsverkeer.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  148,
  'العلامة C22 تعني: ممنوع الدخول. صحيح أم خطأ؟',
  'Sign C22 means: No entry. True or False?',
  'Bord C22 betekent: Verboden toegang voor bestuurders van autocars.. Waar of Onwaar?',
  'Le panneau C22 signifie: Accès interdit. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C22' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  493,
  148,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  494,
  148,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  149,
  'العلامة C23 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign C23 means: This sign is optional. True or False?',
  'Bord C23 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau C23 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C23' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  495,
  149,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  496,
  149,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  150,
  'ما هي العلامة المرورية C23؟',
  'What does the traffic sign C23 mean?',
  'Wat betekent verkeersbord C23?',
  'Que signifie le panneau de signalisation C23?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C23' LIMIT 1),
  'العلامة C23 تعني: ممنوع الدخول',
  'Sign C23 means: No entry',
  'Bord C23 betekent: Verboden toegang voor bestuurders van voertuigen bestemd of gebruikt voor het vervoer van zaken.',
  'Le panneau C23 signifie: Accès interdit',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  497,
  150,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen bestemd of gebruikt voor het vervoer van zaken.',
  'Accès interdit',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  498,
  150,
  'علامة D1f',
  'Verplicht de aangeduide richting te volgen (rechtsaf)',
  'Verplicht de aangeduide richting te volgen (rechtsaf)',
  'Verplicht de aangeduide richting te volgen (rechtsaf)',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  499,
  150,
  'علامة E9h',
  'Parkeren uitsluitend voor kampeerauto''s.',
  'Parkeren uitsluitend voor kampeerauto''s.',
  'Parkeren uitsluitend voor kampeerauto''s.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  500,
  150,
  'علامة F61',
  'Telefoon.',
  'Telefoon.',
  'Telefoon.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  151,
  'إلى أي فئة تنتمي العلامة C23؟',
  'Which category does sign C23 belong to?',
  'Tot welke categorie behoort bord C23?',
  'À quelle catégorie appartient le panneau C23?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C23' LIMIT 1),
  'العلامة C23 تنتمي إلى فئة علامات المنع',
  'Sign C23 belongs to Prohibition Signs',
  'Bord C23 behoort tot Verbodsborden',
  'Le panneau C23 appartient à Panneaux d''interdiction',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  501,
  151,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  502,
  151,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  503,
  151,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  504,
  151,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  152,
  'العلامة C24a تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign C24a means: This sign is optional. True or False?',
  'Bord C24a betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau C24a signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C24a' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  505,
  152,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  506,
  152,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  153,
  'ما هي العلامة المرورية C24a؟',
  'What does the traffic sign C24a mean?',
  'Wat betekent verkeersbord C24a?',
  'Que signifie le panneau de signalisation C24a?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C24a' LIMIT 1),
  'العلامة C24a تعني: ممنوع الدخول',
  'Sign C24a means: No entry',
  'Bord C24a betekent: Verboden toegang voor bestuurders van voertuigen die gevaarlijke goederen vervoeren.',
  'Le panneau C24a signifie: Accès interdit',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  507,
  153,
  'علامة E9a',
  'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  508,
  153,
  'علامة M4',
  'Fietsers mogen in 2 richtingen.',
  'Fietsers mogen in 2 richtingen.',
  'Fietsers mogen in 2 richtingen.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  509,
  153,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen die gevaarlijke goederen vervoeren.',
  'Accès interdit',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  510,
  153,
  'علامة F3a',
  'Einde van een bebouwde kom.',
  'Einde van een bebouwde kom.',
  'Einde van een bebouwde kom.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  154,
  'العلامة C24b تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign C24b means: This sign is optional. True or False?',
  'Bord C24b betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau C24b signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C24b' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  511,
  154,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  512,
  154,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  155,
  'ما هي العلامة المرورية C24b؟',
  'What does the traffic sign C24b mean?',
  'Wat betekent verkeersbord C24b?',
  'Que signifie le panneau de signalisation C24b?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C24b' LIMIT 1),
  'العلامة C24b تعني: ممنوع الدخول',
  'Sign C24b means: No entry',
  'Bord C24b betekent: Verboden toegang voor bestuurders van voertuigen die gevaarlijke ontvlambare of ontplofbare stoffen vervoeren.',
  'Le panneau C24b signifie: Accès interdit',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  513,
  155,
  'تضييق الطريق',
  'Road narrowing',
  'Rijbaanversmalling',
  'Rétrécissement de chaussée',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  514,
  155,
  'علامة F23c',
  'Nummer van een internationale weg.',
  'Nummer van een internationale weg.',
  'Nummer van een internationale weg.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  515,
  155,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen die gevaarlijke ontvlambare of ontplofbare stoffen vervoeren.',
  'Accès interdit',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  516,
  155,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van motorvoertuigen en motorfietsen.',
  'Accès interdit',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  156,
  'ما هي العلامة المرورية C24c؟',
  'What does the traffic sign C24c mean?',
  'Wat betekent verkeersbord C24c?',
  'Que signifie le panneau de signalisation C24c?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C24c' LIMIT 1),
  'العلامة C24c تعني: ممنوع الدخول',
  'Sign C24c means: No entry',
  'Bord C24c betekent: Verboden toegang voor bestuurders van voertuigen die gevaarlijke verontreinigende stoffen vervoeren.',
  'Le panneau C24c signifie: Accès interdit',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  517,
  156,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van rijwielen.',
  'Accès interdit',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  518,
  156,
  'منعطف خطر لليمين',
  'Dangerous bend to the right',
  'Gevaarlijke bocht naar rechts.',
  'Virage dangereux à droite',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  519,
  156,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen die gevaarlijke verontreinigende stoffen vervoeren.',
  'Accès interdit',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  520,
  156,
  'علامة M12',
  'Uitgezonderd fietsers, bromfietsers klasse A en speed pedelecs.',
  'Uitgezonderd fietsers, bromfietsers klasse A en speed pedelecs.',
  'Uitgezonderd fietsers, bromfietsers klasse A en speed pedelecs.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  157,
  'العلامة C24c تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign C24c means: This sign is optional. True or False?',
  'Bord C24c betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau C24c signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C24c' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  521,
  157,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  522,
  157,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  158,
  'إلى أي فئة تنتمي العلامة C24c؟',
  'Which category does sign C24c belong to?',
  'Tot welke categorie behoort bord C24c?',
  'À quelle catégorie appartient le panneau C24c?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C24c' LIMIT 1),
  'العلامة C24c تنتمي إلى فئة علامات المنع',
  'Sign C24c belongs to Prohibition Signs',
  'Bord C24c behoort tot Verbodsborden',
  'Le panneau C24c appartient à Panneaux d''interdiction',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  523,
  158,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  524,
  158,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  525,
  158,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  526,
  158,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  159,
  'ما هي العلامة المرورية C25؟',
  'What does the traffic sign C25 mean?',
  'Wat betekent verkeersbord C25?',
  'Que signifie le panneau de signalisation C25?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C25' LIMIT 1),
  'العلامة C25 تعني: علامة C25',
  'Sign C25 means: Verboden voor voertuigen langer dan het aangeduide',
  'Bord C25 betekent: Verboden voor voertuigen langer dan het aangeduide',
  'Le panneau C25 signifie: Verboden voor voertuigen langer dan het aangeduide',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  527,
  159,
  'علامة M9',
  'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  528,
  159,
  'علامة M20',
  'Enkel voor fietsers en speed pedelecs.',
  'Enkel voor fietsers en speed pedelecs.',
  'Enkel voor fietsers en speed pedelecs.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  529,
  159,
  'علامة C25',
  'Verboden voor voertuigen langer dan het aangeduide',
  'Verboden voor voertuigen langer dan het aangeduide',
  'Verboden voor voertuigen langer dan het aangeduide',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  530,
  159,
  'نفق',
  'Tunnel',
  'Tunnel.',
  'Tunnel',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  160,
  'العلامة C25 تعني: علامة C25. صحيح أم خطأ؟',
  'Sign C25 means: Verboden voor voertuigen langer dan het aangeduide. True or False?',
  'Bord C25 betekent: Verboden voor voertuigen langer dan het aangeduide. Waar of Onwaar?',
  'Le panneau C25 signifie: Verboden voor voertuigen langer dan het aangeduide. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C25' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  531,
  160,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  532,
  160,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  161,
  'إلى أي فئة تنتمي العلامة C25؟',
  'Which category does sign C25 belong to?',
  'Tot welke categorie behoort bord C25?',
  'À quelle catégorie appartient le panneau C25?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C25' LIMIT 1),
  'العلامة C25 تنتمي إلى فئة علامات المنع',
  'Sign C25 belongs to Prohibition Signs',
  'Bord C25 behoort tot Verbodsborden',
  'Le panneau C25 appartient à Panneaux d''interdiction',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  533,
  161,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  534,
  161,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  535,
  161,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  536,
  161,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  162,
  'العلامة C27 تعني: علامة C27. صحيح أم خطأ؟',
  'Sign C27 means: Verboden voor voertuigen breder dan het aangeduide.. True or False?',
  'Bord C27 betekent: Verboden voor voertuigen breder dan het aangeduide.. Waar of Onwaar?',
  'Le panneau C27 signifie: Verboden voor voertuigen breder dan het aangeduide.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C27' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  537,
  162,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  538,
  162,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  163,
  'إلى أي فئة تنتمي العلامة C27؟',
  'Which category does sign C27 belong to?',
  'Tot welke categorie behoort bord C27?',
  'À quelle catégorie appartient le panneau C27?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C27' LIMIT 1),
  'العلامة C27 تنتمي إلى فئة علامات المنع',
  'Sign C27 belongs to Prohibition Signs',
  'Bord C27 behoort tot Verbodsborden',
  'Le panneau C27 appartient à Panneaux d''interdiction',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  539,
  163,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  540,
  163,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  541,
  163,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  542,
  163,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  164,
  'ما هي العلامة المرورية C27؟',
  'What does the traffic sign C27 mean?',
  'Wat betekent verkeersbord C27?',
  'Que signifie le panneau de signalisation C27?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C27' LIMIT 1),
  'العلامة C27 تعني: علامة C27',
  'Sign C27 means: Verboden voor voertuigen breder dan het aangeduide.',
  'Bord C27 betekent: Verboden voor voertuigen breder dan het aangeduide.',
  'Le panneau C27 signifie: Verboden voor voertuigen breder dan het aangeduide.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  543,
  164,
  'أعط الأولوية',
  'Give way',
  'Smalle doorgang voorrang verlenen aan de bestuurders die uit de tegenovergestelde richting komen',
  'Cédez le passage',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  544,
  164,
  'علامة D3b',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  545,
  164,
  'علامة C27',
  'Verboden voor voertuigen breder dan het aangeduide.',
  'Verboden voor voertuigen breder dan het aangeduide.',
  'Verboden voor voertuigen breder dan het aangeduide.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  546,
  164,
  'علامة F45',
  'Doodlopende weg, rechtse doorgang.',
  'Doodlopende weg, rechtse doorgang.',
  'Doodlopende weg, rechtse doorgang.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  165,
  'العلامة C29 تعني: علامة C29. صحيح أم خطأ؟',
  'Sign C29 means: Verboden voor voertuigen hoger dan het aangeduide.. True or False?',
  'Bord C29 betekent: Verboden voor voertuigen hoger dan het aangeduide.. Waar of Onwaar?',
  'Le panneau C29 signifie: Verboden voor voertuigen hoger dan het aangeduide.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C29' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  547,
  165,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  548,
  165,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  166,
  'ما هي العلامة المرورية C29؟',
  'What does the traffic sign C29 mean?',
  'Wat betekent verkeersbord C29?',
  'Que signifie le panneau de signalisation C29?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C29' LIMIT 1),
  'العلامة C29 تعني: علامة C29',
  'Sign C29 means: Verboden voor voertuigen hoger dan het aangeduide.',
  'Bord C29 betekent: Verboden voor voertuigen hoger dan het aangeduide.',
  'Le panneau C29 signifie: Verboden voor voertuigen hoger dan het aangeduide.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  549,
  166,
  'علامة M14',
  'Verplichting voor bromfietsen klasse B en Speed pedelecs.',
  'Verplichting voor bromfietsen klasse B en Speed pedelecs.',
  'Verplichting voor bromfietsen klasse B en Speed pedelecs.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  550,
  166,
  'علامة C29',
  'Verboden voor voertuigen hoger dan het aangeduide.',
  'Verboden voor voertuigen hoger dan het aangeduide.',
  'Verboden voor voertuigen hoger dan het aangeduide.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  551,
  166,
  'علامة A31',
  'Werken.',
  'Werken.',
  'Werken.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  552,
  166,
  'علامة A41',
  'Overweg met slagbomen.',
  'Overweg met slagbomen.',
  'Overweg met slagbomen.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  167,
  'ما هي العلامة المرورية C31a؟',
  'What does the traffic sign C31a mean?',
  'Wat betekent verkeersbord C31a?',
  'Que signifie le panneau de signalisation C31a?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C31a' LIMIT 1),
  'العلامة C31a تعني: علامة C31a',
  'Sign C31a means: Verbod om links af te slaan.',
  'Bord C31a betekent: Verbod om links af te slaan.',
  'Le panneau C31a signifie: Verbod om links af te slaan.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  553,
  167,
  'علامة C31a',
  'Verbod om links af te slaan.',
  'Verbod om links af te slaan.',
  'Verbod om links af te slaan.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  554,
  167,
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Oversteekplaats voor fietsers en bromfietsers.',
  'Cyclistes et cyclomotoristes',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  555,
  167,
  'علامة F34b',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  556,
  167,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor ruiters.',
  'Accès interdit',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  168,
  'العلامة C31a تعني: علامة C31a. صحيح أم خطأ؟',
  'Sign C31a means: Verbod om links af te slaan.. True or False?',
  'Bord C31a betekent: Verbod om links af te slaan.. Waar of Onwaar?',
  'Le panneau C31a signifie: Verbod om links af te slaan.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C31a' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  557,
  168,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  558,
  168,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  169,
  'العلامة C31b تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign C31b means: This sign is optional. True or False?',
  'Bord C31b betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau C31b signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C31b' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  559,
  169,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  560,
  169,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  170,
  'ما هي العلامة المرورية C31b؟',
  'What does the traffic sign C31b mean?',
  'Wat betekent verkeersbord C31b?',
  'Que signifie le panneau de signalisation C31b?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C31b' LIMIT 1),
  'العلامة C31b تعني: علامة C31b',
  'Sign C31b means: Verbod rechts af te slaan.',
  'Bord C31b betekent: Verbod rechts af te slaan.',
  'Le panneau C31b signifie: Verbod rechts af te slaan.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  561,
  170,
  'ممنوع الانتظار',
  'Parking prohibited',
  'Parkeerverbod van de 16e tot het einde van de maand.',
  'Interdiction de stationner',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  562,
  170,
  'طريق يؤدي إلى رصيف أو شاطئ',
  'Road leads to quay or waterside',
  'Uitweg op kaai of oever.',
  'Route menant au quai ou à la rive',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  563,
  170,
  'علامة D9b',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  564,
  170,
  'علامة C31b',
  'Verbod rechts af te slaan.',
  'Verbod rechts af te slaan.',
  'Verbod rechts af te slaan.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  171,
  'العلامة C33 تعني: علامة C33. صحيح أم خطأ؟',
  'Sign C33 means: Verbod om te keren.. True or False?',
  'Bord C33 betekent: Verbod om te keren.. Waar of Onwaar?',
  'Le panneau C33 signifie: Verbod om te keren.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C33' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  565,
  171,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  566,
  171,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  172,
  'ما هي العلامة المرورية C33؟',
  'What does the traffic sign C33 mean?',
  'Wat betekent verkeersbord C33?',
  'Que signifie le panneau de signalisation C33?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C33' LIMIT 1),
  'العلامة C33 تعني: علامة C33',
  'Sign C33 means: Verbod om te keren.',
  'Bord C33 betekent: Verbod om te keren.',
  'Le panneau C33 signifie: Verbod om te keren.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  567,
  172,
  'علامة F105',
  'Einde van een voetgangerszone',
  'Einde van een voetgangerszone',
  'Einde van een voetgangerszone',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  568,
  172,
  'ممر دراجات إلزامي',
  'Compulsory cycle path',
  'Verplicht fietspad.',
  'Piste cyclable obligatoire',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  569,
  172,
  'علامة C45',
  'Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.',
  'Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.',
  'Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  570,
  172,
  'علامة C33',
  'Verbod om te keren.',
  'Verbod om te keren.',
  'Verbod om te keren.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  173,
  'ما هي العلامة المرورية C35؟',
  'What does the traffic sign C35 mean?',
  'Wat betekent verkeersbord C35?',
  'Que signifie le panneau de signalisation C35?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C35' LIMIT 1),
  'العلامة C35 تعني: علامة C35',
  'Sign C35 means: Verbod een voertuig links in te halen.',
  'Bord C35 betekent: Verbod een voertuig links in te halen.',
  'Le panneau C35 signifie: Verbod een voertuig links in te halen.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  571,
  173,
  'علامة C35',
  'Verbod een voertuig links in te halen.',
  'Verbod een voertuig links in te halen.',
  'Verbod een voertuig links in te halen.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  572,
  173,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor ruiters.',
  'Accès interdit',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  573,
  173,
  'علامة F71',
  'Kampeerterrein.',
  'Kampeerterrein.',
  'Kampeerterrein.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  574,
  173,
  'طريق سريع',
  'Motorway',
  'Autosnelweg.',
  'Autoroute',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  174,
  'العلامة C35 تعني: علامة C35. صحيح أم خطأ؟',
  'Sign C35 means: Verbod een voertuig links in te halen.. True or False?',
  'Bord C35 betekent: Verbod een voertuig links in te halen.. Waar of Onwaar?',
  'Le panneau C35 signifie: Verbod een voertuig links in te halen.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C35' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  575,
  174,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  576,
  174,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  175,
  'العلامة C37 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign C37 means: This sign is optional. True or False?',
  'Bord C37 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau C37 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C37' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  577,
  175,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  578,
  175,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  176,
  'ما هي العلامة المرورية C37؟',
  'What does the traffic sign C37 mean?',
  'Wat betekent verkeersbord C37?',
  'Que signifie le panneau de signalisation C37?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C37' LIMIT 1),
  'العلامة C37 تعني: علامة C37',
  'Sign C37 means: Einde verbod opgelegd door het verkeersbord C35',
  'Bord C37 betekent: Einde verbod opgelegd door het verkeersbord C35',
  'Le panneau C37 signifie: Einde verbod opgelegd door het verkeersbord C35',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  579,
  176,
  'علامة C37',
  'Einde verbod opgelegd door het verkeersbord C35',
  'Einde verbod opgelegd door het verkeersbord C35',
  'Einde verbod opgelegd door het verkeersbord C35',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  580,
  176,
  'علامة F56',
  'Brandblusapparaat.',
  'Brandblusapparaat.',
  'Brandblusapparaat.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  581,
  176,
  'علامة F71',
  'Kampeerterrein.',
  'Kampeerterrein.',
  'Kampeerterrein.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  582,
  176,
  'علامة A14',
  'Verhoogde inrichting.',
  'Verhoogde inrichting.',
  'Verhoogde inrichting.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  177,
  'ما هي العلامة المرورية C39؟',
  'What does the traffic sign C39 mean?',
  'Wat betekent verkeersbord C39?',
  'Que signifie le panneau de signalisation C39?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C39' LIMIT 1),
  'العلامة C39 تعني: علامة C39',
  'Sign C39 means: Verbod voertuigen met toegelaten massa > 3500 kg in te halen',
  'Bord C39 betekent: Verbod voertuigen met toegelaten massa > 3500 kg in te halen',
  'Le panneau C39 signifie: Verbod voertuigen met toegelaten massa > 3500 kg in te halen',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  583,
  177,
  'علامة F71',
  'Kampeerterrein.',
  'Kampeerterrein.',
  'Kampeerterrein.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  584,
  177,
  'علامة F19',
  'Eenrichtingsverkeer.',
  'Eenrichtingsverkeer.',
  'Eenrichtingsverkeer.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  585,
  177,
  'ممنوع الانتظار',
  'Parking prohibited',
  'Parkeerverbod.',
  'Interdiction de stationner',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  586,
  177,
  'علامة C39',
  'Verbod voertuigen met toegelaten massa > 3500 kg in te halen',
  'Verbod voertuigen met toegelaten massa > 3500 kg in te halen',
  'Verbod voertuigen met toegelaten massa > 3500 kg in te halen',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  178,
  'العلامة C39 تعني: علامة C39. صحيح أم خطأ؟',
  'Sign C39 means: Verbod voertuigen met toegelaten massa > 3500 kg in te halen. True or False?',
  'Bord C39 betekent: Verbod voertuigen met toegelaten massa > 3500 kg in te halen. Waar of Onwaar?',
  'Le panneau C39 signifie: Verbod voertuigen met toegelaten massa > 3500 kg in te halen. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C39' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  587,
  178,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  588,
  178,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  179,
  'إلى أي فئة تنتمي العلامة C41؟',
  'Which category does sign C41 belong to?',
  'Tot welke categorie behoort bord C41?',
  'À quelle catégorie appartient le panneau C41?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C41' LIMIT 1),
  'العلامة C41 تنتمي إلى فئة علامات المنع',
  'Sign C41 belongs to Prohibition Signs',
  'Bord C41 behoort tot Verbodsborden',
  'Le panneau C41 appartient à Panneaux d''interdiction',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  589,
  179,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  590,
  179,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  591,
  179,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  592,
  179,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  180,
  'ما هي العلامة المرورية C41؟',
  'What does the traffic sign C41 mean?',
  'Wat betekent verkeersbord C41?',
  'Que signifie le panneau de signalisation C41?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C41' LIMIT 1),
  'العلامة C41 تعني: علامة C41',
  'Sign C41 means: Einde van het verbod opgelegd door het verkeersbord C39.',
  'Bord C41 betekent: Einde van het verbod opgelegd door het verkeersbord C39.',
  'Le panneau C41 signifie: Einde van het verbod opgelegd door het verkeersbord C39.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  593,
  180,
  'علامة C41',
  'Einde van het verbod opgelegd door het verkeersbord C39.',
  'Einde van het verbod opgelegd door het verkeersbord C39.',
  'Einde van het verbod opgelegd door het verkeersbord C39.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  594,
  180,
  'علامة F75',
  'Jeugdherberg.',
  'Jeugdherberg.',
  'Jeugdherberg.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  595,
  180,
  'تضييق الطريق',
  'Road narrowing',
  'Rijbaanversmalling rechts',
  'Rétrécissement de chaussée',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  596,
  180,
  'طريق سيارات',
  'Expressway',
  'Einde van de autoweg.',
  'Route pour automobiles',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  181,
  'العلامة C41 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign C41 means: This sign is optional. True or False?',
  'Bord C41 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau C41 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C41' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  597,
  181,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  598,
  181,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  182,
  'ما هي العلامة المرورية C43؟',
  'What does the traffic sign C43 mean?',
  'Wat betekent verkeersbord C43?',
  'Que signifie le panneau de signalisation C43?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C43' LIMIT 1),
  'العلامة C43 تعني: علامة C43',
  'Sign C43 means: Verbod te rijden met een grotere snelheid dan is aangeduid.',
  'Bord C43 betekent: Verbod te rijden met een grotere snelheid dan is aangeduid.',
  'Le panneau C43 signifie: Verbod te rijden met een grotere snelheid dan is aangeduid.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  599,
  182,
  'علامة M14',
  'Verplichting voor bromfietsen klasse B en Speed pedelecs.',
  'Verplichting voor bromfietsen klasse B en Speed pedelecs.',
  'Verplichting voor bromfietsen klasse B en Speed pedelecs.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  600,
  182,
  'منطقة سكنية',
  'Residential zone',
  'Begin van een woonerf of van een erf.',
  'Zone résidentielle',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  601,
  182,
  'علامة F77',
  'Toeristische informatie.',
  'Toeristische informatie.',
  'Toeristische informatie.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  602,
  182,
  'علامة C43',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  183,
  'العلامة C43 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign C43 means: This sign is optional. True or False?',
  'Bord C43 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau C43 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C43' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  603,
  183,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  604,
  183,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  184,
  'إلى أي فئة تنتمي العلامة C43؟',
  'Which category does sign C43 belong to?',
  'Tot welke categorie behoort bord C43?',
  'À quelle catégorie appartient le panneau C43?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C43' LIMIT 1),
  'العلامة C43 تنتمي إلى فئة علامات المنع',
  'Sign C43 belongs to Prohibition Signs',
  'Bord C43 behoort tot Verbodsborden',
  'Le panneau C43 appartient à Panneaux d''interdiction',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  605,
  184,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  606,
  184,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  607,
  184,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  608,
  184,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  185,
  'العلامة C45 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign C45 means: This sign is optional. True or False?',
  'Bord C45 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau C45 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C45' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  609,
  185,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  610,
  185,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  186,
  'ما هي العلامة المرورية C45؟',
  'What does the traffic sign C45 mean?',
  'Wat betekent verkeersbord C45?',
  'Que signifie le panneau de signalisation C45?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C45' LIMIT 1),
  'العلامة C45 تعني: علامة C45',
  'Sign C45 means: Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.',
  'Bord C45 betekent: Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.',
  'Le panneau C45 signifie: Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  611,
  186,
  'علامة F1a',
  'Begin van een bebouwde kom.',
  'Begin van een bebouwde kom.',
  'Begin van een bebouwde kom.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  612,
  186,
  'علامة C45',
  'Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.',
  'Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.',
  'Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  613,
  186,
  'علامة F67',
  'Restaurant.',
  'Restaurant.',
  'Restaurant.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  614,
  186,
  'علامة F45b',
  'Doodlopende weg, uitgezonderd voetgangers en fietsers.',
  'Doodlopende weg, uitgezonderd voetgangers en fietsers.',
  'Doodlopende weg, uitgezonderd voetgangers en fietsers.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  187,
  'إلى أي فئة تنتمي العلامة C45؟',
  'Which category does sign C45 belong to?',
  'Tot welke categorie behoort bord C45?',
  'À quelle catégorie appartient le panneau C45?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C45' LIMIT 1),
  'العلامة C45 تنتمي إلى فئة علامات المنع',
  'Sign C45 belongs to Prohibition Signs',
  'Bord C45 behoort tot Verbodsborden',
  'Le panneau C45 appartient à Panneaux d''interdiction',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  615,
  187,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  616,
  187,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  617,
  187,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  618,
  187,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  188,
  'إلى أي فئة تنتمي العلامة C46؟',
  'Which category does sign C46 belong to?',
  'Tot welke categorie behoort bord C46?',
  'À quelle catégorie appartient le panneau C46?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C46' LIMIT 1),
  'العلامة C46 تنتمي إلى فئة علامات المنع',
  'Sign C46 belongs to Prohibition Signs',
  'Bord C46 behoort tot Verbodsborden',
  'Le panneau C46 appartient à Panneaux d''interdiction',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  619,
  188,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  620,
  188,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  621,
  188,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  622,
  188,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  189,
  'ما هي العلامة المرورية C46؟',
  'What does the traffic sign C46 mean?',
  'Wat betekent verkeersbord C46?',
  'Que signifie le panneau de signalisation C46?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C46' LIMIT 1),
  'العلامة C46 تعني: علامة C46',
  'Sign C46 means: Einde van alle plaatselijke verbodsbepalingen opgelegd aan de voertuigen in beweging.',
  'Bord C46 betekent: Einde van alle plaatselijke verbodsbepalingen opgelegd aan de voertuigen in beweging.',
  'Le panneau C46 signifie: Einde van alle plaatselijke verbodsbepalingen opgelegd aan de voertuigen in beweging.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  623,
  189,
  'علامة B15d',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  624,
  189,
  'علامة C46',
  'Einde van alle plaatselijke verbodsbepalingen opgelegd aan de voertuigen in beweging.',
  'Einde van alle plaatselijke verbodsbepalingen opgelegd aan de voertuigen in beweging.',
  'Einde van alle plaatselijke verbodsbepalingen opgelegd aan de voertuigen in beweging.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  625,
  189,
  'علامة F29',
  'Wegwijzer',
  'Wegwijzer',
  'Wegwijzer',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  626,
  189,
  'علامة E11',
  'Halfmaandelijks parkeren in gans de bebouwde kom.',
  'Halfmaandelijks parkeren in gans de bebouwde kom.',
  'Halfmaandelijks parkeren in gans de bebouwde kom.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  190,
  'ما هي العلامة المرورية C47؟',
  'What does the traffic sign C47 mean?',
  'Wat betekent verkeersbord C47?',
  'Que signifie le panneau de signalisation C47?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C47' LIMIT 1),
  'العلامة C47 تعني: قف',
  'Sign C47 means: Stop',
  'Bord C47 betekent: Tolpost. Verbod voorbij te rijden zonder te stoppen.',
  'Le panneau C47 signifie: Stop',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  627,
  190,
  'علامة F34a',
  'Nabijheid van inrichting die van openbaar of algemeen belang is.',
  'Nabijheid van inrichting die van openbaar of algemeen belang is.',
  'Nabijheid van inrichting die van openbaar of algemeen belang is.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  628,
  190,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor ruiters.',
  'Accès interdit',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  629,
  190,
  'علامة F101a',
  'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  630,
  190,
  'قف',
  'Stop',
  'Tolpost. Verbod voorbij te rijden zonder te stoppen.',
  'Stop',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  191,
  'إلى أي فئة تنتمي العلامة C47؟',
  'Which category does sign C47 belong to?',
  'Tot welke categorie behoort bord C47?',
  'À quelle catégorie appartient le panneau C47?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C47' LIMIT 1),
  'العلامة C47 تنتمي إلى فئة علامات المنع',
  'Sign C47 belongs to Prohibition Signs',
  'Bord C47 behoort tot Verbodsborden',
  'Le panneau C47 appartient à Panneaux d''interdiction',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  631,
  191,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  632,
  191,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  633,
  191,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  634,
  191,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  192,
  'العلامة C47 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign C47 means: This sign is optional. True or False?',
  'Bord C47 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau C47 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'C'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'C47' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  635,
  192,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  636,
  192,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  193,
  'إلى أي فئة تنتمي العلامة D1a؟',
  'Which category does sign D1a belong to?',
  'Tot welke categorie behoort bord D1a?',
  'À quelle catégorie appartient le panneau D1a?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D1a' LIMIT 1),
  'العلامة D1a تنتمي إلى فئة علامات الإلزام',
  'Sign D1a belongs to Mandatory Signs',
  'Bord D1a behoort tot Gebodsborden',
  'Le panneau D1a appartient à Panneaux d''obligation',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  637,
  193,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  638,
  193,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  639,
  193,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  640,
  193,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  194,
  'ما هي العلامة المرورية D1a؟',
  'What does the traffic sign D1a mean?',
  'Wat betekent verkeersbord D1a?',
  'Que signifie le panneau de signalisation D1a?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D1a' LIMIT 1),
  'العلامة D1a تعني: علامة D1a',
  'Sign D1a means: Verplichting rechtdoor.',
  'Bord D1a betekent: Verplichting rechtdoor.',
  'Le panneau D1a signifie: Verplichting rechtdoor.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  641,
  194,
  'علامة B15g',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  642,
  194,
  'علامة C45',
  'Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.',
  'Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.',
  'Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  643,
  194,
  'منطقة 30',
  'Zone 30',
  'Einde zone 30 km/u.',
  'Zone 30',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  644,
  194,
  'علامة D1a',
  'Verplichting rechtdoor.',
  'Verplichting rechtdoor.',
  'Verplichting rechtdoor.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  195,
  'ما هي العلامة المرورية D1b؟',
  'What does the traffic sign D1b mean?',
  'Wat betekent verkeersbord D1b?',
  'Que signifie le panneau de signalisation D1b?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D1b' LIMIT 1),
  'العلامة D1b تعني: علامة D1b',
  'Sign D1b means: Verplichting rechts afslaan.',
  'Bord D1b betekent: Verplichting rechts afslaan.',
  'Le panneau D1b signifie: Verplichting rechts afslaan.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  645,
  195,
  'علامة E9e',
  'Verplicht parkeren op de berm of op het trottoir.',
  'Verplicht parkeren op de berm of op het trottoir.',
  'Verplicht parkeren op de berm of op het trottoir.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  646,
  195,
  'علامة D1b',
  'Verplichting rechts afslaan.',
  'Verplichting rechts afslaan.',
  'Verplichting rechts afslaan.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  647,
  195,
  'علامة A37',
  'Zijwind.',
  'Zijwind.',
  'Zijwind.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  648,
  195,
  'علامة F50b',
  'Opgepast als je van richting veranderd, fietsers.',
  'Opgepast als je van richting veranderd, fietsers.',
  'Opgepast als je van richting veranderd, fietsers.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  196,
  'العلامة D1b تعني: علامة D1b. صحيح أم خطأ؟',
  'Sign D1b means: Verplichting rechts afslaan.. True or False?',
  'Bord D1b betekent: Verplichting rechts afslaan.. Waar of Onwaar?',
  'Le panneau D1b signifie: Verplichting rechts afslaan.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D1b' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  649,
  196,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  650,
  196,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  197,
  'إلى أي فئة تنتمي العلامة D1b؟',
  'Which category does sign D1b belong to?',
  'Tot welke categorie behoort bord D1b?',
  'À quelle catégorie appartient le panneau D1b?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D1b' LIMIT 1),
  'العلامة D1b تنتمي إلى فئة علامات الإلزام',
  'Sign D1b belongs to Mandatory Signs',
  'Bord D1b behoort tot Gebodsborden',
  'Le panneau D1b appartient à Panneaux d''obligation',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  651,
  197,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  652,
  197,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  653,
  197,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  654,
  197,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  198,
  'إلى أي فئة تنتمي العلامة D1c؟',
  'Which category does sign D1c belong to?',
  'Tot welke categorie behoort bord D1c?',
  'À quelle catégorie appartient le panneau D1c?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D1c' LIMIT 1),
  'العلامة D1c تنتمي إلى فئة علامات الإلزام',
  'Sign D1c belongs to Mandatory Signs',
  'Bord D1c behoort tot Gebodsborden',
  'Le panneau D1c appartient à Panneaux d''obligation',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  655,
  198,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  656,
  198,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  657,
  198,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  658,
  198,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  199,
  'ما هي العلامة المرورية D1c؟',
  'What does the traffic sign D1c mean?',
  'Wat betekent verkeersbord D1c?',
  'Que signifie le panneau de signalisation D1c?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D1c' LIMIT 1),
  'العلامة D1c تعني: علامة D1c',
  'Sign D1c means: Verplichting links aanhouden.',
  'Bord D1c betekent: Verplichting links aanhouden.',
  'Le panneau D1c signifie: Verplichting links aanhouden.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  659,
  199,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van motorvoertuigen en motorfietsen.',
  'Accès interdit',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  660,
  199,
  'علامة D9b',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  661,
  199,
  'علامة D1c',
  'Verplichting links aanhouden.',
  'Verplichting links aanhouden.',
  'Verplichting links aanhouden.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  662,
  199,
  'شارع الدراجات',
  'Cycle street',
  'Fietsstraat.',
  'Rue cyclable',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  200,
  'العلامة D1c تعني: علامة D1c. صحيح أم خطأ؟',
  'Sign D1c means: Verplichting links aanhouden.. True or False?',
  'Bord D1c betekent: Verplichting links aanhouden.. Waar of Onwaar?',
  'Le panneau D1c signifie: Verplichting links aanhouden.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D1c' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  663,
  200,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  664,
  200,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  201,
  'العلامة D1d تعني: علامة D1d. صحيح أم خطأ؟',
  'Sign D1d means: Verplichting rechts aanhouden.. True or False?',
  'Bord D1d betekent: Verplichting rechts aanhouden.. Waar of Onwaar?',
  'Le panneau D1d signifie: Verplichting rechts aanhouden.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D1d' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  665,
  201,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  666,
  201,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  202,
  'إلى أي فئة تنتمي العلامة D1d؟',
  'Which category does sign D1d belong to?',
  'Tot welke categorie behoort bord D1d?',
  'À quelle catégorie appartient le panneau D1d?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D1d' LIMIT 1),
  'العلامة D1d تنتمي إلى فئة علامات الإلزام',
  'Sign D1d belongs to Mandatory Signs',
  'Bord D1d behoort tot Gebodsborden',
  'Le panneau D1d appartient à Panneaux d''obligation',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  667,
  202,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  668,
  202,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  669,
  202,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  670,
  202,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  203,
  'ما هي العلامة المرورية D1d؟',
  'What does the traffic sign D1d mean?',
  'Wat betekent verkeersbord D1d?',
  'Que signifie le panneau de signalisation D1d?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D1d' LIMIT 1),
  'العلامة D1d تعني: علامة D1d',
  'Sign D1d means: Verplichting rechts aanhouden.',
  'Bord D1d betekent: Verplichting rechts aanhouden.',
  'Le panneau D1d signifie: Verplichting rechts aanhouden.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  671,
  203,
  'علامة D1d',
  'Verplichting rechts aanhouden.',
  'Verplichting rechts aanhouden.',
  'Verplichting rechts aanhouden.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  672,
  203,
  'علامة C33',
  'Verbod om te keren.',
  'Verbod om te keren.',
  'Verbod om te keren.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  673,
  203,
  'علامة F118',
  'Einde van een lage emissiezone',
  'Einde van een lage emissiezone',
  'Einde van een lage emissiezone',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  674,
  203,
  'معبر للمشاة',
  'Pedestrian crossing',
  'Oversteekplaats voor voetgangers.',
  'Passage pour piétons',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  204,
  'العلامة D1e تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign D1e means: This sign is optional. True or False?',
  'Bord D1e betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau D1e signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D1e' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  675,
  204,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  676,
  204,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  205,
  'ما هي العلامة المرورية D1e؟',
  'What does the traffic sign D1e mean?',
  'Wat betekent verkeersbord D1e?',
  'Que signifie le panneau de signalisation D1e?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D1e' LIMIT 1),
  'العلامة D1e تعني: علامة D1e',
  'Sign D1e means: Verplicht de aangeduide richting te volgen (linksaf)',
  'Bord D1e betekent: Verplicht de aangeduide richting te volgen (linksaf)',
  'Le panneau D1e signifie: Verplicht de aangeduide richting te volgen (linksaf)',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  677,
  205,
  'علامة A17',
  'Kiezelprojectie',
  'Kiezelprojectie',
  'Kiezelprojectie',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  678,
  205,
  'علامة F55',
  'Hulppost.',
  'Hulppost.',
  'Hulppost.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  679,
  205,
  'علامة D1e',
  'Verplicht de aangeduide richting te volgen (linksaf)',
  'Verplicht de aangeduide richting te volgen (linksaf)',
  'Verplicht de aangeduide richting te volgen (linksaf)',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  680,
  205,
  'علامة M2',
  'Uitgezonderd fietsers.',
  'Uitgezonderd fietsers.',
  'Uitgezonderd fietsers.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  206,
  'إلى أي فئة تنتمي العلامة D1f؟',
  'Which category does sign D1f belong to?',
  'Tot welke categorie behoort bord D1f?',
  'À quelle catégorie appartient le panneau D1f?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D1f' LIMIT 1),
  'العلامة D1f تنتمي إلى فئة علامات الإلزام',
  'Sign D1f belongs to Mandatory Signs',
  'Bord D1f behoort tot Gebodsborden',
  'Le panneau D1f appartient à Panneaux d''obligation',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  681,
  206,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  682,
  206,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  683,
  206,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  684,
  206,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  207,
  'ما هي العلامة المرورية D1f؟',
  'What does the traffic sign D1f mean?',
  'Wat betekent verkeersbord D1f?',
  'Que signifie le panneau de signalisation D1f?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D1f' LIMIT 1),
  'العلامة D1f تعني: علامة D1f',
  'Sign D1f means: Verplicht de aangeduide richting te volgen (rechtsaf)',
  'Bord D1f betekent: Verplicht de aangeduide richting te volgen (rechtsaf)',
  'Le panneau D1f signifie: Verplicht de aangeduide richting te volgen (rechtsaf)',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  685,
  207,
  'علامة F101b',
  'Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.',
  'Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.',
  'Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  686,
  207,
  'علامة D1f',
  'Verplicht de aangeduide richting te volgen (rechtsaf)',
  'Verplicht de aangeduide richting te volgen (rechtsaf)',
  'Verplicht de aangeduide richting te volgen (rechtsaf)',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  687,
  207,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van rijwielen.',
  'Accès interdit',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  688,
  207,
  'علامة A27',
  'Overstekend groot wild.',
  'Overstekend groot wild.',
  'Overstekend groot wild.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  208,
  'العلامة D1f تعني: علامة D1f. صحيح أم خطأ؟',
  'Sign D1f means: Verplicht de aangeduide richting te volgen (rechtsaf). True or False?',
  'Bord D1f betekent: Verplicht de aangeduide richting te volgen (rechtsaf). Waar of Onwaar?',
  'Le panneau D1f signifie: Verplicht de aangeduide richting te volgen (rechtsaf). Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D1f' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  689,
  208,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  690,
  208,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  209,
  'إلى أي فئة تنتمي العلامة D3a؟',
  'Which category does sign D3a belong to?',
  'Tot welke categorie behoort bord D3a?',
  'À quelle catégorie appartient le panneau D3a?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D3a' LIMIT 1),
  'العلامة D3a تنتمي إلى فئة علامات الإلزام',
  'Sign D3a belongs to Mandatory Signs',
  'Bord D3a behoort tot Gebodsborden',
  'Le panneau D3a appartient à Panneaux d''obligation',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  691,
  209,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  692,
  209,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  693,
  209,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  694,
  209,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  210,
  'العلامة D3a تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign D3a means: This sign is optional. True or False?',
  'Bord D3a betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau D3a signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D3a' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  695,
  210,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  696,
  210,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  211,
  'ما هي العلامة المرورية D3a؟',
  'What does the traffic sign D3a mean?',
  'Wat betekent verkeersbord D3a?',
  'Que signifie le panneau de signalisation D3a?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D3a' LIMIT 1),
  'العلامة D3a تعني: علامة D3a',
  'Sign D3a means: Verplicht één van de pijlen te volgen.',
  'Bord D3a betekent: Verplicht één van de pijlen te volgen.',
  'Le panneau D3a signifie: Verplicht één van de pijlen te volgen.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  697,
  211,
  'علامة A19',
  'Vallende stenen.',
  'Vallende stenen.',
  'Vallende stenen.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  698,
  211,
  'علامة D3a',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  699,
  211,
  'قف',
  'Stop',
  'Stoppen en voorrang verlenen',
  'Stop',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  700,
  211,
  'تضييق الطريق',
  'Road narrowing',
  'Rijbaanversmalling',
  'Rétrécissement de chaussée',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  212,
  'إلى أي فئة تنتمي العلامة D3b؟',
  'Which category does sign D3b belong to?',
  'Tot welke categorie behoort bord D3b?',
  'À quelle catégorie appartient le panneau D3b?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D3b' LIMIT 1),
  'العلامة D3b تنتمي إلى فئة علامات الإلزام',
  'Sign D3b belongs to Mandatory Signs',
  'Bord D3b behoort tot Gebodsborden',
  'Le panneau D3b appartient à Panneaux d''obligation',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  701,
  212,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  702,
  212,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  703,
  212,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  704,
  212,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  213,
  'العلامة D3b تعني: علامة D3b. صحيح أم خطأ؟',
  'Sign D3b means: Verplicht één van de pijlen te volgen.. True or False?',
  'Bord D3b betekent: Verplicht één van de pijlen te volgen.. Waar of Onwaar?',
  'Le panneau D3b signifie: Verplicht één van de pijlen te volgen.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D3b' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  705,
  213,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  706,
  213,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  214,
  'ما هي العلامة المرورية D4؟',
  'What does the traffic sign D4 mean?',
  'Wat betekent verkeersbord D4?',
  'Que signifie le panneau de signalisation D4?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D4' LIMIT 1),
  'العلامة D4 تعني: علامة D4',
  'Sign D4 means: Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.',
  'Bord D4 betekent: Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.',
  'Le panneau D4 signifie: Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  707,
  214,
  'علامة D11',
  'Verplichte weg voor voetgangers.',
  'Verplichte weg voor voetgangers.',
  'Verplichte weg voor voetgangers.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  708,
  214,
  'علامة D4',
  'Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.',
  'Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.',
  'Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  709,
  214,
  'علامة M9',
  'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  710,
  214,
  'أعط الأولوية',
  'Give way',
  'Voorrang verlenen',
  'Cédez le passage',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  215,
  'إلى أي فئة تنتمي العلامة D4؟',
  'Which category does sign D4 belong to?',
  'Tot welke categorie behoort bord D4?',
  'À quelle catégorie appartient le panneau D4?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D4' LIMIT 1),
  'العلامة D4 تنتمي إلى فئة علامات الإلزام',
  'Sign D4 belongs to Mandatory Signs',
  'Bord D4 behoort tot Gebodsborden',
  'Le panneau D4 appartient à Panneaux d''obligation',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  711,
  215,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  712,
  215,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  713,
  215,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  714,
  215,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  216,
  'إلى أي فئة تنتمي العلامة D5؟',
  'Which category does sign D5 belong to?',
  'Tot welke categorie behoort bord D5?',
  'À quelle catégorie appartient le panneau D5?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D5' LIMIT 1),
  'العلامة D5 تنتمي إلى فئة علامات الإلزام',
  'Sign D5 belongs to Mandatory Signs',
  'Bord D5 behoort tot Gebodsborden',
  'Le panneau D5 appartient à Panneaux d''obligation',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  715,
  216,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  716,
  216,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  717,
  216,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  718,
  216,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  217,
  'العلامة D5 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign D5 means: This sign is optional. True or False?',
  'Bord D5 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau D5 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D5' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  719,
  217,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  720,
  217,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  218,
  'ما هي العلامة المرورية D5؟',
  'What does the traffic sign D5 mean?',
  'Wat betekent verkeersbord D5?',
  'Que signifie le panneau de signalisation D5?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D5' LIMIT 1),
  'العلامة D5 تعني: علامة D5',
  'Sign D5 means: Verplicht rondgaand verkeer.',
  'Bord D5 betekent: Verplicht rondgaand verkeer.',
  'Le panneau D5 signifie: Verplicht rondgaand verkeer.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  721,
  218,
  'علامة F118',
  'Einde van een lage emissiezone',
  'Einde van een lage emissiezone',
  'Einde van een lage emissiezone',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  722,
  218,
  'علامة D5',
  'Verplicht rondgaand verkeer.',
  'Verplicht rondgaand verkeer.',
  'Verplicht rondgaand verkeer.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  723,
  218,
  'علامة E9j',
  'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  724,
  218,
  'علامة F53',
  'Verplegingsinrichting.',
  'Verplegingsinrichting.',
  'Verplegingsinrichting.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  219,
  'العلامة D7 تعني: ممر دراجات إلزامي. صحيح أم خطأ؟',
  'Sign D7 means: Compulsory cycle path. True or False?',
  'Bord D7 betekent: Verplicht fietspad.. Waar of Onwaar?',
  'Le panneau D7 signifie: Piste cyclable obligatoire. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D7' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  725,
  219,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  726,
  219,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  220,
  'ما هي العلامة المرورية D7؟',
  'What does the traffic sign D7 mean?',
  'Wat betekent verkeersbord D7?',
  'Que signifie le panneau de signalisation D7?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D7' LIMIT 1),
  'العلامة D7 تعني: ممر دراجات إلزامي',
  'Sign D7 means: Compulsory cycle path',
  'Bord D7 betekent: Verplicht fietspad.',
  'Le panneau D7 signifie: Piste cyclable obligatoire',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  727,
  220,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor ruiters.',
  'Accès interdit',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  728,
  220,
  'علامة F62',
  'Noodtelefoon.',
  'Noodtelefoon.',
  'Noodtelefoon.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  729,
  220,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van rijwielen.',
  'Accès interdit',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  730,
  220,
  'ممر دراجات إلزامي',
  'Compulsory cycle path',
  'Verplicht fietspad.',
  'Piste cyclable obligatoire',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  221,
  'العلامة D9a تعني: علامة D9a. صحيح أم خطأ؟',
  'Sign D9a means: Deel van de weg voorbehouden voor voetgangers en fietsers.. True or False?',
  'Bord D9a betekent: Deel van de weg voorbehouden voor voetgangers en fietsers.. Waar of Onwaar?',
  'Le panneau D9a signifie: Deel van de weg voorbehouden voor voetgangers en fietsers.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D9a' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  731,
  221,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  732,
  221,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  222,
  'إلى أي فئة تنتمي العلامة D9a؟',
  'Which category does sign D9a belong to?',
  'Tot welke categorie behoort bord D9a?',
  'À quelle catégorie appartient le panneau D9a?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D9a' LIMIT 1),
  'العلامة D9a تنتمي إلى فئة علامات الإلزام',
  'Sign D9a belongs to Mandatory Signs',
  'Bord D9a behoort tot Gebodsborden',
  'Le panneau D9a appartient à Panneaux d''obligation',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  733,
  222,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  734,
  222,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  735,
  222,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  736,
  222,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  223,
  'ما هي العلامة المرورية D9a؟',
  'What does the traffic sign D9a mean?',
  'Wat betekent verkeersbord D9a?',
  'Que signifie le panneau de signalisation D9a?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D9a' LIMIT 1),
  'العلامة D9a تعني: علامة D9a',
  'Sign D9a means: Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Bord D9a betekent: Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Le panneau D9a signifie: Deel van de weg voorbehouden voor voetgangers en fietsers.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  737,
  223,
  'علامة M4',
  'Fietsers mogen in 2 richtingen.',
  'Fietsers mogen in 2 richtingen.',
  'Fietsers mogen in 2 richtingen.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  738,
  223,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van motorfietsen.',
  'Accès interdit',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  739,
  223,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen bestemd of gebruikt voor het vervoer van zaken.',
  'Accès interdit',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  740,
  223,
  'علامة D9a',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  224,
  'العلامة D9b تعني: علامة D9b. صحيح أم خطأ؟',
  'Sign D9b means: Deel van de weg voorbehouden voor voetgangers en fietsers.. True or False?',
  'Bord D9b betekent: Deel van de weg voorbehouden voor voetgangers en fietsers.. Waar of Onwaar?',
  'Le panneau D9b signifie: Deel van de weg voorbehouden voor voetgangers en fietsers.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D9b' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  741,
  224,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  742,
  224,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  225,
  'ما هي العلامة المرورية D9b؟',
  'What does the traffic sign D9b mean?',
  'Wat betekent verkeersbord D9b?',
  'Que signifie le panneau de signalisation D9b?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D9b' LIMIT 1),
  'العلامة D9b تعني: علامة D9b',
  'Sign D9b means: Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Bord D9b betekent: Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Le panneau D9b signifie: Deel van de weg voorbehouden voor voetgangers en fietsers.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  743,
  225,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen waarvan de massa in beladen toestand hoger is dan de aangeduide massa.',
  'Accès interdit',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  744,
  225,
  'علامة D9b',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  745,
  225,
  'علامة F23c',
  'Nummer van een internationale weg.',
  'Nummer van een internationale weg.',
  'Nummer van een internationale weg.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  746,
  225,
  'علامة A43',
  'Overweg zonder slagbomen.',
  'Overweg zonder slagbomen.',
  'Overweg zonder slagbomen.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  226,
  'إلى أي فئة تنتمي العلامة D10؟',
  'Which category does sign D10 belong to?',
  'Tot welke categorie behoort bord D10?',
  'À quelle catégorie appartient le panneau D10?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D10' LIMIT 1),
  'العلامة D10 تنتمي إلى فئة علامات الإلزام',
  'Sign D10 belongs to Mandatory Signs',
  'Bord D10 behoort tot Gebodsborden',
  'Le panneau D10 appartient à Panneaux d''obligation',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  747,
  226,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  748,
  226,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  749,
  226,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  750,
  226,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  227,
  'العلامة D10 تعني: علامة D10. صحيح أم خطأ؟',
  'Sign D10 means: Deel van de weg voorbehouden voor voetgangers en fietsers.. True or False?',
  'Bord D10 betekent: Deel van de weg voorbehouden voor voetgangers en fietsers.. Waar of Onwaar?',
  'Le panneau D10 signifie: Deel van de weg voorbehouden voor voetgangers en fietsers.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D10' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  751,
  227,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  752,
  227,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  228,
  'العلامة D11 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign D11 means: This sign is optional. True or False?',
  'Bord D11 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau D11 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D11' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  753,
  228,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  754,
  228,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  229,
  'إلى أي فئة تنتمي العلامة D11؟',
  'Which category does sign D11 belong to?',
  'Tot welke categorie behoort bord D11?',
  'À quelle catégorie appartient le panneau D11?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D11' LIMIT 1),
  'العلامة D11 تنتمي إلى فئة علامات الإلزام',
  'Sign D11 belongs to Mandatory Signs',
  'Bord D11 behoort tot Gebodsborden',
  'Le panneau D11 appartient à Panneaux d''obligation',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  755,
  229,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  756,
  229,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  757,
  229,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  758,
  229,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  230,
  'ما هي العلامة المرورية D11؟',
  'What does the traffic sign D11 mean?',
  'Wat betekent verkeersbord D11?',
  'Que signifie le panneau de signalisation D11?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D11' LIMIT 1),
  'العلامة D11 تعني: علامة D11',
  'Sign D11 means: Verplichte weg voor voetgangers.',
  'Bord D11 betekent: Verplichte weg voor voetgangers.',
  'Le panneau D11 signifie: Verplichte weg voor voetgangers.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  759,
  230,
  'قف',
  'Stop',
  'Tolpost. Verbod voorbij te rijden zonder te stoppen.',
  'Stop',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  760,
  230,
  'علامة C46',
  'Einde van alle plaatselijke verbodsbepalingen opgelegd aan de voertuigen in beweging.',
  'Einde van alle plaatselijke verbodsbepalingen opgelegd aan de voertuigen in beweging.',
  'Einde van alle plaatselijke verbodsbepalingen opgelegd aan de voertuigen in beweging.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  761,
  230,
  'علامة B17',
  'Kruispunt waar de voorrang van rechts geldt',
  'Kruispunt waar de voorrang van rechts geldt',
  'Kruispunt waar de voorrang van rechts geldt',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  762,
  230,
  'علامة D11',
  'Verplichte weg voor voetgangers.',
  'Verplichte weg voor voetgangers.',
  'Verplichte weg voor voetgangers.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  231,
  'ما هي العلامة المرورية D13؟',
  'What does the traffic sign D13 mean?',
  'Wat betekent verkeersbord D13?',
  'Que signifie le panneau de signalisation D13?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D13' LIMIT 1),
  'العلامة D13 تعني: علامة D13',
  'Sign D13 means: Verplichte weg voor ruiters.',
  'Bord D13 betekent: Verplichte weg voor ruiters.',
  'Le panneau D13 signifie: Verplichte weg voor ruiters.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  763,
  231,
  'علامة D13',
  'Verplichte weg voor ruiters.',
  'Verplichte weg voor ruiters.',
  'Verplichte weg voor ruiters.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  764,
  231,
  'علامة D1e',
  'Verplicht de aangeduide richting te volgen (linksaf)',
  'Verplicht de aangeduide richting te volgen (linksaf)',
  'Verplicht de aangeduide richting te volgen (linksaf)',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  765,
  231,
  'علامة F33a',
  'Bewegwijzeringsbord op afstand',
  'Bewegwijzeringsbord op afstand',
  'Bewegwijzeringsbord op afstand',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  766,
  231,
  'علامة F3b',
  'Einde van een bebouwde kom.',
  'Einde van een bebouwde kom.',
  'Einde van een bebouwde kom.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  232,
  'العلامة D13 تعني: علامة D13. صحيح أم خطأ؟',
  'Sign D13 means: Verplichte weg voor ruiters.. True or False?',
  'Bord D13 betekent: Verplichte weg voor ruiters.. Waar of Onwaar?',
  'Le panneau D13 signifie: Verplichte weg voor ruiters.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'D'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'D13' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  767,
  232,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  768,
  232,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  233,
  'العلامة E1 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign E1 means: This sign is optional. True or False?',
  'Bord E1 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau E1 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E1' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  769,
  233,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  770,
  233,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  234,
  'ما هي العلامة المرورية E1؟',
  'What does the traffic sign E1 mean?',
  'Wat betekent verkeersbord E1?',
  'Que signifie le panneau de signalisation E1?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E1' LIMIT 1),
  'العلامة E1 تعني: ممنوع الانتظار',
  'Sign E1 means: Parking prohibited',
  'Bord E1 betekent: Parkeerverbod.',
  'Le panneau E1 signifie: Interdiction de stationner',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  771,
  234,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang, in beide richtingen, voor iedere bestuurder.',
  'Accès interdit',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  772,
  234,
  'علامة F23d',
  'Nummer van een ringweg.',
  'Nummer van een ringweg.',
  'Nummer van een ringweg.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  773,
  234,
  'ممنوع الانتظار',
  'Parking prohibited',
  'Parkeerverbod.',
  'Interdiction de stationner',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  774,
  234,
  'علامة F103',
  'Begin van een voetgangerszone',
  'Begin van een voetgangerszone',
  'Begin van een voetgangerszone',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  235,
  'إلى أي فئة تنتمي العلامة E1؟',
  'Which category does sign E1 belong to?',
  'Tot welke categorie behoort bord E1?',
  'À quelle catégorie appartient le panneau E1?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E1' LIMIT 1),
  'العلامة E1 تنتمي إلى فئة علامات الوقوف',
  'Sign E1 belongs to Parking Signs',
  'Bord E1 behoort tot Parkeerverbod',
  'Le panneau E1 appartient à Panneaux de stationnement',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  775,
  235,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  776,
  235,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  777,
  235,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  778,
  235,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  236,
  'ما هي العلامة المرورية E3؟',
  'What does the traffic sign E3 mean?',
  'Wat betekent verkeersbord E3?',
  'Que signifie le panneau de signalisation E3?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E3' LIMIT 1),
  'العلامة E3 تعني: ممنوع التوقف والانتظار',
  'Sign E3 means: No stopping or parking',
  'Bord E3 betekent: Stilstaan en parkeren verboden.',
  'Le panneau E3 signifie: Arrêt et stationnement interdits',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  779,
  236,
  'طريق سريع',
  'Motorway',
  'Nummer van een autosnelweg.',
  'Autoroute',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  780,
  236,
  'علامة A15',
  'Gladde rijbaan - Slipgevaar.',
  'Gladde rijbaan - Slipgevaar.',
  'Gladde rijbaan - Slipgevaar.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  781,
  236,
  'ممنوع التوقف والانتظار',
  'No stopping or parking',
  'Stilstaan en parkeren verboden.',
  'Arrêt et stationnement interdits',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  782,
  236,
  'علامة A27',
  'Overstekend groot wild.',
  'Overstekend groot wild.',
  'Overstekend groot wild.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  237,
  'العلامة E3 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign E3 means: This sign is optional. True or False?',
  'Bord E3 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau E3 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E3' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  783,
  237,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  784,
  237,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  238,
  'إلى أي فئة تنتمي العلامة E3؟',
  'Which category does sign E3 belong to?',
  'Tot welke categorie behoort bord E3?',
  'À quelle catégorie appartient le panneau E3?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E3' LIMIT 1),
  'العلامة E3 تنتمي إلى فئة علامات الوقوف',
  'Sign E3 belongs to Parking Signs',
  'Bord E3 behoort tot Parkeerverbod',
  'Le panneau E3 appartient à Panneaux de stationnement',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  785,
  238,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  786,
  238,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  787,
  238,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  788,
  238,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  239,
  'العلامة E5 تعني: ممنوع الانتظار. صحيح أم خطأ؟',
  'Sign E5 means: Parking prohibited. True or False?',
  'Bord E5 betekent: Parkeerverbod van de 1e tot de 15e van de maand.. Waar of Onwaar?',
  'Le panneau E5 signifie: Interdiction de stationner. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E5' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  789,
  239,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  790,
  239,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  240,
  'إلى أي فئة تنتمي العلامة E5؟',
  'Which category does sign E5 belong to?',
  'Tot welke categorie behoort bord E5?',
  'À quelle catégorie appartient le panneau E5?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E5' LIMIT 1),
  'العلامة E5 تنتمي إلى فئة علامات الوقوف',
  'Sign E5 belongs to Parking Signs',
  'Bord E5 behoort tot Parkeerverbod',
  'Le panneau E5 appartient à Panneaux de stationnement',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  791,
  240,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  792,
  240,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  793,
  240,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  794,
  240,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  241,
  'العلامة E7 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign E7 means: This sign is optional. True or False?',
  'Bord E7 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau E7 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E7' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  795,
  241,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  796,
  241,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  242,
  'ما هي العلامة المرورية E7؟',
  'What does the traffic sign E7 mean?',
  'Wat betekent verkeersbord E7?',
  'Que signifie le panneau de signalisation E7?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E7' LIMIT 1),
  'العلامة E7 تعني: ممنوع الانتظار',
  'Sign E7 means: Parking prohibited',
  'Bord E7 betekent: Parkeerverbod van de 16e tot het einde van de maand.',
  'Le panneau E7 signifie: Interdiction de stationner',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  797,
  242,
  'علامة F53',
  'Verplegingsinrichting.',
  'Verplegingsinrichting.',
  'Verplegingsinrichting.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  798,
  242,
  'علامة F101a',
  'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  799,
  242,
  'علامة F23c',
  'Nummer van een internationale weg.',
  'Nummer van een internationale weg.',
  'Nummer van een internationale weg.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  800,
  242,
  'ممنوع الانتظار',
  'Parking prohibited',
  'Parkeerverbod van de 16e tot het einde van de maand.',
  'Interdiction de stationner',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  243,
  'إلى أي فئة تنتمي العلامة E7؟',
  'Which category does sign E7 belong to?',
  'Tot welke categorie behoort bord E7?',
  'À quelle catégorie appartient le panneau E7?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E7' LIMIT 1),
  'العلامة E7 تنتمي إلى فئة علامات الوقوف',
  'Sign E7 belongs to Parking Signs',
  'Bord E7 behoort tot Parkeerverbod',
  'Le panneau E7 appartient à Panneaux de stationnement',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  801,
  243,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  802,
  243,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  803,
  243,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  804,
  243,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  244,
  'ما هي العلامة المرورية E9a؟',
  'What does the traffic sign E9a mean?',
  'Wat betekent verkeersbord E9a?',
  'Que signifie le panneau de signalisation E9a?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9a' LIMIT 1),
  'العلامة E9a تعني: علامة E9a',
  'Sign E9a means: parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  'Bord E9a betekent: parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  'Le panneau E9a signifie: parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  805,
  244,
  'علامة F1a',
  'Begin van een bebouwde kom.',
  'Begin van een bebouwde kom.',
  'Begin van een bebouwde kom.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  806,
  244,
  'طريق يؤدي إلى رصيف أو شاطئ',
  'Road leads to quay or waterside',
  'Uitweg op kaai of oever.',
  'Route menant au quai ou à la rive',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  807,
  244,
  'علامة E9a',
  'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  808,
  244,
  'علامة F19',
  'Eenrichtingsverkeer.',
  'Eenrichtingsverkeer.',
  'Eenrichtingsverkeer.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  245,
  'العلامة E9a تعني: علامة E9a. صحيح أم خطأ؟',
  'Sign E9a means: parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.. True or False?',
  'Bord E9a betekent: parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.. Waar of Onwaar?',
  'Le panneau E9a signifie: parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9a' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  809,
  245,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  810,
  245,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  246,
  'إلى أي فئة تنتمي العلامة E9a؟',
  'Which category does sign E9a belong to?',
  'Tot welke categorie behoort bord E9a?',
  'À quelle catégorie appartient le panneau E9a?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9a' LIMIT 1),
  'العلامة E9a تنتمي إلى فئة علامات الوقوف',
  'Sign E9a belongs to Parking Signs',
  'Bord E9a behoort tot Parkeerverbod',
  'Le panneau E9a appartient à Panneaux de stationnement',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  811,
  246,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  812,
  246,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  813,
  246,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  814,
  246,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  247,
  'ما هي العلامة المرورية E9b؟',
  'What does the traffic sign E9b mean?',
  'Wat betekent verkeersbord E9b?',
  'Que signifie le panneau de signalisation E9b?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9b' LIMIT 1),
  'العلامة E9b تعني: علامة E9b',
  'Sign E9b means: Parkeren uitsluitend voor auto''s.',
  'Bord E9b betekent: Parkeren uitsluitend voor auto''s.',
  'Le panneau E9b signifie: Parkeren uitsluitend voor auto''s.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  815,
  247,
  'علامة E9b',
  'Parkeren uitsluitend voor auto''s.',
  'Parkeren uitsluitend voor auto''s.',
  'Parkeren uitsluitend voor auto''s.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  816,
  247,
  'منطقة 30',
  'Zone 30',
  'Zone 30 km/u.',
  'Zone 30',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  817,
  247,
  'علامة A27',
  'Overstekend groot wild.',
  'Overstekend groot wild.',
  'Overstekend groot wild.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  818,
  247,
  'علامة M15',
  'Verbod voor speed pedelecs.',
  'Verbod voor speed pedelecs.',
  'Verbod voor speed pedelecs.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  248,
  'إلى أي فئة تنتمي العلامة E9b؟',
  'Which category does sign E9b belong to?',
  'Tot welke categorie behoort bord E9b?',
  'À quelle catégorie appartient le panneau E9b?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9b' LIMIT 1),
  'العلامة E9b تنتمي إلى فئة علامات الوقوف',
  'Sign E9b belongs to Parking Signs',
  'Bord E9b behoort tot Parkeerverbod',
  'Le panneau E9b appartient à Panneaux de stationnement',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  819,
  248,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  820,
  248,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  821,
  248,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  822,
  248,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  249,
  'العلامة E9b تعني: علامة E9b. صحيح أم خطأ؟',
  'Sign E9b means: Parkeren uitsluitend voor auto''s.. True or False?',
  'Bord E9b betekent: Parkeren uitsluitend voor auto''s.. Waar of Onwaar?',
  'Le panneau E9b signifie: Parkeren uitsluitend voor auto''s.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9b' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  823,
  249,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  824,
  249,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  250,
  'ما هي العلامة المرورية E9c؟',
  'What does the traffic sign E9c mean?',
  'Wat betekent verkeersbord E9c?',
  'Que signifie le panneau de signalisation E9c?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9c' LIMIT 1),
  'العلامة E9c تعني: علامة E9c',
  'Sign E9c means: Parkeren uitsluitend voorvrachtwagens.',
  'Bord E9c betekent: Parkeren uitsluitend voorvrachtwagens.',
  'Le panneau E9c signifie: Parkeren uitsluitend voorvrachtwagens.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  825,
  250,
  'علامة E9c',
  'Parkeren uitsluitend voorvrachtwagens.',
  'Parkeren uitsluitend voorvrachtwagens.',
  'Parkeren uitsluitend voorvrachtwagens.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  826,
  250,
  'علامة A29',
  'Overstekend vee.',
  'Overstekend vee.',
  'Overstekend vee.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  827,
  250,
  'علامة C41',
  'Einde van het verbod opgelegd door het verkeersbord C39.',
  'Einde van het verbod opgelegd door het verkeersbord C39.',
  'Einde van het verbod opgelegd door het verkeersbord C39.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  828,
  250,
  'علامة B15b',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  251,
  'إلى أي فئة تنتمي العلامة E9c؟',
  'Which category does sign E9c belong to?',
  'Tot welke categorie behoort bord E9c?',
  'À quelle catégorie appartient le panneau E9c?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9c' LIMIT 1),
  'العلامة E9c تنتمي إلى فئة علامات الوقوف',
  'Sign E9c belongs to Parking Signs',
  'Bord E9c behoort tot Parkeerverbod',
  'Le panneau E9c appartient à Panneaux de stationnement',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  829,
  251,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  830,
  251,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  831,
  251,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  832,
  251,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  252,
  'إلى أي فئة تنتمي العلامة E9d؟',
  'Which category does sign E9d belong to?',
  'Tot welke categorie behoort bord E9d?',
  'À quelle catégorie appartient le panneau E9d?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9d' LIMIT 1),
  'العلامة E9d تنتمي إلى فئة علامات الوقوف',
  'Sign E9d belongs to Parking Signs',
  'Bord E9d behoort tot Parkeerverbod',
  'Le panneau E9d appartient à Panneaux de stationnement',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  833,
  252,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  834,
  252,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  835,
  252,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  836,
  252,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  253,
  'ما هي العلامة المرورية E9d؟',
  'What does the traffic sign E9d mean?',
  'Wat betekent verkeersbord E9d?',
  'Que signifie le panneau de signalisation E9d?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9d' LIMIT 1),
  'العلامة E9d تعني: علامة E9d',
  'Sign E9d means: Parkeren uitsluitend voor autocars.',
  'Bord E9d betekent: Parkeren uitsluitend voor autocars.',
  'Le panneau E9d signifie: Parkeren uitsluitend voor autocars.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  837,
  253,
  'علامة F21',
  'Rechts of links voorbijrijden toegelaten.',
  'Rechts of links voorbijrijden toegelaten.',
  'Rechts of links voorbijrijden toegelaten.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  838,
  253,
  'علامة M9',
  'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  839,
  253,
  'علامة B15e',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  840,
  253,
  'علامة E9d',
  'Parkeren uitsluitend voor autocars.',
  'Parkeren uitsluitend voor autocars.',
  'Parkeren uitsluitend voor autocars.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  254,
  'العلامة E9d تعني: علامة E9d. صحيح أم خطأ؟',
  'Sign E9d means: Parkeren uitsluitend voor autocars.. True or False?',
  'Bord E9d betekent: Parkeren uitsluitend voor autocars.. Waar of Onwaar?',
  'Le panneau E9d signifie: Parkeren uitsluitend voor autocars.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9d' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  841,
  254,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  842,
  254,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  255,
  'إلى أي فئة تنتمي العلامة E9e؟',
  'Which category does sign E9e belong to?',
  'Tot welke categorie behoort bord E9e?',
  'À quelle catégorie appartient le panneau E9e?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9e' LIMIT 1),
  'العلامة E9e تنتمي إلى فئة علامات الوقوف',
  'Sign E9e belongs to Parking Signs',
  'Bord E9e behoort tot Parkeerverbod',
  'Le panneau E9e appartient à Panneaux de stationnement',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  843,
  255,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  844,
  255,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  845,
  255,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  846,
  255,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  256,
  'ما هي العلامة المرورية E9e؟',
  'What does the traffic sign E9e mean?',
  'Wat betekent verkeersbord E9e?',
  'Que signifie le panneau de signalisation E9e?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9e' LIMIT 1),
  'العلامة E9e تعني: علامة E9e',
  'Sign E9e means: Verplicht parkeren op de berm of op het trottoir.',
  'Bord E9e betekent: Verplicht parkeren op de berm of op het trottoir.',
  'Le panneau E9e signifie: Verplicht parkeren op de berm of op het trottoir.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  847,
  256,
  'علامة A50',
  'Opgelet file',
  'Opgelet file',
  'Opgelet file',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  848,
  256,
  'علامة M17',
  'Fietsers en speed pedelecs mogen in 2 richtingen.',
  'Fietsers en speed pedelecs mogen in 2 richtingen.',
  'Fietsers en speed pedelecs mogen in 2 richtingen.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  849,
  256,
  'علامة F63',
  '- Specifieke brandstof Tankstation met een specifieke brandstof.',
  '- Specifieke brandstof Tankstation met een specifieke brandstof.',
  '- Specifieke brandstof Tankstation met een specifieke brandstof.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  850,
  256,
  'علامة E9e',
  'Verplicht parkeren op de berm of op het trottoir.',
  'Verplicht parkeren op de berm of op het trottoir.',
  'Verplicht parkeren op de berm of op het trottoir.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  257,
  'إلى أي فئة تنتمي العلامة E9f؟',
  'Which category does sign E9f belong to?',
  'Tot welke categorie behoort bord E9f?',
  'À quelle catégorie appartient le panneau E9f?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9f' LIMIT 1),
  'العلامة E9f تنتمي إلى فئة علامات الوقوف',
  'Sign E9f belongs to Parking Signs',
  'Bord E9f behoort tot Parkeerverbod',
  'Le panneau E9f appartient à Panneaux de stationnement',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  851,
  257,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  852,
  257,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  853,
  257,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  854,
  257,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  258,
  'العلامة E9f تعني: علامة E9f. صحيح أم خطأ؟',
  'Sign E9f means: Verplicht parkeren deels op de berm of op het trottoir.. True or False?',
  'Bord E9f betekent: Verplicht parkeren deels op de berm of op het trottoir.. Waar of Onwaar?',
  'Le panneau E9f signifie: Verplicht parkeren deels op de berm of op het trottoir.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9f' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  855,
  258,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  856,
  258,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  259,
  'إلى أي فئة تنتمي العلامة E9g؟',
  'Which category does sign E9g belong to?',
  'Tot welke categorie behoort bord E9g?',
  'À quelle catégorie appartient le panneau E9g?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9g' LIMIT 1),
  'العلامة E9g تنتمي إلى فئة علامات الوقوف',
  'Sign E9g belongs to Parking Signs',
  'Bord E9g behoort tot Parkeerverbod',
  'Le panneau E9g appartient à Panneaux de stationnement',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  857,
  259,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  858,
  259,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  859,
  259,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  860,
  259,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  260,
  'العلامة E9g تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign E9g means: This sign is optional. True or False?',
  'Bord E9g betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau E9g signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9g' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  861,
  260,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  862,
  260,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  261,
  'ما هي العلامة المرورية E9g؟',
  'What does the traffic sign E9g mean?',
  'Wat betekent verkeersbord E9g?',
  'Que signifie le panneau de signalisation E9g?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9g' LIMIT 1),
  'العلامة E9g تعني: علامة E9g',
  'Sign E9g means: Verplicht parkeren op de rijbaan.',
  'Bord E9g betekent: Verplicht parkeren op de rijbaan.',
  'Le panneau E9g signifie: Verplicht parkeren op de rijbaan.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  863,
  261,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van gespannen.',
  'Accès interdit',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  864,
  261,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van bromfietsen en fietsen.',
  'Accès interdit',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  865,
  261,
  'علامة E9g',
  'Verplicht parkeren op de rijbaan.',
  'Verplicht parkeren op de rijbaan.',
  'Verplicht parkeren op de rijbaan.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  866,
  261,
  'علامة M6',
  'Verplichting voor bromfietsen klasse B.',
  'Verplichting voor bromfietsen klasse B.',
  'Verplichting voor bromfietsen klasse B.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  262,
  'ما هي العلامة المرورية E9h؟',
  'What does the traffic sign E9h mean?',
  'Wat betekent verkeersbord E9h?',
  'Que signifie le panneau de signalisation E9h?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9h' LIMIT 1),
  'العلامة E9h تعني: علامة E9h',
  'Sign E9h means: Parkeren uitsluitend voor kampeerauto''s.',
  'Bord E9h betekent: Parkeren uitsluitend voor kampeerauto''s.',
  'Le panneau E9h signifie: Parkeren uitsluitend voor kampeerauto''s.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  867,
  262,
  'علامة A37',
  'Zijwind.',
  'Zijwind.',
  'Zijwind.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  868,
  262,
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Oversteekplaats voor fietsers en bromfietsers.',
  'Cyclistes et cyclomotoristes',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  869,
  262,
  'علامة E9h',
  'Parkeren uitsluitend voor kampeerauto''s.',
  'Parkeren uitsluitend voor kampeerauto''s.',
  'Parkeren uitsluitend voor kampeerauto''s.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  870,
  262,
  'علامة D9a',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  263,
  'إلى أي فئة تنتمي العلامة E9h؟',
  'Which category does sign E9h belong to?',
  'Tot welke categorie behoort bord E9h?',
  'À quelle catégorie appartient le panneau E9h?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9h' LIMIT 1),
  'العلامة E9h تنتمي إلى فئة علامات الوقوف',
  'Sign E9h belongs to Parking Signs',
  'Bord E9h behoort tot Parkeerverbod',
  'Le panneau E9h appartient à Panneaux de stationnement',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  871,
  263,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  872,
  263,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  873,
  263,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  874,
  263,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  264,
  'إلى أي فئة تنتمي العلامة E9i؟',
  'Which category does sign E9i belong to?',
  'Tot welke categorie behoort bord E9i?',
  'À quelle catégorie appartient le panneau E9i?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9i' LIMIT 1),
  'العلامة E9i تنتمي إلى فئة علامات الوقوف',
  'Sign E9i belongs to Parking Signs',
  'Bord E9i behoort tot Parkeerverbod',
  'Le panneau E9i appartient à Panneaux de stationnement',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  875,
  264,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  876,
  264,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  877,
  264,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  878,
  264,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  265,
  'العلامة E9i تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign E9i means: This sign is optional. True or False?',
  'Bord E9i betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau E9i signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9i' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  879,
  265,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  880,
  265,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  266,
  'ما هي العلامة المرورية E9i؟',
  'What does the traffic sign E9i mean?',
  'Wat betekent verkeersbord E9i?',
  'Que signifie le panneau de signalisation E9i?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9i' LIMIT 1),
  'العلامة E9i تعني: علامة E9i',
  'Sign E9i means: Parkeren uitsluitend voor motorfietsen.',
  'Bord E9i betekent: Parkeren uitsluitend voor motorfietsen.',
  'Le panneau E9i signifie: Parkeren uitsluitend voor motorfietsen.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  881,
  266,
  'علامة F21',
  'Rechts of links voorbijrijden toegelaten.',
  'Rechts of links voorbijrijden toegelaten.',
  'Rechts of links voorbijrijden toegelaten.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  882,
  266,
  'علامة A14',
  'Verhoogde inrichting.',
  'Verhoogde inrichting.',
  'Verhoogde inrichting.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  883,
  266,
  'علامة E9i',
  'Parkeren uitsluitend voor motorfietsen.',
  'Parkeren uitsluitend voor motorfietsen.',
  'Parkeren uitsluitend voor motorfietsen.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  884,
  266,
  'علامة F45b',
  'Doodlopende weg, uitgezonderd voetgangers en fietsers.',
  'Doodlopende weg, uitgezonderd voetgangers en fietsers.',
  'Doodlopende weg, uitgezonderd voetgangers en fietsers.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  267,
  'العلامة E9j تعني: علامة E9j. صحيح أم خطأ؟',
  'Sign E9j means: wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s. True or False?',
  'Bord E9j betekent: wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s. Waar of Onwaar?',
  'Le panneau E9j signifie: wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9j' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  885,
  267,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  886,
  267,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  268,
  'ما هي العلامة المرورية E9j؟',
  'What does the traffic sign E9j mean?',
  'Wat betekent verkeersbord E9j?',
  'Que signifie le panneau de signalisation E9j?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9j' LIMIT 1),
  'العلامة E9j تعني: علامة E9j',
  'Sign E9j means: wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  'Bord E9j betekent: wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  'Le panneau E9j signifie: wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  887,
  268,
  'علامة F50b',
  'Opgepast als je van richting veranderd, fietsers.',
  'Opgepast als je van richting veranderd, fietsers.',
  'Opgepast als je van richting veranderd, fietsers.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  888,
  268,
  'علامة C27',
  'Verboden voor voertuigen breder dan het aangeduide.',
  'Verboden voor voertuigen breder dan het aangeduide.',
  'Verboden voor voertuigen breder dan het aangeduide.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  889,
  268,
  'علامة F101c',
  'Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  890,
  268,
  'علامة E9j',
  'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  269,
  'إلى أي فئة تنتمي العلامة E9j؟',
  'Which category does sign E9j belong to?',
  'Tot welke categorie behoort bord E9j?',
  'À quelle catégorie appartient le panneau E9j?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E9j' LIMIT 1),
  'العلامة E9j تنتمي إلى فئة علامات الوقوف',
  'Sign E9j belongs to Parking Signs',
  'Bord E9j behoort tot Parkeerverbod',
  'Le panneau E9j appartient à Panneaux de stationnement',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  891,
  269,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  892,
  269,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  893,
  269,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  894,
  269,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  270,
  'إلى أي فئة تنتمي العلامة E11؟',
  'Which category does sign E11 belong to?',
  'Tot welke categorie behoort bord E11?',
  'À quelle catégorie appartient le panneau E11?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E11' LIMIT 1),
  'العلامة E11 تنتمي إلى فئة علامات الوقوف',
  'Sign E11 belongs to Parking Signs',
  'Bord E11 behoort tot Parkeerverbod',
  'Le panneau E11 appartient à Panneaux de stationnement',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  895,
  270,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  896,
  270,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  897,
  270,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  898,
  270,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  271,
  'ما هي العلامة المرورية E11؟',
  'What does the traffic sign E11 mean?',
  'Wat betekent verkeersbord E11?',
  'Que signifie le panneau de signalisation E11?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'E'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'E11' LIMIT 1),
  'العلامة E11 تعني: علامة E11',
  'Sign E11 means: Halfmaandelijks parkeren in gans de bebouwde kom.',
  'Bord E11 betekent: Halfmaandelijks parkeren in gans de bebouwde kom.',
  'Le panneau E11 signifie: Halfmaandelijks parkeren in gans de bebouwde kom.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  899,
  271,
  'علامة E11',
  'Halfmaandelijks parkeren in gans de bebouwde kom.',
  'Halfmaandelijks parkeren in gans de bebouwde kom.',
  'Halfmaandelijks parkeren in gans de bebouwde kom.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  900,
  271,
  'علامة A14',
  'Verhoogde inrichting.',
  'Verhoogde inrichting.',
  'Verhoogde inrichting.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  901,
  271,
  'اتجاه ممنوع',
  'Direction prohibited',
  'Verboden richting voor iedere bestuurder',
  'Direction interdite',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  902,
  271,
  'علامة F73',
  'Caravanterrein.',
  'Caravanterrein.',
  'Caravanterrein.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  272,
  'إلى أي فئة تنتمي العلامة F1a؟',
  'Which category does sign F1a belong to?',
  'Tot welke categorie behoort bord F1a?',
  'À quelle catégorie appartient le panneau F1a?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F1a' LIMIT 1),
  'العلامة F1a تنتمي إلى فئة علامات إرشادية',
  'Sign F1a belongs to Information Signs',
  'Bord F1a behoort tot Informatieborden',
  'Le panneau F1a appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  903,
  272,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  904,
  272,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  905,
  272,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  906,
  272,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  273,
  'ما هي العلامة المرورية F1a؟',
  'What does the traffic sign F1a mean?',
  'Wat betekent verkeersbord F1a?',
  'Que signifie le panneau de signalisation F1a?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F1a' LIMIT 1),
  'العلامة F1a تعني: علامة F1a',
  'Sign F1a means: Begin van een bebouwde kom.',
  'Bord F1a betekent: Begin van een bebouwde kom.',
  'Le panneau F1a signifie: Begin van een bebouwde kom.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  907,
  273,
  'علامة F1a',
  'Begin van een bebouwde kom.',
  'Begin van een bebouwde kom.',
  'Begin van een bebouwde kom.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  908,
  273,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor ruiters.',
  'Accès interdit',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  909,
  273,
  'علامة F71',
  'Kampeerterrein.',
  'Kampeerterrein.',
  'Kampeerterrein.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  910,
  273,
  'علامة F34a',
  'Nabijheid van inrichting die van openbaar of algemeen belang is.',
  'Nabijheid van inrichting die van openbaar of algemeen belang is.',
  'Nabijheid van inrichting die van openbaar of algemeen belang is.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  274,
  'العلامة F1a تعني: علامة F1a. صحيح أم خطأ؟',
  'Sign F1a means: Begin van een bebouwde kom.. True or False?',
  'Bord F1a betekent: Begin van een bebouwde kom.. Waar of Onwaar?',
  'Le panneau F1a signifie: Begin van een bebouwde kom.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F1a' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  911,
  274,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  912,
  274,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  275,
  'ما هي العلامة المرورية F1b؟',
  'What does the traffic sign F1b mean?',
  'Wat betekent verkeersbord F1b?',
  'Que signifie le panneau de signalisation F1b?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F1b' LIMIT 1),
  'العلامة F1b تعني: علامة F1b',
  'Sign F1b means: Begin van een bebouwde kom.',
  'Bord F1b betekent: Begin van een bebouwde kom.',
  'Le panneau F1b signifie: Begin van een bebouwde kom.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  913,
  275,
  'علامة E9j',
  'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  914,
  275,
  'علامة A51',
  'Gevaar dat niet door een speciaal symbool wordt bepaald.',
  'Gevaar dat niet door een speciaal symbool wordt bepaald.',
  'Gevaar dat niet door een speciaal symbool wordt bepaald.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  915,
  275,
  'علامة F75',
  'Jeugdherberg.',
  'Jeugdherberg.',
  'Jeugdherberg.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  916,
  275,
  'علامة F1b',
  'Begin van een bebouwde kom.',
  'Begin van een bebouwde kom.',
  'Begin van een bebouwde kom.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  276,
  'العلامة F1b تعني: علامة F1b. صحيح أم خطأ؟',
  'Sign F1b means: Begin van een bebouwde kom.. True or False?',
  'Bord F1b betekent: Begin van een bebouwde kom.. Waar of Onwaar?',
  'Le panneau F1b signifie: Begin van een bebouwde kom.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F1b' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  917,
  276,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  918,
  276,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  277,
  'إلى أي فئة تنتمي العلامة F1b؟',
  'Which category does sign F1b belong to?',
  'Tot welke categorie behoort bord F1b?',
  'À quelle catégorie appartient le panneau F1b?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F1b' LIMIT 1),
  'العلامة F1b تنتمي إلى فئة علامات إرشادية',
  'Sign F1b belongs to Information Signs',
  'Bord F1b behoort tot Informatieborden',
  'Le panneau F1b appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  919,
  277,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  920,
  277,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  921,
  277,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  922,
  277,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  278,
  'ما هي العلامة المرورية F3a؟',
  'What does the traffic sign F3a mean?',
  'Wat betekent verkeersbord F3a?',
  'Que signifie le panneau de signalisation F3a?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F3a' LIMIT 1),
  'العلامة F3a تعني: علامة F3a',
  'Sign F3a means: Einde van een bebouwde kom.',
  'Bord F3a betekent: Einde van een bebouwde kom.',
  'Le panneau F3a signifie: Einde van een bebouwde kom.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  923,
  278,
  'علامة M1',
  'Enkel voor fietsers.',
  'Enkel voor fietsers.',
  'Enkel voor fietsers.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  924,
  278,
  'قف',
  'Stop',
  'Stoppen en voorrang verlenen',
  'Stop',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  925,
  278,
  'أعط الأولوية',
  'Give way',
  'Smalle doorgang voorrang verlenen aan de bestuurders die uit de tegenovergestelde richting komen',
  'Cédez le passage',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  926,
  278,
  'علامة F3a',
  'Einde van een bebouwde kom.',
  'Einde van een bebouwde kom.',
  'Einde van een bebouwde kom.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  279,
  'العلامة F3a تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F3a means: This sign is optional. True or False?',
  'Bord F3a betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F3a signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F3a' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  927,
  279,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  928,
  279,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  280,
  'إلى أي فئة تنتمي العلامة F3b؟',
  'Which category does sign F3b belong to?',
  'Tot welke categorie behoort bord F3b?',
  'À quelle catégorie appartient le panneau F3b?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F3b' LIMIT 1),
  'العلامة F3b تنتمي إلى فئة علامات إرشادية',
  'Sign F3b belongs to Information Signs',
  'Bord F3b behoort tot Informatieborden',
  'Le panneau F3b appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  929,
  280,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  930,
  280,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  931,
  280,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  932,
  280,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  281,
  'ما هي العلامة المرورية F3b؟',
  'What does the traffic sign F3b mean?',
  'Wat betekent verkeersbord F3b?',
  'Que signifie le panneau de signalisation F3b?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F3b' LIMIT 1),
  'العلامة F3b تعني: علامة F3b',
  'Sign F3b means: Einde van een bebouwde kom.',
  'Bord F3b betekent: Einde van een bebouwde kom.',
  'Le panneau F3b signifie: Einde van een bebouwde kom.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  933,
  281,
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Oversteekplaats voor fietsers en bromfietsers.',
  'Cyclistes et cyclomotoristes',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  934,
  281,
  'علامة F3b',
  'Einde van een bebouwde kom.',
  'Einde van een bebouwde kom.',
  'Einde van een bebouwde kom.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  935,
  281,
  'منطقة سكنية',
  'Residential zone',
  'Einde van een woonerf of van een erf.',
  'Zone résidentielle',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  936,
  281,
  'علامة M19',
  'Enkel voor speed pedelecs.',
  'Enkel voor speed pedelecs.',
  'Enkel voor speed pedelecs.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  282,
  'العلامة F4a تعني: منطقة 30. صحيح أم خطأ؟',
  'Sign F4a means: Zone 30. True or False?',
  'Bord F4a betekent: Zone 30 km/u.. Waar of Onwaar?',
  'Le panneau F4a signifie: Zone 30. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F4a' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  937,
  282,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  938,
  282,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  283,
  'إلى أي فئة تنتمي العلامة F4a؟',
  'Which category does sign F4a belong to?',
  'Tot welke categorie behoort bord F4a?',
  'À quelle catégorie appartient le panneau F4a?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F4a' LIMIT 1),
  'العلامة F4a تنتمي إلى فئة علامات إرشادية',
  'Sign F4a belongs to Information Signs',
  'Bord F4a behoort tot Informatieborden',
  'Le panneau F4a appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  939,
  283,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  940,
  283,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  941,
  283,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  942,
  283,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  284,
  'ما هي العلامة المرورية F4a؟',
  'What does the traffic sign F4a mean?',
  'Wat betekent verkeersbord F4a?',
  'Que signifie le panneau de signalisation F4a?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F4a' LIMIT 1),
  'العلامة F4a تعني: منطقة 30',
  'Sign F4a means: Zone 30',
  'Bord F4a betekent: Zone 30 km/u.',
  'Le panneau F4a signifie: Zone 30',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  943,
  284,
  'علامة F118',
  'Einde van een lage emissiezone',
  'Einde van een lage emissiezone',
  'Einde van een lage emissiezone',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  944,
  284,
  'منطقة 30',
  'Zone 30',
  'Zone 30 km/u.',
  'Zone 30',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  945,
  284,
  'علامة F50b',
  'Opgepast als je van richting veranderd, fietsers.',
  'Opgepast als je van richting veranderd, fietsers.',
  'Opgepast als je van richting veranderd, fietsers.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  946,
  284,
  'علامة C25',
  'Verboden voor voertuigen langer dan het aangeduide',
  'Verboden voor voertuigen langer dan het aangeduide',
  'Verboden voor voertuigen langer dan het aangeduide',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  285,
  'إلى أي فئة تنتمي العلامة F4b؟',
  'Which category does sign F4b belong to?',
  'Tot welke categorie behoort bord F4b?',
  'À quelle catégorie appartient le panneau F4b?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F4b' LIMIT 1),
  'العلامة F4b تنتمي إلى فئة علامات إرشادية',
  'Sign F4b belongs to Information Signs',
  'Bord F4b behoort tot Informatieborden',
  'Le panneau F4b appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  947,
  285,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  948,
  285,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  949,
  285,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  950,
  285,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  286,
  'العلامة F4b تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F4b means: This sign is optional. True or False?',
  'Bord F4b betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F4b signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F4b' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  951,
  286,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  952,
  286,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  287,
  'ما هي العلامة المرورية F4b؟',
  'What does the traffic sign F4b mean?',
  'Wat betekent verkeersbord F4b?',
  'Que signifie le panneau de signalisation F4b?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F4b' LIMIT 1),
  'العلامة F4b تعني: منطقة 30',
  'Sign F4b means: Zone 30',
  'Bord F4b betekent: Einde zone 30 km/u.',
  'Le panneau F4b signifie: Zone 30',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  953,
  287,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen die gevaarlijke verontreinigende stoffen vervoeren.',
  'Accès interdit',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  954,
  287,
  'علامة C31a',
  'Verbod om links af te slaan.',
  'Verbod om links af te slaan.',
  'Verbod om links af te slaan.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  955,
  287,
  'علامة A17',
  'Kiezelprojectie',
  'Kiezelprojectie',
  'Kiezelprojectie',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  956,
  287,
  'منطقة 30',
  'Zone 30',
  'Einde zone 30 km/u.',
  'Zone 30',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  288,
  'ما هي العلامة المرورية F5؟',
  'What does the traffic sign F5 mean?',
  'Wat betekent verkeersbord F5?',
  'Que signifie le panneau de signalisation F5?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F5' LIMIT 1),
  'العلامة F5 تعني: طريق سريع',
  'Sign F5 means: Motorway',
  'Bord F5 betekent: Autosnelweg.',
  'Le panneau F5 signifie: Autoroute',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  957,
  288,
  'علامة F53',
  'Verplegingsinrichting.',
  'Verplegingsinrichting.',
  'Verplegingsinrichting.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  958,
  288,
  'منعطفات خطرة، الأول لليسار',
  'Dangerous double or multiple bends, first to the left',
  'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar links.',
  'Virages dangereux, le premier à gauche',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  959,
  288,
  'علامة F87',
  'Verhoogde inrichting (vluchtheuvel).',
  'Verhoogde inrichting (vluchtheuvel).',
  'Verhoogde inrichting (vluchtheuvel).',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  960,
  288,
  'طريق سريع',
  'Motorway',
  'Autosnelweg.',
  'Autoroute',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  289,
  'إلى أي فئة تنتمي العلامة F5؟',
  'Which category does sign F5 belong to?',
  'Tot welke categorie behoort bord F5?',
  'À quelle catégorie appartient le panneau F5?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F5' LIMIT 1),
  'العلامة F5 تنتمي إلى فئة علامات إرشادية',
  'Sign F5 belongs to Information Signs',
  'Bord F5 behoort tot Informatieborden',
  'Le panneau F5 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  961,
  289,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  962,
  289,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  963,
  289,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  964,
  289,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  290,
  'إلى أي فئة تنتمي العلامة F7؟',
  'Which category does sign F7 belong to?',
  'Tot welke categorie behoort bord F7?',
  'À quelle catégorie appartient le panneau F7?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F7' LIMIT 1),
  'العلامة F7 تنتمي إلى فئة علامات إرشادية',
  'Sign F7 belongs to Information Signs',
  'Bord F7 behoort tot Informatieborden',
  'Le panneau F7 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  965,
  290,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  966,
  290,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  967,
  290,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  968,
  290,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  291,
  'ما هي العلامة المرورية F7؟',
  'What does the traffic sign F7 mean?',
  'Wat betekent verkeersbord F7?',
  'Que signifie le panneau de signalisation F7?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F7' LIMIT 1),
  'العلامة F7 تعني: طريق سريع',
  'Sign F7 means: Motorway',
  'Bord F7 betekent: Einde autosnelweg.',
  'Le panneau F7 signifie: Autoroute',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  969,
  291,
  'علامة F99c',
  'Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  970,
  291,
  'طريق سريع',
  'Motorway',
  'Einde autosnelweg.',
  'Autoroute',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  971,
  291,
  'علامة C29',
  'Verboden voor voertuigen hoger dan het aangeduide.',
  'Verboden voor voertuigen hoger dan het aangeduide.',
  'Verboden voor voertuigen hoger dan het aangeduide.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  972,
  291,
  'علامة E9h',
  'Parkeren uitsluitend voor kampeerauto''s.',
  'Parkeren uitsluitend voor kampeerauto''s.',
  'Parkeren uitsluitend voor kampeerauto''s.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  292,
  'العلامة F8 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F8 means: This sign is optional. True or False?',
  'Bord F8 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F8 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F8' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  973,
  292,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  974,
  292,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  293,
  'ما هي العلامة المرورية F8؟',
  'What does the traffic sign F8 mean?',
  'Wat betekent verkeersbord F8?',
  'Que signifie le panneau de signalisation F8?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F8' LIMIT 1),
  'العلامة F8 تعني: نفق',
  'Sign F8 means: Tunnel',
  'Bord F8 betekent: Tunnel.',
  'Le panneau F8 signifie: Tunnel',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  975,
  293,
  'علامة A14',
  'Verhoogde inrichting.',
  'Verhoogde inrichting.',
  'Verhoogde inrichting.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  976,
  293,
  'علامة F103',
  'Begin van een voetgangerszone',
  'Begin van een voetgangerszone',
  'Begin van een voetgangerszone',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  977,
  293,
  'نفق',
  'Tunnel',
  'Tunnel.',
  'Tunnel',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  978,
  293,
  'علامة B17',
  'Kruispunt waar de voorrang van rechts geldt',
  'Kruispunt waar de voorrang van rechts geldt',
  'Kruispunt waar de voorrang van rechts geldt',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  294,
  'إلى أي فئة تنتمي العلامة F8؟',
  'Which category does sign F8 belong to?',
  'Tot welke categorie behoort bord F8?',
  'À quelle catégorie appartient le panneau F8?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F8' LIMIT 1),
  'العلامة F8 تنتمي إلى فئة علامات إرشادية',
  'Sign F8 belongs to Information Signs',
  'Bord F8 behoort tot Informatieborden',
  'Le panneau F8 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  979,
  294,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  980,
  294,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  981,
  294,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  982,
  294,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  295,
  'العلامة F9 تعني: طريق سيارات. صحيح أم خطأ؟',
  'Sign F9 means: Expressway. True or False?',
  'Bord F9 betekent: Autoweg.. Waar of Onwaar?',
  'Le panneau F9 signifie: Route pour automobiles. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F9' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  983,
  295,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  984,
  295,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  296,
  'إلى أي فئة تنتمي العلامة F9؟',
  'Which category does sign F9 belong to?',
  'Tot welke categorie behoort bord F9?',
  'À quelle catégorie appartient le panneau F9?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F9' LIMIT 1),
  'العلامة F9 تنتمي إلى فئة علامات إرشادية',
  'Sign F9 belongs to Information Signs',
  'Bord F9 behoort tot Informatieborden',
  'Le panneau F9 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  985,
  296,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  986,
  296,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  987,
  296,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  988,
  296,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  297,
  'ما هي العلامة المرورية F9؟',
  'What does the traffic sign F9 mean?',
  'Wat betekent verkeersbord F9?',
  'Que signifie le panneau de signalisation F9?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F9' LIMIT 1),
  'العلامة F9 تعني: طريق سيارات',
  'Sign F9 means: Expressway',
  'Bord F9 betekent: Autoweg.',
  'Le panneau F9 signifie: Route pour automobiles',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  989,
  297,
  'علامة F99c',
  'Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  990,
  297,
  'منطقة 30',
  'Zone 30',
  'Einde zone 30 km/u.',
  'Zone 30',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  991,
  297,
  'صعود خطر',
  'Dangerous ascent',
  'Gevaarlijke helling.',
  'Montée dangereuse',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  992,
  297,
  'طريق سيارات',
  'Expressway',
  'Autoweg.',
  'Route pour automobiles',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  298,
  'ما هي العلامة المرورية F11؟',
  'What does the traffic sign F11 mean?',
  'Wat betekent verkeersbord F11?',
  'Que signifie le panneau de signalisation F11?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F11' LIMIT 1),
  'العلامة F11 تعني: طريق سيارات',
  'Sign F11 means: Expressway',
  'Bord F11 betekent: Einde van de autoweg.',
  'Le panneau F11 signifie: Route pour automobiles',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  993,
  298,
  'علامة A27',
  'Overstekend groot wild.',
  'Overstekend groot wild.',
  'Overstekend groot wild.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  994,
  298,
  'طريق سيارات',
  'Expressway',
  'Einde van de autoweg.',
  'Route pour automobiles',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  995,
  298,
  'علامة C31a',
  'Verbod om links af te slaan.',
  'Verbod om links af te slaan.',
  'Verbod om links af te slaan.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  996,
  298,
  'علامة F23a',
  'Nummer van een gewone weg.',
  'Nummer van een gewone weg.',
  'Nummer van een gewone weg.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  299,
  'إلى أي فئة تنتمي العلامة F11؟',
  'Which category does sign F11 belong to?',
  'Tot welke categorie behoort bord F11?',
  'À quelle catégorie appartient le panneau F11?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F11' LIMIT 1),
  'العلامة F11 تنتمي إلى فئة علامات إرشادية',
  'Sign F11 belongs to Information Signs',
  'Bord F11 behoort tot Informatieborden',
  'Le panneau F11 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  997,
  299,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  998,
  299,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  999,
  299,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1000,
  299,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  300,
  'ما هي العلامة المرورية F12a؟',
  'What does the traffic sign F12a mean?',
  'Wat betekent verkeersbord F12a?',
  'Que signifie le panneau de signalisation F12a?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F12a' LIMIT 1),
  'العلامة F12a تعني: منطقة سكنية',
  'Sign F12a means: Residential zone',
  'Bord F12a betekent: Begin van een woonerf of van een erf.',
  'Le panneau F12a signifie: Zone résidentielle',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1001,
  300,
  'منطقة سكنية',
  'Residential zone',
  'Begin van een woonerf of van een erf.',
  'Zone résidentielle',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1002,
  300,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van autocars.',
  'Accès interdit',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1003,
  300,
  'علامة A17',
  'Kiezelprojectie',
  'Kiezelprojectie',
  'Kiezelprojectie',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1004,
  300,
  'علامة F14',
  'Opstelvak voor fietsers en bromfietsen.',
  'Opstelvak voor fietsers en bromfietsen.',
  'Opstelvak voor fietsers en bromfietsen.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  301,
  'العلامة F12a تعني: منطقة سكنية. صحيح أم خطأ؟',
  'Sign F12a means: Residential zone. True or False?',
  'Bord F12a betekent: Begin van een woonerf of van een erf.. Waar of Onwaar?',
  'Le panneau F12a signifie: Zone résidentielle. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F12a' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1005,
  301,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1006,
  301,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  302,
  'إلى أي فئة تنتمي العلامة F12b؟',
  'Which category does sign F12b belong to?',
  'Tot welke categorie behoort bord F12b?',
  'À quelle catégorie appartient le panneau F12b?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F12b' LIMIT 1),
  'العلامة F12b تنتمي إلى فئة علامات إرشادية',
  'Sign F12b belongs to Information Signs',
  'Bord F12b behoort tot Informatieborden',
  'Le panneau F12b appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1007,
  302,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1008,
  302,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1009,
  302,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1010,
  302,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  303,
  'العلامة F12b تعني: منطقة سكنية. صحيح أم خطأ؟',
  'Sign F12b means: Residential zone. True or False?',
  'Bord F12b betekent: Einde van een woonerf of van een erf.. Waar of Onwaar?',
  'Le panneau F12b signifie: Zone résidentielle. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F12b' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1011,
  303,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1012,
  303,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  304,
  'العلامة F13 تعني: علامة F13. صحيح أم خطأ؟',
  'Sign F13 means: Rijstrook keuze.. True or False?',
  'Bord F13 betekent: Rijstrook keuze.. Waar of Onwaar?',
  'Le panneau F13 signifie: Rijstrook keuze.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F13' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1013,
  304,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1014,
  304,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  305,
  'ما هي العلامة المرورية F13؟',
  'What does the traffic sign F13 mean?',
  'Wat betekent verkeersbord F13?',
  'Que signifie le panneau de signalisation F13?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F13' LIMIT 1),
  'العلامة F13 تعني: علامة F13',
  'Sign F13 means: Rijstrook keuze.',
  'Bord F13 betekent: Rijstrook keuze.',
  'Le panneau F13 signifie: Rijstrook keuze.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1015,
  305,
  'علامة F13',
  'Rijstrook keuze.',
  'Rijstrook keuze.',
  'Rijstrook keuze.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1016,
  305,
  'علامة M17',
  'Fietsers en speed pedelecs mogen in 2 richtingen.',
  'Fietsers en speed pedelecs mogen in 2 richtingen.',
  'Fietsers en speed pedelecs mogen in 2 richtingen.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1017,
  305,
  'علامة E9a',
  'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  'parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1018,
  305,
  'علامة D13',
  'Verplichte weg voor ruiters.',
  'Verplichte weg voor ruiters.',
  'Verplichte weg voor ruiters.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  306,
  'العلامة F14 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F14 means: This sign is optional. True or False?',
  'Bord F14 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F14 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F14' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1019,
  306,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1020,
  306,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  307,
  'إلى أي فئة تنتمي العلامة F14؟',
  'Which category does sign F14 belong to?',
  'Tot welke categorie behoort bord F14?',
  'À quelle catégorie appartient le panneau F14?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F14' LIMIT 1),
  'العلامة F14 تنتمي إلى فئة علامات إرشادية',
  'Sign F14 belongs to Information Signs',
  'Bord F14 behoort tot Informatieborden',
  'Le panneau F14 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1021,
  307,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1022,
  307,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1023,
  307,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1024,
  307,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  308,
  'ما هي العلامة المرورية F14؟',
  'What does the traffic sign F14 mean?',
  'Wat betekent verkeersbord F14?',
  'Que signifie le panneau de signalisation F14?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F14' LIMIT 1),
  'العلامة F14 تعني: علامة F14',
  'Sign F14 means: Opstelvak voor fietsers en bromfietsen.',
  'Bord F14 betekent: Opstelvak voor fietsers en bromfietsen.',
  'Le panneau F14 signifie: Opstelvak voor fietsers en bromfietsen.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1025,
  308,
  'علامة E9e',
  'Verplicht parkeren op de berm of op het trottoir.',
  'Verplicht parkeren op de berm of op het trottoir.',
  'Verplicht parkeren op de berm of op het trottoir.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1026,
  308,
  'علامة C31b',
  'Verbod rechts af te slaan.',
  'Verbod rechts af te slaan.',
  'Verbod rechts af te slaan.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1027,
  308,
  'علامة F14',
  'Opstelvak voor fietsers en bromfietsen.',
  'Opstelvak voor fietsers en bromfietsen.',
  'Opstelvak voor fietsers en bromfietsen.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1028,
  308,
  'علامة M1',
  'Enkel voor fietsers.',
  'Enkel voor fietsers.',
  'Enkel voor fietsers.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  309,
  'إلى أي فئة تنتمي العلامة F17؟',
  'Which category does sign F17 belong to?',
  'Tot welke categorie behoort bord F17?',
  'À quelle catégorie appartient le panneau F17?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F17' LIMIT 1),
  'العلامة F17 تنتمي إلى فئة علامات إرشادية',
  'Sign F17 belongs to Information Signs',
  'Bord F17 behoort tot Informatieborden',
  'Le panneau F17 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1029,
  309,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1030,
  309,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1031,
  309,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1032,
  309,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  310,
  'ما هي العلامة المرورية F17؟',
  'What does the traffic sign F17 mean?',
  'Wat betekent verkeersbord F17?',
  'Que signifie le panneau de signalisation F17?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F17' LIMIT 1),
  'العلامة F17 تعني: علامة F17',
  'Sign F17 means: Rijstrook aanduiding voorbehouden voor autobussen.',
  'Bord F17 betekent: Rijstrook aanduiding voorbehouden voor autobussen.',
  'Le panneau F17 signifie: Rijstrook aanduiding voorbehouden voor autobussen.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1033,
  310,
  'منعطف خطر لليمين',
  'Dangerous bend to the right',
  'Gevaarlijke bocht naar rechts.',
  'Virage dangereux à droite',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1034,
  310,
  'علامة F34b',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1035,
  310,
  'معبر للمشاة',
  'Pedestrian crossing',
  'Oversteekplaats voor voetgangers.',
  'Passage pour piétons',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1036,
  310,
  'علامة F17',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  311,
  'إلى أي فئة تنتمي العلامة F18؟',
  'Which category does sign F18 belong to?',
  'Tot welke categorie behoort bord F18?',
  'À quelle catégorie appartient le panneau F18?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F18' LIMIT 1),
  'العلامة F18 تنتمي إلى فئة علامات إرشادية',
  'Sign F18 belongs to Information Signs',
  'Bord F18 behoort tot Informatieborden',
  'Le panneau F18 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1037,
  311,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1038,
  311,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1039,
  311,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1040,
  311,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  312,
  'العلامة F18 تعني: علامة F18. صحيح أم خطأ؟',
  'Sign F18 means: Bijzondere overrijdbare bedding.. True or False?',
  'Bord F18 betekent: Bijzondere overrijdbare bedding.. Waar of Onwaar?',
  'Le panneau F18 signifie: Bijzondere overrijdbare bedding.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F18' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1041,
  312,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1042,
  312,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  313,
  'العلامة F19 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F19 means: This sign is optional. True or False?',
  'Bord F19 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F19 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F19' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1043,
  313,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1044,
  313,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  314,
  'إلى أي فئة تنتمي العلامة F19؟',
  'Which category does sign F19 belong to?',
  'Tot welke categorie behoort bord F19?',
  'À quelle catégorie appartient le panneau F19?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F19' LIMIT 1),
  'العلامة F19 تنتمي إلى فئة علامات إرشادية',
  'Sign F19 belongs to Information Signs',
  'Bord F19 behoort tot Informatieborden',
  'Le panneau F19 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1045,
  314,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1046,
  314,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1047,
  314,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1048,
  314,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  315,
  'ما هي العلامة المرورية F21؟',
  'What does the traffic sign F21 mean?',
  'Wat betekent verkeersbord F21?',
  'Que signifie le panneau de signalisation F21?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F21' LIMIT 1),
  'العلامة F21 تعني: علامة F21',
  'Sign F21 means: Rechts of links voorbijrijden toegelaten.',
  'Bord F21 betekent: Rechts of links voorbijrijden toegelaten.',
  'Le panneau F21 signifie: Rechts of links voorbijrijden toegelaten.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1049,
  315,
  'علامة F17',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1050,
  315,
  'علامة F21',
  'Rechts of links voorbijrijden toegelaten.',
  'Rechts of links voorbijrijden toegelaten.',
  'Rechts of links voorbijrijden toegelaten.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1051,
  315,
  'علامة M13',
  'Verplichting voor speed pedelecs.',
  'Verplichting voor speed pedelecs.',
  'Verplichting voor speed pedelecs.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1052,
  315,
  'علامة F14',
  'Opstelvak voor fietsers en bromfietsen.',
  'Opstelvak voor fietsers en bromfietsen.',
  'Opstelvak voor fietsers en bromfietsen.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  316,
  'إلى أي فئة تنتمي العلامة F21؟',
  'Which category does sign F21 belong to?',
  'Tot welke categorie behoort bord F21?',
  'À quelle catégorie appartient le panneau F21?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F21' LIMIT 1),
  'العلامة F21 تنتمي إلى فئة علامات إرشادية',
  'Sign F21 belongs to Information Signs',
  'Bord F21 behoort tot Informatieborden',
  'Le panneau F21 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1053,
  316,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1054,
  316,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1055,
  316,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1056,
  316,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  317,
  'العلامة F21 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F21 means: This sign is optional. True or False?',
  'Bord F21 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F21 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F21' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1057,
  317,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1058,
  317,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  318,
  'إلى أي فئة تنتمي العلامة F23a؟',
  'Which category does sign F23a belong to?',
  'Tot welke categorie behoort bord F23a?',
  'À quelle catégorie appartient le panneau F23a?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F23a' LIMIT 1),
  'العلامة F23a تنتمي إلى فئة علامات إرشادية',
  'Sign F23a belongs to Information Signs',
  'Bord F23a behoort tot Informatieborden',
  'Le panneau F23a appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1059,
  318,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1060,
  318,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1061,
  318,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1062,
  318,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  319,
  'العلامة F23a تعني: علامة F23a. صحيح أم خطأ؟',
  'Sign F23a means: Nummer van een gewone weg.. True or False?',
  'Bord F23a betekent: Nummer van een gewone weg.. Waar of Onwaar?',
  'Le panneau F23a signifie: Nummer van een gewone weg.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F23a' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1063,
  319,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1064,
  319,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  320,
  'إلى أي فئة تنتمي العلامة F23b؟',
  'Which category does sign F23b belong to?',
  'Tot welke categorie behoort bord F23b?',
  'À quelle catégorie appartient le panneau F23b?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F23b' LIMIT 1),
  'العلامة F23b تنتمي إلى فئة علامات إرشادية',
  'Sign F23b belongs to Information Signs',
  'Bord F23b behoort tot Informatieborden',
  'Le panneau F23b appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1065,
  320,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1066,
  320,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1067,
  320,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1068,
  320,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  321,
  'ما هي العلامة المرورية F23b؟',
  'What does the traffic sign F23b mean?',
  'Wat betekent verkeersbord F23b?',
  'Que signifie le panneau de signalisation F23b?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F23b' LIMIT 1),
  'العلامة F23b تعني: طريق سريع',
  'Sign F23b means: Motorway',
  'Bord F23b betekent: Nummer van een autosnelweg.',
  'Le panneau F23b signifie: Autoroute',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1069,
  321,
  'علامة A31',
  'Werken.',
  'Werken.',
  'Werken.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1070,
  321,
  'طريق سريع',
  'Motorway',
  'Nummer van een autosnelweg.',
  'Autoroute',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1071,
  321,
  'معبر للمشاة',
  'Pedestrian crossing',
  'Oversteekplaats voor voetgangers.',
  'Passage pour piétons',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1072,
  321,
  'علامة A49',
  'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  322,
  'العلامة F23c تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F23c means: This sign is optional. True or False?',
  'Bord F23c betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F23c signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F23c' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1073,
  322,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1074,
  322,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  323,
  'إلى أي فئة تنتمي العلامة F23c؟',
  'Which category does sign F23c belong to?',
  'Tot welke categorie behoort bord F23c?',
  'À quelle catégorie appartient le panneau F23c?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F23c' LIMIT 1),
  'العلامة F23c تنتمي إلى فئة علامات إرشادية',
  'Sign F23c belongs to Information Signs',
  'Bord F23c behoort tot Informatieborden',
  'Le panneau F23c appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1075,
  323,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1076,
  323,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1077,
  323,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1078,
  323,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  324,
  'ما هي العلامة المرورية F23d؟',
  'What does the traffic sign F23d mean?',
  'Wat betekent verkeersbord F23d?',
  'Que signifie le panneau de signalisation F23d?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F23d' LIMIT 1),
  'العلامة F23d تعني: علامة F23d',
  'Sign F23d means: Nummer van een ringweg.',
  'Bord F23d betekent: Nummer van een ringweg.',
  'Le panneau F23d signifie: Nummer van een ringweg.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1079,
  324,
  'علامة F23d',
  'Nummer van een ringweg.',
  'Nummer van een ringweg.',
  'Nummer van een ringweg.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1080,
  324,
  'علامة F101a',
  'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1081,
  324,
  'علامة D4',
  'Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.',
  'Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.',
  'Verplicht rechts voor voertuigen die gevaarlijke goederen vervoeren.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1082,
  324,
  'علامة C35',
  'Verbod een voertuig links in te halen.',
  'Verbod een voertuig links in te halen.',
  'Verbod een voertuig links in te halen.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  325,
  'إلى أي فئة تنتمي العلامة F23d؟',
  'Which category does sign F23d belong to?',
  'Tot welke categorie behoort bord F23d?',
  'À quelle catégorie appartient le panneau F23d?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F23d' LIMIT 1),
  'العلامة F23d تنتمي إلى فئة علامات إرشادية',
  'Sign F23d belongs to Information Signs',
  'Bord F23d behoort tot Informatieborden',
  'Le panneau F23d appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1083,
  325,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1084,
  325,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1085,
  325,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1086,
  325,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  326,
  'إلى أي فئة تنتمي العلامة F29؟',
  'Which category does sign F29 belong to?',
  'Tot welke categorie behoort bord F29?',
  'À quelle catégorie appartient le panneau F29?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F29' LIMIT 1),
  'العلامة F29 تنتمي إلى فئة علامات إرشادية',
  'Sign F29 belongs to Information Signs',
  'Bord F29 behoort tot Informatieborden',
  'Le panneau F29 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1087,
  326,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1088,
  326,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1089,
  326,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1090,
  326,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  327,
  'ما هي العلامة المرورية F29؟',
  'What does the traffic sign F29 mean?',
  'Wat betekent verkeersbord F29?',
  'Que signifie le panneau de signalisation F29?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F29' LIMIT 1),
  'العلامة F29 تعني: علامة F29',
  'Sign F29 means: Wegwijzer',
  'Bord F29 betekent: Wegwijzer',
  'Le panneau F29 signifie: Wegwijzer',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1091,
  327,
  'علامة F75',
  'Jeugdherberg.',
  'Jeugdherberg.',
  'Jeugdherberg.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1092,
  327,
  'علامة F29',
  'Wegwijzer',
  'Wegwijzer',
  'Wegwijzer',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1093,
  327,
  'علامة F101c',
  'Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1094,
  327,
  'علامة E9e',
  'Verplicht parkeren op de berm of op het trottoir.',
  'Verplicht parkeren op de berm of op het trottoir.',
  'Verplicht parkeren op de berm of op het trottoir.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  328,
  'العلامة F29 تعني: علامة F29. صحيح أم خطأ؟',
  'Sign F29 means: Wegwijzer. True or False?',
  'Bord F29 betekent: Wegwijzer. Waar of Onwaar?',
  'Le panneau F29 signifie: Wegwijzer. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F29' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1095,
  328,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1096,
  328,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  329,
  'ما هي العلامة المرورية F31؟',
  'What does the traffic sign F31 mean?',
  'Wat betekent verkeersbord F31?',
  'Que signifie le panneau de signalisation F31?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F31' LIMIT 1),
  'العلامة F31 تعني: علامة F31',
  'Sign F31 means: Wegwijzer autostrade',
  'Bord F31 betekent: Wegwijzer autostrade',
  'Le panneau F31 signifie: Wegwijzer autostrade',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1097,
  329,
  'علامة F31',
  'Wegwijzer autostrade',
  'Wegwijzer autostrade',
  'Wegwijzer autostrade',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1098,
  329,
  'علامة F45b',
  'Doodlopende weg, uitgezonderd voetgangers en fietsers.',
  'Doodlopende weg, uitgezonderd voetgangers en fietsers.',
  'Doodlopende weg, uitgezonderd voetgangers en fietsers.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1099,
  329,
  'علامة B22',
  'Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1100,
  329,
  'علامة A49',
  'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  330,
  'إلى أي فئة تنتمي العلامة F31؟',
  'Which category does sign F31 belong to?',
  'Tot welke categorie behoort bord F31?',
  'À quelle catégorie appartient le panneau F31?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F31' LIMIT 1),
  'العلامة F31 تنتمي إلى فئة علامات إرشادية',
  'Sign F31 belongs to Information Signs',
  'Bord F31 behoort tot Informatieborden',
  'Le panneau F31 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1101,
  330,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1102,
  330,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1103,
  330,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1104,
  330,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  331,
  'العلامة F31 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F31 means: This sign is optional. True or False?',
  'Bord F31 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F31 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F31' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1105,
  331,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1106,
  331,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  332,
  'العلامة F33a تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F33a means: This sign is optional. True or False?',
  'Bord F33a betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F33a signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F33a' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1107,
  332,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1108,
  332,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  333,
  'ما هي العلامة المرورية F33a؟',
  'What does the traffic sign F33a mean?',
  'Wat betekent verkeersbord F33a?',
  'Que signifie le panneau de signalisation F33a?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F33a' LIMIT 1),
  'العلامة F33a تعني: علامة F33a',
  'Sign F33a means: Bewegwijzeringsbord op afstand',
  'Bord F33a betekent: Bewegwijzeringsbord op afstand',
  'Le panneau F33a signifie: Bewegwijzeringsbord op afstand',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1109,
  333,
  'شارع الدراجات',
  'Cycle street',
  'Fietsstraat.',
  'Rue cyclable',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1110,
  333,
  'منعطفات خطرة، الأول لليسار',
  'Dangerous double or multiple bends, first to the left',
  'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar links.',
  'Virages dangereux, le premier à gauche',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1111,
  333,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van motorvoertuigen en motorfietsen.',
  'Accès interdit',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1112,
  333,
  'علامة F33a',
  'Bewegwijzeringsbord op afstand',
  'Bewegwijzeringsbord op afstand',
  'Bewegwijzeringsbord op afstand',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  334,
  'إلى أي فئة تنتمي العلامة F33a؟',
  'Which category does sign F33a belong to?',
  'Tot welke categorie behoort bord F33a?',
  'À quelle catégorie appartient le panneau F33a?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F33a' LIMIT 1),
  'العلامة F33a تنتمي إلى فئة علامات إرشادية',
  'Sign F33a belongs to Information Signs',
  'Bord F33a behoort tot Informatieborden',
  'Le panneau F33a appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1113,
  334,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1114,
  334,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1115,
  334,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1116,
  334,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  335,
  'ما هي العلامة المرورية F33c؟',
  'What does the traffic sign F33c mean?',
  'Wat betekent verkeersbord F33c?',
  'Que signifie le panneau de signalisation F33c?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F33c' LIMIT 1),
  'العلامة F33c تعني: علامة F33c',
  'Sign F33c means: Bewegwijzeringsbord op afstand',
  'Bord F33c betekent: Bewegwijzeringsbord op afstand',
  'Le panneau F33c signifie: Bewegwijzeringsbord op afstand',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1117,
  335,
  'علامة F99b',
  'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1118,
  335,
  'علامة E11',
  'Halfmaandelijks parkeren in gans de bebouwde kom.',
  'Halfmaandelijks parkeren in gans de bebouwde kom.',
  'Halfmaandelijks parkeren in gans de bebouwde kom.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1119,
  335,
  'علامة F33c',
  'Bewegwijzeringsbord op afstand',
  'Bewegwijzeringsbord op afstand',
  'Bewegwijzeringsbord op afstand',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1120,
  335,
  'علامة C27',
  'Verboden voor voertuigen breder dan het aangeduide.',
  'Verboden voor voertuigen breder dan het aangeduide.',
  'Verboden voor voertuigen breder dan het aangeduide.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  336,
  'العلامة F33c تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F33c means: This sign is optional. True or False?',
  'Bord F33c betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F33c signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F33c' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1121,
  336,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1122,
  336,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  337,
  'إلى أي فئة تنتمي العلامة F34a؟',
  'Which category does sign F34a belong to?',
  'Tot welke categorie behoort bord F34a?',
  'À quelle catégorie appartient le panneau F34a?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F34a' LIMIT 1),
  'العلامة F34a تنتمي إلى فئة علامات إرشادية',
  'Sign F34a belongs to Information Signs',
  'Bord F34a behoort tot Informatieborden',
  'Le panneau F34a appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1123,
  337,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1124,
  337,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1125,
  337,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1126,
  337,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  338,
  'ما هي العلامة المرورية F34a؟',
  'What does the traffic sign F34a mean?',
  'Wat betekent verkeersbord F34a?',
  'Que signifie le panneau de signalisation F34a?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F34a' LIMIT 1),
  'العلامة F34a تعني: علامة F34a',
  'Sign F34a means: Nabijheid van inrichting die van openbaar of algemeen belang is.',
  'Bord F34a betekent: Nabijheid van inrichting die van openbaar of algemeen belang is.',
  'Le panneau F34a signifie: Nabijheid van inrichting die van openbaar of algemeen belang is.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1127,
  338,
  'تضييق الطريق',
  'Road narrowing',
  'Rijbaanversmalling rechts',
  'Rétrécissement de chaussée',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1128,
  338,
  'علامة F34a',
  'Nabijheid van inrichting die van openbaar of algemeen belang is.',
  'Nabijheid van inrichting die van openbaar of algemeen belang is.',
  'Nabijheid van inrichting die van openbaar of algemeen belang is.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1129,
  338,
  'علامة B17',
  'Kruispunt waar de voorrang van rechts geldt',
  'Kruispunt waar de voorrang van rechts geldt',
  'Kruispunt waar de voorrang van rechts geldt',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1130,
  338,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van bromfietsen en fietsen.',
  'Accès interdit',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  339,
  'إلى أي فئة تنتمي العلامة F34b؟',
  'Which category does sign F34b belong to?',
  'Tot welke categorie behoort bord F34b?',
  'À quelle catégorie appartient le panneau F34b?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F34b' LIMIT 1),
  'العلامة F34b تنتمي إلى فئة علامات إرشادية',
  'Sign F34b belongs to Information Signs',
  'Bord F34b behoort tot Informatieborden',
  'Le panneau F34b appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1131,
  339,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1132,
  339,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1133,
  339,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1134,
  339,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  340,
  'ما هي العلامة المرورية F34b؟',
  'What does the traffic sign F34b mean?',
  'Wat betekent verkeersbord F34b?',
  'Que signifie le panneau de signalisation F34b?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F34b' LIMIT 1),
  'العلامة F34b تعني: علامة F34b',
  'Sign F34b means: Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Bord F34b betekent: Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Le panneau F34b signifie: Aanbevolen reisweg voor bepaalde weggebruikers.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1135,
  340,
  'علامة F23a',
  'Nummer van een gewone weg.',
  'Nummer van een gewone weg.',
  'Nummer van een gewone weg.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1136,
  340,
  'ممنوع التوقف والانتظار',
  'No stopping or parking',
  'Stilstaan en parkeren verboden.',
  'Arrêt et stationnement interdits',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1137,
  340,
  'علامة F34b',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1138,
  340,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van autocars.',
  'Accès interdit',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  341,
  'إلى أي فئة تنتمي العلامة F34c؟',
  'Which category does sign F34c belong to?',
  'Tot welke categorie behoort bord F34c?',
  'À quelle catégorie appartient le panneau F34c?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F34c' LIMIT 1),
  'العلامة F34c تنتمي إلى فئة علامات إرشادية',
  'Sign F34c belongs to Information Signs',
  'Bord F34c behoort tot Informatieborden',
  'Le panneau F34c appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1139,
  341,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1140,
  341,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1141,
  341,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1142,
  341,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  342,
  'ما هي العلامة المرورية F34c؟',
  'What does the traffic sign F34c mean?',
  'Wat betekent verkeersbord F34c?',
  'Que signifie le panneau de signalisation F34c?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F34c' LIMIT 1),
  'العلامة F34c تعني: علامة F34c',
  'Sign F34c means: Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Bord F34c betekent: Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Le panneau F34c signifie: Aanbevolen reisweg voor bepaalde weggebruikers.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1143,
  342,
  'علامة F34c',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1144,
  342,
  'علامة F19',
  'Eenrichtingsverkeer.',
  'Eenrichtingsverkeer.',
  'Eenrichtingsverkeer.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1145,
  342,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen die gevaarlijke ontvlambare of ontplofbare stoffen vervoeren.',
  'Accès interdit',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1146,
  342,
  'علامة E9j',
  'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  'wisselend parkeren Parkeerplaats voorzien voor wisselend parkeren fietsers en auto’s',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  343,
  'العلامة F34c تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F34c means: This sign is optional. True or False?',
  'Bord F34c betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F34c signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F34c' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1147,
  343,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1148,
  343,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  344,
  'إلى أي فئة تنتمي العلامة F35؟',
  'Which category does sign F35 belong to?',
  'Tot welke categorie behoort bord F35?',
  'À quelle catégorie appartient le panneau F35?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F35' LIMIT 1),
  'العلامة F35 تنتمي إلى فئة علامات إرشادية',
  'Sign F35 belongs to Information Signs',
  'Bord F35 behoort tot Informatieborden',
  'Le panneau F35 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1149,
  344,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1150,
  344,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1151,
  344,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1152,
  344,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  345,
  'العلامة F35 تعني: علامة F35. صحيح أم خطأ؟',
  'Sign F35 means: Plaats voor toerisme of ontspanning.. True or False?',
  'Bord F35 betekent: Plaats voor toerisme of ontspanning.. Waar of Onwaar?',
  'Le panneau F35 signifie: Plaats voor toerisme of ontspanning.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F35' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1153,
  345,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1154,
  345,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  346,
  'ما هي العلامة المرورية F37؟',
  'What does the traffic sign F37 mean?',
  'Wat betekent verkeersbord F37?',
  'Que signifie le panneau de signalisation F37?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F37' LIMIT 1),
  'العلامة F37 تعني: علامة F37',
  'Sign F37 means: Wegwijzer naar hotels, campings, restaurant.',
  'Bord F37 betekent: Wegwijzer naar hotels, campings, restaurant.',
  'Le panneau F37 signifie: Wegwijzer naar hotels, campings, restaurant.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1155,
  346,
  'علامة A37',
  'Zijwind.',
  'Zijwind.',
  'Zijwind.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1156,
  346,
  'منعطفات خطرة، الأول لليسار',
  'Dangerous double or multiple bends, first to the left',
  'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar links.',
  'Virages dangereux, le premier à gauche',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1157,
  346,
  'طريق سريع',
  'Motorway',
  'Einde autosnelweg.',
  'Autoroute',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1158,
  346,
  'علامة F37',
  'Wegwijzer naar hotels, campings, restaurant.',
  'Wegwijzer naar hotels, campings, restaurant.',
  'Wegwijzer naar hotels, campings, restaurant.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  347,
  'العلامة F37 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F37 means: This sign is optional. True or False?',
  'Bord F37 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F37 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F37' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1159,
  347,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1160,
  347,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  348,
  'إلى أي فئة تنتمي العلامة F37؟',
  'Which category does sign F37 belong to?',
  'Tot welke categorie behoort bord F37?',
  'À quelle catégorie appartient le panneau F37?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F37' LIMIT 1),
  'العلامة F37 تنتمي إلى فئة علامات إرشادية',
  'Sign F37 belongs to Information Signs',
  'Bord F37 behoort tot Informatieborden',
  'Le panneau F37 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1161,
  348,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1162,
  348,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1163,
  348,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1164,
  348,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  349,
  'العلامة F39 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F39 means: This sign is optional. True or False?',
  'Bord F39 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F39 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F39' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1165,
  349,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1166,
  349,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  350,
  'ما هي العلامة المرورية F39؟',
  'What does the traffic sign F39 mean?',
  'Wat betekent verkeersbord F39?',
  'Que signifie le panneau de signalisation F39?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F39' LIMIT 1),
  'العلامة F39 تعني: علامة F39',
  'Sign F39 means: Aankondiging van een omleiding.',
  'Bord F39 betekent: Aankondiging van een omleiding.',
  'Le panneau F39 signifie: Aankondiging van een omleiding.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1167,
  350,
  'علامة F31',
  'Wegwijzer autostrade',
  'Wegwijzer autostrade',
  'Wegwijzer autostrade',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1168,
  350,
  'علامة A33',
  'Verkeerslichten.',
  'Verkeerslichten.',
  'Verkeerslichten.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1169,
  350,
  'علامة F39',
  'Aankondiging van een omleiding.',
  'Aankondiging van een omleiding.',
  'Aankondiging van een omleiding.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1170,
  350,
  'علامة C27',
  'Verboden voor voertuigen breder dan het aangeduide.',
  'Verboden voor voertuigen breder dan het aangeduide.',
  'Verboden voor voertuigen breder dan het aangeduide.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  351,
  'العلامة F41 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F41 means: This sign is optional. True or False?',
  'Bord F41 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F41 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F41' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1171,
  351,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1172,
  351,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  352,
  'إلى أي فئة تنتمي العلامة F41؟',
  'Which category does sign F41 belong to?',
  'Tot welke categorie behoort bord F41?',
  'À quelle catégorie appartient le panneau F41?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F41' LIMIT 1),
  'العلامة F41 تنتمي إلى فئة علامات إرشادية',
  'Sign F41 belongs to Information Signs',
  'Bord F41 behoort tot Informatieborden',
  'Le panneau F41 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1173,
  352,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1174,
  352,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1175,
  352,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1176,
  352,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  353,
  'ما هي العلامة المرورية F41؟',
  'What does the traffic sign F41 mean?',
  'Wat betekent verkeersbord F41?',
  'Que signifie le panneau de signalisation F41?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F41' LIMIT 1),
  'العلامة F41 تعني: علامة F41',
  'Sign F41 means: Wegwijzer omleidingsweg',
  'Bord F41 betekent: Wegwijzer omleidingsweg',
  'Le panneau F41 signifie: Wegwijzer omleidingsweg',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1177,
  353,
  'علامة F41',
  'Wegwijzer omleidingsweg',
  'Wegwijzer omleidingsweg',
  'Wegwijzer omleidingsweg',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1178,
  353,
  'علامة B15a',
  'Voorrang op de kruisende zijwegen',
  'Voorrang op de kruisende zijwegen',
  'Voorrang op de kruisende zijwegen',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1179,
  353,
  'علامة A49',
  'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1180,
  353,
  'علامة C39',
  'Verbod voertuigen met toegelaten massa > 3500 kg in te halen',
  'Verbod voertuigen met toegelaten massa > 3500 kg in te halen',
  'Verbod voertuigen met toegelaten massa > 3500 kg in te halen',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  354,
  'العلامة F43 تعني: علامة F43. صحيح أم خطأ؟',
  'Sign F43 means: Gemeentegrens. True or False?',
  'Bord F43 betekent: Gemeentegrens. Waar of Onwaar?',
  'Le panneau F43 signifie: Gemeentegrens. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F43' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1181,
  354,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1182,
  354,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  355,
  'إلى أي فئة تنتمي العلامة F43؟',
  'Which category does sign F43 belong to?',
  'Tot welke categorie behoort bord F43?',
  'À quelle catégorie appartient le panneau F43?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F43' LIMIT 1),
  'العلامة F43 تنتمي إلى فئة علامات إرشادية',
  'Sign F43 belongs to Information Signs',
  'Bord F43 behoort tot Informatieborden',
  'Le panneau F43 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1183,
  355,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1184,
  355,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1185,
  355,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1186,
  355,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  356,
  'ما هي العلامة المرورية F43؟',
  'What does the traffic sign F43 mean?',
  'Wat betekent verkeersbord F43?',
  'Que signifie le panneau de signalisation F43?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F43' LIMIT 1),
  'العلامة F43 تعني: علامة F43',
  'Sign F43 means: Gemeentegrens',
  'Bord F43 betekent: Gemeentegrens',
  'Le panneau F43 signifie: Gemeentegrens',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1187,
  356,
  'علامة F29',
  'Wegwijzer',
  'Wegwijzer',
  'Wegwijzer',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1188,
  356,
  'علامة F43',
  'Gemeentegrens',
  'Gemeentegrens',
  'Gemeentegrens',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1189,
  356,
  'منعطفات خطرة، الأول لليمين',
  'Dangerous double or multiple bends, first to the right',
  'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts.',
  'Virages dangereux, le premier à droite',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1190,
  356,
  'علامة M18',
  'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  357,
  'ما هي العلامة المرورية F45؟',
  'What does the traffic sign F45 mean?',
  'Wat betekent verkeersbord F45?',
  'Que signifie le panneau de signalisation F45?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F45' LIMIT 1),
  'العلامة F45 تعني: علامة F45',
  'Sign F45 means: Doodlopende weg, rechtse doorgang.',
  'Bord F45 betekent: Doodlopende weg, rechtse doorgang.',
  'Le panneau F45 signifie: Doodlopende weg, rechtse doorgang.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1191,
  357,
  'علامة E9b',
  'Parkeren uitsluitend voor auto''s.',
  'Parkeren uitsluitend voor auto''s.',
  'Parkeren uitsluitend voor auto''s.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1192,
  357,
  'علامة C41',
  'Einde van het verbod opgelegd door het verkeersbord C39.',
  'Einde van het verbod opgelegd door het verkeersbord C39.',
  'Einde van het verbod opgelegd door het verkeersbord C39.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1193,
  357,
  'علامة F45',
  'Doodlopende weg, rechtse doorgang.',
  'Doodlopende weg, rechtse doorgang.',
  'Doodlopende weg, rechtse doorgang.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1194,
  357,
  'علامة A41',
  'Overweg met slagbomen.',
  'Overweg met slagbomen.',
  'Overweg met slagbomen.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  358,
  'إلى أي فئة تنتمي العلامة F45؟',
  'Which category does sign F45 belong to?',
  'Tot welke categorie behoort bord F45?',
  'À quelle catégorie appartient le panneau F45?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F45' LIMIT 1),
  'العلامة F45 تنتمي إلى فئة علامات إرشادية',
  'Sign F45 belongs to Information Signs',
  'Bord F45 behoort tot Informatieborden',
  'Le panneau F45 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1195,
  358,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1196,
  358,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1197,
  358,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1198,
  358,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  359,
  'العلامة F45 تعني: علامة F45. صحيح أم خطأ؟',
  'Sign F45 means: Doodlopende weg, rechtse doorgang.. True or False?',
  'Bord F45 betekent: Doodlopende weg, rechtse doorgang.. Waar of Onwaar?',
  'Le panneau F45 signifie: Doodlopende weg, rechtse doorgang.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F45' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1199,
  359,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1200,
  359,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  360,
  'إلى أي فئة تنتمي العلامة F45b؟',
  'Which category does sign F45b belong to?',
  'Tot welke categorie behoort bord F45b?',
  'À quelle catégorie appartient le panneau F45b?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F45b' LIMIT 1),
  'العلامة F45b تنتمي إلى فئة علامات إرشادية',
  'Sign F45b belongs to Information Signs',
  'Bord F45b behoort tot Informatieborden',
  'Le panneau F45b appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1201,
  360,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1202,
  360,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1203,
  360,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1204,
  360,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  361,
  'ما هي العلامة المرورية F45b؟',
  'What does the traffic sign F45b mean?',
  'Wat betekent verkeersbord F45b?',
  'Que signifie le panneau de signalisation F45b?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F45b' LIMIT 1),
  'العلامة F45b تعني: علامة F45b',
  'Sign F45b means: Doodlopende weg, uitgezonderd voetgangers en fietsers.',
  'Bord F45b betekent: Doodlopende weg, uitgezonderd voetgangers en fietsers.',
  'Le panneau F45b signifie: Doodlopende weg, uitgezonderd voetgangers en fietsers.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1205,
  361,
  'علامة F45b',
  'Doodlopende weg, uitgezonderd voetgangers en fietsers.',
  'Doodlopende weg, uitgezonderd voetgangers en fietsers.',
  'Doodlopende weg, uitgezonderd voetgangers en fietsers.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1206,
  361,
  'علامة F35',
  'Plaats voor toerisme of ontspanning.',
  'Plaats voor toerisme of ontspanning.',
  'Plaats voor toerisme of ontspanning.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1207,
  361,
  'علامة B23',
  'Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtdoor rijden en de verkeerslichten voorbijrijden',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1208,
  361,
  'منطقة 30',
  'Zone 30',
  'Zone 30 km/u.',
  'Zone 30',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  362,
  'العلامة F45b تعني: علامة F45b. صحيح أم خطأ؟',
  'Sign F45b means: Doodlopende weg, uitgezonderd voetgangers en fietsers.. True or False?',
  'Bord F45b betekent: Doodlopende weg, uitgezonderd voetgangers en fietsers.. Waar of Onwaar?',
  'Le panneau F45b signifie: Doodlopende weg, uitgezonderd voetgangers en fietsers.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F45b' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1209,
  362,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1210,
  362,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  363,
  'العلامة F47 تعني: علامة F47. صحيح أم خطأ؟',
  'Sign F47 means: Einde van de werken.. True or False?',
  'Bord F47 betekent: Einde van de werken.. Waar of Onwaar?',
  'Le panneau F47 signifie: Einde van de werken.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F47' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1211,
  363,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1212,
  363,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  364,
  'ما هي العلامة المرورية F47؟',
  'What does the traffic sign F47 mean?',
  'Wat betekent verkeersbord F47?',
  'Que signifie le panneau de signalisation F47?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F47' LIMIT 1),
  'العلامة F47 تعني: علامة F47',
  'Sign F47 means: Einde van de werken.',
  'Bord F47 betekent: Einde van de werken.',
  'Le panneau F47 signifie: Einde van de werken.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1213,
  364,
  'علامة D1f',
  'Verplicht de aangeduide richting te volgen (rechtsaf)',
  'Verplicht de aangeduide richting te volgen (rechtsaf)',
  'Verplicht de aangeduide richting te volgen (rechtsaf)',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1214,
  364,
  'معبر للمشاة',
  'Pedestrian crossing',
  'Oversteekplaats voor voetgangers.',
  'Passage pour piétons',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1215,
  364,
  'علامة F47',
  'Einde van de werken.',
  'Einde van de werken.',
  'Einde van de werken.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1216,
  364,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen die gevaarlijke ontvlambare of ontplofbare stoffen vervoeren.',
  'Accès interdit',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  365,
  'إلى أي فئة تنتمي العلامة F47؟',
  'Which category does sign F47 belong to?',
  'Tot welke categorie behoort bord F47?',
  'À quelle catégorie appartient le panneau F47?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F47' LIMIT 1),
  'العلامة F47 تنتمي إلى فئة علامات إرشادية',
  'Sign F47 belongs to Information Signs',
  'Bord F47 behoort tot Informatieborden',
  'Le panneau F47 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1217,
  365,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1218,
  365,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1219,
  365,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1220,
  365,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  366,
  'إلى أي فئة تنتمي العلامة F49؟',
  'Which category does sign F49 belong to?',
  'Tot welke categorie behoort bord F49?',
  'À quelle catégorie appartient le panneau F49?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F49' LIMIT 1),
  'العلامة F49 تنتمي إلى فئة علامات إرشادية',
  'Sign F49 belongs to Information Signs',
  'Bord F49 behoort tot Informatieborden',
  'Le panneau F49 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1221,
  366,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1222,
  366,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1223,
  366,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1224,
  366,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  367,
  'ما هي العلامة المرورية F49؟',
  'What does the traffic sign F49 mean?',
  'Wat betekent verkeersbord F49?',
  'Que signifie le panneau de signalisation F49?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F49' LIMIT 1),
  'العلامة F49 تعني: معبر للمشاة',
  'Sign F49 means: Pedestrian crossing',
  'Bord F49 betekent: Oversteekplaats voor voetgangers.',
  'Le panneau F49 signifie: Passage pour piétons',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1225,
  367,
  'علامة F31',
  'Wegwijzer autostrade',
  'Wegwijzer autostrade',
  'Wegwijzer autostrade',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1226,
  367,
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Uitgezonderd fietsers en bromfietsers klasse A.',
  'Cyclistes et cyclomotoristes',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1227,
  367,
  'طريق سريع',
  'Motorway',
  'Autosnelweg.',
  'Autoroute',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1228,
  367,
  'معبر للمشاة',
  'Pedestrian crossing',
  'Oversteekplaats voor voetgangers.',
  'Passage pour piétons',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  368,
  'إلى أي فئة تنتمي العلامة F50؟',
  'Which category does sign F50 belong to?',
  'Tot welke categorie behoort bord F50?',
  'À quelle catégorie appartient le panneau F50?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F50' LIMIT 1),
  'العلامة F50 تنتمي إلى فئة علامات إرشادية',
  'Sign F50 belongs to Information Signs',
  'Bord F50 behoort tot Informatieborden',
  'Le panneau F50 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1229,
  368,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1230,
  368,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1231,
  368,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1232,
  368,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  369,
  'العلامة F50 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F50 means: This sign is optional. True or False?',
  'Bord F50 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F50 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F50' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1233,
  369,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1234,
  369,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  370,
  'العلامة F50b تعني: علامة F50b. صحيح أم خطأ؟',
  'Sign F50b means: Opgepast als je van richting veranderd, fietsers.. True or False?',
  'Bord F50b betekent: Opgepast als je van richting veranderd, fietsers.. Waar of Onwaar?',
  'Le panneau F50b signifie: Opgepast als je van richting veranderd, fietsers.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F50b' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1235,
  370,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1236,
  370,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  371,
  'إلى أي فئة تنتمي العلامة F50b؟',
  'Which category does sign F50b belong to?',
  'Tot welke categorie behoort bord F50b?',
  'À quelle catégorie appartient le panneau F50b?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F50b' LIMIT 1),
  'العلامة F50b تنتمي إلى فئة علامات إرشادية',
  'Sign F50b belongs to Information Signs',
  'Bord F50b behoort tot Informatieborden',
  'Le panneau F50b appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1237,
  371,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1238,
  371,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1239,
  371,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1240,
  371,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  372,
  'ما هي العلامة المرورية F50b؟',
  'What does the traffic sign F50b mean?',
  'Wat betekent verkeersbord F50b?',
  'Que signifie le panneau de signalisation F50b?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F50b' LIMIT 1),
  'العلامة F50b تعني: علامة F50b',
  'Sign F50b means: Opgepast als je van richting veranderd, fietsers.',
  'Bord F50b betekent: Opgepast als je van richting veranderd, fietsers.',
  'Le panneau F50b signifie: Opgepast als je van richting veranderd, fietsers.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1241,
  372,
  'علامة F60',
  'Overdekte parking.',
  'Overdekte parking.',
  'Overdekte parking.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1242,
  372,
  'علامة M18',
  'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1243,
  372,
  'علامة F50b',
  'Opgepast als je van richting veranderd, fietsers.',
  'Opgepast als je van richting veranderd, fietsers.',
  'Opgepast als je van richting veranderd, fietsers.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1244,
  372,
  'علامة F59',
  'Aankondiging van een parking.',
  'Aankondiging van een parking.',
  'Aankondiging van een parking.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  373,
  'ما هي العلامة المرورية F53؟',
  'What does the traffic sign F53 mean?',
  'Wat betekent verkeersbord F53?',
  'Que signifie le panneau de signalisation F53?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F53' LIMIT 1),
  'العلامة F53 تعني: علامة F53',
  'Sign F53 means: Verplegingsinrichting.',
  'Bord F53 betekent: Verplegingsinrichting.',
  'Le panneau F53 signifie: Verplegingsinrichting.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1245,
  373,
  'منعطفات خطرة، الأول لليمين',
  'Dangerous double or multiple bends, first to the right',
  'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts.',
  'Virages dangereux, le premier à droite',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1246,
  373,
  'علامة F53',
  'Verplegingsinrichting.',
  'Verplegingsinrichting.',
  'Verplegingsinrichting.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1247,
  373,
  'علامة E11',
  'Halfmaandelijks parkeren in gans de bebouwde kom.',
  'Halfmaandelijks parkeren in gans de bebouwde kom.',
  'Halfmaandelijks parkeren in gans de bebouwde kom.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1248,
  373,
  'علامة C35',
  'Verbod een voertuig links in te halen.',
  'Verbod een voertuig links in te halen.',
  'Verbod een voertuig links in te halen.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  374,
  'العلامة F53 تعني: علامة F53. صحيح أم خطأ؟',
  'Sign F53 means: Verplegingsinrichting.. True or False?',
  'Bord F53 betekent: Verplegingsinrichting.. Waar of Onwaar?',
  'Le panneau F53 signifie: Verplegingsinrichting.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F53' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1249,
  374,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1250,
  374,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  375,
  'العلامة F55 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F55 means: This sign is optional. True or False?',
  'Bord F55 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F55 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F55' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1251,
  375,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1252,
  375,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  376,
  'ما هي العلامة المرورية F55؟',
  'What does the traffic sign F55 mean?',
  'Wat betekent verkeersbord F55?',
  'Que signifie le panneau de signalisation F55?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F55' LIMIT 1),
  'العلامة F55 تعني: علامة F55',
  'Sign F55 means: Hulppost.',
  'Bord F55 betekent: Hulppost.',
  'Le panneau F55 signifie: Hulppost.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1253,
  376,
  'علامة D5',
  'Verplicht rondgaand verkeer.',
  'Verplicht rondgaand verkeer.',
  'Verplicht rondgaand verkeer.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1254,
  376,
  'تضييق الطريق',
  'Road narrowing',
  'Rijbaanversmalling links',
  'Rétrécissement de chaussée',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1255,
  376,
  'علامة F55',
  'Hulppost.',
  'Hulppost.',
  'Hulppost.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1256,
  376,
  'علامة D11',
  'Verplichte weg voor voetgangers.',
  'Verplichte weg voor voetgangers.',
  'Verplichte weg voor voetgangers.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  377,
  'إلى أي فئة تنتمي العلامة F55؟',
  'Which category does sign F55 belong to?',
  'Tot welke categorie behoort bord F55?',
  'À quelle catégorie appartient le panneau F55?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F55' LIMIT 1),
  'العلامة F55 تنتمي إلى فئة علامات إرشادية',
  'Sign F55 belongs to Information Signs',
  'Bord F55 behoort tot Informatieborden',
  'Le panneau F55 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1257,
  377,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1258,
  377,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1259,
  377,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1260,
  377,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  378,
  'ما هي العلامة المرورية F56؟',
  'What does the traffic sign F56 mean?',
  'Wat betekent verkeersbord F56?',
  'Que signifie le panneau de signalisation F56?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F56' LIMIT 1),
  'العلامة F56 تعني: علامة F56',
  'Sign F56 means: Brandblusapparaat.',
  'Bord F56 betekent: Brandblusapparaat.',
  'Le panneau F56 signifie: Brandblusapparaat.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1261,
  378,
  'علامة M11',
  'Uitgezonderd fietsers en speed pedelecs.',
  'Uitgezonderd fietsers en speed pedelecs.',
  'Uitgezonderd fietsers en speed pedelecs.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1262,
  378,
  'علامة F56',
  'Brandblusapparaat.',
  'Brandblusapparaat.',
  'Brandblusapparaat.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1263,
  378,
  'علامة E9e',
  'Verplicht parkeren op de berm of op het trottoir.',
  'Verplicht parkeren op de berm of op het trottoir.',
  'Verplicht parkeren op de berm of op het trottoir.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1264,
  378,
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Enkel voor fietsers en bromfietsers.',
  'Cyclistes et cyclomotoristes',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  379,
  'إلى أي فئة تنتمي العلامة F56؟',
  'Which category does sign F56 belong to?',
  'Tot welke categorie behoort bord F56?',
  'À quelle catégorie appartient le panneau F56?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F56' LIMIT 1),
  'العلامة F56 تنتمي إلى فئة علامات إرشادية',
  'Sign F56 belongs to Information Signs',
  'Bord F56 behoort tot Informatieborden',
  'Le panneau F56 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1265,
  379,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1266,
  379,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1267,
  379,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1268,
  379,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  380,
  'العلامة F59 تعني: علامة F59. صحيح أم خطأ؟',
  'Sign F59 means: Aankondiging van een parking.. True or False?',
  'Bord F59 betekent: Aankondiging van een parking.. Waar of Onwaar?',
  'Le panneau F59 signifie: Aankondiging van een parking.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F59' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1269,
  380,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1270,
  380,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  381,
  'ما هي العلامة المرورية F59؟',
  'What does the traffic sign F59 mean?',
  'Wat betekent verkeersbord F59?',
  'Que signifie le panneau de signalisation F59?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F59' LIMIT 1),
  'العلامة F59 تعني: علامة F59',
  'Sign F59 means: Aankondiging van een parking.',
  'Bord F59 betekent: Aankondiging van een parking.',
  'Le panneau F59 signifie: Aankondiging van een parking.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1271,
  381,
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Oversteekplaats voor fietsers en bromfietsers.',
  'Cyclistes et cyclomotoristes',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1272,
  381,
  'علامة F59',
  'Aankondiging van een parking.',
  'Aankondiging van een parking.',
  'Aankondiging van een parking.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1273,
  381,
  'علامة D1c',
  'Verplichting links aanhouden.',
  'Verplichting links aanhouden.',
  'Verplichting links aanhouden.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1274,
  381,
  'طريق يؤدي إلى رصيف أو شاطئ',
  'Road leads to quay or waterside',
  'Uitweg op kaai of oever.',
  'Route menant au quai ou à la rive',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  382,
  'العلامة F59a تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F59a means: This sign is optional. True or False?',
  'Bord F59a betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F59a signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F59a' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1275,
  382,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1276,
  382,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  383,
  'ما هي العلامة المرورية F59a؟',
  'What does the traffic sign F59a mean?',
  'Wat betekent verkeersbord F59a?',
  'Que signifie le panneau de signalisation F59a?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F59a' LIMIT 1),
  'العلامة F59a تعني: علامة F59a',
  'Sign F59a means: Aankondiging van een parking.',
  'Bord F59a betekent: Aankondiging van een parking.',
  'Le panneau F59a signifie: Aankondiging van een parking.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1277,
  383,
  'علامة F14',
  'Opstelvak voor fietsers en bromfietsen.',
  'Opstelvak voor fietsers en bromfietsen.',
  'Opstelvak voor fietsers en bromfietsen.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1278,
  383,
  'علامة M5b',
  'fietsers, bromfietsers klasse A, B en speed pedelecs mogen in 2 richtingen.',
  'fietsers, bromfietsers klasse A, B en speed pedelecs mogen in 2 richtingen.',
  'fietsers, bromfietsers klasse A, B en speed pedelecs mogen in 2 richtingen.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1279,
  383,
  'علامة F61',
  'Telefoon.',
  'Telefoon.',
  'Telefoon.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1280,
  383,
  'علامة F59a',
  'Aankondiging van een parking.',
  'Aankondiging van een parking.',
  'Aankondiging van een parking.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  384,
  'العلامة F59b تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F59b means: This sign is optional. True or False?',
  'Bord F59b betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F59b signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F59b' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1281,
  384,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1282,
  384,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  385,
  'ما هي العلامة المرورية F59b؟',
  'What does the traffic sign F59b mean?',
  'Wat betekent verkeersbord F59b?',
  'Que signifie le panneau de signalisation F59b?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F59b' LIMIT 1),
  'العلامة F59b تعني: علامة F59b',
  'Sign F59b means: Aankondiging van een fietsparking.',
  'Bord F59b betekent: Aankondiging van een fietsparking.',
  'Le panneau F59b signifie: Aankondiging van een fietsparking.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1283,
  385,
  'علامة F101c',
  'Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1284,
  385,
  'علامة F59b',
  'Aankondiging van een fietsparking.',
  'Aankondiging van een fietsparking.',
  'Aankondiging van een fietsparking.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1285,
  385,
  'علامة E9f',
  'Verplicht parkeren deels op de berm of op het trottoir.',
  'Verplicht parkeren deels op de berm of op het trottoir.',
  'Verplicht parkeren deels op de berm of op het trottoir.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1286,
  385,
  'شارع الدراجات',
  'Cycle street',
  'Einde fietsstraat.',
  'Rue cyclable',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  386,
  'العلامة F60 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F60 means: This sign is optional. True or False?',
  'Bord F60 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F60 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F60' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1287,
  386,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1288,
  386,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  387,
  'إلى أي فئة تنتمي العلامة F60؟',
  'Which category does sign F60 belong to?',
  'Tot welke categorie behoort bord F60?',
  'À quelle catégorie appartient le panneau F60?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F60' LIMIT 1),
  'العلامة F60 تنتمي إلى فئة علامات إرشادية',
  'Sign F60 belongs to Information Signs',
  'Bord F60 behoort tot Informatieborden',
  'Le panneau F60 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1289,
  387,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1290,
  387,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1291,
  387,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1292,
  387,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  388,
  'ما هي العلامة المرورية F60؟',
  'What does the traffic sign F60 mean?',
  'Wat betekent verkeersbord F60?',
  'Que signifie le panneau de signalisation F60?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F60' LIMIT 1),
  'العلامة F60 تعني: علامة F60',
  'Sign F60 means: Overdekte parking.',
  'Bord F60 betekent: Overdekte parking.',
  'Le panneau F60 signifie: Overdekte parking.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1293,
  388,
  'علامة C33',
  'Verbod om te keren.',
  'Verbod om te keren.',
  'Verbod om te keren.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1294,
  388,
  'علامة C29',
  'Verboden voor voertuigen hoger dan het aangeduide.',
  'Verboden voor voertuigen hoger dan het aangeduide.',
  'Verboden voor voertuigen hoger dan het aangeduide.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1295,
  388,
  'ممنوع التوقف والانتظار',
  'No stopping or parking',
  'Stilstaan en parkeren verboden.',
  'Arrêt et stationnement interdits',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1296,
  388,
  'علامة F60',
  'Overdekte parking.',
  'Overdekte parking.',
  'Overdekte parking.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  389,
  'ما هي العلامة المرورية F61؟',
  'What does the traffic sign F61 mean?',
  'Wat betekent verkeersbord F61?',
  'Que signifie le panneau de signalisation F61?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F61' LIMIT 1),
  'العلامة F61 تعني: علامة F61',
  'Sign F61 means: Telefoon.',
  'Bord F61 betekent: Telefoon.',
  'Le panneau F61 signifie: Telefoon.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1297,
  389,
  'علامة F23d',
  'Nummer van een ringweg.',
  'Nummer van een ringweg.',
  'Nummer van een ringweg.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1298,
  389,
  'علامة F118',
  'Einde van een lage emissiezone',
  'Einde van een lage emissiezone',
  'Einde van een lage emissiezone',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1299,
  389,
  'علامة F61',
  'Telefoon.',
  'Telefoon.',
  'Telefoon.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1300,
  389,
  'علامة A14',
  'Verhoogde inrichting.',
  'Verhoogde inrichting.',
  'Verhoogde inrichting.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  390,
  'إلى أي فئة تنتمي العلامة F61؟',
  'Which category does sign F61 belong to?',
  'Tot welke categorie behoort bord F61?',
  'À quelle catégorie appartient le panneau F61?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F61' LIMIT 1),
  'العلامة F61 تنتمي إلى فئة علامات إرشادية',
  'Sign F61 belongs to Information Signs',
  'Bord F61 behoort tot Informatieborden',
  'Le panneau F61 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1301,
  390,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1302,
  390,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1303,
  390,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1304,
  390,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  391,
  'إلى أي فئة تنتمي العلامة F62؟',
  'Which category does sign F62 belong to?',
  'Tot welke categorie behoort bord F62?',
  'À quelle catégorie appartient le panneau F62?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F62' LIMIT 1),
  'العلامة F62 تنتمي إلى فئة علامات إرشادية',
  'Sign F62 belongs to Information Signs',
  'Bord F62 behoort tot Informatieborden',
  'Le panneau F62 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1305,
  391,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1306,
  391,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1307,
  391,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1308,
  391,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  392,
  'ما هي العلامة المرورية F62؟',
  'What does the traffic sign F62 mean?',
  'Wat betekent verkeersbord F62?',
  'Que signifie le panneau de signalisation F62?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F62' LIMIT 1),
  'العلامة F62 تعني: علامة F62',
  'Sign F62 means: Noodtelefoon.',
  'Bord F62 betekent: Noodtelefoon.',
  'Le panneau F62 signifie: Noodtelefoon.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1309,
  392,
  'علامة A39',
  'Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer.',
  'Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer.',
  'Twee richtingsverkeer toegelaten na een stuk éénrichtingsverkeer.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1310,
  392,
  'علامة M1',
  'Enkel voor fietsers.',
  'Enkel voor fietsers.',
  'Enkel voor fietsers.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1311,
  392,
  'علامة F62',
  'Noodtelefoon.',
  'Noodtelefoon.',
  'Noodtelefoon.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1312,
  392,
  'علامة B15b',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  393,
  'العلامة F62 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F62 means: This sign is optional. True or False?',
  'Bord F62 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F62 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F62' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1313,
  393,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1314,
  393,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  394,
  'ما هي العلامة المرورية F63؟',
  'What does the traffic sign F63 mean?',
  'Wat betekent verkeersbord F63?',
  'Que signifie le panneau de signalisation F63?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F63' LIMIT 1),
  'العلامة F63 تعني: علامة F63',
  'Sign F63 means: - Specifieke brandstof Tankstation met een specifieke brandstof.',
  'Bord F63 betekent: - Specifieke brandstof Tankstation met een specifieke brandstof.',
  'Le panneau F63 signifie: - Specifieke brandstof Tankstation met een specifieke brandstof.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1315,
  394,
  'علامة C37',
  'Einde verbod opgelegd door het verkeersbord C35',
  'Einde verbod opgelegd door het verkeersbord C35',
  'Einde verbod opgelegd door het verkeersbord C35',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1316,
  394,
  'علامة F63',
  '- Specifieke brandstof Tankstation met een specifieke brandstof.',
  '- Specifieke brandstof Tankstation met een specifieke brandstof.',
  '- Specifieke brandstof Tankstation met een specifieke brandstof.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1317,
  394,
  'علامة D1a',
  'Verplichting rechtdoor.',
  'Verplichting rechtdoor.',
  'Verplichting rechtdoor.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1318,
  394,
  'علامة F71',
  'Kampeerterrein.',
  'Kampeerterrein.',
  'Kampeerterrein.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  395,
  'العلامة F63 تعني: علامة F63. صحيح أم خطأ؟',
  'Sign F63 means: - Specifieke brandstof Tankstation met een specifieke brandstof.. True or False?',
  'Bord F63 betekent: - Specifieke brandstof Tankstation met een specifieke brandstof.. Waar of Onwaar?',
  'Le panneau F63 signifie: - Specifieke brandstof Tankstation met een specifieke brandstof.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F63' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1319,
  395,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1320,
  395,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  396,
  'إلى أي فئة تنتمي العلامة F65؟',
  'Which category does sign F65 belong to?',
  'Tot welke categorie behoort bord F65?',
  'À quelle catégorie appartient le panneau F65?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F65' LIMIT 1),
  'العلامة F65 تنتمي إلى فئة علامات إرشادية',
  'Sign F65 belongs to Information Signs',
  'Bord F65 behoort tot Informatieborden',
  'Le panneau F65 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1321,
  396,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1322,
  396,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1323,
  396,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1324,
  396,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  397,
  'العلامة F65 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F65 means: This sign is optional. True or False?',
  'Bord F65 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F65 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F65' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1325,
  397,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1326,
  397,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  398,
  'ما هي العلامة المرورية F65؟',
  'What does the traffic sign F65 mean?',
  'Wat betekent verkeersbord F65?',
  'Que signifie le panneau de signalisation F65?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F65' LIMIT 1),
  'العلامة F65 تعني: علامة F65',
  'Sign F65 means: Hotel of motel.',
  'Bord F65 betekent: Hotel of motel.',
  'Le panneau F65 signifie: Hotel of motel.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1327,
  398,
  'علامة F65',
  'Hotel of motel.',
  'Hotel of motel.',
  'Hotel of motel.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1328,
  398,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van handkarren.',
  'Accès interdit',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1329,
  398,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van bromfietsen en fietsen.',
  'Accès interdit',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1330,
  398,
  'علامة A41',
  'Overweg met slagbomen.',
  'Overweg met slagbomen.',
  'Overweg met slagbomen.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  399,
  'إلى أي فئة تنتمي العلامة F67؟',
  'Which category does sign F67 belong to?',
  'Tot welke categorie behoort bord F67?',
  'À quelle catégorie appartient le panneau F67?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F67' LIMIT 1),
  'العلامة F67 تنتمي إلى فئة علامات إرشادية',
  'Sign F67 belongs to Information Signs',
  'Bord F67 behoort tot Informatieborden',
  'Le panneau F67 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1331,
  399,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1332,
  399,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1333,
  399,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1334,
  399,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  400,
  'العلامة F67 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F67 means: This sign is optional. True or False?',
  'Bord F67 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F67 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F67' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1335,
  400,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1336,
  400,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  401,
  'ما هي العلامة المرورية F69؟',
  'What does the traffic sign F69 mean?',
  'Wat betekent verkeersbord F69?',
  'Que signifie le panneau de signalisation F69?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F69' LIMIT 1),
  'العلامة F69 تعني: علامة F69',
  'Sign F69 means: Drankgelegenheid.',
  'Bord F69 betekent: Drankgelegenheid.',
  'Le panneau F69 signifie: Drankgelegenheid.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1337,
  401,
  'قف',
  'Stop',
  'Stoppen en voorrang verlenen',
  'Stop',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1338,
  401,
  'علامة F69',
  'Drankgelegenheid.',
  'Drankgelegenheid.',
  'Drankgelegenheid.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1339,
  401,
  'علامة M10',
  'Fietsers en bromfietser in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers en bromfietser in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers en bromfietser in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1340,
  401,
  'علامة F63',
  '- Specifieke brandstof Tankstation met een specifieke brandstof.',
  '- Specifieke brandstof Tankstation met een specifieke brandstof.',
  '- Specifieke brandstof Tankstation met een specifieke brandstof.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  402,
  'العلامة F69 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F69 means: This sign is optional. True or False?',
  'Bord F69 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F69 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F69' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1341,
  402,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1342,
  402,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  403,
  'العلامة F71 تعني: علامة F71. صحيح أم خطأ؟',
  'Sign F71 means: Kampeerterrein.. True or False?',
  'Bord F71 betekent: Kampeerterrein.. Waar of Onwaar?',
  'Le panneau F71 signifie: Kampeerterrein.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F71' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1343,
  403,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1344,
  403,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  404,
  'ما هي العلامة المرورية F71؟',
  'What does the traffic sign F71 mean?',
  'Wat betekent verkeersbord F71?',
  'Que signifie le panneau de signalisation F71?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F71' LIMIT 1),
  'العلامة F71 تعني: علامة F71',
  'Sign F71 means: Kampeerterrein.',
  'Bord F71 betekent: Kampeerterrein.',
  'Le panneau F71 signifie: Kampeerterrein.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1345,
  404,
  'أطفال',
  'Children',
  'Opgelet kinderen.',
  'Enfants',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1346,
  404,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor ruiters.',
  'Accès interdit',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1347,
  404,
  'علامة F71',
  'Kampeerterrein.',
  'Kampeerterrein.',
  'Kampeerterrein.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1348,
  404,
  'علامة F65',
  'Hotel of motel.',
  'Hotel of motel.',
  'Hotel of motel.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  405,
  'إلى أي فئة تنتمي العلامة F71؟',
  'Which category does sign F71 belong to?',
  'Tot welke categorie behoort bord F71?',
  'À quelle catégorie appartient le panneau F71?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F71' LIMIT 1),
  'العلامة F71 تنتمي إلى فئة علامات إرشادية',
  'Sign F71 belongs to Information Signs',
  'Bord F71 behoort tot Informatieborden',
  'Le panneau F71 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1349,
  405,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1350,
  405,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1351,
  405,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1352,
  405,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  406,
  'ما هي العلامة المرورية F73؟',
  'What does the traffic sign F73 mean?',
  'Wat betekent verkeersbord F73?',
  'Que signifie le panneau de signalisation F73?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F73' LIMIT 1),
  'العلامة F73 تعني: علامة F73',
  'Sign F73 means: Caravanterrein.',
  'Bord F73 betekent: Caravanterrein.',
  'Le panneau F73 signifie: Caravanterrein.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1353,
  406,
  'نفق',
  'Tunnel',
  'Tunnel.',
  'Tunnel',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1354,
  406,
  'علامة F73',
  'Caravanterrein.',
  'Caravanterrein.',
  'Caravanterrein.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1355,
  406,
  'طريق سيارات',
  'Expressway',
  'Einde van de autoweg.',
  'Route pour automobiles',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1356,
  406,
  'علامة B15e',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  407,
  'العلامة F73 تعني: علامة F73. صحيح أم خطأ؟',
  'Sign F73 means: Caravanterrein.. True or False?',
  'Bord F73 betekent: Caravanterrein.. Waar of Onwaar?',
  'Le panneau F73 signifie: Caravanterrein.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F73' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1357,
  407,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1358,
  407,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  408,
  'العلامة F75 تعني: علامة F75. صحيح أم خطأ؟',
  'Sign F75 means: Jeugdherberg.. True or False?',
  'Bord F75 betekent: Jeugdherberg.. Waar of Onwaar?',
  'Le panneau F75 signifie: Jeugdherberg.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F75' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1359,
  408,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1360,
  408,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  409,
  'إلى أي فئة تنتمي العلامة F75؟',
  'Which category does sign F75 belong to?',
  'Tot welke categorie behoort bord F75?',
  'À quelle catégorie appartient le panneau F75?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F75' LIMIT 1),
  'العلامة F75 تنتمي إلى فئة علامات إرشادية',
  'Sign F75 belongs to Information Signs',
  'Bord F75 behoort tot Informatieborden',
  'Le panneau F75 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1361,
  409,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1362,
  409,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1363,
  409,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1364,
  409,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  410,
  'ما هي العلامة المرورية F77؟',
  'What does the traffic sign F77 mean?',
  'Wat betekent verkeersbord F77?',
  'Que signifie le panneau de signalisation F77?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F77' LIMIT 1),
  'العلامة F77 تعني: علامة F77',
  'Sign F77 means: Toeristische informatie.',
  'Bord F77 betekent: Toeristische informatie.',
  'Le panneau F77 signifie: Toeristische informatie.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1365,
  410,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor voetgangers.',
  'Accès interdit',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1366,
  410,
  'علامة F77',
  'Toeristische informatie.',
  'Toeristische informatie.',
  'Toeristische informatie.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1367,
  410,
  'علامة F71',
  'Kampeerterrein.',
  'Kampeerterrein.',
  'Kampeerterrein.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1368,
  410,
  'علامة A14',
  'Verhoogde inrichting.',
  'Verhoogde inrichting.',
  'Verhoogde inrichting.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  411,
  'العلامة F77 تعني: علامة F77. صحيح أم خطأ؟',
  'Sign F77 means: Toeristische informatie.. True or False?',
  'Bord F77 betekent: Toeristische informatie.. Waar of Onwaar?',
  'Le panneau F77 signifie: Toeristische informatie.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F77' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1369,
  411,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1370,
  411,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  412,
  'إلى أي فئة تنتمي العلامة F77؟',
  'Which category does sign F77 belong to?',
  'Tot welke categorie behoort bord F77?',
  'À quelle catégorie appartient le panneau F77?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F77' LIMIT 1),
  'العلامة F77 تنتمي إلى فئة علامات إرشادية',
  'Sign F77 belongs to Information Signs',
  'Bord F77 behoort tot Informatieborden',
  'Le panneau F77 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1371,
  412,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1372,
  412,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1373,
  412,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1374,
  412,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  413,
  'ما هي العلامة المرورية F87؟',
  'What does the traffic sign F87 mean?',
  'Wat betekent verkeersbord F87?',
  'Que signifie le panneau de signalisation F87?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F87' LIMIT 1),
  'العلامة F87 تعني: علامة F87',
  'Sign F87 means: Verhoogde inrichting (vluchtheuvel).',
  'Bord F87 betekent: Verhoogde inrichting (vluchtheuvel).',
  'Le panneau F87 signifie: Verhoogde inrichting (vluchtheuvel).',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1375,
  413,
  'علامة F41',
  'Wegwijzer omleidingsweg',
  'Wegwijzer omleidingsweg',
  'Wegwijzer omleidingsweg',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1376,
  413,
  'علامة F21',
  'Rechts of links voorbijrijden toegelaten.',
  'Rechts of links voorbijrijden toegelaten.',
  'Rechts of links voorbijrijden toegelaten.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1377,
  413,
  'علامة F87',
  'Verhoogde inrichting (vluchtheuvel).',
  'Verhoogde inrichting (vluchtheuvel).',
  'Verhoogde inrichting (vluchtheuvel).',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1378,
  413,
  'طريق سريع',
  'Motorway',
  'Einde autosnelweg.',
  'Autoroute',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  414,
  'إلى أي فئة تنتمي العلامة F87؟',
  'Which category does sign F87 belong to?',
  'Tot welke categorie behoort bord F87?',
  'À quelle catégorie appartient le panneau F87?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F87' LIMIT 1),
  'العلامة F87 تنتمي إلى فئة علامات إرشادية',
  'Sign F87 belongs to Information Signs',
  'Bord F87 behoort tot Informatieborden',
  'Le panneau F87 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1379,
  414,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1380,
  414,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1381,
  414,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1382,
  414,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  415,
  'العلامة F87 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F87 means: This sign is optional. True or False?',
  'Bord F87 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F87 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F87' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1383,
  415,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1384,
  415,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  416,
  'ما هي العلامة المرورية F97؟',
  'What does the traffic sign F97 mean?',
  'Wat betekent verkeersbord F97?',
  'Que signifie le panneau de signalisation F97?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F97' LIMIT 1),
  'العلامة F97 تعني: علامة F97',
  'Sign F97 means: Rijstrook versmalling.',
  'Bord F97 betekent: Rijstrook versmalling.',
  'Le panneau F97 signifie: Rijstrook versmalling.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1385,
  416,
  'علامة F97',
  'Rijstrook versmalling.',
  'Rijstrook versmalling.',
  'Rijstrook versmalling.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1386,
  416,
  'قف',
  'Stop',
  'Stoppen en voorrang verlenen',
  'Stop',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1387,
  416,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van rijwielen.',
  'Accès interdit',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1388,
  416,
  'منطقة 30',
  'Zone 30',
  'Einde zone 30 km/u.',
  'Zone 30',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  417,
  'العلامة F97 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F97 means: This sign is optional. True or False?',
  'Bord F97 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F97 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F97' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1389,
  417,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1390,
  417,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  418,
  'إلى أي فئة تنتمي العلامة F97؟',
  'Which category does sign F97 belong to?',
  'Tot welke categorie behoort bord F97?',
  'À quelle catégorie appartient le panneau F97?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F97' LIMIT 1),
  'العلامة F97 تنتمي إلى فئة علامات إرشادية',
  'Sign F97 belongs to Information Signs',
  'Bord F97 behoort tot Informatieborden',
  'Le panneau F97 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1391,
  418,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1392,
  418,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1393,
  418,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1394,
  418,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  419,
  'إلى أي فئة تنتمي العلامة F99a؟',
  'Which category does sign F99a belong to?',
  'Tot welke categorie behoort bord F99a?',
  'À quelle catégorie appartient le panneau F99a?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F99a' LIMIT 1),
  'العلامة F99a تنتمي إلى فئة علامات إرشادية',
  'Sign F99a belongs to Information Signs',
  'Bord F99a behoort tot Informatieborden',
  'Le panneau F99a appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1395,
  419,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1396,
  419,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1397,
  419,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1398,
  419,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  420,
  'ما هي العلامة المرورية F99a؟',
  'What does the traffic sign F99a mean?',
  'Wat betekent verkeersbord F99a?',
  'Que signifie le panneau de signalisation F99a?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F99a' LIMIT 1),
  'العلامة F99a تعني: علامة F99a',
  'Sign F99a means: Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Bord F99a betekent: Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Le panneau F99a signifie: Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1399,
  420,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van autocars.',
  'Accès interdit',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1400,
  420,
  'علامة F19',
  'Eenrichtingsverkeer.',
  'Eenrichtingsverkeer.',
  'Eenrichtingsverkeer.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1401,
  420,
  'علامة B21',
  'Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen',
  'Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen',
  'Smalle doorgang voorrang ten opzichte van de bestuurders die uit de tegenovergestelde richting komen',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1402,
  420,
  'علامة F99a',
  'Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  421,
  'العلامة F99b تعني: علامة F99b. صحيح أم خطأ؟',
  'Sign F99b means: Deel van de openbare weg voorbehouden voor fietsers en voetgangers. True or False?',
  'Bord F99b betekent: Deel van de openbare weg voorbehouden voor fietsers en voetgangers. Waar of Onwaar?',
  'Le panneau F99b signifie: Deel van de openbare weg voorbehouden voor fietsers en voetgangers. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F99b' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1403,
  421,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1404,
  421,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  422,
  'إلى أي فئة تنتمي العلامة F99b؟',
  'Which category does sign F99b belong to?',
  'Tot welke categorie behoort bord F99b?',
  'À quelle catégorie appartient le panneau F99b?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F99b' LIMIT 1),
  'العلامة F99b تنتمي إلى فئة علامات إرشادية',
  'Sign F99b belongs to Information Signs',
  'Bord F99b behoort tot Informatieborden',
  'Le panneau F99b appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1405,
  422,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1406,
  422,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1407,
  422,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1408,
  422,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  423,
  'ما هي العلامة المرورية F99b؟',
  'What does the traffic sign F99b mean?',
  'Wat betekent verkeersbord F99b?',
  'Que signifie le panneau de signalisation F99b?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F99b' LIMIT 1),
  'العلامة F99b تعني: علامة F99b',
  'Sign F99b means: Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  'Bord F99b betekent: Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  'Le panneau F99b signifie: Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1409,
  423,
  'علامة D9a',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1410,
  423,
  'علامة D1f',
  'Verplicht de aangeduide richting te volgen (rechtsaf)',
  'Verplicht de aangeduide richting te volgen (rechtsaf)',
  'Verplicht de aangeduide richting te volgen (rechtsaf)',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1411,
  423,
  'علامة F101a',
  'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1412,
  423,
  'علامة F99b',
  'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  424,
  'إلى أي فئة تنتمي العلامة F99c؟',
  'Which category does sign F99c belong to?',
  'Tot welke categorie behoort bord F99c?',
  'À quelle catégorie appartient le panneau F99c?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F99c' LIMIT 1),
  'العلامة F99c تنتمي إلى فئة علامات إرشادية',
  'Sign F99c belongs to Information Signs',
  'Bord F99c behoort tot Informatieborden',
  'Le panneau F99c appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1413,
  424,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1414,
  424,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1415,
  424,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1416,
  424,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  425,
  'ما هي العلامة المرورية F99c؟',
  'What does the traffic sign F99c mean?',
  'Wat betekent verkeersbord F99c?',
  'Que signifie le panneau de signalisation F99c?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F99c' LIMIT 1),
  'العلامة F99c تعني: علامة F99c',
  'Sign F99c means: Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Bord F99c betekent: Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Le panneau F99c signifie: Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1417,
  425,
  'علامة F99c',
  'Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1418,
  425,
  'قف',
  'Stop',
  'Stoppen en voorrang verlenen',
  'Stop',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1419,
  425,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor voetgangers.',
  'Accès interdit',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1420,
  425,
  'علامة F17',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  426,
  'ما هي العلامة المرورية F101a؟',
  'What does the traffic sign F101a mean?',
  'Wat betekent verkeersbord F101a?',
  'Que signifie le panneau de signalisation F101a?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F101a' LIMIT 1),
  'العلامة F101a تعني: علامة F101a',
  'Sign F101a means: Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Bord F101a betekent: Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Le panneau F101a signifie: Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1421,
  426,
  'علامة F99b',
  'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1422,
  426,
  'اتجاه ممنوع',
  'Direction prohibited',
  'Verboden richting voor iedere bestuurder',
  'Direction interdite',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1423,
  426,
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Enkel voor fietsers en bromfietsers.',
  'Cyclistes et cyclomotoristes',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1424,
  426,
  'علامة F101a',
  'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  427,
  'إلى أي فئة تنتمي العلامة F101a؟',
  'Which category does sign F101a belong to?',
  'Tot welke categorie behoort bord F101a?',
  'À quelle catégorie appartient le panneau F101a?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F101a' LIMIT 1),
  'العلامة F101a تنتمي إلى فئة علامات إرشادية',
  'Sign F101a belongs to Information Signs',
  'Bord F101a behoort tot Informatieborden',
  'Le panneau F101a appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1425,
  427,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1426,
  427,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1427,
  427,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1428,
  427,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  428,
  'العلامة F101a تعني: علامة F101a. صحيح أم خطأ؟',
  'Sign F101a means: Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.. True or False?',
  'Bord F101a betekent: Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.. Waar of Onwaar?',
  'Le panneau F101a signifie: Einde voorbehouden voor het verkeer van voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F101a' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1429,
  428,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1430,
  428,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  429,
  'العلامة F101b تعني: علامة F101b. صحيح أم خطأ؟',
  'Sign F101b means: Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.. True or False?',
  'Bord F101b betekent: Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.. Waar of Onwaar?',
  'Le panneau F101b signifie: Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F101b' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1431,
  429,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1432,
  429,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  430,
  'ما هي العلامة المرورية F101b؟',
  'What does the traffic sign F101b mean?',
  'Wat betekent verkeersbord F101b?',
  'Que signifie le panneau de signalisation F101b?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F101b' LIMIT 1),
  'العلامة F101b تعني: علامة F101b',
  'Sign F101b means: Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.',
  'Bord F101b betekent: Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.',
  'Le panneau F101b signifie: Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1433,
  430,
  'طريق سريع',
  'Motorway',
  'Nummer van een autosnelweg.',
  'Autoroute',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1434,
  430,
  'علامة B15c',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1435,
  430,
  'علامة F101b',
  'Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.',
  'Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.',
  'Einde deel van de openbare weg voorbehouden voor fietsers en voetgangers.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1436,
  430,
  'علامة B15g',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  431,
  'العلامة F101c تعني: علامة F101c. صحيح أم خطأ؟',
  'Sign F101c means: Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.. True or False?',
  'Bord F101c betekent: Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.. Waar of Onwaar?',
  'Le panneau F101c signifie: Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F101c' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1437,
  431,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1438,
  431,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  432,
  'ما هي العلامة المرورية F101c؟',
  'What does the traffic sign F101c mean?',
  'Wat betekent verkeersbord F101c?',
  'Que signifie le panneau de signalisation F101c?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F101c' LIMIT 1),
  'العلامة F101c تعني: علامة F101c',
  'Sign F101c means: Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Bord F101c betekent: Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Le panneau F101c signifie: Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1439,
  432,
  'علامة B15d',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  'Voorrang op kruisende zijweg',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1440,
  432,
  'علامة A49',
  'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  'Openbare weg kruist met een of meer in de rijbaan aangelegde sporen.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1441,
  432,
  'علامة A51',
  'Gevaar dat niet door een speciaal symbool wordt bepaald.',
  'Gevaar dat niet door een speciaal symbool wordt bepaald.',
  'Gevaar dat niet door een speciaal symbool wordt bepaald.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1442,
  432,
  'علامة F101c',
  'Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  'Einde voorbehouden voor het verkeer van landbouwvoertuigen, voetgangers, fietsers, ruiters en bestuurders van speed pedelecs.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  433,
  'العلامة F103 تعني: علامة F103. صحيح أم خطأ؟',
  'Sign F103 means: Begin van een voetgangerszone. True or False?',
  'Bord F103 betekent: Begin van een voetgangerszone. Waar of Onwaar?',
  'Le panneau F103 signifie: Begin van een voetgangerszone. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F103' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1443,
  433,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1444,
  433,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  434,
  'ما هي العلامة المرورية F103؟',
  'What does the traffic sign F103 mean?',
  'Wat betekent verkeersbord F103?',
  'Que signifie le panneau de signalisation F103?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F103' LIMIT 1),
  'العلامة F103 تعني: علامة F103',
  'Sign F103 means: Begin van een voetgangerszone',
  'Bord F103 betekent: Begin van een voetgangerszone',
  'Le panneau F103 signifie: Begin van een voetgangerszone',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1445,
  434,
  'جسر متحرك',
  'Movable bridge',
  'Beweegbare brug.',
  'Pont mobile',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1446,
  434,
  'علامة F103',
  'Begin van een voetgangerszone',
  'Begin van een voetgangerszone',
  'Begin van een voetgangerszone',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1447,
  434,
  'علامة D1a',
  'Verplichting rechtdoor.',
  'Verplichting rechtdoor.',
  'Verplichting rechtdoor.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1448,
  434,
  'علامة M1',
  'Enkel voor fietsers.',
  'Enkel voor fietsers.',
  'Enkel voor fietsers.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  435,
  'ما هي العلامة المرورية F105؟',
  'What does the traffic sign F105 mean?',
  'Wat betekent verkeersbord F105?',
  'Que signifie le panneau de signalisation F105?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F105' LIMIT 1),
  'العلامة F105 تعني: علامة F105',
  'Sign F105 means: Einde van een voetgangerszone',
  'Bord F105 betekent: Einde van een voetgangerszone',
  'Le panneau F105 signifie: Einde van een voetgangerszone',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1449,
  435,
  'علامة M7',
  'Verbod voor bromfietsen klasse B.',
  'Verbod voor bromfietsen klasse B.',
  'Verbod voor bromfietsen klasse B.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1450,
  435,
  'علامة F103',
  'Begin van een voetgangerszone',
  'Begin van een voetgangerszone',
  'Begin van een voetgangerszone',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1451,
  435,
  'علامة F105',
  'Einde van een voetgangerszone',
  'Einde van een voetgangerszone',
  'Einde van een voetgangerszone',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1452,
  435,
  'علامة M18',
  'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  436,
  'العلامة F105 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F105 means: This sign is optional. True or False?',
  'Bord F105 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F105 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F105' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1453,
  436,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1454,
  436,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  437,
  'ما هي العلامة المرورية F111؟',
  'What does the traffic sign F111 mean?',
  'Wat betekent verkeersbord F111?',
  'Que signifie le panneau de signalisation F111?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F111' LIMIT 1),
  'العلامة F111 تعني: شارع الدراجات',
  'Sign F111 means: Cycle street',
  'Bord F111 betekent: Fietsstraat.',
  'Le panneau F111 signifie: Rue cyclable',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1455,
  437,
  'علامة D5',
  'Verplicht rondgaand verkeer.',
  'Verplicht rondgaand verkeer.',
  'Verplicht rondgaand verkeer.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1456,
  437,
  'قف',
  'Stop',
  'Tolpost. Verbod voorbij te rijden zonder te stoppen.',
  'Stop',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1457,
  437,
  'تضييق الطريق',
  'Road narrowing',
  'Rijbaanversmalling rechts',
  'Rétrécissement de chaussée',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1458,
  437,
  'شارع الدراجات',
  'Cycle street',
  'Fietsstraat.',
  'Rue cyclable',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  438,
  'إلى أي فئة تنتمي العلامة F111؟',
  'Which category does sign F111 belong to?',
  'Tot welke categorie behoort bord F111?',
  'À quelle catégorie appartient le panneau F111?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F111' LIMIT 1),
  'العلامة F111 تنتمي إلى فئة علامات إرشادية',
  'Sign F111 belongs to Information Signs',
  'Bord F111 behoort tot Informatieborden',
  'Le panneau F111 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1459,
  438,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1460,
  438,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1461,
  438,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1462,
  438,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  439,
  'العلامة F111 تعني: شارع الدراجات. صحيح أم خطأ؟',
  'Sign F111 means: Cycle street. True or False?',
  'Bord F111 betekent: Fietsstraat.. Waar of Onwaar?',
  'Le panneau F111 signifie: Rue cyclable. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F111' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1463,
  439,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1464,
  439,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  440,
  'ما هي العلامة المرورية F113؟',
  'What does the traffic sign F113 mean?',
  'Wat betekent verkeersbord F113?',
  'Que signifie le panneau de signalisation F113?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F113' LIMIT 1),
  'العلامة F113 تعني: شارع الدراجات',
  'Sign F113 means: Cycle street',
  'Bord F113 betekent: Einde fietsstraat.',
  'Le panneau F113 signifie: Rue cyclable',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1465,
  440,
  'شارع الدراجات',
  'Cycle street',
  'Einde fietsstraat.',
  'Rue cyclable',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1466,
  440,
  'علامة E9g',
  'Verplicht parkeren op de rijbaan.',
  'Verplicht parkeren op de rijbaan.',
  'Verplicht parkeren op de rijbaan.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1467,
  440,
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Uitgezonderd fietsers en bromfietsers.',
  'Cyclistes et cyclomotoristes',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1468,
  440,
  'علامة E9f',
  'Verplicht parkeren deels op de berm of op het trottoir.',
  'Verplicht parkeren deels op de berm of op het trottoir.',
  'Verplicht parkeren deels op de berm of op het trottoir.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  441,
  'العلامة F113 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F113 means: This sign is optional. True or False?',
  'Bord F113 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F113 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F113' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1469,
  441,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1470,
  441,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  442,
  'ما هي العلامة المرورية F117؟',
  'What does the traffic sign F117 mean?',
  'Wat betekent verkeersbord F117?',
  'Que signifie le panneau de signalisation F117?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F117' LIMIT 1),
  'العلامة F117 تعني: علامة F117',
  'Sign F117 means: Begin van een lage emissiezone',
  'Bord F117 betekent: Begin van een lage emissiezone',
  'Le panneau F117 signifie: Begin van een lage emissiezone',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1471,
  442,
  'علامة D1d',
  'Verplichting rechts aanhouden.',
  'Verplichting rechts aanhouden.',
  'Verplichting rechts aanhouden.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1472,
  442,
  'علامة F117',
  'Begin van een lage emissiezone',
  'Begin van een lage emissiezone',
  'Begin van een lage emissiezone',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1473,
  442,
  'علامة D3a',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1474,
  442,
  'علامة F47',
  'Einde van de werken.',
  'Einde van de werken.',
  'Einde van de werken.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  443,
  'العلامة F117 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F117 means: This sign is optional. True or False?',
  'Bord F117 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F117 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F117' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1475,
  443,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1476,
  443,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  444,
  'إلى أي فئة تنتمي العلامة F117؟',
  'Which category does sign F117 belong to?',
  'Tot welke categorie behoort bord F117?',
  'À quelle catégorie appartient le panneau F117?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F117' LIMIT 1),
  'العلامة F117 تنتمي إلى فئة علامات إرشادية',
  'Sign F117 belongs to Information Signs',
  'Bord F117 behoort tot Informatieborden',
  'Le panneau F117 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1477,
  444,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1478,
  444,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1479,
  444,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1480,
  444,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  445,
  'العلامة F118 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign F118 means: This sign is optional. True or False?',
  'Bord F118 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau F118 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F118' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1481,
  445,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1482,
  445,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  446,
  'إلى أي فئة تنتمي العلامة F118؟',
  'Which category does sign F118 belong to?',
  'Tot welke categorie behoort bord F118?',
  'À quelle catégorie appartient le panneau F118?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F118' LIMIT 1),
  'العلامة F118 تنتمي إلى فئة علامات إرشادية',
  'Sign F118 belongs to Information Signs',
  'Bord F118 behoort tot Informatieborden',
  'Le panneau F118 appartient à Panneaux d''indication',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1483,
  446,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1484,
  446,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1485,
  446,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1486,
  446,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  447,
  'ما هي العلامة المرورية F118؟',
  'What does the traffic sign F118 mean?',
  'Wat betekent verkeersbord F118?',
  'Que signifie le panneau de signalisation F118?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'F'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'F118' LIMIT 1),
  'العلامة F118 تعني: علامة F118',
  'Sign F118 means: Einde van een lage emissiezone',
  'Bord F118 betekent: Einde van een lage emissiezone',
  'Le panneau F118 signifie: Einde van een lage emissiezone',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1487,
  447,
  'علامة F118',
  'Einde van een lage emissiezone',
  'Einde van een lage emissiezone',
  'Einde van een lage emissiezone',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1488,
  447,
  'تضييق الطريق',
  'Road narrowing',
  'Rijbaanversmalling links',
  'Rétrécissement de chaussée',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1489,
  447,
  'أعط الأولوية',
  'Give way',
  'Voorrang verlenen',
  'Cédez le passage',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1490,
  447,
  'منطقة سكنية',
  'Residential zone',
  'Begin van een woonerf of van een erf.',
  'Zone résidentielle',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  448,
  'ما هي العلامة المرورية M1؟',
  'What does the traffic sign M1 mean?',
  'Wat betekent verkeersbord M1?',
  'Que signifie le panneau de signalisation M1?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M1' LIMIT 1),
  'العلامة M1 تعني: علامة M1',
  'Sign M1 means: Enkel voor fietsers.',
  'Bord M1 betekent: Enkel voor fietsers.',
  'Le panneau M1 signifie: Enkel voor fietsers.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1491,
  448,
  'علامة E9b',
  'Parkeren uitsluitend voor auto''s.',
  'Parkeren uitsluitend voor auto''s.',
  'Parkeren uitsluitend voor auto''s.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1492,
  448,
  'علامة M1',
  'Enkel voor fietsers.',
  'Enkel voor fietsers.',
  'Enkel voor fietsers.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1493,
  448,
  'منعطفات خطرة، الأول لليمين',
  'Dangerous double or multiple bends, first to the right',
  'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts.',
  'Virages dangereux, le premier à droite',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1494,
  448,
  'منطقة 30',
  'Zone 30',
  'Einde zone 30 km/u.',
  'Zone 30',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  449,
  'العلامة M1 تعني: علامة M1. صحيح أم خطأ؟',
  'Sign M1 means: Enkel voor fietsers.. True or False?',
  'Bord M1 betekent: Enkel voor fietsers.. Waar of Onwaar?',
  'Le panneau M1 signifie: Enkel voor fietsers.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M1' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1495,
  449,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1496,
  449,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  450,
  'إلى أي فئة تنتمي العلامة M1؟',
  'Which category does sign M1 belong to?',
  'Tot welke categorie behoort bord M1?',
  'À quelle catégorie appartient le panneau M1?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M1' LIMIT 1),
  'العلامة M1 تنتمي إلى فئة لوحات الدراجات',
  'Sign M1 belongs to Bicycle Signs',
  'Bord M1 behoort tot Fietsborden',
  'Le panneau M1 appartient à Panneaux vélos',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1497,
  450,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1498,
  450,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1499,
  450,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1500,
  450,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  451,
  'ما هي العلامة المرورية M2؟',
  'What does the traffic sign M2 mean?',
  'Wat betekent verkeersbord M2?',
  'Que signifie le panneau de signalisation M2?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M2' LIMIT 1),
  'العلامة M2 تعني: علامة M2',
  'Sign M2 means: Uitgezonderd fietsers.',
  'Bord M2 betekent: Uitgezonderd fietsers.',
  'Le panneau M2 signifie: Uitgezonderd fietsers.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1501,
  451,
  'علامة F59b',
  'Aankondiging van een fietsparking.',
  'Aankondiging van een fietsparking.',
  'Aankondiging van een fietsparking.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1502,
  451,
  'علامة C43',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  'Verbod te rijden met een grotere snelheid dan is aangeduid.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1503,
  451,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen bestemd of gebruikt voor het vervoer van zaken.',
  'Accès interdit',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1504,
  451,
  'علامة M2',
  'Uitgezonderd fietsers.',
  'Uitgezonderd fietsers.',
  'Uitgezonderd fietsers.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  452,
  'إلى أي فئة تنتمي العلامة M2؟',
  'Which category does sign M2 belong to?',
  'Tot welke categorie behoort bord M2?',
  'À quelle catégorie appartient le panneau M2?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M2' LIMIT 1),
  'العلامة M2 تنتمي إلى فئة لوحات الدراجات',
  'Sign M2 belongs to Bicycle Signs',
  'Bord M2 behoort tot Fietsborden',
  'Le panneau M2 appartient à Panneaux vélos',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1505,
  452,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1506,
  452,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1507,
  452,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1508,
  452,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  453,
  'العلامة M2 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign M2 means: This sign is optional. True or False?',
  'Bord M2 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau M2 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M2' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1509,
  453,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1510,
  453,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  454,
  'ما هي العلامة المرورية M3؟',
  'What does the traffic sign M3 mean?',
  'Wat betekent verkeersbord M3?',
  'Que signifie le panneau de signalisation M3?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M3' LIMIT 1),
  'العلامة M3 تعني: دراجات ودراجات نارية',
  'Sign M3 means: Cyclists and moped riders',
  'Bord M3 betekent: Uitgezonderd fietsers en bromfietsers klasse A.',
  'Le panneau M3 signifie: Cyclistes et cyclomotoristes',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1511,
  454,
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Uitgezonderd fietsers en bromfietsers klasse A.',
  'Cyclistes et cyclomotoristes',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1512,
  454,
  'قف',
  'Stop',
  'Tolpost. Verbod voorbij te rijden zonder te stoppen.',
  'Stop',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1513,
  454,
  'علامة D13',
  'Verplichte weg voor ruiters.',
  'Verplichte weg voor ruiters.',
  'Verplichte weg voor ruiters.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1514,
  454,
  'علامة F59a',
  'Aankondiging van een parking.',
  'Aankondiging van een parking.',
  'Aankondiging van een parking.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  455,
  'إلى أي فئة تنتمي العلامة M3؟',
  'Which category does sign M3 belong to?',
  'Tot welke categorie behoort bord M3?',
  'À quelle catégorie appartient le panneau M3?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M3' LIMIT 1),
  'العلامة M3 تنتمي إلى فئة لوحات الدراجات',
  'Sign M3 belongs to Bicycle Signs',
  'Bord M3 behoort tot Fietsborden',
  'Le panneau M3 appartient à Panneaux vélos',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1515,
  455,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1516,
  455,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1517,
  455,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1518,
  455,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  456,
  'العلامة M3b تعني: دراجات ودراجات نارية. صحيح أم خطأ؟',
  'Sign M3b means: Cyclists and moped riders. True or False?',
  'Bord M3b betekent: Uitgezonderd fietsers en bromfietsers.. Waar of Onwaar?',
  'Le panneau M3b signifie: Cyclistes et cyclomotoristes. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M3b' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1519,
  456,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1520,
  456,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  457,
  'ما هي العلامة المرورية M3b؟',
  'What does the traffic sign M3b mean?',
  'Wat betekent verkeersbord M3b?',
  'Que signifie le panneau de signalisation M3b?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M3b' LIMIT 1),
  'العلامة M3b تعني: دراجات ودراجات نارية',
  'Sign M3b means: Cyclists and moped riders',
  'Bord M3b betekent: Uitgezonderd fietsers en bromfietsers.',
  'Le panneau M3b signifie: Cyclistes et cyclomotoristes',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1521,
  457,
  'علامة D3a',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1522,
  457,
  'علامة D1e',
  'Verplicht de aangeduide richting te volgen (linksaf)',
  'Verplicht de aangeduide richting te volgen (linksaf)',
  'Verplicht de aangeduide richting te volgen (linksaf)',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1523,
  457,
  'منعطف خطر لليمين',
  'Dangerous bend to the right',
  'Gevaarlijke bocht naar rechts.',
  'Virage dangereux à droite',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1524,
  457,
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Uitgezonderd fietsers en bromfietsers.',
  'Cyclistes et cyclomotoristes',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  458,
  'إلى أي فئة تنتمي العلامة M4؟',
  'Which category does sign M4 belong to?',
  'Tot welke categorie behoort bord M4?',
  'À quelle catégorie appartient le panneau M4?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M4' LIMIT 1),
  'العلامة M4 تنتمي إلى فئة لوحات الدراجات',
  'Sign M4 belongs to Bicycle Signs',
  'Bord M4 behoort tot Fietsborden',
  'Le panneau M4 appartient à Panneaux vélos',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1525,
  458,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1526,
  458,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1527,
  458,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1528,
  458,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  459,
  'ما هي العلامة المرورية M4؟',
  'What does the traffic sign M4 mean?',
  'Wat betekent verkeersbord M4?',
  'Que signifie le panneau de signalisation M4?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M4' LIMIT 1),
  'العلامة M4 تعني: علامة M4',
  'Sign M4 means: Fietsers mogen in 2 richtingen.',
  'Bord M4 betekent: Fietsers mogen in 2 richtingen.',
  'Le panneau M4 signifie: Fietsers mogen in 2 richtingen.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1529,
  459,
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Oversteekplaats voor fietsers en bromfietsers.',
  'Cyclistes et cyclomotoristes',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1530,
  459,
  'علامة D9a',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1531,
  459,
  'منطقة 30',
  'Zone 30',
  'Einde zone 30 km/u.',
  'Zone 30',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1532,
  459,
  'علامة M4',
  'Fietsers mogen in 2 richtingen.',
  'Fietsers mogen in 2 richtingen.',
  'Fietsers mogen in 2 richtingen.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  460,
  'ما هي العلامة المرورية M5؟',
  'What does the traffic sign M5 mean?',
  'Wat betekent verkeersbord M5?',
  'Que signifie le panneau de signalisation M5?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M5' LIMIT 1),
  'العلامة M5 تعني: دراجات ودراجات نارية',
  'Sign M5 means: Cyclists and moped riders',
  'Bord M5 betekent: Fietsers en bromfietsers Klasse A mogen in 2 richtingen.',
  'Le panneau M5 signifie: Cyclistes et cyclomotoristes',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1533,
  460,
  'أطفال',
  'Children',
  'Opgelet kinderen.',
  'Enfants',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1534,
  460,
  'علامة M5b',
  'fietsers, bromfietsers klasse A, B en speed pedelecs mogen in 2 richtingen.',
  'fietsers, bromfietsers klasse A, B en speed pedelecs mogen in 2 richtingen.',
  'fietsers, bromfietsers klasse A, B en speed pedelecs mogen in 2 richtingen.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1535,
  460,
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Fietsers en bromfietsers Klasse A mogen in 2 richtingen.',
  'Cyclistes et cyclomotoristes',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1536,
  460,
  'قف',
  'Stop',
  'Stoppen en voorrang verlenen',
  'Stop',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  461,
  'العلامة M5 تعني: دراجات ودراجات نارية. صحيح أم خطأ؟',
  'Sign M5 means: Cyclists and moped riders. True or False?',
  'Bord M5 betekent: Fietsers en bromfietsers Klasse A mogen in 2 richtingen.. Waar of Onwaar?',
  'Le panneau M5 signifie: Cyclistes et cyclomotoristes. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M5' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1537,
  461,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1538,
  461,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  462,
  'إلى أي فئة تنتمي العلامة M5؟',
  'Which category does sign M5 belong to?',
  'Tot welke categorie behoort bord M5?',
  'À quelle catégorie appartient le panneau M5?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M5' LIMIT 1),
  'العلامة M5 تنتمي إلى فئة لوحات الدراجات',
  'Sign M5 belongs to Bicycle Signs',
  'Bord M5 behoort tot Fietsborden',
  'Le panneau M5 appartient à Panneaux vélos',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1539,
  462,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1540,
  462,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1541,
  462,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1542,
  462,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  463,
  'ما هي العلامة المرورية M5b؟',
  'What does the traffic sign M5b mean?',
  'Wat betekent verkeersbord M5b?',
  'Que signifie le panneau de signalisation M5b?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M5b' LIMIT 1),
  'العلامة M5b تعني: علامة M5b',
  'Sign M5b means: fietsers, bromfietsers klasse A, B en speed pedelecs mogen in 2 richtingen.',
  'Bord M5b betekent: fietsers, bromfietsers klasse A, B en speed pedelecs mogen in 2 richtingen.',
  'Le panneau M5b signifie: fietsers, bromfietsers klasse A, B en speed pedelecs mogen in 2 richtingen.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1543,
  463,
  'علامة A50',
  'Opgelet file',
  'Opgelet file',
  'Opgelet file',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1544,
  463,
  'علامة A17',
  'Kiezelprojectie',
  'Kiezelprojectie',
  'Kiezelprojectie',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1545,
  463,
  'علامة M5b',
  'fietsers, bromfietsers klasse A, B en speed pedelecs mogen in 2 richtingen.',
  'fietsers, bromfietsers klasse A, B en speed pedelecs mogen in 2 richtingen.',
  'fietsers, bromfietsers klasse A, B en speed pedelecs mogen in 2 richtingen.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1546,
  463,
  'علامة C45',
  'Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.',
  'Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.',
  'Einde van de snelheidsbeperking opgelegd door het verkeersbord C43.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  464,
  'إلى أي فئة تنتمي العلامة M5b؟',
  'Which category does sign M5b belong to?',
  'Tot welke categorie behoort bord M5b?',
  'À quelle catégorie appartient le panneau M5b?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M5b' LIMIT 1),
  'العلامة M5b تنتمي إلى فئة لوحات الدراجات',
  'Sign M5b belongs to Bicycle Signs',
  'Bord M5b behoort tot Fietsborden',
  'Le panneau M5b appartient à Panneaux vélos',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1547,
  464,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1548,
  464,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1549,
  464,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1550,
  464,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  465,
  'إلى أي فئة تنتمي العلامة M6؟',
  'Which category does sign M6 belong to?',
  'Tot welke categorie behoort bord M6?',
  'À quelle catégorie appartient le panneau M6?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M6' LIMIT 1),
  'العلامة M6 تنتمي إلى فئة لوحات الدراجات',
  'Sign M6 belongs to Bicycle Signs',
  'Bord M6 behoort tot Fietsborden',
  'Le panneau M6 appartient à Panneaux vélos',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1551,
  465,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1552,
  465,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1553,
  465,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1554,
  465,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  466,
  'ما هي العلامة المرورية M6؟',
  'What does the traffic sign M6 mean?',
  'Wat betekent verkeersbord M6?',
  'Que signifie le panneau de signalisation M6?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M6' LIMIT 1),
  'العلامة M6 تعني: علامة M6',
  'Sign M6 means: Verplichting voor bromfietsen klasse B.',
  'Bord M6 betekent: Verplichting voor bromfietsen klasse B.',
  'Le panneau M6 signifie: Verplichting voor bromfietsen klasse B.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1555,
  466,
  'علامة D10',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1556,
  466,
  'علامة F1a',
  'Begin van een bebouwde kom.',
  'Begin van een bebouwde kom.',
  'Begin van een bebouwde kom.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1557,
  466,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen die gevaarlijke goederen vervoeren.',
  'Accès interdit',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1558,
  466,
  'علامة M6',
  'Verplichting voor bromfietsen klasse B.',
  'Verplichting voor bromfietsen klasse B.',
  'Verplichting voor bromfietsen klasse B.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  467,
  'العلامة M6 تعني: علامة M6. صحيح أم خطأ؟',
  'Sign M6 means: Verplichting voor bromfietsen klasse B.. True or False?',
  'Bord M6 betekent: Verplichting voor bromfietsen klasse B.. Waar of Onwaar?',
  'Le panneau M6 signifie: Verplichting voor bromfietsen klasse B.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M6' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1559,
  467,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1560,
  467,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  468,
  'إلى أي فئة تنتمي العلامة M7؟',
  'Which category does sign M7 belong to?',
  'Tot welke categorie behoort bord M7?',
  'À quelle catégorie appartient le panneau M7?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M7' LIMIT 1),
  'العلامة M7 تنتمي إلى فئة لوحات الدراجات',
  'Sign M7 belongs to Bicycle Signs',
  'Bord M7 behoort tot Fietsborden',
  'Le panneau M7 appartient à Panneaux vélos',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1561,
  468,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1562,
  468,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1563,
  468,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1564,
  468,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  469,
  'ما هي العلامة المرورية M7؟',
  'What does the traffic sign M7 mean?',
  'Wat betekent verkeersbord M7?',
  'Que signifie le panneau de signalisation M7?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M7' LIMIT 1),
  'العلامة M7 تعني: علامة M7',
  'Sign M7 means: Verbod voor bromfietsen klasse B.',
  'Bord M7 betekent: Verbod voor bromfietsen klasse B.',
  'Le panneau M7 signifie: Verbod voor bromfietsen klasse B.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1565,
  469,
  'علامة M6',
  'Verplichting voor bromfietsen klasse B.',
  'Verplichting voor bromfietsen klasse B.',
  'Verplichting voor bromfietsen klasse B.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1566,
  469,
  'علامة D1c',
  'Verplichting links aanhouden.',
  'Verplichting links aanhouden.',
  'Verplichting links aanhouden.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1567,
  469,
  'علامة M7',
  'Verbod voor bromfietsen klasse B.',
  'Verbod voor bromfietsen klasse B.',
  'Verbod voor bromfietsen klasse B.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1568,
  469,
  'علامة D3b',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  'Verplicht één van de pijlen te volgen.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  470,
  'العلامة M7 تعني: علامة M7. صحيح أم خطأ؟',
  'Sign M7 means: Verbod voor bromfietsen klasse B.. True or False?',
  'Bord M7 betekent: Verbod voor bromfietsen klasse B.. Waar of Onwaar?',
  'Le panneau M7 signifie: Verbod voor bromfietsen klasse B.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M7' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1569,
  470,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1570,
  470,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  471,
  'إلى أي فئة تنتمي العلامة M8؟',
  'Which category does sign M8 belong to?',
  'Tot welke categorie behoort bord M8?',
  'À quelle catégorie appartient le panneau M8?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M8' LIMIT 1),
  'العلامة M8 تنتمي إلى فئة لوحات الدراجات',
  'Sign M8 belongs to Bicycle Signs',
  'Bord M8 behoort tot Fietsborden',
  'Le panneau M8 appartient à Panneaux vélos',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1571,
  471,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1572,
  471,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1573,
  471,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1574,
  471,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  472,
  'ما هي العلامة المرورية M8؟',
  'What does the traffic sign M8 mean?',
  'Wat betekent verkeersbord M8?',
  'Que signifie le panneau de signalisation M8?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M8' LIMIT 1),
  'العلامة M8 تعني: دراجات ودراجات نارية',
  'Sign M8 means: Cyclists and moped riders',
  'Bord M8 betekent: Enkel voor fietsers en bromfietsers.',
  'Le panneau M8 signifie: Cyclistes et cyclomotoristes',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1575,
  472,
  'علامة B22',
  'Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
  'Fietsers en speed pedelecs mogen rechtsaf slaan en de verkeerslichten voorbijrijden',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1576,
  472,
  'دراجات ودراجات نارية',
  'Cyclists and moped riders',
  'Enkel voor fietsers en bromfietsers.',
  'Cyclistes et cyclomotoristes',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1577,
  472,
  'منطقة 30',
  'Zone 30',
  'Zone 30 km/u.',
  'Zone 30',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1578,
  472,
  'علامة C25',
  'Verboden voor voertuigen langer dan het aangeduide',
  'Verboden voor voertuigen langer dan het aangeduide',
  'Verboden voor voertuigen langer dan het aangeduide',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  473,
  'العلامة M8 تعني: دراجات ودراجات نارية. صحيح أم خطأ؟',
  'Sign M8 means: Cyclists and moped riders. True or False?',
  'Bord M8 betekent: Enkel voor fietsers en bromfietsers.. Waar of Onwaar?',
  'Le panneau M8 signifie: Cyclistes et cyclomotoristes. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M8' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1579,
  473,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1580,
  473,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  474,
  'العلامة M9 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign M9 means: This sign is optional. True or False?',
  'Bord M9 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau M9 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M9' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1581,
  474,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1582,
  474,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  475,
  'ما هي العلامة المرورية M9؟',
  'What does the traffic sign M9 mean?',
  'Wat betekent verkeersbord M9?',
  'Que signifie le panneau de signalisation M9?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M9' LIMIT 1),
  'العلامة M9 تعني: علامة M9',
  'Sign M9 means: Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Bord M9 betekent: Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Le panneau M9 signifie: Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1583,
  475,
  'علامة D9b',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  'Deel van de weg voorbehouden voor voetgangers en fietsers.',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1584,
  475,
  'علامة F13',
  'Rijstrook keuze.',
  'Rijstrook keuze.',
  'Rijstrook keuze.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1585,
  475,
  'علامة F34c',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1586,
  475,
  'علامة M9',
  'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  476,
  'إلى أي فئة تنتمي العلامة M9؟',
  'Which category does sign M9 belong to?',
  'Tot welke categorie behoort bord M9?',
  'À quelle catégorie appartient le panneau M9?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M9' LIMIT 1),
  'العلامة M9 تنتمي إلى فئة لوحات الدراجات',
  'Sign M9 belongs to Bicycle Signs',
  'Bord M9 behoort tot Fietsborden',
  'Le panneau M9 appartient à Panneaux vélos',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1587,
  476,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1588,
  476,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1589,
  476,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1590,
  476,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  477,
  'إلى أي فئة تنتمي العلامة M10؟',
  'Which category does sign M10 belong to?',
  'Tot welke categorie behoort bord M10?',
  'À quelle catégorie appartient le panneau M10?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M10' LIMIT 1),
  'العلامة M10 تنتمي إلى فئة لوحات الدراجات',
  'Sign M10 belongs to Bicycle Signs',
  'Bord M10 behoort tot Fietsborden',
  'Le panneau M10 appartient à Panneaux vélos',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1591,
  477,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1592,
  477,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1593,
  477,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1594,
  477,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  478,
  'ما هي العلامة المرورية M10؟',
  'What does the traffic sign M10 mean?',
  'Wat betekent verkeersbord M10?',
  'Que signifie le panneau de signalisation M10?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M10' LIMIT 1),
  'العلامة M10 تعني: علامة M10',
  'Sign M10 means: Fietsers en bromfietser in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Bord M10 betekent: Fietsers en bromfietser in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Le panneau M10 signifie: Fietsers en bromfietser in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1595,
  478,
  'علامة M10',
  'Fietsers en bromfietser in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers en bromfietser in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  'Fietsers en bromfietser in twee richtingen op de dwarslopende weg die je gaat oprijden.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1596,
  478,
  'علامة F17',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  'Rijstrook aanduiding voorbehouden voor autobussen.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1597,
  478,
  'ممنوع الانتظار',
  'Parking prohibited',
  'Parkeerverbod.',
  'Interdiction de stationner',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1598,
  478,
  'علامة E9b',
  'Parkeren uitsluitend voor auto''s.',
  'Parkeren uitsluitend voor auto''s.',
  'Parkeren uitsluitend voor auto''s.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  479,
  'العلامة M10 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign M10 means: This sign is optional. True or False?',
  'Bord M10 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau M10 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M10' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1599,
  479,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1600,
  479,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  480,
  'العلامة M11 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign M11 means: This sign is optional. True or False?',
  'Bord M11 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau M11 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M11' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1601,
  480,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1602,
  480,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  481,
  'إلى أي فئة تنتمي العلامة M11؟',
  'Which category does sign M11 belong to?',
  'Tot welke categorie behoort bord M11?',
  'À quelle catégorie appartient le panneau M11?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M11' LIMIT 1),
  'العلامة M11 تنتمي إلى فئة لوحات الدراجات',
  'Sign M11 belongs to Bicycle Signs',
  'Bord M11 behoort tot Fietsborden',
  'Le panneau M11 appartient à Panneaux vélos',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1603,
  481,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1604,
  481,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1605,
  481,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1606,
  481,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  482,
  'ما هي العلامة المرورية M11؟',
  'What does the traffic sign M11 mean?',
  'Wat betekent verkeersbord M11?',
  'Que signifie le panneau de signalisation M11?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M11' LIMIT 1),
  'العلامة M11 تعني: علامة M11',
  'Sign M11 means: Uitgezonderd fietsers en speed pedelecs.',
  'Bord M11 betekent: Uitgezonderd fietsers en speed pedelecs.',
  'Le panneau M11 signifie: Uitgezonderd fietsers en speed pedelecs.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1607,
  482,
  'منعطفات خطرة، الأول لليسار',
  'Dangerous double or multiple bends, first to the left',
  'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar links.',
  'Virages dangereux, le premier à gauche',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1608,
  482,
  'علامة M11',
  'Uitgezonderd fietsers en speed pedelecs.',
  'Uitgezonderd fietsers en speed pedelecs.',
  'Uitgezonderd fietsers en speed pedelecs.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1609,
  482,
  'علامة F53',
  'Verplegingsinrichting.',
  'Verplegingsinrichting.',
  'Verplegingsinrichting.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1610,
  482,
  'علامة A19',
  'Vallende stenen.',
  'Vallende stenen.',
  'Vallende stenen.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  483,
  'إلى أي فئة تنتمي العلامة M12؟',
  'Which category does sign M12 belong to?',
  'Tot welke categorie behoort bord M12?',
  'À quelle catégorie appartient le panneau M12?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M12' LIMIT 1),
  'العلامة M12 تنتمي إلى فئة لوحات الدراجات',
  'Sign M12 belongs to Bicycle Signs',
  'Bord M12 behoort tot Fietsborden',
  'Le panneau M12 appartient à Panneaux vélos',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1611,
  483,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1612,
  483,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1613,
  483,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1614,
  483,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  484,
  'العلامة M12 تعني: علامة M12. صحيح أم خطأ؟',
  'Sign M12 means: Uitgezonderd fietsers, bromfietsers klasse A en speed pedelecs.. True or False?',
  'Bord M12 betekent: Uitgezonderd fietsers, bromfietsers klasse A en speed pedelecs.. Waar of Onwaar?',
  'Le panneau M12 signifie: Uitgezonderd fietsers, bromfietsers klasse A en speed pedelecs.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M12' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1615,
  484,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1616,
  484,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  485,
  'ما هي العلامة المرورية M13؟',
  'What does the traffic sign M13 mean?',
  'Wat betekent verkeersbord M13?',
  'Que signifie le panneau de signalisation M13?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M13' LIMIT 1),
  'العلامة M13 تعني: علامة M13',
  'Sign M13 means: Verplichting voor speed pedelecs.',
  'Bord M13 betekent: Verplichting voor speed pedelecs.',
  'Le panneau M13 signifie: Verplichting voor speed pedelecs.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1617,
  485,
  'جسر متحرك',
  'Movable bridge',
  'Beweegbare brug.',
  'Pont mobile',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1618,
  485,
  'علامة M13',
  'Verplichting voor speed pedelecs.',
  'Verplichting voor speed pedelecs.',
  'Verplichting voor speed pedelecs.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1619,
  485,
  'علامة E9d',
  'Parkeren uitsluitend voor autocars.',
  'Parkeren uitsluitend voor autocars.',
  'Parkeren uitsluitend voor autocars.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1620,
  485,
  'طريق سريع',
  'Motorway',
  'Nummer van een autosnelweg.',
  'Autoroute',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  486,
  'إلى أي فئة تنتمي العلامة M13؟',
  'Which category does sign M13 belong to?',
  'Tot welke categorie behoort bord M13?',
  'À quelle catégorie appartient le panneau M13?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M13' LIMIT 1),
  'العلامة M13 تنتمي إلى فئة لوحات الدراجات',
  'Sign M13 belongs to Bicycle Signs',
  'Bord M13 behoort tot Fietsborden',
  'Le panneau M13 appartient à Panneaux vélos',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1621,
  486,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1622,
  486,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1623,
  486,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1624,
  486,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  487,
  'العلامة M13 تعني: علامة M13. صحيح أم خطأ؟',
  'Sign M13 means: Verplichting voor speed pedelecs.. True or False?',
  'Bord M13 betekent: Verplichting voor speed pedelecs.. Waar of Onwaar?',
  'Le panneau M13 signifie: Verplichting voor speed pedelecs.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M13' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1625,
  487,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1626,
  487,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  488,
  'العلامة M14 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign M14 means: This sign is optional. True or False?',
  'Bord M14 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau M14 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M14' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1627,
  488,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1628,
  488,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  489,
  'ما هي العلامة المرورية M14؟',
  'What does the traffic sign M14 mean?',
  'Wat betekent verkeersbord M14?',
  'Que signifie le panneau de signalisation M14?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M14' LIMIT 1),
  'العلامة M14 تعني: علامة M14',
  'Sign M14 means: Verplichting voor bromfietsen klasse B en Speed pedelecs.',
  'Bord M14 betekent: Verplichting voor bromfietsen klasse B en Speed pedelecs.',
  'Le panneau M14 signifie: Verplichting voor bromfietsen klasse B en Speed pedelecs.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1629,
  489,
  'علامة M14',
  'Verplichting voor bromfietsen klasse B en Speed pedelecs.',
  'Verplichting voor bromfietsen klasse B en Speed pedelecs.',
  'Verplichting voor bromfietsen klasse B en Speed pedelecs.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1630,
  489,
  'علامة F29',
  'Wegwijzer',
  'Wegwijzer',
  'Wegwijzer',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1631,
  489,
  'طريق سيارات',
  'Expressway',
  'Einde van de autoweg.',
  'Route pour automobiles',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1632,
  489,
  'طريق ذو أولوية',
  'Priority road',
  'Voorrangsweg',
  'Route prioritaire',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  490,
  'ما هي العلامة المرورية M15؟',
  'What does the traffic sign M15 mean?',
  'Wat betekent verkeersbord M15?',
  'Que signifie le panneau de signalisation M15?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M15' LIMIT 1),
  'العلامة M15 تعني: علامة M15',
  'Sign M15 means: Verbod voor speed pedelecs.',
  'Bord M15 betekent: Verbod voor speed pedelecs.',
  'Le panneau M15 signifie: Verbod voor speed pedelecs.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1633,
  490,
  'منعطفات خطرة، الأول لليمين',
  'Dangerous double or multiple bends, first to the right',
  'Gevaarlijke dubbele of meer dan twee bochten, de eerste naar rechts.',
  'Virages dangereux, le premier à droite',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1634,
  490,
  'علامة M15',
  'Verbod voor speed pedelecs.',
  'Verbod voor speed pedelecs.',
  'Verbod voor speed pedelecs.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1635,
  490,
  'علامة M20',
  'Enkel voor fietsers en speed pedelecs.',
  'Enkel voor fietsers en speed pedelecs.',
  'Enkel voor fietsers en speed pedelecs.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1636,
  490,
  'شارع الدراجات',
  'Cycle street',
  'Einde fietsstraat.',
  'Rue cyclable',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  491,
  'العلامة M15 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign M15 means: This sign is optional. True or False?',
  'Bord M15 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau M15 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M15' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1637,
  491,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1638,
  491,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  492,
  'العلامة M16 تعني: علامة M16. صحيح أم خطأ؟',
  'Sign M16 means: Verbod voor bromfietsen klasse B en speed pedelecs.. True or False?',
  'Bord M16 betekent: Verbod voor bromfietsen klasse B en speed pedelecs.. Waar of Onwaar?',
  'Le panneau M16 signifie: Verbod voor bromfietsen klasse B en speed pedelecs.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M16' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1639,
  492,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1640,
  492,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  493,
  'إلى أي فئة تنتمي العلامة M16؟',
  'Which category does sign M16 belong to?',
  'Tot welke categorie behoort bord M16?',
  'À quelle catégorie appartient le panneau M16?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M16' LIMIT 1),
  'العلامة M16 تنتمي إلى فئة لوحات الدراجات',
  'Sign M16 belongs to Bicycle Signs',
  'Bord M16 behoort tot Fietsborden',
  'Le panneau M16 appartient à Panneaux vélos',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1641,
  493,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1642,
  493,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1643,
  493,
  'علامات الوقوف',
  'Parking Signs',
  'Parkeerverbod',
  'Panneaux de stationnement',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1644,
  493,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  494,
  'ما هي العلامة المرورية M16؟',
  'What does the traffic sign M16 mean?',
  'Wat betekent verkeersbord M16?',
  'Que signifie le panneau de signalisation M16?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M16' LIMIT 1),
  'العلامة M16 تعني: علامة M16',
  'Sign M16 means: Verbod voor bromfietsen klasse B en speed pedelecs.',
  'Bord M16 betekent: Verbod voor bromfietsen klasse B en speed pedelecs.',
  'Le panneau M16 signifie: Verbod voor bromfietsen klasse B en speed pedelecs.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1645,
  494,
  'علامة F99b',
  'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1646,
  494,
  'علامة A27',
  'Overstekend groot wild.',
  'Overstekend groot wild.',
  'Overstekend groot wild.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1647,
  494,
  'علامة C25',
  'Verboden voor voertuigen langer dan het aangeduide',
  'Verboden voor voertuigen langer dan het aangeduide',
  'Verboden voor voertuigen langer dan het aangeduide',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1648,
  494,
  'علامة M16',
  'Verbod voor bromfietsen klasse B en speed pedelecs.',
  'Verbod voor bromfietsen klasse B en speed pedelecs.',
  'Verbod voor bromfietsen klasse B en speed pedelecs.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  495,
  'ما هي العلامة المرورية M17؟',
  'What does the traffic sign M17 mean?',
  'Wat betekent verkeersbord M17?',
  'Que signifie le panneau de signalisation M17?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M17' LIMIT 1),
  'العلامة M17 تعني: علامة M17',
  'Sign M17 means: Fietsers en speed pedelecs mogen in 2 richtingen.',
  'Bord M17 betekent: Fietsers en speed pedelecs mogen in 2 richtingen.',
  'Le panneau M17 signifie: Fietsers en speed pedelecs mogen in 2 richtingen.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1649,
  495,
  'علامة M17',
  'Fietsers en speed pedelecs mogen in 2 richtingen.',
  'Fietsers en speed pedelecs mogen in 2 richtingen.',
  'Fietsers en speed pedelecs mogen in 2 richtingen.',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1650,
  495,
  'علامة A15',
  'Gladde rijbaan - Slipgevaar.',
  'Gladde rijbaan - Slipgevaar.',
  'Gladde rijbaan - Slipgevaar.',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1651,
  495,
  'علامة F34b',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  'Aanbevolen reisweg voor bepaalde weggebruikers.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1652,
  495,
  'ممنوع الانتظار',
  'Parking prohibited',
  'Parkeerverbod van de 16e tot het einde van de maand.',
  'Interdiction de stationner',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  496,
  'العلامة M17 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign M17 means: This sign is optional. True or False?',
  'Bord M17 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau M17 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M17' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1653,
  496,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1654,
  496,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  497,
  'إلى أي فئة تنتمي العلامة M18؟',
  'Which category does sign M18 belong to?',
  'Tot welke categorie behoort bord M18?',
  'À quelle catégorie appartient le panneau M18?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M18' LIMIT 1),
  'العلامة M18 تنتمي إلى فئة لوحات الدراجات',
  'Sign M18 belongs to Bicycle Signs',
  'Bord M18 behoort tot Fietsborden',
  'Le panneau M18 appartient à Panneaux vélos',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1655,
  497,
  'علامات الخطر',
  'Danger Signs',
  'Gevaarborden',
  'Panneaux de danger',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1656,
  497,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1657,
  497,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1658,
  497,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  498,
  'العلامة M18 تعني: علامة M18. صحيح أم خطأ؟',
  'Sign M18 means: Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.. True or False?',
  'Bord M18 betekent: Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.. Waar of Onwaar?',
  'Le panneau M18 signifie: Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M18' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1659,
  498,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  TRUE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1660,
  498,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  FALSE,
  1,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  499,
  'ما هي العلامة المرورية M18؟',
  'What does the traffic sign M18 mean?',
  'Wat betekent verkeersbord M18?',
  'Que signifie le panneau de signalisation M18?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M18' LIMIT 1),
  'العلامة M18 تعني: علامة M18',
  'Sign M18 means: Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  'Bord M18 betekent: Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  'Le panneau M18 signifie: Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1661,
  499,
  'منعطف خطر لليمين',
  'Dangerous bend to the right',
  'Gevaarlijke bocht naar rechts.',
  'Virage dangereux à droite',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1662,
  499,
  'نفق',
  'Tunnel',
  'Tunnel.',
  'Tunnel',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1663,
  499,
  'علامة M18',
  'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  'Fietsers, bromfietsen klasse A en speed pedelecs mogen in 2 richtingen.',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1664,
  499,
  'ممنوع الدخول',
  'No entry',
  'Verboden toegang voor bestuurders van voertuigen bestemd of gebruikt voor het vervoer van zaken.',
  'Accès interdit',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  500,
  'ما هي العلامة المرورية M19؟',
  'What does the traffic sign M19 mean?',
  'Wat betekent verkeersbord M19?',
  'Que signifie le panneau de signalisation M19?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M19' LIMIT 1),
  'العلامة M19 تعني: علامة M19',
  'Sign M19 means: Enkel voor speed pedelecs.',
  'Bord M19 betekent: Enkel voor speed pedelecs.',
  'Le panneau M19 signifie: Enkel voor speed pedelecs.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1665,
  500,
  'طريق ذو أولوية',
  'Priority road',
  'Einde voorrangsweg',
  'Route prioritaire',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1666,
  500,
  'علامة M19',
  'Enkel voor speed pedelecs.',
  'Enkel voor speed pedelecs.',
  'Enkel voor speed pedelecs.',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1667,
  500,
  'علامة D13',
  'Verplichte weg voor ruiters.',
  'Verplichte weg voor ruiters.',
  'Verplichte weg voor ruiters.',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1668,
  500,
  'علامة A41',
  'Overweg met slagbomen.',
  'Overweg met slagbomen.',
  'Overweg met slagbomen.',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  501,
  'إلى أي فئة تنتمي العلامة M19؟',
  'Which category does sign M19 belong to?',
  'Tot welke categorie behoort bord M19?',
  'À quelle catégorie appartient le panneau M19?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M19' LIMIT 1),
  'العلامة M19 تنتمي إلى فئة لوحات الدراجات',
  'Sign M19 belongs to Bicycle Signs',
  'Bord M19 behoort tot Fietsborden',
  'Le panneau M19 appartient à Panneaux vélos',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1669,
  501,
  'علامات الإلزام',
  'Mandatory Signs',
  'Gebodsborden',
  'Panneaux d''obligation',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1670,
  501,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1671,
  501,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  TRUE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1672,
  501,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  502,
  'إلى أي فئة تنتمي العلامة M20؟',
  'Which category does sign M20 belong to?',
  'Tot welke categorie behoort bord M20?',
  'À quelle catégorie appartient le panneau M20?',
  'MULTIPLE_CHOICE',
  'MEDIUM',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M20' LIMIT 1),
  'العلامة M20 تنتمي إلى فئة لوحات الدراجات',
  'Sign M20 belongs to Bicycle Signs',
  'Bord M20 behoort tot Fietsborden',
  'Le panneau M20 appartient à Panneaux vélos',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1673,
  502,
  'علامات المنع',
  'Prohibition Signs',
  'Verbodsborden',
  'Panneaux d''interdiction',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1674,
  502,
  'لوحات الدراجات',
  'Bicycle Signs',
  'Fietsborden',
  'Panneaux vélos',
  TRUE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1675,
  502,
  'علامات إرشادية',
  'Information Signs',
  'Informatieborden',
  'Panneaux d''indication',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1676,
  502,
  'علامات الأولوية',
  'Priority Signs',
  'Voorrangsborden',
  'Panneaux de priorité',
  FALSE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  503,
  'ما هي العلامة المرورية M20؟',
  'What does the traffic sign M20 mean?',
  'Wat betekent verkeersbord M20?',
  'Que signifie le panneau de signalisation M20?',
  'MULTIPLE_CHOICE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M20' LIMIT 1),
  'العلامة M20 تعني: علامة M20',
  'Sign M20 means: Enkel voor fietsers en speed pedelecs.',
  'Bord M20 betekent: Enkel voor fietsers en speed pedelecs.',
  'Le panneau M20 signifie: Enkel voor fietsers en speed pedelecs.',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1677,
  503,
  'علامة F99b',
  'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  'Deel van de openbare weg voorbehouden voor fietsers en voetgangers',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1678,
  503,
  'تضييق الطريق',
  'Road narrowing',
  'Rijbaanversmalling links',
  'Rétrécissement de chaussée',
  FALSE,
  1,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1679,
  503,
  'طريق ذو أولوية',
  'Priority road',
  'Einde voorrangsweg',
  'Route prioritaire',
  FALSE,
  2,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1680,
  503,
  'علامة M20',
  'Enkel voor fietsers en speed pedelecs.',
  'Enkel voor fietsers en speed pedelecs.',
  'Enkel voor fietsers en speed pedelecs.',
  TRUE,
  3,
  NOW()
);


INSERT INTO quiz_questions (
  id, question_ar, question_en, question_nl, question_fr,
  question_type, difficulty_level, category_id, traffic_sign_id,
  explanation_ar, explanation_en, explanation_nl, explanation_fr,
  is_active, created_at, updated_at
) VALUES (
  504,
  'العلامة M20 تعني: هذه العلامة اختيارية. صحيح أم خطأ؟',
  'Sign M20 means: This sign is optional. True or False?',
  'Bord M20 betekent: Dit bord is optioneel. Waar of Onwaar?',
  'Le panneau M20 signifie: Ce panneau est optionnel. Vrai ou Faux?',
  'TRUE_FALSE',
  'EASY',
  (SELECT id FROM categories WHERE code = 'M'),
  (SELECT id FROM traffic_signs WHERE sign_code = 'M20' LIMIT 1),
  '',
  '',
  '',
  '',
  TRUE,
  NOW(),
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1681,
  504,
  'صحيح',
  'True',
  'Waar',
  'Vrai',
  FALSE,
  0,
  NOW()
);

INSERT INTO quiz_answer_options (
  id, question_id,
  option_text_ar, option_text_en, option_text_nl, option_text_fr,
  is_correct, display_order, created_at
) VALUES (
  1682,
  504,
  'خطأ',
  'False',
  'Onwaar',
  'Faux',
  TRUE,
  1,
  NOW()
);

