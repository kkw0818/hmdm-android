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

## Battery / background behavior (diagnostics)

Use this when MDM agent battery use seems high after install. Most cost comes from **push transport**, **location**, and **15-minute WorkManager** tasks (see `PushNotificationWorker`, `SendDeviceInfoWorker`, `ScheduledAppUpdateWorker`).

### 1. Server / web panel configuration (JSON → `ServerConfig`)

In the Headwind MDM web panel (device configuration / server response), confirm how these fields are set. They map to [app/src/main/java/com/hmdm/launcher/json/ServerConfig.java](app/src/main/java/com/hmdm/launcher/json/ServerConfig.java):

| Field | Effect on device |
|-------|------------------|
| `pushOptions` | `mqttAlarm` / `mqttWorker`: MQTT client + optional foreground `MqttService` (see `BuildConfig.MQTT_SERVICE_FOREGROUND`). `polling` (default): long-running HTTP in `PushLongPollingService` (5 min read timeout in `Const.LONG_POLLING_READ_TIMEOUT`). |
| `keepaliveTime` | MQTT keepalive in seconds (server may send `null`; agent uses defaults in `Const`, e.g. alarm vs worker modes). Worker mode enforces a minimum interval in `PushNotificationMqttWrapper`. |
| `requestUpdates` | Passed to `LocationService` from `MainActivity.startLocationService()`: `gps` / `network` start periodic updates (`LocationService.LOCATION_UPDATE_INTERVAL` = 60s); `stop` turns location off. If the field is omitted or `null`, [ServerConfig.getRequestUpdates()](app/src/main/java/com/hmdm/launcher/json/ServerConfig.java) still resolves to `stop`. |

**Recommended (no server-side location collection):** set in the panel / device JSON explicitly:

```json
"requestUpdates": "stop"
```

Cross-check behavior in code: [Initializer.java](app/src/main/java/com/hmdm/launcher/helper/Initializer.java), [PushLongPollingService.java](app/src/main/java/com/hmdm/launcher/service/PushLongPollingService.java), [LocationService.java](app/src/main/java/com/hmdm/launcher/service/LocationService.java).

### 2. Device / ADB (batterystats)

1. On the device: **Settings → Battery → App usage** (wording varies by OEM). Compare **Mobile network active** vs **CPU** for `com.hmdm.launcher` — high network time suggests long polling or frequent MQTT traffic.
2. Reset stats, reproduce, then dump:

```text
adb shell dumpsys batterystats --reset
adb shell dumpsys batterystats com.hmdm.launcher > batterystats-hmdm.txt
```

Inspect partial wake locks, mobile radio active time, and alarm counts for `com.hmdm.launcher` and related services (e.g. Paho MQTT). For a full battery report (newer Android):

```text
adb bugreport bugreport.zip
```

Then search the archive for `com.hmdm.launcher` and `Estimated power use`.