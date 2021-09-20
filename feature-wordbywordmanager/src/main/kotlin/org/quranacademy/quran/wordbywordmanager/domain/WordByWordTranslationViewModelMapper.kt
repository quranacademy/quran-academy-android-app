package org.quranacademy.quran.wordbywordmanager.domain

import org.quranacademy.quran.domain.models.WordByWordTranslation
import javax.inject.Inject

class WordByWordTranslationViewModelMapper @Inject constructor() {

    fun mapToViewModel(
            models: List<WordByWordTranslation>,
            currentTranslationCode: String?
    ) = models.map { mapToUIModel(it, currentTranslationCode) }

    fun mapToUIModel(
            model: WordByWordTranslation,
            currentTranslationCode: String?
    ): WordByWordTranslationUIModel {
        return WordByWordTranslationUIModel(
                id = model.id,
                languageCode = model.languageCode,
                name = model.name,
                fileName = model.fileName,
                fileUrl = model.fileUrl,
                isDownloaded = model.isDownloaded,
                isEnabled = model.languageCode == currentTranslationCode,
                isUpdateAvailable = model.isUpdateAvailable
        )
    }

    fun mapFromUIModel(model: WordByWordTranslationUIModel): WordByWordTranslation {
        return WordByWordTranslation(
                id = model.id,
                name = model.name,
                languageCode = model.languageCode,
                fileName = model.fileName,
                fileUrl = model.fileUrl,
                isDownloaded = model.isDownloaded,
                isUpdateAvailable = model.isUpdateAvailable
        )
    }

}