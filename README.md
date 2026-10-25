# Wrench Remote — Android prototype v0.1

Target: Chromecast with Google TV (4K). Native Java Android project (Android Studio / Gradle).

## Implemented
- Dark remote-control layout with D-pad, media, volume, navigation and keyboard placeholders.
- Local network DNS-SD discovery for `_googlecast._tcp.` Google Cast advertisements.
- Connection status and explicit placeholder feedback when buttons are tapped.

## Not yet implemented
- Google TV remote protocol pairing, authenticated TLS session and key-command transport.
- Device selection and persistence, text input, microphone, actual volume/power commands.
- End-to-end device testing and signed APK.

**Important:** Cast discovery alone does NOT provide a functional remote connection. Google TV remote control is a separate protocol. Do not assume a found Cast device is paired or controllable.

## Build
Open the folder in Android Studio with Android SDK 35 and JDK 17 installed. Sync Gradle and run on an Android device. This environment did not have Android SDK/Gradle installed, so build success is not verified.

## Next test at TV
Connect phone and Chromecast to same Wi-Fi, launch prototype, tap Find Google TV, report detected service name and Android version. Then implement and test pairing and authenticated remote commands.

## Phone-only build using GitHub Actions
A GitHub Actions workflow is included at `.github/workflows/build-apk.yml`.
Upload this project to a GitHub repository, preserving the project root layout, then use **Actions → Build Wrench Remote APK → Run workflow**. After the build completes, download the `WrenchRemote-debug-apk` artifact from the run. GitHub may package the APK inside a ZIP; extract it on your phone before installation. This is a **debug APK**, not a Play Store release. Android may ask you to allow installs from your browser/file manager. Only install APKs from your own build.

This workflow has **not been executed or validated** in the current environment. The APK is a discovery/UI prototype; it **cannot yet control or cast to** the Chromecast or Xbox. Xbox Series X DLNA casting, local MP4/MKV serving, audio conversion, and Google TV remote pairing are future work.
