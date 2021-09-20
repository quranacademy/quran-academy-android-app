package org.quranacademy.quran.player.data.quranplayer.notification

import org.quranacademy.quran.data.lifecycle.ServiceLifecycleObserver

/**
 * Creates MediaSession [onCreate], and closes MediaSession [onDestroy]
 */
internal class PlaybackServiceMediaSession(
        private val mediaSessionHolder: MediaSessionHolder
) : ServiceLifecycleObserver {

    override fun onCreate() {
        mediaSessionHolder.openSession()
    }

    override fun onDestroy() {
        mediaSessionHolder.closeSession()
    }

}