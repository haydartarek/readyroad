import json

with open('signs.json', 'r', encoding='utf-8') as f:
    signs = json.load(f)

# اللافتات المفقودة التي يجب إضافتها
new_signs = [
    {
        'signCode': 'ZC5',
        'imagePath': 'images/signs/zone_signs/ZC5.png',
        'category': 'zone_signs',
        'label_nl': 'Zone verboden toegang voor motorvoertuigen met meer dan 2 wielen',
        'label_fr': 'Zone interdite aux véhicules à moteur à plus de 2 roues',
        'label_ar': 'منطقة ممنوعة للمركبات ذات المحركات بأكثر من عجلتين',
        'label_en': 'Zone prohibited for motor vehicles with more than 2 wheels'
    },
    {
        'signCode': 'ZC21T',
        'imagePath': 'images/signs/zone_signs/ZC21T.png',
        'category': 'zone_signs',
        'label_nl': 'Zone verboden toegang voor bestuurders van voertuigen waarvan de massa hoger is dan de aangeduide massa',
        'label_fr': 'Zone interdite aux conducteurs de véhicules dont la masse est supérieure à la masse indiquée',
        'label_ar': 'منطقة ممنوعة للمركبات التي تتجاوز كتلتها الكتلة المحددة',
        'label_en': 'Zone prohibited for vehicles exceeding indicated mass'
    },
    {
        'signCode': 'ZC35',
        'imagePath': 'images/signs/zone_signs/ZC35.png',
        'category': 'zone_signs',
        'label_nl': 'Zone verboden inhalen',
        'label_fr': 'Zone interdite de dépasser',
        'label_ar': 'منطقة ممنوع التجاوز',
        'label_en': 'Zone no overtaking'
    },
    {
        'signCode': 'F50b',
        'imagePath': 'images/signs/information_signs/F50b.png',
        'category': 'information_signs',
        'label_nl': 'Opgepast als je van richting verandert',
        'label_fr': 'Attention lors du changement de direction',
        'label_ar': 'انتبه عند تغيير الاتجاه',
        'label_en': 'Caution when changing direction'
    },
    {
        'signCode': 'F45b',
        'imagePath': 'images/signs/information_signs/F45b.png',
        'category': 'information_signs',
        'label_nl': 'Doodlopende weg',
        'label_fr': 'Impasse',
        'label_ar': 'طريق مسدود',
        'label_en': 'Dead end'
    },
    {
        'signCode': 'M12-richtingen',
        'imagePath': 'images/signs/additional_signs/M12-richtingen.png',
        'category': 'additional_signs',
        'label_nl': 'Parkeren in verschillende richtingen',
        'label_fr': 'Stationnement dans différentes directions',
        'label_ar': 'وقوف السيارات في اتجاهات مختلفة',
        'label_en': 'Parking in different directions'
    },
    {
        'signCode': 'GVIId-PR',
        'imagePath': 'images/signs/additional_signs/GVIId-PR.png',
        'category': 'additional_signs',
        'label_nl': 'Park & Ride',
        'label_fr': 'Park & Ride',
        'label_ar': 'ركن وانتقل',
        'label_en': 'Park & Ride'
    },
    {
        'signCode': 'GIII-aquaplaning',
        'imagePath': 'images/signs/additional_signs/GIII-aquaplaning.png',
        'category': 'additional_signs',
        'label_nl': 'Opgepast kans op aquaplaning',
        'label_fr': 'Attention risque aquaplanage',
        'label_ar': 'انتبه خطر انزلاق الماء',
        'label_en': 'Caution risk of aquaplaning'
    },
    {
        'signCode': 'GVIId-CARPOOL',
        'imagePath': 'images/signs/additional_signs/GVIId-CARPOOL.png',
        'category': 'additional_signs',
        'label_nl': 'Carpool parkeren',
        'label_fr': 'Stationnement covoiturage',
        'label_ar': 'وقوف السيارات المشتركة',
        'label_en': 'Carpool parking'
    }
]

# إضافة اللافتات الجديدة
signs.extend(new_signs)

# حفظ الملف
with open('signs.json', 'w', encoding='utf-8') as f:
    json.dump(signs, f, ensure_ascii=False, indent=2)

print('✅ تمت إضافة ' + str(len(new_signs)) + ' لافتة جديدة إلى signs.json!')
print('')
print('📋 اللافتات المضافة:')
for sign in new_signs:
    print('  - ' + sign['signCode'] + ': ' + sign['label_nl'])
