package org.quranacademy.quran.player.presentation.playeroptions

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.launch
import org.quranacademy.quran.RadioPlayerSynchronizer
import org.quranacademy.quran.domain.exceptions.NoNetworkException
import org.quranacademy.quran.domain.models.*
import org.quranacademy.quran.player.di.playeroptions.EndAyah
import org.quranacademy.quran.player.di.playeroptions.StartAyah
import org.quranacademy.quran.player.domain.PlayerControl
import org.quranacademy.quran.player.domain.playeroptions.PlayerOptionsInteractor
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.routing.screens.AyahSelectionScreen
import org.quranacademy.quran.presentation.mvp.routing.screens.RecitationSelectionScreen
import javax.inject.Inject

@InjectViewState
class PlayerOptionsPresenter @Inject constructor(
        @StartAyah private var startAyah: AyahId,
        @EndAyah private var endAyah: AyahId,
        private val playerOptionsShell: PlayerOptionsShell,
        private val interactor: PlayerOptionsInteractor,
        private val playerControl: PlayerControl,
        private val radioPlayerSynchronizer: RadioPlayerSynchronizer
) : BasePresenter<PlayerOptionsView>() {

    private lateinit var surahs: List<Surah>
    private lateinit var recitations: List<Recitation>
    private var isWordByWordEnabled: Boolean = false
    private var highlightWords = false
    private var autoScrollEnabled = false
    private var rangeRepetitionsCount = 1
    private var ayahRepetitionsCount = 1
    private lateinit var currentRecitation: Recitation

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.showRecitationsLoadingProgress(true)
        launch {
            playerOptionsShell.onRecitationSelected {
                currentRecitation = it
                invalidateView()
            }
        }
        launch {
            surahs = interactor.getSurahsList()
            loadRecitationsList()
        }
    }

    fun onRecitationClicked() {
        router.goForward(RecitationSelectionScreen())
    }
    fun onHighlightWordsChanged(isChecked: Boolean) = launch {
        if (isChecked && !currentRecitation.isTimecodesDownloaded) {
            viewState.showTimecodesDownloadingNeededDialog()
        } else {
            highlightWords = isChecked
            interactor.setWordsHighlightingEnabled(highlightWords)
        }
    }

    fun onAutoScrollEnabledChanged(isEnabled: Boolean) {
        interactor.setPlayerAutoScrollEnabled(isEnabled)
        autoScrollEnabled = isEnabled
    }

    fun onDownloadTimecodesClicked() = launch {
        try {
            viewState.showTimecodesDownloadingProgress(true)
            interactor.downloadRecitationTimecodes(currentRecitation) {
                viewState.updateTimecodesDownloadingProgress(it)
            }
            interactor.setWordsHighlightingEnabled(true)
            highlightWords = true
            currentRecitation.isTimecodesDownloaded = true
        } catch (error: Exception) {
            errorHandler.proceed(error, viewState::showMessage)
        } finally {
            viewState.showTimecodesDownloadingProgress(false)
        }
    }

    fun onCancelTimecodesDownloadingClicked() {
        highlightWords = false
        invalidateView()
    }

    fun onStartAyahClicked() {
        router.goForward(AyahSelectionScreen(startAyah, AyahSelectionScreen.Type.START))
    }

    fun onStartAyahSelected(ayahId: AyahId) {
        startAyah = ayahId
        invalidateView()
    }

    fun onEndAyahClicked() {
        router.goForward(AyahSelectionScreen(endAyah, AyahSelectionScreen.Type.END))
    }

    fun onEndAyahSelected(ayahId: AyahId) {
        endAyah = ayahId
        invalidateView()
    }

    fun onRangeRepetitionsCountClicked() {
        viewState.showRangeRepetitionsSelectionDialog(rangeRepetitionsCount)
    }

    fun onRangeRepetitionsCountSelected(count: Int) {
        rangeRepetitionsCount = count
        invalidateView()
    }

    fun onAyahRepetitionsCountClicked() {
        viewState.showAyahRepetitionsSelectionDialog(ayahRepetitionsCount)
    }

    fun onAyahRepetitionCountSelected(count: Int) {
        ayahRepetitionsCount = count
        invalidateView()
    }

    fun onPlayButtonClicked() {
        if (radioPlayerSynchronizer.isRadioActive()) {
            radioPlayerSynchronizer.stopRadio {
                onPlayButtonClicked()
            }
            return
        }

        val playbackRequest = PlaybackRequest(
                verseRange = getPlaybackRange(),
                rangeRepetitionCount = rangeRepetitionsCount,
                ayahRepetitionCount = ayahRepetitionsCount,
                recitation = currentRecitation,
                autoScrollEnabled = autoScrollEnabled,
                highlightWords = highlightWords
        )
        playerControl.play(playbackRequest)
    }

    fun onRetryLoadRecitations() = launch {
        viewState.showRecitationsLoadingError(false)
        viewState.showRecitationsLoadingProgress(true)
        loadRecitationsList()
    }

    private fun getPlaybackRange(): VerseRange {
        val inverseRange = startAyah.surahNumber > endAyah.surahNumber ||
                (startAyah.surahNumber == endAyah.surahNumber && startAyah.ayahNumber > endAyah.ayahNumber)
        return if (inverseRange) {
            VerseRange(endAyah.surahNumber, endAyah.ayahNumber, startAyah.surahNumber, startAyah.ayahNumber)
        } else {
            VerseRange(startAyah.surahNumber, startAyah.ayahNumber, endAyah.surahNumber, endAyah.ayahNumber)
        }
    }

    private suspend fun loadRecitationsList() {
        try {
            val recitationsList = interactor.getRecitations()
            recitations = recitationsList.recitations
            currentRecitation = recitationsList.currentRecitation
            isWordByWordEnabled = interactor.isWordByWordEnabled()
            highlightWords = interactor.isWordsHighlightingEnabled()
            autoScrollEnabled = interactor.isPlayerAutoScrollEnabled()
            viewState.showRecitationsLoadingProgress(false)
            invalidateView()
        } catch (error: Exception) {
            onRecitationsListLoadingError(error)
        }
    }

    private fun onRecitationsListLoadingError(error: Throwable) {
        if (error is NoNetworkException) {
            viewState.showRecitationsLoadingProgress(false)
            viewState.showRecitationsLoadingError(true)
        } else {
            errorHandler.proceed(error, viewState::showMessage)
        }
    }

    private fun invalidateView() = launch {
        viewState.showPlayerOptions(
                recitationName = currentRecitation.name,
                autoScrollEnabled = autoScrollEnabled,
                showHighlightWordsOption = isWordByWordEnabled && currentRecitation.hasTimecodes(),
                highlightWords = highlightWords && currentRecitation.isTimecodesDownloaded,
                startSurahName = surahs[startAyah.surahNumber - 1].transliteratedName,
                startAyah = startAyah,
                endSurahName = surahs[endAyah.surahNumber - 1].transliteratedName,
                endAyah = endAyah,
                rangeRepetitionsCount = rangeRepetitionsCount,
                ayahRepetitionsCount = ayahRepetitionsCount
        )
    }

}