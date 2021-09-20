package org.quranacademy.quran.player.data.quranplayer.notification

import android.content.Context
import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.quranacademy.quran.domain.models.AyahAudio
import org.quranacademy.quran.domain.models.PlayerState
import org.quranacademy.quran.player.R
import org.quranacademy.quran.player.data.quranplayer.PlaybackData
import javax.inject.Inject

/**
 * [MediaSessionCompat] holder
 */
class MediaSessionHolder @Inject constructor(
        context: Context,
        private val playbackData: PlaybackData,
        private val mediaSessionFactory: MediaSessionFactory
) {

    private val resources = context.resources

    @Volatile
    private var openCount: Int = 0

    var mediaSession: MediaSessionCompat? = null
        private set

    fun openSession() {
        synchronized(MediaSessionHolder::class.java) {
            openCount++
            if (openCount == 1) {
                doOpenSession()
            }
        }
    }

    fun closeSession() {
        synchronized(MediaSessionHolder::class.java) {
            openCount--
            if (openCount == 0) {
                doCloseSession()
            }
        }
    }

    fun reportAyahChanged(ayahAudio: AyahAudio) {
        mediaSession?.let { mediaSession ->
            val ayahAudioTitle = resources.getString(R.string.ayah_playback_title, ayahAudio.surahName, ayahAudio.ayahNumber)
            val audioUri = Uri.fromFile(ayahAudio.audioFile)
            val builder = MediaMetadataCompat.Builder()
                    .putText(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "${ayahAudio.surahName}:${ayahAudio.ayahNumber}")
                    .putText(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, audioUri.toString())
                    .putText(MediaMetadataCompat.METADATA_KEY_TITLE, ayahAudioTitle)
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, ayahAudio.duration)
                    .putText(MediaMetadataCompat.METADATA_KEY_ARTIST, ayahAudio.recitation.name)

            mediaSession.setMetadata(builder.build())
        }
    }

    fun reportPlaybackStateChanged(
            state: PlayerState,
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
                            or PlaybackStateCompat.ACTION_STOP
                            or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                            or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                            or PlaybackStateCompat.ACTION_SEEK_TO
                            or PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH)
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
            playbackData.currentAudio?.let { currentAudio ->
                reportAyahChanged(currentAudio)
            }
            reportPlaybackStateChanged(playbackData.playbackState)
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
        private fun toPlaybackStateCompat(state: PlayerState): Int {
            return when (state) {
                PlayerState.DOWNLOADING, PlayerState.LOADING -> PlaybackStateCompat.STATE_BUFFERING
                PlayerState.PLAYING -> PlaybackStateCompat.STATE_PLAYING
                PlayerState.PAUSED -> PlaybackStateCompat.STATE_PAUSED
                PlayerState.ERROR -> PlaybackStateCompat.STATE_ERROR
                PlayerState.IDLE -> PlaybackStateCompat.STATE_NONE
            }
        }

    }

}