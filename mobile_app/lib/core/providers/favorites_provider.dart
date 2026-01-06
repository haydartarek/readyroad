import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../shared/models/traffic_sign.dart';

/// Favorites Provider - Manages favorite traffic signs
class FavoritesProvider extends ChangeNotifier {
  final Set<int> _favoriteIds = {};
  bool _isLoaded = false;

  Set<int> get favoriteIds => _favoriteIds;
  bool get isLoaded => _isLoaded;

  FavoritesProvider() {
    _loadFavorites();
  }

  /// Load favorites from local storage
  Future<void> _loadFavorites() async {
    final prefs = await SharedPreferences.getInstance();
    final favorites = prefs.getStringList('favorites') ?? [];
    _favoriteIds.clear();
    _favoriteIds.addAll(favorites.map((id) => int.parse(id)));
    _isLoaded = true;
    notifyListeners();
  }

  /// Check if a sign is favorite
  bool isFavorite(int signId) {
    return _favoriteIds.contains(signId);
  }

  /// Toggle favorite status
  Future<void> toggleFavorite(int signId) async {
    if (_favoriteIds.contains(signId)) {
      _favoriteIds.remove(signId);
    } else {
      _favoriteIds.add(signId);
    }

    await _saveFavorites();
    notifyListeners();
  }

  /// Add to favorites
  Future<void> addFavorite(int signId) async {
    if (!_favoriteIds.contains(signId)) {
      _favoriteIds.add(signId);
      await _saveFavorites();
      notifyListeners();
    }
  }

  /// Remove from favorites
  Future<void> removeFavorite(int signId) async {
    if (_favoriteIds.contains(signId)) {
      _favoriteIds.remove(signId);
      await _saveFavorites();
      notifyListeners();
    }
  }

  /// Save favorites to local storage
  Future<void> _saveFavorites() async {
    final prefs = await SharedPreferences.getInstance();
    final favorites = _favoriteIds.map((id) => id.toString()).toList();
    await prefs.setStringList('favorites', favorites);
  }

  /// Get favorites count
  int get favoritesCount => _favoriteIds.length;

  /// Clear all favorites
  Future<void> clearFavorites() async {
    _favoriteIds.clear();
    await _saveFavorites();
    notifyListeners();
  }
}

