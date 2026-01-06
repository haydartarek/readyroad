import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/di/service_locator.dart';
import '../../core/providers/language_provider.dart';
import '../../shared/models/traffic_sign.dart';
import '../signs/traffic_sign_service.dart';
import '../categories/sign_details_screen.dart';

/// Search Screen - Search for traffic signs
class SearchScreen extends StatefulWidget {
  const SearchScreen({super.key});

  @override
  State<SearchScreen> createState() => _SearchScreenState();
}

class _SearchScreenState extends State<SearchScreen> {
  final TrafficSignService _signService = getIt<TrafficSignService>();
  final TextEditingController _searchController = TextEditingController();

  List<TrafficSign> _allSigns = [];
  List<TrafficSign> _filteredSigns = [];
  bool _isLoading = true;
  String? _error;
  String _searchQuery = '';

  @override
  void initState() {
    super.initState();
    _loadAllSigns();
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  Future<void> _loadAllSigns() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    try {
      final signs = await _signService.getAllTrafficSigns();
      setState(() {
        _allSigns = signs;
        _filteredSigns = signs;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = e.toString();
        _isLoading = false;
      });
    }
  }

  void _filterSigns(String query) {
    setState(() {
      _searchQuery = query;

      if (query.isEmpty) {
        _filteredSigns = _allSigns;
      } else {
        final languageCode = context.read<LanguageProvider>().currentLanguage;
        _filteredSigns = _allSigns.where((sign) {
          final name = sign.getName(languageCode).toLowerCase();
          final code = sign.code.toLowerCase();
          final searchLower = query.toLowerCase();

          return name.contains(searchLower) || code.contains(searchLower);
        }).toList();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: TextField(
          controller: _searchController,
          autofocus: true,
          decoration: InputDecoration(
            hintText: 'Search traffic signs...',
            border: InputBorder.none,
            hintStyle: TextStyle(
              color: Colors.white.withOpacity(0.7),
            ),
            suffixIcon: _searchQuery.isNotEmpty
                ? IconButton(
                    icon: const Icon(Icons.clear, color: Colors.white),
                    onPressed: () {
                      _searchController.clear();
                      _filterSigns('');
                    },
                  )
                : null,
          ),
          style: const TextStyle(color: Colors.white),
          onChanged: _filterSigns,
        ),
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
            const SizedBox(height: 24),
            ElevatedButton.icon(
              onPressed: _loadAllSigns,
              icon: const Icon(Icons.refresh),
              label: const Text('Retry'),
            ),
          ],
        ),
      );
    }

    if (_searchQuery.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.search,
              size: 100,
              color: Colors.grey[400],
            ),
            const SizedBox(height: 24),
            Text(
              'Search for traffic signs',
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: 8),
            Text(
              'Enter sign name or code',
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: Colors.grey[600],
                  ),
            ),
          ],
        ),
      );
    }

    if (_filteredSigns.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.search_off,
              size: 100,
              color: Colors.grey[400],
            ),
            const SizedBox(height: 24),
            Text(
              'No results found',
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: 8),
            Text(
              'Try a different search term',
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: Colors.grey[600],
                  ),
            ),
          ],
        ),
      );
    }

    return Consumer<LanguageProvider>(
      builder: (context, languageProvider, child) {
        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: Text(
                '${_filteredSigns.length} results found',
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                      color: Colors.grey[600],
                    ),
              ),
            ),
            Expanded(
              child: ListView.builder(
                itemCount: _filteredSigns.length,
                itemBuilder: (context, index) {
                  final sign = _filteredSigns[index];
                  return _buildSignListItem(sign, languageProvider.currentLanguage);
                },
              ),
            ),
          ],
        );
      },
    );
  }

  Widget _buildSignListItem(TrafficSign sign, String languageCode) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: ListTile(
        leading: Container(
          width: 60,
          height: 60,
          decoration: BoxDecoration(
            color: Colors.grey[200],
            borderRadius: BorderRadius.circular(8),
          ),
          child: sign.imageUrl != null
              ? Image.network(
                  sign.imageUrl!,
                  fit: BoxFit.contain,
                  errorBuilder: (context, error, stackTrace) {
                    return const Icon(Icons.traffic, color: Colors.grey);
                  },
                )
              : const Icon(Icons.traffic, color: Colors.grey),
        ),
        title: Text(
          sign.getName(languageCode),
          style: const TextStyle(fontWeight: FontWeight.bold),
        ),
        subtitle: Text(sign.code),
        trailing: const Icon(Icons.arrow_forward_ios, size: 16),
        onTap: () {
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => SignDetailsScreen(sign: sign),
            ),
          );
        },
      ),
    );
  }
}

