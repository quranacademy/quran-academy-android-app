package org.quranacademy.quran.languagesrepository

import org.quranacademy.quran.data.network.NetworkChecker
import org.quranacademy.quran.data.network.QuranAcademyApi
import org.quranacademy.quran.domain.models.Language
import org.quranacademy.quran.languagesrepository.database.LanguageDatabaseMapper
import org.quranacademy.quran.languagesrepository.database.LanguageResponseDatabaseMapper
import timber.log.Timber
import javax.inject.Inject

class LanguagesDataSource @Inject constructor(
        private val quranAcademyApi: QuranAcademyApi,
        private val languagesCache: LanguagesCache,
        private val networkChecker: NetworkChecker,
        private val languageResponseDatabaseMapper: LanguageResponseDatabaseMapper,
        private val languageDatabaseMapper: LanguageDatabaseMapper
) {

    suspend fun getLanguagesList(forceLoad: Boolean): List<Language> {
        val downloadFromServer = if (languagesCache.isCacheEmpty()) {
            true
        } else if (!networkChecker.isConnected) {
            false
        } else {
            languagesCache.isCacheExpired() || forceLoad
        }
        return if (downloadFromServer) {
            Timber.i("Загрузка списка языков с сервера")
            val languagesResponse = quranAcademyApi.getLanguages()
            val languagesDbModels = languageResponseDatabaseMapper.map(languagesResponse.languages)
            languagesCache.saveLanguages(languagesDbModels)
            languageDatabaseMapper.mapFromDatabase(languagesDbModels)
        } else {
            Timber.i("Загрузка списка языков из кеша")
            val languagesDbModels = languagesCache.getLanguages()
            languageDatabaseMapper.mapFromDatabase(languagesDbModels)
        }
    }

    fun getLanguage(languageCode: String): Language {
        val languageModel = languagesCache.getLanguage(languageCode)
        return languageDatabaseMapper.mapFromDatabase(languageModel)
    }

}