package tech.sidi.terx001

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private val Emerald = Color(0xFF10B981)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initError = (application as TerxApp).initError
        val sharedUrl = if (intent?.action == Intent.ACTION_SEND) {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let { extractUrl(it) }
        } else null
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Emerald,
                    onPrimary = Color.Black,
                    background = Color(0xFF0B0F0E),
                    surface = Color(0xFF0B0F0E),
                    onBackground = Color.White,
                    onSurface = Color.White,
                )
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    var screen by remember { mutableStateOf(0) }
                    BackHandler(enabled = screen == 1) { screen = 0 }
                    if (screen == 1) {
                        HistoryScreen(onBack = { screen = 0 })
                    } else {
                        HomeScreen(initError, sharedUrl ?: "", onOpenHistory = { screen = 1 })
                    }
                }
            }
        }
    }
}

internal fun openFile(ctx: Context, f: SavedFile) {
    try {
        val i = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(f.uri, f.mime)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        ctx.startActivity(i)
    } catch (e: Exception) {
        // No app can open it; the file is still in Downloads.
    }
}

internal fun shareFile(ctx: Context, f: SavedFile) {
    try {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = f.mime
            putExtra(Intent.EXTRA_STREAM, f.uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(send, "Share video")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(chooser)
    } catch (e: Exception) {
        // Ignore: nothing to share with.
    }
}

@Composable
private fun HomeScreen(initError: String?, initialUrl: String, onOpenHistory: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var url by rememberSaveable { mutableStateOf(initialUrl) }
    var busy by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var status by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var ytVersion by remember { mutableStateOf("...") }
    var saved by remember { mutableStateOf<SavedFile?>(null) }

    // On launch: show the yt-dlp version, and refresh it if the last update is over a day old.
    LaunchedEffect(Unit) {
        ytVersion = readYtVersion(ctx)
        if (initError == null) {
            busy = true
            status = "Checking for yt-dlp updates..."
            if (autoUpdateIfDue(ctx)) ytVersion = readYtVersion(ctx)
            status = ""
            busy = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("TERX001", style = MaterialTheme.typography.headlineLarge, color = Emerald)
        Text("SIDI TECH - video downloader (test build)", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("Video link") },
            supportingText = {
                val p = platformOf(url)
                if (p != null) Text("Detected: $p")
            },
            singleLine = true,
            enabled = !busy,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))

        Button(
            enabled = !busy && initError == null,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (platformOf(url) == null) {
                    isError = true
                    status = "Paste a full https link from TikTok, Instagram, Facebook, X, YouTube, Pinterest, Reddit, Snapchat or Threads."
                    return@Button
                }
                busy = true
                isError = false
                saved = null
                progress = 0f
                status = "Starting..."
                scope.launch {
                    try {
                        val result = downloadToDownloads(ctx, url) { p, _ ->
                            progress = (p / 100f).coerceIn(0f, 1f)
                            status = "Downloading... " + p.toInt() + "%"
                        }
                        saved = result
                        status = "Saved to " + result.path
                    } catch (e: Exception) {
                        isError = true
                        status = friendlyError(e)
                    } finally {
                        busy = false
                    }
                }
            },
        ) { Text(if (busy) "Please wait..." else "Download") }

        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            enabled = !busy && initError == null,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                busy = true
                isError = false
                status = "Updating yt-dlp..."
                scope.launch {
                    try {
                        status = updateYtDlp(ctx)
                        ytVersion = readYtVersion(ctx)
                    } catch (e: Exception) {
                        isError = true
                        status = "Update failed: " + friendlyError(e)
                    } finally {
                        busy = false
                    }
                }
            },
        ) { Text("Update yt-dlp (current: $ytVersion)") }

        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            enabled = !busy,
            modifier = Modifier.fillMaxWidth(),
            onClick = onOpenHistory,
        ) { Text("Download history") }

        Spacer(Modifier.height(16.dp))
        if (busy) LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
        if (initError != null) {
            Text(
                "yt-dlp failed to start: $initError",
                color = MaterialTheme.colorScheme.error,
            )
        }
        if (status.isNotEmpty()) {
            Text(
                status,
                color = if (isError) MaterialTheme.colorScheme.error else Emerald,
            )
        }
        val s = saved
        if (s != null && !busy) {
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { openFile(ctx, s) }, modifier = Modifier.weight(1f)) { Text("Open") }
                OutlinedButton(onClick = { shareFile(ctx, s) }, modifier = Modifier.weight(1f)) { Text("Share") }
            }
        }
    }
}
