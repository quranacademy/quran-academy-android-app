package org.quranacademy.quran.search.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.presentation.ui.appearance.LanguageManager
import org.quranacademy.quran.search.data.QuranSearchRepository
import javax.inject.Inject

class QuranSearchInteractor @Inject constructor(
        private val quranSearchRepository: QuranSearchRepository,
        private val languageManager: LanguageManager,
        private val appPreferences: AppPreferences
) {

    suspend fun getSearchFilters(): List<SearchTranslationFilter> {
        return quranSearchRepository.getTranslationsForSearch()
    }

    suspend fun setSearchFilters(filters: List<SearchTranslationFilter>) {
        quranSearchRepository.setTranslationsForSearch(filters)
    }

    suspend fun search(text: String): QuranSearchResults {
        return quranSearchRepository.search(text, isArabicText(text))
    }

    suspend fun setLastReadPosition(
            surahNumber: Int,
            ayahNumber: Int
    ) = withContext(Dispatchers.IO) {
        val readSettings = appPreferences.getReadSettings()
        readSettings.lastReadSurah = surahNumber
        readSettings.lastReadAyah = ayahNumber
        appPreferences.saveReadSettings(readSettings)
    }

    suspend fun isCurrentModeMushaf() = withContext(Dispatchers.IO) {
        appPreferences.getReadSettings().isMushafMode
    }

    fun isArabicText(text: String): Boolean = SearchUtils.doesStringContainArabic(text)

    fun isCurrentLanguageRtl(): Boolean = languageManager.getCurrentAppLanguage().isRtl

}
