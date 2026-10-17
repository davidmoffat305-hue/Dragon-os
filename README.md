# Dragon SO: Android-based TV operating system

Dragon SO is a TV-specific build of Android (AOSP, Android TV flavor) with its own
home screen (the Dragon launcher), branding and system properties.

## Layout
- launcher/ : Dragon SO home screen (Kotlin + Jetpack Compose, D-pad navigation)
- vendor/dragon/dragon_tv/ : AOSP product definition that bakes the launcher into the system image

## 1. Try the launcher now (no AOSP needed)
1. Open launcher/ in Android Studio, or run ./gradlew assembleDebug
2. Create a "Television (1080p)" emulator, or use a real Android TV device
3. adb install -r launcher/app/build/outputs/apk/debug/app-debug.apk
4. Press Home on the emulator and choose Dragon SO as the default home app

## 2. Build it as a full system image
1. Check out AOSP on a Linux machine (about 400 GB free disk, 32 GB RAM recommended)
2. Copy vendor/dragon into AOSP's vendor/ directory
3. Copy the debug or signed APK to vendor/dragon/dragon_tv/prebuilt/DragonLauncher.apk
4. source build/envsetup.sh && lunch dragon_tv-userdebug && m -j
5. Boot it in the emulator (emulator -no-snapshot) or flash it to a supported board

## Verify against your AOSP branch
Module names and product paths change between Android releases. Check these first:
- the inherited ATV base product path in dragon_tv.mk
- the stock TV launcher module name in Android.bp (overrides)
- the lunch target format (newer releases use dragon_tv-<release>-userdebug)
Running on real TV hardware also needs a device tree for that board (kernel, drivers, bootloader).
