// SPDX-License-Identifier: GPL-3.0-only
// TERX001 - Copyright (C) 2026 SIDI TECH
// This program is free software under the GNU GPL version 3. See the LICENSE file.

package tech.sidi.terx001

import android.app.Application
import com.yausername.youtubedl_android.YoutubeDL

class TerxApp : Application() {
    /** Non-null if the bundled yt-dlp failed to initialise. */
    var initError: String? = null
        private set

    override fun onCreate() {
        super.onCreate()
        try {
            YoutubeDL.getInstance().init(this)
        } catch (e: Exception) {
            initError = e.message ?: e.javaClass.simpleName
        }
    }
}
