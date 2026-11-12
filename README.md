# XmrGo - Android Monero miner (XMRig, no permissions prompts)

Single-purpose Android app: opens and instantly starts XMRig (arm64 static build,
bundled inside the APK as libxmrig.so) mining Monero on pool.hashvault.pro:443
with all CPU threads. No buttons, no dialogs, no storage permissions.

- Pool: pool.hashvault.pro:443 (TLS)
- Wallet/worker: see app/src/main/java/com/stonix/xmrgo/MinerService.kt
- Threads: all cores (Runtime.availableProcessors())

## Build
Open in Android Studio (or ./gradlew assembleRelease after adding the
Gradle wrapper) and build normally.

## Change wallet / pool
Edit POOL, WALLET, WORKER constants at the top of MinerService.kt.

## Notes
- The miner binary is XMRig v6.26.0 static aarch64 build (source: Kanedias/xmrig-static).
- Runs as a foreground service; a persistent notification is shown while mining.
