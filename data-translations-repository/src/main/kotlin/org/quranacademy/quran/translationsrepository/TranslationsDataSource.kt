package org.quranacademy.quran.translationsrepository

import org.quranacademy.quran.data.network.NetworkChecker
import org.quranacademy.quran.data.network.QuranAcademyApi
import org.quranacademy.quran.domain.exceptions.NoNetworkException
import org.quranacademy.quran.domain.models.Language
import org.quranacademy.quran.domain.models.Translation
import org.quranacademy.quran.translationsrepository.database.TranslationDatabaseMapper
import org.quranacademy.quran.translationsrepository.database.TranslationResponseDatabaseMapper
import timber.log.Timber
import javax.inject.Inject

class TranslationsDataSource @Inject constructor(
        private val quranAcademyApi: QuranAcademyApi,
        private val translationsCache: TranslationsCache,
        private val networkChecker: NetworkChecker,
        private val translationResponseDatabaseMapper: TranslationResponseDatabaseMapper,
        private val translationDatabaseMapper: TranslationDatabaseMapper
) {

    var onListUpdatedListener: (() -> Unit)? = null

    suspend fun getTranslationsList(language: Language? = null, forceLoad: Boolean): List<Translation> {
        val downloadFromServer = if (translationsCache.isCacheEmpty()) {
            true
        } else if (!networkChecker.isConnected) {
            false
        } else {
            translationsCache.isCacheExpired() || forceLoad
        }
        if (downloadFromServer) {
            Timber.i("Загрузка списка переводов с сервера")
            val translationsResponseList = quranAcademyApi.getAyahTranslationsList(language?.code)
            val translationsDbModels = translationResponseDatabaseMapper.map(translationsResponseList.translations)
            translationsCache.saveTranslations(translationsDbModels)
            return translationDatabaseMapper.mapFromDatabase(translationsDbModels).also {
                onListUpdatedListener?.invoke()
            }
        } else {
            Timber.i("Загрузка списка переводов из кеша")
            val translationsDbModels = translationsCache.getTranslations()
            val translations = translationDatabaseMapper.mapFromDatabase(translationsDbModels)
            //если нет интернета и список пустой, показываем ошибку
            return if (translations.isEmpty() && !networkChecker.isConnected) {
                throw NoNetworkException()
            } else {
                translations
            }
        }
    }

    suspend fun getTranslationsWithUpdates(checkForUpdatesFromServer: Boolean): List<Translation> {
        getTranslationsList(forceLoad = checkForUpdatesFromServer) //инициируем обновление списка

        val translationUpdates = translationsCache.getTranslationsWithUpdates()
        return translationDatabaseMapper.mapFromDatabase(translationUpdates)
    }

}