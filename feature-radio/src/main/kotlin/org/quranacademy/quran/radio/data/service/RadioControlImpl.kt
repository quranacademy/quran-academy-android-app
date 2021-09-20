package org.quranacademy.quran.radio.data.service

import android.content.Context
import org.quranacademy.quran.radio.domain.RadioControl
import javax.inject.Inject

class RadioControlImpl @Inject constructor(
        private val context: Context,
        private val intentFactory: RadioServiceIntentFactory
) : RadioControl {

    override fun playRadio() {
        context.startService(intentFactory.intentPlay())
    }

    override fun pauseRadio() {
        context.startService(intentFactory.intentPause())
    }

    override fun stopRadio() {
        context.startService(intentFactory.intentStop())
    }

}