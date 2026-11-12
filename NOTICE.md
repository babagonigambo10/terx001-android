# Third-party notices

TERX001 is free software: you can redistribute it and modify it under the terms of the
GNU General Public License, version 3 only. See [LICENSE](LICENSE).
Copyright (C) 2026 SIDI TECH.

TERX001 includes or depends on the following components. Versions are those resolved in the
release build; the build prints the full resolved list in the GitHub Actions log
(step "List runtime dependencies").

| Component | Version | Licence | Project |
| --- | --- | --- | --- |
| youtubedl-android (library, common) | 0.18.1 | GPL-3.0 | https://github.com/yausername/youtubedl-android |
| yt-dlp (bundled in youtubedl-android, and updated at runtime from the yt-dlp project's releases) | updates daily | Unlicense | https://github.com/yt-dlp/yt-dlp |
| Python runtime (bundled in youtubedl-android) | as packaged by the library | PSF License | https://www.python.org/psf/license/ |
| Jackson (core, databind, annotations) | 2.11.1 | Apache-2.0 | https://github.com/FasterXML/jackson |
| Apache Commons IO | 2.5 | Apache-2.0 | https://commons.apache.org/proper/commons-io/ |
| Apache Commons Compress | 1.12 | Apache-2.0 | https://commons.apache.org/proper/commons-compress/ |
| AndroidX and Jetpack Compose (Compose BOM) | 2024.10.01 | Apache-2.0 | https://developer.android.com/jetpack |
| Kotlin standard library, kotlinx.coroutines, JetBrains annotations | 2.0.21 / 1.9.0 / 23.0.0 | Apache-2.0 | https://kotlinlang.org |
| Google Guava (ListenableFuture, pulled in by AndroidX) | as resolved | Apache-2.0 | https://github.com/google/guava |

The bundled Python runtime may contain further libraries (for example OpenSSL, SQLite and
libffi) under their own licences. Their exact contents are set by the published
youtubedl-android artifacts.

## Licence compatibility
The Apache-2.0 components above are compatible with GPL-3.0 and are used under their own
terms. The combined work is distributed under GPL-3.0.

## Corresponding source
The complete corresponding source of each release is this repository at the git tag for that
release. See the README for build instructions.
