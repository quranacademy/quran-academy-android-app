package org.quranacademy.quran.mushaf.presentation.mvp.mushaf.statecontroller

import org.quranacademy.quran.domain.models.AyahId

sealed class MushafEvent {

    class OnAyahClick(
            val ayahId: AyahId,
            val pageNumber: Int,
            val isLongClick: Boolean
    ) : MushafEvent()

    class OnAyahLongClick(
            val ayahId: AyahId,
            val pageNumber: Int
    ) : MushafEvent()

    class OnDoubleClick : MushafEvent()

    class OnBackPressed : MushafEvent()

    class OnStopSelectionMode : MushafEvent()

    class OnShowTranslationClicked : MushafEvent()

    class OnTranslationPanelClosed : MushafEvent()

    class OnPageChanged(val pageNumber: Int) : MushafEvent()

  // onPageClick()
  // highlightAyahs(ayahs: List<AyahId>, showToolbar: Boolean = true)
  // clearAyahsHighlight(pageNumber: Int)
  // onError(error: Throwable)

  // suspend fun showAyahTranslation(ayahs: List<AyahId>)
  // suspend fun closeTranslationPanel()
}