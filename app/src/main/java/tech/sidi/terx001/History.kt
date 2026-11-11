// SPDX-License-Identifier: GPL-3.0-only
// TERX001 - Copyright (C) 2026 SIDI TECH
// This program is free software under the GNU GPL version 3. See the LICENSE file.

package tech.sidi.terx001

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.text.format.Formatter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.Date

private val Emerald = Color(0xFF10B981)

data class HistoryItem(
    val uri: Uri,
    val name: String,
    val size: Long,
    val addedMs: Long,
    val mime: String,
)

/** Lists videos this app saved in Download/TERX001 (MediaStore, no storage permission needed). */
suspend fun loadHistory(ctx: Context): List<HistoryItem> = withContext(Dispatchers.IO) {
    val result = mutableListOf<HistoryItem>()
    val projection = arrayOf(
        MediaStore.Downloads._ID,
        MediaStore.Downloads.DISPLAY_NAME,
        MediaStore.Downloads.SIZE,
        MediaStore.Downloads.DATE_ADDED,
        MediaStore.Downloads.MIME_TYPE,
    )
    val selection = MediaStore.Downloads.RELATIVE_PATH + " = ?"
    val args = arrayOf("Download/TERX001/")
    val order = MediaStore.Downloads.DATE_ADDED + " DESC"
    ctx.contentResolver.query(
        MediaStore.Downloads.EXTERNAL_CONTENT_URI, projection, selection, args, order
    )?.use { c ->
        val idCol = c.getColumnIndexOrThrow(MediaStore.Downloads._ID)
        val nameCol = c.getColumnIndexOrThrow(MediaStore.Downloads.DISPLAY_NAME)
        val sizeCol = c.getColumnIndexOrThrow(MediaStore.Downloads.SIZE)
        val dateCol = c.getColumnIndexOrThrow(MediaStore.Downloads.DATE_ADDED)
        val mimeCol = c.getColumnIndexOrThrow(MediaStore.Downloads.MIME_TYPE)
        while (c.moveToNext()) {
            result.add(
                HistoryItem(
                    uri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, c.getLong(idCol)),
                    name = c.getString(nameCol) ?: "video",
                    size = c.getLong(sizeCol),
                    addedMs = c.getLong(dateCol) * 1000L,
                    mime = c.getString(mimeCol) ?: "video/mp4",
                )
            )
        }
    }
    result
}

@Composable
fun HistoryScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current
    var history by remember { mutableStateOf<List<HistoryItem>?>(null) }
    LaunchedEffect(Unit) { history = loadHistory(ctx) }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = onBack) { Text("Back") }
            Spacer(Modifier.width(16.dp))
            Text("Download history", style = MaterialTheme.typography.headlineSmall, color = Emerald)
        }
        Spacer(Modifier.height(16.dp))
        val list = history
        when {
            list == null -> Text("Loading...")
            list.isEmpty() -> Text(
                "No downloads yet. Videos you download with TERX001 will appear here, " +
                    "newest first, with Open and Share buttons."
            )
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(list, key = { it.uri.toString() }) { item -> HistoryRow(item) }
            }
        }
    }
}

@Composable
private fun HistoryRow(item: HistoryItem) {
    val ctx = LocalContext.current
    val file = SavedFile(item.uri, item.mime, item.name)
    val details = Formatter.formatShortFileSize(ctx, item.size) + "  |  " +
        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(item.addedMs))
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(item.name, style = MaterialTheme.typography.bodyLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text(details, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { openFile(ctx, file) }, modifier = Modifier.weight(1f)) { Text("Open") }
            OutlinedButton(onClick = { shareFile(ctx, file) }, modifier = Modifier.weight(1f)) { Text("Share") }
        }
    }
}
