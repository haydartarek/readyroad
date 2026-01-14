import '../network/api_client.dart';
import '../constants/api_constants.dart';
import 'dart:convert';

class LessonService {
  final ApiClient _apiClient;

  LessonService(this _apiClient);

  /// Get all lessons
  Future<List<Map<String, dynamic>>> getAllLessons() async {
    final response = await _apiClient.get('${ApiConstants.baseUrl}/api/lessons');
    if (response.statusCode == 200) {
      return List<Map<String, dynamic>>.from(json.decode(response.body));
    }
    throw Exception('Failed to load lessons');
  }

  /// Get lesson by ID
  Future<Map<String, dynamic>> getLessonById(int id) async {
    final response = await _apiClient.get('${ApiConstants.baseUrl}/api/lessons/$id');
    if (response.statusCode == 200) {
      return json.decode(response.body);
    }
    throw Exception('Failed to load lesson');
  }

  /// Get lessons by category
  Future<List<Map<String, dynamic>>> getLessonsByCategory(int categoryId) async {
    final response = await _apiClient.get('${ApiConstants.baseUrl}/api/lessons/by-category?categoryId=$categoryId');
    if (response.statusCode == 200) {
      return List<Map<String, dynamic>>.from(json.decode(response.body));
    }
    throw Exception('Failed to load lessons by category');
  }
}
