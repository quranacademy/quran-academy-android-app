package org.quranacademy.quran.wordbywordrepository

import org.quranacademy.quran.data.database.adapters.WordByWordDatabaseAdapter
import org.quranacademy.quran.data.database.adapters.WordByWordTranslationDatabaseAdapter
import org.quranacademy.quran.data.database.adapters.WordByWordTranslationDatabaseManager
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.data.translations.WordByWordDataSource
import org.quranacademy.quran.domain.models.AyahWordItem
import org.quranacademy.quran.wordbywordrepository.database.AyahWordDatabaseMapper
import javax.inject.Inject

private class TranslationInfo(
        val adapter: WordByWordTranslationDatabaseAdapter?,
        val isWordsCached: Boolean
)

class WordByWordDataSourceImpl @Inject constructor(
        private val appPreferences: AppPreferences,
        private val arabicWordByWordAdapter: WordByWordDatabaseAdapter,
        private val wordByWordTranslationDatabaseManager: WordByWordTranslationDatabaseManager,
        private val ayahWordDatabaseMapper: AyahWordDatabaseMapper,
        private val wordByWordGrouper: WordByWordGrouper,
        private val wordByWordCache: WordByWordCache
) : WordByWordDataSource {

    override fun isWordByWordEnabled(): Boolean = with(appPreferences) {
        isWordByWordEnabled() && getCurrentWbwTranslation() != null
    }

    override fun getSurahWordByWord(surahNumber: Int, ayahsCount: Int): List<List<AyahWordItem>> {
        val info = getWordByWordTranslationAdapter(surahNumber)
        return if (info.isWordsCached) {
            wordByWordCache.getSurahWordByWord()
        } else {
            getSurahWordsFromDatabase(surahNumber, ayahsCount, info.adapter)
        }
    }

    override fun getAyahWordByWord(surahNumber: Int, ayahNumber: Int): List<AyahWordItem> {
        val info = getWordByWordTranslationAdapter(surahNumber)
        return if (info.isWordsCached) {
            wordByWordCache.getAyahWordByWord(ayahNumber)
        } else {
            getAyahFromDatabase(surahNumber, ayahNumber, info.adapter)
        }
    }

    private fun getWordByWordTranslationAdapter(surahNumber: Int): TranslationInfo {
        val haveActiveWordByWordTranslation = appPreferences.getCurrentWbwTranslation() != null
        val adapter = if (haveActiveWordByWordTranslation) {
            wordByWordTranslationDatabaseManager.getCurrentTranslationAdapter()
        } else {
            null
        }
        val isWordsCached = adapter?.let {
            wordByWordCache.isWordsCached(surahNumber, it.getTranslation())
        } ?: false
        return TranslationInfo(adapter, isWordsCached)
    }

    private fun getSurahWordsFromDatabase(
            surahNumber: Int,
            ayahsCount: Int,
            adapter: WordByWordTranslationDatabaseAdapter?
    ): List<List<AyahWordItem>> {
        val surahWordModels = arabicWordByWordAdapter.getWordsForSurah(surahNumber)
        val surahWordTranslations = adapter?.getSurahWordTranslations(surahNumber)
        val surahWords = (1..ayahsCount).map { ayahNumber ->
            val ayahWordModels = surahWordModels.filter { it.ayahNumber == ayahNumber }
            val ayahWordTranslationModels = surahWordTranslations?.filter { it.ayahNumber == ayahNumber }
            val ayahWords = ayahWordDatabaseMapper.map(ayahWordModels, ayahWordTranslationModels)
            if (adapter != null) {
                wordByWordGrouper.groupWords(ayahWords)
            } else {
                ayahWords
            }
        }

        if (adapter != null) {
            wordByWordCache.saveToCache(surahNumber, adapter.getTranslation(), surahWords)
        }

        return surahWords
    }

    private fun getAyahFromDatabase(
            surahNumber: Int,
            ayahNumber: Int,
            adapter: WordByWordTranslationDatabaseAdapter?
    ): List<AyahWordItem> {
        val ayahWordsModels = arabicWordByWordAdapter.getWordsForAyah(surahNumber, ayahNumber)
        val ayahWordTranslationModels = adapter?.getAyahWordTranslations(surahNumber, ayahNumber)
        val ayahWords = ayahWordDatabaseMapper.map(ayahWordsModels, ayahWordTranslationModels)
        return if (adapter != null) {
            wordByWordGrouper.groupWords(ayahWords)
        } else {
            ayahWords
        }
    }

}