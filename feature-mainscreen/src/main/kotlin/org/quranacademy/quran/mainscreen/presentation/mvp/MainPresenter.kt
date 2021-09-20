package org.quranacademy.quran.mainscreen.presentation.mvp

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.aartikov.alligator.Screen
import org.quranacademy.quran.domain.commons.SystemManager
import org.quranacademy.quran.domain.models.AppInfo
import org.quranacademy.quran.domain.models.PlayerState
import org.quranacademy.quran.mainscreen.domain.MainInteractor
import org.quranacademy.quran.memorization.routing.MemorizationOptionsScreen
import org.quranacademy.quran.player.data.quranplayer.PlaybackData
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.routing.screens.*
import javax.inject.Inject

@InjectViewState
class MainPresenter @Inject constructor(
        private val interactor: MainInteractor,
        private val playbackData: PlaybackData,
        private val appInfo: AppInfo,
        private val systemManager: SystemManager
) : BasePresenter<MainView>() {

    private var isPlayerControlPanelShowing = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        launch {
            val isNeededToShowTranslationsCopyrightDialog = interactor.isNeededToShowTranslationsCopyrightDialog()
            if (!isNeededToShowTranslationsCopyrightDialog) {
                viewState.showTranslationsCopyrightDialog()
            }

            val isNeededToShowTranslationUpdatesNotification = interactor.isNeededToShowTranslationUpdatesNotification()
            if (isNeededToShowTranslationUpdatesNotification) {
                viewState.showTranslationsUpdatesAvailable(true)
            }
        }

        launch {
            playbackData.playbackStateUpdates()
                    .collect { state ->
                        val isPlaying = state != PlayerState.IDLE && state != PlayerState.ERROR
                        viewState.showPlayerButton(isPlaying && !isPlayerControlPanelShowing)

                        if (state == PlayerState.IDLE) {
                            viewState.hidePlayerControlPanel()
                        }
                    }
        }
    }

    fun onTranslationsCopyrightAcceptedClicked() = launch {
        interactor.disableTranslationsCopyrightDialogShowing()
    }

    fun onTranslationUpdatesNotificationClicked() {
        interactor.deferTranslationUpdatesNotificationShowing()
        viewState.showTranslationsUpdatesAvailable(false)
        router.goForward(SettingsScreen())
    }

    fun onAudioManagerClicked() {
        router.goForward(AudioManagerScreen())
    }

    fun onOpenSettingsClicked() {
        router.goForward(SettingsScreen())
    }

    fun onOpenSearchScreenClicked() {
        router.goForward(SearchScreen())
    }

    fun onOpenBookmarkClicked() = launch {
        val isMushafMode = interactor.isCurrentModeMushaf()
        val quranScreen: Screen = if (isMushafMode) MushafScreen() else SurahDetailsScreen()
        router.goForward(quranScreen)
    }

    fun onJumpToAyahClicked() {
        router.goForward(JumpToAyahScreen())
    }

    fun onOpenTajweedRulesClicked() {
        router.goForward(TajweedRulesScreen())
    }

    fun onOpenQuranMemorizationClicked() {
        router.goForward(MemorizationOptionsScreen())
    }

    fun onOpenPlayerButtonClick() {
        isPlayerControlPanelShowing = true
        viewState.showPlayerButton(false)
        viewState.showPlayerControlPanel()
    }

    fun onPlayerControlVisibilityChanged(isVisible: Boolean) {
        isPlayerControlPanelShowing = isVisible

        if (!isVisible) {
            viewState.hidePlayerControlPanel()

            val isPlaying = playbackData.playbackState != PlayerState.IDLE && playbackData.playbackState != PlayerState.ERROR
            viewState.showPlayerButton(isPlaying)
        }
    }

    fun onOpenRadioCleacked() {
        router.goForward(RadioScreen())
    }

    fun onLeaveFeedbackClicked() {
        router.goForward(FeedbackScreen())
    }

    fun onRateAppClicked() {
        router.goForward(RateAppScreen())
    }

    fun onShareAppClicked() {
        systemManager.shareText(appInfo.googlePlayUrl)
    }

    fun onShowCopyrightInfoClicked() {
        viewState.showTranslationsCopyrightDialog()
    }

}