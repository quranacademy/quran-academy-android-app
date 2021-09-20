package org.quranacademy.quran.common

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.quranacademy.quran.RadioPlayerSynchronizer
import org.quranacademy.quran.domain.models.PlayerState
import org.quranacademy.quran.player.data.quranplayer.PlaybackData
import org.quranacademy.quran.player.domain.PlayerControl
import org.quranacademy.quran.radio.data.manager.RadioData
import org.quranacademy.quran.radio.data.radio.RadioState
import org.quranacademy.quran.radio.domain.RadioControl
import javax.inject.Inject

class RadioPlayerSynchonizerImpl @Inject constructor(
        private val radioData: RadioData,
        private val playbackData: PlaybackData,
        private val radioControl: RadioControl,
        private val playerControl: PlayerControl
) : RadioPlayerSynchronizer {

    override fun isPlayerActive(): Boolean {
        val state = playbackData.playbackState

        return state == PlayerState.PLAYING
                || state == PlayerState.PAUSED
                || state == PlayerState.LOADING
                || state == PlayerState.DOWNLOADING
    }

    override fun isRadioActive(): Boolean {
        return false
        //val state = radioData.playbackState
        //return state == RadioState.PLAYING
        //        || state == RadioState.PAUSED
        //        || state == RadioState.PREPARING
    }

    override fun stopPlayer(onStopped: () -> Unit) {
        GlobalScope.launch {
            playbackData.playbackStateUpdates().collect {
                if (it == PlayerState.IDLE) {
                    withContext(Dispatchers.Main) {
                        onStopped()
                    }
                    cancel()
                }
            }
        }
        playerControl.stop()
    }

    override fun stopRadio(onStopped: () -> Unit) {
        GlobalScope.launch {
            radioData.radioStateUpdates().collect {
                if (it == RadioState.IDLE) {
                    withContext(Dispatchers.Main) {
                        onStopped()
                    }
                    cancel()
                }
            }
        }
        radioControl.stopRadio()
    }


}