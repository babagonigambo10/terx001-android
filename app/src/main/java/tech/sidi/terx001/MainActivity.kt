package tech.sidi.terx001

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
                    HomeScreen(initError)
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(initError: String?) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var url by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var status by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("TERX001", style = MaterialTheme.typography.headlineLarge, color = Emerald)
        Text("SIDI TECH - TikTok spike build", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("TikTok link") },
            singleLine = true,
            enabled = !busy,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))

        Button(
            enabled = !busy && initError == null,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (!isTikTokUrl(url)) {
                    isError = true
                    status = "Paste a full https TikTok link, for example https://vm.tiktok.com/..."
                    return@Button
                }
                busy = true
                isError = false
                progress = 0f
                status = "Starting..."
                scope.launch {
                    try {
                        val saved = downloadToDownloads(ctx, url) { p, _ ->
                            progress = (p / 100f).coerceIn(0f, 1f)
                            status = "Downloading... " + p.toInt() + "%"
                        }
                        status = "Saved to $saved"
                    } catch (e: Exception) {
                        isError = true
                        status = (e.message ?: e.javaClass.simpleName).take(400)
                    } finally {
                        busy = false
                    }
                }
            },
        ) { Text(if (busy) "Downloading..." else "Download") }

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
    }
}
