-- ==========================================================================
-- V183 — Developer Skills Assessment System — Seed: Categories & Settings
-- ==========================================================================

-- ── Categories (19 total, order determines auto-assigned IDs 1-19) ─────────
INSERT INTO dev_exam_categories (code, icon, sort_order, is_active) VALUES
  ('restapi',              'Globe',         1,  TRUE),
  ('databaseintegration',  'Database',      2,  TRUE),
  ('authentication',       'Shield',        3,  TRUE),
  ('authorization',        'Key',           4,  TRUE),
  ('frontend',             'Layout',        5,  TRUE),
  ('backendlogic',         'Server',        6,  TRUE),
  ('validation',           'CheckCircle',   7,  TRUE),
  ('security',             'ShieldAlert',   8,  TRUE),
  ('testing',              'TestTube',      9,  TRUE),
  ('errorhandling',        'AlertTriangle', 10, TRUE),
  ('performance',          'Zap',           11, TRUE),
  ('internationalization', 'Globe2',        12, TRUE),
  ('deployment',           'Rocket',        13, TRUE),
  ('loggingmonitoring',    'Activity',      14, TRUE),
  ('cicd',                 'GitBranch',     15, TRUE),
  ('filehandling',         'FileText',      16, TRUE),
  ('reporting',            'BarChart2',     17, TRUE),
  ('searchfiltering',      'Search',        18, TRUE),
  ('configuration',        'Settings',      19, TRUE);

-- ── Category translations — EN ─────────────────────────────────────────────
INSERT INTO dev_exam_category_i18n (category_id, language_code, name, description) VALUES
  (1,  'en', 'REST API',                'Design, build and consume RESTful APIs'),
  (2,  'en', 'Database Integration',    'Database access, ORM, transactions and query optimisation'),
  (3,  'en', 'Authentication',          'Identity verification using JWT, OAuth and session management'),
  (4,  'en', 'Authorization',           'Access control, roles, permissions and policies'),
  (5,  'en', 'Frontend Development',    'Client-side UI architecture, rendering and performance'),
  (6,  'en', 'Backend Logic',           'Business logic, services, repositories and domain design'),
  (7,  'en', 'Validation',              'Input validation, constraints and data integrity'),
  (8,  'en', 'Security',                'Application security, OWASP and vulnerability prevention'),
  (9,  'en', 'Testing',                 'Unit, integration and E2E testing strategies and TDD'),
  (10, 'en', 'Error Handling',          'Exception management, error responses and resilience patterns'),
  (11, 'en', 'Performance',             'Caching, optimisation, profiling and scalability'),
  (12, 'en', 'Internationalization',    'i18n, l10n, locale handling and multilingual support'),
  (13, 'en', 'Deployment',              'Containers, orchestration and deployment strategies'),
  (14, 'en', 'Logging & Monitoring',    'Structured logging, metrics, tracing and alerting'),
  (15, 'en', 'CI/CD',                   'Continuous integration, delivery and deployment pipelines'),
  (16, 'en', 'File Handling',           'File uploads, storage, streaming and processing'),
  (17, 'en', 'Reporting',               'Data export, report generation and aggregation'),
  (18, 'en', 'Search & Filtering',      'Full-text search, faceted filtering and pagination'),
  (19, 'en', 'Configuration',           'App configuration, profiles, secrets and feature flags');

-- ── Category translations — AR ─────────────────────────────────────────────
INSERT INTO dev_exam_category_i18n (category_id, language_code, name, description) VALUES
  (1,  'ar', 'واجهة برمجة REST',              'تصميم وبناء واستهلاك واجهات برمجة RESTful'),
  (2,  'ar', 'تكامل قاعدة البيانات',           'الوصول إلى البيانات والـORM والمعاملات وتحسين الاستعلامات'),
  (3,  'ar', 'المصادقة',                       'التحقق من الهوية باستخدام JWT وOAuth وإدارة الجلسات'),
  (4,  'ar', 'التفويض',                        'التحكم في الوصول والأدوار والصلاحيات والسياسات'),
  (5,  'ar', 'تطوير الواجهة الأمامية',          'معمارية واجهة المستخدم والعرض والأداء'),
  (6,  'ar', 'منطق الخادم',                    'منطق الأعمال والخدمات والمستودعات وتصميم النطاق'),
  (7,  'ar', 'التحقق من الصحة',                'التحقق من المدخلات والقيود وسلامة البيانات'),
  (8,  'ar', 'الأمان',                         'أمان التطبيقات وأفضل الممارسات ومنع الثغرات'),
  (9,  'ar', 'الاختبار',                       'استراتيجيات الاختبار الوحدوي والتكاملي والنهاية إلى النهاية'),
  (10, 'ar', 'معالجة الأخطاء',                 'إدارة الاستثناءات واستجابات الأخطاء وأنماط المرونة'),
  (11, 'ar', 'الأداء',                         'التخزين المؤقت والتحسين وقابلية التوسع'),
  (12, 'ar', 'التدويل',                        'دعم اللغات المتعددة والترجمة وإعدادات اللغة'),
  (13, 'ar', 'النشر',                          'الحاويات والتنسيق واستراتيجيات النشر'),
  (14, 'ar', 'التسجيل والمراقبة',              'التسجيل المنظم والمقاييس والتتبع والتنبيه'),
  (15, 'ar', 'التكامل والنشر المستمر',          'خطوط أنابيب التكامل والتسليم والنشر المستمر'),
  (16, 'ar', 'معالجة الملفات',                  'تحميل الملفات والتخزين والبث والمعالجة'),
  (17, 'ar', 'التقارير',                       'تصدير البيانات وإنشاء التقارير وتجميعها'),
  (18, 'ar', 'البحث والتصفية',                 'البحث في النص والتصفية المتعددة ومعالجة الصفحات'),
  (19, 'ar', 'الضبط والإعداد',                 'إعداد التطبيق والملفات الشخصية والأسرار وأعلام الميزات');

-- ── Category translations — NL ─────────────────────────────────────────────
INSERT INTO dev_exam_category_i18n (category_id, language_code, name, description) VALUES
  (1,  'nl', 'REST API',                    'Ontwerpen, bouwen en aanroepen van RESTful API''s'),
  (2,  'nl', 'Database-integratie',         'Databasetoegang, ORM, transacties en queryoptimalisatie'),
  (3,  'nl', 'Authenticatie',               'Identiteitsverificatie met JWT, OAuth en sessiebeheer'),
  (4,  'nl', 'Autorisatie',                 'Toegangsbeheer, rollen, rechten en beleid'),
  (5,  'nl', 'Frontend-ontwikkeling',       'Client-side UI-architectuur, rendering en prestaties'),
  (6,  'nl', 'Backendlogica',               'Bedrijfslogica, services, repositories en domeinontwerp'),
  (7,  'nl', 'Validatie',                   'Invoervalidatie, beperkingen en gegevensintegriteit'),
  (8,  'nl', 'Beveiliging',                 'Applicatiebeveiliging, OWASP en preventie van kwetsbaarheden'),
  (9,  'nl', 'Testen',                      'Unit-, integratie- en E2E-teststrategieën en TDD'),
  (10, 'nl', 'Foutafhandeling',             'Uitzonderingsbeheer, foutreacties en veerkrachtpatronen'),
  (11, 'nl', 'Prestaties',                  'Caching, optimalisatie, profilering en schaalbaarheid'),
  (12, 'nl', 'Internationalisatie',         'i18n, l10n, lokaalverwerking en meertalige ondersteuning'),
  (13, 'nl', 'Implementatie',               'Containers, orkestratie en implementatiestrategieën'),
  (14, 'nl', 'Logging & Monitoring',        'Gestructureerd loggen, metrics, tracering en waarschuwingen'),
  (15, 'nl', 'CI/CD',                       'Pijplijnen voor continue integratie, levering en implementatie'),
  (16, 'nl', 'Bestandsverwerking',          'Bestandsuploads, opslag, streaming en verwerking'),
  (17, 'nl', 'Rapportage',                  'Gegevensexport, rapportgeneratie en aggregatie'),
  (18, 'nl', 'Zoeken & Filteren',           'Volledige tekstzoekopdrachten, facetfiltering en paginering'),
  (19, 'nl', 'Configuratie',                'App-configuratie, profielen, geheimen en functievlaggen');

-- ── Category translations — FR ─────────────────────────────────────────────
INSERT INTO dev_exam_category_i18n (category_id, language_code, name, description) VALUES
  (1,  'fr', 'API REST',                        'Concevoir, créer et consommer des API RESTful'),
  (2,  'fr', 'Intégration base de données',     'Accès aux données, ORM, transactions et optimisation des requêtes'),
  (3,  'fr', 'Authentification',                'Vérification d''identité avec JWT, OAuth et gestion de sessions'),
  (4,  'fr', 'Autorisation',                    'Contrôle d''accès, rôles, permissions et politiques'),
  (5,  'fr', 'Développement Frontend',          'Architecture UI côté client, rendu et performances'),
  (6,  'fr', 'Logique Backend',                 'Logique métier, services, repositories et conception de domaine'),
  (7,  'fr', 'Validation',                      'Validation des entrées, contraintes et intégrité des données'),
  (8,  'fr', 'Sécurité',                        'Sécurité applicative, OWASP et prévention des vulnérabilités'),
  (9,  'fr', 'Tests',                           'Stratégies de tests unitaires, intégration, E2E et TDD'),
  (10, 'fr', 'Gestion des Erreurs',             'Gestion des exceptions, réponses d''erreur et résilience'),
  (11, 'fr', 'Performance',                     'Mise en cache, optimisation, profilage et scalabilité'),
  (12, 'fr', 'Internationalisation',            'i18n, l10n, gestion des locales et support multilingue'),
  (13, 'fr', 'Déploiement',                     'Conteneurs, orchestration et stratégies de déploiement'),
  (14, 'fr', 'Journalisation & Monitoring',     'Journalisation structurée, métriques, traçage et alertes'),
  (15, 'fr', 'CI/CD',                           'Pipelines d''intégration, livraison et déploiement continus'),
  (16, 'fr', 'Gestion de Fichiers',             'Uploads, stockage, streaming et traitement de fichiers'),
  (17, 'fr', 'Reporting',                       'Export de données, génération de rapports et agrégation'),
  (18, 'fr', 'Recherche & Filtrage',            'Recherche plein texte, filtrage facetté et pagination'),
  (19, 'fr', 'Configuration',                   'Config d''app, profils, secrets et indicateurs de fonctionnalités');

-- ── Settings (3 questions per difficulty, 20 min, 70% pass) ───────────────
INSERT INTO dev_exam_settings
  (category_id, questions_beginner, questions_intermediate, questions_advanced, time_limit_minutes, pass_score_percent)
VALUES
  (1,  3, 3, 3, 20, 70), (2,  3, 3, 3, 20, 70), (3,  3, 3, 3, 20, 70),
  (4,  3, 3, 3, 20, 70), (5,  3, 3, 3, 20, 70), (6,  3, 3, 3, 20, 70),
  (7,  3, 3, 3, 20, 70), (8,  3, 3, 3, 20, 70), (9,  3, 3, 3, 20, 70),
  (10, 3, 3, 3, 20, 70), (11, 3, 3, 3, 20, 70), (12, 3, 3, 3, 20, 70),
  (13, 3, 3, 3, 20, 70), (14, 3, 3, 3, 20, 70), (15, 3, 3, 3, 20, 70),
  (16, 3, 3, 3, 20, 70), (17, 3, 3, 3, 20, 70), (18, 3, 3, 3, 20, 70),
  (19, 3, 3, 3, 20, 70);
