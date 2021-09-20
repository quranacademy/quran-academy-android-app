package org.quranacademy.quran.player.data.quranplayer.playbackcontrollers.audiofocus.audiofocusmanager

import android.media.AudioManager

abstract class AudioManagerApi {

    abstract fun requestAudioFocus(
            audioManager: AudioManager,
            request: AudioFocusRequestCompat
    ): Int

    abstract fun abandonAudioFocus(audioManager: AudioManager): Int

}