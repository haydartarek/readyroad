-- V175: Add road_markings F-series signs (F39, F79, F81, F83, F85, F89, F91, F95, F98)

INSERT IGNORE INTO traffic_signs
    (category_id, sign_code, normalized_sign_code,
     name_nl, name_en, name_fr, name_ar,
     description_nl, description_en, description_fr, description_ar,
     image_url, image_path, is_active, created_at, updated_at,
     long_description_nl, long_description_en, long_description_fr, long_description_ar)
VALUES

-- F39
(
    (SELECT id FROM categories WHERE code = 'F'),
    'F39', 'f39',
    'Aankondiging van een omleiding',
    'Diversion announcement',
    'Annonce de deviation',
    'إعلان تحويلة طريق',
    'Tijdelijke omleiding verplicht.',
    'Temporary diversion required.',
    'Deviation temporaire obligatoire.',
    'تحويلة مؤقتة إلزامية.',
    'images/signs/road_markings/F39 Aankondiging van een omleiding.png',
    'images/signs/road_markings/F39 Aankondiging van een omleiding.png',
    1, NOW(), NOW(),
    'Dit verkeersbord geeft aan dat er een tijdelijke omleiding is die verplicht moet worden gevolgd vanwege wegwerkzaamheden of een obstakel op de weg. De pijl geeft de richting van de omleiding aan. De tekst kan een uitzondering vermelden voor bepaalde voertuigen zoals openbaar vervoer of zware voertuigen. Alle andere bestuurders moeten de omleiding volgen tot de werkzaamheden zijn afgerond of een andere aanwijzing wordt gegeven.',
    'This traffic sign indicates a temporary diversion that must be followed due to roadworks or an obstacle on the road. The arrow indicates the direction of the diversion. The text may indicate an exception for certain vehicles such as public transport or heavy vehicles. All other drivers must follow the diversion until the works are completed or another instruction is given.',
    'Ce panneau de signalisation indique une deviation temporaire qui doit etre suivie obligatoirement en raison de travaux ou d''un obstacle sur la route. La fleche indique la direction de la deviation. Le texte peut indiquer une exception pour certains vehicules comme les transports en commun ou les poids lourds. Tous les autres conducteurs doivent suivre la deviation jusqu''a la fin des travaux ou jusqu''a ce qu''une autre instruction soit donnee.',
    'تشير هذه العلامة المرورية إلى وجود تحويلة مؤقتة يجب اتباعها بسبب أعمال الطرق أو وجود عائق على الطريق. السهم يشير إلى اتجاه التحويل. وقد يشير النص إلى استثناء لمركبات معينة مثل وسائل النقل العام أو المركبات الثقيلة. يجب على جميع السائقين الآخرين اتباع التحويل حتى اكتمال الأعمال أو إعطاء تعليمات أخرى.'
),

-- F79
(
    (SELECT id FROM categories WHERE code = 'F'),
    'F79', 'f79',
    'Tijdelijke verdeling van de rijstroken',
    'Temporary lane division with distance indication',
    'Repartition temporaire des voies avec indication de distance',
    'التقسيم المؤقت للحارات مع إشارة المسافة',
    'Tijdelijke verdeling van rijstroken.',
    'Temporary lane division.',
    'Repartition temporaire des voies.',
    'تقسيم مؤقت للحارات.',
    'images/signs/road_markings/F79 Tijdelijke verdeling van de rijstroken (met afstandsaanduiding).png',
    'images/signs/road_markings/F79 Tijdelijke verdeling van de rijstroken (met afstandsaanduiding).png',
    1, NOW(), NOW(),
    'Dit tijdelijke verkeersbord geeft aan dat de gereserveerde rijstrook aan de linkerkant van de weg na 1500 meter weer beschikbaar wordt voor gebruik. Dit bord wordt gebruikt om aan te geven dat de weg wordt verbreed.',
    'This temporary traffic sign indicates that the reserved lane on the left side of the road will become available again after 1500 meters. This sign is used to indicate road widening.',
    'Ce panneau de signalisation temporaire indique que la voie reservee sur le cote gauche de la route redeviendra disponible apres 1500 metres. Ce panneau est utilise pour indiquer l''elargissement de la route.',
    'تشير هذه العلامة المرورية المؤقتة إلى أن المسار المحجوز على الجانب الأيسر من الطريق سوف يصبح متاحاً مرة أخرى بعد 1500 متر. تستخدم هذه العلامة للإشارة إلى توسيع الطريق.'
),

-- F81
(
    (SELECT id FROM categories WHERE code = 'F'),
    'F81', 'f81',
    'Voorwegwijzer uitwijking',
    'Advance detour sign',
    'Panneau avance de deviation',
    'علامة انحراف مسبقة',
    'Vooraankondiging van een uitwijking.',
    'Advance notice of a detour.',
    'Annonce avancee d''une deviation.',
    'إعلان مسبق بانحراف.',
    'images/signs/road_markings/F81 Voorwegwijzer uitwijking.png',
    'images/signs/road_markings/F81 Voorwegwijzer uitwijking.png',
    1, NOW(), NOW(),
    'Dit tijdelijke verkeersbord kondigt aan dat er verderop een uitwijking of omleiding is. Bestuurders worden gewaarschuwd zodat ze zich tijdig kunnen voorbereiden om de aangewezen uitwijkroute te volgen.',
    'This temporary traffic sign announces that there is a deviation or detour ahead. Drivers are warned in advance so they can prepare to follow the designated detour route.',
    'Ce panneau de signalisation temporaire annonce une deviation ou un detournement plus loin. Les conducteurs sont avertis a l''avance afin de pouvoir se preparer a suivre l''itineraire de deviation designe.',
    'تشير هذه العلامة المرورية المؤقتة إلى وجود انحراف أو تحويل للطريق في الأمام. يتم تحذير السائقين مسبقاً حتى يتمكنوا من الاستعداد لاتباع مسار الانحراف المحدد.'
),

-- F83
(
    (SELECT id FROM categories WHERE code = 'F'),
    'F83', 'f83',
    'Versmalling van de rijbaan',
    'Road narrowing',
    'Retrecissement de la chaussee',
    'تضييق الطريق',
    'Versmalling van de rijbaan.',
    'Road narrowing ahead.',
    'Retrecissement de la chaussee.',
    'تضييق في الطريق قادم.',
    'images/signs/road_markings/F83 Versmalling van de rijbaan.png',
    'images/signs/road_markings/F83 Versmalling van de rijbaan.png',
    1, NOW(), NOW(),
    'Dit tijdelijke verkeersbord geeft aan dat het tijdelijke obstakel op de weg (zoals barrières of wegwerkzaamheden) zal eindigen en de afgesloten rijstrook na 1500 meter weer wordt geopend. Dit bord markeert het einde van een verboden of afgesloten zone.',
    'This temporary traffic sign indicates that the temporary obstacle on the road (such as barriers or roadworks) will end and the closed lane will reopen after 1500 meters. This sign marks the end of a prohibited or closed zone.',
    'Ce panneau de signalisation temporaire indique que l''obstacle temporaire sur la route (comme les barrieres ou les travaux) se terminera et que la voie fermee rouvrira apres 1500 metres. Ce panneau marque la fin d''une zone interdite ou fermee.',
    'تشير هذه العلامة المرورية المؤقتة إلى أن العائق المؤقت على الطريق (مثل الحواجز أو أعمال الطرق) سينتهي وسيعاد فتح المسار المغلق بعد 1500 متر. تشير هذه العلامة إلى نهاية المنطقة المحظورة أو المغلقة.'
),

-- F85
(
    (SELECT id FROM categories WHERE code = 'F'),
    'F85', 'f85',
    'Verlegging van de rijbaan',
    'Lane shift',
    'Deviement de voie',
    'انحراف المسار',
    'Verlegging van de rijbaan.',
    'Lane shift ahead.',
    'Deviement de voie.',
    'انحراف في مسار الطريق.',
    'images/signs/road_markings/F85 Verlegging van de rijbaan.png',
    'images/signs/road_markings/F85 Verlegging van de rijbaan.png',
    1, NOW(), NOW(),
    'Dit tijdelijke verkeersbord waarschuwt dat de linkerrijstrook na een bepaalde afstand (1500 meter) zal verdwijnen. Bestuurders in deze rijstrook moeten naar rechts overgaan. Dit bord wordt gebruikt in tijdelijke werkzones om het verkeer veilig te begeleiden.',
    'This temporary traffic sign warns that the left lane will disappear after a certain distance (1500 meters). Drivers in this lane must move to the right. This sign is used in temporary work zones to safely guide traffic.',
    'Ce panneau de signalisation temporaire avertit que la voie de gauche disparaitra apres une certaine distance (1500 metres). Les conducteurs sur cette voie doivent se deplacer vers la droite. Ce panneau est utilise dans les zones de travaux temporaires pour guider le trafic en toute securite.',
    'تحذر هذه العلامة المرورية المؤقتة من أن المسار الأيسر سوف يختفي بعد مسافة معينة (1500 متر). يجب على السائقين في هذا المسار التحرك إلى اليمين. تُستخدم هذه العلامة في مناطق العمل المؤقتة لتوجيه حركة المرور بشكل آمن.'
),

-- F89
(
    (SELECT id FROM categories WHERE code = 'F'),
    'F89', 'f89',
    'Aanduiding van de maximumsnelheid per rijstrook',
    'Maximum speed per lane with distance indication',
    'Vitesse maximale par voie avec indication de distance',
    'الحد الأقصى للسرعة لكل حارة مع إشارة المسافة',
    'Maximumsnelheid per rijstrook met afstandsaanduiding.',
    'Speed limit per lane with distance indication.',
    'Vitesse maximale par voie avec distance.',
    'سرعة قصوى لكل حارة مع مسافة.',
    'images/signs/road_markings/F89 Aanduiding van de maximumsnelheid per rijstrook.png',
    'images/signs/road_markings/F89 Aanduiding van de maximumsnelheid per rijstrook.png',
    1, NOW(), NOW(),
    'Dit verkeersbord heeft hetzelfde doel als F91, maar wordt gebruikt om bestuurders vooraf te waarschuwen dat de verschillende snelheden per rijstrook na 1500 meter van toepassing zullen zijn. Het geeft tijd om van rijstrook te wisselen indien nodig.',
    'This traffic sign has the same purpose as F91, but is used to warn drivers in advance that the different speeds per lane will apply after 1500 meters. It gives time to change lanes if needed.',
    'Ce panneau de signalisation a le meme objectif que F91, mais est utilise pour avertir les conducteurs a l''avance que les differentes vitesses par voie s''appliqueront apres 1500 metres. Il donne le temps de changer de voie si necessaire.',
    'هذه العلامة المرورية لها نفس غرض F91، ولكنها تستخدم لتحذير السائقين مسبقاً من أن السرعات المختلفة لكل حارة سيتم تطبيقها بعد 1500 متر. يعطي الوقت لتغيير المسارات إذا لزم الأمر.'
),

-- F91
(
    (SELECT id FROM categories WHERE code = 'F'),
    'F91', 'f91',
    'Aanduiding van de maximumsnelheid per rijstrook (zonder afstand)',
    'Maximum speed per lane indication',
    'Indication de vitesse maximale par voie',
    'تحديد السرعة القصوى حسب المسارات',
    'Maximumsnelheid per rijstrook.',
    'Maximum speed per lane.',
    'Vitesse maximale par voie.',
    'السرعة القصوى لكل حارة.',
    'images/signs/road_markings/F91 Aanduiding van de maximumsnelheid per rijstrook (zonder afstand).png',
    'images/signs/road_markings/F91 Aanduiding van de maximumsnelheid per rijstrook (zonder afstand).png',
    1, NOW(), NOW(),
    'Dit verkeersbord geeft de maximumsnelheid per rijstrook aan, bijvoorbeeld de linkerrijstrook 70 km/u, de middelste 90 km/u, enzovoort. Dit bord wordt gebruikt op snelwegen met meerdere rijstroken om de verkeersstroom te reguleren.',
    'This traffic sign indicates the maximum speed in each lane of the road, for example the left lane 70 km/h, the middle 90 km/h, and so on. It is used on multi-lane highways to regulate traffic flow.',
    'Ce panneau de signalisation indique la vitesse maximale dans chaque voie de la route, par exemple la voie de gauche 70 km/h, celle du milieu 90 km/h, et ainsi de suite. Il est utilise sur les autoroutes a plusieurs voies pour reguler le flux de circulation.',
    'تشير هذه العلامة المرورية إلى السرعة القصوى في كل حارة من الطريق، مثلاً الحارة اليسرى 70 كم/ساعة، الوسطى 90 كم/ساعة وهكذا. يتم استخدامه على الطرق السريعة متعددة المسارات لتنظيم تدفق حركة المرور.'
),

-- F95
(
    (SELECT id FROM categories WHERE code = 'F'),
    'F95', 'f95',
    'Einde van een rijstrook',
    'End of a lane',
    'Fin d''une voie',
    'نهاية الحارة',
    'Einde van een rijstrook.',
    'End of a lane.',
    'Fin d''une voie.',
    'نهاية الحارة.',
    'images/signs/road_markings/F95 Einde van een rijstrook.png',
    'images/signs/road_markings/F95 Einde van een rijstrook.png',
    1, NOW(), NOW(),
    'Dit verkeersbord geeft aan dat er een afrit is naar een servicegebied langs de snelweg, zoals een tankstation of rustplaats. De zijlijn toont de richting van de afrit naar de zijweg.',
    'This traffic sign indicates an exit leading to a service area on the highway, such as a gas station or rest area. The side line shows the direction of the deviation towards the side road.',
    'Ce panneau de signalisation indique une sortie menant a une zone de services sur l''autoroute, comme une station-service ou une aire de repos. La ligne laterale montre la direction de la deviation vers la voie laterale.',
    'تشير هذه العلامة المرورية إلى مخرج يؤدي إلى منطقة خدمة على الطريق السريع، مثل محطة وقود أو منطقة استراحة. يوضح الخط الجانبي اتجاه الانحراف نحو الطريق الجانبي.'
),

-- F98
(
    (SELECT id FROM categories WHERE code = 'F'),
    'F98', 'f98',
    'Bijzondere rijstrookregeling',
    'Special lane regulation',
    'Reglementation speciale de voie',
    'لوائح خاصة بالحارة',
    'Bijzondere rijstrookregeling.',
    'Special lane regulation.',
    'Reglementation speciale de voie.',
    'لوائح خاصة بالحارة.',
    'images/signs/road_markings/F98 Bijzondere rijstrookregeling.png',
    'images/signs/road_markings/F98 Bijzondere rijstrookregeling.png',
    1, NOW(), NOW(),
    'Dit verkeersbord wijst de bestuurder naar een speciale noodstrook aan de zijkant van de snelweg, bedoeld voor noodgevallen zoals een mechanisch defect of om hulp te vragen. Alleen te gebruiken in noodsituaties.',
    'This traffic sign directs the driver to a designated emergency area on the side of the highway, for example for stopping in case of mechanical breakdown or requesting assistance. Only to be used in emergency situations.',
    'Ce panneau de signalisation dirige le conducteur vers une zone d''urgence designee sur le cote de l''autoroute, par exemple pour s''arreter en cas de panne mecanique ou pour demander de l''aide. A utiliser uniquement en cas d''urgence.',
    'تقوم هذه العلامة المرورية بتوجيه السائق إلى منطقة طوارئ مخصصة على جانب الطريق السريع، مثلاً للتوقف في حالة حدوث عطل ميكانيكي أو طلب المساعدة. يستخدم فقط في حالات الطوارئ.'
);
