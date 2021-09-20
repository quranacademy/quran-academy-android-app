package org.quranacademy.quran.search.presentation

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.presentation.mvp.BaseMvpView
import org.quranacademy.quran.search.domain.QuranSearchResults
import org.quranacademy.quran.search.domain.SearchTranslationFilter

@StateStrategyType(AddToEndSingleStrategy::class)
interface QuranSearchView : BaseMvpView {

    fun showSearchTranslations(filters: List<SearchTranslationFilter>)

    fun hideFiltersButton()

    fun showProgress(isVisible: Boolean)

    fun showEmptySearch(isVisible: Boolean)

    fun showResults(isVisible: Boolean)

    fun showResults(searchResults: QuranSearchResults)

    fun showEmptyResults(queryText: String)

    fun hideEmptyResults()

}