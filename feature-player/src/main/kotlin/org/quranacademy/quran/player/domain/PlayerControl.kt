package org.quranacademy.quran.player.domain

import org.quranacademy.quran.domain.models.PlaybackOptions
import org.quranacademy.quran.domain.models.PlaybackRequest

interface PlayerControl {

    fun play(playbackRequest: PlaybackRequest)

    fun changePlaybackOptions(playbackOptions: PlaybackOptions)

    fun playPause()

    fun resume()

    fun pause()

    fun stop()

    fun stopWithError(errorMessage: String)

    fun prevAyah()

    fun nextAyah()

    fun seekTo(position: Long)

}