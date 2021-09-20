package org.quranacademy.quran.wordbywordmanager.domain

import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.repositories.WordByWordTranslationsRepository
import javax.inject.Inject

class WordByWordTranslationsInteractor @Inject constructor(
        private val wordByWordTranslationsRepository: WordByWordTranslationsRepository,
        private val modelMapper: WordByWordTranslationViewModelMapper
) {

    suspend fun getTranslations(forceLoad: Boolean = false): WordByWordTranslationsUiWrapper {
        val translationsWrapper = wordByWordTranslationsRepository.getTranslationsList(forceLoad)
        val currentTranslationLanguageCode = translationsWrapper.currentTranslationLanguageCode
        val translations = modelMapper.mapToViewModel(translationsWrapper.translations, currentTranslationLanguageCode).toMutableList()
        val currentTranslation = translations.firstOrNull { it.languageCode == currentTranslationLanguageCode }
        return WordByWordTranslationsUiWrapper(translations, currentTranslation)
    }

    suspend fun downloadTranslation(translation: WordByWordTranslationUIModel, onProgress: (FileDownloadInfo) -> Unit) =
            wordByWordTranslationsRepository.downloadTranslation(translation.fromUIModel(), onProgress)

    suspend fun cancelTranslationDownloading(translation: WordByWordTranslationUIModel) =
            wordByWordTranslationsRepository.cancelTranslationDownloading(translation.fromUIModel())

    suspend fun deleteTranslation(translation: WordByWordTranslationUIModel) =
            wordByWordTranslationsRepository.deleteTranslation(translation.fromUIModel())

    suspend fun enableTranslation(translation: WordByWordTranslationUIModel) =
            wordByWordTranslationsRepository.enableTranslation(translation.fromUIModel())

    private fun WordByWordTranslationUIModel.fromUIModel() = modelMapper.mapFromUIModel(this)

}