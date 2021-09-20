package org.quranacademy.quran.wordbywordrepository

import org.quranacademy.quran.data.network.NetworkChecker
import org.quranacademy.quran.data.network.QuranAcademyApi
import org.quranacademy.quran.domain.exceptions.NoNetworkException
import org.quranacademy.quran.domain.models.Language
import org.quranacademy.quran.domain.models.WordByWordTranslation
import org.quranacademy.quran.wordbywordrepository.database.WordByWordTranslationDatabaseMapper
import timber.log.Timber
import javax.inject.Inject

class WordByWordTranslationsDataSource @Inject constructor(
        private val quranAcademyApi: QuranAcademyApi,
        private val wordByWordTranslationsCache: WordByWordTranslationsCache,
        private val networkChecker: NetworkChecker,
        private val translationResponseDatabaseMapper: WordByWordTranslationResponseDatabaseMapper,
        private val wordByWordTranslationDatabaseMapper: WordByWordTranslationDatabaseMapper
) {

    var onListUpdatedListener: (() -> Unit)? = null

    suspend fun getTranslations(language: Language? = null, forceLoad: Boolean): List<WordByWordTranslation> {
        val downloadFromServer = if (wordByWordTranslationsCache.isCacheEmpty()) {
            true
        } else if (!networkChecker.isConnected) {
            false
        } else {
            wordByWordTranslationsCache.isCacheExpired() || forceLoad
        }
        val translationModels = if (downloadFromServer) {
            Timber.i("Загрузка списка пословных переводов с сервера")
            val wordByWordTranslationsResponse = quranAcademyApi.getWordByWordTranslationsList(language?.code)
            val wordByWordTranslationModels = translationResponseDatabaseMapper.map(wordByWordTranslationsResponse.translations)
            wordByWordTranslationsCache.saveTranslations(wordByWordTranslationModels)
            wordByWordTranslationModels.also {
                onListUpdatedListener?.invoke()
            }
        } else {
            Timber.i("Загрузка списка пословных переводов из кеша")
            wordByWordTranslationsCache.getTranslations()
        }

        val translations = wordByWordTranslationDatabaseMapper.mapFromDatabase(translationModels)
        //если нет интернета и список пустой, показываем ошибку
        return if (translations.isEmpty() && !networkChecker.isConnected) {
            throw NoNetworkException()
        } else {
            translations
        }
    }

    suspend fun getTranslationsWithUpdates(
            language: Language? = null,
            checkForUpdatesFromServer: Boolean
    ): List<WordByWordTranslation> {
        getTranslations(language, checkForUpdatesFromServer) //инициируем обновление списка
        val translationModels = wordByWordTranslationsCache.getTranslationsWithUpdates()
        return wordByWordTranslationDatabaseMapper.mapFromDatabase(translationModels)
    }

}