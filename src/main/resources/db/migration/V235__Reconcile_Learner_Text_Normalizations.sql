-- Reconcile the overlapping learner-facing terminology passes from V210 and V211.
-- This superseding migration preserves applied history and converges shared content
-- tables on a deterministic post-cleanup wording state.

SET SESSION group_concat_max_len = 1024 * 1024 * 16;

DROP TEMPORARY TABLE IF EXISTS tmp_v235_phrase_replacements;

CREATE TEMPORARY TABLE tmp_v235_phrase_replacements (
    language_code VARCHAR(2) NOT NULL,
    search_text TEXT NOT NULL,
    replacement_text TEXT NOT NULL
) ENGINE=InnoDB;

INSERT INTO tmp_v235_phrase_replacements (language_code, search_text, replacement_text) VALUES
    ('AR', 'لافتات الخطر', 'علامات الخطر'),
    ('AR', 'لافتات الحظر', 'علامات الحظر'),
    ('AR', 'لافتات الأولوية', 'علامات الأولوية'),
    ('AR', 'لافتات الإلزام', 'علامات الإلزام'),
    ('AR', 'لافتات المعلومات', 'علامات المعلومات'),
    ('AR', 'لافتات الوقوف والانتظار', 'علامات الوقوف والانتظار'),
    ('AR', 'لافتات التحذير والخطر', 'علامات التحذير والخطر'),
    ('AR', 'من لافتات المرور', 'من العلامات المرورية'),
    ('AR', 'اللافتة العلامة المرورية:', 'العلامة المرورية:'),
    ('AR', 'لافتة العلامة المرورية:', 'العلامة المرورية:'),
    ('AR', 'خارج التجمعات السكانية', 'خارج المنطقة السكنية'),
    ('AR', 'داخل التجمعات السكانية', 'داخل المنطقة السكنية'),
    ('AR', 'هل أنت ملزم بتعديل سلوك قيادتك عندما ترى لافتة الخطر هذه؟', 'هل يجب عليك تعديل سلوك قيادتك عند رؤية هذه العلامة المرورية التحذيرية؟'),
    ('AR', 'على أي مسافة من الخطر توضع لافتة الخطر خارج المنطقة السكنية؟', 'ما هي المسافة التي توضع عندها هذه العلامة التحذيرية خارج المنطقة السكنية؟'),
    ('AR', 'على أي مسافة يُوضع هذا اللافتة التحذيرية خارج المنطقة السكنية؟', 'ما هي المسافة التي توضع عندها هذه العلامة التحذيرية خارج المنطقة السكنية؟'),
    ('AR', 'على أي مسافة قبل إشارات المرور توضع لافتة العلامة المرورية: وجود إشارة ضوئية عادةً خارج المنطقة السكنية؟', 'ما هي المسافة التي توضع عندها هذه العلامة التحذيرية عادةً خارج المنطقة السكنية؟'),
    ('AR', 'ترى اللافتة العلامة المرورية: وجود إشارة ضوئية. كيف تعدّل سلوكك القيادي؟', 'ترى العلامة المرورية التي تشير إلى وجود إشارة ضوئية. كيف تعدّل سلوكك أثناء القيادة؟'),
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
    ('AR', 'الالعلامة المروريةية', 'العلامة المرورية'),
    ('AR', 'العلامة المروريةية', 'العلامة المرورية'),
    ('AR', 'علامة مرورية حظر', 'علامة حظر'),
    ('AR', 'نظام المنعطفات المتتالية', 'نظام التعاقب'),
    ('AR', 'خالم', 'خالٍ'),
    ('AR', 'استثناءًا', 'استثناءً'),
    ('EN', 'To which series does the traffic sign', 'To which category does the traffic sign'),
    ('EN', 'Which series does sign the traffic sign', 'To which category does the traffic sign'),
    ('EN', 'Which series does the traffic sign', 'To which category does the traffic sign'),
    ('EN', 'the the traffic sign', 'the traffic sign'),
    ('EN', 'What is the purpose of the the traffic sign', 'What is the purpose of the traffic sign'),
    ('EN', 'B-series (priority signs):', 'Priority signs:'),
    ('EN', 'E-series (parking and stopping signs):', 'Parking and stopping signs:'),
    ('EN', 'F-series (information signs):', 'Information signs:'),
    ('EN', ' is a hazard sign from the A-series', ' is a danger sign'),
    ('EN', ' belongs to the G-series (additional and information signs)', ' belongs to the information signs'),
    ('EN', ' series that opened the reserved road', ' that opened the reserved road'),
    ('EN', ' signs from E-series', ' parking and stopping signs'),
    ('EN', 'Yes: stopping is never regulated by E-series signs', 'Yes: stopping is never regulated by parking and stopping signs'),
    ('EN', 'because it is a C-series sign', 'because it is a prohibition sign'),
    ('EN', 'belongs to the zone series', 'belongs to the zone signs'),
    ('EN', 'entire series of bends', 'entire sequence of bends'),
    ('EN', 'series of bends', 'sequence of bends'),
    ('EN', 'entire series', 'entire sequence of bends'),
    ('EN', 'E-series signs', 'parking and stopping signs'),
    ('EN', 'zone series', 'zone signs'),
    ('EN', 'C-series sign', 'prohibition sign'),
    ('NL', 'de reeks het verkeersbord', 'het verkeersbord'),
    ('NL', 'Tot welke reeks hoort', 'Tot welke categorie behoort'),
    ('NL', 'Tot welke reeks behoort', 'Tot welke categorie behoort'),
    ('NL', 'B-reeks (voorrangsborden):', 'Voorrangsborden:'),
    ('NL', 'E-reeks (parkeer- en stilstandsborden):', 'Parkeer- en stilstandsborden:'),
    ('NL', 'F-reeks (informatieve borden):', 'Informatieborden:'),
    ('NL', ' is een gevaarsbord uit de A-reeks', ' is een gevaarsbord'),
    ('NL', 'E-reeksborden', 'parkeer- en stilstandsborden'),
    ('NL', '-reeks dat de voorbehouden weg opende', ' dat de voorbehouden weg opende'),
    ('NL', 'eerste bocht van de reeks', 'eerste van de opeenvolgende bochten'),
    ('NL', 'de eerste bocht van de reeks', 'de eerste van de opeenvolgende bochten'),
    ('NL', 'de volledige reeks', 'alle opeenvolgende bochten'),
    ('NL', 'voor de volledige reeks', 'over alle opeenvolgende bochten'),
    ('NL', 'reeks gevaarlijke bochten', 'opeenvolgende gevaarlijke bochten'),
    ('NL', 'C-reeksbord', 'verbodsbord'),
    ('NL', 'G-reeks', 'aanvullende en informatieve borden'),
    ('FR', 'la serie le panneau', 'le panneau'),
    ('FR', 'la série le panneau', 'le panneau'),
    ('FR', 'A quelle serie appartient', 'A quelle catégorie appartient'),
    ('FR', 'A quelle série appartient', 'A quelle catégorie appartient'),
    ('FR', 'À quelle serie appartient', 'À quelle catégorie appartient'),
    ('FR', ' est un panneau de danger de la serie A', ' est un panneau de danger'),
    ('FR', 'Serie B (panneaux de priorite):', 'Panneaux de priorité:'),
    ('FR', 'Série B (panneaux de priorité):', 'Panneaux de priorité:'),
    ('FR', 'de la serie E', 'des panneaux de stationnement et d''arrêt'),
    ('FR', 'de la série E', 'des panneaux de stationnement et d''arrêt'),
    ('FR', ' de la serie le panneau', ' du panneau'),
    ('FR', ' de la série le panneau', ' du panneau'),
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

UPDATE sign_questions
SET
    question_ar = REGEXP_REPLACE(question_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', ''),
    explanation_ar = REGEXP_REPLACE(explanation_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', '')
WHERE question_ar REGEXP '\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)'
   OR explanation_ar REGEXP '\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)';

UPDATE sign_choices
SET text_ar = REGEXP_REPLACE(text_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', '')
WHERE text_ar REGEXP '\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)';

UPDATE quiz_questions
SET
    question_ar = REGEXP_REPLACE(question_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', ''),
    explanation_ar = REGEXP_REPLACE(explanation_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', ''),
    error_explanation_ar = REGEXP_REPLACE(error_explanation_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', '')
WHERE question_ar REGEXP '\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)'
   OR explanation_ar REGEXP '\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)'
   OR error_explanation_ar REGEXP '\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)';

UPDATE quiz_answer_options
SET option_text_ar = REGEXP_REPLACE(option_text_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', '')
WHERE option_text_ar REGEXP '\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)';

UPDATE traffic_rules
SET
    title_ar = REGEXP_REPLACE(title_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', ''),
    content_ar = REGEXP_REPLACE(content_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', ''),
    penalty_info_ar = REGEXP_REPLACE(penalty_info_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', '')
WHERE title_ar REGEXP '\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)'
   OR content_ar REGEXP '\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)'
   OR penalty_info_ar REGEXP '\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)';

UPDATE lesson_pages
SET
    title_ar = REGEXP_REPLACE(title_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', ''),
    content_ar = REGEXP_REPLACE(content_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', ''),
    bullet_points_ar = REGEXP_REPLACE(bullet_points_ar, '[[:space:]]*\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)', '')
WHERE title_ar REGEXP '\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)'
   OR content_ar REGEXP '\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)'
   OR bullet_points_ar REGEXP '\\((?:السلسلة|الفئة)[[:space:]]*[A-Zأ-ي0-9]+\\)';

SET @expr_ar = (
    SELECT CONCAT(
        REPEAT('REPLACE(', COUNT(*)),
        '__COLUMN__',
        GROUP_CONCAT(
            CONCAT(', ''', REPLACE(search_text, '''', ''''''), ''', ''', REPLACE(replacement_text, '''', ''''''), ''')')
            ORDER BY CHAR_LENGTH(search_text) DESC, search_text
            SEPARATOR ''
        )
    )
    FROM tmp_v235_phrase_replacements
    WHERE language_code = 'AR'
);

SET @expr_en = (
    SELECT CONCAT(
        REPEAT('REPLACE(', COUNT(*)),
        '__COLUMN__',
        GROUP_CONCAT(
            CONCAT(', ''', REPLACE(search_text, '''', ''''''), ''', ''', REPLACE(replacement_text, '''', ''''''), ''')')
            ORDER BY CHAR_LENGTH(search_text) DESC, search_text
            SEPARATOR ''
        )
    )
    FROM tmp_v235_phrase_replacements
    WHERE language_code = 'EN'
);

SET @expr_nl = (
    SELECT CONCAT(
        REPEAT('REPLACE(', COUNT(*)),
        '__COLUMN__',
        GROUP_CONCAT(
            CONCAT(', ''', REPLACE(search_text, '''', ''''''), ''', ''', REPLACE(replacement_text, '''', ''''''), ''')')
            ORDER BY CHAR_LENGTH(search_text) DESC, search_text
            SEPARATOR ''
        )
    )
    FROM tmp_v235_phrase_replacements
    WHERE language_code = 'NL'
);

SET @expr_fr = (
    SELECT CONCAT(
        REPEAT('REPLACE(', COUNT(*)),
        '__COLUMN__',
        GROUP_CONCAT(
            CONCAT(', ''', REPLACE(search_text, '''', ''''''), ''', ''', REPLACE(replacement_text, '''', ''''''), ''')')
            ORDER BY CHAR_LENGTH(search_text) DESC, search_text
            SEPARATOR ''
        )
    )
    FROM tmp_v235_phrase_replacements
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
    question_en = REGEXP_REPLACE(question_en, '(the traffic sign "[^"]+")[[:space:]]*([0-9]+/)?[A-Z]+[0-9]+[A-Za-z0-9/-]*', '\\1'),
    question_nl = REGEXP_REPLACE(question_nl, '(het verkeersbord "[^"]+")[[:space:]]*([0-9]+/)?[A-Z]+[0-9]+[A-Za-z0-9/-]*', '\\1'),
    question_fr = REGEXP_REPLACE(question_fr, '(le panneau "[^"]+")[[:space:]]*([0-9]+/)?[A-Z]+[0-9]+[A-Za-z0-9/-]*', '\\1'),
    explanation_en = REGEXP_REPLACE(explanation_en, '(the traffic sign "[^"]+")[[:space:]]*([0-9]+/)?[A-Z]+[0-9]+[A-Za-z0-9/-]*', '\\1'),
    explanation_nl = REGEXP_REPLACE(explanation_nl, '(het verkeersbord "[^"]+")[[:space:]]*([0-9]+/)?[A-Z]+[0-9]+[A-Za-z0-9/-]*', '\\1'),
    explanation_fr = REGEXP_REPLACE(explanation_fr, '(le panneau "[^"]+")[[:space:]]*([0-9]+/)?[A-Z]+[0-9]+[A-Za-z0-9/-]*', '\\1');

UPDATE sign_choices
SET
    text_en = REGEXP_REPLACE(text_en, '(the traffic sign "[^"]+")[[:space:]]*([0-9]+/)?[A-Z]+[0-9]+[A-Za-z0-9/-]*', '\\1'),
    text_nl = REGEXP_REPLACE(text_nl, '(het verkeersbord "[^"]+")[[:space:]]*([0-9]+/)?[A-Z]+[0-9]+[A-Za-z0-9/-]*', '\\1'),
    text_fr = REGEXP_REPLACE(text_fr, '(le panneau "[^"]+")[[:space:]]*([0-9]+/)?[A-Z]+[0-9]+[A-Za-z0-9/-]*', '\\1');

UPDATE quiz_questions
SET
    question_en = REGEXP_REPLACE(question_en, '(the traffic sign "[^"]+")[[:space:]]*([0-9]+/)?[A-Z]+[0-9]+[A-Za-z0-9/-]*', '\\1'),
    question_nl = REGEXP_REPLACE(question_nl, '(het verkeersbord "[^"]+")[[:space:]]*([0-9]+/)?[A-Z]+[0-9]+[A-Za-z0-9/-]*', '\\1'),
    question_fr = REGEXP_REPLACE(question_fr, '(le panneau "[^"]+")[[:space:]]*([0-9]+/)?[A-Z]+[0-9]+[A-Za-z0-9/-]*', '\\1'),
    explanation_en = REGEXP_REPLACE(explanation_en, '(the traffic sign "[^"]+")[[:space:]]*([0-9]+/)?[A-Z]+[0-9]+[A-Za-z0-9/-]*', '\\1'),
    explanation_nl = REGEXP_REPLACE(explanation_nl, '(het verkeersbord "[^"]+")[[:space:]]*([0-9]+/)?[A-Z]+[0-9]+[A-Za-z0-9/-]*', '\\1'),
    explanation_fr = REGEXP_REPLACE(explanation_fr, '(le panneau "[^"]+")[[:space:]]*([0-9]+/)?[A-Z]+[0-9]+[A-Za-z0-9/-]*', '\\1'),
    error_explanation_en = REGEXP_REPLACE(error_explanation_en, '(the traffic sign "[^"]+")[[:space:]]*([0-9]+/)?[A-Z]+[0-9]+[A-Za-z0-9/-]*', '\\1'),
    error_explanation_nl = REGEXP_REPLACE(error_explanation_nl, '(het verkeersbord "[^"]+")[[:space:]]*([0-9]+/)?[A-Z]+[0-9]+[A-Za-z0-9/-]*', '\\1'),
    error_explanation_fr = REGEXP_REPLACE(error_explanation_fr, '(le panneau "[^"]+")[[:space:]]*([0-9]+/)?[A-Z]+[0-9]+[A-Za-z0-9/-]*', '\\1');

UPDATE quiz_answer_options
SET
    option_text_en = REGEXP_REPLACE(option_text_en, '(the traffic sign "[^"]+")[[:space:]]*([0-9]+/)?[A-Z]+[0-9]+[A-Za-z0-9/-]*', '\\1'),
    option_text_nl = REGEXP_REPLACE(option_text_nl, '(het verkeersbord "[^"]+")[[:space:]]*([0-9]+/)?[A-Z]+[0-9]+[A-Za-z0-9/-]*', '\\1'),
    option_text_fr = REGEXP_REPLACE(option_text_fr, '(le panneau "[^"]+")[[:space:]]*([0-9]+/)?[A-Z]+[0-9]+[A-Za-z0-9/-]*', '\\1');

DROP TEMPORARY TABLE IF EXISTS tmp_v235_phrase_replacements;