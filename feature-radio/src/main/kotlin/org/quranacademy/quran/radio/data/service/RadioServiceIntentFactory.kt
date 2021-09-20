package org.quranacademy.quran.radio.data.service

import android.content.Context
import android.content.Intent
import javax.inject.Inject

class RadioServiceIntentFactory @Inject constructor(
        private val context: Context
) {

    fun intentPlay(): Intent = intentAction(RadioService.ACTION_PLAY)

    internal fun intentPause() = intentAction(RadioService.ACTION_PAUSE)

    internal fun intentStop() = intentAction(RadioService.ACTION_STOP)

    private fun intentAction(action: String): Intent {
        val intent = Intent(context, RadioService::class.java)
        intent.action = action
        return intent
    }

}