-- ============================================================
-- V167__Add_D13_Sign.sql
-- Add D13 (Verplichte weg voor ruiters) - image now available
-- Previously deactivated in V116 due to missing image
-- ============================================================

SET NAMES utf8mb4;

INSERT INTO traffic_signs (
    category_id, sign_code, normalized_sign_code,
    name_ar, name_en, name_nl, name_fr,
    description_ar, description_en, description_nl, description_fr,
    image_url, image_path, is_active, created_at, updated_at
)
SELECT 4, 'D13', 'D13',
    'طريق إجباري مخصص لراكبي الخيل',
    'Mandatory path for horse riders',
    'Verplichte weg voor ruiters',
    'Chemin obligatoire pour cavaliers',
    'تشير هذه العلامة إلى مسار إلزامي لراكبي الخيل. لا يُسمح للمشاة والمركبات بالسير على هذا الطريق.',
    'This sign indicates a mandatory path for horse riders. Pedestrians and vehicles are not allowed on this path.',
    'Dit bord geeft een verplichte weg aan voor ruiters. Voetgangers en voertuigen mogen dit pad niet gebruiken.',
    'Ce panneau indique un chemin obligatoire pour les cavaliers. Les piétons et les véhicules ne peuvent pas utiliser ce chemin.',
    'images/signs/mandatory_signs/D13 Verplichte weg voor ruiters.png',
    'images/signs/mandatory_signs/D13 Verplichte weg voor ruiters.png',
    1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM traffic_signs WHERE sign_code = 'D13');
