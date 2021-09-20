package org.quranacademy.quran.translationsmanager.domain.translations

import kotlinx.coroutines.coroutineScope
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.repositories.LanguagesRepository
import org.quranacademy.quran.domain.repositories.TranslationsRepository
import javax.inject.Inject

class TranslationsInteractor @Inject constructor(
        private val translationsRepository: TranslationsRepository,
        private val languagesRepository: LanguagesRepository,
        private val translationUIModelMapper: TranslationUIModelMapper
) {

    suspend fun getTranslations(forceLoad: Boolean, isTafseer: Boolean): List<TranslationUIModel> = coroutineScope {
        val translations = translationsRepository.getTranslations(isTafseer, forceLoad)
        val languages = languagesRepository.getLanguages(false).languages
        val downloadingTranslationCodes = translationsRepository.getDownloadingTranslationCodes()
        translationUIModelMapper.mapToUIModel(translations, languages, downloadingTranslationCodes)
    }

    suspend fun downloadTranslation(translation: TranslationUIModel, onDownloadProgress: (FileDownloadInfo) -> Unit) =
            translationsRepository.downloadTranslation(translation.fromUIModel(), onDownloadProgress)

    suspend fun downloadTranslationUpdate(translation: TranslationUIModel, onDownloadProgress: (FileDownloadInfo) -> Unit) =
            translationsRepository.downloadTranslationUpdate(translation.fromUIModel(), onDownloadProgress)

    fun setCurrentTranslationsDownloadingListener(listener: (code: String, progress: FileDownloadInfo) -> Unit) =
            translationsRepository.setCurrentTranslationsDownloadingListener(listener)

    suspend fun cancelTranslationDownloading(translation: TranslationUIModel) =
            translationsRepository.cancelTranslationDownloading(translation.fromUIModel())

    suspend fun deleteTranslation(translation: TranslationUIModel) =
            translationsRepository.deleteTranslation(translation.fromUIModel())

    suspend fun enableTranslation(translation: TranslationUIModel, isEnabled: Boolean) =
            translationsRepository.enableTranslation(translation.fromUIModel(), isEnabled)

    private fun TranslationUIModel.fromUIModel() = translationUIModelMapper.mapFromUIModel(this)

}
