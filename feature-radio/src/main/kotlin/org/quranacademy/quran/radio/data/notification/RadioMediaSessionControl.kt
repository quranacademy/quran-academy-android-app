package org.quranacademy.quran.radio.data.notification

import android.support.v4.media.session.MediaSessionCompat
import org.quranacademy.quran.radio.domain.RadioControl
import javax.inject.Inject

class RadioMediaSessionControl @Inject constructor(
        private val playbackServiceControl: RadioControl
) : MediaSessionCompat.Callback() {

    override fun onPlay() {
        playbackServiceControl.playRadio()
    }

    override fun onStop() {
        playbackServiceControl.stopRadio()
    }

    override fun onPause() {
        playbackServiceControl.pauseRadio()
    }

}