package org.quranacademy.quran.surahdetails.mvp

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.domain.models.AyahAudio
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.PlayerState
import org.quranacademy.quran.domain.models.SurahDetails
import org.quranacademy.quran.player.data.quranplayer.PlaybackData
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.routing.screens.AyahDetailsScreen
import org.quranacademy.quran.presentation.mvp.routing.screens.MushafScreen
import org.quranacademy.quran.presentation.mvp.routing.screens.SettingsScreen
import org.quranacademy.quran.presentation.mvp.routing.screens.SurahDetailsScreen
import org.quranacademy.quran.surahdetails.domain.SurahDetailsInteractor
import javax.inject.Inject

@InjectViewState
class SurahDetailsPresenter @Inject constructor(
        private val interactor: SurahDetailsInteractor,
        private val playbackData: PlaybackData,
        private val surahDetailsUiMapper: SurahDetailsUiMapper
) : BasePresenter<SurahDetailsView>() {

    private lateinit var surahDetails: SurahDetailsUiModel
    private var isTranslationTextSettingsPanelOpened = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        launch {
            interactor.getTranslationsListChangedUpdates()
                    .collect { loadSurah() }
        }

        launch {
            playbackData.playbackStartedUpdates()
                    .collect { viewState.showPlayerControlPanel() }
        }

        launch {
            playbackData.currentAudioUpdates()
                    .collect { onAudioAyahChanged(it) }
        }

        launch {
            playbackData.currentWordNumberUpdates()
                    .collect {
                        val isCurrentSurah = isSurahLoaded() && surahDetails.surahNumber == playbackData.currentAudio?.surahNumber
                        if (isCurrentSurah) {
                            viewState.highlightNowPlayingWord(it)
                        }
                    }
        }

        launch {
            playbackData.playbackStateUpdates()
                    .collect { state ->
                        if (state == PlayerState.IDLE) {
                            viewState.hidePlayerControlPanel()
                        }

                        if (state == PlayerState.IDLE || state == PlayerState.ERROR) {
                            viewState.highlightNowPlayingAyah(null)
                        }
                    }
        }

        launch { loadSurah() }
    }

    fun onOpenSettingsScreenClicked() {
        router.goForward(SettingsScreen())
    }

    fun onOpenTranslationTextSettingsPanelClicked() {
        if (!isTranslationTextSettingsPanelOpened) {
            isTranslationTextSettingsPanelOpened = true
            viewState.openTextSettingsPanelClicked(true)
        }
    }

    fun onTextSettingsPanelClosed() {
        isTranslationTextSettingsPanelOpened = false
    }

    fun onAyahSelected(ayah: AyahUiModel) {
        router.goForward(AyahDetailsScreen(AyahId(ayah.surahNumber, ayah.ayahNumber)))
    }

    fun onSwitchReaderModeClicked(currentAyahNumber: Int) = launch {
        if (isSurahLoaded()) {
            interactor.saveLastReadPosition(surahDetails.surahNumber, currentAyahNumber, true)
            router.replace(MushafScreen())
        }
    }

    fun saveLastReadPosition(ayahNumber: Int) = launch {
        if (isSurahLoaded()) {
            interactor.saveLastReadPosition(surahDetails.surahNumber, ayahNumber)
        }
    }

    fun onOpenPlayerButtonClicked() {
        if (isSurahLoaded()) {
            //viewState.showPlayerButton(false)
            if (playbackData.playbackState == PlayerState.IDLE) {
                val startAyah = AyahId(surahDetails.surahNumber, 1)
                val endAyah = AyahId(surahDetails.surahNumber, surahDetails.ayahsCount)
                viewState.showPlayerOptionsPanel(startAyah, endAyah)
            } else {
                viewState.showPlayerControlPanel()
            }
        }
    }

    fun onOpenNextSurahClicked() = launch {
        interactor.saveLastReadPosition(surahDetails.surahNumber + 1, 1)
        router.replace(SurahDetailsScreen())
    }

    fun onBackPressed() {
        if (isTranslationTextSettingsPanelOpened) {
            viewState.openTextSettingsPanelClicked(false)
            isTranslationTextSettingsPanelOpened = false
        } else {
            router.finish()
        }
    }

    private suspend fun loadSurah() {
        viewState.showProgressLayout(true)

        val lastAyahPosition = interactor.getLastReadPosition()
        val surahDetails = interactor.getSurahDetails(lastAyahPosition.surahNumber)
        viewState.showProgressLayout(false)
        onSurahDetailsLoaded(surahDetails, lastAyahPosition)
    }

    private fun onSurahDetailsLoaded(surahDetails: SurahDetails, lastReadPosition: AyahId) {
        this.surahDetails = surahDetailsUiMapper.mapTo(surahDetails)
        viewState.showSurah(this.surahDetails, lastReadPosition.ayahNumber)

        launch {
            delay(500)
            playbackData.currentAudio?.let {
                onAudioAyahChanged(it)
            }
        }
    }

    private fun isSurahLoaded(): Boolean = ::surahDetails.isInitialized

    private fun onAudioAyahChanged(currentAyah: AyahAudio) {
        if (isSurahLoaded() && currentAyah.surahNumber == surahDetails.surahNumber) {
            viewState.highlightNowPlayingAyah(currentAyah.ayahNumber)
            if (playbackData.autoScrollEnabled) {
                viewState.scrollToAyah(currentAyah.ayahNumber)
            }
        } else {
            viewState.highlightNowPlayingAyah(null)
        }
    }

    fun onScrolled(firstVisibleAyah: Int) {
        val ayah = surahDetails.ayahs[firstVisibleAyah - 1]
        viewState.updateCurrentPositionInfo(ayah.pageNumber, ayah.juzNumber)
    }

}