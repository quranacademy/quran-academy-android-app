package org.quranacademy.quran.player.data.quranplayer.service

import android.content.Context
import android.content.Intent
import org.quranacademy.quran.domain.models.PlaybackOptions
import org.quranacademy.quran.domain.models.PlaybackRequest
import javax.inject.Inject

/**
 * Intent factory for playback service
 */
class PlaybackServiceIntentFactory @Inject constructor(
        private val context: Context
) {

    fun intentPlay(
            playbackRequest: PlaybackRequest
    ): Intent {
        val intent = intentAction(PlaybackService.ACTION_PLAY)
        intent.putExtra(PlaybackService.EXTRA_PLAYBACK_REQUEST, playbackRequest)
        return intent
    }

    fun intentChangePlaybackOptions(
            playbackOptions: PlaybackOptions
    ): Intent {
        val intent = intentAction(PlaybackService.ACTION_CHANGE_PLAYBACK_OPTIONS)
        intent.putExtra(PlaybackService.EXTRA_CHANGE_PLAYBACK_OPTIONS, playbackOptions)
        return intent
    }

    fun intentPlayPause(): Intent {
        return intentAction(PlaybackService.ACTION_PLAY_PAUSE)
    }

    internal fun intentResume() = intentAction(PlaybackService.ACTION_RESUME)

    internal fun intentPause() = intentAction(PlaybackService.ACTION_PAUSE)

    internal fun intentStop() = intentAction(PlaybackService.ACTION_STOP)

    fun intentPrev() = intentAction(PlaybackService.ACTION_PREV)

    fun intentNext() = intentAction(PlaybackService.ACTION_NEXT)

    internal fun intentResendState() = Intent(PlaybackService.ACTION_RESEND_STATE)

    internal fun intentStopWithError(
            errorMessage: String
    ): Intent {
        val intent = intentAction(PlaybackService.ACTION_STOP_WITH_ERROR)
        intent.putExtra(PlaybackService.EXTRA_ERROR_MESSAGE, errorMessage)
        return intent
    }


    internal fun intentSeekTo(
            position: Long
    ): Intent {
        val intent = intentAction(PlaybackService.ACTION_SEEK)
        intent.putExtra(PlaybackService.EXTRA_PLAYBACK_POSITION, position)
        return intent
    }

    private fun intentAction(
            action: String
    ): Intent {
        val intent = Intent(context, PlaybackService::class.java)
        intent.action = action
        return intent
    }


}