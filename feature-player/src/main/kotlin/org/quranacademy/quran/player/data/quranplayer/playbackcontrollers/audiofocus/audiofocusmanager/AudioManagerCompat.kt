package org.quranacademy.quran.player.data.quranplayer.playbackcontrollers.audiofocus.audiofocusmanager

import android.media.AudioManager
import android.os.Build

class AudioManagerCompat(private val audioManager: AudioManager) {

    private val audioManagerImpl: AudioManagerApi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        AudioManagerOreo()
    } else {
        AudioManagerLegacy()
    }

    fun requestAudioFocus(request: AudioFocusRequestCompat) = audioManagerImpl.requestAudioFocus(audioManager, request)

    fun abandonAudioFocus() = audioManagerImpl.abandonAudioFocus(audioManager)

}