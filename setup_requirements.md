# Setup Requirements

## Required software
- JDK 17
- Android Studio latest stable
- Android SDK Platform 34
- Android SDK Build-Tools 34.x
- Android Emulator or physical Android device

## Project libraries
- Jetpack Compose BOM
- Navigation Compose
- Lifecycle ViewModel Compose
- Kotlin Coroutines
- Hilt
- Room
- Firebase Auth
- Firebase BOM
- Coil

## Manual setup still required
1. Add `google-services.json` to `app/` when Firebase Auth implementation begins
2. Sign in to Android Studio SDK Manager
3. Install SDK packages
4. Sync project
5. Let Android Studio generate Gradle wrapper files if prompted
6. Set `local.properties` with your SDK path
7. After placing `app/google-services.json`, sync again so the Google Services plugin activates automatically

## Notes
- Firebase Auth is required by the SRD
- Room is required for prices, recipes, favourites, health data, and FPO data
- The app should target Kotlin + Compose + MVVM + Repository
- Android Studio is present on this machine, but the SDK/JDK tools are not yet available in PATH from this terminal
- This project should sync with `Gradle 8.7`, not `Gradle 9.x`
- Firebase can be added back once the Firebase project is created and `google-services.json` is available
- The app module now auto-applies the Google Services plugin only when `app/google-services.json` exists
