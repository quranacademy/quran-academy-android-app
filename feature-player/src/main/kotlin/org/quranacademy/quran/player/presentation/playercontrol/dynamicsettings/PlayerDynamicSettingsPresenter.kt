package org.quranacademy.quran.player.presentation.playercontrol.dynamicsettings

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.launch
import org.quranacademy.quran.domain.models.PlaybackOptions
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.player.data.quranplayer.PlaybackData
import org.quranacademy.quran.player.domain.playeroptions.PlayerOptionsInteractor
import org.quranacademy.quran.player.presentation.playeroptions.PlayerOptionsShell
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.routing.screens.RecitationSelectionScreen
import javax.inject.Inject

@InjectViewState
class PlayerDynamicSettingsPresenter @Inject constructor(
        playbackData: PlaybackData,
        private val interactor: PlayerOptionsInteractor,
        private val playerOptionsShell: PlayerOptionsShell
) : BasePresenter<PlayerDynamicSettingsView>() {

    private lateinit var recitations: List<Recitation>
    private var isWordByWordEnabled: Boolean = false
    private var highlightWords = false
    private var autoScrollEnabled = playbackData.autoScrollEnabled
    private var rangeRepetitionsCount = playbackData.rangeRepetitionCount
    private var ayahRepetitionsCount = playbackData.ayahRepetitionCount
    private lateinit var currentRecitation: Recitation

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        launch {
            playerOptionsShell.onRecitationSelected {
                currentRecitation = it
                invalidateView()
            }
        }

        launch {
            loadRecitationsList()
        }
    }

    fun onRecitationClicked() {
        router.goForward(RecitationSelectionScreen())
    }

    fun onRecitationSelected(recitation: Recitation) = launch {
        currentRecitation = recitation
        invalidateView()
    }

    fun onAutoScrollEnabledChanged(isEnabled: Boolean) {
        interactor.setPlayerAutoScrollEnabled(isEnabled)
        autoScrollEnabled = isEnabled
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

    fun onClosePlaybackSettingsClicked() {
        goBack(null)
    }

    fun onApplyPlaybackSettingsClicked() {
        val playbackOptions = PlaybackOptions(
                rangeRepeatCount = rangeRepetitionsCount,
                ayahRepeatCount = ayahRepetitionsCount,
                recitation = currentRecitation,
                autoScrollEnabled = autoScrollEnabled,
                highlightWords = highlightWords
        )
        goBack(playbackOptions)
    }

    private suspend fun loadRecitationsList() {
        try {
            val recitationsList = interactor.getRecitations()
            recitations = recitationsList.recitations
            currentRecitation = recitationsList.currentRecitation
            isWordByWordEnabled = interactor.isWordByWordEnabled()
            highlightWords = interactor.isWordsHighlightingEnabled()
            autoScrollEnabled = interactor.isPlayerAutoScrollEnabled()
            invalidateView()
        } catch (error: Exception) {
            errorHandler.proceed(error, viewState::showMessage)
        }
    }

    private fun invalidateView() = launch {
        viewState.showPlaybackSettings(
                recitationName = currentRecitation.name,
                autoScrollEnabled = autoScrollEnabled,
                rangeRepetitionsCount = rangeRepetitionsCount,
                ayahRepetitionsCount = ayahRepetitionsCount
        )
    }

    private fun goBack(options: PlaybackOptions?) {
        val result = PlayerDynamicSettingsScreen.Result(options)
        router.goBackWithResult(result)
    }

}