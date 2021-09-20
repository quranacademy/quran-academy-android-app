package org.quranacademy.quran.search.data

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import org.quranacademy.quran.data.database.SurahNameProvider
import org.quranacademy.quran.data.database.adapters.TranslationDatabaseAdapter
import org.quranacademy.quran.data.database.adapters.TranslationDatabaseManager
import org.quranacademy.quran.data.database.models.SearchResultModel
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.commons.ResourcesManager
import org.quranacademy.quran.domain.models.Translation
import org.quranacademy.quran.presentation.ui.global.toArabicNumberIfNeeded
import org.quranacademy.quran.search.R
import org.quranacademy.quran.search.domain.SearchResult
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume

class TranslationsSearcher @Inject constructor(
        private val appPreferences: AppPreferences,
        private val translationDatabaseManager: TranslationDatabaseManager,
        private val surahNameProvider: SurahNameProvider,
        private val resourcesManager: ResourcesManager
) : BaseSearcher() {

    companion object {
        const val TABLE_NAME = "translation"
        const val SNIPPET = "snippet($TABLE_NAME, '$HIGHLIGHT_START', '$HIGHLIGHT_END', '$ELLIPSIZE', -1, 64)"
    }

    override suspend fun search(searchText: String) = suspendCancellableCoroutine<List<SearchResult>> { continuation ->
        val query = "SELECT sura AS surah, ayat AS ayah, $SNIPPET AS text FROM $TABLE_NAME WHERE text MATCH '$searchText*'"
        val allResults = mutableListOf<SearchResult>()

        val translationsForSearch = appPreferences.getTranslationsForSearch()
        val translationAdapters = translationDatabaseManager.getAdapters()
                .filter { !it.getTranslation().isArabic } //search by arabic tafseers isn't needed
                .filter {
                    translationsForSearch?.contains(it.getTranslation().code) ?: true
                }
        for (adapter in translationAdapters) {
            if (continuation.isCancelled) {
                continuation.cancel()
                return@suspendCancellableCoroutine
            }

            try {
                val results = runBlocking { search(adapter, query) }
                allResults.addAll(results)
            } catch (error: Exception) {
                val translationName = adapter.getTranslation().name
                Timber.e("Error when search by translation ($translationName)", error)
            }
        }

        continuation.resume(allResults)
    }

    private suspend fun search(it: TranslationDatabaseAdapter, query: String): List<SearchResult> {
        val translation = it.getTranslation()
        val resultsCursor = it.rawQuery(query)
        val descriptionGenerator = { result: SearchResultModel -> getDescription(translation, result) }
        return mapResults(resultsCursor, descriptionGenerator)
    }

    private fun getDescription(translation: Translation, result: SearchResultModel): String {
        val surahName = surahNameProvider.getSurahName(result.surahNumber)
        return resourcesManager.getString(
                R.string.translation_search_result_ayah_info,
                surahName,
                result.ayahNumber.toArabicNumberIfNeeded(),
                translation.name
        )
    }

}
