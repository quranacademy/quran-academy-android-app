package org.quranacademy.quran.ayahsrepository

import android.util.Log
import org.quranacademy.quran.ayahsrepository.database.AyahDatabaseMapper
import org.quranacademy.quran.data.AyahsDataSource
import org.quranacademy.quran.data.database.daos.AyahTranslationsDao
import org.quranacademy.quran.data.database.daos.AyahsDao
import org.quranacademy.quran.data.translations.WordByWordDataSource
import org.quranacademy.quran.domain.models.Ayah
import org.quranacademy.quran.domain.models.AyahWord
import org.quranacademy.quran.domain.models.AyahWordGroup
import timber.log.Timber
import javax.inject.Inject

class AyahsDataSourceImpl @Inject constructor(
        private val ayahsDao: AyahsDao,
        private val ayahTranslationsDao: AyahTranslationsDao,
        private val ayahDatabaseMapper: AyahDatabaseMapper,
        private val ayahTranslationDatabaseMapper: AyahTranslationDatabaseMapper,
        private val wordByWordDataSource: WordByWordDataSource
) : AyahsDataSource {

    override fun getSurahAyahs(surahNumber: Int): List<Ayah> {
        Timber.i("Загрузка аятов суры №$surahNumber")
        val ayahs = loadAyahs(surahNumber)
        loadAyahTranslations(surahNumber, ayahs)
        loadWordByWordTranslations(surahNumber, ayahs)
        return ayahs
    }

    private fun loadAyahs(surahNumber: Int): List<Ayah> {
        val ayahModels = ayahsDao.getSurahAyahs(surahNumber)
        return ayahDatabaseMapper.map(ayahModels)
    }

    private fun loadAyahTranslations(surahNumber: Int, ayahs: List<Ayah>) {
        val ayahTranslationModelsMap = ayahTranslationsDao.getTranslationsForShowingInList(surahNumber)
        ayahTranslationModelsMap.keys.forEach { translation ->
            val ayahTranslationModels = ayahTranslationModelsMap[translation]!!
            ayahs.forEachIndexed { ayahIndex, ayah ->
                val ayahTranslation = ayahTranslationDatabaseMapper.map(ayahTranslationModels[ayahIndex], translation)
                ayah.translations.add(ayahTranslation)
            }
        }
    }

    private fun loadWordByWordTranslations(surahNumber: Int, ayahs: List<Ayah>) {
        val isWordByWordEnabled = wordByWordDataSource.isWordByWordEnabled()
        val wordByWord = wordByWordDataSource.getSurahWordByWord(surahNumber, ayahs.size)
        ayahs.forEachIndexed { index, ayah ->
            ayah.words = wordByWord[index]
            ayah.isWordByWordEnabled = isWordByWordEnabled
        }
    }

}