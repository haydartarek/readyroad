UPDATE sign_questions
SET question_en =
                'Which sign marks the beginning of the reserved zone that ends with the traffic sign "End of path reserved for pedestrians, cyclists, horse riders and agricultural vehicles"?',
        explanation_en =
                'The traffic sign "End of path reserved for pedestrians, cyclists, horse riders and agricultural vehicles" ends a reserved road. That road was previously opened by a matching start sign such as the traffic sign "Path reserved for pedestrians, cyclists and horse riders" (pedestrians, cyclists and horse riders).'
WHERE id = 4088;

UPDATE sign_questions
SET explanation_fr =
                'Ce panneau appartient aux panneaux de stationnement et d''arrêt. Ces panneaux réglementent le stationnement et l''arrêt.'
WHERE id = 4044;

UPDATE sign_questions
SET explanation_fr =
                'Le panneau "Zone de limitation de vitesse à 50 km/h" appartient à la catégorie des panneaux de zone. Contrairement au panneau "Limitation de vitesse 30 km/h", la limitation indiquée par ce panneau s''applique dans toute la zone, alors que le panneau "Limitation de vitesse 30 km/h" ne s''applique qu''à la section de route concernée.'
WHERE id = 4596;
