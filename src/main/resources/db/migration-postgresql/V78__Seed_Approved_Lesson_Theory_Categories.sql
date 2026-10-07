-- Published associations exported from the approved local 32-lesson catalogue.
-- Resolve production IDs by stable codes; preserve content, media and user data.
-- Existing associations and additional production lessons are never replaced.
WITH approved (lesson_code, category_code) AS (
    VALUES
        ('les-0', 'TH08'),
        ('les-1', 'TH07'),
        ('les-2', 'TH08'),
        ('les-3', 'TH09'),
        ('les-4', 'TH09'),
        ('les-5', 'TH06'),
        ('les-6', 'TH09'),
        ('les-7', 'TH02'),
        ('les-8', 'TH06'),
        ('les-9', 'TH07'),
        ('les-10', 'TH10'),
        ('les-11', 'TH10'),
        ('les-12', 'TH07'),
        ('les-13', 'TH02'),
        ('les-14', 'TH02'),
        ('les-15', 'TH01'),
        ('les-16', 'TH03'),
        ('les-17', 'TH03'),
        ('les-18', 'TH05'),
        ('les-19', 'TH05'),
        ('les-20', 'TH01'),
        ('les-21', 'TH01'),
        ('les-22', 'TH03'),
        ('les-23', 'TH05'),
        ('les-24', 'TH05'),
        ('les-25', 'TH04'),
        ('les-26', 'TH04'),
        ('les-27', 'TH04'),
        ('les-28', 'TH08'),
        ('les-29', 'TH08'),
        ('les-30', 'TH08'),
        ('les-31', 'TH07')
)
INSERT INTO lesson_categories (lesson_id, category_id, is_primary, display_order)
SELECT l.id, c.id, TRUE, 1
FROM approved a
JOIN lessons l ON l.lesson_code = a.lesson_code
JOIN categories c ON c.code = a.category_code
ON CONFLICT (lesson_id, category_id) DO NOTHING;
