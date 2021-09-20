package org.quranacademy.quran.player.data.quranplayer.service

import android.content.Context
import android.os.Build
import org.quranacademy.quran.domain.models.PlaybackOptions
import org.quranacademy.quran.domain.models.PlaybackRequest
import org.quranacademy.quran.player.domain.PlayerControl
import javax.inject.Inject

/**
 * Playback service control implementation
 */
class PlayerServiceControl @Inject constructor(
        private val context: Context,
        private val intentFactory: PlaybackServiceIntentFactory
) : PlayerControl {

    override fun play(playbackRequest: PlaybackRequest) {
        val intent = intentFactory.intentPlay(playbackRequest)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    override fun changePlaybackOptions(playbackOptions: PlaybackOptions) {
        context.startService(intentFactory.intentChangePlaybackOptions(playbackOptions))
    }

    override fun playPause() {
        context.startService(intentFactory.intentPlayPause())
    }

    override fun resume() {
        context.startService(intentFactory.intentResume())
    }

    override fun pause() {
        context.startService(intentFactory.intentPause())
    }

    override fun stop() {
        context.startService(intentFactory.intentStop())
    }

    override fun stopWithError(errorMessage: String) {
        context.startService(intentFactory.intentStopWithError(errorMessage))
    }

    override fun prevAyah() {
        context.startService(intentFactory.intentPrev())
    }

    override fun nextAyah() {
        context.startService(intentFactory.intentNext())
    }

    override fun seekTo(position: Long) {
        context.startService(intentFactory.intentSeekTo(position))
    }

}