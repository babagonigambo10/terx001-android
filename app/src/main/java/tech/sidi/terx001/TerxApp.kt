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
