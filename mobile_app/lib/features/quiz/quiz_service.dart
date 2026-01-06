import 'dart:math';
import '../../shared/models/traffic_sign.dart';
import '../../shared/models/quiz_models.dart';
import '../../core/di/service_locator.dart';
import '../signs/traffic_sign_service.dart';

/// Quiz Service - Generates quiz questions
class QuizService {
  final TrafficSignService _signService = getIt<TrafficSignService>();
  final Random _random = Random();

  /// Generate quiz questions
  Future<List<QuizQuestion>> generateQuiz({
    int? categoryId,
    int questionCount = 10,
  }) async {
    try {
      // Get all signs or signs by category
      List<TrafficSign> allSigns;
      if (categoryId != null) {
        allSigns = await _signService.getTrafficSignsByCategory(categoryId);
      } else {
        allSigns = await _signService.getAllTrafficSigns();
      }

      if (allSigns.length < 4) {
        throw Exception('Not enough signs to generate quiz');
      }

      // Limit question count to available signs
      final actualQuestionCount = min(questionCount, allSigns.length);

      // Shuffle and select signs for questions
      final shuffled = List<TrafficSign>.from(allSigns)..shuffle(_random);
      final selectedSigns = shuffled.take(actualQuestionCount).toList();

      // Generate questions
      final questions = <QuizQuestion>[];
      for (final sign in selectedSigns) {
        questions.add(_generateQuestion(sign, allSigns));
      }

      return questions;
    } catch (e) {
      throw Exception('Failed to generate quiz: $e');
    }
  }

  /// Generate a single question
  QuizQuestion _generateQuestion(TrafficSign correctSign, List<TrafficSign> allSigns) {
    // Get 3 random wrong answers
    final wrongSigns = List<TrafficSign>.from(allSigns)
      ..remove(correctSign)
      ..shuffle(_random);

    final wrongOptions = wrongSigns.take(3).toList();

    // Combine correct and wrong answers
    final options = [correctSign, ...wrongOptions]..shuffle(_random);

    return QuizQuestion(
      correctSign: correctSign,
      options: options,
      questionType: 'image',
    );
  }
}

