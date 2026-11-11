# Release security plan (Stage 3) - PLAN ONLY

STATUS (2026-11-11): steps 1-6 below were executed with the owner's approval (Option B, RSA 2048, alias terx001). The four secrets exist; CI signs v* tags; v0.4.0 was built and its signature verified. Steps 5-7 of the release checklist remain pending the owner's confirmation.
Every step marked GATED needs the owner's explicit approval for that step.

## Why this matters
Android only installs an update over an installed app when both APKs are signed with the same key.
If the release key is lost, no one can ever update the app; every user would have to uninstall and
reinstall. The key is the single most important file in this project.

## Current state
Debug and release builds are signed with a shared TEST key (`app/debug-shared.p12`, committed on
purpose, not secret). It must never be used for a public release.

## The plan
1. One permanent release keystore: RSA 4096 (2048 minimum), validity 30 years, alias `terx001`,
   long random passwords. Created once, never regenerated.  GATED
2. Where to generate it (owner picks):
   - Option A, recommended: on the owner's phone with Termux (`pkg install openjdk-17`, then `keytool`).
     The key never passes through any chat or server except GitHub's secret store.
   - Option B, faster: Claude generates it in a sandbox, stores it only in GitHub secrets, and hands the
     owner one copy plus the passwords once, in chat, for the owner to save. The key then appears in
     this conversation history. Acceptable only if the owner is comfortable with that.
3. Two backups, both outside GitHub: an encrypted copy in the owner's password manager and a second
   encrypted copy in a different place (another cloud account or a USB stick). GitHub secrets cannot
   be read back, so they are not a backup.
4. GitHub Actions secrets (repository secrets, never in the code):  GATED
   `KEYSTORE_B64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`.
5. Workflow change (after step 4): the release build reads those secrets and signs only on version tags
   (`v*`); every other build keeps using the test key. Secrets are never exposed to pull requests
   or forks.
6. Verify each release APK with `apksigner verify --print-certs`, and publish the signing certificate's
   SHA-256 fingerprint in the README so users can check it.
7. First release signed with the new key: users on test-key builds must uninstall once. Say so in
   the release notes.
8. Remove any use of `debug-shared.p12` from the release build type at that point.

## Release checklist (after the gates above are approved)
1. Owner confirms legal copyright name (currently "SIDI TECH") and a public support contact.
2. Bump version (suggested: 0.4.0, first public beta) in `app/build.gradle.kts`.
3. Tag the release in git (`v0.4.0`); the tag is the GPL corresponding source for that binary.
4. CI builds and signs the APK; record the APK SHA-256 and the signing-cert fingerprint.
5. Make the repository public.  GATED
6. Publish the APK (GitHub Release first), with the checksum and source tag.  GATED
7. Announce on the @TERX001 channel.  GATED
