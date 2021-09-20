package org.quranacademy.quran.memorization.mvp.memorization.gamemanager

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.memorization.domain.MemorizationInteractor
import org.quranacademy.quran.memorization.models.MemorizationOptions
import org.quranacademy.quran.memorization.mvp.memorization.player.MemorizationPlayer
import org.quranacademy.quran.mushaf.presentation.mvp.mushafpage.AyahHighlightType
import java.util.*
import javax.inject.Inject

class MemorizationGameManager @Inject constructor(
        private val interactor: MemorizationInteractor,
        private val ayahsRangeCalculator: MemorizationAyahsRangeCalculator,
        private val memorizationDataDownloader: MemorizationDataDownloader,
        private val ayahHighlightsManager: MemorizationAyahHighlightsManager,
        private val audioPlayer: MemorizationPlayer
) {

    lateinit var view: View
    private lateinit var memorizationOptions: MemorizationOptions
    private lateinit var ayahsForMemorization: List<AyahId>
    private lateinit var currentAyah: AyahId
    private lateinit var lastOpenedAyah: AyahId
    private var currentPage = -1
    private var isGameStopped = false

    suspend fun startMemorization(options: MemorizationOptions) {
        this.memorizationOptions = options
        this.ayahsForMemorization = ayahsRangeCalculator.getAyahsForMemorization(options.mode, options.modeData)
        downloadDataForMemorization()

        coroutineScope {
            launch {
                audioPlayer.stateChangeUpdates().collect {
                    view.updatePlayPauseState(audioPlayer.isPlaying())
                }
            }
        }
    }

    fun isAudioPlaying() = audioPlayer.isPlaying()

    fun playAudio() = audioPlayer.play()

    fun pauseAudio() = audioPlayer.pause()

    fun stopAudio() {
        isGameStopped = true
        audioPlayer.stop()
    }

    private suspend fun downloadDataForMemorization() {
        view.showDataDownloadDialog(true)
        memorizationDataDownloader.downloadDataForMemorization(memorizationOptions.recitation, ayahsForMemorization)
        view.showDataDownloadDialog(false)
        onDownloadingFinished(ayahsForMemorization)
    }

    private suspend fun onDownloadingFinished(ayahsForMemorization: List<AyahId>) {
        ayahHighlightsManager.hideAllAyahs(ayahsForMemorization)
        openNewAyah()
    }

    private suspend fun showCurrentAyah() {
        val ayahPage = interactor.getAyahPage(currentAyah)
        if (ayahPage != currentPage) {
            this.currentPage = ayahPage
            view.showPage(ayahPage)
        }
        ayahHighlightsManager.showAyah(currentAyah)
        ayahHighlightsManager.highlightCurrentAyah(currentAyah)
        view.updateHighlights(ayahHighlightsManager.getHighlights())
    }

    private suspend fun openNewAyah() {
        val isFirstAyah = !::currentAyah.isInitialized
        val isLastAyah = !isFirstAyah && lastOpenedAyah == ayahsForMemorization.last()
        when {
            isFirstAyah -> {
                currentAyah = ayahsForMemorization.first()
                lastOpenedAyah = currentAyah
                showCurrentAyah()
                playAyahWithRepetition(currentAyah, memorizationOptions.repetitionsCount)
            }
            isLastAyah -> view.onMemorizationCompleted()
            else -> {
                currentAyah = getNextAyah(lastOpenedAyah)
                lastOpenedAyah = currentAyah
                showCurrentAyah()
                playAyahWithRepetition(currentAyah, memorizationOptions.repetitionsCount)
            }
        }
    }

    private suspend fun playAyahWithRepetition(ayah: AyahId, repetitionCount: Int) {
        playAudio(ayah) {
            if (repetitionCount - 1 > 0) {
                delay(memorizationOptions.delayBetweenRepetitions.toLong())
                playAyahWithRepetition(ayah, repetitionCount - 1)
            } else {
                val isFirstAyah = lastOpenedAyah == ayahsForMemorization.first()
                if (isFirstAyah) {
                    openNewAyah()
                } else {
                    repeatOpenedAyahs()
                }
            }
        }
    }

    private suspend fun repeatOpenedAyahs() {
        currentAyah = ayahsForMemorization.first()
        showCurrentAyah()
        playAudio(currentAyah) {
            playNextOpenedAyah()
        }
    }

    private suspend fun playNextOpenedAyah() {
        val isLastOpenedAyah = currentAyah == lastOpenedAyah
        if (isLastOpenedAyah) {
            openNewAyah()
        } else {
            currentAyah = getNextAyah(currentAyah)
            showCurrentAyah()
            playAudio(currentAyah) {
                playNextOpenedAyah()
            }
        }
    }

    private fun getNextAyah(ayah: AyahId): AyahId {
        return ayahsForMemorization.indexOf(ayah).let { lastOpenedAyahIndex ->
            ayahsForMemorization[lastOpenedAyahIndex + 1]
        }
    }

    private suspend fun playAudio(ayahId: AyahId, onPlaybackFinished: suspend () -> Unit) {
        if (!isGameStopped) {
            audioPlayer.playAudio(ayahId, memorizationOptions.recitation, onPlaybackFinished)
        }
    }

    interface View {

        fun showDataDownloadDialog(isVisible: Boolean)

        fun showPage(pageNumber: Int)

        fun updateHighlights(highlights: SortedMap<AyahHighlightType, MutableSet<AyahId>>)

        fun updatePlayPauseState(isPlaying: Boolean)

        fun onMemorizationCompleted()

    }

}