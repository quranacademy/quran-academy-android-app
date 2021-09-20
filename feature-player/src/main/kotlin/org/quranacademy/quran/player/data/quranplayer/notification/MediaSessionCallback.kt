package org.quranacademy.quran.player.data.quranplayer.notification

import android.support.v4.media.session.MediaSessionCompat
import org.quranacademy.quran.player.data.quranplayer.service.PlayerServiceControl
import javax.inject.Inject

class MediaSessionCallback @Inject constructor(
        private val playbackServiceControl: PlayerServiceControl
) : MediaSessionCompat.Callback() {

    override fun onPlay() {
        playbackServiceControl.resume()
    }

    override fun onStop() {
        playbackServiceControl.stop()
    }

    override fun onPause() {
        playbackServiceControl.pause()
    }

    override fun onSkipToPrevious() {
        playbackServiceControl.prevAyah()
    }

    override fun onSkipToNext() {
        playbackServiceControl.nextAyah()
    }

    companion object {

        private val TAG = "MediaSessionCallback"

    }

}