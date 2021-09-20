package org.quranacademy.quran.player.data.quranplayer.playbackmanager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.domain.models.AyahAudio
import org.quranacademy.quran.domain.models.PlayerState
import org.quranacademy.quran.player.data.quranplayer.BasmalahPlayer
import org.quranacademy.quran.player.data.quranplayer.PlaybackData
import org.quranacademy.quran.player.data.quranplayer.playbackcontrollers.audiofocus.AudioFocusPlaybackController
import org.quranacademy.quran.player.data.quranplayer.queue.QueueManager
import org.quranacademy.quran.player.di.playerservice.PlayerServiceScope
import javax.inject.Inject

class AyahPlaybackFinishedHandler @Inject constructor(
        @PlayerServiceScope
        private val coroutineScope: CoroutineScope,
        private val playbackData: PlaybackData,
        private val queueManager: QueueManager,
        private val basmalahPlayer: BasmalahPlayer,
        private val audioPlayerProgressUpdater: AudioPlayerProgressUpdater,
        private val audioFocusPlaybackController: AudioFocusPlaybackController
) {

    private val playAudioCommands = BroadcastChannel<PlayAudioCommand>(1)
    private val playerStateChangedUpdates = BroadcastChannel<PlayerState>(1)
    //playback options
    var currentRangeRepetitionNumber: Int = 0
    var currentAyahRepetitionNumber: Int = 0

    fun onPlayAudioCommand(listener: (PlayAudioCommand) -> Unit) {
        coroutineScope.launch {
            playAudioCommands.asFlow().collect { listener(it) }
        }
    }

    fun onPlayerStateChanged(listener: (PlayerState) -> Unit) {
        coroutineScope.launch {
            playerStateChangedUpdates.asFlow().collect { listener(it) }
        }
    }

    fun onAyahPlaybackFinished() {
        //there are 4 states:
        //1. Ayah playback finished > play the ayah again
        //2. Ayah repetitions finished > go to the next ayah
        //3. Range playback finished > go to the queue start and play all ayahs again
        //4. Range repetitions finished > stop playback
        val isInfiniteAyahRepetition = playbackData.ayahRepetitionCount == -1
        val isAyahRepetitionFinished = currentAyahRepetitionNumber + 1 == playbackData.ayahRepetitionCount
        if (isInfiniteAyahRepetition || !isAyahRepetitionFinished) {
            playCurrentAyahAgain()
        } else {
            val nextAyah = queueManager.getNextAyah()
            val isLastAyah = nextAyah == null //range playback finished
            if (isLastAyah) {
                playLastAyah()
            } else {
                playNextAyah()
            }
        }
    }

    private fun playAudio(ayahAudio: AyahAudio, playBasmalah: Boolean = false) {
        coroutineScope.launch {
            playAudioCommands.send(PlayAudioCommand(ayahAudio, playBasmalah))
        }
    }

    private fun playCurrentAyahAgain() {
        currentAyahRepetitionNumber++
        queueManager.getCurrentAyah()?.let { ayahAudio ->
            playAudio(ayahAudio)
        }
    }

    private fun playNextAyah() {
        currentAyahRepetitionNumber = 0
        queueManager.getCurrentAyah()?.let { ayahAudio ->
            val playBasmalah = basmalahPlayer.basmalahPlaybackNeeded(ayahAudio)
            playAudio(ayahAudio, playBasmalah)
        }
    }

    private fun playLastAyah() {
        val isInfiniteRangeRepetition = playbackData.rangeRepetitionCount == -1
        val isRangeRepetitionFinished = currentRangeRepetitionNumber + 1 == playbackData.rangeRepetitionCount
        if (!isInfiniteRangeRepetition && isRangeRepetitionFinished) {
            //playback fully finished
            onPlaybackFinished()
        } else {
            //start range playback again
            startRangePlaybackAgain()
        }
    }

    private fun startRangePlaybackAgain() {
        queueManager.goToFirstAyah()
        currentRangeRepetitionNumber++
        currentAyahRepetitionNumber = 0
        queueManager.getCurrentAyah()?.let { ayahAudio ->
            playAudio(ayahAudio)
        }
    }

    private fun onPlaybackFinished() {
        audioPlayerProgressUpdater.stop()
        audioFocusPlaybackController.abandonAudioFocus()
        coroutineScope.launch {
            playerStateChangedUpdates.send(PlayerState.IDLE)
        }
    }

    class PlayAudioCommand(
            val audio: AyahAudio,
            val playBasmalah: Boolean
    )

}