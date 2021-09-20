package org.quranacademy.quran.radio.data.notification

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.quranacademy.quran.radio.data.manager.RadioData
import org.quranacademy.quran.radio.data.radio.RadioMetadata
import org.quranacademy.quran.radio.data.radio.RadioState
import javax.inject.Inject

class RadioMediaSessionHolder @Inject constructor(
        private val radioData: RadioData,
        private val mediaSessionFactory: RadioMediaSessionFactory
) {

    @Volatile
    private var openCount: Int = 0

    var mediaSession: MediaSessionCompat? = null
        private set

    fun openSession() {
        synchronized(RadioMediaSessionHolder::class.java) {
            openCount++
            if (openCount == 1) {
                doOpenSession()
            }
        }
    }

    fun closeSession() {
        synchronized(RadioMediaSessionHolder::class.java) {
            openCount--
            if (openCount == 0) {
                doCloseSession()
            }
        }
    }

    fun reportTrackChanged(metadata: RadioMetadata) {
        mediaSession?.let { mediaSession ->
            val builder = MediaMetadataCompat.Builder()
                    .putText(MediaMetadataCompat.METADATA_KEY_TITLE, metadata.info)
                    .putText(MediaMetadataCompat.METADATA_KEY_ARTIST, "Quran Academy")

            mediaSession.setMetadata(builder.build())
        }
    }

    fun reportPlaybackStateChanged(
            state: RadioState,
            errorMessage: CharSequence? = null
    ) {
        mediaSession?.let { mediaSession ->
            @PlaybackStateCompat.State
            val playbackState = toPlaybackStateCompat(state)
            val isPlaying = playbackState == PlaybackStateCompat.STATE_PLAYING
            val builder = PlaybackStateCompat.Builder()
                    .setActions(PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_PAUSE
                            or PlaybackStateCompat.ACTION_PLAY_PAUSE
                            or PlaybackStateCompat.ACTION_STOP)
                    .setState(playbackState, 0, (if (isPlaying) 1 else 0).toFloat())

            if (errorMessage != null) {
                builder.setErrorMessage(PlaybackStateCompat.ERROR_CODE_APP_ERROR, errorMessage)
            }

            mediaSession.setPlaybackState(builder.build())
        }
    }

    private fun doOpenSession() {
        this.mediaSession = mediaSessionFactory.newMediaSession()
        GlobalScope.launch {
            reportPlaybackStateChanged(radioData.playbackState)
        }
    }

    private fun doCloseSession() {
        mediaSession?.let { mediaSession ->
            mediaSession.isActive = false
            mediaSession.release()
        }
        mediaSession = null
    }

    companion object {

        @PlaybackStateCompat.State
        private fun toPlaybackStateCompat(state: RadioState): Int {
            return when (state) {
                RadioState.PREPARING -> PlaybackStateCompat.STATE_BUFFERING
                RadioState.PLAYING -> PlaybackStateCompat.STATE_PLAYING
                RadioState.PAUSED -> PlaybackStateCompat.STATE_PAUSED
                RadioState.ERROR -> PlaybackStateCompat.STATE_ERROR
                RadioState.IDLE -> PlaybackStateCompat.STATE_NONE
            }
        }

    }

}