CREATE TEMPORARY TABLE official_article_keyword_map (
    topic_key VARCHAR(64) PRIMARY KEY,
    primary_query TEXT NOT NULL,
    target_queries TEXT[] NOT NULL
) ON COMMIT DROP;

INSERT INTO official_article_keyword_map (topic_key, primary_query, target_queries)
VALUES
    ('OFFICIAL-001', 'امتحان السياقة النظري في بلجيكا', ARRAY[
        'امتحان السياقة النظري في بلجيكا',
        'دليل امتحان السياقة النظري بلجيكا',
        'النجاح في امتحان النظري البلجيكي'
    ]),
    ('OFFICIAL-002', 'كم عدد أسئلة امتحان السياقة النظري في بلجيكا', ARRAY[
        'كم عدد أسئلة امتحان السياقة النظري في بلجيكا',
        'عدد أسئلة امتحان السياقة النظري بلجيكا',
        'امتحان النظري بلجيكا 50 سؤال',
        'كم سؤال في امتحان رخصة B بلجيكا'
    ]),
    ('OFFICIAL-003', 'نقطة النجاح في امتحان السياقة النظري بلجيكا', ARRAY[
        'نقطة النجاح في امتحان السياقة النظري بلجيكا',
        'علامة النجاح في امتحان النظري البلجيكي',
        'كم نقطة للنجاح في امتحان رخصة B بلجيكا'
    ]),
    ('OFFICIAL-004', 'الأخطاء الخطيرة في امتحان السياقة النظري', ARRAY[
        'الأخطاء الخطيرة في امتحان السياقة النظري',
        'حساب الأخطاء في امتحان النظري بلجيكا',
        'أخطاء امتحان رخصة B البلجيكي'
    ]),
    ('OFFICIAL-005', 'الرسوب مرتين في امتحان السياقة النظري بلجيكا', ARRAY[
        'الرسوب مرتين في امتحان السياقة النظري بلجيكا',
        'ماذا بعد الرسوب مرتين في امتحان النظري',
        'إجراءات الرسوب مرتين في امتحان النظري بلجيكا'
    ]),
    ('OFFICIAL-006', 'امتحان السياقة النظري بالعربية في بلجيكا', ARRAY[
        'امتحان السياقة النظري بالعربية في بلجيكا',
        'امتحان النظري بلجيكا باللغة العربية',
        'مترجم عربي امتحان رخصة B بلجيكا'
    ]),
    ('OFFICIAL-007', 'حجز موعد امتحان السياقة النظري بلجيكا', ARRAY[
        'حجز موعد امتحان السياقة النظري بلجيكا',
        'موعد امتحان النظري رخصة B',
        'كيفية حجز امتحان النظري في بلجيكا'
    ]),
    ('OFFICIAL-008', 'وثائق امتحان السياقة النظري بلجيكا', ARRAY[
        'وثائق امتحان السياقة النظري بلجيكا',
        'الأوراق المطلوبة لامتحان النظري',
        'ماذا أحضر يوم امتحان رخصة B النظري'
    ]),
    ('OFFICIAL-009', 'الحصول على رخصة السياقة في بلجيكا', ARRAY[
        'الحصول على رخصة السياقة في بلجيكا',
        'خطوات رخصة السياقة البلجيكية',
        'رخصة B في بلجيكا خطوة بخطوة'
    ]),
    ('OFFICIAL-010', 'الفرق بين الرخصة المؤقتة مع مرافق وبدون مرافق', ARRAY[
        'الفرق بين الرخصة المؤقتة مع مرافق وبدون مرافق',
        'رخصة سياقة مؤقتة مع مرافق بلجيكا',
        'رخصة سياقة مؤقتة بدون مرافق بلجيكا'
    ]),
    ('OFFICIAL-011', 'مدة صلاحية امتحان النظري في بلجيكا', ARRAY[
        'مدة صلاحية امتحان النظري في بلجيكا',
        'صلاحية شهادة امتحان السياقة النظري',
        'متى تنتهي صلاحية امتحان النظري بلجيكا'
    ]),
    ('OFFICIAL-012', 'تكلفة رخصة السياقة في بلجيكا', ARRAY[
        'تكلفة رخصة السياقة في بلجيكا',
        'كم سعر رخصة B في بلجيكا',
        'مصاريف رخصة السياقة البلجيكية'
    ]),
    ('OFFICIAL-013', 'قيادة سيارة أوتوماتيك بالرخصة المؤقتة', ARRAY[
        'قيادة سيارة أوتوماتيك بالرخصة المؤقتة',
        'الرخصة المؤقتة والسيارة الأوتوماتيك بلجيكا',
        'تعلم القيادة بسيارة أوتوماتيك في بلجيكا'
    ]),
    ('OFFICIAL-014', 'فترة التدريب الإلزامية قبل الامتحان العملي', ARRAY[
        'فترة التدريب الإلزامية قبل الامتحان العملي',
        'مدة التدريب قبل امتحان السياقة العملي بلجيكا',
        'شروط التدريب للامتحان العملي رخصة B'
    ]),
    ('OFFICIAL-015', 'الأولوية للقادم من اليمين في بلجيكا', ARRAY[
        'الأولوية للقادم من اليمين في بلجيكا',
        'قاعدة الأولوية لليمين',
        'شرح أولوية اليمين مع أمثلة'
    ]),
    ('OFFICIAL-016', 'متى لا تطبق قاعدة الأولوية لليمين', ARRAY[
        'متى لا تطبق قاعدة الأولوية لليمين',
        'استثناءات أولوية اليمين في بلجيكا',
        'حالات عدم تطبيق الأولوية لليمين'
    ]),
    ('OFFICIAL-017', 'علامة الطريق ذي الأولوية', ARRAY[
        'علامة الطريق ذي الأولوية',
        'علامة نهاية طريق الأولوية',
        'شرح إشارات طريق الأولوية في بلجيكا'
    ]),
    ('OFFICIAL-018', 'الأولوية في الدوار في بلجيكا', ARRAY[
        'الأولوية في الدوار في بلجيكا',
        'من له الأولوية داخل الدوار',
        'قواعد المرور في الدوار البلجيكي'
    ]),
    ('OFFICIAL-019', 'أسنان القرش في الطريق', ARRAY[
        'أسنان القرش في الطريق',
        'علامات أسنان القرش والأولوية',
        'ماذا تعني أسنان القرش في بلجيكا'
    ]),
    ('OFFICIAL-020', 'الاندماج بالتناوب عند الازدحام', ARRAY[
        'الاندماج بالتناوب عند الازدحام',
        'نظام السحاب في المرور بلجيكا',
        'قواعد الاندماج بالتناوب في بلجيكا'
    ]),
    ('OFFICIAL-021', 'حدود السرعة في بلجيكا', ARRAY[
        'حدود السرعة في بلجيكا',
        'سرعة الطرق حسب الإقليم في بلجيكا',
        'جدول السرعات في بلجيكا'
    ]),
    ('OFFICIAL-022', 'الفرق بين ممنوع الوقوف وممنوع التوقف', ARRAY[
        'الفرق بين ممنوع الوقوف وممنوع التوقف',
        'علامة ممنوع الوقوف بلجيكا',
        'علامة ممنوع التوقف بلجيكا'
    ]),
    ('OFFICIAL-023', 'الوقوف أمام مداخل المنازل في بلجيكا', ARRAY[
        'الوقوف أمام مداخل المنازل في بلجيكا',
        'الوقوف أمام المرائب في بلجيكا',
        'قواعد ركن السيارة أمام مدخل منزل'
    ]),
    ('OFFICIAL-024', 'الوقوف على الرصيف في بلجيكا', ARRAY[
        'الوقوف على الرصيف في بلجيكا',
        'متى يسمح بركن السيارة على الرصيف',
        'قواعد الوقوف فوق الرصيف بلجيكا'
    ]),
    ('OFFICIAL-025', 'القرص الأزرق في بلجيكا', ARRAY[
        'القرص الأزرق في بلجيكا',
        'طريقة استخدام قرص الوقوف الأزرق',
        'غرامة القرص الأزرق في بلجيكا'
    ]),
    ('OFFICIAL-026', 'مسافة الوقوف من التقاطع في بلجيكا', ARRAY[
        'مسافة الوقوف من التقاطع في بلجيكا',
        'مسافة الوقوف من ممر المشاة',
        'الوقوف قرب محطة الحافلات بلجيكا'
    ]),
    ('OFFICIAL-027', 'قواعد منطقة 30 في بلجيكا', ARRAY[
        'قواعد منطقة 30 في بلجيكا',
        'منطقة المدرسة والسرعة 30',
        'علامة منطقة 30 بلجيكا'
    ]),
    ('OFFICIAL-028', 'أنواع العلامات المرورية في بلجيكا', ARRAY[
        'أنواع العلامات المرورية في بلجيكا',
        'تصنيف إشارات المرور البلجيكية',
        'أشكال العلامات المرورية ومعانيها'
    ]),
    ('OFFICIAL-029', 'علامات الخطر في بلجيكا', ARRAY[
        'علامات الخطر في بلجيكا',
        'أشكال علامات الخطر البلجيكية',
        'معاني إشارات الخطر المرورية'
    ]),
    ('OFFICIAL-030', 'علامات المنع في بلجيكا', ARRAY[
        'علامات المنع في بلجيكا',
        'معاني إشارات المنع البلجيكية',
        'دليل مصور لعلامات المنع'
    ]),
    ('OFFICIAL-031', 'العلامات الإجبارية الزرقاء', ARRAY[
        'العلامات الإجبارية الزرقاء',
        'معاني إشارات المرور الإجبارية',
        'ماذا تعني العلامات الزرقاء الدائرية'
    ]),
    ('OFFICIAL-032', 'علامات المناطق المرورية في بلجيكا', ARRAY[
        'علامات المناطق المرورية في بلجيكا',
        'علامة woonerf في بلجيكا',
        'علامات منطقة 30 ومنطقة المشاة'
    ]),
    ('OFFICIAL-033', 'اللوحات التكميلية تحت العلامات', ARRAY[
        'اللوحات التكميلية تحت العلامات',
        'كيفية قراءة اللوحات التكميلية',
        'معاني اللوحات الإضافية لإشارات المرور'
    ]),
    ('OFFICIAL-034', 'العلامات المرورية الجديدة في بلجيكا 2027', ARRAY[
        'العلامات المرورية الجديدة في بلجيكا 2027',
        'قانون الطريق البلجيكي 2027 علامات المرور',
        'إشارات المرور الجديدة بلجيكا'
    ]),
    ('OFFICIAL-035', 'الامتحان العملي لرخصة B في بلجيكا', ARRAY[
        'الامتحان العملي لرخصة B في بلجيكا',
        'ماذا يحدث يوم امتحان السياقة العملي',
        'اختبار القيادة العملي في بلجيكا'
    ]),
    ('OFFICIAL-036', 'اختبار إدراك المخاطر في بلجيكا', ARRAY[
        'اختبار إدراك المخاطر في بلجيكا',
        'كيفية الاستعداد لاختبار إدراك المخاطر',
        'شرح اختبار المخاطر رخصة B'
    ]),
    ('OFFICIAL-037', 'شروط سيارة الامتحان العملي في بلجيكا', ARRAY[
        'شروط سيارة الامتحان العملي في بلجيكا',
        'السيارة المستخدمة في امتحان السياقة العملي',
        'متطلبات سيارة اختبار رخصة B'
    ]),
    ('OFFICIAL-038', 'أسباب الرسوب في الامتحان العملي', ARRAY[
        'أسباب الرسوب في الامتحان العملي',
        'أخطاء امتحان السياقة العملي بلجيكا',
        'كيف أتجنب الرسوب في اختبار القيادة'
    ]),
    ('OFFICIAL-039', 'استخدام GPS في الامتحان العملي فلاندرز', ARRAY[
        'استخدام GPS في الامتحان العملي فلاندرز',
        'GPS في اختبار القيادة العملي بلجيكا',
        'قواعد الملاحة في امتحان السياقة فلاندرز'
    ]),
    ('OFFICIAL-040', 'قائمة مراجعة يوم الامتحان العملي', ARRAY[
        'قائمة مراجعة يوم الامتحان العملي',
        'وثائق وسيارة امتحان السياقة العملي',
        'الاستعداد ليوم اختبار القيادة في بلجيكا'
    ]);

DO $$
BEGIN
    IF (SELECT count(*) FROM official_article_keyword_map) <> 40 THEN
        RAISE EXCEPTION 'Official editorial keyword map must contain exactly 40 topics';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM official_article_keyword_map mapping
        CROSS JOIN LATERAL unnest(mapping.target_queries) AS query(value)
        WHERE btrim(query.value) = '' OR char_length(btrim(query.value)) > 120
    ) THEN
        RAISE EXCEPTION 'Official editorial target queries must be non-empty and at most 120 characters';
    END IF;
END $$;

INSERT INTO article_keyword_clusters (
    cluster_key, primary_query, search_intent, primary_language,
    source_opportunity_id, content_pillar_id, funnel_stage_id, status
)
SELECT
    mapping.topic_key || '-AR',
    mapping.primary_query,
    'INFORMATIONAL',
    'AR',
    topic.source_opportunity_id,
    topic.content_pillar_id,
    topic.funnel_stage_id,
    'ACTIVE'
FROM official_article_keyword_map mapping
JOIN article_topics topic ON topic.topic_key = mapping.topic_key
WHERE topic.source_type = 'OFFICIAL_STRATEGIC_BACKLOG'
ON CONFLICT (cluster_key) DO UPDATE
SET primary_query = EXCLUDED.primary_query,
    search_intent = EXCLUDED.search_intent,
    primary_language = EXCLUDED.primary_language,
    source_opportunity_id = EXCLUDED.source_opportunity_id,
    content_pillar_id = EXCLUDED.content_pillar_id,
    funnel_stage_id = EXCLUDED.funnel_stage_id,
    status = EXCLUDED.status,
    updated_at = CURRENT_TIMESTAMP;

UPDATE article_topics topic
SET target_queries = to_jsonb(mapping.target_queries),
    keyword_cluster_id = cluster.id,
    updated_at = CURRENT_TIMESTAMP
FROM official_article_keyword_map mapping
JOIN article_keyword_clusters cluster
  ON cluster.cluster_key = mapping.topic_key || '-AR'
WHERE topic.topic_key = mapping.topic_key
  AND topic.source_type = 'OFFICIAL_STRATEGIC_BACKLOG';

UPDATE article_briefs brief
SET target_queries = topic.target_queries,
    keyword_cluster_id = topic.keyword_cluster_id,
    updated_at = CURRENT_TIMESTAMP
FROM article_topics topic
WHERE brief.article_topic_id = topic.id
  AND topic.source_type = 'OFFICIAL_STRATEGIC_BACKLOG'
  AND brief.target_language = topic.title_language;

CREATE OR REPLACE FUNCTION editorial_target_queries_are_valid(candidate JSONB)
RETURNS BOOLEAN
LANGUAGE SQL
IMMUTABLE
AS $$
    SELECT candidate IS NOT NULL
       AND jsonb_typeof(candidate) = 'array'
       AND jsonb_array_length(candidate) BETWEEN 1 AND 12
       AND NOT EXISTS (
           SELECT 1
           FROM jsonb_array_elements(candidate) AS entry(value)
           WHERE jsonb_typeof(entry.value) <> 'string'
       )
       AND NOT EXISTS (
           SELECT 1
           FROM jsonb_array_elements_text(candidate) AS query(value)
           WHERE btrim(query.value) = '' OR char_length(btrim(query.value)) > 120
       );
$$;

ALTER TABLE article_topics
    ADD CONSTRAINT chk_official_article_topics_keyword_mapping
    CHECK (
        source_type <> 'OFFICIAL_STRATEGIC_BACKLOG'
        OR (
            keyword_cluster_id IS NOT NULL
            AND editorial_target_queries_are_valid(target_queries)
        )
    );

ALTER TABLE article_briefs
    ADD CONSTRAINT chk_article_briefs_target_queries_valid
    CHECK (editorial_target_queries_are_valid(target_queries));
