# TERX001 Android (SIDI TECH)

Technical spike: paste a TikTok link, download it on the phone, save it to
`Download/TERX001`. No backend, no bot token, no secrets.

## How it works
- Kotlin + Jetpack Compose UI.
- Downloads run locally through [youtubedl-android](https://github.com/yausername/youtubedl-android) 0.18.1 (bundled yt-dlp + Python).
- yt-dlp writes to app cache, then the file is copied to Downloads through MediaStore, so no storage permission is requested.
- Spike limits: TikTok https links only, arm64 only, Android 10+ (minSdk 29).

## Build
Pushes to `main` build a debug APK on GitHub Actions (workflow `Build debug APK`).
Download it from the run page under **Artifacts** > `terx001-debug-apk`
(artifact is private to this repo and expires after 14 days).

## Install on your phone
1. Open the finished Actions run in GitHub, download the artifact zip, extract `app-debug.apk`.
2. Open the APK. Allow "Install unknown apps" for your browser or file manager when asked.
3. Open TERX001, paste a TikTok link, tap Download.

## Not done yet
- yt-dlp self-update from inside the app (needed long term, sites change often).
- Other platforms, quality choice, history, branding, release signing.

## License note
youtubedl-android is GPL-3.0. Distributing this app publicly means releasing its
source under a GPL-compatible license. Private testing is fine.
