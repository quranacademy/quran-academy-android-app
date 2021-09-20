package org.quranacademy.quran.search.data

import android.database.Cursor
import org.quranacademy.quran.data.database.SurahNameProvider
import org.quranacademy.quran.data.database.adapters.AyahsArabicDatabaseAdapter
import org.quranacademy.quran.data.database.models.SearchResultModel
import org.quranacademy.quran.domain.commons.ResourcesManager
import org.quranacademy.quran.presentation.ui.global.toArabicNumberIfNeeded
import org.quranacademy.quran.search.R
import org.quranacademy.quran.search.domain.SearchResult
import javax.inject.Inject

class ArabicSearcher @Inject constructor(
        private val databaseAdapter: AyahsArabicDatabaseAdapter,
        private val surahNameProvider: SurahNameProvider,
        private val resourcesManager: ResourcesManager
) : BaseSearcher() {

    companion object {
        private val ARABIC_VOWELS = "[\u202C\u064B\u064C\u064E-\u0652]".toRegex()
        private val QURAN_SMALL_SIGNS = ("[" +
                "\u06d2\u06d3\u06d6\u06d7\u06d8\u06da\u06db\u06dc\u06e3" +
                "\u06e4\u06e5\u06e6\u06e7\u06e8]" +
                "").toRegex()
    }

    override suspend fun search(searchText: String): List<SearchResult> {
        val arabicQuery = searchText.replace(ARABIC_VOWELS, "")
        val searchQuery = "SELECT id FROM ayahs_search_text WHERE text LIKE '%$arabicQuery%'"
        val resultsCursor = databaseAdapter.rawQuery(searchQuery, null)
        val ids = getIds(resultsCursor).joinToString(", ")
        val ayahsQuery = "SELECT surah, ayah, text_uthmanic AS text FROM ayahs WHERE id IN ($ids)"
        val ayahsCursor = databaseAdapter.rawQuery(ayahsQuery, null)
        return mapResults(ayahsCursor, ::getDescription)
                .map {
                    it.copy(text = it.text.replace(QURAN_SMALL_SIGNS, ""))
                }
    }


    private fun getIds(cursor: Cursor): List<Long> {
        val ids = mutableListOf<Long>()
        if (cursor.count != 0) {
            if (cursor.moveToFirst()) {
                do {
                    ids.add(cursor.getLong(0))
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return ids
    }

    private fun getDescription(result: SearchResultModel): String {
        val surahName = surahNameProvider.getSurahName(result.surahNumber)
        return resourcesManager.getString(
                R.string.arabic_search_result_ayah_info,
                surahName,
                result.ayahNumber.toArabicNumberIfNeeded()
        )
    }

}
