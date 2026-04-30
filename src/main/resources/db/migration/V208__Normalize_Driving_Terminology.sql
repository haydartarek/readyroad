-- Normalize persisted driving-learning terminology without modifying
-- historical seed migrations that are already applied in existing databases.

-- Arabic prompt normalization for persisted learner-facing content

UPDATE sign_questions
SET     question_ar = REPLACE(question_ar, 'ما معنى لافتة المرور هذه؟', 'ما معنى هذه العلامة المرورية؟'),
    explanation_ar = REPLACE(explanation_ar, 'ما معنى لافتة المرور هذه؟', 'ما معنى هذه العلامة المرورية؟')
WHERE question_ar LIKE '%ما معنى لافتة المرور هذه؟%' OR explanation_ar LIKE '%ما معنى لافتة المرور هذه؟%';

UPDATE sign_questions
SET     question_ar = REPLACE(question_ar, 'ما الخطر الذي تعلنه هذه اللافتة؟', 'ما هو الخطر الذي تشير إليه هذه العلامة المرورية؟'),
    explanation_ar = REPLACE(explanation_ar, 'ما الخطر الذي تعلنه هذه اللافتة؟', 'ما هو الخطر الذي تشير إليه هذه العلامة المرورية؟')
WHERE question_ar LIKE '%ما الخطر الذي تعلنه هذه اللافتة؟%' OR explanation_ar LIKE '%ما الخطر الذي تعلنه هذه اللافتة؟%';

UPDATE sign_questions
SET     question_ar = REPLACE(question_ar, 'إلى أي فئة من لافتات المرور تنتمي هذه اللافتة؟', 'إلى أي فئة تُصنَّف هذه العلامة المرورية؟'),
    explanation_ar = REPLACE(explanation_ar, 'إلى أي فئة من لافتات المرور تنتمي هذه اللافتة؟', 'إلى أي فئة تُصنَّف هذه العلامة المرورية؟')
WHERE question_ar LIKE '%إلى أي فئة من لافتات المرور تنتمي هذه اللافتة؟%' OR explanation_ar LIKE '%إلى أي فئة من لافتات المرور تنتمي هذه اللافتة؟%';

UPDATE sign_questions
SET     question_ar = REPLACE(question_ar, 'تعرجات خطيرة متعددة، الأول إلى اليسار', 'منعطف مزدوج خطير، الأول إلى اليسار'),
    explanation_ar = REPLACE(explanation_ar, 'تعرجات خطيرة متعددة، الأول إلى اليسار', 'منعطف مزدوج خطير، الأول إلى اليسار')
WHERE question_ar LIKE '%تعرجات خطيرة متعددة، الأول إلى اليسار%' OR explanation_ar LIKE '%تعرجات خطيرة متعددة، الأول إلى اليسار%';

UPDATE sign_questions
SET     question_ar = REPLACE(question_ar, 'منعطفات مزدوجة خطيرة أو أكثر', 'منعطف مزدوج خطير أو أكثر'),
    explanation_ar = REPLACE(explanation_ar, 'منعطفات مزدوجة خطيرة أو أكثر', 'منعطف مزدوج خطير أو أكثر')
WHERE question_ar LIKE '%منعطفات مزدوجة خطيرة أو أكثر%' OR explanation_ar LIKE '%منعطفات مزدوجة خطيرة أو أكثر%';

UPDATE quiz_questions
SET     question_ar = REPLACE(question_ar, 'ما معنى لافتة المرور هذه؟', 'ما معنى هذه العلامة المرورية؟'),
    explanation_ar = REPLACE(explanation_ar, 'ما معنى لافتة المرور هذه؟', 'ما معنى هذه العلامة المرورية؟'),
    error_explanation_ar = REPLACE(error_explanation_ar, 'ما معنى لافتة المرور هذه؟', 'ما معنى هذه العلامة المرورية؟')
WHERE question_ar LIKE '%ما معنى لافتة المرور هذه؟%' OR explanation_ar LIKE '%ما معنى لافتة المرور هذه؟%' OR error_explanation_ar LIKE '%ما معنى لافتة المرور هذه؟%';

UPDATE quiz_questions
SET     question_ar = REPLACE(question_ar, 'ما الخطر الذي تعلنه هذه اللافتة؟', 'ما هو الخطر الذي تشير إليه هذه العلامة المرورية؟'),
    explanation_ar = REPLACE(explanation_ar, 'ما الخطر الذي تعلنه هذه اللافتة؟', 'ما هو الخطر الذي تشير إليه هذه العلامة المرورية؟'),
    error_explanation_ar = REPLACE(error_explanation_ar, 'ما الخطر الذي تعلنه هذه اللافتة؟', 'ما هو الخطر الذي تشير إليه هذه العلامة المرورية؟')
WHERE question_ar LIKE '%ما الخطر الذي تعلنه هذه اللافتة؟%' OR explanation_ar LIKE '%ما الخطر الذي تعلنه هذه اللافتة؟%' OR error_explanation_ar LIKE '%ما الخطر الذي تعلنه هذه اللافتة؟%';

UPDATE quiz_questions
SET     question_ar = REPLACE(question_ar, 'إلى أي فئة من لافتات المرور تنتمي هذه اللافتة؟', 'إلى أي فئة تُصنَّف هذه العلامة المرورية؟'),
    explanation_ar = REPLACE(explanation_ar, 'إلى أي فئة من لافتات المرور تنتمي هذه اللافتة؟', 'إلى أي فئة تُصنَّف هذه العلامة المرورية؟'),
    error_explanation_ar = REPLACE(error_explanation_ar, 'إلى أي فئة من لافتات المرور تنتمي هذه اللافتة؟', 'إلى أي فئة تُصنَّف هذه العلامة المرورية؟')
WHERE question_ar LIKE '%إلى أي فئة من لافتات المرور تنتمي هذه اللافتة؟%' OR explanation_ar LIKE '%إلى أي فئة من لافتات المرور تنتمي هذه اللافتة؟%' OR error_explanation_ar LIKE '%إلى أي فئة من لافتات المرور تنتمي هذه اللافتة؟%';

UPDATE quiz_questions
SET     question_ar = REPLACE(question_ar, 'تعرجات خطيرة متعددة، الأول إلى اليسار', 'منعطف مزدوج خطير، الأول إلى اليسار'),
    explanation_ar = REPLACE(explanation_ar, 'تعرجات خطيرة متعددة، الأول إلى اليسار', 'منعطف مزدوج خطير، الأول إلى اليسار'),
    error_explanation_ar = REPLACE(error_explanation_ar, 'تعرجات خطيرة متعددة، الأول إلى اليسار', 'منعطف مزدوج خطير، الأول إلى اليسار')
WHERE question_ar LIKE '%تعرجات خطيرة متعددة، الأول إلى اليسار%' OR explanation_ar LIKE '%تعرجات خطيرة متعددة، الأول إلى اليسار%' OR error_explanation_ar LIKE '%تعرجات خطيرة متعددة، الأول إلى اليسار%';

UPDATE quiz_questions
SET     question_ar = REPLACE(question_ar, 'منعطفات مزدوجة خطيرة أو أكثر', 'منعطف مزدوج خطير أو أكثر'),
    explanation_ar = REPLACE(explanation_ar, 'منعطفات مزدوجة خطيرة أو أكثر', 'منعطف مزدوج خطير أو أكثر'),
    error_explanation_ar = REPLACE(error_explanation_ar, 'منعطفات مزدوجة خطيرة أو أكثر', 'منعطف مزدوج خطير أو أكثر')
WHERE question_ar LIKE '%منعطفات مزدوجة خطيرة أو أكثر%' OR explanation_ar LIKE '%منعطفات مزدوجة خطيرة أو أكثر%' OR error_explanation_ar LIKE '%منعطفات مزدوجة خطيرة أو أكثر%';

UPDATE traffic_rules
SET     title_ar = REPLACE(title_ar, 'ما معنى لافتة المرور هذه؟', 'ما معنى هذه العلامة المرورية؟'),
    content_ar = REPLACE(content_ar, 'ما معنى لافتة المرور هذه؟', 'ما معنى هذه العلامة المرورية؟'),
    penalty_info_ar = REPLACE(penalty_info_ar, 'ما معنى لافتة المرور هذه؟', 'ما معنى هذه العلامة المرورية؟')
WHERE title_ar LIKE '%ما معنى لافتة المرور هذه؟%' OR content_ar LIKE '%ما معنى لافتة المرور هذه؟%' OR penalty_info_ar LIKE '%ما معنى لافتة المرور هذه؟%';

UPDATE traffic_rules
SET     title_ar = REPLACE(title_ar, 'ما الخطر الذي تعلنه هذه اللافتة؟', 'ما هو الخطر الذي تشير إليه هذه العلامة المرورية؟'),
    content_ar = REPLACE(content_ar, 'ما الخطر الذي تعلنه هذه اللافتة؟', 'ما هو الخطر الذي تشير إليه هذه العلامة المرورية؟'),
    penalty_info_ar = REPLACE(penalty_info_ar, 'ما الخطر الذي تعلنه هذه اللافتة؟', 'ما هو الخطر الذي تشير إليه هذه العلامة المرورية؟')
WHERE title_ar LIKE '%ما الخطر الذي تعلنه هذه اللافتة؟%' OR content_ar LIKE '%ما الخطر الذي تعلنه هذه اللافتة؟%' OR penalty_info_ar LIKE '%ما الخطر الذي تعلنه هذه اللافتة؟%';

UPDATE traffic_rules
SET     title_ar = REPLACE(title_ar, 'إلى أي فئة من لافتات المرور تنتمي هذه اللافتة؟', 'إلى أي فئة تُصنَّف هذه العلامة المرورية؟'),
    content_ar = REPLACE(content_ar, 'إلى أي فئة من لافتات المرور تنتمي هذه اللافتة؟', 'إلى أي فئة تُصنَّف هذه العلامة المرورية؟'),
    penalty_info_ar = REPLACE(penalty_info_ar, 'إلى أي فئة من لافتات المرور تنتمي هذه اللافتة؟', 'إلى أي فئة تُصنَّف هذه العلامة المرورية؟')
WHERE title_ar LIKE '%إلى أي فئة من لافتات المرور تنتمي هذه اللافتة؟%' OR content_ar LIKE '%إلى أي فئة من لافتات المرور تنتمي هذه اللافتة؟%' OR penalty_info_ar LIKE '%إلى أي فئة من لافتات المرور تنتمي هذه اللافتة؟%';

UPDATE traffic_rules
SET     title_ar = REPLACE(title_ar, 'تعرجات خطيرة متعددة، الأول إلى اليسار', 'منعطف مزدوج خطير، الأول إلى اليسار'),
    content_ar = REPLACE(content_ar, 'تعرجات خطيرة متعددة، الأول إلى اليسار', 'منعطف مزدوج خطير، الأول إلى اليسار'),
    penalty_info_ar = REPLACE(penalty_info_ar, 'تعرجات خطيرة متعددة، الأول إلى اليسار', 'منعطف مزدوج خطير، الأول إلى اليسار')
WHERE title_ar LIKE '%تعرجات خطيرة متعددة، الأول إلى اليسار%' OR content_ar LIKE '%تعرجات خطيرة متعددة، الأول إلى اليسار%' OR penalty_info_ar LIKE '%تعرجات خطيرة متعددة، الأول إلى اليسار%';

UPDATE traffic_rules
SET     title_ar = REPLACE(title_ar, 'منعطفات مزدوجة خطيرة أو أكثر', 'منعطف مزدوج خطير أو أكثر'),
    content_ar = REPLACE(content_ar, 'منعطفات مزدوجة خطيرة أو أكثر', 'منعطف مزدوج خطير أو أكثر'),
    penalty_info_ar = REPLACE(penalty_info_ar, 'منعطفات مزدوجة خطيرة أو أكثر', 'منعطف مزدوج خطير أو أكثر')
WHERE title_ar LIKE '%منعطفات مزدوجة خطيرة أو أكثر%' OR content_ar LIKE '%منعطفات مزدوجة خطيرة أو أكثر%' OR penalty_info_ar LIKE '%منعطفات مزدوجة خطيرة أو أكثر%';

UPDATE sign_questions
SET     question_ar = REPLACE(question_ar, 'سلسلة منعطفات خطيرة متتالية', 'منعطفات خطيرة متتالية'),
    explanation_ar = REPLACE(explanation_ar, 'سلسلة منعطفات خطيرة متتالية', 'منعطفات خطيرة متتالية')
WHERE question_ar LIKE '%سلسلة منعطفات خطيرة متتالية%' OR explanation_ar LIKE '%سلسلة منعطفات خطيرة متتالية%';

UPDATE sign_questions
SET     question_ar = REPLACE(question_ar, 'طوال السلسلة', 'على امتداد جميع المنعطفات'),
    explanation_ar = REPLACE(explanation_ar, 'طوال السلسلة', 'على امتداد جميع المنعطفات')
WHERE question_ar LIKE '%طوال السلسلة%' OR explanation_ar LIKE '%طوال السلسلة%';

UPDATE sign_questions
SET     question_ar = REPLACE(question_ar, 'لكامل السلسلة', 'على امتداد جميع المنعطفات'),
    explanation_ar = REPLACE(explanation_ar, 'لكامل السلسلة', 'على امتداد جميع المنعطفات')
WHERE question_ar LIKE '%لكامل السلسلة%' OR explanation_ar LIKE '%لكامل السلسلة%';

UPDATE sign_questions
SET     question_ar = REPLACE(question_ar, 'من السلسلة', 'من المنعطفات المتتالية'),
    explanation_ar = REPLACE(explanation_ar, 'من السلسلة', 'من المنعطفات المتتالية')
WHERE question_ar LIKE '%من السلسلة%' OR explanation_ar LIKE '%من السلسلة%';

UPDATE sign_questions
SET     question_ar = REPLACE(question_ar, 'تنتمي إلى السلسلة أ وتحذر من المواقف الخطرة.', 'تنتمي إلى علامات الخطر وتحذر من المواقف الخطرة.'),
    explanation_ar = REPLACE(explanation_ar, 'تنتمي إلى السلسلة أ وتحذر من المواقف الخطرة.', 'تنتمي إلى علامات الخطر وتحذر من المواقف الخطرة.')
WHERE question_ar LIKE '%تنتمي إلى السلسلة أ وتحذر من المواقف الخطرة.%' OR explanation_ar LIKE '%تنتمي إلى السلسلة أ وتحذر من المواقف الخطرة.%';

UPDATE sign_questions
SET     question_ar = REPLACE(question_ar, 'علامات الخطر (A) مثلثة؛ علامات الإلزام (D) زرقاء.', 'علامات الخطر مثلثة؛ وعلامات الإلزام زرقاء.'),
    explanation_ar = REPLACE(explanation_ar, 'علامات الخطر (A) مثلثة؛ علامات الإلزام (D) زرقاء.', 'علامات الخطر مثلثة؛ وعلامات الإلزام زرقاء.')
WHERE question_ar LIKE '%علامات الخطر (A) مثلثة؛ علامات الإلزام (D) زرقاء.%' OR explanation_ar LIKE '%علامات الخطر (A) مثلثة؛ علامات الإلزام (D) زرقاء.%';

UPDATE sign_choices
SET     text_ar = REPLACE(text_ar, 'فئة A وB', 'الفئتين أ و ب')
WHERE text_ar LIKE '%فئة A وB%';

UPDATE quiz_questions
SET     question_ar = REPLACE(question_ar, 'سلسلة منعطفات خطيرة متتالية', 'منعطفات خطيرة متتالية'),
    explanation_ar = REPLACE(explanation_ar, 'سلسلة منعطفات خطيرة متتالية', 'منعطفات خطيرة متتالية'),
    error_explanation_ar = REPLACE(error_explanation_ar, 'سلسلة منعطفات خطيرة متتالية', 'منعطفات خطيرة متتالية')
WHERE question_ar LIKE '%سلسلة منعطفات خطيرة متتالية%' OR explanation_ar LIKE '%سلسلة منعطفات خطيرة متتالية%' OR error_explanation_ar LIKE '%سلسلة منعطفات خطيرة متتالية%';

UPDATE quiz_questions
SET     question_ar = REPLACE(question_ar, 'طوال السلسلة', 'على امتداد جميع المنعطفات'),
    explanation_ar = REPLACE(explanation_ar, 'طوال السلسلة', 'على امتداد جميع المنعطفات'),
    error_explanation_ar = REPLACE(error_explanation_ar, 'طوال السلسلة', 'على امتداد جميع المنعطفات')
WHERE question_ar LIKE '%طوال السلسلة%' OR explanation_ar LIKE '%طوال السلسلة%' OR error_explanation_ar LIKE '%طوال السلسلة%';

UPDATE quiz_questions
SET     question_ar = REPLACE(question_ar, 'لكامل السلسلة', 'على امتداد جميع المنعطفات'),
    explanation_ar = REPLACE(explanation_ar, 'لكامل السلسلة', 'على امتداد جميع المنعطفات'),
    error_explanation_ar = REPLACE(error_explanation_ar, 'لكامل السلسلة', 'على امتداد جميع المنعطفات')
WHERE question_ar LIKE '%لكامل السلسلة%' OR explanation_ar LIKE '%لكامل السلسلة%' OR error_explanation_ar LIKE '%لكامل السلسلة%';

UPDATE quiz_questions
SET     question_ar = REPLACE(question_ar, 'من السلسلة', 'من المنعطفات المتتالية'),
    explanation_ar = REPLACE(explanation_ar, 'من السلسلة', 'من المنعطفات المتتالية'),
    error_explanation_ar = REPLACE(error_explanation_ar, 'من السلسلة', 'من المنعطفات المتتالية')
WHERE question_ar LIKE '%من السلسلة%' OR explanation_ar LIKE '%من السلسلة%' OR error_explanation_ar LIKE '%من السلسلة%';

UPDATE quiz_questions
SET     question_ar = REPLACE(question_ar, 'تنتمي إلى السلسلة أ وتحذر من المواقف الخطرة.', 'تنتمي إلى علامات الخطر وتحذر من المواقف الخطرة.'),
    explanation_ar = REPLACE(explanation_ar, 'تنتمي إلى السلسلة أ وتحذر من المواقف الخطرة.', 'تنتمي إلى علامات الخطر وتحذر من المواقف الخطرة.'),
    error_explanation_ar = REPLACE(error_explanation_ar, 'تنتمي إلى السلسلة أ وتحذر من المواقف الخطرة.', 'تنتمي إلى علامات الخطر وتحذر من المواقف الخطرة.')
WHERE question_ar LIKE '%تنتمي إلى السلسلة أ وتحذر من المواقف الخطرة.%' OR explanation_ar LIKE '%تنتمي إلى السلسلة أ وتحذر من المواقف الخطرة.%' OR error_explanation_ar LIKE '%تنتمي إلى السلسلة أ وتحذر من المواقف الخطرة.%';

UPDATE quiz_questions
SET     question_ar = REPLACE(question_ar, 'علامات الخطر (A) مثلثة؛ علامات الإلزام (D) زرقاء.', 'علامات الخطر مثلثة؛ وعلامات الإلزام زرقاء.'),
    explanation_ar = REPLACE(explanation_ar, 'علامات الخطر (A) مثلثة؛ علامات الإلزام (D) زرقاء.', 'علامات الخطر مثلثة؛ وعلامات الإلزام زرقاء.'),
    error_explanation_ar = REPLACE(error_explanation_ar, 'علامات الخطر (A) مثلثة؛ علامات الإلزام (D) زرقاء.', 'علامات الخطر مثلثة؛ وعلامات الإلزام زرقاء.')
WHERE question_ar LIKE '%علامات الخطر (A) مثلثة؛ علامات الإلزام (D) زرقاء.%' OR explanation_ar LIKE '%علامات الخطر (A) مثلثة؛ علامات الإلزام (D) زرقاء.%' OR error_explanation_ar LIKE '%علامات الخطر (A) مثلثة؛ علامات الإلزام (D) زرقاء.%';

UPDATE quiz_answer_options
SET     option_text_ar = REPLACE(option_text_ar, 'فئة A وB', 'الفئتين أ و ب')
WHERE option_text_ar LIKE '%فئة A وB%';

UPDATE traffic_rules
SET     title_ar = REPLACE(title_ar, 'نظام السلسلة', 'نظام التعاقب'),
    content_ar = REPLACE(content_ar, 'نظام السلسلة', 'نظام التعاقب'),
    penalty_info_ar = REPLACE(penalty_info_ar, 'نظام السلسلة', 'نظام التعاقب')
WHERE title_ar LIKE '%نظام السلسلة%' OR content_ar LIKE '%نظام السلسلة%' OR penalty_info_ar LIKE '%نظام السلسلة%';

-- Core Arabic terminology normalization across persisted content

UPDATE sign_questions
SET     question_ar = REPLACE(question_ar, 'لافتات المرور', 'العلامات المرورية'),
    explanation_ar = REPLACE(explanation_ar, 'لافتات المرور', 'العلامات المرورية')
WHERE question_ar LIKE '%لافتات المرور%' OR explanation_ar LIKE '%لافتات المرور%';

UPDATE sign_questions
SET     question_ar = REPLACE(question_ar, 'هذه اللافتة', 'هذه العلامة المرورية'),
    explanation_ar = REPLACE(explanation_ar, 'هذه اللافتة', 'هذه العلامة المرورية')
WHERE question_ar LIKE '%هذه اللافتة%' OR explanation_ar LIKE '%هذه اللافتة%';

UPDATE sign_questions
SET     question_ar = REPLACE(question_ar, 'لافتة المرور', 'العلامة المرورية'),
    explanation_ar = REPLACE(explanation_ar, 'لافتة المرور', 'العلامة المرورية')
WHERE question_ar LIKE '%لافتة المرور%' OR explanation_ar LIKE '%لافتة المرور%';

UPDATE sign_questions
SET     question_ar = REPLACE(question_ar, 'خارج المناطق المبنية', 'خارج المنطقة السكنية'),
    explanation_ar = REPLACE(explanation_ar, 'خارج المناطق المبنية', 'خارج المنطقة السكنية')
WHERE question_ar LIKE '%خارج المناطق المبنية%' OR explanation_ar LIKE '%خارج المناطق المبنية%';

UPDATE sign_questions
SET     question_ar = REPLACE(question_ar, 'داخل المناطق المبنية', 'داخل المنطقة السكنية'),
    explanation_ar = REPLACE(explanation_ar, 'داخل المناطق المبنية', 'داخل المنطقة السكنية')
WHERE question_ar LIKE '%داخل المناطق المبنية%' OR explanation_ar LIKE '%داخل المناطق المبنية%';

UPDATE sign_questions
SET     question_ar = REPLACE(question_ar, 'طريق رطب', 'طريق مبلل'),
    explanation_ar = REPLACE(explanation_ar, 'طريق رطب', 'طريق مبلل')
WHERE question_ar LIKE '%طريق رطب%' OR explanation_ar LIKE '%طريق رطب%';

UPDATE sign_questions
SET     question_ar = REPLACE(question_ar, 'تروس', 'غيار'),
    explanation_ar = REPLACE(explanation_ar, 'تروس', 'غيار')
WHERE question_ar LIKE '%تروس%' OR explanation_ar LIKE '%تروس%';

UPDATE sign_questions
SET     question_ar = REPLACE(question_ar, 'انبعاث الحصى', 'تطاير الحصى'),
    explanation_ar = REPLACE(explanation_ar, 'انبعاث الحصى', 'تطاير الحصى')
WHERE question_ar LIKE '%انبعاث الحصى%' OR explanation_ar LIKE '%انبعاث الحصى%';

UPDATE sign_questions
SET     question_ar = REPLACE(question_ar, 'تعرج', 'منعطف'),
    explanation_ar = REPLACE(explanation_ar, 'تعرج', 'منعطف')
WHERE question_ar LIKE '%تعرج%' OR explanation_ar LIKE '%تعرج%';

UPDATE sign_choices
SET     text_ar = REPLACE(text_ar, 'لافتات المرور', 'العلامات المرورية')
WHERE text_ar LIKE '%لافتات المرور%';

UPDATE sign_choices
SET     text_ar = REPLACE(text_ar, 'هذه اللافتة', 'هذه العلامة المرورية')
WHERE text_ar LIKE '%هذه اللافتة%';

UPDATE sign_choices
SET     text_ar = REPLACE(text_ar, 'لافتة المرور', 'العلامة المرورية')
WHERE text_ar LIKE '%لافتة المرور%';

UPDATE sign_choices
SET     text_ar = REPLACE(text_ar, 'خارج المناطق المبنية', 'خارج المنطقة السكنية')
WHERE text_ar LIKE '%خارج المناطق المبنية%';

UPDATE sign_choices
SET     text_ar = REPLACE(text_ar, 'داخل المناطق المبنية', 'داخل المنطقة السكنية')
WHERE text_ar LIKE '%داخل المناطق المبنية%';

UPDATE sign_choices
SET     text_ar = REPLACE(text_ar, 'طريق رطب', 'طريق مبلل')
WHERE text_ar LIKE '%طريق رطب%';

UPDATE sign_choices
SET     text_ar = REPLACE(text_ar, 'تروس', 'غيار')
WHERE text_ar LIKE '%تروس%';

UPDATE sign_choices
SET     text_ar = REPLACE(text_ar, 'انبعاث الحصى', 'تطاير الحصى')
WHERE text_ar LIKE '%انبعاث الحصى%';

UPDATE sign_choices
SET     text_ar = REPLACE(text_ar, 'تعرج', 'منعطف')
WHERE text_ar LIKE '%تعرج%';

UPDATE quiz_questions
SET     question_ar = REPLACE(question_ar, 'لافتات المرور', 'العلامات المرورية'),
    explanation_ar = REPLACE(explanation_ar, 'لافتات المرور', 'العلامات المرورية'),
    error_explanation_ar = REPLACE(error_explanation_ar, 'لافتات المرور', 'العلامات المرورية')
WHERE question_ar LIKE '%لافتات المرور%' OR explanation_ar LIKE '%لافتات المرور%' OR error_explanation_ar LIKE '%لافتات المرور%';

UPDATE quiz_questions
SET     question_ar = REPLACE(question_ar, 'هذه اللافتة', 'هذه العلامة المرورية'),
    explanation_ar = REPLACE(explanation_ar, 'هذه اللافتة', 'هذه العلامة المرورية'),
    error_explanation_ar = REPLACE(error_explanation_ar, 'هذه اللافتة', 'هذه العلامة المرورية')
WHERE question_ar LIKE '%هذه اللافتة%' OR explanation_ar LIKE '%هذه اللافتة%' OR error_explanation_ar LIKE '%هذه اللافتة%';

UPDATE quiz_questions
SET     question_ar = REPLACE(question_ar, 'لافتة المرور', 'العلامة المرورية'),
    explanation_ar = REPLACE(explanation_ar, 'لافتة المرور', 'العلامة المرورية'),
    error_explanation_ar = REPLACE(error_explanation_ar, 'لافتة المرور', 'العلامة المرورية')
WHERE question_ar LIKE '%لافتة المرور%' OR explanation_ar LIKE '%لافتة المرور%' OR error_explanation_ar LIKE '%لافتة المرور%';

UPDATE quiz_questions
SET     question_ar = REPLACE(question_ar, 'خارج المناطق المبنية', 'خارج المنطقة السكنية'),
    explanation_ar = REPLACE(explanation_ar, 'خارج المناطق المبنية', 'خارج المنطقة السكنية'),
    error_explanation_ar = REPLACE(error_explanation_ar, 'خارج المناطق المبنية', 'خارج المنطقة السكنية')
WHERE question_ar LIKE '%خارج المناطق المبنية%' OR explanation_ar LIKE '%خارج المناطق المبنية%' OR error_explanation_ar LIKE '%خارج المناطق المبنية%';

UPDATE quiz_questions
SET     question_ar = REPLACE(question_ar, 'داخل المناطق المبنية', 'داخل المنطقة السكنية'),
    explanation_ar = REPLACE(explanation_ar, 'داخل المناطق المبنية', 'داخل المنطقة السكنية'),
    error_explanation_ar = REPLACE(error_explanation_ar, 'داخل المناطق المبنية', 'داخل المنطقة السكنية')
WHERE question_ar LIKE '%داخل المناطق المبنية%' OR explanation_ar LIKE '%داخل المناطق المبنية%' OR error_explanation_ar LIKE '%داخل المناطق المبنية%';

UPDATE quiz_questions
SET     question_ar = REPLACE(question_ar, 'طريق رطب', 'طريق مبلل'),
    explanation_ar = REPLACE(explanation_ar, 'طريق رطب', 'طريق مبلل'),
    error_explanation_ar = REPLACE(error_explanation_ar, 'طريق رطب', 'طريق مبلل')
WHERE question_ar LIKE '%طريق رطب%' OR explanation_ar LIKE '%طريق رطب%' OR error_explanation_ar LIKE '%طريق رطب%';

UPDATE quiz_questions
SET     question_ar = REPLACE(question_ar, 'تروس', 'غيار'),
    explanation_ar = REPLACE(explanation_ar, 'تروس', 'غيار'),
    error_explanation_ar = REPLACE(error_explanation_ar, 'تروس', 'غيار')
WHERE question_ar LIKE '%تروس%' OR explanation_ar LIKE '%تروس%' OR error_explanation_ar LIKE '%تروس%';

UPDATE quiz_questions
SET     question_ar = REPLACE(question_ar, 'انبعاث الحصى', 'تطاير الحصى'),
    explanation_ar = REPLACE(explanation_ar, 'انبعاث الحصى', 'تطاير الحصى'),
    error_explanation_ar = REPLACE(error_explanation_ar, 'انبعاث الحصى', 'تطاير الحصى')
WHERE question_ar LIKE '%انبعاث الحصى%' OR explanation_ar LIKE '%انبعاث الحصى%' OR error_explanation_ar LIKE '%انبعاث الحصى%';

UPDATE quiz_questions
SET     question_ar = REPLACE(question_ar, 'تعرج', 'منعطف'),
    explanation_ar = REPLACE(explanation_ar, 'تعرج', 'منعطف'),
    error_explanation_ar = REPLACE(error_explanation_ar, 'تعرج', 'منعطف')
WHERE question_ar LIKE '%تعرج%' OR explanation_ar LIKE '%تعرج%' OR error_explanation_ar LIKE '%تعرج%';

UPDATE quiz_answer_options
SET     option_text_ar = REPLACE(option_text_ar, 'لافتات المرور', 'العلامات المرورية')
WHERE option_text_ar LIKE '%لافتات المرور%';

UPDATE quiz_answer_options
SET     option_text_ar = REPLACE(option_text_ar, 'هذه اللافتة', 'هذه العلامة المرورية')
WHERE option_text_ar LIKE '%هذه اللافتة%';

UPDATE quiz_answer_options
SET     option_text_ar = REPLACE(option_text_ar, 'لافتة المرور', 'العلامة المرورية')
WHERE option_text_ar LIKE '%لافتة المرور%';

UPDATE quiz_answer_options
SET     option_text_ar = REPLACE(option_text_ar, 'خارج المناطق المبنية', 'خارج المنطقة السكنية')
WHERE option_text_ar LIKE '%خارج المناطق المبنية%';

UPDATE quiz_answer_options
SET     option_text_ar = REPLACE(option_text_ar, 'داخل المناطق المبنية', 'داخل المنطقة السكنية')
WHERE option_text_ar LIKE '%داخل المناطق المبنية%';

UPDATE quiz_answer_options
SET     option_text_ar = REPLACE(option_text_ar, 'طريق رطب', 'طريق مبلل')
WHERE option_text_ar LIKE '%طريق رطب%';

UPDATE quiz_answer_options
SET     option_text_ar = REPLACE(option_text_ar, 'تروس', 'غيار')
WHERE option_text_ar LIKE '%تروس%';

UPDATE quiz_answer_options
SET     option_text_ar = REPLACE(option_text_ar, 'انبعاث الحصى', 'تطاير الحصى')
WHERE option_text_ar LIKE '%انبعاث الحصى%';

UPDATE quiz_answer_options
SET     option_text_ar = REPLACE(option_text_ar, 'تعرج', 'منعطف')
WHERE option_text_ar LIKE '%تعرج%';

UPDATE road_signs
SET     description_ar = REPLACE(description_ar, 'لافتات المرور', 'العلامات المرورية')
WHERE description_ar LIKE '%لافتات المرور%';

UPDATE road_signs
SET     description_ar = REPLACE(description_ar, 'هذه اللافتة', 'هذه العلامة المرورية')
WHERE description_ar LIKE '%هذه اللافتة%';

UPDATE road_signs
SET     description_ar = REPLACE(description_ar, 'لافتة المرور', 'العلامة المرورية')
WHERE description_ar LIKE '%لافتة المرور%';

UPDATE road_signs
SET     description_ar = REPLACE(description_ar, 'خارج المناطق المبنية', 'خارج المنطقة السكنية')
WHERE description_ar LIKE '%خارج المناطق المبنية%';

UPDATE road_signs
SET     description_ar = REPLACE(description_ar, 'داخل المناطق المبنية', 'داخل المنطقة السكنية')
WHERE description_ar LIKE '%داخل المناطق المبنية%';

UPDATE road_signs
SET     description_ar = REPLACE(description_ar, 'طريق رطب', 'طريق مبلل')
WHERE description_ar LIKE '%طريق رطب%';

UPDATE road_signs
SET     description_ar = REPLACE(description_ar, 'تروس', 'غيار')
WHERE description_ar LIKE '%تروس%';

UPDATE road_signs
SET     description_ar = REPLACE(description_ar, 'انبعاث الحصى', 'تطاير الحصى')
WHERE description_ar LIKE '%انبعاث الحصى%';

UPDATE road_signs
SET     description_ar = REPLACE(description_ar, 'تعرج', 'منعطف')
WHERE description_ar LIKE '%تعرج%';

UPDATE categories
SET     description_ar = REPLACE(description_ar, 'لافتات المرور', 'العلامات المرورية')
WHERE description_ar LIKE '%لافتات المرور%';

UPDATE categories
SET     description_ar = REPLACE(description_ar, 'هذه اللافتة', 'هذه العلامة المرورية')
WHERE description_ar LIKE '%هذه اللافتة%';

UPDATE categories
SET     description_ar = REPLACE(description_ar, 'لافتة المرور', 'العلامة المرورية')
WHERE description_ar LIKE '%لافتة المرور%';

UPDATE categories
SET     description_ar = REPLACE(description_ar, 'خارج المناطق المبنية', 'خارج المنطقة السكنية')
WHERE description_ar LIKE '%خارج المناطق المبنية%';

UPDATE categories
SET     description_ar = REPLACE(description_ar, 'داخل المناطق المبنية', 'داخل المنطقة السكنية')
WHERE description_ar LIKE '%داخل المناطق المبنية%';

UPDATE categories
SET     description_ar = REPLACE(description_ar, 'طريق رطب', 'طريق مبلل')
WHERE description_ar LIKE '%طريق رطب%';

UPDATE categories
SET     description_ar = REPLACE(description_ar, 'تروس', 'غيار')
WHERE description_ar LIKE '%تروس%';

UPDATE categories
SET     description_ar = REPLACE(description_ar, 'انبعاث الحصى', 'تطاير الحصى')
WHERE description_ar LIKE '%انبعاث الحصى%';

UPDATE categories
SET     description_ar = REPLACE(description_ar, 'تعرج', 'منعطف')
WHERE description_ar LIKE '%تعرج%';

UPDATE lessons
SET     description_ar = REPLACE(description_ar, 'لافتات المرور', 'العلامات المرورية')
WHERE description_ar LIKE '%لافتات المرور%';

UPDATE lessons
SET     description_ar = REPLACE(description_ar, 'هذه اللافتة', 'هذه العلامة المرورية')
WHERE description_ar LIKE '%هذه اللافتة%';

UPDATE lessons
SET     description_ar = REPLACE(description_ar, 'لافتة المرور', 'العلامة المرورية')
WHERE description_ar LIKE '%لافتة المرور%';

UPDATE lessons
SET     description_ar = REPLACE(description_ar, 'خارج المناطق المبنية', 'خارج المنطقة السكنية')
WHERE description_ar LIKE '%خارج المناطق المبنية%';

UPDATE lessons
SET     description_ar = REPLACE(description_ar, 'داخل المناطق المبنية', 'داخل المنطقة السكنية')
WHERE description_ar LIKE '%داخل المناطق المبنية%';

UPDATE lessons
SET     description_ar = REPLACE(description_ar, 'طريق رطب', 'طريق مبلل')
WHERE description_ar LIKE '%طريق رطب%';

UPDATE lessons
SET     description_ar = REPLACE(description_ar, 'تروس', 'غيار')
WHERE description_ar LIKE '%تروس%';

UPDATE lessons
SET     description_ar = REPLACE(description_ar, 'انبعاث الحصى', 'تطاير الحصى')
WHERE description_ar LIKE '%انبعاث الحصى%';

UPDATE lessons
SET     description_ar = REPLACE(description_ar, 'تعرج', 'منعطف')
WHERE description_ar LIKE '%تعرج%';

UPDATE lesson_pages
SET     title_ar = REPLACE(title_ar, 'لافتات المرور', 'العلامات المرورية'),
    content_ar = REPLACE(content_ar, 'لافتات المرور', 'العلامات المرورية'),
    bullet_points_ar = REPLACE(bullet_points_ar, 'لافتات المرور', 'العلامات المرورية')
WHERE title_ar LIKE '%لافتات المرور%' OR content_ar LIKE '%لافتات المرور%' OR bullet_points_ar LIKE '%لافتات المرور%';

UPDATE lesson_pages
SET     title_ar = REPLACE(title_ar, 'هذه اللافتة', 'هذه العلامة المرورية'),
    content_ar = REPLACE(content_ar, 'هذه اللافتة', 'هذه العلامة المرورية'),
    bullet_points_ar = REPLACE(bullet_points_ar, 'هذه اللافتة', 'هذه العلامة المرورية')
WHERE title_ar LIKE '%هذه اللافتة%' OR content_ar LIKE '%هذه اللافتة%' OR bullet_points_ar LIKE '%هذه اللافتة%';

UPDATE lesson_pages
SET     title_ar = REPLACE(title_ar, 'لافتة المرور', 'العلامة المرورية'),
    content_ar = REPLACE(content_ar, 'لافتة المرور', 'العلامة المرورية'),
    bullet_points_ar = REPLACE(bullet_points_ar, 'لافتة المرور', 'العلامة المرورية')
WHERE title_ar LIKE '%لافتة المرور%' OR content_ar LIKE '%لافتة المرور%' OR bullet_points_ar LIKE '%لافتة المرور%';

UPDATE lesson_pages
SET     title_ar = REPLACE(title_ar, 'خارج المناطق المبنية', 'خارج المنطقة السكنية'),
    content_ar = REPLACE(content_ar, 'خارج المناطق المبنية', 'خارج المنطقة السكنية'),
    bullet_points_ar = REPLACE(bullet_points_ar, 'خارج المناطق المبنية', 'خارج المنطقة السكنية')
WHERE title_ar LIKE '%خارج المناطق المبنية%' OR content_ar LIKE '%خارج المناطق المبنية%' OR bullet_points_ar LIKE '%خارج المناطق المبنية%';

UPDATE lesson_pages
SET     title_ar = REPLACE(title_ar, 'داخل المناطق المبنية', 'داخل المنطقة السكنية'),
    content_ar = REPLACE(content_ar, 'داخل المناطق المبنية', 'داخل المنطقة السكنية'),
    bullet_points_ar = REPLACE(bullet_points_ar, 'داخل المناطق المبنية', 'داخل المنطقة السكنية')
WHERE title_ar LIKE '%داخل المناطق المبنية%' OR content_ar LIKE '%داخل المناطق المبنية%' OR bullet_points_ar LIKE '%داخل المناطق المبنية%';

UPDATE lesson_pages
SET     title_ar = REPLACE(title_ar, 'طريق رطب', 'طريق مبلل'),
    content_ar = REPLACE(content_ar, 'طريق رطب', 'طريق مبلل'),
    bullet_points_ar = REPLACE(bullet_points_ar, 'طريق رطب', 'طريق مبلل')
WHERE title_ar LIKE '%طريق رطب%' OR content_ar LIKE '%طريق رطب%' OR bullet_points_ar LIKE '%طريق رطب%';

UPDATE lesson_pages
SET     title_ar = REPLACE(title_ar, 'تروس', 'غيار'),
    content_ar = REPLACE(content_ar, 'تروس', 'غيار'),
    bullet_points_ar = REPLACE(bullet_points_ar, 'تروس', 'غيار')
WHERE title_ar LIKE '%تروس%' OR content_ar LIKE '%تروس%' OR bullet_points_ar LIKE '%تروس%';

UPDATE lesson_pages
SET     title_ar = REPLACE(title_ar, 'انبعاث الحصى', 'تطاير الحصى'),
    content_ar = REPLACE(content_ar, 'انبعاث الحصى', 'تطاير الحصى'),
    bullet_points_ar = REPLACE(bullet_points_ar, 'انبعاث الحصى', 'تطاير الحصى')
WHERE title_ar LIKE '%انبعاث الحصى%' OR content_ar LIKE '%انبعاث الحصى%' OR bullet_points_ar LIKE '%انبعاث الحصى%';

UPDATE lesson_pages
SET     title_ar = REPLACE(title_ar, 'تعرج', 'منعطف'),
    content_ar = REPLACE(content_ar, 'تعرج', 'منعطف'),
    bullet_points_ar = REPLACE(bullet_points_ar, 'تعرج', 'منعطف')
WHERE title_ar LIKE '%تعرج%' OR content_ar LIKE '%تعرج%' OR bullet_points_ar LIKE '%تعرج%';

UPDATE traffic_rules
SET     title_ar = REPLACE(title_ar, 'لافتات المرور', 'العلامات المرورية'),
    content_ar = REPLACE(content_ar, 'لافتات المرور', 'العلامات المرورية'),
    penalty_info_ar = REPLACE(penalty_info_ar, 'لافتات المرور', 'العلامات المرورية')
WHERE title_ar LIKE '%لافتات المرور%' OR content_ar LIKE '%لافتات المرور%' OR penalty_info_ar LIKE '%لافتات المرور%';

UPDATE traffic_rules
SET     title_ar = REPLACE(title_ar, 'هذه اللافتة', 'هذه العلامة المرورية'),
    content_ar = REPLACE(content_ar, 'هذه اللافتة', 'هذه العلامة المرورية'),
    penalty_info_ar = REPLACE(penalty_info_ar, 'هذه اللافتة', 'هذه العلامة المرورية')
WHERE title_ar LIKE '%هذه اللافتة%' OR content_ar LIKE '%هذه اللافتة%' OR penalty_info_ar LIKE '%هذه اللافتة%';

UPDATE traffic_rules
SET     title_ar = REPLACE(title_ar, 'لافتة المرور', 'العلامة المرورية'),
    content_ar = REPLACE(content_ar, 'لافتة المرور', 'العلامة المرورية'),
    penalty_info_ar = REPLACE(penalty_info_ar, 'لافتة المرور', 'العلامة المرورية')
WHERE title_ar LIKE '%لافتة المرور%' OR content_ar LIKE '%لافتة المرور%' OR penalty_info_ar LIKE '%لافتة المرور%';

UPDATE traffic_rules
SET     title_ar = REPLACE(title_ar, 'خارج المناطق المبنية', 'خارج المنطقة السكنية'),
    content_ar = REPLACE(content_ar, 'خارج المناطق المبنية', 'خارج المنطقة السكنية'),
    penalty_info_ar = REPLACE(penalty_info_ar, 'خارج المناطق المبنية', 'خارج المنطقة السكنية')
WHERE title_ar LIKE '%خارج المناطق المبنية%' OR content_ar LIKE '%خارج المناطق المبنية%' OR penalty_info_ar LIKE '%خارج المناطق المبنية%';

UPDATE traffic_rules
SET     title_ar = REPLACE(title_ar, 'داخل المناطق المبنية', 'داخل المنطقة السكنية'),
    content_ar = REPLACE(content_ar, 'داخل المناطق المبنية', 'داخل المنطقة السكنية'),
    penalty_info_ar = REPLACE(penalty_info_ar, 'داخل المناطق المبنية', 'داخل المنطقة السكنية')
WHERE title_ar LIKE '%داخل المناطق المبنية%' OR content_ar LIKE '%داخل المناطق المبنية%' OR penalty_info_ar LIKE '%داخل المناطق المبنية%';

UPDATE traffic_rules
SET     title_ar = REPLACE(title_ar, 'طريق رطب', 'طريق مبلل'),
    content_ar = REPLACE(content_ar, 'طريق رطب', 'طريق مبلل'),
    penalty_info_ar = REPLACE(penalty_info_ar, 'طريق رطب', 'طريق مبلل')
WHERE title_ar LIKE '%طريق رطب%' OR content_ar LIKE '%طريق رطب%' OR penalty_info_ar LIKE '%طريق رطب%';

UPDATE traffic_rules
SET     title_ar = REPLACE(title_ar, 'تروس', 'غيار'),
    content_ar = REPLACE(content_ar, 'تروس', 'غيار'),
    penalty_info_ar = REPLACE(penalty_info_ar, 'تروس', 'غيار')
WHERE title_ar LIKE '%تروس%' OR content_ar LIKE '%تروس%' OR penalty_info_ar LIKE '%تروس%';

UPDATE traffic_rules
SET     title_ar = REPLACE(title_ar, 'انبعاث الحصى', 'تطاير الحصى'),
    content_ar = REPLACE(content_ar, 'انبعاث الحصى', 'تطاير الحصى'),
    penalty_info_ar = REPLACE(penalty_info_ar, 'انبعاث الحصى', 'تطاير الحصى')
WHERE title_ar LIKE '%انبعاث الحصى%' OR content_ar LIKE '%انبعاث الحصى%' OR penalty_info_ar LIKE '%انبعاث الحصى%';

UPDATE traffic_rules
SET     title_ar = REPLACE(title_ar, 'تعرج', 'منعطف'),
    content_ar = REPLACE(content_ar, 'تعرج', 'منعطف'),
    penalty_info_ar = REPLACE(penalty_info_ar, 'تعرج', 'منعطف')
WHERE title_ar LIKE '%تعرج%' OR content_ar LIKE '%تعرج%' OR penalty_info_ar LIKE '%تعرج%';

-- Standardize learner-facing English prompts

UPDATE sign_questions
SET     question_en = REPLACE(question_en, 'What does this sign mean?', 'What does this traffic sign mean?'),
    explanation_en = REPLACE(explanation_en, 'What does this sign mean?', 'What does this traffic sign mean?')
WHERE question_en LIKE '%What does this sign mean?%' OR explanation_en LIKE '%What does this sign mean?%';

UPDATE sign_questions
SET     question_en = REPLACE(question_en, 'What does this road sign mean?', 'What does this traffic sign mean?'),
    explanation_en = REPLACE(explanation_en, 'What does this road sign mean?', 'What does this traffic sign mean?')
WHERE question_en LIKE '%What does this road sign mean?%' OR explanation_en LIKE '%What does this road sign mean?%';

UPDATE sign_questions
SET     question_en = REPLACE(question_en, 'What hazard does this sign announce?', 'What danger does this traffic sign indicate?'),
    explanation_en = REPLACE(explanation_en, 'What hazard does this sign announce?', 'What danger does this traffic sign indicate?')
WHERE question_en LIKE '%What hazard does this sign announce?%' OR explanation_en LIKE '%What hazard does this sign announce?%';

UPDATE sign_questions
SET     question_en = REPLACE(question_en, 'What should you do when you see this sign?', 'What should you do when you see this traffic sign?'),
    explanation_en = REPLACE(explanation_en, 'What should you do when you see this sign?', 'What should you do when you see this traffic sign?')
WHERE question_en LIKE '%What should you do when you see this sign?%' OR explanation_en LIKE '%What should you do when you see this sign?%';

UPDATE sign_questions
SET     question_en = REPLACE(question_en, 'To which category of traffic signs does this sign belong?', 'To which category does this traffic sign belong?'),
    explanation_en = REPLACE(explanation_en, 'To which category of traffic signs does this sign belong?', 'To which category does this traffic sign belong?')
WHERE question_en LIKE '%To which category of traffic signs does this sign belong?%' OR explanation_en LIKE '%To which category of traffic signs does this sign belong?%';

UPDATE quiz_questions
SET     question_en = REPLACE(question_en, 'What does this sign mean?', 'What does this traffic sign mean?'),
    explanation_en = REPLACE(explanation_en, 'What does this sign mean?', 'What does this traffic sign mean?'),
    error_explanation_en = REPLACE(error_explanation_en, 'What does this sign mean?', 'What does this traffic sign mean?')
WHERE question_en LIKE '%What does this sign mean?%' OR explanation_en LIKE '%What does this sign mean?%' OR error_explanation_en LIKE '%What does this sign mean?%';

UPDATE quiz_questions
SET     question_en = REPLACE(question_en, 'What does this road sign mean?', 'What does this traffic sign mean?'),
    explanation_en = REPLACE(explanation_en, 'What does this road sign mean?', 'What does this traffic sign mean?'),
    error_explanation_en = REPLACE(error_explanation_en, 'What does this road sign mean?', 'What does this traffic sign mean?')
WHERE question_en LIKE '%What does this road sign mean?%' OR explanation_en LIKE '%What does this road sign mean?%' OR error_explanation_en LIKE '%What does this road sign mean?%';

UPDATE quiz_questions
SET     question_en = REPLACE(question_en, 'What hazard does this sign announce?', 'What danger does this traffic sign indicate?'),
    explanation_en = REPLACE(explanation_en, 'What hazard does this sign announce?', 'What danger does this traffic sign indicate?'),
    error_explanation_en = REPLACE(error_explanation_en, 'What hazard does this sign announce?', 'What danger does this traffic sign indicate?')
WHERE question_en LIKE '%What hazard does this sign announce?%' OR explanation_en LIKE '%What hazard does this sign announce?%' OR error_explanation_en LIKE '%What hazard does this sign announce?%';

UPDATE quiz_questions
SET     question_en = REPLACE(question_en, 'What should you do when you see this sign?', 'What should you do when you see this traffic sign?'),
    explanation_en = REPLACE(explanation_en, 'What should you do when you see this sign?', 'What should you do when you see this traffic sign?'),
    error_explanation_en = REPLACE(error_explanation_en, 'What should you do when you see this sign?', 'What should you do when you see this traffic sign?')
WHERE question_en LIKE '%What should you do when you see this sign?%' OR explanation_en LIKE '%What should you do when you see this sign?%' OR error_explanation_en LIKE '%What should you do when you see this sign?%';

UPDATE quiz_questions
SET     question_en = REPLACE(question_en, 'To which category of traffic signs does this sign belong?', 'To which category does this traffic sign belong?'),
    explanation_en = REPLACE(explanation_en, 'To which category of traffic signs does this sign belong?', 'To which category does this traffic sign belong?'),
    error_explanation_en = REPLACE(error_explanation_en, 'To which category of traffic signs does this sign belong?', 'To which category does this traffic sign belong?')
WHERE question_en LIKE '%To which category of traffic signs does this sign belong?%' OR explanation_en LIKE '%To which category of traffic signs does this sign belong?%' OR error_explanation_en LIKE '%To which category of traffic signs does this sign belong?%';

-- Standardize learner-facing Dutch prompts

UPDATE sign_questions
SET     question_nl = REPLACE(question_nl, 'Wat betekent dit bord?', 'Wat betekent dit verkeersbord?'),
    explanation_nl = REPLACE(explanation_nl, 'Wat betekent dit bord?', 'Wat betekent dit verkeersbord?')
WHERE question_nl LIKE '%Wat betekent dit bord?%' OR explanation_nl LIKE '%Wat betekent dit bord?%';

UPDATE sign_questions
SET     question_nl = REPLACE(question_nl, 'Welk gevaar kondigt dit bord aan?', 'Welk gevaar duidt dit verkeersbord aan?'),
    explanation_nl = REPLACE(explanation_nl, 'Welk gevaar kondigt dit bord aan?', 'Welk gevaar duidt dit verkeersbord aan?')
WHERE question_nl LIKE '%Welk gevaar kondigt dit bord aan?%' OR explanation_nl LIKE '%Welk gevaar kondigt dit bord aan?%';

UPDATE sign_questions
SET     question_nl = REPLACE(question_nl, 'Wat moet u doen wanneer u dit bord ziet?', 'Wat moet u doen wanneer u dit verkeersbord ziet?'),
    explanation_nl = REPLACE(explanation_nl, 'Wat moet u doen wanneer u dit bord ziet?', 'Wat moet u doen wanneer u dit verkeersbord ziet?')
WHERE question_nl LIKE '%Wat moet u doen wanneer u dit bord ziet?%' OR explanation_nl LIKE '%Wat moet u doen wanneer u dit bord ziet?%';

UPDATE quiz_questions
SET     question_nl = REPLACE(question_nl, 'Wat betekent dit bord?', 'Wat betekent dit verkeersbord?'),
    explanation_nl = REPLACE(explanation_nl, 'Wat betekent dit bord?', 'Wat betekent dit verkeersbord?'),
    error_explanation_nl = REPLACE(error_explanation_nl, 'Wat betekent dit bord?', 'Wat betekent dit verkeersbord?')
WHERE question_nl LIKE '%Wat betekent dit bord?%' OR explanation_nl LIKE '%Wat betekent dit bord?%' OR error_explanation_nl LIKE '%Wat betekent dit bord?%';

UPDATE quiz_questions
SET     question_nl = REPLACE(question_nl, 'Welk gevaar kondigt dit bord aan?', 'Welk gevaar duidt dit verkeersbord aan?'),
    explanation_nl = REPLACE(explanation_nl, 'Welk gevaar kondigt dit bord aan?', 'Welk gevaar duidt dit verkeersbord aan?'),
    error_explanation_nl = REPLACE(error_explanation_nl, 'Welk gevaar kondigt dit bord aan?', 'Welk gevaar duidt dit verkeersbord aan?')
WHERE question_nl LIKE '%Welk gevaar kondigt dit bord aan?%' OR explanation_nl LIKE '%Welk gevaar kondigt dit bord aan?%' OR error_explanation_nl LIKE '%Welk gevaar kondigt dit bord aan?%';

UPDATE quiz_questions
SET     question_nl = REPLACE(question_nl, 'Wat moet u doen wanneer u dit bord ziet?', 'Wat moet u doen wanneer u dit verkeersbord ziet?'),
    explanation_nl = REPLACE(explanation_nl, 'Wat moet u doen wanneer u dit bord ziet?', 'Wat moet u doen wanneer u dit verkeersbord ziet?'),
    error_explanation_nl = REPLACE(error_explanation_nl, 'Wat moet u doen wanneer u dit bord ziet?', 'Wat moet u doen wanneer u dit verkeersbord ziet?')
WHERE question_nl LIKE '%Wat moet u doen wanneer u dit bord ziet?%' OR explanation_nl LIKE '%Wat moet u doen wanneer u dit bord ziet?%' OR error_explanation_nl LIKE '%Wat moet u doen wanneer u dit bord ziet?%';

-- Standardize learner-facing French prompts

UPDATE sign_questions
SET     question_fr = REPLACE(question_fr, 'Que signifie ce panneau ?', 'Que signifie ce panneau de signalisation ?'),
    explanation_fr = REPLACE(explanation_fr, 'Que signifie ce panneau ?', 'Que signifie ce panneau de signalisation ?')
WHERE question_fr LIKE '%Que signifie ce panneau ?%' OR explanation_fr LIKE '%Que signifie ce panneau ?%';

UPDATE sign_questions
SET     question_fr = REPLACE(question_fr, 'Quel danger ce panneau annonce-t-il ?', 'Quel danger indique ce panneau de signalisation ?'),
    explanation_fr = REPLACE(explanation_fr, 'Quel danger ce panneau annonce-t-il ?', 'Quel danger indique ce panneau de signalisation ?')
WHERE question_fr LIKE '%Quel danger ce panneau annonce-t-il ?%' OR explanation_fr LIKE '%Quel danger ce panneau annonce-t-il ?%';

UPDATE sign_questions
SET     question_fr = REPLACE(question_fr, 'Que devez-vous faire lorsque vous voyez ce panneau ?', 'Que devez-vous faire lorsque vous voyez ce panneau de signalisation ?'),
    explanation_fr = REPLACE(explanation_fr, 'Que devez-vous faire lorsque vous voyez ce panneau ?', 'Que devez-vous faire lorsque vous voyez ce panneau de signalisation ?')
WHERE question_fr LIKE '%Que devez-vous faire lorsque vous voyez ce panneau ?%' OR explanation_fr LIKE '%Que devez-vous faire lorsque vous voyez ce panneau ?%';

UPDATE sign_questions
SET     question_fr = REPLACE(question_fr, 'À quelle catégorie de panneaux de signalisation ce panneau appartient-il ?', 'À quelle catégorie appartient ce panneau de signalisation ?'),
    explanation_fr = REPLACE(explanation_fr, 'À quelle catégorie de panneaux de signalisation ce panneau appartient-il ?', 'À quelle catégorie appartient ce panneau de signalisation ?')
WHERE question_fr LIKE '%À quelle catégorie de panneaux de signalisation ce panneau appartient-il ?%' OR explanation_fr LIKE '%À quelle catégorie de panneaux de signalisation ce panneau appartient-il ?%';

UPDATE quiz_questions
SET     question_fr = REPLACE(question_fr, 'Que signifie ce panneau ?', 'Que signifie ce panneau de signalisation ?'),
    explanation_fr = REPLACE(explanation_fr, 'Que signifie ce panneau ?', 'Que signifie ce panneau de signalisation ?'),
    error_explanation_fr = REPLACE(error_explanation_fr, 'Que signifie ce panneau ?', 'Que signifie ce panneau de signalisation ?')
WHERE question_fr LIKE '%Que signifie ce panneau ?%' OR explanation_fr LIKE '%Que signifie ce panneau ?%' OR error_explanation_fr LIKE '%Que signifie ce panneau ?%';

UPDATE quiz_questions
SET     question_fr = REPLACE(question_fr, 'Quel danger ce panneau annonce-t-il ?', 'Quel danger indique ce panneau de signalisation ?'),
    explanation_fr = REPLACE(explanation_fr, 'Quel danger ce panneau annonce-t-il ?', 'Quel danger indique ce panneau de signalisation ?'),
    error_explanation_fr = REPLACE(error_explanation_fr, 'Quel danger ce panneau annonce-t-il ?', 'Quel danger indique ce panneau de signalisation ?')
WHERE question_fr LIKE '%Quel danger ce panneau annonce-t-il ?%' OR explanation_fr LIKE '%Quel danger ce panneau annonce-t-il ?%' OR error_explanation_fr LIKE '%Quel danger ce panneau annonce-t-il ?%';

UPDATE quiz_questions
SET     question_fr = REPLACE(question_fr, 'Que devez-vous faire lorsque vous voyez ce panneau ?', 'Que devez-vous faire lorsque vous voyez ce panneau de signalisation ?'),
    explanation_fr = REPLACE(explanation_fr, 'Que devez-vous faire lorsque vous voyez ce panneau ?', 'Que devez-vous faire lorsque vous voyez ce panneau de signalisation ?'),
    error_explanation_fr = REPLACE(error_explanation_fr, 'Que devez-vous faire lorsque vous voyez ce panneau ?', 'Que devez-vous faire lorsque vous voyez ce panneau de signalisation ?')
WHERE question_fr LIKE '%Que devez-vous faire lorsque vous voyez ce panneau ?%' OR explanation_fr LIKE '%Que devez-vous faire lorsque vous voyez ce panneau ?%' OR error_explanation_fr LIKE '%Que devez-vous faire lorsque vous voyez ce panneau ?%';

UPDATE quiz_questions
SET     question_fr = REPLACE(question_fr, 'À quelle catégorie de panneaux de signalisation ce panneau appartient-il ?', 'À quelle catégorie appartient ce panneau de signalisation ?'),
    explanation_fr = REPLACE(explanation_fr, 'À quelle catégorie de panneaux de signalisation ce panneau appartient-il ?', 'À quelle catégorie appartient ce panneau de signalisation ?'),
    error_explanation_fr = REPLACE(error_explanation_fr, 'À quelle catégorie de panneaux de signalisation ce panneau appartient-il ?', 'À quelle catégorie appartient ce panneau de signalisation ?')
WHERE question_fr LIKE '%À quelle catégorie de panneaux de signalisation ce panneau appartient-il ?%' OR explanation_fr LIKE '%À quelle catégorie de panneaux de signalisation ce panneau appartient-il ?%' OR error_explanation_fr LIKE '%À quelle catégorie de panneaux de signalisation ce panneau appartient-il ?%';

