package org.quranacademy.quran.player.data.quranplayer.playbackcontrollers.audiofocus.audiofocusmanager

import android.annotation.TargetApi
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build

@TargetApi(Build.VERSION_CODES.O)
class AudioManagerOreo : AudioManagerApi() {

    private var request: AudioFocusRequest? = null

    override fun requestAudioFocus(
            audioManager: AudioManager,
            request: AudioFocusRequestCompat): Int {
        val realRequest = toAudioFocusRequest(request)
        this.request = realRequest
        return audioManager.requestAudioFocus(realRequest)
    }

    override fun abandonAudioFocus(
            audioManager: AudioManager
    ): Int {
        var returnValue = AudioManager.AUDIOFOCUS_REQUEST_FAILED
        request?.let {
            returnValue = audioManager.abandonAudioFocusRequest(it)
        }
        request = null
        return returnValue
    }

    private fun toAudioAttributes(attributes: AudioAttributesCompat) = AudioAttributes
            .Builder()
            .setContentType(attributes.contentType)
            .setLegacyStreamType(attributes.legacyStreamType)
            .setUsage(attributes.usage)
            .build()

    private fun toAudioFocusRequest(request: AudioFocusRequestCompat) = AudioFocusRequest
            .Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(toAudioAttributes(request.audioAttributes))
            .setAcceptsDelayedFocusGain(request.acceptsDelayedFocusGain)
            .setOnAudioFocusChangeListener(request.onAudioFocusChangeListener)
            .setWillPauseWhenDucked(request.willPauseWhenDucked)
            .build()

}