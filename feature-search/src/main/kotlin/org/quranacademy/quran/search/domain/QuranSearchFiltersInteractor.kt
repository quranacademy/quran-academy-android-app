package org.quranacademy.quran.search.domain

import org.quranacademy.quran.search.data.QuranSearchRepository
import javax.inject.Inject

class QuranSearchFiltersInteractor @Inject constructor(
        private val quranSearchRepository: QuranSearchRepository
) {

    suspend fun getTranslationsForSearch(): List<SearchTranslationFilter> {
        return quranSearchRepository.getTranslationsForSearch()
    }

    suspend fun setTranslationsForSearch(filters: List<SearchTranslationFilter>) {
        quranSearchRepository.setTranslationsForSearch(filters)
    }


}
