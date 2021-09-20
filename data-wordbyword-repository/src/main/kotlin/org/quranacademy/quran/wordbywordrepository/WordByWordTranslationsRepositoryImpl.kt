package org.quranacademy.quran.wordbywordrepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.quranacademy.quran.data.database.adapters.WordByWordTranslationDatabaseManager
import org.quranacademy.quran.data.database.daos.WordByWordTranslationsDao
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.WordByWordTranslation
import org.quranacademy.quran.domain.models.WordByWordTranslationsWrapper
import org.quranacademy.quran.domain.repositories.WordByWordTranslationsRepository
import timber.log.Timber
import javax.inject.Inject

class WordByWordTranslationsRepositoryImpl @Inject constructor(
        private val appPreferences: AppPreferences,
        private val wordByWordTranslationsDao: WordByWordTranslationsDao,
        private val wordByWordTranslationsDataSource: WordByWordTranslationsDataSource,
        private val wordByWordTranslationDatabaseManager: WordByWordTranslationDatabaseManager,
        private val wordByWordTranslationDownloader: WordByWordTranslationDownloader
) : WordByWordTranslationsRepository {

    private val enabledTranslationsUpdates = BroadcastChannel<Unit>(1)
    private val translationListUpdates = BroadcastChannel<Unit>(1)

    init {
        wordByWordTranslationsDataSource.onListUpdatedListener = {
            GlobalScope.launch {
                translationListUpdates.send(Unit)
            }
        }
    }

    override suspend fun getTranslationsList(
            forceLoad: Boolean
    ): WordByWordTranslationsWrapper = withContext(Dispatchers.IO) {
        Timber.i("Получение списка переводов: ${appPreferences.getCurrentWbwTranslation()}")
        val translations = wordByWordTranslationsDataSource.getTranslations(forceLoad = forceLoad)
                .sortedBy { it.name }
        WordByWordTranslationsWrapper(translations, appPreferences.getCurrentWbwTranslation())
    }

    override suspend fun downloadTranslation(
            translation: WordByWordTranslation,
            onProgress: (FileDownloadInfo) -> Unit
    ) = withContext(Dispatchers.IO) {
        Timber.i("Запрос на загруку пословного перевода \"${translation.name}\" + ${translation.languageCode}")
        try {
            wordByWordTranslationDownloader.download(translation, false, onProgress)
            onTranslationDownloaded(translation)
        } catch (error: Exception) {
            Timber.i("Ошибка при загрузке пословного перевода \"${translation.name}\"")
            throw error
        }
    }

    override suspend fun cancelTranslationDownloading(
            translation: WordByWordTranslation
    ) = withContext(Dispatchers.IO) {
        wordByWordTranslationDownloader.cancelDownloading(translation)
    }

    override suspend fun deleteTranslation(
            translation: WordByWordTranslation
    ) = withContext(Dispatchers.IO) {
        wordByWordTranslationDatabaseManager.deleteTranslation(translation)
        wordByWordTranslationsDao.markTranslationAsDownloaded(translation.id, false)
        val currentTranslation = appPreferences.getCurrentWbwTranslation()
        if (currentTranslation == translation.languageCode) {
            appPreferences.setCurrentWbwTranslation(null)
        }
        enabledTranslationsUpdates.send(Unit)
    }

    override suspend fun enableTranslation(
            translation: WordByWordTranslation
    ) = withContext(Dispatchers.IO) {
        appPreferences.setCurrentWbwTranslation(translation.languageCode)
        wordByWordTranslationDatabaseManager.connectTo(translation)
        enabledTranslationsUpdates.send(Unit)
    }

    override suspend fun isTranslationUpdatesAvailable(
            checkForUpdatesFromServer: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        wordByWordTranslationsDataSource
                .getTranslationsWithUpdates(checkForUpdatesFromServer = checkForUpdatesFromServer)
                .isNotEmpty()
    }

    override suspend fun getTranslationUpdatesCount(): Int = withContext(Dispatchers.IO) {
        wordByWordTranslationsDataSource
                .getTranslationsWithUpdates(checkForUpdatesFromServer = false)
                .count()
    }

    override suspend fun downloadTranslationUpdate(
            translation: WordByWordTranslation,
            onProgress: (FileDownloadInfo) -> Unit
    ) = withContext(Dispatchers.IO) {
        val currentTranslation = wordByWordTranslationDatabaseManager.getCurrentTranslationAdapter()?.getTranslation()
        val isCurrentTranslation = translation == currentTranslation
        if (isCurrentTranslation) {
            wordByWordTranslationDatabaseManager.closeConnection()
        }
        try {
            wordByWordTranslationDownloader.download(translation, true, onProgress)
            onTranslationUpdated(translation, isCurrentTranslation)
        } catch (error: Exception) {
            Timber.i("Ошибка при обновлении перевода \"${translation.name}\"")
            throw error
        }
    }

    override suspend fun getEnabledTranslationsListUpdates() = enabledTranslationsUpdates.asFlow()

    override suspend fun getTranslationsListUpdates() = translationListUpdates.asFlow()

    private suspend fun onTranslationDownloaded(translation: WordByWordTranslation) {
        Timber.i("Загрузка пословного перевода \"${translation.name}\" завершена")
        appPreferences.setCurrentWbwTranslation(translation.languageCode)
        wordByWordTranslationsDao.markTranslationAsDownloaded(translation.id, true)
        wordByWordTranslationDatabaseManager.connectTo(translation)
        enabledTranslationsUpdates.send(Unit)
        translationListUpdates.send(Unit)
    }

    private suspend fun onTranslationUpdated(translation: WordByWordTranslation, isCurrentTranslation: Boolean) {
        if (isCurrentTranslation) {
            wordByWordTranslationDatabaseManager.connectTo(translation)
        }
        wordByWordTranslationsDao.markTranslationAsLastUpdated(translation.id)
        translationListUpdates.send(Unit)
        Timber.i("Обновление перевода \"${translation.name}\" завершено")
    }

}