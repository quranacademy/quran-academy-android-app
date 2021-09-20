package org.quranacademy.quran.presentation.systemmanager

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

class CopyTextCommand(
        private val text: String
) : SystemManagerCommand {

    override fun execute(activity: Activity) {
        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
    }

}