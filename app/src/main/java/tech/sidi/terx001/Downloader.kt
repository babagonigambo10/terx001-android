package tech.sidi.terx001

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URI

/** Spike scope: https links on tiktok.com and its subdomains (vm., vt., www.) only. */
fun isTikTokUrl(raw: String): Boolean {
    val uri = try {
        URI(raw.trim())
    } catch (e: Exception) {
        return false
    }
    if (uri.scheme != "https") return false
    val host = uri.host?.lowercase() ?: return false
    return host == "tiktok.com" || host.endsWith(".tiktok.com")
}

/**
 * Downloads with yt-dlp into app cache, then copies into the public
 * Download/TERX001 folder through MediaStore (no storage permission needed).
 * Returns the saved path for display.
 */
suspend fun downloadToDownloads(
    ctx: Context,
    url: String,
    onProgress: (Float, Long) -> Unit,
): String = withContext(Dispatchers.IO) {
    val dir = File(ctx.cacheDir, "dl")
    dir.deleteRecursively()
    dir.mkdirs()

    try {
        val request = YoutubeDLRequest(url.trim()).apply {
            addOption("--no-playlist")
            addOption("--no-mtime")
            addOption("-f", "best[ext=mp4]/best")
            addOption("-o", dir.absolutePath + "/%(id)s.%(ext)s")
        }
        YoutubeDL.getInstance().execute(request, null) { progress, eta, _ ->
            onProgress(progress, eta)
        }

        val file = dir.listFiles()
            ?.filter { it.isFile && !it.name.endsWith(".part") }
            ?.maxByOrNull { it.lastModified() }
            ?: throw IllegalStateException("yt-dlp finished but produced no file")

        val mime = when (file.extension.lowercase()) {
            "mp4" -> "video/mp4"
            "webm" -> "video/webm"
            "mkv" -> "video/x-matroska"
            else -> "application/octet-stream"
        }
        val displayName = "TERX001_" + file.name
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, displayName)
            put(MediaStore.Downloads.MIME_TYPE, mime)
            put(MediaStore.Downloads.RELATIVE_PATH, "Download/TERX001")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        val resolver = ctx.contentResolver
        val target = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: throw IllegalStateException("Could not create a file in Downloads")
        try {
            val out = resolver.openOutputStream(target)
                ?: throw IllegalStateException("Could not open the Downloads file for writing")
            out.use { o -> file.inputStream().use { it.copyTo(o) } }
            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(target, values, null, null)
        } catch (e: Exception) {
            resolver.delete(target, null, null)
            throw e
        }
        "Download/TERX001/$displayName"
    } finally {
        dir.deleteRecursively()
    }
}

/** yt-dlp stderr mixes warnings with the real failure; show the ERROR line(s) first. */
fun friendlyError(e: Throwable): String {
    val raw = e.message ?: e.javaClass.simpleName
    val lines = raw.lines().map { it.trim() }.filter { it.isNotEmpty() }
    val errors = lines.filter { it.startsWith("ERROR") }
    val pick = errors.ifEmpty { lines.takeLast(3) }
    return (if (pick.isEmpty()) raw else pick.joinToString("\n")).take(400)
}

/** Updates the bundled yt-dlp in place (stored in app files, survives restarts). */
suspend fun updateYtDlp(ctx: Context): String = withContext(Dispatchers.IO) {
    val status = YoutubeDL.getInstance().updateYoutubeDL(ctx)
    val version = YoutubeDL.getInstance().version(ctx) ?: "unknown"
    "yt-dlp $version ($status)"
}
