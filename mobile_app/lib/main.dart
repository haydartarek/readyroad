import 'package:flutter/material.dart';
import 'core/di/service_locator.dart';
import 'core/constants/app_theme.dart';
import 'features/home/home_screen.dart';

void main() {
  // Setup Dependency Injection
  setupDependencies();

  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'ReadyRoad',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      home: const HomeScreen(),
    );
  }
}
  }
}
