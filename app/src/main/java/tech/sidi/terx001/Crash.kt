// SPDX-License-Identifier: GPL-3.0-only
// TERX001 - Copyright (C) 2026 SIDI TECH
// This program is free software under the GNU GPL version 3. See the LICENSE file.

package tech.sidi.terx001

import android.content.Context
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

private fun crashFile(ctx: Context) = File(ctx.filesDir, "last_crash.txt")

/** Saves the stack trace of a crash on the phone only; shown inside the app on the next launch. */
fun recordCrash(ctx: Context, e: Throwable) {
    val sw = StringWriter()
    e.printStackTrace(PrintWriter(sw))
    crashFile(ctx).writeText(sw.toString().take(6000))
}

fun installCrashRecorder(ctx: Context) {
    val previous = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { thread, e ->
        try {
            recordCrash(ctx, e)
        } catch (_: Throwable) {
        }
        previous?.uncaughtException(thread, e)
    }
}

fun readCrash(ctx: Context): String? = crashFile(ctx).takeIf { it.exists() }?.readText()

fun clearCrash(ctx: Context) {
    crashFile(ctx).delete()
}
