package org.quranacademy.quran.search.presentation

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.aartikov.alligator.Screen
import org.quranacademy.quran.Constants
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.routing.screens.MushafScreen
import org.quranacademy.quran.presentation.mvp.routing.screens.SurahDetailsScreen
import org.quranacademy.quran.search.domain.QuranSearchInteractor
import org.quranacademy.quran.search.domain.SearchResult
import org.quranacademy.quran.search.domain.SearchTranslationFilter
import javax.inject.Inject

@InjectViewState
class QuranSearchPresenter @Inject constructor(
        private val interactor: QuranSearchInteractor
) : BasePresenter<QuranSearchView>() {

    private var lastQuery: String = ""
    private var searchRequest: Job? = null
    private lateinit var searchFilters: List<SearchTranslationFilter>

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        launch {
            searchFilters = interactor.getSearchFilters()
            if (searchFilters.isEmpty()) {
                viewState.hideFiltersButton()
            }
        }
    }

    fun onOpenSearchFiltersClicked() = launch {
        viewState.showSearchTranslations(searchFilters)
    }

    fun onSearchTranslationsSelected(selectedTranslations: List<SearchTranslationFilter>) = launch {
        interactor.setSearchFilters(selectedTranslations)
        onSearchInput(lastQuery)
    }

    fun onSearchInput(text: String) {
        searchRequest?.cancel()

        val queryText = text.trimStart()
        lastQuery = queryText
        if (text.isBlank() || getNoResultsLabel(queryText).length < 3) {
            viewState.showResults(false)
            viewState.hideEmptyResults()
            viewState.showEmptySearch(true)
            return
        }

        searchRequest = launch {
            try {
                viewState.showEmptySearch(false)
                viewState.hideEmptyResults()
                viewState.showProgress(true)
                val results = interactor.search(queryText)
                viewState.showProgress(false)
                if (isActive) {
                    if (results.results.isNotEmpty()) {
                        viewState.showResults(true)
                        viewState.showResults(results)
                    } else {
                        viewState.showResults(false)
                        viewState.showEmptyResults(getNoResultsLabel(text))
                    }
                }
            } catch (e: Exception) {
                viewState.showProgress(false)
                viewState.showResults(false)
                viewState.showEmptyResults(getNoResultsLabel(text))
                errorHandler.proceed(e, viewState::showMessage)
            }
        }
    }

    fun onOpenSearchResultClicked(result: SearchResult) = launch {
        val ayahId = AyahId(result.surahNumber, result.ayahNumber)
        interactor.setLastReadPosition(ayahId.surahNumber, ayahId.ayahNumber)

        val isMushafMode = interactor.isCurrentModeMushaf()
        val quranScreen: Screen = if (isMushafMode) MushafScreen(ayahId) else SurahDetailsScreen()
        router.goForward(quranScreen)
    }

    private fun getNoResultsLabel(text: String): String {
        val isCurrentLanguageRtl = interactor.isCurrentLanguageRtl()
        val isQueryInArabic = interactor.isArabicText(text)
        if (!isCurrentLanguageRtl && isQueryInArabic) {
            return Constants.RTL_FIXER_SYMBOL + text
        }
        return text
    }

}