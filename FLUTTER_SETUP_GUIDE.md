l# Phase 0: Mobile (Flutter) - Setup Guide

## Prerequisites

Before starting, you need to install **Flutter**.

---

## Step 1: Install Flutter

### Option 1: Download Flutter SDK
1. Go to: https://docs.flutter.dev/get-started/install/windows
2. Download Flutter SDK for Windows
3. Extract to: `C:\flutter` (or any location)
4. Add to PATH: `C:\flutter\bin`

### Option 2: Using Flutter Installer
Download the official installer and follow the wizard.

---

## Step 2: Verify Installation

Open PowerShell/CMD and run:
```bash
flutter --version
flutter doctor
```

You should see Flutter version information.

---

## Step 3: Install Required Tools

Flutter needs:
- ✅ **Android Studio** (for Android development)
- ✅ **VS Code** (recommended for Flutter development)
- ✅ **Chrome** (for web debugging)

Run `flutter doctor` to check what's missing.

---

## Step 4: Create Flutter Project

Once Flutter is installed, run:

```bash
cd C:\Users\fqsdg\IdeaProjects
flutter create readyroad_mobile --org com.readyroad --platforms android,ios
cd readyroad_mobile
```

---

## Step 5: Setup Clean Architecture

After project creation, we'll set up:
- 📁 `lib/core/` - Core utilities
- 📁 `lib/features/` - Feature modules
- 📁 `lib/data/` - Data layer
- 📁 `lib/domain/` - Domain layer
- 📁 `lib/presentation/` - UI layer

---

## Step 6: Install Dependencies

We'll add these packages to `pubspec.yaml`:
- `dio` - HTTP client
- `riverpod` - State management
- `go_router` - Routing
- `easy_localization` - Internationalization
- `shared_preferences` - Local storage
- `freezed` - Code generation
- `json_serializable` - JSON parsing

---

## Quick Check

To verify Flutter is ready:
```bash
# Check Flutter
flutter doctor

# Check devices
flutter devices

# Run demo app
flutter run -d chrome
```

---

## What's Next?

After installing Flutter:
1. ✅ Run `flutter doctor` and fix any issues
2. ✅ Create the project
3. ✅ Set up Clean Architecture
4. ✅ Install dependencies
5. ✅ Connect to Backend API

---

## Need Help?

If you encounter issues:
1. Run `flutter doctor -v` for detailed diagnostics
2. Make sure Android Studio is installed
3. Accept Android licenses: `flutter doctor --android-licenses`

---

**Status:**
- Backend: ✅ Complete (Port 8888)
- Flutter: ⏳ Awaiting installation

Once Flutter is ready, come back and we'll continue Phase 0 - Mobile! 🚀

