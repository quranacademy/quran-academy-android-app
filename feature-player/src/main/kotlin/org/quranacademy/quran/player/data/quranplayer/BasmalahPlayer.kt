package org.quranacademy.quran.player.data.quranplayer

import android.annotation.SuppressLint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.domain.models.AyahAudio
import org.quranacademy.quran.domain.models.PlayerState
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.player.data.simpleplayer.SimpleAudioPlayer
import org.quranacademy.quran.player.data.simpleplayer.SimpleAudioPlayerState
import org.quranacademy.quran.player.di.playerservice.PlayerServiceScope
import org.quranacademy.quran.recitationsrepository.AudioPathProvider
import org.quranacademy.quran.recitationsrepository.downloading.AudioDownloadException
import org.quranacademy.quran.recitationsrepository.downloading.QuranAudioDownloader
import java.io.File
import javax.inject.Inject

class BasmalahPlayer @Inject constructor(
        @PlayerServiceScope
        private val coroutineScope: CoroutineScope,
        private val simpleAudioPlayer: SimpleAudioPlayer,
        private val quranAudioDownloader: QuranAudioDownloader,
        private val playbackData: PlaybackData,
        private val audioPathProvider: AudioPathProvider
) {

    private val stateChangeUpdates = BroadcastChannel<PlayerState>(2)
    private var isBasmalahPlaybackActive = false
    private var basmalahPlaybackListener: Listener? = null

    init {
        coroutineScope.launch {
            simpleAudioPlayer.stateChangeUpdates().collect { simplePlayerState ->
                val state: PlayerState? = when (simplePlayerState) {
                    SimpleAudioPlayerState.LOADING -> PlayerState.LOADING
                    SimpleAudioPlayerState.PLAYING -> PlayerState.PLAYING
                    SimpleAudioPlayerState.PAUSED -> PlayerState.PAUSED
                    else -> null
                }
                state?.let {
                    stateChangeUpdates.send(it)
                }
            }
        }
    }

    fun stateUpdates(): Flow<PlayerState> = stateChangeUpdates.asFlow()

    fun basmalahPlaybackNeeded(ayahAudio: AyahAudio): Boolean {
        val skipBasmalah = ayahAudio.surahNumber == 1 || ayahAudio.surahNumber == 9
        return !skipBasmalah && ayahAudio.ayahNumber == 1
    }

    fun isBasmalahDownloaded(recitation: Recitation): Boolean {
        return getBasmalahFile(recitation).exists()
    }

    fun isBasmalahPlaying(): Boolean = isBasmalahPlaybackActive

    fun playPause() {
        if (simpleAudioPlayer.isPlaying()) {
            pause()
        } else {
            play()
        }
    }

    fun play() = simpleAudioPlayer.play()

    fun pause() = simpleAudioPlayer.pause()

    fun stop() {
        basmalahPlaybackListener?.cancel()
        simpleAudioPlayer.stop()
    }

    @SuppressLint("LogNotTimber")
    fun playBasmalah(recitation: Recitation, onFinishListener: Listener) {
        basmalahPlaybackListener = onFinishListener

        val basmalahFile = getBasmalahFile(recitation)
        val isBasmalahDownloaded = basmalahFile.exists()
        isBasmalahPlaybackActive = true
        if (isBasmalahDownloaded) {
            playAudio(basmalahFile, onFinishListener)
        } else {
            downloadBasmalah(recitation) {
                playAudio(basmalahFile, onFinishListener)
            }
        }
    }

    private fun playAudio(basmalahFile: File, onFinishListener: Listener) {
        simpleAudioPlayer.playFromFile(basmalahFile) { isFinished ->
            isBasmalahPlaybackActive = false
            basmalahPlaybackListener = null
            if (isFinished) {
                onFinishListener.onFinished()
            }
        }
    }

    private fun downloadBasmalah(
            recitation: Recitation,
            onDownloaded: () -> Unit
    ) = coroutineScope.launch {
        try {
            val downloadTask = quranAudioDownloader.downloadAudio(
                    surahNumber = 1,
                    ayahNumber = 1,
                    recitation = recitation
            )
            downloadTask.start()
            onDownloaded()
        } catch (error: AudioDownloadException) {
            playbackData.onPlaybackError(PlaybackException.Downloading(cause = error))
            isBasmalahPlaybackActive = false
        }
    }

    private fun getBasmalahFile(recitation: Recitation) =
            File(audioPathProvider.getAyahAudioPath(1, 1, recitation))

    class Listener(
            private val listener: () -> Unit
    ) {

        private var isCancelled = false

        internal fun onFinished() {
            if (!isCancelled) {
                listener()
            }
        }

        internal fun cancel() {
            isCancelled = true
        }

    }

}