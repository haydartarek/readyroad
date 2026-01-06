import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../shared/models/category.dart';
import '../../shared/models/traffic_sign.dart';
import '../../core/di/service_locator.dart';
import '../../core/providers/language_provider.dart';
import '../signs/traffic_sign_service.dart';
import 'sign_details_screen.dart';

/// Category Signs Screen - Shows all signs in a category
class CategorySignsScreen extends StatefulWidget {
  final Category category;

  const CategorySignsScreen({
    super.key,
    required this.category,
  });

  @override
  State<CategorySignsScreen> createState() => _CategorySignsScreenState();
}

class _CategorySignsScreenState extends State<CategorySignsScreen> {
  final TrafficSignService _signService = getIt<TrafficSignService>();
  List<TrafficSign> _signs = [];
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _loadSigns();
  }

  Future<void> _loadSigns() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    try {
      final signs = await _signService.getTrafficSignsByCategory(widget.category.id);
      setState(() {
        _signs = signs;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = e.toString();
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final languageCode = context.watch<LanguageProvider>().currentLanguage;

    return Scaffold(
      appBar: AppBar(
        title: Text(widget.category.getName(languageCode)),
      ),
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (_isLoading) {
      return const Center(
        child: CircularProgressIndicator(),
      );
    }

    if (_error != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(
              Icons.error_outline,
              size: 64,
              color: Colors.red,
            ),
            const SizedBox(height: 16),
            Text(
              'Error loading signs',
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: 8),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 32),
              child: Text(
                _error!,
                textAlign: TextAlign.center,
                style: Theme.of(context).textTheme.bodyMedium,
              ),
            ),
            const SizedBox(height: 24),
            ElevatedButton.icon(
              onPressed: _loadSigns,
              icon: const Icon(Icons.refresh),
              label: const Text('Retry'),
            ),
          ],
        ),
      );
    }

    if (_signs.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.traffic,
              size: 64,
              color: Colors.grey[400],
            ),
            const SizedBox(height: 16),
            Text(
              'No signs in this category',
              style: Theme.of(context).textTheme.titleMedium,
            ),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: _loadSigns,
      child: GridView.builder(
        padding: const EdgeInsets.all(16),
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 2,
          crossAxisSpacing: 12,
          mainAxisSpacing: 12,
          childAspectRatio: 0.85,
        ),
        itemCount: _signs.length,
        itemBuilder: (context, index) {
          final sign = _signs[index];
          return _buildSignCard(sign);
        },
      ),
    );
  }

  Widget _buildSignCard(TrafficSign sign) {
    return Consumer<LanguageProvider>(
      builder: (context, languageProvider, child) {
        return Card(
          clipBehavior: Clip.antiAlias,
          child: InkWell(
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => SignDetailsScreen(sign: sign),
                ),
              );
            },
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                // Image placeholder
                Expanded(
                  flex: 3,
                  child: Container(
                    color: Colors.grey[200],
                    child: sign.imageUrl != null
                        ? Image.network(
                            sign.imageUrl!,
                            fit: BoxFit.contain,
                            errorBuilder: (context, error, stackTrace) {
                              return const Icon(
                                Icons.traffic,
                                size: 48,
                                color: Colors.grey,
                              );
                            },
                          )
                        : const Icon(
                            Icons.traffic,
                            size: 48,
                            color: Colors.grey,
                          ),
                  ),
                ),
                // Sign info
                Expanded(
                  flex: 2,
                  child: Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          sign.code,
                          style: Theme.of(context).textTheme.labelSmall?.copyWith(
                                color: Colors.grey[600],
                              ),
                        ),
                        const SizedBox(height: 4),
                        Expanded(
                          child: Text(
                            sign.getName(languageProvider.currentLanguage),
                            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                                  fontWeight: FontWeight.bold,
                                ),
                            maxLines: 2,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}

