-- ============================================================
-- V165__Add_Missing_D_Category_Signs.sql
-- Add missing D category mandatory signs:
-- D1b-rechts, D5, D7, D9a, D9b
-- ============================================================

SET NAMES utf8mb4;

DELETE FROM traffic_signs WHERE sign_code IN ('D1b-rechts','D5','D7','D9a','D9b');

INSERT INTO traffic_signs (
    category_id, sign_code, normalized_sign_code,
    name_ar, name_en, name_nl, name_fr,
    description_ar, description_en, description_nl, description_fr,
    image_url, image_path,
    is_active, created_at, updated_at
) VALUES
(4, 'D1b-rechts', 'D1b-rechts',
 'إلزام الانعطاف يميناً',
 'Mandatory right turn',
 'Verplichting rechts afslaan',
 'Obligation de tourner à droite',
 'هذه العلامة تلزم السائقين بالانعطاف يميناً.',
 'This traffic sign requires drivers to turn right. They must continue in this direction only.',
 'Dit verkeersbord verplicht bestuurders om naar rechts af te slaan.',
 'Ce panneau oblige les conducteurs à tourner à droite.',
 'images/signs/mandatory_signs/D1b Verplichting rechts afslaan.png',
 'images/signs/mandatory_signs/D1b Verplichting rechts afslaan.png',
 1, NOW(), NOW()),

(4, 'D5', 'D5',
 'إلزام بالسير الدوراني',
 'Compulsory roundabout',
 'Verplicht rondgaand verkeer',
 'Sens giratoire obligatoire',
 'هذه العلامة تلزم السائقين باتباع حركة المرور الدوارة.',
 'This sign indicates a compulsory roundabout. Drivers must follow the rotary traffic flow.',
 'Dit bord geeft aan dat het rondgaand verkeer verplicht is.',
 'Ce panneau indique un sens giratoire obligatoire.',
 'images/signs/mandatory_signs/D5 Verplicht rondgaand verkeer.png',
 'images/signs/mandatory_signs/D5 Verplicht rondgaand verkeer.png',
 1, NOW(), NOW()),

(4, 'D7', 'D7',
 'مسار إلزامي للدراجات',
 'Compulsory cycle path',
 'Verplicht fietspad',
 'Piste cyclable obligatoire',
 'هذه العلامة تشير إلى مسار إلزامي للدراجات.',
 'This sign indicates a compulsory cycle path. Cyclists must use this path.',
 'Dit bord geeft aan dat het fietspad verplicht is voor fietsers.',
 'Ce panneau indique une piste cyclable obligatoire.',
 'images/signs/mandatory_signs/D7 Verplicht fietspad.png',
 'images/signs/mandatory_signs/D7 Verplicht fietspad.png',
 1, NOW(), NOW()),

(4, 'D9a', 'D9a',
 'جزء من الطريق مخصص للمشاة والدراجات (أ)',
 'Part of the road reserved for pedestrians and cyclists (variant a)',
 'Deel van de weg voorbehouden voor voetgangers en fietsers',
 'Partie de la route réservée aux piétons et cyclistes (variante a)',
 'جزء من الطريق مخصص للمشاة والدراجين.',
 'This sign indicates that part of the road is reserved for pedestrians and cyclists (variant a).',
 'Dit bord geeft aan dat een deel van de weg voorbehouden is voor voetgangers en fietsers.',
 'Ce panneau indique qu une partie de la route est reservee aux pietons et cyclistes (variante a).',
 'images/signs/mandatory_signs/D9a Deel van de weg voorbehouden voor voetgangers en fietsers.png',
 'images/signs/mandatory_signs/D9a Deel van de weg voorbehouden voor voetgangers en fietsers.png',
 1, NOW(), NOW()),

(4, 'D9b', 'D9b',
 'جزء من الطريق مخصص للمشاة والدراجات (ب)',
 'Part of the road reserved for pedestrians and cyclists (variant b)',
 'Deel van de weg voorbehouden voor voetgangers en fietsers',
 'Partie de la route réservée aux piétons et cyclistes (variante b)',
 'جزء من الطريق مخصص للمشاة والدراجين (variant b).',
 'This sign indicates that part of the road is reserved for pedestrians and cyclists (variant b).',
 'Dit bord geeft aan dat een deel van de weg voorbehouden is voor voetgangers en fietsers (variant b).',
 'Ce panneau indique qu une partie de la route est reservee aux pietons et cyclistes (variante b).',
 'images/signs/mandatory_signs/D9b Deel van de weg voorbehouden voor voetgangers en fietsers.png',
 'images/signs/mandatory_signs/D9b Deel van de weg voorbehouden voor voetgangers en fietsers.png',
 1, NOW(), NOW());
