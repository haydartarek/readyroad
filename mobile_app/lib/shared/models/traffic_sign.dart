/// Traffic Sign Model
class TrafficSign {
  final int id;
  final String code;
  final String nameEn;
  final String nameAr;
  final String nameNl;
  final String nameFr;
  final String? descriptionEn;
  final String? descriptionAr;
  final String? descriptionNl;
  final String? descriptionFr;
  final String? imageUrl;
  final int categoryId;

  TrafficSign({
    required this.id,
    required this.code,
    required this.nameEn,
    required this.nameAr,
    required this.nameNl,
    required this.nameFr,
    this.descriptionEn,
    this.descriptionAr,
    this.descriptionNl,
    this.descriptionFr,
    this.imageUrl,
    required this.categoryId,
  });

  /// Get name by language code
  String getName(String languageCode) {
    switch (languageCode) {
      case 'ar':
        return nameAr;
      case 'nl':
        return nameNl;
      case 'fr':
        return nameFr;
      default:
        return nameEn;
    }
  }

  /// Get description by language code
  String? getDescription(String languageCode) {
    switch (languageCode) {
      case 'ar':
        return descriptionAr;
      case 'nl':
        return descriptionNl;
      case 'fr':
        return descriptionFr;
      default:
        return descriptionEn;
    }
  }

  /// From JSON
  factory TrafficSign.fromJson(Map<String, dynamic> json) {
    return TrafficSign(
      id: json['id'],
      code: json['code'],
      nameEn: json['nameEn'],
      nameAr: json['nameAr'],
      nameNl: json['nameNl'],
      nameFr: json['nameFr'],
      descriptionEn: json['descriptionEn'],
      descriptionAr: json['descriptionAr'],
      descriptionNl: json['descriptionNl'],
      descriptionFr: json['descriptionFr'],
      imageUrl: json['imageUrl'],
      categoryId: json['categoryId'],
    );
  }

  /// To JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'code': code,
      'nameEn': nameEn,
      'nameAr': nameAr,
      'nameNl': nameNl,
      'nameFr': nameFr,
      'descriptionEn': descriptionEn,
      'descriptionAr': descriptionAr,
      'descriptionNl': descriptionNl,
      'descriptionFr': descriptionFr,
      'imageUrl': imageUrl,
      'categoryId': categoryId,
    };
  }
}

