package org.quranacademy.quran.translationsrepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.quranacademy.quran.data.database.adapters.TranslationDatabaseManager
import org.quranacademy.quran.data.database.adapters.TranslationsOrderManager
import org.quranacademy.quran.data.database.daos.TranslationsDao
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.Translation
import org.quranacademy.quran.domain.repositories.TranslationsRepository
import timber.log.Timber
import javax.inject.Inject

class TranslationsRepositoryImpl @Inject constructor(
        private val translationsDataSource: TranslationsDataSource,
        private val translationDatabaseManager: TranslationDatabaseManager,
        private val translationDownloader: TranslationDownloader,
        private val translationsDao: TranslationsDao,
        private val translationsOrderManager: TranslationsOrderManager
) : TranslationsRepository {

    private val translationEnablingUpdates = BroadcastChannel<Unit>(1)
    private val translationListUpdates = BroadcastChannel<Unit>(1)

    init {
        GlobalScope.launch {
            translationEnablingUpdates.asFlow().collect {
                translationsOrderManager.onTranslationsListUpdated()
            }
        }

        translationsDataSource.onListUpdatedListener = {
            GlobalScope.launch {
                translationListUpdates.send(Unit)
            }
        }
    }

    override suspend fun getTranslations(
            isTafseer: Boolean,
            forceLoad: Boolean
    ): List<Translation> = withContext(Dispatchers.IO) {
        val translations = translationsDataSource.getTranslationsList(forceLoad = forceLoad)
        Timber.i("Список переводов загружен. Количество записей: ${translations.size}")
        return@withContext translations.filter { translation -> translation.isTafseer == isTafseer }
                .sortedWith(compareBy(Translation::languageCode, Translation::name));
    }

    override suspend fun downloadTranslation(
            translation: Translation,
            onDownloadProgress: (FileDownloadInfo) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            Timber.i("Запрос на загруку перевода \"${translation.name}\"")
            translationDownloader.download(translation, false, onDownloadProgress)
            onTranslationDownloadingFinished(translation)
        } catch (error: Exception) {
            Timber.w("Ошибка при загрузке перевода \"${translation.name}\"")
            throw error
        }
    }

    override fun getDownloadingTranslationCodes(): List<String> =
            translationDownloader.getDownloadingTranslationCodes()

    override fun setCurrentTranslationsDownloadingListener(
            listener: (code: String, progress: FileDownloadInfo) -> Unit
    ) = translationDownloader.setCurrentTranslationsDownloadingListener(listener)


    override suspend fun cancelTranslationDownloading(
            translation: Translation
    ) = withContext(Dispatchers.IO) {
        translationDownloader.cancelDownloading(translation)
    }

    override suspend fun deleteTranslation(
            translation: Translation
    ) = withContext(Dispatchers.IO) {
        translationDatabaseManager.deleteTranslation(translation)
        translationsDao.markTranslationAsDownloaded(translation.code, false)
        Timber.i("Перевод \"${translation.name}\" удален")

        translationListUpdates.send(Unit)
        translationEnablingUpdates.send(Unit)
    }

    override suspend fun enableTranslation(
            translation: Translation,
            isEnabled: Boolean
    ) = withContext(Dispatchers.IO) {
        if (isEnabled) {
            translationDatabaseManager.connectTo(translation)
        } else {
            translationDatabaseManager.closeConnection(translation)
        }

        translationsDao.markTranslationAsEnabled(translation.code, isEnabled)
        Timber.i("Включение/отключение перевода \"${translation.name}\": ${translation.isEnabled}")
        translationEnablingUpdates.send(Unit)
    }

    override suspend fun getEnabledTranslations(): List<Translation> {
        return translationDatabaseManager.getAdapters()
                .map { it.getTranslation() }
                .sortedBy { it.name }
    }

    override suspend fun isTranslationUpdatesAvailable(checkForUpdatesFromServer: Boolean): Boolean = withContext(Dispatchers.IO) {
        translationsDataSource.getTranslationsWithUpdates(checkForUpdatesFromServer).isNotEmpty()
    }

    override suspend fun getTranslationUpdatesCount(isTafseers: Boolean): Int = withContext(Dispatchers.IO) {
        translationsDataSource.getTranslationsWithUpdates(false)
                .filter { it.isTafseer == isTafseers }
                .size
    }

    override suspend fun downloadTranslationUpdate(
            translation: Translation,
            onDownloadProgress: (FileDownloadInfo) -> Unit
    ) = withContext(Dispatchers.IO) {
        translationDatabaseManager.closeConnection(translation)
        try {
            translationDownloader.download(translation, true, onDownloadProgress)
            onTranslationUpdatingFinished(translation)
        } catch (error: Exception) {
            Timber.w("Ошибка при обновлении перевода \"${translation.name}\"")
            translationDatabaseManager.connectTo(translation)
            throw error
        }
    }

    override fun getEnabledTranslationsListUpdates(): Flow<Unit> = translationEnablingUpdates.asFlow()

    override fun getTranslationsListUpdates(): Flow<Unit> = translationListUpdates.asFlow()

    private suspend fun onTranslationDownloadingFinished(translation: Translation) {
        translationsDao.markTranslationAsDownloaded(translation.code, true)
        translationDatabaseManager.connectTo(translation)
        translationEnablingUpdates.send(Unit)
        translationListUpdates.send(Unit)
        Timber.i("Загрузка перевода \"${translation.name}\" завершена")
    }

    private suspend fun onTranslationUpdatingFinished(translation: Translation) {
        translationDatabaseManager.connectTo(translation)
        translationsDao.markTranslationAsLastUpdated(translation.code)
        translationListUpdates.send(Unit)
        Timber.i("Обновление перевода \"${translation.name}\" завершено")
    }

}