package org.quranacademy.quran.player.data.quranplayer.playbackmanager

import android.os.Handler
import org.quranacademy.quran.player.data.quranplayer.BasmalahPlayer
import org.quranacademy.quran.player.data.quranplayer.HQAAudioPlayer
import org.quranacademy.quran.player.data.quranplayer.PlaybackData
import javax.inject.Inject

class AudioPlayerProgressUpdater @Inject constructor(
        private val hqaAudioPlayer: HQAAudioPlayer,
        private val basmalahPlayer: BasmalahPlayer,
        private val playbackData: PlaybackData
) {

    private val updatingDelayMillis: Long = 100
    private val progressHandler = Handler()
    private val updatePlayingProgress = object : Runnable {
        override fun run() {
            onProgressUpdate()
            progressHandler.postDelayed(this, updatingDelayMillis)
        }
    }

    fun start() {
        progressHandler.postDelayed(updatePlayingProgress, updatingDelayMillis)
    }

    fun stop() = progressHandler.removeCallbacks(updatePlayingProgress)

    private fun onProgressUpdate() {
        val isBasmalahPlaying = basmalahPlayer.isBasmalahPlaying()
        val progress = hqaAudioPlayer.getCurrentPlaybackPosition()
        playbackData.playbackProgress = if (!isBasmalahPlaying) progress else 0
    }

}