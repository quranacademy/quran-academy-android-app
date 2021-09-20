package org.quranacademy.quran.player.data.quranplayer.playbackmanager

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.quranacademy.quran.data.lifecycle.ServiceLifecycleObserver
import org.quranacademy.quran.domain.models.AyahAudio
import org.quranacademy.quran.domain.models.PlaybackOptions
import org.quranacademy.quran.domain.models.PlaybackRequest
import org.quranacademy.quran.domain.models.PlayerState
import org.quranacademy.quran.player.data.quranplayer.*
import org.quranacademy.quran.player.data.quranplayer.BasmalahPlayer.Listener
import org.quranacademy.quran.player.data.quranplayer.downloading.PlayerAudioDownloadManager
import org.quranacademy.quran.player.data.quranplayer.playbackcontrollers.audiofocus.AudioFocusPlaybackController
import org.quranacademy.quran.player.data.quranplayer.playbackcontrollers.headset.HeadsetPlaybackController
import org.quranacademy.quran.player.data.quranplayer.queue.QueueManager
import org.quranacademy.quran.player.data.quranplayer.timecodes.TimecodesManager
import org.quranacademy.quran.player.di.playerservice.PlayerServiceScope
import org.quranacademy.quran.recitationsrepository.downloading.AudioDownloadException
import javax.inject.Inject

class QuranAudioPlaybackManager @Inject constructor(
        @PlayerServiceScope
        private val coroutineScope: CoroutineScope,
        private val playbackData: PlaybackData,
        private val queueManager: QueueManager,
        //player
        private val hqaAudioPlayer: HQAAudioPlayer,
        private val basmalahPlayer: BasmalahPlayer,
        //downloading
        private val playerAudioDownloadManager: PlayerAudioDownloadManager,
        //playback controllers
        private val audioFocusPlaybackController: AudioFocusPlaybackController,
        private val headsetPlaybackController: HeadsetPlaybackController,
        private val timecodesManager: TimecodesManager,
        private val playerStateChangeHandler: PlayerStateChangeHandler,
        private val ayahPlaybackFinishedHandler: AyahPlaybackFinishedHandler
) : ServiceLifecycleObserver {

    init {
        ayahPlaybackFinishedHandler.onPlayAudioCommand {
            playAudio(it.audio, it.playBasmalah)
        }
        ayahPlaybackFinishedHandler.onPlayerStateChanged {
            playerStateChangeHandler.onPlayerStateChanged(it)
        }
    }

    override fun onCreate() {
        audioFocusPlaybackController.onCreate()
        headsetPlaybackController.onCreate()
    }

    override fun onDestroy() {
        stop()
        audioFocusPlaybackController.onDestroy()
        headsetPlaybackController.onDestroy()
    }

    fun stateChangeUpdates(): Flow<PlayerState> = playerStateChangeHandler.stateChangeUpdates()

    fun play(request: PlaybackRequest) = coroutineScope.launch {
        //clear data of previous playback
        clearPlaybackData()
        playbackData.rangeRepetitionCount = request.rangeRepetitionCount
        playbackData.ayahRepetitionCount = request.ayahRepetitionCount
        playbackData.autoScrollEnabled = request.autoScrollEnabled
        timecodesManager.enableHighlighting(request.highlightWords)
        timecodesManager.initTimecodes(request.recitation)
        queueManager.newQueue(request.verseRange, request.recitation)
        launch {
            playerAudioDownloadManager.downloadAudio(
                    queueManager.getPlaybackQueue()!!,
                    request.currentPlaybackPosition
            )
        }

        audioFocusPlaybackController.requestAudioFocus()
        queueManager.setCurrentAyahPosition(request.currentPlaybackPosition)
        queueManager.getCurrentAyah()?.let {
            playbackData.onPlaybackStarted()
            val playBasmalah = basmalahPlayer.basmalahPlaybackNeeded(it)
            playAudio(it, playBasmalah)
        }
    }

    fun changePlaybackOptions(options: PlaybackOptions) = coroutineScope.launch {
        val currentRecitation = queueManager.getCurrentRecitation()
        if (currentRecitation != options.recitation) {
            val range = PlaybackRequest(
                    verseRange = queueManager.getVerseRange(),
                    rangeRepetitionCount = options.rangeRepeatCount,
                    ayahRepetitionCount = options.ayahRepeatCount,
                    recitation = options.recitation,
                    autoScrollEnabled = options.autoScrollEnabled,
                    highlightWords = options.highlightWords,
                    currentPlaybackPosition = queueManager.getCurrentAyahPosition()
            )
            stop()
            //делаем задержку, чтобы плеер полностью остановился
            // и не было всяких глюков
            delay(100)
            play(range)
            return@launch
        }

        playbackData.ayahRepetitionCount = options.ayahRepeatCount
        playbackData.rangeRepetitionCount = options.rangeRepeatCount
        playbackData.autoScrollEnabled = options.autoScrollEnabled

        resume()
    }

    fun resume() {
        audioFocusPlaybackController.requestAudioFocus()
        if (basmalahPlayer.isBasmalahPlaying()) {
            basmalahPlayer.play()
        } else {
            hqaAudioPlayer.play()
        }
    }

    fun pause(reason: PauseReason = PauseReason.NONE) {
        if (basmalahPlayer.isBasmalahPlaying()) {
            basmalahPlayer.pause()
        } else {
            hqaAudioPlayer.pause(reason)
        }
    }

    fun playPause() {
        if (basmalahPlayer.isBasmalahPlaying()) {
            basmalahPlayer.playPause()
        } else {
            hqaAudioPlayer.playPause()
        }
    }

    fun seekTo(position: Long) = hqaAudioPlayer.seekTo(position)

    fun stop() {
        clearPlaybackData()
        playerStateChangeHandler.onPlayerStateChanged(PlayerState.IDLE)
    }

    fun prevAyah() = queueManager.getPrevAyah()?.let { prevAyah ->
        hqaAudioPlayer.pause(PauseReason.WAITING_FOR_NEXT_AYAH)
        ayahPlaybackFinishedHandler.currentAyahRepetitionNumber = 0
        playAudio(prevAyah)
    }

    fun nextAyah() = queueManager.getNextAyah()?.let { nextAyah ->
        if (basmalahPlayer.isBasmalahPlaying()) {
            basmalahPlayer.stop()
        }

        hqaAudioPlayer.pause(PauseReason.WAITING_FOR_NEXT_AYAH)
        ayahPlaybackFinishedHandler.currentAyahRepetitionNumber = 0
        val playBasmalah = basmalahPlayer.basmalahPlaybackNeeded(nextAyah)
        playAudio(nextAyah, playBasmalah)
    }

    fun getNowPlaying(): AyahAudio? = queueManager.getCurrentAyah()

    fun onError(errorMessage: String) {
        playbackData.onPlaybackError(PlaybackException.Unknown(playbackData.currentAudio, errorMessage))
    }

    private fun playAudio(ayahAudio: AyahAudio, playBasmalah: Boolean = false) {
        playbackData.currentAudio = queueManager.getCurrentAyah()
        if (playBasmalah) {
            basmalahPlayer.playBasmalah(ayahAudio.recitation, Listener {
                Log.d("HQA", "Basmalah finished")
                playAudio(ayahAudio)
            })
        } else {
            val audioFile = ayahAudio.audioFile
            if (audioFile.exists()) {
                hqaAudioPlayer.playAudio(ayahAudio)
            } else {
                downloadAudio(ayahAudio)
            }
        }
    }

    private fun downloadAudio(ayahAudio: AyahAudio) {
        val isAudioDownloading = playerAudioDownloadManager.isAudioDownloading(ayahAudio)
        if (isAudioDownloading) {
            playerStateChangeHandler.onPlayerStateChanged(PlayerState.DOWNLOADING)
        } else {
            if (playerAudioDownloadManager.currentError is AudioDownloadException.NoNetwork) {
                val downloadingError = AudioDownloadException.NoNetwork()
                playbackData.onPlaybackError(PlaybackException.Downloading(ayahAudio, downloadingError))
            } else {
                playbackData.onPlaybackError(PlaybackException.Unknown(ayahAudio))
            }
        }
    }

    private fun clearPlaybackData() {
        playbackData.currentAudio = null
        playbackData.playbackProgress = 0
        playbackData.rangeRepetitionCount = 0
        playbackData.ayahRepetitionCount = 0
        ayahPlaybackFinishedHandler.currentRangeRepetitionNumber = 0
        ayahPlaybackFinishedHandler.currentAyahRepetitionNumber = 0
        queueManager.clear()
        basmalahPlayer.stop()
        hqaAudioPlayer.stop()
        playerAudioDownloadManager.clearQueue()
        timecodesManager.onDestroy()
    }

}