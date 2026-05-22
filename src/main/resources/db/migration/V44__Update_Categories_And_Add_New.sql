-- V44__Update_Categories_And_Add_New.sql
-- تحديث الفئات الحالية وإضافة فئة جديدة للمعلومات المؤقتة
-- Update existing categories with rich descriptions and add new category

-- Update Category A: gevaarsborden (Warning/Danger signs)
UPDATE categories SET
    name_nl = 'Gevaarsborden',
    name_en = 'Warning Signs',
    name_fr = 'Panneaux de danger',
    name_ar = 'علامات الخطر'
WHERE code = 'A';

-- Update Category B: voorrangsborden (Priority signs)
UPDATE categories SET
    name_nl = 'Voorrangsborden',
    name_en = 'Priority Signs',
    name_fr = 'Panneaux de priorité',
    name_ar = 'علامات الأولوية'
WHERE code = 'B';

-- Update Category C: verbodsborden (Prohibition signs)
UPDATE categories SET
    name_nl = 'Verbodsborden',
    name_en = 'Prohibition Signs',
    name_fr = 'Panneaux d''interdiction',
    name_ar = 'علامات المنع'
WHERE code = 'C';

-- Update Category D: gebodsborden (Mandatory signs)
UPDATE categories SET
    name_nl = 'Gebodsborden',
    name_en = 'Mandatory Signs',
    name_fr = 'Panneaux d''obligation',
    name_ar = 'العلامات الإجبارية'
WHERE code = 'D';

-- Update Category E: parkeer- en stilstaanborden (Parking signs)
UPDATE categories SET
    name_nl = 'Parkeer- en stilstaanborden',
    name_en = 'Parking and Standing Signs',
    name_fr = 'Panneaux de stationnement et d''arrêt',
    name_ar = 'علامات الوقوف والتوقف'
WHERE code = 'E';

-- Update Category F: aanwijzingsborden (Information/Direction signs)
UPDATE categories SET
    name_nl = 'Aanwijzingsborden',
    name_en = 'Information Signs',
    name_fr = 'Panneaux d''indication',
    name_ar = 'العلامات الإرشادية'
WHERE code = 'F';

-- Update Category G: onderborden (Supplementary signs)
UPDATE categories SET
    name_nl = 'Onderborden',
    name_en = 'Supplementary Signs',
    name_fr = 'Panneaux complémentaires',
    name_ar = 'العلامات التكميلية'
WHERE code = 'G';

-- Update Category Z: zoneborden (Zone signs)
UPDATE categories SET
    name_nl = 'Zoneborden',
    name_en = 'Zone Signs',
    name_fr = 'Panneaux de zone',
    name_ar = 'علامات المناطق المرورية'
WHERE code = 'Z';

-- Update Category M: afbakeningsborden (Delineation signs) - was "Road Markings"
UPDATE categories SET
    name_nl = 'Afbakeningsborden',
    name_en = 'Delineation Signs',
    name_fr = 'Panneaux de balisage',
    name_ar = 'علامات التوجيه'
WHERE code = 'M';

-- Add new category H: informatieborden en tijdelijke verkeersmaatregelen
INSERT INTO categories (code, name_ar, name_en, name_nl, name_fr, description_ar, description_en, description_nl, description_fr, display_order, is_active, created_at, updated_at)
VALUES ('H', 'علامات المعلومات والإجراءات المرورية المؤقتة', 'Information and Temporary Traffic Signs', 'Informatieborden en tijdelijke verkeersmaatregelen', 'Panneaux d''information et mesures de circulation temporaires',
        'علامات المعلومات والإجراءات المرورية المؤقتة', 'Information and temporary traffic measure signs', 'Informatieborden en tijdelijke verkeersmaatregelen', 'Panneaux d''information et mesures de circulation temporaires',
        10, TRUE, NOW(), NOW()) AS new_values
ON DUPLICATE KEY UPDATE
    name_ar = new_values.name_ar, name_en = new_values.name_en, name_nl = new_values.name_nl, name_fr = new_values.name_fr,
    description_ar = new_values.description_ar, description_en = new_values.description_en, description_nl = new_values.description_nl, description_fr = new_values.description_fr;
