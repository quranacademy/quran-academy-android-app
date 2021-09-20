package org.quranacademy.quran.settings.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.MushafPageType
import org.quranacademy.quran.domain.repositories.QuranImagesRepository
import org.quranacademy.quran.domain.repositories.TranslationsRepository
import org.quranacademy.quran.domain.repositories.WordByWordTranslationsRepository
import org.quranacademy.quran.settings.data.StorageRepository
import javax.inject.Inject

class AppSettingsInteractor @Inject constructor(
        private val appPreferences: AppPreferences,
        private val translationsRepository: TranslationsRepository,
        private val wordByWordTranslationsRepository: WordByWordTranslationsRepository,
        private val quranImagesRepository: QuranImagesRepository,
        private val storageRepository: StorageRepository
) {

    suspend fun getTranslationUpdatesCount(isTafseer: Boolean) = translationsRepository.getTranslationUpdatesCount(isTafseer)

    suspend fun getWordByWordTranslationUpdatesCount() = wordByWordTranslationsRepository.getTranslationUpdatesCount()

    suspend fun getTranslationUpdatesCountListener(isTafseer: Boolean) =
            translationsRepository.getTranslationsListUpdates()
                    .map { translationsRepository.getTranslationUpdatesCount(isTafseer) }


    suspend fun getWordByWordTranslationUpdatesCountListener(): Flow<Int> =
            wordByWordTranslationsRepository.getTranslationsListUpdates()
                    .map { wordByWordTranslationsRepository.getTranslationUpdatesCount() }

    suspend fun isAllImagesDownloaded() = quranImagesRepository.isAllPageImagesDownloaded()

    suspend fun downloadImagesBundle(onProgress: (FileDownloadInfo) -> Unit) {
        quranImagesRepository.downloadImagesBundle(onProgress)
        appPreferences.setSuggestDownloadImagesBundle(false)
    }

    suspend fun cancelImagesBundleDownloading() = quranImagesRepository.cancelImagesBundleDownloading()

    fun getReadingHistorySize() = appPreferences.getReadingHistorySize()

    fun setReadingHistorySize(size: Int) {
        appPreferences.setReadingHistorySize(size)
    }

    fun getStoragesList(): StoragesInfo {
        return storageRepository.getStoragesInfo()
    }

    suspend fun setCurrentAppStorage(storage: StoragesInfo.Storage) {
        storageRepository.setAppDataFilePath(storage.folderPath)
        appPreferences.setAppDataFilePath(storage.folderPath)
    }

    fun isTranslationsDownloading(): Boolean {
        return translationsRepository.getDownloadingTranslationCodes().isNotEmpty()
    }

    fun getMushafPageType(): MushafPageType {
        return appPreferences.getMushafType()
    }

}
