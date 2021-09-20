package org.quranacademy.quran.mainscreen.presentation.mvp.jumptoayah

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.launch
import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.Surah
import org.quranacademy.quran.mainscreen.R
import org.quranacademy.quran.mainscreen.domain.JumpToAyahInteractor
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.routing.screens.MushafScreen
import org.quranacademy.quran.presentation.mvp.routing.screens.SurahDetailsScreen
import javax.inject.Inject

@InjectViewState
class JumpToAyahPresenter @Inject constructor(
        private val interactor: JumpToAyahInteractor
) : BasePresenter<JumpToAyahView>() {

    private lateinit var surahs: List<Surah>
    private var selectedSurah: Int = 1
    private var selectedAyah: Int = 1

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        launch {
            surahs = interactor.getSurahsList()
            viewState.showSurahsList(surahs)
            viewState.configureAyahSelector(surahs.first().ayahsCount)
        }
    }

    fun onSurahSelected(surahNumber: Int) {
        this.selectedSurah = surahNumber
        this.selectedAyah = 1
        viewState.configureAyahSelector(surahs[surahNumber - 1].ayahsCount)
        updatePageNumber()
    }

    fun onAyahSelected(selectedAyah: Int) {
        this.selectedAyah = selectedAyah
        updatePageNumber()
    }

    fun onPageInput(pageNumber: Int) {
        if (pageNumber in (1..QuranConstants.QURAN_PAGES_COUNT)) {
            selectedSurah = QuranConstants.SURAH_FOR_PAGE[pageNumber - 1]
            selectedAyah = QuranConstants.AYAH_FOR_PAGE[pageNumber - 1]
            viewState.configureAyahSelector(surahs[selectedSurah - 1].ayahsCount, selectedAyah)
            viewState.setCurrentSurahAndAyah(selectedSurah, selectedAyah)
        }
    }

    fun onPageSelected(pageNumber: Int) {
        if (pageNumber <= QuranConstants.QURAN_PAGES_COUNT) {
            selectedSurah = QuranConstants.SURAH_FOR_PAGE[pageNumber - 1]
            selectedAyah = QuranConstants.AYAH_FOR_PAGE[pageNumber - 1]
            viewState.showReadModeSelectionDialog()
        } else {
            viewState.showMessage(resourcesManager.getString(R.string.max_quran_page_number_error_message))
        }
    }

    fun onOkButtonClicked() {
        viewState.showReadModeSelectionDialog()
    }

    fun onReadModeSelected(isMushafMode: Boolean) = launch {
        interactor.setLastReadPosition(selectedSurah, selectedAyah, isMushafMode)
        router.goBack()
        if (isMushafMode) {
            router.goForward(MushafScreen(AyahId(selectedSurah, selectedAyah)))
        } else {
            router.goForward(SurahDetailsScreen())
        }
    }


    private fun updatePageNumber() = launch {
        val ayahPageNumber = interactor.getAyahPageNumber(selectedSurah, selectedAyah)
        viewState.showPageNumberHint(ayahPageNumber)
    }

}