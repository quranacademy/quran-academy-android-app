package org.quranacademy.quran.radio.data.notification

import org.quranacademy.quran.data.lifecycle.ServiceLifecycleObserver

/**
 * Creates MediaSession [onCreate], and closes MediaSession [onDestroy]
 */
internal class RadioServiceMediaSession(
        private val mediaSessionHolder: RadioMediaSessionHolder
) : ServiceLifecycleObserver {

    override fun onCreate() {
        mediaSessionHolder.openSession()
    }

    override fun onDestroy() {
        mediaSessionHolder.closeSession()
    }

}