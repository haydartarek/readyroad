-- ====================================================================
-- ReadyRoad - Manual Insert For Missing Traffic Signs
-- ====================================================================
-- Purpose:
--   Manual one-off insert script for catalog gaps that are not yet
--   covered by canonical Flyway migrations.
--
-- NOTE:
--   This script targets the live ReadyRoad schema name used by the
--   application: readyroad_prod.
-- ====================================================================

USE readyroad_prod;

-- ====================
-- Pre-check: ensure category 5 exists
-- ====================
SELECT COUNT(*) AS category_check 
FROM categories WHERE id = 5;

-- ====================
-- Insert parking signs
-- ====================
INSERT INTO traffic_signs (
    category_id, sign_code,
    name_nl, name_en, name_fr, name_ar,
    description_nl, description_en, description_fr, description_ar,
    image_url,
    long_description_nl, long_description_en, long_description_fr, long_description_ar,
    is_active, created_at, updated_at
) VALUES
(5, 'E1',
 'Parkeerverbod', 'No parking', 'Interdiction de stationnement', 'ممنوع الوقوف',
 'Dit verkeersbord geeft aan dat parkeren verboden is op deze plaats, maar tijdelijk stilstaan om passagiers te laten in- of uitstappen is wel toegestaan.',
 'This traffic sign indicates that parking is prohibited at this location, but temporary stopping to let passengers in or out is allowed.',
 'Ce panneau indique que le stationnement est interdit à cet endroit, mais un arrêt temporaire pour faire monter ou descendre des passagers est autorisé.',
 'تشير هذه العلامة المرورية إلى أن وقوف السيارات محظور في هذا الموقع، ولكن يسمح بالتوقف المؤقت للسماح للركاب بالدخول أو الخروج.',
 'images/signs/parkeren/E1 Parkeerverbod.png',  -- ✅ unified path
 'Dit verkeersbord geeft aan dat parkeren verboden is op deze plaats, maar tijdelijk stilstaan om passagiers te laten in- of uitstappen is wel toegestaan.',
 'This traffic sign indicates that parking is prohibited at this location, but temporary stopping to let passengers in or out is allowed.',
 'Ce panneau indique que le stationnement est interdit à cet endroit, mais un arrêt temporaire pour faire monter ou descendre des passagers est autorisé.',
 'تشير هذه العلامة المرورية إلى أن وقوف السيارات محظور في هذا الموقع، ولكن يسمح بالتوقف المؤقت للسماح للركاب بالدخول أو الخروج.',
 TRUE, NOW(), NOW()),

(5, 'E3',
 'Stilstaan en parkeren verboden', 'No stopping and parking', 'Arrêt et stationnement interdits', 'ممنوع التوقف والوقوف',  -- ✅ Fixed Arabic
 'Dit verkeersbord geeft aan dat stilstaan en parkeren volledig verboden zijn op deze plaats, om welke reden dan ook.',
 'This traffic sign indicates that stopping and parking are completely prohibited at this location for any reason.',
 'Ce panneau indique que l''arrêt et le stationnement sont totalement interdits à cet endroit, quelle que soit la raison.',  -- ✅ Fixed apostrophe
 'تشير هذه العلامة المرورية إلى أن التوقف والوقوف ممنوع تماماً في هذا الموقع لأي سبب من الأسباب.',
 'images/signs/parkeren/E3 Stilstaan en parkeren verboden.png',
 'Dit verkeersbord geeft aan dat stilstaan en parkeren volledig verboden zijn op deze plaats, om welke reden dan ook. Overtreding leidt tot een verkeersboete.',
 'This traffic sign indicates that stopping and parking are completely prohibited at this location for any reason. Violation results in a traffic fine.',
 'Ce panneau indique que l''arrêt et le stationnement sont totalement interdits à cet endroit, quelle que soit la raison. Une infraction entraîne une amende.',
 'تشير هذه العلامة المرورية إلى أن التوقف والوقوف ممنوع تماماً في هذا الموقع لأي سبب من الأسباب. المخالفة تؤدي إلى غرامة مرورية.',
 TRUE, NOW(), NOW()),

(5, 'E9a',
 'Parkeerplaats voor elektrische voertuigen', 'Parking for electric vehicles', 'Parking pour véhicules électriques', 'مواقف للسيارات الكهربائية',
 'Dit verkeersbord geeft aan dat parkeren alleen is toegestaan voor elektrische voertuigen.',
 'This traffic sign indicates that parking is only allowed for electric vehicles.',
 'Ce panneau de signalisation indique que le stationnement est uniquement autorisé pour les véhicules électriques.',
 'تشير هذه العلامة المرورية إلى أنه يسمح بوقوف السيارات الكهربائية فقط.',
 'images/signs/parkeren/E9a elektrisch laden Parkeerplaats voorbehouden voor het elektrisch opladen van je wagen.png',  -- fixed: was wrong generic parking image
 'Dit verkeersbord geeft aan dat parkeren alleen is toegestaan voor elektrische voertuigen. Deze plek is exclusief bestemd voor auto''s die op elektrische energie rijden en vaak voorzien van een laadpunt.',
 'This traffic sign indicates that parking is only allowed for electric vehicles. This space is exclusively intended for cars that run on electric energy and is often equipped with a charging point.',
 'Ce panneau de signalisation indique que le stationnement est uniquement autorisé pour les véhicules électriques. Cet emplacement est exclusivement destiné aux voitures fonctionnant à l''énergie électrique.',
 'تشير هذه العلامة المرورية إلى أنه يسمح بوقوف السيارات الكهربائية فقط. هذه المساحة مخصصة حصريًا للسيارات التي تعمل بالطاقة الكهربائية وغالبًا ما تكون مجهزة بنقطة شحن.',
 TRUE, NOW(), NOW()),

(5, 'E9a-v2',
 'Parkeren toegelaten', 'Parking allowed', 'Stationnement autorisé', 'وقوف السيارات مسموح به',
 'Dit verkeersbord geeft aan dat parkeren is toegestaan voor alle soorten voertuigen zonder uitzondering.',
 'This traffic sign indicates that parking is allowed for all types of vehicles without exception.',
 'Ce panneau de signalisation indique que le stationnement est autorisé pour tous les types de véhicules sans exception.',
 'تشير هذه العلامة المرورية إلى السماح بوقوف جميع أنواع المركبات دون استثناء.',
 'images/signs/parkeren/E9a - Parkeren toegelaten.png',  -- ✅ unified: was 'assets/'
 'Dit verkeersbord geeft aan dat parkeren is toegestaan voor alle soorten voertuigen zonder uitzondering. Het is niet beperkt tot een specifiek type voertuig.',
 'This traffic sign indicates that parking is allowed for all types of vehicles without exception. It is not limited to a specific type of vehicle.',
 'Ce panneau de signalisation indique que le stationnement est autorisé pour tous les types de véhicules sans exception. Il n''est pas limité à un type de véhicule spécifique.',
 'تشير هذه العلامة المرورية إلى السماح بوقوف جميع أنواع المركبات دون استثناء. ولا يقتصر على نوع معين من المركبات.',
 TRUE, NOW(), NOW()),

(5, 'E9a-v3',
 'Parkeerplaats voor elektrisch opladen', 'Parking space for electric charging', 'Place de stationnement pour recharge électrique', 'مكان لوقوف السيارات للشحن الكهربائي',
 'Dit verkeersbord geeft aan dat de parkeerplaats is gereserveerd voor elektrische voertuigen die moeten opladen.',
 'This traffic sign indicates that the parking space is reserved for electric vehicles that need to charge.',
 'Ce panneau indique que la place de stationnement est réservée aux véhicules électriques qui doivent se recharger.',
 'تشير إشارة المرور هذه إلى أن مكان ركن السيارة مخصص للسيارات الكهربائية التي تحتاج إلى الشحن.',
 'images/signs/parkeren/E9a Elektrisch opladen.png',  -- ✅ unified: was 'assets/'
 'Dit verkeersbord geeft aan dat de parkeerplaats is gereserveerd voor elektrische voertuigen die moeten opladen. De auto moet aangesloten zijn op de laadpaal tijdens het parkeren.',
 'This traffic sign indicates that the parking space is reserved for electric vehicles that need to charge. The car must be connected to the charging station while parked.',
 'Ce panneau indique que la place de stationnement est réservée aux véhicules électriques qui doivent se recharger. La voiture doit être branchée à la borne de recharge pendant le stationnement.',
 'تشير إشارة المرور هذه إلى أن مكان ركن السيارة مخصص للسيارات الكهربائية التي تحتاج إلى الشحن. يجب أن تكون السيارة متصلة بمحطة الشحن أثناء وقوفها.',
 TRUE, NOW(), NOW()),

(5, 'E9a-v6',
 'Parkeren beperkt in tijd met parkeerschijf', 'Time-limited parking with parking disc', 'Stationnement limité dans le temps avec disque', 'وقوف السيارات لفترة محدودة مع قرص وقوف',  -- ✅ Fixed French accent
 'Parkeren is hier toegestaan voor een beperkte tijd (maximaal 30 minuten). Een parkeerschijf is verplicht.',
 'Parking is allowed here for a limited time (maximum 30 minutes). A parking disc is required.',
 'Le stationnement est autorisé ici pour une durée limitée (30 minutes maximum). Un disque de stationnement est obligatoire.',  -- ✅ Fixed French accents
 'يُسمح بوقوف السيارات هنا لفترة محدودة (بحد أقصى 30 دقيقة). مطلوب قرص وقوف السيارات.',
 'images/signs/parkeren/E9a parkeerschijf Parkeren beperkt in tijd, parkeerschijf verplicht.png',  -- ✅ unified: was 'assets/'
 'Parkeren is hier toegestaan voor een beperkte tijd (maximaal 30 minuten). Een parkeerschijf is verplicht. Verlenging van de parkeertijd is niet toegestaan.',
 'Parking is allowed here for a limited time (maximum 30 minutes). A parking disc is required. Extending parking time is not allowed.',
 'Le stationnement est autorisé ici pour une durée limitée (30 minutes maximum). Un disque de stationnement est obligatoire. La prolongation du temps de stationnement n''est pas autorisée.',
 'يُسمح بوقوف السيارات هنا لفترة محدودة (بحد أقصى 30 دقيقة). مطلوب قرص وقوف السيارات. لا يسمح بتمديد وقت وقوف السيارات.',
 TRUE, NOW(), NOW()),

(5, 'E9a-v7',
 'Parkeren voor mindervaliden', 'Parking for disabled persons', 'Stationnement pour personnes handicapées', 'مواقف سيارات لذوي الاحتياجات الخاصة',  -- ✅ Fixed French accent
 'Deze parkeerplaats is gereserveerd voor personen met een officiële gehandicaptenkaart.',  -- ✅ Fixed Dutch spelling
 'This parking space is reserved for persons with an official disability card issued by the government.',
 'Cette place de stationnement est réservée aux personnes détenant une carte de handicap officielle.',  -- ✅ Fixed French accents
 'مكان ركن السيارات هذا مخصص للأشخاص الذين يحملون بطاقة إعاقة رسمية صادرة عن الحكومة.',
 'images/signs/parkeren/E9a mindervaliden Parkeren enkel toegelaten voor mindervaliden.png',  -- ✅ unified: was 'assets/'
 'Deze parkeerplaats is gereserveerd voor personen met een officiële gehandicaptenkaart uitgegeven door de overheid. Andere voertuigen mogen hier niet parkeren.',
 'This parking space is reserved for persons with an official disability card issued by the government. Other vehicles are not allowed to park here.',
 'Cette place de stationnement est réservée aux personnes détenant une carte de handicap officielle délivrée par l''état. Les autres véhicules ne peuvent pas stationner ici.',
 'مكان ركن السيارات هذا مخصص للأشخاص الذين يحملون بطاقة إعاقة رسمية صادرة عن الحكومة. لا يُسمح للمركبات الأخرى بالوقوف هنا.',
 TRUE, NOW(), NOW()),

(5, 'E9a-v10',
 'Parkeerzone speciale bestemming', 'Parking zone special destination', 'Zone de stationnement destination spéciale', 'منطقة وقوف خاصة',  -- ✅ Removed misleading sign code prefix from name
 'Parkeerbord: Parkeren toegelaten voor speciale bestemming. Volg de aangegeven parkeerregels.',
 'Parking sign: Parking allowed for special destination. Follow the indicated parking rules.',
 'Panneau de stationnement : stationnement autorisé pour destination spéciale. Suivez les règles indiquées.',
 'علامة وقوف السيارات: وقوف مسموح لوجهة خاصة. اتبع قواعد وقوف السيارات المشار إليها.',
 '',  -- image removed
 'Parkeerbord: Parkeren toegelaten voor speciale bestemming. Volg de aangegeven parkeerregels.',
 'Parking sign: Parking allowed for special destination. Follow the indicated parking rules.',
 'Panneau de stationnement : stationnement autorisé pour destination spéciale. Suivez les règles indiquées.',
 'علامة وقوف السيارات: وقوف مسموح لوجهة خاصة. اتبع قواعد وقوف السيارات المشار إليها.',
 TRUE, NOW(), NOW()),

(5, 'E9b',
 'Parkeren uitsluitend voor auto''s', 'Parking for cars only', 'Stationnement réservé aux voitures', 'مواقف للسيارات الخاصة فقط',
 'Dit verkeersbord geeft aan dat deze parkeerplaats uitsluitend is bestemd voor kleine personenauto''s met een maximaal gewicht van 3,5 ton.',
 'This traffic sign indicates that this parking space is exclusively for small private cars with a maximum weight of 3.5 tons.',
 'Ce panneau indique que cette place de stationnement est exclusivement réservée aux petites voitures particulières d''un poids maximal de 3,5 tonnes.',
 'تشير هذه العلامة المرورية إلى أن هذا المكان مخصص حصراً للسيارات الخاصة الصغيرة التي يبلغ وزنها الأقصى 3.5 طن.',
 'images/signs/parkeren/E9b Parkeren uitsluitend voor auto''s.png',
 'Dit verkeersbord geeft aan dat deze parkeerplaats uitsluitend is bestemd voor kleine personenauto''s met een maximaal gewicht van 3,5 ton. Vrachtwagens of bussen zwaarder dan 3,5 ton mogen hier niet parkeren.',
 'This traffic sign indicates that this parking space is exclusively for small private cars with a maximum weight of 3.5 tons. Trucks or buses heavier than 3.5 tons are not allowed to park here.',
 'Ce panneau indique que cette place de stationnement est exclusivement réservée aux petites voitures particulières d''un poids maximal de 3,5 tonnes. Les camions ou bus de plus de 3,5 tonnes ne peuvent pas stationner ici.',
 'تشير هذه العلامة المرورية إلى أن هذا المكان مخصص حصراً للسيارات الخاصة الصغيرة التي يبلغ وزنها الأقصى 3.5 طن. لا يُسمح للشاحنات أو الحافلات التي يزيد وزنها عن 3.5 طن بالوقوف هنا.',
 TRUE, NOW(), NOW()),

(5, 'E9g-v1',
 'Verplicht parkeren op de rijbaan variant 2', 'Mandatory parking on the road variant 2', 'Stationnement obligatoire sur la route variante 2', 'وقوف إلزامي على الطريق - نوع 2',  -- ✅ Removed sign code prefix, fixed Arabic
 'Parkeerbord: Parkeren uitsluitend voor fietsen en bromfietsen. Volg de aangegeven parkeerregels.',
 'Parking sign: Parking for bicycles and mopeds only. Follow the indicated parking rules.',
 'Panneau de stationnement : stationnement pour vélos et cyclomoteurs uniquement.',
 'علامة وقوف السيارات: وقوف للدراجات والدراجات البخارية فقط.',
 '',  -- image removed
 'Parkeerbord: Parkeren uitsluitend voor fietsen en bromfietsen. Volg de aangegeven parkeerregels.',
 'Parking sign: Parking for bicycles and mopeds only. Follow the indicated parking rules.',
 'Panneau de stationnement : stationnement pour vélos et cyclomoteurs uniquement.',
 'علامة وقوف السيارات: وقوف للدراجات والدراجات البخارية فقط.',
 TRUE, NOW(), NOW()),

(5, 'E9h',
 'Parkeren uitsluitend voor kampeerauto''s', 'Parking for camper vans only', 'Stationnement réservé aux camping-cars', 'مواقف لسيارات التخييم فقط',
 'Deze parkeerplaats is uitsluitend bestemd voor kampeerauto''s en caravans die bedoeld zijn voor kamperen en lange reizen.',
 'This parking space is exclusively for camper vans and caravans intended for camping and long trips.',
 'Cette place de stationnement est exclusivement réservée aux camping-cars et caravanes destinés au camping et aux longs voyages.',
 'مساحة وقوف السيارات هذه مخصصة حصريًا لعربات الكارافانات والكرفانات المخصصة للتخييم والرحلات الطويلة.',
 'images/signs/parkeren/E9h Parkeren uitsluitend voor kampeerauto''s.png',
 'Deze parkeerplaats is uitsluitend bestemd voor kampeerauto''s en caravans die bedoeld zijn voor kamperen en lange reizen. Het is niet toegestaan voor gewone voertuigen om hier te parkeren.',
 'This parking space is exclusively for camper vans and caravans intended for camping and long trips. Regular vehicles are not allowed to park here.',
 'Cette place de stationnement est exclusivement réservée aux camping-cars et caravanes destinés au camping et aux longs voyages. Il n''est pas permis aux véhicules ordinaires de stationner ici.',
 'مساحة وقوف السيارات هذه مخصصة حصريًا لعربات الكارافانات والكرفانات المخصصة للتخييم والرحلات الطويلة. لا يُسمح للمركبات العادية بالوقوف هنا.',
 TRUE, NOW(), NOW()),

(5, 'E11',
 'Halfmaandelijks parkeren', 'Half-monthly parking', 'Stationnement semi-mensuel', 'وقوف نصف شهري',
 'Dit verkeersbord regelt het parkeren volgens de datum van de maand.',
 'This traffic sign regulates parking according to the date of the month.',
 'Ce panneau réglemente le stationnement selon la date du mois.',
 'تنظم هذه العلامة المرورية وقوف السيارات حسب التاريخ من الشهر.',
 'images/signs/parkeren/E11 Halfmaandelijks parkeren in gans de bebouwde kom.png',
 'Dit verkeersbord regelt het parkeren volgens de datum van de maand. Van de 1e tot de 15e is parkeren toegestaan aan de kant met oneven huisnummers, en van de 16e tot het einde van de maand alleen aan de kant met even huisnummers.',
 'This traffic sign regulates parking according to the date of the month. From the 1st to the 15th, parking is allowed on the side with odd house numbers, and from the 16th to the end of the month only on the side with even numbers.',
 'Ce panneau réglemente le stationnement selon la date du mois. Du 1er au 15, le stationnement est autorisé du côté des numéros impairs, et du 16 à la fin du mois uniquement du côté des numéros pairs.',
 'تنظم هذه العلامة المرورية وقوف السيارات حسب التاريخ من الشهر. من اليوم 1 إلى اليوم 15، يُسمح بالوقوف على الجانب الذي به أرقام فردية، ومن اليوم 16 إلى نهاية الشهر فقط على الجانب الذي يحتوي على أرقام زوجية.',
 TRUE, NOW(), NOW());

-- ====================
-- Verify insertion
-- ====================
SELECT 
    sign_code,
    name_nl,
    image_url,
    is_active
FROM traffic_signs
WHERE category_id = 5
ORDER BY sign_code;

SELECT CONCAT('✅ Inserted ', ROW_COUNT(), ' parking signs successfully') AS status;
