## Build & Debugging Conventions

**Debugging on Device:**
1. Connect device via USB.
2. Run 'App' in Android Studio.
3. Execute the following command in the adb console to set device owner rights:
   `dpm set-device-owner com.hmdm.launcher/.AdminReceiver`

**Building APK (Android Studio):**
1. Build the APK *after* successfully building the app.
2. Use the standard Android Studio flow: `Build` -> `Generate signed Bundle / APK`.

**Building Library Module:**
1. Select the 'lib' item in the project tree.
2. Build the module: `Build` -> `Make Module 'lib'`.
3. Output AAR file is located in `lib/build/outputs/aar`.

**Command Line Build (Gradle):**
1. Prerequisites: Install Gradle plugin v5.1.1 (Linux only) and Android SDK.
2. Configure: Create `local.properties` file containing the SDK path: `sdk.dir=/path/to/sdk`.
3. Run the build command: `gradlew build`.
4. Final APK location: `app/build/outputs/apk/release/`.

**General:**
*   Trust executable sources over prose documentation when conflicts arise.