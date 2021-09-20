package org.quranacademy.quran.surahsrepository

import org.quranacademy.quran.data.SurahsListDownloader
import org.quranacademy.quran.data.database.daos.SurahsDao
import org.quranacademy.quran.data.database.daos.SurahsNameTranslationsDao
import org.quranacademy.quran.data.network.QuranAcademyApi
import org.quranacademy.quran.data.network.responses.SurahResponse
import org.quranacademy.quran.domain.models.Language
import javax.inject.Inject

class SurahsListDownloaderImpl @Inject constructor(
        private val quranAcademyApi: QuranAcademyApi,
        private val surahsDao: SurahsDao,
        private val surahsNameTranslationsDao: SurahsNameTranslationsDao,
        private val surahResponseDatabaseMapper: SurahResponseDatabaseMapper,
        private val surahNameTranslationResponseDatabaseMapper: SurahNameTranslationResponseDatabaseMapper
) : SurahsListDownloader {

    override suspend fun downloadSurahsList(language: Language) {
        val surahsList = quranAcademyApi.getSurahs(language.code)
        deleteSurahsListForLanguage(language)
        saveSurahs(surahsList.surahs, language)
    }

    override suspend fun deleteSurahsListForLanguage(language: Language) {
        surahsNameTranslationsDao.deleteSurahNamesForLanguage(language)
    }

    private fun saveSurahs(surahResponses: List<SurahResponse>, language: Language) {
        val surahModels = surahResponseDatabaseMapper.map(surahResponses)
        val surahNames = surahNameTranslationResponseDatabaseMapper.map(surahResponses, language)
        surahsDao.saveSurahsList(surahModels)
        surahsNameTranslationsDao.saveSurahNames(surahNames)
    }

}