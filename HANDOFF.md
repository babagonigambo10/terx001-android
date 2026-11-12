# HANDOFF - TERX001 Android

Read this first. Keep it current: update it after every commit (see AGENTS.md).
Last updated: 2026-11-12, v0.4.0 crash on the owner's phone; fix build 0.4.1 on main (8be3f42), not yet tagged.

## 1. Project
TERX001 is a free video downloader for Android by SIDI TECH. The user pastes a link (or shares one
into the app) and the video is downloaded on the phone with yt-dlp, then saved to
`Download/TERX001`. No backend, no server cost, no bot token or secret in the app.
It is the Android companion to the Telegram bot @TERX001bot (separate repo
`babagonigambo10/terx001-bot`, hosted on Railway; do not touch it from here).

- Repo: github.com/babagonigambo10/terx001-android (PUBLIC since 2026-11-11, owner approved; do not change visibility)
- Current version: 0.4.0 (versionCode 5), package `tech.sidi.terx001`; published as GitHub pre-release v0.4.0 (tag -> commit 1df4632)
- Stack: Kotlin, Jetpack Compose (Compose BOM 2024.10.01), AGP 8.7.3, Gradle 8.9 (set in the workflow,
  there is no Gradle wrapper), Kotlin 2.0.21, JDK 17, compileSdk/targetSdk 35, minSdk 29, arm64-v8a only
- Downloader: youtubedl-android `io.github.junkfood02.youtubedl-android:library:0.18.1` (GPL-3.0),
  runs yt-dlp locally. No FFmpeg bundled (keeps the app small; no MP3 yet).
- Features in 0.3.1: link input with platform detection (TikTok, Instagram, Facebook, X, YouTube,
  Pinterest, Reddit, Snapchat, Threads), progress, Open/Share, daily automatic yt-dlp update plus a
  manual update button, accept links shared from other apps, Download history screen, launcher icon.
- Signing: a REAL release key now exists (RSA 2048, alias `terx001`, valid 30 years, created 2026-11-11 with the
  owner's approval, Option B). It lives ONLY in four GitHub Actions secrets (`KEYSTORE_B64`, `KEYSTORE_PASSWORD`,
  `KEY_ALIAS`, `KEY_PASSWORD`) plus the one backup copy handed to the owner in chat. Release builds are signed
  with it only on `v*` tags; every other build still uses the shared TEST key `app/debug-shared.p12`.
  Release certificate SHA-256: `11:16:20:D7:5D:D9:75:51:6A:20:DD:72:B7:7A:23:CC:B3:CF:66:53:8D:ED:1D:FE:AB:72:1E:30:F0:82:D5:E0`. Never print, regenerate, rotate or move this key without owner approval.
- Size: release APK about 21 MB (debug about 44 MB). Most of it is the bundled Python/yt-dlp engine.

### Where the latest APK comes from
GitHub Actions workflow "Build APKs" (`.github/workflows/build.yml`) runs on every push to main.
Latest published release: https://github.com/babagonigambo10/terx001-android/releases/tag/v0.4.0
Assets: `TERX001-v0.4.0.apk` (21,311,632 bytes) and `SHA256SUMS.txt`. APK SHA-256:
`c103d60cb5432b20f39baf13658296a0828cf2e24b2663e487522d3319f1c5fc`. Built by CI from tag v0.4.0 (run 43066292271),
signed with the release key; the workflow fails the build unless the certificate fingerprint matches.
The owner has not yet confirmed a phone test of the signed APK (it will not install over test-key builds).
Workflow also prints APK size, SHA-256 and the resolved dependency list in its log.

## 2. Stage status
Plan agreed with the owner: six stages toward a public GPL-3.0 release. "Waiting for owner" means do not start.

| Stage | Status | Notes / commits |
| --- | --- | --- |
| 1. Finish the current app | DONE | Scaffold to 0.3.1: 8fc66d5 ... 99bd5f2. Key points: working TikTok spike 21de22e; yt-dlp update + error text b01b158, 35c136f; shared debug key 9684c32, 1cabc2a; minified release build 917971d; all platforms + auto-update 557c3ff, a883323; icon 07b6ec9, 5f760bf (0.2.0); history screen cde85f7, 4b0031f, 70591da (0.3.0); status-bar spacing 9b66672, 99bd5f2 (0.3.1). |
| 2. GPL-3.0 compliance | IN PROGRESS (files done, verification open) | LICENSE 5d855db; SPDX headers on the 4 Kotlin files ab6ca1b, 3a16aa9, d1939eb, 9df9d29; README licence + source sections c2d6525; CI prints dependency list ec788f8; NOTICE.md 13a1741. Still open: see section 5. |
| 3. Release security (signing-key strategy) | DONE (key + secrets + CI signing) | Key and 4 secrets created 2026-11-11; CI signs on v* tags: 6322122, 559d30a, 63f6a6d; version 0.4.0 6c621cf; tag v0.4.0 -> 63f6a6d. Owner must keep their backup copy safe. |
| 4. Public product prep (privacy policy, terms/copyright guidance, support contact, store text, screenshots, release notes, checksums) | MOSTLY DONE | docs/PRIVACY.md (support contact = GitHub Issues, 3455add), docs/TERMS.md, docs/STORE_LISTING.md, docs/RELEASE_NOTES.md; README has the signing fingerprint (4cd9a39). Missing: phone screenshots. |
| 5. Distribution readiness (Google Play, Aptoide, APKPure, Softonic review; downloader copyright and platform-terms risk) | NOT STARTED | Research only. No submissions. |
| 6. Public launch (make repo public, publish signed APK, announce on @TERX001) | REPO PUBLIC + RELEASE PUBLISHED; announcement pending | Owner said "Go" on 2026-11-11. Repo made public; GitHub pre-release v0.4.0 published (https://github.com/babagonigambo10/terx001-android/releases/tag/v0.4.0). The @TERX001 post is for the owner to make (Claude has no tool for it). Stage 5 store research not done; no store submissions. |

## 3. Last action and next step
- INCIDENT 2026-11-12: the owner installed the signed v0.4.0 release on a fresh install and it crashes on launch
  ("TERX001 keeps stopping", Xiaomi/Mi phone). Cause not yet confirmed. Suspects: Error types (not Exception) thrown
  by youtubedl-android init/auto-update on a FIRST install of the R8-minified build (fresh installs of the release build
  were never tested; earlier tests were updates over existing installs). Fix on main: catch Throwable everywhere,
  record the crash on the phone and show it in the app on next launch (Crash.kt), scrollable screen, version 0.4.1 (6).
  Build run 43219763230 succeeded (test-key signed, not tagged). Waiting for the owner to test it. Do NOT tag v0.4.1 or
  announce until the owner confirms it launches; ask the owner before tagging. The public v0.4.0 pre-release is known
  broken on at least one phone: tell the owner not to announce it. If 0.4.1 still crashes, ask for the crash text shown
  in the app (or "View summary" on the Android crash dialog) and consider disabling R8 (`isMinifyEnabled = false`) to test.
- Last action: made the release key's CI path verify the fingerprint, added automatic GitHub Release creation on `v*`
  tags, rebuilt v0.4.0, published it, made the repo public, added a README download section.
- Next step (owner-driven):
  1. Owner installs the signed APK from the release and confirms TikTok/Instagram/Snapchat still work.
  2. Owner posts the announcement on @TERX001 (draft text provided in chat on request). Do NOT touch @TERX001bot.
  3. Optional, only if the owner asks: Stage 5 store-policy research (Play, Aptoide, APKPure, Softonic); X/Facebook/YouTube
     testing; screenshots; MP3 (FFmpeg adds ~10-15 MB); verifying the bundled Python/yt-dlp component licences.
  4. For any new release: bump versionCode/versionName, tag `vX.Y.Z`; CI signs, verifies and publishes. Ask the owner first.
- If you have to resume cold: read this file, `README.md`, `.github/workflows/build.yml`, `app/build.gradle.kts`;
  check the latest Actions run and the Releases page; ask the owner.

## 4. Owner decisions
- Public release direction under GPL-3.0 only (`GPL-3.0-only`).
- Copyright line: "Copyright (C) 2026 SIDI TECH" - SIDI TECH is the owner's company name "for now";
  the owner should confirm the final legal name before launch.
- Confirmed working on the owner's phone: TikTok, Instagram, Snapchat (plus Open, Share, History,
  automatic yt-dlp updates).
- NOT confirmed: X, Facebook, YouTube. Pinterest, Reddit, Threads are also untested. Do not claim
  them in any public text until confirmed.
- Wants a small app size. No MP3 and no new platforms for now.
- Repo stays private and the current signing setup stays as is until the owner approves otherwise.

## 5. Hard rules
1. The repository is public. NEVER change its visibility or delete releases/tags without owner approval.
2. NEVER regenerate, rotate, print or move the release signing key or change GitHub secrets without owner approval.
3. NEVER publish a new release, upload or post an APK elsewhere, or submit to any store without owner approval.
4. NEVER post to the @TERX001 Telegram channel and NEVER change @TERX001bot or its repo/Railway project.
5. NEVER start a gated stage or action (key creation, secrets, going public, publishing, announcing)
   without the owner's explicit approval for that specific action. Approval of a direction is not approval of an action.
6. No paid services, backend, database or subscriptions.
7. Test before saying something works. A passing build does not prove downloads work; the owner tests on their phone.
8. The owner works from a phone: keep replies short, give links, no emojis, and make the coding decisions
   (the owner prefers complete working changes over being asked technical questions).
9. Do not add secrets, tokens or personal data to the repo.

## 6. Open items
- Licences of components inside the bundled Python/yt-dlp bundle (libpython.zip.so, about 14 MB) are NOT
  verified. NOTICE.md assumes yt-dlp = Unlicense and Python = PSF, and says the bundle may contain
  OpenSSL, SQLite, libffi etc. Verify against the youtubedl-android artifacts and tighten NOTICE.md before launch.
- Files without the SPDX/copyright header: `build.gradle.kts`, `settings.gradle.kts`,
  `app/build.gradle.kts` (build scripts), plus XML resources, `app/proguard-rules.pro` and the workflow.
  All four Kotlin source files have it. Optional to add to the Gradle scripts.
- LICENSE exists (GPL-3.0 text, 5d855db).
- No git tags or GitHub Releases exist. GPL source-for-binary plan: tag each release (for example `v0.3.1`)
  and link the tag next to every published binary. Do not create release objects without approval.
- Release key exists (see section 1). Key was shown to the owner once in chat as the backup; the owner must store it safely.
- Screenshots not taken; Stage 5 store-policy research not done; repo still private; no GitHub Release published.
- The owner's git email appears in the (now public) commit history; the address was removed from HANDOFF.md going forward.
- Known limits for a public release: arm64-only (older 32-bit phones excluded), minSdk 29 (Android 10+),
  no MP3 (FFmpeg would add roughly 10-15 MB), Instagram often needs login, YouTube blocks downloaders most.
- History screen only lists files saved by the current install (Android scoped storage).
- Downloader copyright/platform-terms risk must be reviewed in Stage 5 before any store submission.

## 7. How work is done here
- No local Android SDK. Edit files through the GitHub Contents API (the previous agent used the Composio
  GitHub connection with `proxy_execute`), push to main, then poll the Actions run and read logs.
- Concurrency in the workflow cancels older runs when several commits land quickly.
- Docs-only commits: put `[skip ci]` in the commit message so Actions does not rebuild.
