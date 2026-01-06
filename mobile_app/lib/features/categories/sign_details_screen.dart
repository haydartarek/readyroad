import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../shared/models/traffic_sign.dart';
import '../../core/providers/favorites_provider.dart';

/// Sign Details Screen - Shows detailed information about a traffic sign
class SignDetailsScreen extends StatelessWidget {
  final TrafficSign sign;

  const SignDetailsScreen({
    super.key,
    required this.sign,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(sign.code),
        actions: [
          Consumer<FavoritesProvider>(
            builder: (context, favoritesProvider, child) {
              final isFavorite = favoritesProvider.isFavorite(sign.id);
              return IconButton(
                icon: Icon(
                  isFavorite ? Icons.favorite : Icons.favorite_border,
                  color: isFavorite ? Colors.red : null,
                ),
                onPressed: () {
                  favoritesProvider.toggleFavorite(sign.id);
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text(
                        isFavorite
                            ? 'Removed from favorites'
                            : 'Added to favorites',
                      ),
                      duration: const Duration(seconds: 1),
                    ),
                  );
                },
              );
            },
          ),
        ],
      ),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Sign Image
            _buildImageSection(),

            // Sign Information
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Code badge
                  Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 12,
                      vertical: 6,
                    ),
                    decoration: BoxDecoration(
                      color: Theme.of(context).primaryColor.withValues(alpha: 0.1),
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: Text(
                      sign.code,
                      style: Theme.of(context).textTheme.labelLarge?.copyWith(
                            color: Theme.of(context).primaryColor,
                            fontWeight: FontWeight.bold,
                          ),
                    ),
                  ),

                  const SizedBox(height: 16),

                  // English Name
                  _buildLanguageSection(
                    context,
                    '🇬🇧 English',
                    sign.nameEn,
                    sign.descriptionEn,
                  ),

                  const Divider(height: 32),

                  // Arabic Name
                  _buildLanguageSection(
                    context,
                    '🇸🇦 العربية',
                    sign.nameAr,
                    sign.descriptionAr,
                  ),

                  const Divider(height: 32),

                  // Dutch Name
                  _buildLanguageSection(
                    context,
                    '🇳🇱 Nederlands',
                    sign.nameNl,
                    sign.descriptionNl,
                  ),

                  const Divider(height: 32),

                  // French Name
                  _buildLanguageSection(
                    context,
                    '🇫🇷 Français',
                    sign.nameFr,
                    sign.descriptionFr,
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
      bottomNavigationBar: _buildBottomBar(context),
    );
  }

  Widget _buildImageSection() {
    return Container(
      height: 250,
      color: Colors.grey[100],
      child: sign.imageUrl != null
          ? Image.network(
              sign.imageUrl!,
              fit: BoxFit.contain,
              errorBuilder: (context, error, stackTrace) {
                return const Center(
                  child: Icon(
                    Icons.traffic,
                    size: 100,
                    color: Colors.grey,
                  ),
                );
              },
            )
          : const Center(
              child: Icon(
                Icons.traffic,
                size: 100,
                color: Colors.grey,
              ),
            ),
    );
  }

  Widget _buildLanguageSection(
    BuildContext context,
    String languageLabel,
    String name,
    String? description,
  ) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          languageLabel,
          style: Theme.of(context).textTheme.labelMedium?.copyWith(
                color: Colors.grey[600],
              ),
        ),
        const SizedBox(height: 8),
        Text(
          name,
          style: Theme.of(context).textTheme.titleLarge?.copyWith(
                fontWeight: FontWeight.bold,
              ),
        ),
        if (description != null && description.isNotEmpty) ...[
          const SizedBox(height: 8),
          Text(
            description,
            style: Theme.of(context).textTheme.bodyMedium,
          ),
        ],
      ],
    );
  }

  Widget _buildBottomBar(BuildContext context) {
    return SafeArea(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Row(
          children: [
            Expanded(
              child: OutlinedButton.icon(
                onPressed: () {
                  // TODO: Practice with this sign
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                      content: Text('Practice feature coming in Phase 2!'),
                      duration: Duration(seconds: 2),
                    ),
                  );
                },
                icon: const Icon(Icons.quiz),
                label: const Text('Practice'),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: ElevatedButton.icon(
                onPressed: () {
                  // TODO: Start quiz
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                      content: Text('Quiz feature coming in Phase 2!'),
                      duration: Duration(seconds: 2),
                    ),
                  );
                },
                icon: const Icon(Icons.play_arrow),
                label: const Text('Take Quiz'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

