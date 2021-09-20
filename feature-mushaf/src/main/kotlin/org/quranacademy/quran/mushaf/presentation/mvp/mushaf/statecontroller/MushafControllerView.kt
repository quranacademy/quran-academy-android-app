package org.quranacademy.quran.mushaf.presentation.mvp.mushaf.statecontroller

import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.QuranPage
import org.quranacademy.quran.mushaf.domain.MushafInteractor
import org.quranacademy.quran.mushaf.presentation.mvp.MushafPageCommandShell
import org.quranacademy.quran.mushaf.presentation.mvp.mushaf.MushafView
import org.quranacademy.quran.mushaf.presentation.mvp.mushafpage.AyahHighlightType
import org.quranacademy.quran.presentation.mvp.ErrorHandler

class MushafControllerView(
        private val interactor: MushafInteractor,
        private val viewState: MushafView,
        private val mushafPageCommandShell: MushafPageCommandShell,
        private val errorHandler: ErrorHandler,
        private val currentPage: () -> QuranPage
) {

    private var isToolbarVisible = true

    fun onPageClick() {
        isToolbarVisible = !isToolbarVisible
        viewState.showToolbar(isToolbarVisible)
    }

    suspend fun highlightAyahs(ayahs: List<AyahId>, showToolbar: Boolean = true) {
        mushafPageCommandShell.highlightAyahs(
                pageNumber = currentPage().pageNumber,
                selectedAyahs = ayahs,
                highlightType = AyahHighlightType.SELECTION,
                addToOld = false
        )

        if (showToolbar) {
            val firstAyah = ayahs.first()
            val firstAyahBounds = currentPage().pageBounds.pageAyahsBounds[firstAyah]
            val ayahDetails = interactor.getAyahDetails(firstAyah)
            viewState.showAyahToolbar(ayahDetails, firstAyahBounds!!)
        }
    }

    suspend fun clearAyahsHighlight(pageNumber: Int) {
        mushafPageCommandShell.highlightAyahs(
                pageNumber = pageNumber,
                selectedAyahs = emptyList(),
                highlightType = AyahHighlightType.SELECTION,
                addToOld = false
        )
        viewState.hideAyahToolbar()
    }

    fun onError(error: Throwable) {
        errorHandler.proceed(error) { viewState.showMessage(it) }
    }

    fun showAyahTranslation(ayahs: List<AyahId>) {
        viewState.showAyahTranslations(ayahs)
        viewState.showToolbar(false)
    }

    fun closeTranslationPanel() {
        viewState.closeTranslationPanel()
    }

}