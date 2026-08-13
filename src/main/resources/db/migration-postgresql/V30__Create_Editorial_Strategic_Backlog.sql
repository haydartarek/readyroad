CREATE TABLE article_topics (
    id BIGSERIAL PRIMARY KEY,
    topic_key VARCHAR(64) NOT NULL UNIQUE,
    official_backlog_order SMALLINT NOT NULL UNIQUE
        CHECK (official_backlog_order BETWEEN 1 AND 40),
    cluster_order SMALLINT NOT NULL CHECK (cluster_order BETWEEN 1 AND 6),
    cluster_key VARCHAR(64) NOT NULL,
    cluster_name VARCHAR(255) NOT NULL,
    working_title TEXT NOT NULL,
    title_language VARCHAR(8) NOT NULL CHECK (title_language IN ('AR', 'NL', 'FR', 'EN')),
    primary_language VARCHAR(8) CHECK (primary_language IN ('AR', 'NL', 'FR', 'EN')),
    pillar BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(32) NOT NULL CHECK (
        status IN (
            'IDEA', 'PLANNED', 'BRIEF_READY', 'DRAFTING', 'DRAFT_READY',
            'FACT_CHECK_REQUIRED', 'LEGAL_REVIEW_REQUIRED', 'TRANSLATION_REQUIRED',
            'IMAGE_REQUIRED', 'WAITING_APPROVAL', 'APPROVED', 'SCHEDULED',
            'PUBLISHED', 'UPDATE_RECOMMENDED', 'ARCHIVED', 'REJECTED'
        )
    ),
    source_type VARCHAR(40) NOT NULL CHECK (source_type = 'OFFICIAL_STRATEGIC_BACKLOG'),
    article_priority VARCHAR(2) CHECK (article_priority IN ('P0', 'P1', 'P2', 'P3')),
    priority_reason TEXT,
    source_opportunity_id BIGINT REFERENCES seo_opportunities(id) ON DELETE SET NULL,
    target_queries JSONB NOT NULL DEFAULT '[]'::jsonb,
    content_pillar_id BIGINT REFERENCES marketing_content_pillars(id) ON DELETE SET NULL,
    funnel_stage_id BIGINT REFERENCES marketing_funnel_stages(id) ON DELETE SET NULL,
    conversion_goal_id BIGINT REFERENCES marketing_conversion_goals(id) ON DELETE SET NULL,
    supporting_pages JSONB NOT NULL DEFAULT '[]'::jsonb,
    internal_link_targets JSONB NOT NULL DEFAULT '[]'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_article_topics_working_title UNIQUE (title_language, working_title),
    CONSTRAINT chk_article_topics_target_queries_array
        CHECK (jsonb_typeof(target_queries) = 'array'),
    CONSTRAINT chk_article_topics_supporting_pages_array
        CHECK (jsonb_typeof(supporting_pages) = 'array'),
    CONSTRAINT chk_article_topics_internal_links_array
        CHECK (jsonb_typeof(internal_link_targets) = 'array')
);

CREATE INDEX idx_article_topics_cluster_order
    ON article_topics (cluster_order, official_backlog_order);

INSERT INTO article_topics (
    topic_key, official_backlog_order, cluster_order, cluster_key, cluster_name,
    working_title, title_language, pillar, status, source_type
)
VALUES
    ('OFFICIAL-001', 1, 1, 'THEORY_EXAM', 'امتحان السياقة النظري', 'امتحان السياقة النظري في بلجيكا: الدليل الكامل للنجاح', 'AR', TRUE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-002', 2, 1, 'THEORY_EXAM', 'امتحان السياقة النظري', 'كم عدد أسئلة امتحان السياقة النظري في بلجيكا؟', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-003', 3, 1, 'THEORY_EXAM', 'امتحان السياقة النظري', 'كم نقطة تحتاج للنجاح في امتحان السياقة النظري البلجيكي؟', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-004', 4, 1, 'THEORY_EXAM', 'امتحان السياقة النظري', 'كيف تُحسب الأخطاء الخطيرة في امتحان السياقة النظري؟', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-005', 5, 1, 'THEORY_EXAM', 'امتحان السياقة النظري', 'ماذا يحدث بعد الرسوب مرتين في امتحان السياقة النظري؟', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-006', 6, 1, 'THEORY_EXAM', 'امتحان السياقة النظري', 'هل يمكن تقديم امتحان السياقة النظري باللغة العربية في بلجيكا؟', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-007', 7, 1, 'THEORY_EXAM', 'امتحان السياقة النظري', 'كيف تحجز موعد امتحان السياقة النظري في بلجيكا؟', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-008', 8, 1, 'THEORY_EXAM', 'امتحان السياقة النظري', 'ما الوثائق المطلوبة يوم امتحان السياقة النظري؟', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-009', 9, 2, 'BELGIAN_LICENSE', 'رخصة السياقة البلجيكية', 'كيفية الحصول على رخصة السياقة البلجيكية خطوة بخطوة', 'AR', TRUE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-010', 10, 2, 'BELGIAN_LICENSE', 'رخصة السياقة البلجيكية', 'ما الفرق بين الرخصة المؤقتة مع مرافق وبدون مرافق؟', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-011', 11, 2, 'BELGIAN_LICENSE', 'رخصة السياقة البلجيكية', 'كم مدة صلاحية امتحان النظري في بلجيكا؟', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-012', 12, 2, 'BELGIAN_LICENSE', 'رخصة السياقة البلجيكية', 'كم تكلف رخصة السياقة في بلجيكا؟', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-013', 13, 2, 'BELGIAN_LICENSE', 'رخصة السياقة البلجيكية', 'هل يمكن قيادة سيارة أوتوماتيك بالرخصة المؤقتة؟', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-014', 14, 2, 'BELGIAN_LICENSE', 'رخصة السياقة البلجيكية', 'ما فترة التدريب الإلزامية قبل الامتحان العملي؟', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-015', 15, 3, 'PRIORITY_INTERSECTIONS', 'الأولوية والتقاطعات', 'الأولوية للقادم من اليمين في بلجيكا: شرح مع أمثلة', 'AR', TRUE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-016', 16, 3, 'PRIORITY_INTERSECTIONS', 'الأولوية والتقاطعات', 'متى لا تطبق قاعدة الأولوية لليمين؟', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-017', 17, 3, 'PRIORITY_INTERSECTIONS', 'الأولوية والتقاطعات', 'شرح علامات الطريق ذي الأولوية ونهاية طريق الأولوية', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-018', 18, 3, 'PRIORITY_INTERSECTIONS', 'الأولوية والتقاطعات', 'من له الأولوية في الدوار في بلجيكا؟', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-019', 19, 3, 'PRIORITY_INTERSECTIONS', 'الأولوية والتقاطعات', 'أسنان القرش في الطريق: ماذا تعني ومن له الأولوية؟', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-020', 20, 3, 'PRIORITY_INTERSECTIONS', 'الأولوية والتقاطعات', 'كيف يعمل نظام الاندماج بالتناوب عند الازدحام؟', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-021', 21, 4, 'SPEED_PARKING_STOPPING', 'السرعة والوقوف والتوقف', 'حدود السرعة في بلجيكا حسب الإقليم والطريق', 'AR', TRUE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-022', 22, 4, 'SPEED_PARKING_STOPPING', 'السرعة والوقوف والتوقف', 'الفرق بين ممنوع الوقوف وممنوع التوقف', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-023', 23, 4, 'SPEED_PARKING_STOPPING', 'السرعة والوقوف والتوقف', 'قواعد الوقوف أمام مداخل المنازل والمرائب في بلجيكا', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-024', 24, 4, 'SPEED_PARKING_STOPPING', 'السرعة والوقوف والتوقف', 'متى يسمح بالوقوف على الرصيف في بلجيكا؟', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-025', 25, 4, 'SPEED_PARKING_STOPPING', 'السرعة والوقوف والتوقف', 'القرص الأزرق في بلجيكا: طريقة الاستخدام والغرامات', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-026', 26, 4, 'SPEED_PARKING_STOPPING', 'السرعة والوقوف والتوقف', 'مسافة الوقوف من التقاطع وممر المشاة ومحطة الحافلات', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-027', 27, 4, 'SPEED_PARKING_STOPPING', 'السرعة والوقوف والتوقف', 'قواعد منطقة 30 ومنطقة المدرسة في بلجيكا', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-028', 28, 5, 'TRAFFIC_SIGNS', 'العلامات المرورية', 'أنواع العلامات المرورية في بلجيكا وكيف تميز بينها', 'AR', TRUE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-029', 29, 5, 'TRAFFIC_SIGNS', 'العلامات المرورية', 'علامات الخطر البلجيكية: الأشكال والمعاني الأكثر ورودًا', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-030', 30, 5, 'TRAFFIC_SIGNS', 'العلامات المرورية', 'علامات المنع في بلجيكا: دليل مصور', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-031', 31, 5, 'TRAFFIC_SIGNS', 'العلامات المرورية', 'العلامات الإجبارية الزرقاء: ماذا يجب أن يفعل السائق؟', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-032', 32, 5, 'TRAFFIC_SIGNS', 'العلامات المرورية', 'علامات المناطق المرورية: منطقة 30 وwoonerf ومنطقة المشاة', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-033', 33, 5, 'TRAFFIC_SIGNS', 'العلامات المرورية', 'اللوحات التكميلية تحت العلامات: كيف تقرأها؟', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-034', 34, 5, 'TRAFFIC_SIGNS', 'العلامات المرورية', 'العلامات المرورية الجديدة في قانون الطريق البلجيكي 2027', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-035', 35, 6, 'PRACTICAL_EXAM', 'الامتحان العملي', 'الامتحان العملي لرخصة B في بلجيكا: ماذا يحدث يوم الامتحان؟', 'AR', TRUE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-036', 36, 6, 'PRACTICAL_EXAM', 'الامتحان العملي', 'اختبار إدراك المخاطر: كيف يعمل وكيف تستعد له؟', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-037', 37, 6, 'PRACTICAL_EXAM', 'الامتحان العملي', 'شروط السيارة المستخدمة في الامتحان العملي', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-038', 38, 6, 'PRACTICAL_EXAM', 'الامتحان العملي', 'أكثر أسباب الرسوب في الامتحان العملي وكيف تتجنبها', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-039', 39, 6, 'PRACTICAL_EXAM', 'الامتحان العملي', 'استخدام GPS أثناء الامتحان العملي في فلاندرز', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG'),
    ('OFFICIAL-040', 40, 6, 'PRACTICAL_EXAM', 'الامتحان العملي', 'قائمة مراجعة يوم الامتحان العملي: الوثائق والسيارة والاستعداد', 'AR', FALSE, 'PLANNED', 'OFFICIAL_STRATEGIC_BACKLOG');
