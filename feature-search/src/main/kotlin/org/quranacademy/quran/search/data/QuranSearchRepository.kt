package org.quranacademy.quran.search.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.repositories.TranslationsRepository
import org.quranacademy.quran.search.domain.QuranSearchResults
import org.quranacademy.quran.search.domain.SearchTranslationFilter
import javax.inject.Inject

class QuranSearchRepository @Inject constructor(
        private val appPreferences: AppPreferences,
        private val arabicSearcher: ArabicSearcher,
        private val translationsRepository: TranslationsRepository,
        private val translationsSearcher: TranslationsSearcher
) {

    init {
        GlobalScope.launch(context = Dispatchers.IO) {
            translationsRepository.getEnabledTranslationsListUpdates().collect {
                updateSearchTranslationFilters()
            }
        }
    }

    suspend fun search(
            searchQuery: String,
            isArabicSearch: Boolean
    ): QuranSearchResults = withContext(Dispatchers.IO) {
        val searcher = if (isArabicSearch) arabicSearcher else translationsSearcher
        val results = searcher.search(searchQuery)
        return@withContext QuranSearchResults(searchQuery, results, isArabicSearch)
    }

    suspend fun getTranslationsForSearch(): List<SearchTranslationFilter> {
        val enabledTranslations = translationsRepository.getEnabledTranslations()
        val filters = appPreferences.getTranslationsForSearch()
                ?: enabledTranslations.map { it.code }
        return enabledTranslations.map {
            SearchTranslationFilter(
                    name = it.name,
                    code = it.code,
                    isSelected = filters.contains(it.code)
            )
        }
    }

    suspend fun setTranslationsForSearch(translations: List<SearchTranslationFilter>) {
        val translationCodes = translations.map { it.code }
        appPreferences.setTranslationsForSearch(translationCodes)
    }

    private suspend fun updateSearchTranslationFilters() {
        val enabledTranslations = translationsRepository.getEnabledTranslations().map { it.code }

        val resultSearchFilters = appPreferences.getTranslationsForSearch()
                ?.filter {
                    //remove disabled translations
                    enabledTranslations.contains(it)
                }
                ?.let {
                    val newTranslations = it.toSet().minus(enabledTranslations.toSet())
                    it + newTranslations
                } ?: enabledTranslations

        appPreferences.setTranslationsForSearch(resultSearchFilters)
    }

}