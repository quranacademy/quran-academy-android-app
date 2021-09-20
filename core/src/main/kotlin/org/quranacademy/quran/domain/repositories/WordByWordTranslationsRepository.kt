package org.quranacademy.quran.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.WordByWordTranslation
import org.quranacademy.quran.domain.models.WordByWordTranslationsWrapper

interface WordByWordTranslationsRepository {

    suspend fun getTranslationsList(forceLoad: Boolean): WordByWordTranslationsWrapper

    suspend fun downloadTranslation(translation: WordByWordTranslation, onProgress: (FileDownloadInfo) -> Unit)

    suspend fun cancelTranslationDownloading(translation: WordByWordTranslation)

    suspend fun deleteTranslation(translation: WordByWordTranslation)

    suspend fun enableTranslation(translation: WordByWordTranslation)

    suspend fun isTranslationUpdatesAvailable(checkForUpdatesFromServer: Boolean): Boolean

    suspend fun getTranslationUpdatesCount(): Int

    suspend fun downloadTranslationUpdate(translation: WordByWordTranslation, onProgress: (FileDownloadInfo) -> Unit)

    suspend fun getEnabledTranslationsListUpdates(): Flow<Unit>

    suspend fun getTranslationsListUpdates(): Flow<Unit>

}