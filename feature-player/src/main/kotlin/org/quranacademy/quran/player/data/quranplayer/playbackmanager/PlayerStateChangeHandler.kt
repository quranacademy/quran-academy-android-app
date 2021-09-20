package org.quranacademy.quran.player.data.quranplayer.playbackmanager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.domain.models.PlayerState
import org.quranacademy.quran.player.data.quranplayer.BasmalahPlayer
import org.quranacademy.quran.player.data.quranplayer.HQAAudioPlayer
import org.quranacademy.quran.player.data.quranplayer.PauseReason
import org.quranacademy.quran.player.data.quranplayer.playbackcontrollers.audiofocus.AudioFocusPlaybackController
import org.quranacademy.quran.player.di.playerservice.PlayerServiceScope
import javax.inject.Inject

class PlayerStateChangeHandler @Inject constructor(
        @PlayerServiceScope
        private val coroutineScope: CoroutineScope,
        private val hqaAudioPlayer: HQAAudioPlayer,
        private val basmalahPlayer: BasmalahPlayer,
        private val audioPlayerProgressUpdater: AudioPlayerProgressUpdater,
        private val audioFocusPlaybackController: AudioFocusPlaybackController,
        private val ayahPlaybackFinishedHandler: AyahPlaybackFinishedHandler
) {

    private val stateChangeUpdates = BroadcastChannel<PlayerState>(2)

    init {
        coroutineScope.launch {
            basmalahPlayer.stateUpdates().collect {
                stateChangeUpdates.send(it)
            }
        }

        coroutineScope.launch {
            hqaAudioPlayer.stateChangeUpdates().collect { state ->
                if (basmalahPlayer.isBasmalahPlaying()) {
                    return@collect
                }

                when (state) {
                    PlayerState.DOWNLOADING -> {
                        stateChangeUpdates.send(state)
                    }
                    PlayerState.PLAYING -> {
                        audioPlayerProgressUpdater.start()
                        stateChangeUpdates.send(state)
                    }
                    PlayerState.PAUSED -> {
                        audioPlayerProgressUpdater.stop()
                        if (hqaAudioPlayer.pauseReason != PauseReason.WAITING_FOR_NEXT_AYAH) {
                            audioFocusPlaybackController.abandonAudioFocus()
                            stateChangeUpdates.send(state)
                        }
                    }
                    PlayerState.IDLE -> {
                        ayahPlaybackFinishedHandler.onAyahPlaybackFinished()
                    }
                    PlayerState.LOADING,
                    PlayerState.ERROR -> {
                        audioPlayerProgressUpdater.stop()
                        stateChangeUpdates.send(state)
                    }
                }
            }
        }
    }

    fun onPlayerStateChanged(state: PlayerState) {
        coroutineScope.launch {
            stateChangeUpdates.send(state)
        }
    }

    fun stateChangeUpdates(): Flow<PlayerState> = stateChangeUpdates.asFlow()


}