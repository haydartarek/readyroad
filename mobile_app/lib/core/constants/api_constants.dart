/// API Constants for ReadyRoad Backend
class ApiConstants {
  // Base URL - Change this to match your backend
  static const String baseUrl = 'http://localhost:8888';

  // API Endpoints
  static const String apiVersion = '/api/v1';

  // Categories
  static const String categories = '$apiVersion/categories';

  // Traffic Signs
  static const String trafficSigns = '$apiVersion/traffic-signs';

  // Helper method to get full URL
  static String getFullUrl(String endpoint) {
    return '$baseUrl$endpoint';
  }

  // Timeout durations
  static const Duration connectionTimeout = Duration(seconds: 30);
  static const Duration receiveTimeout = Duration(seconds: 30);
}

