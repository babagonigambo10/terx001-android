# TERX001 Android (SIDI TECH)

Technical spike: paste a TikTok link, download it on the phone, save it to
`Download/TERX001`. No backend, no bot token, no secrets.

## Download
Latest release: https://github.com/babagonigambo10/terx001-android/releases/tag/v0.4.0
Download `TERX001-v0.4.0.apk` (about 20 MB, Android 10+, 64-bit). Verify it against the checksum and signing
fingerprint on the release page and in the section below.

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

## License
TERX001 is free software, released under the **GNU General Public License v3.0 only**
(see [LICENSE](LICENSE)). Copyright (C) 2026 SIDI TECH.

It is built on [youtubedl-android](https://github.com/yausername/youtubedl-android) (GPL-3.0),
which bundles yt-dlp. Third-party components and their licences are listed in
[NOTICE.md](NOTICE.md).

## Source code
The complete corresponding source for every released APK is this repository, at the git tag
matching the release version (for example `v0.3.1`). Build it with the included Gradle
configuration and the GitHub Actions workflow in `.github/workflows/build.yml`.
No signing key or secret is needed to build an unsigned or debug-signed APK.

## Verifying a release
Official releases are signed with one permanent key. The certificate fingerprint (SHA-256) is:

`11:16:20:D7:5D:D9:75:51:6A:20:DD:72:B7:7A:23:CC:B3:CF:66:53:8D:ED:1D:FE:AB:72:1E:30:F0:82:D5:E0`

Check an APK with `apksigner verify --print-certs app-release.apk`. The release page also lists the
APK's SHA-256 checksum. If the fingerprint differs, do not install it. Builds signed with a different
key (including test builds) will not update an official install.
