package org.quranacademy.quran.player.data.quranplayer.playbackcontrollers.audiofocus

import android.content.Context
import android.media.AudioManager
import org.quranacademy.quran.data.lifecycle.ServiceLifecycleObserver
import org.quranacademy.quran.player.data.quranplayer.HQAAudioPlayer
import org.quranacademy.quran.player.data.quranplayer.playbackcontrollers.audiofocus.audiofocusmanager.*
import javax.inject.Inject

class AudioFocusPlaybackController @Inject constructor(
        private val context: Context,
        private val audioPlayer: HQAAudioPlayer
) : ServiceLifecycleObserver {

    private var focusGranted = false
    private val onAudioFocusChangeListener = { focusChange: Int ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK -> {
                audioPlayer.play()
            }
            else -> {
                audioPlayer.pauseTemporary()
            }
        }
    }

    private val audioAttributes = AudioAttributesCompat(
            usage = USAGE_MEDIA,
            contentType = CONTENT_TYPE_MUSIC,
            legacyStreamType = AudioManager.STREAM_MUSIC
    )
    private val audioFocusRequest = AudioFocusRequestCompat(
            audioAttributes = audioAttributes,
            acceptsDelayedFocusGain = false,
            onAudioFocusChangeListener = onAudioFocusChangeListener,
            willPauseWhenDucked = false
    )
    private var audioManager: AudioManagerCompat? = null
    private var audioFocusRequested = false

    override fun onCreate() {
        val audioManagerSource = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        this.audioManager = if (audioManagerSource != null) {
            AudioManagerCompat(audioManagerSource)
        } else {
            null
        }
    }

    override fun onDestroy() {
        audioManager?.abandonAudioFocus()
        audioFocusRequested = false
    }

    fun requestAudioFocus() {
        if (!audioFocusRequested) {
            audioFocusRequested = true
            val result = audioManager?.requestAudioFocus(audioFocusRequest)
                    ?: AudioManager.AUDIOFOCUS_REQUEST_FAILED
            focusGranted = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }

    fun abandonAudioFocus() {
        audioFocusRequested = false
        audioManager?.abandonAudioFocus()
    }

}