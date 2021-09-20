package org.quranacademy.quran.presentation.systemmanager

import android.app.Activity
import androidx.core.app.ShareCompat

class ShareTextCommand(
        private val text: String
) : SystemManagerCommand {

    override fun execute(activity: Activity) {
        val shareIntent = ShareCompat.IntentBuilder.from(activity)
                .setText(text)
                .setType("text/plain")
                .intent

        activity.startActivity(shareIntent)
    }

}