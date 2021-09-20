package org.quranacademy.quran.player.data.quranplayer.playbackmanager

import org.quranacademy.quran.domain.models.AyahAudio
import org.quranacademy.quran.domain.models.PlayerState
import org.quranacademy.quran.player.data.quranplayer.BasmalahPlayer
import org.quranacademy.quran.player.data.quranplayer.HQAAudioPlayer
import org.quranacademy.quran.player.data.quranplayer.PlaybackData
import org.quranacademy.quran.player.data.quranplayer.PlaybackException
import org.quranacademy.quran.player.data.quranplayer.downloading.PlayerAudioDownloadManager
import org.quranacademy.quran.player.data.quranplayer.queue.QueueManager
import javax.inject.Inject

class PlayerDownloadHandler @Inject constructor(
        private val playbackData: PlaybackData,
        private val queueManager: QueueManager,
        private val hqaAudioPlayer: HQAAudioPlayer,
        private val basmalahPlayer: BasmalahPlayer
) : PlayerAudioDownloadManager.Listener {

    override fun onAyahDownloadStarted(ayahAudio: AyahAudio) {
        if (playbackData.currentAudio == ayahAudio) {
            playbackData.playbackState = PlayerState.DOWNLOADING
        }
    }

    override fun onAyahDownloaded(ayahAudio: AyahAudio) {
        try {
            queueManager.onAyahAudioDownloaded(ayahAudio)
            val isCurrentAudio = playbackData.currentAudio == ayahAudio
            val isBasmalahPlaying = basmalahPlayer.isBasmalahPlaying()
            if (isCurrentAudio && !isBasmalahPlaying) {
                hqaAudioPlayer.playAudio(queueManager.getCurrentAyah()!!)
            }
        } catch (error: Exception) {
            playbackData.onPlaybackError(PlaybackException.Unknown(ayahAudio))
        }
    }

    override fun onDownloadingError(ayahAudio: AyahAudio, error: PlaybackException) {
        if (playbackData.currentAudio == ayahAudio) {
            playbackData.onPlaybackError(error)
        }
    }

}