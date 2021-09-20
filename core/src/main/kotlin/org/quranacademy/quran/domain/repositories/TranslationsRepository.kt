package org.quranacademy.quran.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.Translation

interface TranslationsRepository {

    suspend fun getTranslations(isTafseer: Boolean, forceLoad: Boolean): List<Translation>

    suspend fun downloadTranslation(translation: Translation, onDownloadProgress: (FileDownloadInfo) -> Unit)

    suspend fun downloadTranslationUpdate(translation: Translation, onDownloadProgress: (FileDownloadInfo) -> Unit)

    fun getDownloadingTranslationCodes(): List<String>

    fun setCurrentTranslationsDownloadingListener(listener: (code: String, progress: FileDownloadInfo) -> Unit)

    suspend fun cancelTranslationDownloading(translation: Translation)

    suspend fun deleteTranslation(translation: Translation)

    suspend fun enableTranslation(translation: Translation, isEnabled: Boolean)

    suspend fun getEnabledTranslations(): List<Translation>

    suspend fun isTranslationUpdatesAvailable(checkForUpdatesFromServer: Boolean): Boolean

    suspend fun getTranslationUpdatesCount(isTafseers: Boolean): Int


    fun getEnabledTranslationsListUpdates(): Flow<Unit>

    fun getTranslationsListUpdates(): Flow<Unit>
}