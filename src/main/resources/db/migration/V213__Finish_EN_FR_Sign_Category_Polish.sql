-- Final EN/FR sign-category cleanup after V212.

SET SESSION group_concat_max_len = 1024 * 1024 * 8;

DROP TEMPORARY TABLE IF EXISTS tmp_v213_phrase_replacements;

CREATE TEMPORARY TABLE tmp_v213_phrase_replacements (
    language_code VARCHAR(2) NOT NULL,
    search_text TEXT NOT NULL,
    replacement_text TEXT NOT NULL
) ENGINE=InnoDB;

INSERT INTO tmp_v213_phrase_replacements (language_code, search_text, replacement_text) VALUES
    ('EN', 'What is the purpose of the traffic sign "Mandatory direction for dangerous goods" / the traffic sign "Mandatory left for dangerous goods vehicles" / the traffic sign "Mandatory direction for dangerous goods"-rechts series?',
        'What is the purpose of the traffic sign "Mandatory direction for dangerous goods" and related mandatory signs for dangerous-goods vehicles?'),
    ('EN', 'The the traffic sign "Mandatory direction for dangerous goods" series channels dangerous transport at junctions via safe routes determined by the authorities.',
        'This mandatory sign channels dangerous-goods transport at junctions via safe routes determined by the authorities.'),
    ('EN', 'The E-series contains all parking and stopping regulation signs.',
        'Parking and stopping signs regulate parking and stopping.'),
    ('EN', 'E9-series signs regulate parking. Brief stopping (staying with vehicle) is normally permitted unless the traffic sign "No stopping and parking" also applies.',
        'These parking signs regulate parking. Brief stopping while remaining with the vehicle is normally permitted unless the traffic sign "No stopping and parking" also applies.'),
    ('EN', 'the traffic sign "End of path reserved for pedestrians, cyclists, horse riders and agricultural vehicles" ends a reserved road. That road was previously opened by a matching start sign from 1 series such as the traffic sign "Path reserved for pedestrians, cyclists, horse riders and agricultural vehicles".',
        'The traffic sign "End of path reserved for pedestrians, cyclists, horse riders and agricultural vehicles" ends a reserved road. That road was previously opened by the matching start sign "Path reserved for pedestrians, cyclists, horse riders and agricultural vehicles".'),
    ('EN', 'the traffic sign "End of zone parking limited in time, parking disc required" is a zone sign belonging to the Z-series.',
        'The traffic sign "End of zone parking limited in time, parking disc required" is a zone sign.'),
    ('EN', 'the traffic sign "End zone parking for cars only" is a zone sign belonging to the Z-series.',
        'The traffic sign "End zone parking for cars only" is a zone sign.'),
    ('EN', 'the traffic sign "End of paid parking zone for cars only" is a zone sign belonging to the Z-series.',
        'The traffic sign "End of paid parking zone for cars only" is a zone sign.'),
    ('EN', 'the traffic sign "End zone parking for cars only (with time)" is a zone sign belonging to the Z-series.',
        'The traffic sign "End zone parking for cars only (with time)" is a zone sign.'),
    ('EN', 'the traffic sign "ZONE Bicycle street" is a zone sign belonging to the Z-series.',
        'The traffic sign "ZONE Bicycle street" is a zone sign.'),
    ('EN', 'the traffic sign "End ZONE Bicycle street" is a zone sign belonging to the Z-series.',
        'The traffic sign "End ZONE Bicycle street" is a zone sign.'),
    ('EN', 'Priority over crossing side road (the traffic sign "Give way"5 series)',
        'Priority over crossing side road (priority-configuration signs)'),
    ('FR', 'La serie E contient tous les panneaux de reglementation de stationnement.',
        'Ces panneaux réglementent le stationnement.'),
    ('FR', 'La serie E contient tous les panneaux de reglementation du stationnement.',
        'Ces panneaux réglementent le stationnement.'),
    ('FR', 'La serie E contient tous les panneaux de reglementation du stationnement et de l''arret.',
        'Ces panneaux réglementent le stationnement et l''arrêt.'),
    ('FR', 'Les panneaux serie E9 reglementent le stationnement. Un arret bref (en restant) est normalement autorise sauf si le panneau "Arrêt et stationnement interdits" s''applique aussi.',
        'Ces panneaux réglementent le stationnement. Un arrêt bref en restant avec le véhicule est normalement autorisé, sauf si le panneau "Arrêt et stationnement interdits" s''applique aussi.'),
    ('FR', 'Les panneaux additionnels (serie G) font partie integrante du panneau de signalisation et sont juridiquement contraignants. Ignorer GVIIb est une infraction au code de la route.',
        'Les panneaux additionnels font partie intégrante du panneau de signalisation et sont juridiquement contraignants. Ignorer GVIIb constitue une infraction au code de la route.'),
    ('FR', 'le panneau "Indication de priorité" est par definition un panneau additionnel (serie G) et ne peut jamais imposer une regle de priorite par lui-meme. Il tire sa force juridique exclusivement du panneau principal sous lequel il est fixe.',
        'Le panneau "Indication de priorité" est par définition un panneau additionnel et ne peut jamais imposer une règle de priorité à lui seul. Il tire sa force juridique exclusivement du panneau principal sous lequel il est fixé.'),
    ('FR', 'GXI appartient aux panneaux additionnels de la serie G et est utilise exclusivement sur les autoroutes et routes pour automobiles pour indiquer les situations de sortie. Sur les routes ordinaires d''autres types d''indicateurs de direction sont utilises.',
        'GXI appartient aux panneaux additionnels et est utilisé exclusivement sur les autoroutes et routes pour automobiles pour indiquer les situations de sortie. Sur les routes ordinaires, d''autres types d''indicateurs de direction sont utilisés.'),
    ('FR', 'le panneau "Zone de limitation de vitesse à 50 km/h" appartient à la série de zone. La différence avec le panneau "Limitation de vitesse 30 km/h" est que la limitation de vitesse de le panneau "Zone de limitation de vitesse à 50 km/h" s''applique dans toute la zone jusqu''au panneau de fin de zone.',
        'Le panneau "Zone de limitation de vitesse à 50 km/h" appartient aux panneaux de zone. Contrairement à un panneau de limitation de vitesse simple, cette limitation s''applique dans toute la zone jusqu''au panneau de fin de zone.'),
    ('FR', 'le panneau "Fin de zone de stationnement limité dans le temps, disque de stationnement obligatoire" est un panneau de zone appartenant à la série Z.',
        'Le panneau "Fin de zone de stationnement limité dans le temps, disque de stationnement obligatoire" est un panneau de zone.'),
    ('FR', 'le panneau "Fin zone stationnement uniquement pour voitures" est un panneau de zone appartenant à la série Z.',
        'Le panneau "Fin zone stationnement uniquement pour voitures" est un panneau de zone.'),
    ('FR', 'le panneau "Fin de zone de stationnement payant réservée aux voitures" est un panneau de zone appartenant à la série Z.',
        'Le panneau "Fin de zone de stationnement payant réservée aux voitures" est un panneau de zone.'),
    ('FR', 'le panneau "Fin zone stationnement voitures (avec indication)" est un panneau de zone appartenant à la série Z.',
        'Le panneau "Fin zone stationnement voitures (avec indication)" est un panneau de zone.'),
    ('FR', 'le panneau "ZONE Rue cyclable" est un panneau de zone appartenant à la série Z.',
        'Le panneau "ZONE Rue cyclable" est un panneau de zone.'),
    ('FR', 'le panneau "Fin ZONE Rue cyclable" est un panneau de zone appartenant à la série Z.',
        'Le panneau "Fin ZONE Rue cyclable" est un panneau de zone.');

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
    FROM tmp_v213_phrase_replacements
    WHERE language_code = 'EN'
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
    FROM tmp_v213_phrase_replacements
    WHERE language_code = 'FR'
);

SET @sql = CONCAT(
    'UPDATE sign_questions SET ',
    'question_en = ', REPLACE(@expr_en, '__COLUMN__', 'question_en'), ', ',
    'explanation_en = ', REPLACE(@expr_en, '__COLUMN__', 'explanation_en'), ', ',
    'question_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'question_fr'), ', ',
    'explanation_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'explanation_fr')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT(
    'UPDATE sign_choices SET ',
    'text_en = ', REPLACE(@expr_en, '__COLUMN__', 'text_en'), ', ',
    'text_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'text_fr')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT(
    'UPDATE quiz_questions SET ',
    'question_en = ', REPLACE(@expr_en, '__COLUMN__', 'question_en'), ', ',
    'explanation_en = ', REPLACE(@expr_en, '__COLUMN__', 'explanation_en'), ', ',
    'error_explanation_en = ', REPLACE(@expr_en, '__COLUMN__', 'error_explanation_en'), ', ',
    'question_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'question_fr'), ', ',
    'explanation_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'explanation_fr'), ', ',
    'error_explanation_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'error_explanation_fr')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT(
    'UPDATE quiz_answer_options SET ',
    'option_text_en = ', REPLACE(@expr_en, '__COLUMN__', 'option_text_en'), ', ',
    'option_text_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'option_text_fr')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

DROP TEMPORARY TABLE IF EXISTS tmp_v213_phrase_replacements;
