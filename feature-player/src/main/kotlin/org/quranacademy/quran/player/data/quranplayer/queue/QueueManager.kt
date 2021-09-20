package org.quranacademy.quran.player.data.quranplayer.queue

import org.quranacademy.quran.domain.models.AyahAudio
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.domain.models.VerseRange
import org.quranacademy.quran.presentation.extensions.replace
import javax.inject.Inject

class QueueManager @Inject constructor(
        private val playbackQueueCreator: PlaybackQueueCreator,
        private val audioFileDurationProvider: AudioFileDurationProvider
) {

    private var playbackQueue: MutableList<AyahAudio>? = null
    private var currentAudioPosition: Int = 0

    fun getCurrentRecitation() = playbackQueue?.first()?.recitation

    fun getVerseRange(): VerseRange {
        val firstAyah = playbackQueue!!.first()
        val lastAyah = playbackQueue!!.last()
        return VerseRange(
                startSurah = firstAyah.surahNumber,
                startAyah = firstAyah.ayahNumber,
                endingSurah = lastAyah.surahNumber,
                endingAyah = lastAyah.ayahNumber
        )
    }

    fun newQueue(verseRange: VerseRange, recitation: Recitation) {
        playbackQueue = playbackQueueCreator.createQueue(verseRange, recitation).toMutableList()
        currentAudioPosition = 0
    }

    fun getPlaybackQueue(): List<AyahAudio>? = playbackQueue

    fun getCurrentAyah(): AyahAudio? {
        val audio = playbackQueue?.get(currentAudioPosition)
        return audio?.let { updateAudioDurationInfo(it) }
    }

    fun setCurrentAyahPosition(position: Int) {
        currentAudioPosition = position
    }

    fun getCurrentAyahPosition(): Int = currentAudioPosition

    fun getPrevAyah(): AyahAudio? {
        playbackQueue?.let { playbackQueue ->
            if (currentAudioPosition > 0) {
                currentAudioPosition--
                val audio = playbackQueue[currentAudioPosition]
                return updateAudioDurationInfo(audio)
            }
        }
        return null
    }

    fun getNextAyah(): AyahAudio? {
        playbackQueue?.let { playbackQueue ->
            if (currentAudioPosition + 1 < playbackQueue.size) {
                currentAudioPosition++
                val audio = playbackQueue[currentAudioPosition]
                return updateAudioDurationInfo(audio)
            }
        }
        return null
    }

    fun goToFirstAyah() {
        currentAudioPosition = 0
    }

    fun onAyahAudioDownloaded(ayahAudio: AyahAudio) {
        updateAudioDurationInfo(ayahAudio)
    }

    fun clear() {
        playbackQueue = null
        currentAudioPosition = 0
    }

    private fun updateAudioDurationInfo(ayahAudio: AyahAudio): AyahAudio {
        if (ayahAudio.duration != AyahAudio.UNKNOWN_DURATION) return ayahAudio

        val downloadedFileDuration = audioFileDurationProvider.getAudioDuration(ayahAudio.audioFile)
        val newAyahAudioInfo = ayahAudio.copy(duration = downloadedFileDuration)
        playbackQueue?.replace(ayahAudio, newAyahAudioInfo)
        return newAyahAudioInfo
    }

}