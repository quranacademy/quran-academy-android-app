package org.quranacademy.quran.player.data.quranplayer

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import org.quranacademy.quran.domain.models.AyahAudio
import org.quranacademy.quran.domain.models.PlayerState
import timber.log.Timber
import javax.inject.Inject

class PlaybackData @Inject constructor() {

    private val currentAudioUpdates = BroadcastChannel<AyahAudio>(Channel.CONFLATED)
    private val currentWordNumberUpdates = BroadcastChannel<Int?>(Channel.CONFLATED)
    private val playbackStateUpdates = BroadcastChannel<PlayerState>(Channel.CONFLATED)
    private val currentAudioPlaybackProgressUpdates = BroadcastChannel<Long>(1)
    private val playbackErrorUpdates = BroadcastChannel<PlaybackException>(1)
    private val onPlaybackStarted = BroadcastChannel<Unit>(1)

    var currentAudio: AyahAudio? = null
        set(value) {
            val isValueChanged = value != field
            field = value
            if (value != null && isValueChanged) {
                GlobalScope.launch { currentAudioUpdates.send(value) }
            }
        }
    var currentWordNumber: Int? = null
        set(value) {
            if (value != field) {
                field = value
                GlobalScope.launch { currentWordNumberUpdates.send(value) }
            }
        }
    var playbackState: PlayerState = PlayerState.IDLE
        set(value) {
            if (value != field) {
                field = value
                GlobalScope.launch { playbackStateUpdates.send(value) }
            }
        }
    var playbackProgress: Long = 0
        set(value) {
            field = value
            GlobalScope.launch { currentAudioPlaybackProgressUpdates.send(value) }
        }

    var rangeRepetitionCount: Int = 1
    var ayahRepetitionCount: Int = 1
    var autoScrollEnabled = false

    fun onPlaybackError(error: PlaybackException) {
        GlobalScope.launch { playbackErrorUpdates.send(error) }
        playbackState = PlayerState.ERROR
        Timber.e(error)
    }

    suspend fun onPlaybackStarted() = GlobalScope.launch {
        onPlaybackStarted.send(Unit)
    }

    fun playbackStartedUpdates(): Flow<Unit> = onPlaybackStarted.asFlow()

    fun currentAudioUpdates(): Flow<AyahAudio> = currentAudioUpdates.asFlow()

    fun currentWordNumberUpdates(): Flow<Int?> = currentWordNumberUpdates.asFlow()

    fun playbackStateUpdates(): Flow<PlayerState> = playbackStateUpdates.asFlow()

    fun currentAudioProgressUpdates(): Flow<Long> = currentAudioPlaybackProgressUpdates.asFlow()

    fun playbackErrorUpdates(): Flow<PlaybackException> = playbackErrorUpdates.asFlow()

}