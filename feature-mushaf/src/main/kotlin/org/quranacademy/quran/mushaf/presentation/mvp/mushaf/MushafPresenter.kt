package org.quranacademy.quran.mushaf.presentation.mvp.mushaf

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.bookmarks.ui.BookmarkFoldersScreen
import org.quranacademy.quran.di.PrimitiveWrapper
import org.quranacademy.quran.domain.models.*
import org.quranacademy.quran.mushaf.di.AyahForHighlighting
import org.quranacademy.quran.mushaf.domain.MushafInteractor
import org.quranacademy.quran.mushaf.presentation.mvp.AyahClickEvent
import org.quranacademy.quran.mushaf.presentation.mvp.MushafPageCommandShell
import org.quranacademy.quran.mushaf.presentation.mvp.mushaf.statecontroller.MushafControllerView
import org.quranacademy.quran.mushaf.presentation.mvp.mushaf.statecontroller.MushafPageStateController
import org.quranacademy.quran.mushaf.presentation.mvp.mushafpage.AyahHighlightType
import org.quranacademy.quran.player.data.quranplayer.PlaybackData
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.routing.screens.MushafPageTypeSelectionScreen
import org.quranacademy.quran.presentation.mvp.routing.screens.SettingsScreen
import org.quranacademy.quran.presentation.mvp.routing.screens.SurahDetailsScreen
import org.quranacademy.quran.sharingdialog.SharingType
import org.quranacademy.quran.sharingdialog.TranslationsSharingScreen
import javax.inject.Inject

@InjectViewState
class MushafPresenter @Inject constructor(
        @AyahForHighlighting private val ayahForHighlighting: PrimitiveWrapper<AyahId?>,
        private val mushafPageCommandShell: MushafPageCommandShell,
        private val interactor: MushafInteractor,
        private val playbackData: PlaybackData
) : BasePresenter<MushafView>() {

    private lateinit var currentPage: QuranPage
    private var isReadModeSwitching = false
    private val mushafPageStateController = MushafPageStateController(
            ayahsRangeFinder = { first: AyahId, last: AyahId ->
                interactor.getAyahsRange(first, last)
            },
            viewController = MushafControllerView(
                    viewState = viewState,
                    interactor = interactor,
                    mushafPageCommandShell = mushafPageCommandShell,
                    errorHandler = errorHandler,
                    currentPage = { currentPage }
            )
    )

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        launch {
            mushafPageCommandShell.ayahClicks()
                    .collect { onAyahClick(it) }
        }

        launch {
            playbackData.playbackStartedUpdates()
                    .collect { viewState.showPlayerControlPanel() }
        }

        launch {
            playbackData.playbackStateUpdates().collect { state ->
                if (state == PlayerState.IDLE) {
                    viewState.hidePlayerControlPanel()
                }
            }
        }

        launch {
            playbackData.currentAudioUpdates()
                    .collect { onAudioAyahChanged(it) }
        }

        launch {
            playbackData.playbackStateUpdates().collect { state ->
                if (state == PlayerState.IDLE) {
                    viewState.hidePlayerControlPanel()
                    clearAyahAudioHighlights()
                } else if (state == PlayerState.ERROR) {
                    clearAyahAudioHighlights()
                }
            }
        }

        launch {
            interactor.getMushafTypeChangingUpdates().collect {
                if (isPageInfoLoaded()) {
                    viewState.hideAyahToolbar()
                    mushafPageCommandShell.highlightAyahs(
                            pageNumber = currentPage.pageNumber,
                            selectedAyahs = emptyList(),
                            highlightType = AyahHighlightType.SELECTION,
                            addToOld = false
                    )
                    loadPage(currentPage.pageNumber)
                }
            }
        }

        launch { checkIsImagesDownloadingNeeded() }
    }

    fun onBackPressed() {
        launch {
            if (!mushafPageStateController.onBackPressed()) {
                router.finish()
            }
        }
    }

    fun onDownloadImagesBundleClicked() = launch {
        try {
            viewState.showImagesBundleDownloadSuggestion(false)
            viewState.showImagesBundleDownloadProgress(true)
            interactor.downloadImagesBundle {
                viewState.updateImagesBundleDownloadProgress(it)
            }
            interactor.disableImagesBundleDownloadingSuggestion()
        } catch (error: Exception) {
            errorHandler.proceed(error, viewState::showMessage)
        } finally {
            viewState.showImagesBundleDownloadProgress(false)
            showInitialPage()
        }
    }

    fun onDisableImagesBundleDownloadingSuggestionClicked() = launch {
        interactor.disableImagesBundleDownloadingSuggestion()
        showInitialPage()
    }

    fun onCancelImagesBundleDownloadingClicked() = launch {
        interactor.cancelImagesBundleDownloading()
        interactor.disableImagesBundleDownloadingSuggestion()
        showInitialPage()
    }

    fun onChangeMushafThemeClicked() {
        viewState.showMushafThemeSelectingDialog()
    }

    fun onSwitchReadModeClicked() = launch {
        if (isPageInfoLoaded()) {
            isReadModeSwitching = true

            val selectedAyahs = mushafPageStateController.getSelectedAyahs()
            if (selectedAyahs.isEmpty()) {
                interactor.saveLastReadPosition(currentPage.pageNumber, false)
                router.replace(SurahDetailsScreen())
            } else {
                //если выделен какой-нибудь аят, то при переключении на режим списка переходим к нему,
                //а не к аяту, находящемуся в начале страницы
                val firstSelectedAyah = selectedAyahs.first()
                interactor.saveLastReadPosition(firstSelectedAyah.surahNumber, firstSelectedAyah.ayahNumber, false)
                router.replace(SurahDetailsScreen())
            }
        }
    }

    fun onOpenMushafTypeSelectionScreenClicked() {
        router.goForward(MushafPageTypeSelectionScreen())
    }

    fun onPageChanged(pageNumber: Int) = launch {
        interactor.saveLastReadPosition(pageNumber)

        viewState.switchToPage(pageNumber) //to save page position, when the view will be recreated
        mushafPageStateController.onPageChanged(pageNumber)
        loadPage(pageNumber)
    }

    fun onBookmarkPage(isBookmarked: Boolean) = launch {
        if (isPageInfoLoaded()) {
            interactor.setBookmarked(currentPage.pageNumber, isBookmarked)
        }
    }

    fun onShowAyahTranslationsClicked() = launch {
        mushafPageStateController.onShowTranslationClicked()
        viewState.hideAyahToolbar()
    }

    fun onTranslationPanelClosed() = launch {
        mushafPageStateController.onTranslationPanelClosed()
    }

    fun onOpenSettingsScreenClicked() {
        router.goForward(SettingsScreen())
    }

    fun onBookmarkAyahClicked(ayahId: AyahId, isAyahBookmarked: Boolean) = launch {
        if (isAyahBookmarked) {
            val folders = interactor.getBookmarkFolders()
            if (folders.size > 1) {
                router.goForward(BookmarkFoldersScreen(ayahId))
            } else {
                interactor.bookmarkAyah(ayahId)
                onAyahBookmarked(ayahId)
            }
        } else {
            interactor.removeAyahBookmark(ayahId)
            mushafPageCommandShell.unhighlightAyahs(
                    pageNumber = currentPage.pageNumber,
                    selectedAyahs = listOf(ayahId),
                    highlightType = AyahHighlightType.BOOKMARK
            )
        }
    }

    fun onAyahBookmarked(
            ayahId: AyahId
    ) = launch {
        mushafPageCommandShell.highlightAyahs(
                pageNumber = currentPage.pageNumber,
                selectedAyahs = listOf(ayahId),
                highlightType = AyahHighlightType.BOOKMARK,
                addToOld = true
        )
    }

    fun onShareAyahClicked(sharingType: SharingType) = launch {
        val selectedAyahs = mushafPageStateController.getSelectedAyahs()
        mushafPageStateController.stopSelectionMode()

        val enabledTranslations = interactor.getEnabledTranslations()
        router.goForward(TranslationsSharingScreen(
                translations = enabledTranslations,
                ayahs = selectedAyahs,
                type = sharingType
        ))
    }

    fun onPlayAyahClicked() = launch {
        viewState.showToolbar(false)
        val selectedAyahs = mushafPageStateController.getSelectedAyahs()
        mushafPageStateController.stopSelectionMode()

        val startAyah = selectedAyahs.first()
        val endAyah = selectedAyahs.last()
        viewState.showPlayerOptionsPanel(startAyah, endAyah)
    }

    fun saveLastReadPosition() = launch {
        if (isPageInfoLoaded() && !isReadModeSwitching) {
            interactor.saveLastReadPosition(currentPage.pageNumber)
        }
    }

    fun onOpenPlayerButtonClicked() = launch {
        viewState.showToolbar(false)
        if (playbackData.playbackState == PlayerState.IDLE) {
            val startAyah = AyahId(currentPage.surahNumber, currentPage.firstAyahNumber)
            val lastPageAyah = interactor.getPageLastAyahId(currentPage.pageNumber)
            viewState.showPlayerOptionsPanel(startAyah, lastPageAyah)
        } else {
            viewState.showPlayerControlPanel()
        }
    }

    fun onThemeSelected(theme: AppTheme) = launch {
        interactor.setMushafTheme(theme)
    }

    fun onEnableHorizontalModeClicked(isEnabled: Boolean) {
        viewState.setHorizontalMode(!isEnabled)
    }

    private fun checkIsImagesDownloadingNeeded() = launch {
        val isImagesDownloadingNeeded = interactor.isImagesDownloadingNeeded()
        if (isImagesDownloadingNeeded) {
            viewState.showImagesBundleDownloadSuggestion(true)
        } else {
            showInitialPage()
        }
    }

    private fun showInitialPage() = launch {
        viewState.showQuranPager()
        val currentPageNumber = interactor.getCurrentPageNumber()
        viewState.switchToPage(currentPageNumber)
        loadPage(currentPageNumber)
    }

    private suspend fun loadPage(pageNumber: Int) {
        val pageInfo = interactor.getPageInfo(pageNumber)
        onPageInfoLoaded(pageInfo)
    }

    private suspend fun onPageInfoLoaded(quranPage: QuranPage) {
        currentPage = quranPage
        viewState.showPageInfo(quranPage)

        delay(500) //highlight ayah after 0.5 seconds (waiting for the creation of the page fragment)

        //highlight target ayah, to which the user jumped
        if (ayahForHighlighting.value != null) {
            mushafPageCommandShell.highlightAyahs(
                    pageNumber = quranPage.pageNumber,
                    selectedAyahs = listOf(ayahForHighlighting.value!!),
                    highlightType = AyahHighlightType.SELECTION,
                    addToOld = false
            )
        }

        //highlight ayah, which is playing now
        val audioState = playbackData.playbackState
        val isAudioPlaying = audioState != PlayerState.IDLE && audioState != PlayerState.ERROR
        if (isAudioPlaying) {
            playbackData.currentAudio?.let { nowPlayingAyahAudio ->
                val nowPlayingAyahId = AyahId(nowPlayingAyahAudio.surahNumber, nowPlayingAyahAudio.ayahNumber)
                mushafPageCommandShell.highlightAyahs(
                        pageNumber = quranPage.pageNumber,
                        selectedAyahs = listOf(nowPlayingAyahId),
                        highlightType = AyahHighlightType.AUDIO,
                        addToOld = false
                )
            }
        }
    }

    private fun isPageInfoLoaded() = ::currentPage.isInitialized

    private fun onAyahClick(clickEvent: AyahClickEvent) = launch {
        when (clickEvent.clickType) {
            AyahClickEvent.ClickType.SINGLE -> mushafPageStateController.onAyahClick(clickEvent.ayahId, clickEvent.pageNumber, false)
            AyahClickEvent.ClickType.DOUBLE -> mushafPageStateController.onDoubleClick()
            AyahClickEvent.ClickType.LONG -> mushafPageStateController.onAyahClick(clickEvent.ayahId, clickEvent.pageNumber, true)
        }
    }

    private fun onAudioAyahChanged(currentAyah: AyahAudio) = launch {
        val ayahId = AyahId(currentAyah.surahNumber, currentAyah.ayahNumber)
        val pageNumber = interactor.getPageForAyah(currentAyah.surahNumber, currentAyah.ayahNumber)

        clearAyahAudioHighlights()
        mushafPageCommandShell.highlightAyahs(pageNumber, listOf(ayahId), AyahHighlightType.AUDIO, false)

        if (playbackData.autoScrollEnabled) {
            if (mushafPageStateController.isTranslationsPanelOpened()) {
                val currentAyahId = AyahId(currentAyah.surahNumber, currentAyah.ayahNumber)
                viewState.showAyahTranslations(listOf(currentAyahId))
                mushafPageCommandShell.highlightAyahs(pageNumber, listOf(currentAyahId), AyahHighlightType.SELECTION, false)
            }

            val isPageChanged = isPageInfoLoaded() && currentPage.pageNumber != pageNumber
            if (isPageChanged) {
                viewState.switchToPage(pageNumber)
            }
        }
    }

    private suspend fun clearAyahAudioHighlights() {
        if (isPageInfoLoaded()) {
            mushafPageCommandShell.highlightAyahs(
                    pageNumber = currentPage.pageNumber,
                    selectedAyahs = emptyList(),
                    highlightType = AyahHighlightType.AUDIO,
                    addToOld = false
            )
        }
    }

}