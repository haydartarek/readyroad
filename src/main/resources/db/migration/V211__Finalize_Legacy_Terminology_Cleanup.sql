-- Final learner-facing terminology cleanup after V210.
-- This migration targets the remaining persisted remnants in questions,
-- answers, sign descriptions, and rule content without touching history.

SET SESSION group_concat_max_len = 1024 * 1024 * 16;

DROP TEMPORARY TABLE IF EXISTS tmp_v211_phrase_replacements;

CREATE TEMPORARY TABLE tmp_v211_phrase_replacements (
    language_code VARCHAR(2) NOT NULL,
    search_text TEXT NOT NULL,
    replacement_text TEXT NOT NULL
) ENGINE=InnoDB;

INSERT INTO tmp_v211_phrase_replacements (language_code, search_text, replacement_text) VALUES
    ('AR', 'علامات الأولوية (السلسلة ب)', 'علامات الأولوية'),
    ('AR', 'علامات الحظر (السلسلة ج)', 'علامات الحظر'),
    ('AR', 'علامات الخطر (السلسلة أ)', 'علامات الخطر'),
    ('AR', 'هل أنت ملزم قانونياً بتقليل سرعتك قبل دخول المنعطف الأول من السلسلة؟', 'هل أنت ملزم قانونياً بتقليل سرعتك قبل دخول المنعطف الأول من المنعطفات المتتالية؟'),
    ('AR', 'هل يُسمح بالتجاوز في منطقة أشغال عندما لا توجد لافتة حظر تجاوز مؤقتة والطريق مقلّص إلى مسار واحد؟', 'هل يُسمح بالتجاوز في منطقة أشغال عندما لا توجد علامة حظر تجاوز مؤقتة والطريق مقلّص إلى مسار واحد؟'),
    ('AR', 'ماذا يجب أن تفعل عند رؤية لافتة تضيق الطريق هذه؟', 'ماذا يجب عليك فعله عند رؤية هذه العلامة المرورية التي تشير إلى تضيق الطريق؟'),
    ('AR', 'هل أنت ملزم قانونياً بتقليل سرعتك عند رؤية لافتة تضيق الطريق هذه؟', 'هل يجب عليك قانونياً تخفيف سرعتك عند رؤية هذه العلامة المرورية التي تشير إلى تضيق الطريق؟'),
    ('AR', 'ماذا يجب أن تفعل عند رؤية لافتة التضيق من اليسار هذه؟', 'ماذا يجب عليك فعله عند رؤية هذه العلامة المرورية التي تشير إلى تضيق الطريق من اليسار؟'),
    ('AR', 'هل أنت ملزم قانونياً بتعديل سلوك قيادتك عند رؤية لافتة تضيق الطريق من اليمين هذه؟', 'هل يجب عليك قانونياً تعديل سلوك قيادتك عند رؤية هذه العلامة المرورية التي تشير إلى تضيق الطريق من اليمين؟'),
    ('AR', 'ماذا تعني لافتة الممر الضيق هذه للمركبات المقابلة؟', 'ماذا تعني هذه العلامة المرورية الخاصة بالممر الضيق للمركبات المقابلة؟'),
    ('AR', 'ما معنى الرمز الموجود على اللافتة؟', 'ما معنى الرمز الموجود على هذه العلامة المرورية؟'),
    ('AR', 'ترى العلامة المرورية: وجود إشارة ضوئية. كيف تعدّل سلوكك القيادي؟', 'ترى العلامة المرورية التي تشير إلى وجود إشارة ضوئية. كيف تعدّل سلوكك أثناء القيادة؟'),
    ('AR', 'ZC43 تنتمي إلى سلسلة المنطقة. تسري سرعتها على جميع طرق المنطقة.', 'ZC43 تنتمي إلى علامات المنطقة. تسري سرعتها على جميع طرق المنطقة.'),
    ('AR', 'قيد السرعة في منطقة ZC43 مطلق. الطريق الخالي وجودة الرؤية ليستا استثناءًا من الحد القانوني.', 'قيد السرعة في منطقة ZC43 مطلق. الطريق الخالي وجودة الرؤية ليستا استثناءً من الحد القانوني.'),
    ('AR', 'عند تقاطع مع أربعة اتصالات: • ينظر كل سائق إلى اليمين • يتم إنشاء نظام السلسلة', 'عند تقاطع مع أربعة اتصالات: • ينظر كل سائق إلى اليمين • ينشأ نظام التعاقب'),
    ('EN', 'Which series does sign the traffic sign', 'To which category does the traffic sign'),
    ('EN', 'Which series does the traffic sign', 'To which category does the traffic sign'),
    ('EN', 'To which series does the traffic sign', 'To which category does the traffic sign'),
    ('EN', 'Yes: stopping is never regulated by E-series signs', 'Yes: stopping is never regulated by parking and stopping signs'),
    ('EN', 'because it is a C-series sign', 'because it is a prohibition sign'),
    ('EN', 'belongs to the zone series', 'belongs to the zone signs'),
    ('EN', 'entire series of bends', 'entire sequence of bends'),
    ('EN', 'series of bends', 'sequence of bends'),
    ('NL', 'Tot welke reeks hoort', 'Tot welke categorie behoort'),
    ('NL', 'Tot welke reeks behoort', 'Tot welke categorie behoort'),
    ('NL', 'eerste bocht van de reeks', 'eerste van de opeenvolgende bochten'),
    ('NL', 'de volledige reeks', 'alle opeenvolgende bochten'),
    ('NL', 'voor de volledige reeks', 'over alle opeenvolgende bochten'),
    ('NL', 'reeks gevaarlijke bochten', 'opeenvolgende gevaarlijke bochten'),
    ('NL', 'C-reeksbord', 'verbodsbord'),
    ('NL', 'G-reeks', 'aanvullende en informatieve borden'),
    ('FR', 'Serie F (panneaux d information):', 'Panneaux d''information :'),
    ('FR', 'Serie E (panneaux de stationnement et d arret):', 'Panneaux de stationnement et d''arrêt :'),
    ('FR', 'serie de virages', 'succession de virages'),
    ('FR', 'série de virages', 'succession de virages'),
    ('FR', 'premier virage de la série', 'premier de la succession de virages'),
    ('FR', 'sur toute la série', 'sur l''ensemble des virages'),
    ('FR', 'pour toute la série', 'pour l''ensemble des virages'),
    ('FR', 'parce que c''est un panneau de la série C', 'parce que c''est un panneau d''interdiction'),
    ('FR', 'appartient a la serie G (panneaux additionnels et informatifs)', 'appartient aux panneaux additionnels et informatifs'),
    ('FR', 'appartient à la série G (panneaux additionnels et informatifs)', 'appartient aux panneaux additionnels et informatifs');

SET @expr_ar = (
    SELECT CONCAT(
        REPEAT('REPLACE(', COUNT(*)),
        '__COLUMN__',
        GROUP_CONCAT(
            CONCAT(', ''', REPLACE(search_text, '''', ''''''), ''', ''', REPLACE(replacement_text, '''', ''''''), ''')')
            ORDER BY CHAR_LENGTH(search_text) DESC
            SEPARATOR ''
        )
    )
    FROM tmp_v211_phrase_replacements
    WHERE language_code = 'AR'
);

SET @expr_en = (
    SELECT CONCAT(
        REPEAT('REPLACE(', COUNT(*)),
        '__COLUMN__',
        GROUP_CONCAT(
            CONCAT(', ''', REPLACE(search_text, '''', ''''''), ''', ''', REPLACE(replacement_text, '''', ''''''), ''')')
            ORDER BY CHAR_LENGTH(search_text) DESC
            SEPARATOR ''
        )
    )
    FROM tmp_v211_phrase_replacements
    WHERE language_code = 'EN'
);

SET @expr_nl = (
    SELECT CONCAT(
        REPEAT('REPLACE(', COUNT(*)),
        '__COLUMN__',
        GROUP_CONCAT(
            CONCAT(', ''', REPLACE(search_text, '''', ''''''), ''', ''', REPLACE(replacement_text, '''', ''''''), ''')')
            ORDER BY CHAR_LENGTH(search_text) DESC
            SEPARATOR ''
        )
    )
    FROM tmp_v211_phrase_replacements
    WHERE language_code = 'NL'
);

SET @expr_fr = (
    SELECT CONCAT(
        REPEAT('REPLACE(', COUNT(*)),
        '__COLUMN__',
        GROUP_CONCAT(
            CONCAT(', ''', REPLACE(search_text, '''', ''''''), ''', ''', REPLACE(replacement_text, '''', ''''''), ''')')
            ORDER BY CHAR_LENGTH(search_text) DESC
            SEPARATOR ''
        )
    )
    FROM tmp_v211_phrase_replacements
    WHERE language_code = 'FR'
);

SET @sql = CONCAT(
    'UPDATE sign_questions SET ',
    'question_ar = ', REPLACE(@expr_ar, '__COLUMN__', 'question_ar'), ', ',
    'question_en = ', REPLACE(@expr_en, '__COLUMN__', 'question_en'), ', ',
    'question_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'question_nl'), ', ',
    'question_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'question_fr'), ', ',
    'explanation_ar = ', REPLACE(@expr_ar, '__COLUMN__', 'explanation_ar'), ', ',
    'explanation_en = ', REPLACE(@expr_en, '__COLUMN__', 'explanation_en'), ', ',
    'explanation_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'explanation_nl'), ', ',
    'explanation_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'explanation_fr')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT(
    'UPDATE sign_choices SET ',
    'text_ar = ', REPLACE(@expr_ar, '__COLUMN__', 'text_ar'), ', ',
    'text_en = ', REPLACE(@expr_en, '__COLUMN__', 'text_en'), ', ',
    'text_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'text_nl'), ', ',
    'text_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'text_fr')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT(
    'UPDATE quiz_questions SET ',
    'question_ar = ', REPLACE(@expr_ar, '__COLUMN__', 'question_ar'), ', ',
    'question_en = ', REPLACE(@expr_en, '__COLUMN__', 'question_en'), ', ',
    'question_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'question_nl'), ', ',
    'question_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'question_fr'), ', ',
    'explanation_ar = ', REPLACE(@expr_ar, '__COLUMN__', 'explanation_ar'), ', ',
    'explanation_en = ', REPLACE(@expr_en, '__COLUMN__', 'explanation_en'), ', ',
    'explanation_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'explanation_nl'), ', ',
    'explanation_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'explanation_fr'), ', ',
    'error_explanation_ar = ', REPLACE(@expr_ar, '__COLUMN__', 'error_explanation_ar'), ', ',
    'error_explanation_en = ', REPLACE(@expr_en, '__COLUMN__', 'error_explanation_en'), ', ',
    'error_explanation_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'error_explanation_nl'), ', ',
    'error_explanation_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'error_explanation_fr')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT(
    'UPDATE quiz_answer_options SET ',
    'option_text_ar = ', REPLACE(@expr_ar, '__COLUMN__', 'option_text_ar'), ', ',
    'option_text_en = ', REPLACE(@expr_en, '__COLUMN__', 'option_text_en'), ', ',
    'option_text_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'option_text_nl'), ', ',
    'option_text_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'option_text_fr')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT(
    'UPDATE road_signs SET ',
    'name_ar = ', REPLACE(@expr_ar, '__COLUMN__', 'name_ar'), ', ',
    'name_en = ', REPLACE(@expr_en, '__COLUMN__', 'name_en'), ', ',
    'name_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'name_nl'), ', ',
    'name_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'name_fr'), ', ',
    'description_ar = ', REPLACE(@expr_ar, '__COLUMN__', 'description_ar'), ', ',
    'description_en = ', REPLACE(@expr_en, '__COLUMN__', 'description_en'), ', ',
    'description_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'description_nl'), ', ',
    'description_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'description_fr')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT(
    'UPDATE traffic_rules SET ',
    'title_ar = ', REPLACE(@expr_ar, '__COLUMN__', 'title_ar'), ', ',
    'title_en = ', REPLACE(@expr_en, '__COLUMN__', 'title_en'), ', ',
    'title_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'title_nl'), ', ',
    'title_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'title_fr'), ', ',
    'content_ar = ', REPLACE(@expr_ar, '__COLUMN__', 'content_ar'), ', ',
    'content_en = ', REPLACE(@expr_en, '__COLUMN__', 'content_en'), ', ',
    'content_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'content_nl'), ', ',
    'content_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'content_fr'), ', ',
    'penalty_info_ar = ', REPLACE(@expr_ar, '__COLUMN__', 'penalty_info_ar'), ', ',
    'penalty_info_en = ', REPLACE(@expr_en, '__COLUMN__', 'penalty_info_en'), ', ',
    'penalty_info_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'penalty_info_nl'), ', ',
    'penalty_info_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'penalty_info_fr')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT(
    'UPDATE lesson_pages SET ',
    'title_ar = ', REPLACE(@expr_ar, '__COLUMN__', 'title_ar'), ', ',
    'title_en = ', REPLACE(@expr_en, '__COLUMN__', 'title_en'), ', ',
    'title_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'title_nl'), ', ',
    'title_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'title_fr'), ', ',
    'content_ar = ', REPLACE(@expr_ar, '__COLUMN__', 'content_ar'), ', ',
    'content_en = ', REPLACE(@expr_en, '__COLUMN__', 'content_en'), ', ',
    'content_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'content_nl'), ', ',
    'content_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'content_fr'), ', ',
    'bullet_points_ar = ', REPLACE(@expr_ar, '__COLUMN__', 'bullet_points_ar'), ', ',
    'bullet_points_en = ', REPLACE(@expr_en, '__COLUMN__', 'bullet_points_en'), ', ',
    'bullet_points_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'bullet_points_nl'), ', ',
    'bullet_points_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'bullet_points_fr')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE sign_questions
SET
    question_ar = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
        REGEXP_REPLACE(question_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', ''),
        'الالعلامة المروريةية', 'العلامة المرورية'),
        'العلامة المروريةية', 'العلامة المرورية'),
        'اللافتة العلامة المرورية:', 'العلامة المرورية:'),
        'لافتة العلامة المرورية:', 'العلامة المرورية:'),
        'لافتات', 'علامات'),
        'اللافتة', 'العلامة المرورية'),
        'لافتة', 'علامة مرورية'),
        'السلسلة', 'المنعطفات المتتالية'),
        'علامة مرورية حظر', 'علامة حظر'),
        'خالم', 'خالٍ'),
    explanation_ar = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
        REGEXP_REPLACE(explanation_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', ''),
        'الالعلامة المروريةية', 'العلامة المرورية'),
        'العلامة المروريةية', 'العلامة المرورية'),
        'اللافتة العلامة المرورية:', 'العلامة المرورية:'),
        'لافتة العلامة المرورية:', 'العلامة المرورية:'),
        'لافتات', 'علامات'),
        'اللافتة', 'العلامة المرورية'),
        'لافتة', 'علامة مرورية'),
        'السلسلة', 'المنعطفات المتتالية'),
        'علامة مرورية حظر', 'علامة حظر'),
        'خالم', 'خالٍ')
WHERE question_ar LIKE '%لافتة%' OR question_ar LIKE '%لافتات%' OR question_ar LIKE '%السلسلة%'
   OR explanation_ar LIKE '%لافتة%' OR explanation_ar LIKE '%لافتات%' OR explanation_ar LIKE '%السلسلة%';

UPDATE sign_choices
SET
    text_ar = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
        REGEXP_REPLACE(text_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', ''),
        'الالعلامة المروريةية', 'العلامة المرورية'),
        'العلامة المروريةية', 'العلامة المرورية'),
        'اللافتة العلامة المرورية:', 'العلامة المرورية:'),
        'لافتة العلامة المرورية:', 'العلامة المرورية:'),
        'لافتات', 'علامات'),
        'اللافتة', 'العلامة المرورية'),
        'لافتة', 'علامة مرورية'),
        'السلسلة', 'المنعطفات المتتالية'),
        'علامة مرورية حظر', 'علامة حظر'),
        'خالم', 'خالٍ')
WHERE text_ar LIKE '%لافتة%' OR text_ar LIKE '%لافتات%' OR text_ar LIKE '%السلسلة%';

UPDATE quiz_questions
SET
    question_ar = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
        REGEXP_REPLACE(question_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', ''),
        'الالعلامة المروريةية', 'العلامة المرورية'),
        'العلامة المروريةية', 'العلامة المرورية'),
        'اللافتة العلامة المرورية:', 'العلامة المرورية:'),
        'لافتة العلامة المرورية:', 'العلامة المرورية:'),
        'لافتات', 'علامات'),
        'اللافتة', 'العلامة المرورية'),
        'لافتة', 'علامة مرورية'),
        'السلسلة', 'المنعطفات المتتالية'),
        'علامة مرورية حظر', 'علامة حظر'),
        'خالم', 'خالٍ'),
    explanation_ar = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
        REGEXP_REPLACE(explanation_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', ''),
        'الالعلامة المروريةية', 'العلامة المرورية'),
        'العلامة المروريةية', 'العلامة المرورية'),
        'اللافتة العلامة المرورية:', 'العلامة المرورية:'),
        'لافتة العلامة المرورية:', 'العلامة المرورية:'),
        'لافتات', 'علامات'),
        'اللافتة', 'العلامة المرورية'),
        'لافتة', 'علامة مرورية'),
        'السلسلة', 'المنعطفات المتتالية'),
        'علامة مرورية حظر', 'علامة حظر'),
        'خالم', 'خالٍ'),
    error_explanation_ar = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
        REGEXP_REPLACE(error_explanation_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', ''),
        'الالعلامة المروريةية', 'العلامة المرورية'),
        'العلامة المروريةية', 'العلامة المرورية'),
        'اللافتة العلامة المرورية:', 'العلامة المرورية:'),
        'لافتة العلامة المرورية:', 'العلامة المرورية:'),
        'لافتات', 'علامات'),
        'اللافتة', 'العلامة المرورية'),
        'لافتة', 'علامة مرورية'),
        'السلسلة', 'المنعطفات المتتالية'),
        'علامة مرورية حظر', 'علامة حظر'),
        'خالم', 'خالٍ')
WHERE question_ar LIKE '%لافتة%' OR question_ar LIKE '%لافتات%' OR question_ar LIKE '%السلسلة%'
   OR explanation_ar LIKE '%لافتة%' OR explanation_ar LIKE '%لافتات%' OR explanation_ar LIKE '%السلسلة%'
   OR error_explanation_ar LIKE '%لافتة%' OR error_explanation_ar LIKE '%لافتات%' OR error_explanation_ar LIKE '%السلسلة%';

UPDATE quiz_answer_options
SET
    option_text_ar = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
        REGEXP_REPLACE(option_text_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', ''),
        'الالعلامة المروريةية', 'العلامة المرورية'),
        'العلامة المروريةية', 'العلامة المرورية'),
        'اللافتة العلامة المرورية:', 'العلامة المرورية:'),
        'لافتة العلامة المرورية:', 'العلامة المرورية:'),
        'لافتات', 'علامات'),
        'اللافتة', 'العلامة المرورية'),
        'لافتة', 'علامة مرورية'),
        'السلسلة', 'المنعطفات المتتالية'),
        'علامة مرورية حظر', 'علامة حظر'),
        'خالم', 'خالٍ')
WHERE option_text_ar LIKE '%لافتة%' OR option_text_ar LIKE '%لافتات%' OR option_text_ar LIKE '%السلسلة%';

UPDATE road_signs
SET
    description_ar = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
        REGEXP_REPLACE(description_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', ''),
        'الالعلامة المروريةية', 'العلامة المرورية'),
        'العلامة المروريةية', 'العلامة المرورية'),
        'اللافتة العلامة المرورية:', 'العلامة المرورية:'),
        'لافتة العلامة المرورية:', 'العلامة المرورية:'),
        'لافتات', 'علامات'),
        'اللافتة', 'العلامة المرورية'),
        'لافتة', 'علامة مرورية'),
        'السلسلة', 'المنعطفات المتتالية'),
        'علامة مرورية حظر', 'علامة حظر'),
        'خالم', 'خالٍ')
WHERE description_ar LIKE '%لافتة%' OR description_ar LIKE '%لافتات%' OR description_ar LIKE '%السلسلة%';

UPDATE traffic_rules
SET
    title_ar = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
        REGEXP_REPLACE(title_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', ''),
        'الالعلامة المروريةية', 'العلامة المرورية'),
        'العلامة المروريةية', 'العلامة المرورية'),
        'الالعلامة المروريةية', 'العلامة المرورية'),
        'لافتات', 'علامات'),
        'اللافتة', 'العلامة المرورية'),
        'لافتة', 'علامة مرورية'),
        'السلسلة', 'المنعطفات المتتالية'),
        'نظام المنعطفات المتتالية', 'نظام التعاقب'),
        'خالم', 'خالٍ'),
        'استثناءًا', 'استثناءً'),
    content_ar = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
        REGEXP_REPLACE(content_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', ''),
        'الالعلامة المروريةية', 'العلامة المرورية'),
        'العلامة المروريةية', 'العلامة المرورية'),
        'الالعلامة المروريةية', 'العلامة المرورية'),
        'لافتات', 'علامات'),
        'اللافتة', 'العلامة المرورية'),
        'لافتة', 'علامة مرورية'),
        'السلسلة', 'المنعطفات المتتالية'),
        'نظام المنعطفات المتتالية', 'نظام التعاقب'),
        'خالم', 'خالٍ'),
        'استثناءًا', 'استثناءً'),
    penalty_info_ar = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
        REGEXP_REPLACE(penalty_info_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', ''),
        'الالعلامة المروريةية', 'العلامة المرورية'),
        'العلامة المروريةية', 'العلامة المرورية'),
        'الالعلامة المروريةية', 'العلامة المرورية'),
        'لافتات', 'علامات'),
        'اللافتة', 'العلامة المرورية'),
        'لافتة', 'علامة مرورية'),
        'السلسلة', 'المنعطفات المتتالية'),
        'نظام المنعطفات المتتالية', 'نظام التعاقب'),
        'خالم', 'خالٍ'),
        'استثناءًا', 'استثناءً')
WHERE title_ar LIKE '%لافتة%' OR title_ar LIKE '%لافتات%' OR title_ar LIKE '%السلسلة%'
   OR content_ar LIKE '%لافتة%' OR content_ar LIKE '%لافتات%' OR content_ar LIKE '%السلسلة%'
   OR penalty_info_ar LIKE '%لافتة%' OR penalty_info_ar LIKE '%لافتات%' OR penalty_info_ar LIKE '%السلسلة%';

UPDATE lesson_pages
SET
    title_ar = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
        REGEXP_REPLACE(title_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', ''),
        'الالعلامة المروريةية', 'العلامة المرورية'),
        'العلامة المروريةية', 'العلامة المرورية'),
        'الالعلامة المروريةية', 'العلامة المرورية'),
        'لافتات', 'علامات'),
        'اللافتة', 'العلامة المرورية'),
        'لافتة', 'علامة مرورية'),
        'السلسلة', 'المنعطفات المتتالية'),
        'نظام المنعطفات المتتالية', 'نظام التعاقب'),
        'خالم', 'خالٍ'),
        'استثناءًا', 'استثناءً'),
    content_ar = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
        REGEXP_REPLACE(content_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', ''),
        'الالعلامة المروريةية', 'العلامة المرورية'),
        'العلامة المروريةية', 'العلامة المرورية'),
        'الالعلامة المروريةية', 'العلامة المرورية'),
        'لافتات', 'علامات'),
        'اللافتة', 'العلامة المرورية'),
        'لافتة', 'علامة مرورية'),
        'السلسلة', 'المنعطفات المتتالية'),
        'نظام المنعطفات المتتالية', 'نظام التعاقب'),
        'خالم', 'خالٍ'),
        'استثناءًا', 'استثناءً'),
    bullet_points_ar = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
        REGEXP_REPLACE(bullet_points_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', ''),
        'الالعلامة المروريةية', 'العلامة المرورية'),
        'العلامة المروريةية', 'العلامة المرورية'),
        'الالعلامة المروريةية', 'العلامة المرورية'),
        'لافتات', 'علامات'),
        'اللافتة', 'العلامة المرورية'),
        'لافتة', 'علامة مرورية'),
        'السلسلة', 'المنعطفات المتتالية'),
        'نظام المنعطفات المتتالية', 'نظام التعاقب'),
        'خالم', 'خالٍ'),
        'استثناءًا', 'استثناءً')
WHERE title_ar LIKE '%لافتة%' OR title_ar LIKE '%لافتات%' OR title_ar LIKE '%السلسلة%'
   OR content_ar LIKE '%لافتة%' OR content_ar LIKE '%لافتات%' OR content_ar LIKE '%السلسلة%'
   OR bullet_points_ar LIKE '%لافتة%' OR bullet_points_ar LIKE '%لافتات%' OR bullet_points_ar LIKE '%السلسلة%';

UPDATE sign_questions
SET
    question_en = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(question_en,
        'Which series does sign the traffic sign', 'To which category does the traffic sign'),
        'Which series does the traffic sign', 'To which category does the traffic sign'),
        'To which series does the traffic sign', 'To which category does the traffic sign'),
        'series of bends', 'sequence of bends'),
        'entire series', 'entire sequence of bends'),
        'E-series signs', 'parking and stopping signs'),
        'zone series', 'zone signs'),
    explanation_en = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(explanation_en,
        'Which series does sign the traffic sign', 'To which category does the traffic sign'),
        'Which series does the traffic sign', 'To which category does the traffic sign'),
        'To which series does the traffic sign', 'To which category does the traffic sign'),
        'series of bends', 'sequence of bends'),
        'entire series', 'entire sequence of bends'),
        'E-series signs', 'parking and stopping signs'),
        'zone series', 'zone signs'),
    question_nl = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(question_nl,
        'Tot welke reeks hoort', 'Tot welke categorie behoort'),
        'Tot welke reeks behoort', 'Tot welke categorie behoort'),
        'reeks gevaarlijke bochten', 'opeenvolgende gevaarlijke bochten'),
        'de eerste bocht van de reeks', 'de eerste van de opeenvolgende bochten'),
        'de volledige reeks', 'alle opeenvolgende bochten'),
        'voor de volledige reeks', 'over alle opeenvolgende bochten'),
        'G-reeks', 'aanvullende en informatieve borden'),
    explanation_nl = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(explanation_nl,
        'Tot welke reeks hoort', 'Tot welke categorie behoort'),
        'Tot welke reeks behoort', 'Tot welke categorie behoort'),
        'reeks gevaarlijke bochten', 'opeenvolgende gevaarlijke bochten'),
        'de eerste bocht van de reeks', 'de eerste van de opeenvolgende bochten'),
        'de volledige reeks', 'alle opeenvolgende bochten'),
        'voor de volledige reeks', 'over alle opeenvolgende bochten'),
        'G-reeks', 'aanvullende en informatieve borden'),
    question_fr = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(question_fr,
        'Serie F (panneaux d information):', 'Panneaux d''information :'),
        'Serie E (panneaux de stationnement et d arret):', 'Panneaux de stationnement et d''arrêt :'),
        'série de virages', 'succession de virages'),
        'serie de virages', 'succession de virages'),
        'premier virage de la série', 'premier de la succession de virages'),
        'sur toute la série', 'sur l''ensemble des virages'),
        'pour toute la série', 'pour l''ensemble des virages'),
        'parce que c''est un panneau de la série C', 'parce que c''est un panneau d''interdiction'),
    explanation_fr = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(explanation_fr,
        'Serie F (panneaux d information):', 'Panneaux d''information :'),
        'Serie E (panneaux de stationnement et d arret):', 'Panneaux de stationnement et d''arrêt :'),
        'série de virages', 'succession de virages'),
        'serie de virages', 'succession de virages'),
        'premier virage de la série', 'premier de la succession de virages'),
        'sur toute la série', 'sur l''ensemble des virages'),
        'pour toute la série', 'pour l''ensemble des virages'),
        'parce que c''est un panneau de la série C', 'parce que c''est un panneau d''interdiction')
WHERE question_en REGEXP '(A|B|C|D|E|F)-series|series [A-F]' OR explanation_en REGEXP '(A|B|C|D|E|F)-series|series [A-F]' OR question_en LIKE '%series%' OR explanation_en LIKE '%series%'
   OR question_nl LIKE '%reeks%' OR explanation_nl LIKE '%reeks%'
   OR question_fr REGEXP 's[ée]rie' OR explanation_fr REGEXP 's[ée]rie';

UPDATE sign_choices
SET
    text_en = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(text_en,
        'Which series does sign the traffic sign', 'To which category does the traffic sign'),
        'Which series does the traffic sign', 'To which category does the traffic sign'),
        'To which series does the traffic sign', 'To which category does the traffic sign'),
        'C-series sign', 'prohibition sign'),
        'E-series signs', 'parking and stopping signs'),
        'series of bends', 'sequence of bends'),
        'zone series', 'zone signs'),
    text_nl = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(text_nl,
        'Tot welke reeks hoort', 'Tot welke categorie behoort'),
        'Tot welke reeks behoort', 'Tot welke categorie behoort'),
        'reeks gevaarlijke bochten', 'opeenvolgende gevaarlijke bochten'),
        'de eerste bocht van de reeks', 'de eerste van de opeenvolgende bochten'),
        'de volledige reeks', 'alle opeenvolgende bochten'),
        'voor de volledige reeks', 'over alle opeenvolgende bochten'),
        'C-reeksbord', 'verbodsbord'),
    text_fr = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(text_fr,
        'Serie F (panneaux d information):', 'Panneaux d''information :'),
        'Serie E (panneaux de stationnement et d arret):', 'Panneaux de stationnement et d''arrêt :'),
        'série de virages', 'succession de virages'),
        'serie de virages', 'succession de virages'),
        'premier virage de la série', 'premier de la succession de virages'),
        'sur toute la série', 'sur l''ensemble des virages'),
        'pour toute la série', 'pour l''ensemble des virages'),
        'parce que c''est un panneau de la série C', 'parce que c''est un panneau d''interdiction')
WHERE text_en REGEXP '(A|B|C|D|E|F)-series|series [A-F]' OR text_en LIKE '%series%'
   OR text_nl LIKE '%reeks%'
   OR text_fr REGEXP 's[ée]rie';

UPDATE quiz_answer_options
SET
    option_text_en = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(option_text_en,
        'Which series does sign the traffic sign', 'To which category does the traffic sign'),
        'Which series does the traffic sign', 'To which category does the traffic sign'),
        'To which series does the traffic sign', 'To which category does the traffic sign'),
        'C-series sign', 'prohibition sign'),
        'E-series signs', 'parking and stopping signs'),
        'series of bends', 'sequence of bends'),
        'zone series', 'zone signs'),
    option_text_nl = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(option_text_nl,
        'Tot welke reeks hoort', 'Tot welke categorie behoort'),
        'Tot welke reeks behoort', 'Tot welke categorie behoort'),
        'reeks gevaarlijke bochten', 'opeenvolgende gevaarlijke bochten'),
        'de eerste bocht van de reeks', 'de eerste van de opeenvolgende bochten'),
        'de volledige reeks', 'alle opeenvolgende bochten'),
        'voor de volledige reeks', 'over alle opeenvolgende bochten'),
        'C-reeksbord', 'verbodsbord'),
    option_text_fr = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(option_text_fr,
        'Serie F (panneaux d information):', 'Panneaux d''information :'),
        'Serie E (panneaux de stationnement et d arret):', 'Panneaux de stationnement et d''arrêt :'),
        'série de virages', 'succession de virages'),
        'serie de virages', 'succession de virages'),
        'premier virage de la série', 'premier de la succession de virages'),
        'sur toute la série', 'sur l''ensemble des virages'),
        'pour toute la série', 'pour l''ensemble des virages'),
        'parce que c''est un panneau de la série C', 'parce que c''est un panneau d''interdiction')
WHERE option_text_en REGEXP '(A|B|C|D|E|F)-series|series [A-F]' OR option_text_en LIKE '%series%'
   OR option_text_nl LIKE '%reeks%'
   OR option_text_fr REGEXP 's[ée]rie';

UPDATE road_signs
SET
    description_nl = REPLACE(description_nl, 'reeks gevaarlijke bochten', 'opeenvolgende gevaarlijke bochten'),
    description_fr = REPLACE(REPLACE(description_fr, 'série de virages', 'succession de virages'), 'serie de virages', 'succession de virages')
WHERE description_nl LIKE '%reeks%' OR description_fr REGEXP 's[ée]rie';

DROP TEMPORARY TABLE IF EXISTS tmp_v211_phrase_replacements;
