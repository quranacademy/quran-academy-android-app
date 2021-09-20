package org.quranacademy.quran.mushaf.presentation.mvp.mushafpage

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.di.PrimitiveWrapper
import org.quranacademy.quran.domain.models.QuranPage
import org.quranacademy.quran.mushaf.di.mushafpage.PageNumber
import org.quranacademy.quran.mushaf.domain.page.AyahByPositionNotFoundException
import org.quranacademy.quran.mushaf.domain.page.MushafPageInteractor
import org.quranacademy.quran.mushaf.presentation.mvp.AyahClickEvent
import org.quranacademy.quran.mushaf.presentation.mvp.HighlightAyahsCommand
import org.quranacademy.quran.mushaf.presentation.mvp.MushafPageCommandShell
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.ErrorHandler
import javax.inject.Inject

@InjectViewState
class MushafPagePresenter @Inject constructor(
        @PageNumber private val pageNumber: PrimitiveWrapper<Int>,
        private val interactor: MushafPageInteractor,
        private val mushafPageCommandShell: MushafPageCommandShell
) : BasePresenter<MushafPageView>() {

    private val ayahHighlightsManager = AyahHighlightsManager()
    private lateinit var pageInfo: QuranPage

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        launch {
            mushafPageCommandShell.highlightAyahsCommands()
                    .collect { onNewHighlightCommand(it) }
        }

        launch {
            interactor.getMushafTypeChangingUpdates().collect {
                loadPageInfo()
            }
        }

        launch { loadPageInfo() }
    }

    fun onRetryLoadPageClicked() = launch {
        viewState.showImageDownloadError(false)
        downloadPageImage()
    }

    fun onPageClickEvent(event: PageTouchEvent) = launch {
        if (!::pageInfo.isInitialized) {
            return@launch
        }

        try {
            val x = event.x
            val y = event.y

            val selectedAyah = interactor.getAyahIdByPosition(x, y, pageInfo.pageBounds)
            val clickType = when (event.type) {
                PageTouchEvent.TouchType.SINGLE -> AyahClickEvent.ClickType.SINGLE
                PageTouchEvent.TouchType.DOUBLE -> AyahClickEvent.ClickType.DOUBLE
                PageTouchEvent.TouchType.LONG -> AyahClickEvent.ClickType.LONG
            }
            mushafPageCommandShell.onAyahClick(selectedAyah, clickType, pageInfo.pageNumber)
        } catch (error: Exception) {
            when (error) {
                is AyahByPositionNotFoundException -> {
                }
                else -> errorHandler.proceed(error) { viewState.showMessage(it) }
            }
        }
    }

    private fun onNewHighlightCommand(command: HighlightAyahsCommand) {
        if (isPageLoaded() && pageInfo.pageNumber != command.pageNumber) {
            return
        }

        if (command.isHighlighted) {
            ayahHighlightsManager.highlightAyahs(command.selectedAyahs, command.highlightType, command.addToOld)
        } else {
            ayahHighlightsManager.unhighlightAyahsWithType(command.selectedAyahs, command.highlightType)
        }
        viewState.updateAyahHighlighting(ayahHighlightsManager.currentHighlights)
    }

    private suspend fun loadPageInfo() {
        this.pageInfo = interactor.getPageInfo(pageNumber.value)
        if (pageInfo.isImageExists) {
            viewState.showPage(pageInfo)
            loadBookmarks()
        } else {
            downloadPageImage()
        }
    }

    private suspend fun showPage() {
        viewState.showPage(pageInfo)
        loadBookmarks()
    }

    private suspend fun loadBookmarks() {
        val bookmarks = interactor.getPageBookmarks(pageInfo.pageNumber)
        ayahHighlightsManager.highlightAyahs(bookmarks, AyahHighlightType.BOOKMARK, false)
        viewState.updateAyahHighlighting(ayahHighlightsManager.currentHighlights)
    }

    private suspend fun downloadPageImage() {
        try {
            viewState.showImageDownloadProgress(true)
            interactor.downloadPageImage(pageInfo.pageNumber) {
                viewState.updateImageDownloadProgress(it)
            }
            showPage()
        } catch (error: Exception) {
            viewState.showImageDownloadError(true)
        } finally {
            viewState.showImageDownloadProgress(false)
        }
    }

    private fun isPageLoaded(): Boolean = ::pageInfo.isInitialized

}