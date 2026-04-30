-- Final multilingual cleanup for learner-facing legacy wording that remained
-- after V211. This migration focuses on EN/NL/FR category and bend wording.

SET SESSION group_concat_max_len = 1024 * 1024 * 16;

DROP TEMPORARY TABLE IF EXISTS tmp_v212_phrase_replacements;

CREATE TEMPORARY TABLE tmp_v212_phrase_replacements (
    language_code VARCHAR(2) NOT NULL,
    search_text TEXT NOT NULL,
    replacement_text TEXT NOT NULL
) ENGINE=InnoDB;

INSERT INTO tmp_v212_phrase_replacements (language_code, search_text, replacement_text) VALUES
    ('EN', 'This sign announces a series of dangerous bends. You must adapt your speed for the entire sequence, not just the first bend.',
        'This sign announces a succession of dangerous bends. You must adapt your speed throughout the bends, not only for the first one.'),
    ('EN', 'Overtaking just before a series of dangerous bends is prohibited. You cannot safely return to your lane before the first bend.',
        'Overtaking just before a succession of dangerous bends is prohibited. You cannot safely return to your lane before the first bend.'),
    ('EN', 'Danger signs legally require you to adapt your driving. You must reduce speed before entering the first bend of the series.',
        'Danger signs legally require you to adapt your driving. You must reduce speed before entering the first bend in the sequence.'),
    ('EN', 'This is a danger sign (triangular, red border). It belongs to the A-series and warns of hazardous situations.',
        'This is a danger sign (triangular, red border). It warns of hazardous situations.'),
    ('EN', 'The the traffic sign "Give way"5 series uses symbols to show road configuration. the traffic sign "Priority road" is a general priority road sign; the traffic sign "Give way" requires you to give way.',
        'These priority-configuration signs use symbols to show the road layout. The traffic sign "Priority road" grants priority, while the traffic sign "Give way" requires you to yield.'),
    ('EN', 'The the traffic sign "Give way"5 series uses symbols to show road configuration and indicate your right of way on specific side roads.',
        'These priority-configuration signs use symbols to show the road layout and indicate your right of way on specific side roads.'),
    ('EN', 'In all the traffic sign "Give way"5 series signs the thick line always represents your road (the priority road). The thin line represents the side road that must give way.',
        'In these priority-configuration signs, the thick line represents your road, and the thin line represents the side road that must yield.'),
    ('EN', 'Prohibition signs are round with a red border. the traffic sign "End of speed limit 50 km/h" is an end-sign within the C-series that cancels a previous restriction.',
        'Prohibition signs are round with a red border. The traffic sign "End of speed limit 50 km/h" is an end sign that cancels a previous restriction.'),
    ('EN', 'the traffic sign "Shared path for pedestrians and cyclists" is a mandatory sign from the D-series. It requires pedestrians and cyclists to use this path.',
        'The traffic sign "Shared path for pedestrians and cyclists" is a mandatory sign. It requires pedestrians and cyclists to use this path.'),
    ('EN', 'D-series signs are mandatory signs. the traffic sign "Mandatory straight ahead" requires drivers to follow the indicated direction.',
        'Mandatory signs require drivers to follow the indicated direction. The traffic sign "Mandatory straight ahead" is one of these signs.'),
    ('EN', 'D-series mandatory signs apply to all road users unless an additional supplementary sign excludes a specific category.',
        'Mandatory signs apply to all road users unless an additional supplementary sign excludes a specific category.'),
    ('EN', 'D-series signs are mandatory signs. the traffic sign "Mandatory straight ahead"-links requires passing to the left of a central island.',
        'Mandatory signs require drivers to follow the indicated direction. The traffic sign "Mandatory straight ahead" on the left requires passing to the left of a central island.'),
    ('EN', 'D-series signs are mandatory signs. the traffic sign "Mandatory straight ahead"-rechts requires passing to the right of a central island.',
        'Mandatory signs require drivers to follow the indicated direction. The traffic sign "Mandatory straight ahead" on the right requires passing to the right of a central island.'),
    ('EN', 'D-series signs are mandatory signs. the traffic sign "Mandatory left turn" requires drivers to turn left.',
        'Mandatory signs require drivers to follow the indicated direction. The traffic sign "Mandatory left turn" requires drivers to turn left.'),
    ('EN', 'A-series', 'danger-sign category'),
    ('EN', 'B-series', 'priority-sign category'),
    ('EN', 'C-series', 'prohibition-sign category'),
    ('EN', 'D-series', 'mandatory-sign category'),
    ('EN', 'F-series', 'information-sign category'),
    ('EN', 'G-series', 'supplementary-sign category'),
    ('EN', 'series of dangerous bends', 'succession of dangerous bends'),
    ('EN', 'series of bends', 'sequence of bends'),
    ('EN', 'first bend of the series', 'first bend in the sequence'),
    ('EN', 'entire series', 'entire sequence'),
    ('EN', 'for the entire series', 'through all the bends'),
    ('EN', 'throughout the series', 'through all the bends'),
    ('NL', 'Op een natte weg bij een reeks bochten moet u vóór de eerste bocht sterk vertragen. Tussen de bochten mag u de snelheid niet verhogen en u rijdt altijd op uw rijbaan.',
        'Op een natte weg met opeenvolgende bochten moet u vóór de eerste bocht sterk vertragen. Tussen de bochten verhoogt u de snelheid niet en u blijft altijd op uw rijstrook.'),
    ('NL', 'Dit is een gevaarsbord (driehoekig, rode rand). Het behoort tot de A-reeks en waarschuwt voor gevaarlijke situaties.',
        'Dit is een gevaarsbord (driehoekig, rode rand). Het waarschuwt voor gevaarlijke situaties.'),
    ('NL', 'Verbodsborden zijn rond met rode rand. het verkeersbord "Einde snelheidsbeperking" is een eindbord binnen de C-reeks dat een eerdere beperking opheft.',
        'Verbodsborden zijn rond met rode rand. Het verkeersbord "Einde snelheidsbeperking" is een eindbord dat een eerdere beperking opheft.'),
    ('NL', 'het verkeersbord "Deel van de weg voorbehouden voor voetgangers en fietsers" is een gebodsteken uit de D-reeks. Het bord verplicht voetgangers en fietsers dit pad te gebruiken.',
        'Het verkeersbord "Deel van de weg voorbehouden voor voetgangers en fietsers" is een gebodsbord. Het verplicht voetgangers en fietsers dit pad te gebruiken.'),
    ('NL', 'D-reeksborden zijn gebodstekens. het verkeersbord "Verplicht rechtdoor" verplicht bestuurders om de aangeduide richting te volgen.',
        'Gebodsborden verplichten bestuurders om de aangeduide richting te volgen. Het verkeersbord "Verplicht rechtdoor" is daar een voorbeeld van.'),
    ('NL', 'Gebodsborden van de D-reeks gelden voor alle weggebruikers tenzij een bijkomend onderbord een andere categorie uitsluit.',
        'Gebodsborden gelden voor alle weggebruikers tenzij een bijkomend onderbord een andere categorie uitsluit.'),
    ('NL', 'D-reeksborden zijn gebodstekens. het verkeersbord "Verplicht rechtdoor"-links verplicht links passeren ten opzichte van een middeneiland.',
        'Gebodsborden verplichten bestuurders om de aangeduide richting te volgen. Dit bord verplicht links passeren ten opzichte van een middeneiland.'),
    ('NL', 'D-reeksborden zijn gebodstekens. het verkeersbord "Verplicht rechtdoor"-rechts verplicht rechts passeren ten opzichte van een middeneiland.',
        'Gebodsborden verplichten bestuurders om de aangeduide richting te volgen. Dit bord verplicht rechts passeren ten opzichte van een middeneiland.'),
    ('NL', 'D-reeksborden zijn gebodstekens. het verkeersbord "Verplicht links afslaan" verplicht bestuurders links af te slaan.',
        'Gebodsborden verplichten bestuurders om de aangeduide richting te volgen. Het verkeersbord "Verplicht links afslaan" verplicht bestuurders links af te slaan.'),
    ('NL', 'A-reeks', 'categorie gevaarsborden'),
    ('NL', 'B-reeks', 'categorie voorrangsborden'),
    ('NL', 'C-reeks', 'categorie verbodsborden'),
    ('NL', 'D-reeks', 'categorie gebodsborden'),
    ('NL', 'F-reeks', 'categorie informatieborden'),
    ('NL', 'G-reeks', 'aanvullende en informatieve borden'),
    ('NL', 'reeks bochten', 'opeenvolgende bochten'),
    ('NL', 'de volledige reeks', 'alle opeenvolgende bochten'),
    ('NL', 'voor de volledige reeks', 'over alle opeenvolgende bochten'),
    ('NL', 'reeks gevaarlijke bochten', 'opeenvolgende gevaarlijke bochten'),
    ('NL', 'reeks', 'opeenvolging'),
    ('FR', 'C''est un panneau de danger (triangulaire, bordure rouge). Il appartient à la série A et avertit de situations dangereuses.',
        'C''est un panneau de danger (triangulaire, bordure rouge). Il avertit de situations dangereuses.'),
    ('FR', 'La série le panneau "Cédez le passage"5 utilise des symboles pour montrer la configuration de la route. le panneau "Route prioritaire" est un panneau de route prioritaire général ; le panneau "Cédez le passage" vous oblige à céder le passage.',
        'Ces panneaux schématiques de priorité utilisent des symboles pour montrer la configuration de la route. Le panneau "Route prioritaire" accorde la priorité, tandis que le panneau "Cédez le passage" oblige à céder le passage.'),
    ('FR', 'La série le panneau "Cédez le passage"5 utilise des symboles pour montrer la configuration de la route et indiquer votre droit de priorité sur des routes latérales spécifiques.',
        'Ces panneaux schématiques de priorité utilisent des symboles pour montrer la configuration de la route et indiquer votre droit de priorité sur des routes latérales précises.'),
    ('FR', 'Les panneaux d interdiction sont ronds avec un bord rouge. le panneau "Fin de la limitation de vitesse à 50 km/h" est un panneau de fin dans la serie C qui leve une restriction precedente.',
        'Les panneaux d''interdiction sont ronds avec un bord rouge. Le panneau "Fin de la limitation de vitesse à 50 km/h" met fin à une restriction précédente.'),
    ('FR', 'le panneau "Chemin partagé pour piétons et cyclistes" est un panneau d obligation de la serie D. Il oblige les pietons et cyclistes a utiliser ce chemin.',
        'Le panneau "Chemin partagé pour piétons et cyclistes" est un panneau d''obligation. Il oblige les piétons et les cyclistes à utiliser ce chemin.'),
    ('FR', 'Les panneaux de la serie D sont des panneaux d''obligation. le panneau "Obligation d''aller tout droit" oblige les conducteurs a suivre la direction indiquee.',
        'Les panneaux d''obligation imposent une direction précise. Le panneau "Obligation d''aller tout droit" oblige les conducteurs à suivre la direction indiquée.'),
    ('FR', 'Les panneaux d''obligation de la serie D s''appliquent a tous les usagers de la route sauf si un panneau complementaire exclut une categorie specifique.',
        'Les panneaux d''obligation s''appliquent à tous les usagers de la route sauf si un panneau complémentaire exclut une catégorie spécifique.'),
    ('FR', 'Les panneaux de la serie D sont des panneaux d''obligation. le panneau "Obligation de tourner a gauche" oblige les conducteurs a tourner a gauche.',
        'Les panneaux d''obligation imposent une direction précise. Le panneau "Obligation de tourner à gauche" oblige les conducteurs à tourner à gauche.'),
    ('FR', 'Les panneaux de la serie D sont des panneaux d''obligation. le panneau "Obligation de serrer à droite" oblige les conducteurs a serrer a droite.',
        'Les panneaux d''obligation imposent une direction précise. Le panneau "Obligation de serrer à droite" oblige les conducteurs à serrer à droite.'),
    ('FR', 'La serie le panneau "Direction obligatoire marchandises dangereuses" canalise le transport dangereux aux carrefours via des itineraires surs determines par les autorites.',
        'Ce panneau de direction obligatoire pour marchandises dangereuses canalise le transport dangereux aux carrefours via des itinéraires sûrs déterminés par les autorités.'),
    ('FR', 'série A', 'catégorie des panneaux de danger'),
    ('FR', 'serie A', 'catégorie des panneaux de danger'),
    ('FR', 'série B', 'catégorie des panneaux de priorité'),
    ('FR', 'serie B', 'catégorie des panneaux de priorité'),
    ('FR', 'série C', 'catégorie des panneaux d''interdiction'),
    ('FR', 'serie C', 'catégorie des panneaux d''interdiction'),
    ('FR', 'série D', 'catégorie des panneaux d''obligation'),
    ('FR', 'serie D', 'catégorie des panneaux d''obligation'),
    ('FR', 'série F', 'catégorie des panneaux d''information'),
    ('FR', 'serie F', 'catégorie des panneaux d''information'),
    ('FR', 'série de virages', 'succession de virages'),
    ('FR', 'premier virage de la série', 'premier virage de la succession'),
    ('FR', 'sur toute la série', 'sur l''ensemble des virages'),
    ('FR', 'pour toute la série', 'pour l''ensemble des virages');

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
    FROM tmp_v212_phrase_replacements
    WHERE language_code = 'EN'
);

SET @expr_nl = (
    SELECT CONCAT(
        REPEAT('REPLACE(', COUNT(*)),
        '__COLUMN__',
        GROUP_CONCAT(
            CONCAT(', ''', REPLACE(search_text, '''', ''''''), ''', ''', REPLACE(replacement_text, '''', ''''''), ''')')
            ORDER BY CHAR_LENGTH(search_text) DESC
            SEPARATOR ''
        )
    )
    FROM tmp_v212_phrase_replacements
    WHERE language_code = 'NL'
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
    FROM tmp_v212_phrase_replacements
    WHERE language_code = 'FR'
);

SET @sql = CONCAT(
    'UPDATE sign_questions SET ',
    'question_en = ', REPLACE(@expr_en, '__COLUMN__', 'question_en'), ', ',
    'question_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'question_nl'), ', ',
    'question_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'question_fr'), ', ',
    'explanation_en = ', REPLACE(@expr_en, '__COLUMN__', 'explanation_en'), ', ',
    'explanation_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'explanation_nl'), ', ',
    'explanation_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'explanation_fr')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT(
    'UPDATE sign_choices SET ',
    'text_en = ', REPLACE(@expr_en, '__COLUMN__', 'text_en'), ', ',
    'text_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'text_nl'), ', ',
    'text_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'text_fr')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT(
    'UPDATE quiz_questions SET ',
    'question_en = ', REPLACE(@expr_en, '__COLUMN__', 'question_en'), ', ',
    'question_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'question_nl'), ', ',
    'question_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'question_fr'), ', ',
    'explanation_en = ', REPLACE(@expr_en, '__COLUMN__', 'explanation_en'), ', ',
    'explanation_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'explanation_nl'), ', ',
    'explanation_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'explanation_fr'), ', ',
    'error_explanation_en = ', REPLACE(@expr_en, '__COLUMN__', 'error_explanation_en'), ', ',
    'error_explanation_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'error_explanation_nl'), ', ',
    'error_explanation_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'error_explanation_fr')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT(
    'UPDATE quiz_answer_options SET ',
    'option_text_en = ', REPLACE(@expr_en, '__COLUMN__', 'option_text_en'), ', ',
    'option_text_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'option_text_nl'), ', ',
    'option_text_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'option_text_fr')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT(
    'UPDATE road_signs SET ',
    'description_en = ', REPLACE(@expr_en, '__COLUMN__', 'description_en'), ', ',
    'description_nl = ', REPLACE(@expr_nl, '__COLUMN__', 'description_nl'), ', ',
    'description_fr = ', REPLACE(@expr_fr, '__COLUMN__', 'description_fr')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

DROP TEMPORARY TABLE IF EXISTS tmp_v212_phrase_replacements;
