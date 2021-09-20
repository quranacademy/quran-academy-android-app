package org.quranacademy.quran.mainscreen.presentation.mvp.surahslist

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import me.aartikov.alligator.Screen
import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.Surah
import org.quranacademy.quran.mainscreen.domain.surahslist.SurahsListInteractor
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.routing.screens.MushafScreen
import org.quranacademy.quran.presentation.mvp.routing.screens.SurahDetailsScreen
import javax.inject.Inject

@InjectViewState
class SurahsListPresenter @Inject constructor(
        private val interactor: SurahsListInteractor
) : BasePresenter<SurahsListView>() {

    val FIRST_SURAH_AYAH = 1

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        launch {
            interactor.getLanguageChanges()
                    .consumeEach { loadSurahsList() }
        }

        launch { loadSurahsList() }
    }

    fun onSurahSelected(surah: Surah) = launch {
        interactor.saveLastReadPosition(surah.surahNumber, FIRST_SURAH_AYAH)
        openQuranScreen()
    }

    fun onJuzSelected(juz: QuranJuz) = launch {
        val juzAyah = juz.ayah
        val pageSurah = juzAyah.surahNumber
        val pageAyah = juzAyah.ayahNumber
        interactor.saveLastReadPosition(pageSurah, pageAyah)
        openQuranScreen()
    }

    fun saveLastScrollPosition(position: Int) = launch {
        interactor.saveLastScrollPosition(position)
    }

    private suspend fun loadSurahsList() {
        viewState.updateSurahsVisibility(false)
        viewState.showProgressLayout(true)
        val surahs = interactor.getSurahsList()
        viewState.showProgressLayout(false)
        onSurahsListLoaded(surahs)
        restoreScrollPosition()
    }

    private fun onSurahsListLoaded(surahs: List<Surah>) {
        viewState.updateSurahsVisibility(true)
        val surahsGroupedByJuz = (1..QuranConstants.JUZES_COUNT).map { juzNumber ->
            val juzSurahs = surahs.filter { it.juzNumber == juzNumber }
            val juzIndex = juzNumber - 1
            val juzAyah = QuranConstants.AYAH_SURAH[juzIndex]
            return@map QuranJuz(
                    number = juzNumber,
                    startPageNumber = QuranConstants.PAGE_FOR_JUZ[juzIndex],
                    ayah = AyahId(juzAyah.first, juzAyah.second),
                    surahs = juzSurahs
            )
        }
        viewState.showSurahs(surahsGroupedByJuz)
    }

    private fun restoreScrollPosition() {
        val lastScrollPosition = interactor.getLastScrollPosition()
        viewState.scrollTo(lastScrollPosition)
    }

    private suspend fun openQuranScreen() {
        val isMushafMode = interactor.isMushafMode()
        val quranScreen: Screen = if (isMushafMode) MushafScreen() else SurahDetailsScreen()
        router.goForward(quranScreen)

    }

}