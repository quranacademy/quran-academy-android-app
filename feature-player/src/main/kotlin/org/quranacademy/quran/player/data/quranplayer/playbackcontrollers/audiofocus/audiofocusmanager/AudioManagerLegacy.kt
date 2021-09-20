package org.quranacademy.quran.player.data.quranplayer.playbackcontrollers.audiofocus.audiofocusmanager

import android.media.AudioManager

class AudioManagerLegacy : AudioManagerApi() {

    private var listener: AudioManager.OnAudioFocusChangeListener? = null

    override fun requestAudioFocus(
            audioManager: AudioManager,
            request: AudioFocusRequestCompat): Int {
        val listener = AudioFocusChangeListenerLegacyWrapper(
                request.onAudioFocusChangeListener)

        this.listener = listener

        @Suppress("DEPRECATION")
        return audioManager.requestAudioFocus(
                listener,
                request.audioAttributes.legacyStreamType,
                AudioManager.AUDIOFOCUS_GAIN)
    }

    override fun abandonAudioFocus(audioManager: AudioManager): Int {
        var returnValue = AudioManager.AUDIOFOCUS_REQUEST_FAILED
        listener?.let {
            @Suppress("DEPRECATION")
            returnValue = audioManager.abandonAudioFocus(it)
        }
        listener = null
        return returnValue
    }

    class AudioFocusChangeListenerLegacyWrapper(
            private val wrapped: (Int) -> Unit
    ) : AudioManager.OnAudioFocusChangeListener {

        override fun onAudioFocusChange(focusChange: Int) {
            wrapped.invoke(focusChange)
        }

    }
}